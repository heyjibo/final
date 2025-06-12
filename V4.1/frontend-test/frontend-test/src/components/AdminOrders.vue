<template>
  <div class="admin-orders-container">
    <AdminNavBar />
    <div class="header-section">
      <h2>订单管理</h2>
      <el-button type="primary" @click="refreshData" icon="el-icon-refresh">
        刷新数据
      </el-button>
    </div>

    <div class="table-container">
      <el-table
          :data="sortedOrders"
          style="width: 100%"
          stripe
          border
          v-loading="loading"
          element-loading-text="数据加载中..."
          element-loading-spinner="el-icon-loading"
      >
        <el-table-column
            prop="id"
            label="订单号"
            width="100"
            fixed="left"
            align="center"
        ></el-table-column>

        <el-table-column
            prop="content"
            label="订单内容"
            min-width="150"
            show-overflow-tooltip
        ></el-table-column>

        <el-table-column
            label="顾客信息"
            min-width="180"
        >
          <template slot-scope="scope">
            <div v-if="scope.row.customer" class="customer-info">
              <div class="info-row">
                <i class="el-icon-user"></i>
                <span>{{ scope.row.customer.name }}</span>
              </div>
              <div class="info-row">
                <i class="el-icon-location-outline"></i>
                <span>{{ scope.row.customer.address }}</span>
              </div>
              <div class="info-row">
                <i class="el-icon-phone"></i>
                <span>{{ scope.row.customerPhone }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            prop="state"
            label="状态"
            width="120"
            align="center"
        >
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.state)">
              {{ scope.row.state }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
            prop="createTime"
            label="创建时间"
            width="170"
            align="center"
        >
          <template slot-scope="scope">
            <div>{{ formatTime(scope.row.createTime) }}</div>
          </template>
        </el-table-column>

        <el-table-column
            prop="completionTime"
            label="完成时间"
            width="170"
            align="center"
        >
          <template slot-scope="scope">
            <div>{{ formatTime(scope.row.completionTime) || '-' }}</div>
          </template>
        </el-table-column>

        <!-- 商家评分列 - 同时显示星星和分数 -->
        <el-table-column
            label="商家评分"
            width="180"
            align="center"
        >
          <template slot-scope="scope">
            <div class="rating-container">
              <el-rate
                  v-model="scope.row.score"
                  disabled
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  :max="5"
                  text-color="#ff9900"
                  class="rating-stars"
              />
              <div class="rating-score">
                {{ getFormattedScore(scope.row.score) }}
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 骑手评分列 - 同时显示星星和分数 -->
        <el-table-column
            label="骑手评分"
            width="180"
            align="center"
        >
          <template slot-scope="scope">
            <div class="rating-container">
              <el-rate
                  v-model="scope.row.deliverScore"
                  disabled
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  :max="5"
                  text-color="#ff9900"
                  class="rating-stars"
              />
              <div class="rating-score">
                {{ getFormattedScore(scope.row.deliverScore) }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            label="配送员"
            width="200"
            align="center"
        >
          <template slot-scope="scope">
            <div class="deliver-select-container">
              <el-select
                  v-model="scope.row.deliverID"
                  :disabled="!isEditable(scope.row.state)"
                  @change="updateDeliver(scope.row)"
                  placeholder="选择骑手"
                  size="small"
              >
                <el-option
                    v-for="deliver in delivers"
                    :key="deliver.workId"
                    :label="deliver.name"
                    :value="deliver.workId"
                >
                  <span>{{ deliver.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">
                    {{ deliver.workId }}
                  </span>
                </el-option>
              </el-select>

              <div v-if="scope.row.deliverPhone" class="deliver-phone">
                <i class="el-icon-phone"></i>
                {{ scope.row.deliverPhone }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            label="商家电话"
            width="130"
            align="center"
        >
          <template slot-scope="scope">
            <div class="phone-number">
              <i class="el-icon-phone"></i>
              {{ scope.row.merchantPhone }}
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import AdminNavBar from './AdminNavBar.vue';
import axios from 'axios';

export default {
  components: { AdminNavBar },
  data() {
    return {
      orders: [],
      delivers: [],
      refreshTimer: null,
      loading: false
    };
  },
  computed: {
    sortedOrders() {
      // 复制数组避免修改原始数据
      const ordersCopy = [...this.orders];

      // 自定义排序
      return ordersCopy.sort((a, b) => {
        // 1. 优先排序有评分的订单
        const aHasRating = a.score > 0 || a.deliverScore > 0;
        const bHasRating = b.score > 0 || b.deliverScore > 0;

        if (aHasRating && !bHasRating) return -1;
        if (!aHasRating && bHasRating) return 1;

        // 2. 按创建时间从新到旧排序
        const aTime = new Date(a.createTime).getTime();
        const bTime = new Date(b.createTime).getTime();

        return bTime - aTime;
      });
    }
  },
  async mounted() {
    this.loading = true;
    await this.fetchOrders();
    await this.fetchDelivers();
    this.loading = false;

    // 设置自动刷新
    this.refreshTimer = setInterval(async () => {
      await this.fetchOrders();
      await this.fetchDelivers();
    }, 300000); // 5分钟刷新一次
  },
  beforeDestroy() {
    clearInterval(this.refreshTimer);
  },
  methods: {
    getFormattedScore(score) {
      if (score === null || score === undefined || score === 0) {
        return "未评分";
      }
      return `${score.toFixed(1)}分`;
    },
    getStatusTagType(state) {
      const statusMap = {
        'Completed': 'success',
        'Picking Up': 'warning',
        'Pending': 'info',
        'Delivering': '',
        'Awaits confirmation': 'danger'
      };
      return statusMap[state] || 'danger';
    },
    refreshData() {
      this.loading = true;
      Promise.all([this.fetchOrders(), this.fetchDelivers()])
          .finally(() => {
            this.loading = false;
            this.$message.success('数据已刷新');
          });
    },
    isEditable(state) {
      return ['Pending', 'Delivering'].includes(state);
    },
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    async fetchOrders() {
      try {
        const res = await axios.get('/api/indent', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });

        this.orders = await Promise.all(
            res.data.map(async order => {
              try {
                const [customerRes, merchantRes, deliverRes] = await Promise.all([
                  axios.get(`/api/customer/${order.customerID}`),
                  axios.get(`/api/merchant/phonenumber/${order.merchantID}`),
                  order.deliverID ?
                      axios.get(`/api/deliver/phonenumber/${order.deliverID}`)
                      : Promise.resolve({ data: null })
                ]);

                // 转换评分显示（0-10分制转换为0-5分制显示）
                const merchantScore = order.score ? order.score / 2 : 0;
                const deliverScore = order.deliverScore ? order.deliverScore / 2 : 0;

                return {
                  ...order,
                  customer: customerRes.data,
                  customerPhone: customerRes.data?.phonenumber || '未知',
                  merchantPhone: merchantRes.data || '未知',
                  deliverPhone: deliverRes?.data || '未分配',
                  score: merchantScore, // 添加转换后的商家评分
                  deliverScore: deliverScore // 添加转换后的骑手评分
                };
              } catch (error) {
                console.error(`获取订单${order.id}信息失败:`, error);
                return {
                  ...order,
                  customer: {},
                  customerPhone: '未知',
                  merchantPhone: '未知',
                  deliverPhone: '未知',
                  score: 0,
                  deliverScore: 0
                };
              }
            })
        );
      } catch (error) {
        console.error('获取订单失败:', error);
        this.$message.error('订单数据加载失败');
      }
    },
    async fetchDelivers() {
      try {
        const res = await axios.get('/api/deliver');
        this.delivers = res.data;
      } catch (error) {
        console.error('获取骑手列表失败:', error);
        this.$message.error('骑手列表加载失败');
      }
    },
    async updateDeliver(order) {
      if (!this.isEditable(order.state)) return;

      try {
        await axios.put(`/api/indent/change-deliver/${order.id}`, {
          deliverID: order.deliverID
        }, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });

        this.$message.success('配送员更新成功');
        await this.fetchOrders();
      } catch (error) {
        const errorMsg = error.response?.data?.error || error.message;
        console.error('更新失败:', errorMsg);
        this.$message.error(`更新失败: ${errorMsg}`);
      }
    }
  }
};
</script>

<style scoped>
.admin-orders-container {
  max-width: 98%;
  margin: 2rem auto;
  padding: 60px 1.5rem 2rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #ebeef5;
}

.header-section h2 {
  color: #303133;
  font-weight: 600;
}

.table-container {
  margin-top: 1.5rem;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.customer-info {
  padding: 8px 0;
}

.info-row {
  display: flex;
  align-items: center;
  margin: 5px 0;
  font-size: 13px;
  color: #606266;
}

.info-row i {
  margin-right: 8px;
  color: #909399;
}

.deliver-select-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.deliver-phone {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
}

.deliver-phone i {
  margin-right: 4px;
}

.phone-number {
  display: flex;
  align-items: center;
  justify-content: center;
}

.phone-number i {
  margin-right: 5px;
  color: #909399;
}

.rating-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60px;
}

.rating-stars {
  margin-bottom: 5px;
}

.rating-score {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.el-table .cell {
  padding: 8px 12px;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #303133;
}

.el-tag {
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
  font-size: 12px;
}

/* 响应式调整 */
@media (max-width: 1600px) {
  .admin-orders-container {
    padding: 60px 1rem 1.5rem;
  }

  .el-table {
    font-size: 13px;
  }

  .el-table-column {
    padding: 6px 8px;
  }
}

@media (max-width: 1200px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-section .el-button {
    margin-top: 10px;
    align-self: flex-end;
  }
}
</style>
<template>
  <div class="profile-container">
    <CustomerNavBar />
    <div class="profile-content">
      <h2 class="profile-title">顾客个人信息</h2>
      <div class="profile-card">
        <div class="info-item">
          <label class="info-label">姓名：</label>
          <span class="info-value">{{ customerInfo.name }}</span>
        </div>
        <div class="info-item">
          <label class="info-label">地址：</label>
          <span class="info-value">{{ customerInfo.address }}</span>
        </div>
        <div class="info-item">
          <label class="info-label">累计点单：</label>
          <span class="info-value">{{ customerInfo.numOfOrder }}</span>
        </div>
        <div class="info-item">
          <label class="info-label">手机号：</label>
          <span class="info-value">{{ customerInfo.phonenumber }}</span>
        </div>

        <!-- 评分对话框 -->
        <el-dialog
            :title="currentOrder ? '评价订单 #' + currentOrder.id : '评价订单'"
            :visible.sync="ratingDialogVisible"
            width="600px"
            :close-on-click-modal="false"
            :show-close="!isSubmitting"
            :close-on-press-escape="false">

          <div v-if="currentOrder" class="rating-form">
            <div class="rating-item">
              <label>商家评分：</label>
              <el-rate
                  v-model="ratingForm.merchantScore"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  :max="5"
                  show-text
                  text-color="#ff9900"
                  :disabled="isSubmitting">
              </el-rate>
            </div>

            <div class="rating-item">
              <label>骑手评分：</label>
              <el-rate
                  v-model="ratingForm.deliverScore"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  :max="5"
                  show-text
                  text-color="#ff9900"
                  :disabled="isSubmitting">
              </el-rate>
            </div>
          </div>
          <div v-else>
            <p>加载订单信息中...</p>
          </div>

          <span slot="footer" class="dialog-footer">
            <el-button @click="ratingDialogVisible = false" :disabled="isSubmitting">取消</el-button>
            <el-button
                type="primary"
                @click="submitRating"
                :loading="isSubmitting">
              提交评价
            </el-button>
          </span>
        </el-dialog>

        <el-card class="order-list">
          <h3>我的订单</h3>
          <el-table :data="sortedOrders" stripe style="width: 100%">
            <el-table-column prop="id" label="订单号" width="100"></el-table-column>
            <el-table-column prop="content" label="菜品" width="180"></el-table-column>
            <el-table-column label="金额" width="120">
              <template slot-scope="scope">¥{{ scope.row.money }}</template>
            </el-table-column>
            <el-table-column label="创建时间" width="180">
              <template slot-scope="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="接单时间" width="180">
              <template slot-scope="scope">
                {{ formatTime(scope.row.pickupTime) }}
              </template>
            </el-table-column>
            <el-table-column label="完成时间" width="180">
              <template slot-scope="scope">
                {{ formatTime(scope.row.completionTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.state)">
                  {{ scope.row.state || '未知状态' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="商家评分" width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.state === 'Completed'">
                  <el-rate
                      v-model="scope.row.merchantScore"
                      disabled
                      :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                      :max="5"
                      show-score
                      text-color="#ff9900"
                      score-template="{value}">
                  </el-rate>
                </div>
                <span v-else>--</span>
              </template>
            </el-table-column>
            <el-table-column label="骑手评分" width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.state === 'Completed'">
                  <el-rate
                      v-model="scope.row.deliverScore"
                      disabled
                      :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                      :max="5"
                      show-score
                      text-color="#ff9900"
                      score-template="{value}">
                  </el-rate>
                </div>
                <span v-else>--</span>
              </template>
            </el-table-column>
            <el-table-column label="骑手电话">
              <template slot-scope="scope">
                {{ scope.row.deliverPhone || '未分配' }}
              </template>
            </el-table-column>
            <el-table-column label="商家电话">
              <template slot-scope="scope">
                {{ scope.row.merchantPhone }}
              </template>
            </el-table-column>

            <!-- 添加操作列 -->
            <el-table-column label="操作" width="120" fixed="right">
              <template slot-scope="scope">
                <el-button
                    v-if="scope.row.state === 'Completed' && !scope.row.hasRated"
                    type="primary"
                    size="mini"
                    @click="openRatingDialog(scope.row)">
                  评价
                </el-button>
                <el-tag v-else-if="scope.row.hasRated" type="success">已评价</el-tag>
                <span v-else>--</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import CustomerNavBar from '@/components/CustomerNavBar.vue'

export default {
  name: 'CustomerProfile',
  components: {
    CustomerNavBar
  },
  data() {
    return {
      customerInfo: {},
      orders: [],
      // 评分相关数据
      ratingDialogVisible: false,
      isSubmitting: false,
      currentOrder: null,
      ratingForm: {
        merchantScore: 0,
        deliverScore: 0
      }
    }
  },
  computed: {
    // 添加计算属性实现排序
    sortedOrders() {
      // 首先复制数组避免修改原始数据
      const ordersCopy = [...this.orders];

      // 实现自定义排序
      return ordersCopy.sort((a, b) => {
        // 1. 优先排序已完成订单
        const aIsCompleted = a.state === 'Completed';
        const bIsCompleted = b.state === 'Completed';

        // 如果a已完成而b未完成，a排前面
        if (aIsCompleted && !bIsCompleted) {
          return -1;
        }
        // 如果b已完成而a未完成，b排前面
        if (!aIsCompleted && bIsCompleted) {
          return 1;
        }

        // 2. 对于其他状态，按创建时间从近到远排序（最新订单排前面）
        const aTime = new Date(a.createTime).getTime();
        const bTime = new Date(b.createTime).getTime();

        // 从近到远排序（最新时间最大，所以用bTime - aTime）
        return bTime - aTime;
      });
    }
  },
  async mounted() {
    await this.fetchCustomerProfile()
    await this.fetchAllOrders()
  },
  methods: {
    formatTime(time) {
      if (!time) return '--'
      return new Date(time).toLocaleString()
    },
    getStatusType(state) {
      switch (state) {
        case 'Completed': return 'success'
        case 'Picking Up': return 'warning'
        case 'Pending': return 'info'
        case 'Awaits confirmation': return ''
        default: return 'danger'
      }
    },
    async fetchCustomerProfile() {
      try {
        const userId = localStorage.getItem('userId')
        if (!userId) {
          console.error('用户ID未找到')
          this.$message.error('用户信息错误，请重新登录')
          return
        }

        const response = await this.$axios.get(`/api/customer/${userId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.customerInfo = response.data
      } catch (error) {
        console.error('获取顾客信息失败:', error)
        this.$message.error('获取个人信息失败')
      }
    },

    // 打开评分对话框
    openRatingDialog(order) {
      if (!order || !order.id) {
        console.error('无法打开评分对话框: 订单对象为空')
        this.$message.error('无法打开评价界面，请刷新页面后重试')
        return
      }

      this.currentOrder = order
      this.ratingForm = {
        merchantScore: order.merchantScore || 0,
        deliverScore: order.deliverScore || 0
      }
      this.ratingDialogVisible = true
    },

    // 提交评分
    async submitRating() {
      // 检查当前订单是否存在
      if (!this.currentOrder || !this.currentOrder.id) {
        this.$message.error('无法提交评价，订单信息丢失')
        this.ratingDialogVisible = false
        return
      }

      this.isSubmitting = true

      try {
        // 调用后端评分接口
        await this.$axios.put(`/api/indent/score/${this.currentOrder.id}`, {
          score: this.ratingForm.merchantScore * 2, // 转换为0-10分制
          deliverScore: this.ratingForm.deliverScore * 2
        }, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })

        // 更新本地数据
        const orderIndex = this.orders.findIndex(o => o.id === this.currentOrder.id)
        if (orderIndex !== -1) {
          this.orders[orderIndex].merchantScore = this.ratingForm.merchantScore
          this.orders[orderIndex].deliverScore = this.ratingForm.deliverScore
          this.orders[orderIndex].hasRated = true
        }

        this.$message.success('评价提交成功！')
        this.ratingDialogVisible = false
      } catch (error) {
        console.error('提交评价失败:', error)
        this.$message.error('提交评价失败，请重试')
      } finally {
        this.isSubmitting = false
      }
    },

    // 获取所有订单
    async fetchAllOrders() {
      try {
        const response = await this.$axios.get('/api/indent', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        })

        const customerId = localStorage.getItem('userId')
        if (!customerId) {
          console.error('用户ID未找到')
          this.$message.error('用户信息错误，请重新登录')
          this.orders = []
          return
        }

        this.orders = await Promise.all(
            response.data
                .filter(order => order && order.customerID === customerId)
                .map(async order => {
                  if (!order) return null

                  try {
                    const [merchantRes, deliverRes] = await Promise.all([
                      this.$axios.get(`/api/merchant/phonenumber/${order.merchantID}`),
                      order.deliverID ?
                          this.$axios.get(`/api/deliver/phonenumber/${order.deliverID}`)
                          : Promise.resolve({ data: null })
                    ])

                    // 转换评分（后端是0-10分制，转换为0-5分制）
                    const merchantScore = order.score ? (order.score / 2).toFixed(1) : 0
                    const deliverScore = order.deliverScore ? (order.deliverScore / 2).toFixed(1) : 0

                    // 添加hasRated字段 - 判断是否已评价
                    const hasRated = !!(order.score && order.deliverScore)

                    return {
                      ...order,
                      merchantPhone: merchantRes?.data || '未知',
                      deliverPhone: deliverRes?.data || '未知',
                      merchantScore: parseFloat(merchantScore),
                      deliverScore: parseFloat(deliverScore),
                      hasRated
                    }
                  } catch (error) {
                    console.error('处理订单数据时出错:', error, order)
                    // 返回基本订单数据
                    return {
                      ...order,
                      merchantPhone: '获取失败',
                      deliverPhone: '获取失败',
                      merchantScore: 0,
                      deliverScore: 0,
                      hasRated: false
                    }
                  }
                })
        )

        // 过滤掉空值
        this.orders = this.orders.filter(order => order !== null)
      } catch (error) {
        console.error('获取订单失败:', error)
        this.$message.error('获取订单数据失败')
        this.orders = [] // 确保orders不为null
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-content {
  padding: 2rem;
  margin-top: 20px;
}

.profile-title {
  text-align: left;
  color: #303133;
  margin-bottom: 2rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #409EFF;
}

.profile-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  text-align: left;
}

.info-item {
  margin: 1.5rem 0;
  font-size: 16px;
  display: flex;
  align-items: baseline;
  gap: 1rem;
}

.info-label {
  font-weight: bold;
  color: #606266;
  min-width: 120px;
  text-align: right;
}

.info-value {
  color: #303133;
  flex-grow: 1;
  padding-left: 10px;
  border-left: 2px solid #eee;
}

.order-list {
  margin-top: 2rem;
}

.el-table {
  margin-top: 1rem;
  font-size: 14px;
}

.el-table__row td {
  padding: 12px 0;
}

.el-table .cell {
  white-space: nowrap;
}

/* 新增评分表单样式 */
.rating-form {
  padding: 20px;
}

.rating-item {
  margin-bottom: 25px;
  display: flex;
  align-items: center;
}

.rating-item label {
  width: 80px;
  font-weight: bold;
  margin-right: 15px;
}

.rating-item .el-rate {
  flex-grow: 1;
}
</style>
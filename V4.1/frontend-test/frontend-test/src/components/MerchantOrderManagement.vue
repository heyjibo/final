<template>
  <div>
    <MerchantNavBar />
    <div class="order-management">
      <el-card class="container">
        <div class="header">
          <h2>待接单订单</h2>
          <el-button
              type="primary"
              icon="el-icon-refresh"
              @click="fetchOrders"
          >刷新列表</el-button>
        </div>

        <el-table :data="pendingOrders" stripe style="width: 100%">
          <el-table-column prop="id" label="订单号" width="180"></el-table-column>
          <el-table-column prop="content" label="订单内容"></el-table-column>
          <el-table-column label="金额">
            <template #default="scope">¥{{ scope.row.money }}</template>
          </el-table-column>
          <el-table-column label="创建时间">
            <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="顾客电话">
            <template #default="scope">
              {{ scope.row.customerPhone }}
            </template>
          </el-table-column>
          <el-table-column label="骑手电话">
            <template #default="scope">
              {{ scope.row.deliverPhone || '未分配' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button
                  type="success"
                  size="mini"
                  @click="confirmOrder(scope.row)"
                  :disabled="scope.row.state !== 'Awaits confirmation'"
              >接单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>


      <el-card class="container accepted-orders">
        <h2>已确认的订单</h2>
        <el-table :data="acceptedOrders" stripe style="width: 100%">
          <el-table-column prop="id" label="订单号"></el-table-column>
          <el-table-column prop="content" label="订单内容"></el-table-column>
          <el-table-column label="金额">
            <template #default="scope">¥{{ scope.row.money }}</template>
          </el-table-column>
          <el-table-column label="创建时间">
            <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="状态">
            <template #default="scope">{{ stateMap[scope.row.state] }}</template>
          </el-table-column>
          <el-table-column label="顾客电话">
            <template #default="scope">
              {{ scope.row.customerPhone }}
            </template>
          </el-table-column>
          <el-table-column label="骑手电话">
            <template #default="scope">
              {{ scope.row.deliverPhone || '未分配' }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import MerchantNavBar from '@/components/MerchantNavBar.vue'
import dayjs from 'dayjs'

export default {
  components: { MerchantNavBar },
  data() {
    return {
      allOrders: [],
      stateMap: {
        'Awaits confirmation': '待确认',
        'Pending': '已确认',
        'Picking Up': '配送中',
        'Completed': '已完成'
      },
      refreshInterval: null,
      merchantOnline: false
    }
  },
  computed: {
    pendingOrders() {
      return this.allOrders.filter(order =>
          order.state === 'Awaits confirmation'
      )
    },
    acceptedOrders() {
      return this.allOrders.filter(order =>
          ['Pending', 'Picking Up'].includes(order.state)
      )
    }
  },
  async mounted() {
    await this.checkMerchantStatus();

    if (this.merchantOnline) {
      await this.fetchOrders();
      this.refreshInterval = setInterval(async () => {
        await this.fetchOrders();
      }, 30000);
    }
  },
  beforeDestroy() {
    clearInterval(this.refreshInterval);
  },

  methods: {
    formatDate(date) {
      return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '--'
    },

    async checkMerchantStatus() {
      try {
        const savedStatus = localStorage.getItem('merchantOnlineStatus');
        if (savedStatus !== null) {
          this.merchantOnline = savedStatus === 'true';
        } else {
          const merchantId = localStorage.getItem('userId');
          const response = await this.$axios.get(`/api/merchant/${merchantId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          });
          this.merchantOnline = response.data.isOnline;
          localStorage.setItem('merchantOnlineStatus', this.merchantOnline);
        }

        if (!this.merchantOnline) {
          this.$message.warning('商家当前处于离线状态，无法接单');
          this.$router.push('/merchant/dashboard');
        }
      } catch (error) {
        console.error('检查商家状态失败:', error);
        this.$message.error('无法获取商家状态');
        this.$router.push('/merchant/dashboard');
      }
    },

    async fetchOrders() {
      try {
        const merchantId = localStorage.getItem('userId')
        const { data } = await this.$axios.get(`/api/indent/getByMerchantID/${merchantId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        })

        this.allOrders = await Promise.all(
            data.map(async order => {
              try {
                const [customerRes, deliverRes] = await Promise.all([
                  this.$axios.get(`/api/customer/phonenumber/${order.customerID}`),
                  order.deliverID ?
                      this.$axios.get(`/api/deliver/phonenumber/${order.deliverID}`)
                      : Promise.resolve({ data: null })
                ])

                return {
                  ...order,
                  customerPhone: customerRes.data,
                  deliverPhone: deliverRes?.data || '未分配'
                }
              } catch (error) {
                console.error(`获取订单${order.id}电话信息失败:`, error)
                return {
                  ...order,
                  customerPhone: '获取失败',
                  deliverPhone: '获取失败'
                }
              }
            })
        )
      } catch (error) {
        this.$message.error(`获取订单失败: ${error.response?.data?.error || error.message}`)
      }
    },
    async confirmOrder(order) {
      if (!this.merchantOnline) {
        this.$message.warning('商家当前处于离线状态，无法接单');
        return;
      }

      try {
        await this.$confirm('确定要接受此订单吗？', '接单确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await this.$axios.put(`/api/indent/awaits/${order.id}`, null, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        })

        this.$message.success('接单成功')
        await this.fetchOrders()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`接单失败: ${error.response?.data?.error || error.message}`)
        }
      }
    }
  }
}
</script>

<style scoped>
.order-management {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.container {
  margin-bottom: 30px;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.accepted-orders {
  margin-top: 40px;
}
</style>
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


        <el-card class="order-list">
          <h3>我的订单</h3>
          <el-table :data="orders" stripe style="width: 100%">
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
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import CustomerNavBar from '@/components/CustomerNavBar.vue'

export default {
  components: {
    CustomerNavBar
  },
  data() {
    return {
      customerInfo: {},
      orders: []
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
        case 'Delivering': return 'warning'
        case 'Pending': return 'info'
        default: return 'danger'
      }
    },

    async fetchCustomerProfile() {
      try {
        const userId = localStorage.getItem('userId')
        const response = await this.$axios.get(`/api/customer/${userId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.customerInfo = response.data
      } catch (error) {
        console.error('获取顾客信息失败:', error)
      }
    },

    async fetchAllOrders() {
      try {
        const response = await this.$axios.get('/api/indent', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        })

        const customerId = localStorage.getItem('userId')
        this.orders = await Promise.all(
            response.data
                .filter(order => order.customerID === customerId)
                .map(async order => {
                  const [merchantRes, deliverRes] = await Promise.all([
                    this.$axios.get(`/api/merchant/phonenumber/${order.merchantID}`),
                    order.deliverID ?
                        this.$axios.get(`/api/deliver/phonenumber/${order.deliverID}`)
                        : Promise.resolve({ data: null })
                  ])

                  return {
                    ...order,
                    merchantPhone: merchantRes.data,
                    deliverPhone: deliverRes?.data
                  }
                })
        )
      } catch (error) {
        console.error('获取订单失败:', error)
        this.$message.error('获取订单数据失败')
      }
    }
  }
}
</script>

<style scoped>

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


.order-list {
  margin-top: 2rem;
}

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
</style>
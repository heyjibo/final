<template>
  <div class="dashboard-container">
    <!-- Welcome message -->
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-card">
          <div class="welcome-info">
            <div class="welcome-avatar">
              <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="Rider Avatar">
            </div>
            <div class="welcome-text">
              <h2>Welcome back, {{ riderName }}</h2>
              <p>{{ currentDate }}</p>
            </div>
          </div>
          <div class="welcome-status">
            <el-tag>Online</el-tag>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- Statistics cards -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67C23A;">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statistics.ordersToday }}</p>
              <p class="stat-title">Today's Orders</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409EFF;">
              <i class="el-icon-money"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">${{ statistics.income }}</p>
              <p class="stat-title">Today's Income</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <i class="el-icon-location-information"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statistics.distance }}km</p>
              <p class="stat-title">Distance</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #F56C6C;">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statistics.rating }}</p>
              <p class="stat-title">Rating</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- Pending orders -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Pending Orders</span>
          </div>
          <el-table :data="orders" stripe style="width: 100%">
            <el-table-column prop="id" label="Order ID" width="120"></el-table-column>
            <el-table-column prop="address" label="Delivery Address" width="220"></el-table-column>
            <el-table-column prop="time" label="ETA" width="120"></el-table-column>
            <el-table-column prop="amount" label="Fee" width="100"></el-table-column>
            <el-table-column prop="status" label="Status">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 'Picking up' ? 'warning' : 'primary'">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="Action" width="120">
              <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="handleOrder(scope.row)" v-if="scope.row.status === 'Pending'">Accept</el-button>
                <span v-else>Processing</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getRiderInfo, getRiderStatistics, getPendingOrders, takeOrder } from '@/api/rider'

export default {
  name: 'RiderDashboard',
  data() {
    return {
      riderName: 'Rider',
      currentDate: new Date().toLocaleDateString(),
      statistics: {
        ordersToday: 0,
        income: 0,
        distance: 0,
        rating: 0
      },
      orders: []
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    // Get data
    fetchData() {
      // Get rider info
      getRiderInfo().then(response => {
        if (response.code === 200) {
          this.riderName = response.data.name
        }
      })
      
      // Get statistics
      getRiderStatistics().then(response => {
        if (response.code === 200) {
          this.statistics = response.data
        }
      })
      
      // Get order data
      getPendingOrders().then(response => {
        if (response.code === 200) {
          this.orders = response.data.items
          // Convert status text to English
          this.orders.forEach(order => {
            if (order.status === '待接单') {
              order.status = 'Pending'
            } else if (order.status === '取餐中') {
              order.status = 'Picking up'
            }
          })
        }
      })
    },
    
    // Get translated status text
    getStatusText(status) {
      if (status === 'Pending') return 'Pending'
      if (status === 'Picking up') return 'Picking up'
      return status
    },
    
    // Handle order
    handleOrder(order) {
      this.$confirm('Are you sure to accept this order?', 'Confirm', {
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        type: 'info'
      }).then(() => {
        takeOrder(order.id).then(response => {
          if (response.code === 200) {
            this.$message.success('Order accepted successfully')
            // Update order status
            order.status = 'Picking up'
          }
        })
      }).catch(() => {
        // Cancel accept
      })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.welcome-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-info {
  display: flex;
  align-items: center;
}
.welcome-avatar img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
}
.welcome-text {
  margin-left: 20px;
}
.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
}
.welcome-text p {
  margin: 0;
  color: #909399;
}
.stat-card {
  height: 100px;
}
.stat-content {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}
.stat-icon i {
  font-size: 30px;
  color: #fff;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  line-height: 1.2;
}
.stat-title {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}
</style> 
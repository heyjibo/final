<template>
  <div class="rider-orders-container">
    <NavBar />

    <div class="orders-container">
      <!-- 待接单订单 -->
      <div class="order-section">
        <h3 class="section-title">待接取的订单</h3>
        <div v-if="pendingOrders.length === 0" class="empty-tip">暂无待接订单</div>
        <el-card
            v-for="order in pendingOrders"
            :key="order.id"
            class="order-card"
        >
          <div class="order-info">
            <div>单号：{{ order.id }}</div>
            <div>距离：{{ order.distance }}米</div>
            <div>金额：¥{{ order.money }}</div>
          </div>
          <el-button
              type="primary"
              @click="acceptOrder(order.id)"
              class="action-btn"
          >
            接收订单
          </el-button>
        </el-card>
      </div>

      <!-- 取餐中订单 -->
      <div class="order-section">
        <h3 class="section-title">取餐中的订单</h3>
        <div v-if="pickingOrders.length === 0" class="empty-tip">暂无取餐中订单</div>
        <el-card
            v-for="order in pickingOrders"
            :key="order.id"
            class="order-card"
        >
          <div class="order-info">
            <div>单号：{{ order.id }}</div>
            <div>取餐时间：{{ formatTime(order.time) }}</div>
          </div>
          <el-button
              type="success"
              @click="startDelivery(order.id)"
              class="action-btn"
          >
            开始配送
          </el-button>
        </el-card>
      </div>

      <!-- 配送中订单 -->
      <div class="order-section">
        <h3 class="section-title">配送中的订单</h3>
        <div v-if="deliveringOrders.length === 0" class="empty-tip">暂无配送中订单</div>
        <el-card
            v-for="order in deliveringOrders"
            :key="order.id"
            class="order-card"
        >
          <div class="order-info">
            <div>单号：{{ order.id }}</div>
            <div>预计送达：{{ formatTime(order.time) }}</div>
          </div>
          <el-button
              type="warning"
              @click="completeOrder(order.id)"
              class="action-btn"
          >
            确认送达
          </el-button>
        </el-card>
      </div>

      <!-- 已完成订单 -->
      <div class="order-section">
        <h3 class="section-title">已完成的订单</h3>
        <div v-if="completedOrders.length === 0" class="empty-tip">暂无完成订单</div>
        <el-card
            v-for="order in completedOrders"
            :key="order.id"
            class="order-card completed"
        >
          <div class="order-info">
            <div>单号：{{ order.id }}</div>
            <div>完成时间：{{ formatTime(order.time) }}</div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from './NavBar.vue'

export default {
  components: {
    NavBar
  },
  data() {
    return {
      orders: []
    }
  },
  computed: {
    pendingOrders() {
      return this.orders.filter(o => o.state === 'Pending')
    },
    pickingOrders() {
      return this.orders.filter(o => o.state === 'Picking Up')
    },
    deliveringOrders() {
      return this.orders.filter(o => o.state === 'Delivering')
    },
    completedOrders() {
      return this.orders.filter(o => o.state === 'Completed')
    }
  },
  async mounted() {
    await this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      try {
        const token = localStorage.getItem('token')
        const res = await this.$axios.get('/api/indent/my-orders', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        this.orders = res.data
      } catch (error) {
        this.$message.error('获取订单失败')
      }
    },

    async acceptOrder(orderId) {
      try {
        await this.$axios.put(`/api/indent/pick/${orderId}`, {id:orderId}, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.$message.success('接单成功')
        await this.fetchOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    async startDelivery(orderId) {
      try {
        await this.$axios.put(`/api/indent/deliver/${orderId}`, {id:orderId}, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.$message.success('开始配送')
        await this.fetchOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    async completeOrder(orderId) {
      try {
        await this.$axios.put(`/api/indent/complete/${orderId}`, {id:orderId}, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.$message.success('订单已完成')
        await this.fetchOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    formatTime(time) {
      return new Date(time).toLocaleString()
    }
  }
}
</script>

<style scoped>
.rider-orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.orders-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 20px;
}

.order-section {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.section-title {
  color: #409EFF;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
  margin-bottom: 15px;
}

.order-card {
  margin-bottom: 15px;
  transition: transform 0.3s;
}

.order-card:hover {
  transform: translateY(-3px);
}

.order-info {
  margin-bottom: 12px;
  line-height: 1.8;
}

.action-btn {
  width: 100%;
}

.empty-tip {
  color: #909399;
  text-align: center;
  padding: 20px;
}

.completed {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
}
</style>




<template>
  <div class="rider-orders-container">
    <NavBar />


    <div class="orders-container">

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
            <div>商家电话：{{ order.merchantPhone }}</div>
            <div>顾客电话：{{ order.customerPhone }}</div>
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
            <div>商家电话：{{ order.merchantPhone }}</div>
            <div>顾客电话：{{ order.customerPhone }}</div>
            <div>取餐时间：{{ actionTimes.accepted[order.id] || formatTime(order.pickupTime) }}</div>
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
            <div>商家电话：{{ order.merchantPhone }}</div>
            <div>顾客电话：{{ order.customerPhone }}</div>
            <div>开始配送：{{ actionTimes.delivering[order.id] || formatTime(order.deliverTime) }}</div>
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
            <div>商家电话：{{ order.merchantPhone }}</div>
            <div>顾客电话：{{ order.customerPhone }}</div>
            <div>完成时间：{{ actionTimes.completed[order.id] || formatTime(order.completionTime) }}</div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from './NavBar.vue'
import {jwtDecode as jwt_decode} from 'jwt-decode';

export default {
  components: {
    NavBar
  },
  data() {
    return {
      orders: [],
      actionTimes: {
        accepted: {},
        delivering: {},
        completed: {},
        autoRefreshTimer: null
      }
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
    await this.fetchOrders();
    this.autoRefreshTimer = setInterval(async () => {
      await this.fetchOrders();
    }, 15000);
  },
  beforeDestroy() {
    clearInterval(this.autoRefreshTimer);
  },

  methods: {
    async fetchOrders() {
      try {
        const token = localStorage.getItem('token');
        const res = await this.$axios.get('/api/indent/my-orders', {
          headers: { Authorization: `Bearer ${token}` }
        });

        // 独立获取商家和顾客电话
        this.orders = await Promise.all(
            res.data.map(async (order) => {
              let merchantPhone = '获取失败';
              let customerPhone = '获取失败';

              // 获取商家电话
              try {
                const merchantRes = await this.$axios.get(
                    `/api/merchant/phonenumber/${order.merchantID}`
                );
                merchantPhone = merchantRes.data;
              } catch (error) {
                console.error(`商家电话获取失败（订单 ${order.id}）:`, error);
              }

              // 获取顾客电话
              try {
                const customerRes = await this.$axios.get(
                    `/api/customer/phonenumber/${order.customerID}`
                );
                customerPhone = customerRes.data;
              } catch (error) {
                console.error(`顾客电话获取失败（订单 ${order.id}）:`, error);
              }

              return {
                ...order,
                merchantPhone,
                customerPhone,
              };
            })
        );
      } catch (error) {
        this.$message.error('获取订单失败');
        console.error('获取订单列表失败:', error);
      }
    },

    async acceptOrder(orderId) {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          const decoded = jwt_decode(token);
          console.log(decoded);
        } else {
          console.error('令牌为空');
        }

        const decoded = jwt_decode(token); // 解码 Token
        const subObj = JSON.parse(decoded.sub);
        // 解析JSON字符串为对象
        console.log(subObj);
        console.log(typeof(subObj));
        const workID = subObj.toString();
        console.log(workID);
        console.log(typeof (workID));


        await this.$axios.put(`/api/indent/pick/${orderId}`, {workID}, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.$set(this.actionTimes.accepted, orderId, new Date().toLocaleString())
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
        this.$set(this.actionTimes.delivering, orderId, new Date().toLocaleString())
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
        this.$set(this.actionTimes.completed, orderId, new Date().toLocaleString())
        this.$message.success('订单已完成')
        await this.fetchOrders()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },


    formatTime(time) {
      if (!time) return '--'
      const date = new Date(time)
      const pad = n => n.toString().padStart(2, '0')
      return `${date.getFullYear()}/${pad(date.getMonth()+1)}/${pad(date.getDate())} ` +
          `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
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
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-top: 20px;
}

.order-section {
  width: 95%;
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 20px;
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

.order-info div:nth-child(2) {
  color: #666;
  font-size: 0.9em;
  margin: 5px 0;
}

.order-info div:nth-child(3) {
  color: #409EFF;
  font-weight: bold;
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






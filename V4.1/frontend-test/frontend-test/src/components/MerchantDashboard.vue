<template>
  <div>
    <MerchantNavBar :isOnline="isOnline" @online-status-changed="handleStatusChanged" />
    <div class="dashboard-container">
      <div class="header">
        <h2>{{ merchantName }} 的主页</h2>
      </div>

      <!-- 添加在线状态切换 -->
      <div class="online-switch">
        <el-switch
            v-model="isOnline"
            active-text="在线"
            inactive-text="离线"
            @change="toggleOnlineStatus"
        ></el-switch>
        <span class="status-text">{{ isOnline ? '（当前可接单）' : '（当前不可接单）' }}</span>
      </div>

      <div class="stats-container">
        <el-card class="stat-card">
          <h3>菜品数量</h3>
          <div class="stat-value">{{ stats.dishCount }}</div>
        </el-card>

        <el-card class="stat-card">
          <h3>可用骑手</h3>
          <div class="stat-value">{{ stats.riderCount }}</div>
        </el-card>

        <el-card class="stat-card">
          <h3>累计收益</h3>
          <div class="stat-value">¥{{ stats.totalRevenue }}</div>
        </el-card>
      </div>

      <el-card class="order-list">
        <h3>已完成订单</h3>
        <el-table :data="orders" stripe>
          <el-table-column prop="id" label="订单号"></el-table-column>
          <el-table-column prop="content" label="菜品"></el-table-column>
          <el-table-column prop="amount" label="金额"></el-table-column>
          <el-table-column prop="time" label="完成时间"></el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs';
import MerchantNavBar from '@/components/MerchantNavBar.vue'

export default {
  components: { MerchantNavBar },
  data() {
    return {
      merchantName: '加载中...',
      merchantId: localStorage.getItem('userId'),
      stats: {
        dishCount: 0,
        riderCount: 0,
        totalRevenue: 0
      },
      orders: [],
      loading: false,
      visibilityHandler: null,
      refreshTimer: null,
      isOnline: false,
      hasRestoredStatus: false
    }
  },
  async mounted() {
    this.restoreOnlineStatus();

    await this.fetchData();

    this.visibilityHandler = () => {
      if (!document.hidden) {
        this.refreshTimer = setInterval(async () => {
          await this.fetchData();
          this.syncOnlineStatus();
        }, 30000);
      } else {
        clearInterval(this.refreshTimer);
      }
    };

    document.addEventListener('visibilitychange', this.visibilityHandler);
    this.visibilityHandler();
  },
  beforeDestroy() {
    document.removeEventListener('visibilitychange', this.visibilityHandler);
    clearInterval(this.refreshTimer);
  },

  methods: {
    restoreOnlineStatus() {
      const savedStatus = localStorage.getItem('merchantOnlineStatus');
      if (savedStatus !== null) {
        this.isOnline = savedStatus === 'true';
        this.hasRestoredStatus = true;
      }
    },

    async syncOnlineStatus() {
      if (!this.hasRestoredStatus) return;

      try {
        const merchantId = localStorage.getItem('userId');
        const endpoint = this.isOnline
            ? `/api/merchant/Online/${merchantId}`
            : `/api/merchant/Outline/${merchantId}`;

        await this.$axios.put(endpoint, null, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
      } catch (error) {
        console.error('在线状态同步失败:', error);
      }
    },

    formatDate(dateString) {
      return dateString ? dayjs(dateString).format('YYYY-MM-DD HH:mm:ss') : '未知时间';
    },

    async fetchOnlineStatus() {
      try {
        const merchantId = localStorage.getItem('userId');
        const response = await this.$axios.get(`/api/merchant/${merchantId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });

        if (!this.hasRestoredStatus) {
          this.isOnline = response.data.isOnline;
          localStorage.setItem('merchantOnlineStatus', this.isOnline);
        }
      } catch (error) {
        console.error('获取在线状态失败:', error);
      }
    },

    async toggleOnlineStatus() {
      try {
        const merchantId = localStorage.getItem('userId');
        const endpoint = this.isOnline
            ? `/api/merchant/Online/${merchantId}`
            : `/api/merchant/Outline/${merchantId}`;

        await this.$axios.put(endpoint, null, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });

        localStorage.setItem('merchantOnlineStatus', this.isOnline);

        // 触发状态变更事件
        this.$emit('online-status-changed', this.isOnline);

        this.$message.success(`已${this.isOnline ? '上线' : '下线'}`);
      } catch (error) {
        console.error('切换状态失败:', error);
        this.$message.error('切换状态失败');
        this.isOnline = !this.isOnline;
      }
    },

    async fetchData() {
      try {
        this.loading = true;
        this.merchantName = '加载中...';
        this.stats = { dishCount: 0, riderCount: 0, totalRevenue: 0 };
        this.orders = [];

        await this.fetchOnlineStatus();

        const [dishRes, ordersRes, merchantRes, deliverRes] = await Promise.all([
          this.$axios.get(`/api/dish/getByMerchant/${this.merchantId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          }),
          this.$axios.get(`/api/indent/getByMerchantID/${this.merchantId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          }),
          this.$axios.get(`/api/merchant/${this.merchantId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          }),
          this.$axios.get('/api/deliver', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          })
        ]);

        this.stats.dishCount = Array.isArray(dishRes?.data) ? dishRes.data.length : 0;

        const validRiders = Array.isArray(deliverRes?.data)
            ? deliverRes.data.filter(rider => rider.numofindent < 10)
            : [];
        this.stats.riderCount = validRiders.length;

        const completedOrders = Array.isArray(ordersRes?.data)
            ? ordersRes.data.filter(order => order.state === 'Completed')
            : [];

        this.stats.totalRevenue = completedOrders.reduce((sum, order) => sum + (order.money || 0), 0);

        this.orders = completedOrders.map(order => ({
          id: order.id || 'N/A',
          content: order.content ? order.content.replace(/,/g, ', ') : '无菜品信息',
          amount: order.money ? `¥${order.money}` : '待计算',
          time: this.formatDate(order.time)
        }));

        this.merchantName = merchantRes?.data?.name || '未知商家';

      } catch (error) {
        console.error('数据加载失败:', error);
        clearInterval(this.refreshTimer);
        this.refreshTimer = setInterval(this.fetchData, 120000);
        this.$message.error('数据加载失败: ' + (error.response?.data?.message || error.message));
      } finally {
        this.loading = false;
      }
    },

    handleStatusChanged(newStatus) {
      this.isOnline = newStatus;
    },

    handleLogout() {
      localStorage.removeItem('merchantOnlineStatus');
      localStorage.clear();
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.online-switch {
  margin-bottom: 30px;
  padding: 15px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
}

.status-text {
  margin-left: 15px;
  font-size: 14px;
  color: #606266;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}

.order-list {
  margin-top: 20px;
}
</style>
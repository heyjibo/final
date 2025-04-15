<template>
  <div class="delivery-dashboard">
    <header class="dashboard-header">
      <h1>外卖配送管理系统</h1>
      <div class="user-info">
        <span>{{ user?.username }}</span>
        <button @click="handleLogout" class="logout-btn">退出登录</button>
      </div>
    </header>

    <div class="dashboard-content">
      <div class="sidebar">
        <div 
          v-for="(item, index) in menuItems" 
          :key="index"
          :class="['menu-item', { active: activeMenu === item.key }]"
          @click="activeMenu = item.key"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span class="menu-text">{{ item.text }}</span>
        </div>
      </div>

      <div class="main-content">
        <div v-if="activeMenu === 'overview'" class="content-section">
          <h2>今日概览</h2>
          <div class="stats-grid">
            <div class="stat-card">
              <h3>待配送订单</h3>
              <div class="stat-value">{{ pendingOrders }}</div>
            </div>
            <div class="stat-card">
              <h3>已完成订单</h3>
              <div class="stat-value">{{ completedOrders }}</div>
            </div>
            <div class="stat-card">
              <h3>今日收入</h3>
              <div class="stat-value">¥{{ todayEarnings }}</div>
            </div>
            <div class="stat-card">
              <h3>配送评分</h3>
              <div class="stat-value">{{ deliveryScore }}/5.0</div>
            </div>
          </div>
        </div>

        <div v-if="activeMenu === 'orders'" class="content-section">
          <h2>订单管理</h2>
          <div class="order-filters">
            <button 
              v-for="(filter, idx) in orderFilters" 
              :key="idx"
              :class="['filter-btn', { active: activeFilter === filter.key }]"
              @click="activeFilter = filter.key"
            >
              {{ filter.text }}
            </button>
          </div>

          <div class="order-list">
            <div v-if="filteredOrders.length === 0" class="empty-state">
              暂无订单数据
            </div>
            <div v-else v-for="order in filteredOrders" :key="order.id" class="order-card">
              <div class="order-header">
                <span class="order-id">订单号: {{ order.id }}</span>
                <span :class="['order-status', order.status]">{{ getStatusText(order.status) }}</span>
              </div>
              <div class="order-details">
                <div class="order-info">
                  <div><strong>客户:</strong> {{ order.customerName }}</div>
                  <div><strong>地址:</strong> {{ order.address }}</div>
                  <div><strong>时间:</strong> {{ formatDate(order.createdAt) }}</div>
                </div>
                <div class="order-actions">
                  <button v-if="order.status === 'pending'" @click="acceptOrder(order.id)" class="action-btn accept">接单</button>
                  <button v-if="order.status === 'accepted'" @click="completeOrder(order.id)" class="action-btn complete">送达</button>
                  <button v-if="order.status === 'pending'" @click="rejectOrder(order.id)" class="action-btn reject">拒绝</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeMenu === 'profile'" class="content-section">
          <h2>个人信息</h2>
          <div class="profile-card">
            <div class="profile-header">
              <div class="avatar">
                {{ user?.username?.charAt(0).toUpperCase() }}
              </div>
              <div class="profile-info">
                <h3>{{ user?.username }}</h3>
                <p>{{ user?.email }}</p>
                <p>{{ user?.phone }}</p>
              </div>
            </div>
            
            <div class="profile-stats">
              <div class="profile-stat">
                <h4>总订单数</h4>
                <div>{{ totalOrders }}</div>
              </div>
              <div class="profile-stat">
                <h4>平均评分</h4>
                <div>{{ avgRating }}/5.0</div>
              </div>
              <div class="profile-stat">
                <h4>总收入</h4>
                <div>¥{{ totalEarnings }}</div>
              </div>
            </div>

            <div class="profile-actions">
              <button class="profile-btn">修改资料</button>
              <button class="profile-btn">修改密码</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '../../store/auth';

const router = useRouter();
const authStore = useAuthStore();

// 使用 authStore 中的用户信息
const user = computed(() => authStore.user);

// 菜单选项
const activeMenu = ref('overview');
const menuItems = [
  { key: 'overview', text: '概览', icon: '📊' },
  { key: 'orders', text: '订单管理', icon: '📦' },
  { key: 'profile', text: '个人信息', icon: '👤' }
];

// 订单过滤
const activeFilter = ref('pending');
const orderFilters = [
  { key: 'pending', text: '待接单' },
  { key: 'accepted', text: '已接单' },
  { key: 'completed', text: '已完成' },
  { key: 'all', text: '全部' }
];

// 统计数据
const pendingOrders = ref(0);
const completedOrders = ref(0);
const todayEarnings = ref(0);
const deliveryScore = ref(0);
const totalOrders = ref(0);
const avgRating = ref(0);
const totalEarnings = ref(0);

// 订单数据
const orders = ref<any[]>([]);

// 过滤后的订单
const filteredOrders = computed(() => {
  if (activeFilter.value === 'all') {
    return orders.value;
  }
  return orders.value.filter(order => order.status === activeFilter.value);
});

// 获取订单状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'pending': '待接单',
    'accepted': '配送中',
    'completed': '已完成',
    'rejected': '已拒绝'
  };
  return statusMap[status] || status;
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

// 接单
const acceptOrder = async (orderId: string) => {
  try {
    await axios.post(`/api/delivery/orders/${orderId}/accept`);
    fetchOrders();
    fetchStats();
  } catch (error) {
    console.error('接单失败:', error);
  }
};

// 完成订单
const completeOrder = async (orderId: string) => {
  try {
    await axios.post(`/api/delivery/orders/${orderId}/complete`);
    fetchOrders();
    fetchStats();
  } catch (error) {
    console.error('完成订单失败:', error);
  }
};

// 拒绝订单
const rejectOrder = async (orderId: string) => {
  try {
    await axios.post(`/api/delivery/orders/${orderId}/reject`);
    fetchOrders();
    fetchStats();
  } catch (error) {
    console.error('拒绝订单失败:', error);
  }
};

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await axios.get('/api/delivery/stats');
    const stats = response.data;
    
    pendingOrders.value = stats.pendingOrders;
    completedOrders.value = stats.completedOrders;
    todayEarnings.value = stats.todayEarnings;
    deliveryScore.value = stats.deliveryScore;
    totalOrders.value = stats.totalOrders;
    avgRating.value = stats.avgRating;
    totalEarnings.value = stats.totalEarnings;
  } catch (error) {
    // 静默处理错误，使用默认值
  }
};

// 获取订单列表
const fetchOrders = async () => {
  try {
    const response = await axios.get('/api/delivery/orders');
    orders.value = response.data;
  } catch (error) {
    // 静默处理错误，保持订单列表为空
  }
};

// 退出登录
const handleLogout = () => {
  authStore.logout();
};

onMounted(() => {
  // 不需要再初始化用户信息，由 authStore 管理
  
  // 模拟数据获取
  fetchStats();
  fetchOrders();
});
</script>

<style scoped>
.delivery-dashboard {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.dashboard-header {
  background-color: #4a6cf7;
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.dashboard-content {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 240px;
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: #555;
}

.menu-item:hover {
  background-color: #f8f9fa;
  color: #4a6cf7;
}

.menu-item.active {
  background-color: #f0f4ff;
  color: #4a6cf7;
  font-weight: 500;
  border-left: 3px solid #4a6cf7;
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
}

.main-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

.content-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  padding: 25px;
}

h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 20px;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.stat-card h3 {
  font-size: 16px;
  color: #666;
  margin-top: 0;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 26px;
  font-weight: 600;
  color: #333;
}

/* 订单列表样式 */
.order-filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.filter-btn.active {
  background-color: #4a6cf7;
  color: white;
  border-color: #4a6cf7;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.order-id {
  font-weight: 500;
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.order-status.pending {
  background-color: #ffe0b2;
  color: #e65100;
}

.order-status.accepted {
  background-color: #b3e5fc;
  color: #0277bd;
}

.order-status.completed {
  background-color: #c8e6c9;
  color: #2e7d32;
}

.order-status.rejected {
  background-color: #ffcdd2;
  color: #c62828;
}

.order-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  line-height: 1.6;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  border: none;
}

.action-btn.accept {
  background-color: #4a6cf7;
  color: white;
}

.action-btn.complete {
  background-color: #4caf50;
  color: white;
}

.action-btn.reject {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
}

/* 个人信息样式 */
.profile-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 25px;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.avatar {
  width: 80px;
  height: 80px;
  background-color: #4a6cf7;
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  margin-right: 20px;
}

.profile-info h3 {
  margin-top: 0;
  margin-bottom: 5px;
  font-size: 20px;
}

.profile-info p {
  margin: 5px 0;
  color: #666;
}

.profile-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.profile-stat {
  text-align: center;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  flex: 1;
  margin: 0 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.profile-stat h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #666;
  font-size: 16px;
  font-weight: normal;
}

.profile-stat div {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.profile-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.profile-btn {
  padding: 10px 20px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 5px;
  color: #555;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.profile-btn:hover {
  background-color: #e9ecef;
}
</style> 
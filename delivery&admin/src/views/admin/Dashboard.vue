<template>
  <div class="admin-dashboard">
    <header class="dashboard-header">
      <h1>管理员系统</h1>
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
          <h2>系统概览</h2>
          <div class="stats-grid">
            <div class="stat-card">
              <h3>用户总数</h3>
              <div class="stat-value">{{ totalUsers }}</div>
            </div>
            <div class="stat-card">
              <h3>配送员数量</h3>
              <div class="stat-value">{{ deliveryUsersCount }}</div>
            </div>
            <div class="stat-card">
              <h3>订单总数</h3>
              <div class="stat-value">{{ totalOrders }}</div>
            </div>
            <div class="stat-card">
              <h3>今日订单</h3>
              <div class="stat-value">{{ todayOrders }}</div>
            </div>
          </div>
          
          <div class="chart-container">
            <h3>订单统计</h3>
            <div class="chart-placeholder">
              <!-- 这里可以放入真实的图表组件 -->
              <div class="mock-chart">
                <div v-for="i in 7" :key="i" class="chart-bar" :style="{ height: Math.random() * 100 + 50 + 'px' }"></div>
              </div>
              <div class="chart-labels">
                <span v-for="i in 7" :key="i">{{ getLastNDays(i-1) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeMenu === 'users'" class="content-section">
          <h2>用户管理</h2>
          <div class="user-filters">
            <button 
              v-for="(filter, idx) in userFilters" 
              :key="idx"
              :class="['filter-btn', { active: activeUserFilter === filter.key }]"
              @click="activeUserFilter = filter.key"
            >
              {{ filter.text }}
            </button>
            <div class="search-box">
              <input type="text" v-model="searchQuery" placeholder="搜索用户..." />
              <button class="search-btn">搜索</button>
            </div>
          </div>

          <div class="user-list">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>电子邮箱</th>
                  <th>用户类型</th>
                  <th>注册时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.userType === 'delivery' ? '配送员' : '管理员' }}</td>
                  <td>{{ formatDate(user.createdAt) }}</td>
                  <td>
                    <span :class="['status-badge', user.isActive ? 'active' : 'inactive']">
                      {{ user.isActive ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td>
                    <div class="action-buttons">
                      <button class="btn-edit" @click="editUser(user.id)">编辑</button>
                      <button 
                        :class="['btn-status', user.isActive ? 'btn-disable' : 'btn-enable']"
                        @click="toggleUserStatus(user.id)"
                      >
                        {{ user.isActive ? '禁用' : '启用' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-if="activeMenu === 'orders'" class="content-section">
          <h2>订单管理</h2>
          <div class="order-filters">
            <button 
              v-for="(filter, idx) in orderFilters" 
              :key="idx"
              :class="['filter-btn', { active: activeOrderFilter === filter.key }]"
              @click="activeOrderFilter = filter.key"
            >
              {{ filter.text }}
            </button>
            <div class="search-box">
              <input type="text" v-model="orderSearchQuery" placeholder="搜索订单..." />
              <button class="search-btn">搜索</button>
            </div>
          </div>

          <div class="order-admin-list">
            <table>
              <thead>
                <tr>
                  <th>订单号</th>
                  <th>客户名称</th>
                  <th>配送地址</th>
                  <th>配送员</th>
                  <th>订单状态</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in filteredOrders" :key="order.id">
                  <td>{{ order.id }}</td>
                  <td>{{ order.customerName }}</td>
                  <td>{{ order.address }}</td>
                  <td>{{ order.deliveryPerson || '未分配' }}</td>
                  <td>
                    <span :class="['status-badge', order.status]">
                      {{ getOrderStatusText(order.status) }}
                    </span>
                  </td>
                  <td>{{ formatDate(order.createdAt) }}</td>
                  <td>
                    <div class="action-buttons">
                      <button class="btn-view" @click="viewOrderDetails(order.id)">查看</button>
                      <button class="btn-edit" @click="editOrder(order.id)">编辑</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-if="activeMenu === 'settings'" class="content-section">
          <h2>系统设置</h2>
          <div class="settings-card">
            <h3>系统配置</h3>
            <form @submit.prevent="saveSettings">
              <div class="form-group">
                <label>系统名称</label>
                <input type="text" v-model="settings.systemName" />
              </div>
              
              <div class="form-group">
                <label>客服电话</label>
                <input type="text" v-model="settings.supportPhone" />
              </div>
              
              <div class="form-group">
                <label>自动分配订单</label>
                <div class="toggle-switch">
                  <input type="checkbox" id="autoAssign" v-model="settings.autoAssignOrders" />
                  <label for="autoAssign"></label>
                </div>
              </div>
              
              <div class="form-group">
                <label>允许新用户注册</label>
                <div class="toggle-switch">
                  <input type="checkbox" id="allowRegistration" v-model="settings.allowRegistration" />
                  <label for="allowRegistration"></label>
                </div>
              </div>
              
              <div class="form-actions">
                <button type="submit" class="btn-save">保存设置</button>
                <button type="button" class="btn-reset" @click="resetSettings">重置</button>
              </div>
            </form>
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
  { key: 'overview', text: '系统概览', icon: '📊' },
  { key: 'users', text: '用户管理', icon: '👥' },
  { key: 'orders', text: '订单管理', icon: '📦' },
  { key: 'settings', text: '系统设置', icon: '⚙️' }
];

// 统计数据
const totalUsers = ref(0);
const deliveryUsersCount = ref(0);
const totalOrders = ref(0);
const todayOrders = ref(0);

// 用户管理
const users = ref<any[]>([]);
const activeUserFilter = ref('all');
const searchQuery = ref('');
const userFilters = [
  { key: 'all', text: '所有用户' },
  { key: 'delivery', text: '配送员' },
  { key: 'admin', text: '管理员' },
  { key: 'active', text: '启用' },
  { key: 'inactive', text: '禁用' }
];

// 订单管理
const orders = ref<any[]>([]);
const activeOrderFilter = ref('all');
const orderSearchQuery = ref('');
const orderFilters = [
  { key: 'all', text: '所有订单' },
  { key: 'pending', text: '待接单' },
  { key: 'accepted', text: '配送中' },
  { key: 'completed', text: '已完成' },
  { key: 'rejected', text: '已拒绝' }
];

// 系统设置
const settings = ref({
  systemName: '外卖配送管理系统',
  supportPhone: '400-123-4567',
  autoAssignOrders: true,
  allowRegistration: true
});
const originalSettings = ref({
  systemName: '',
  supportPhone: '',
  autoAssignOrders: false,
  allowRegistration: false
});

// 计算属性：过滤用户
const filteredUsers = computed(() => {
  let result = [...users.value];
  
  // 应用类型过滤
  if (activeUserFilter.value === 'delivery') {
    result = result.filter(user => user.userType === 'delivery');
  } else if (activeUserFilter.value === 'admin') {
    result = result.filter(user => user.userType === 'admin');
  } else if (activeUserFilter.value === 'active') {
    result = result.filter(user => user.isActive);
  } else if (activeUserFilter.value === 'inactive') {
    result = result.filter(user => !user.isActive);
  }
  
  // 应用搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(user => 
      user.username.toLowerCase().includes(query) || 
      user.email.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// 计算属性：过滤订单
const filteredOrders = computed(() => {
  let result = [...orders.value];
  
  // 应用状态过滤
  if (activeOrderFilter.value !== 'all') {
    result = result.filter(order => order.status === activeOrderFilter.value);
  }
  
  // 应用搜索过滤
  if (orderSearchQuery.value) {
    const query = orderSearchQuery.value.toLowerCase();
    result = result.filter(order => 
      order.id.toLowerCase().includes(query) || 
      order.customerName.toLowerCase().includes(query) ||
      order.address.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// 获取过去N天的日期
const getLastNDays = (n: number) => {
  const date = new Date();
  date.setDate(date.getDate() - n);
  return `${date.getMonth() + 1}/${date.getDate()}`;
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'pending': '待接单',
    'accepted': '配送中',
    'completed': '已完成',
    'rejected': '已拒绝'
  };
  return statusMap[status] || status;
};

// 编辑用户
const editUser = (userId: string) => {
  console.log('编辑用户:', userId);
  // 这里可以打开编辑用户的对话框或页面
};

// 切换用户状态
const toggleUserStatus = async (userId: string) => {
  try {
    const userIndex = users.value.findIndex(u => u.id === userId);
    if (userIndex !== -1) {
      const newStatus = !users.value[userIndex].isActive;
      await axios.put(`/api/admin/users/${userId}/status`, { isActive: newStatus });
      users.value[userIndex].isActive = newStatus;
    }
  } catch (error) {
    console.error('切换用户状态失败:', error);
  }
};

// 查看订单详情
const viewOrderDetails = (orderId: string) => {
  console.log('查看订单详情:', orderId);
  // 这里可以打开查看订单详情的对话框或页面
};

// 编辑订单
const editOrder = (orderId: string) => {
  console.log('编辑订单:', orderId);
  // 这里可以打开编辑订单的对话框或页面
};

// 保存系统设置
const saveSettings = async () => {
  try {
    await axios.put('/api/admin/settings', settings.value);
    alert('设置已保存');
    originalSettings.value = { ...settings.value };
  } catch (error) {
    console.error('保存设置失败:', error);
  }
};

// 重置系统设置
const resetSettings = () => {
  settings.value = { ...originalSettings.value };
};

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await axios.get('/api/admin/stats');
    const stats = response.data;
    
    totalUsers.value = stats.totalUsers;
    deliveryUsersCount.value = stats.deliveryUsersCount;
    totalOrders.value = stats.totalOrders;
    todayOrders.value = stats.todayOrders;
  } catch (error) {
    // 静默处理错误，使用默认值
  }
};

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await axios.get('/api/admin/users');
    users.value = response.data;
  } catch (error) {
    // 静默处理错误，保持用户列表为空
  }
};

// 获取订单列表
const fetchOrders = async () => {
  try {
    const response = await axios.get('/api/admin/orders');
    orders.value = response.data;
  } catch (error) {
    // 静默处理错误，保持订单列表为空
  }
};

// 获取系统设置
const fetchSettings = async () => {
  try {
    const response = await axios.get('/api/admin/settings');
    settings.value = response.data;
    originalSettings.value = { ...response.data };
  } catch (error) {
    // 静默处理错误，使用默认值
    originalSettings.value = { ...settings.value };
  }
};

// 退出登录
const handleLogout = () => {
  authStore.logout();
};

onMounted(() => {
  // 不需要再初始化用户信息，由 authStore 管理
  
  // 获取数据
  fetchStats();
  fetchUsers();
  fetchOrders();
  fetchSettings();
});
</script>

<style scoped>
.admin-dashboard {
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
  margin-bottom: 30px;
}

h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 20px;
}

h3 {
  margin-top: 0;
  color: #555;
  font-size: 18px;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
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

/* 图表样式 */
.chart-container {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.chart-placeholder {
  margin-top: 15px;
  height: 250px;
  position: relative;
}

.mock-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 200px;
}

.chart-bar {
  width: 40px;
  background-color: #4a6cf7;
  border-radius: 4px 4px 0 0;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: #777;
  font-size: 12px;
}

/* 用户管理样式 */
.user-filters, .order-filters {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
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

.search-box {
  display: flex;
  margin-left: auto;
}

.search-box input {
  width: 200px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
}

.search-btn {
  padding: 8px 12px;
  background-color: #4a6cf7;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  font-weight: 500;
  color: #555;
  background-color: #f8f9fa;
}

tbody tr:hover {
  background-color: #f8f9fa;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #c8e6c9;
  color: #2e7d32;
}

.status-badge.inactive {
  background-color: #ffcdd2;
  color: #c62828;
}

.status-badge.pending {
  background-color: #ffe0b2;
  color: #e65100;
}

.status-badge.accepted {
  background-color: #b3e5fc;
  color: #0277bd;
}

.status-badge.completed {
  background-color: #c8e6c9;
  color: #2e7d32;
}

.status-badge.rejected {
  background-color: #ffcdd2;
  color: #c62828;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-buttons button {
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  border: none;
}

.btn-view {
  background-color: #f0f4ff;
  color: #4a6cf7;
}

.btn-edit {
  background-color: #4a6cf7;
  color: white;
}

.btn-status {
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  border: none;
}

.btn-enable {
  background-color: #c8e6c9;
  color: #2e7d32;
}

.btn-disable {
  background-color: #ffcdd2;
  color: #c62828;
}

/* 系统设置样式 */
.settings-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

input[type="text"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-switch label {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.toggle-switch label:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

.toggle-switch input:checked + label {
  background-color: #4a6cf7;
}

.toggle-switch input:checked + label:before {
  transform: translateX(26px);
}

.form-actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}

.btn-save {
  padding: 10px 20px;
  background-color: #4a6cf7;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.btn-reset {
  padding: 10px 20px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #555;
  font-size: 14px;
  cursor: pointer;
}
</style> 
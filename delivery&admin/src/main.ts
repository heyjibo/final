import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import axios from 'axios'
import './style.css'

// 配置 axios 默认值
axios.defaults.baseURL = import.meta.env.VITE_API_URL || '/api';
axios.defaults.headers.common['Content-Type'] = 'application/json';

// 在开发环境中使用模拟数据
if (import.meta.env.DEV) {
  // 创建模拟测试账号
  const mockUsers = [
    {
      id: '1',
      username: 'delivery1',
      email: 'delivery1@example.com',
      phone: '13800000001',
      userType: 'delivery',
      isActive: true,
      createdAt: '2023-01-15T08:30:00.000Z'
    },
    {
      id: '2',
      username: 'admin1',
      email: 'admin1@example.com',
      userType: 'admin',
      isActive: true,
      createdAt: '2023-01-10T10:15:00.000Z'
    },
    {
      id: '3',
      username: 'delivery2',
      email: 'delivery2@example.com',
      phone: '13800000002',
      userType: 'delivery',
      isActive: false,
      createdAt: '2023-02-20T14:45:00.000Z'
    }
  ];
  
  // 模拟订单数据
  const mockOrders = [
    {
      id: 'ORD001',
      customerName: '张三',
      address: '北京市海淀区中关村大街1号',
      status: 'pending',
      createdAt: '2023-05-15T09:30:00.000Z',
      deliveryPerson: null
    },
    {
      id: 'ORD002',
      customerName: '李四',
      address: '上海市浦东新区张江高科技园区',
      status: 'accepted',
      createdAt: '2023-05-15T10:15:00.000Z',
      deliveryPerson: 'delivery1'
    },
    {
      id: 'ORD003',
      customerName: '王五',
      address: '广州市天河区天河路385号',
      status: 'completed',
      createdAt: '2023-05-14T14:20:00.000Z',
      deliveryPerson: 'delivery1'
    },
    {
      id: 'ORD004',
      customerName: '赵六',
      address: '深圳市南山区科技园',
      status: 'rejected',
      createdAt: '2023-05-14T16:05:00.000Z',
      deliveryPerson: null
    }
  ];
  
  // 模拟统计数据
  const mockDeliveryStats = {
    pendingOrders: 5,
    completedOrders: 28, 
    todayEarnings: 320,
    deliveryScore: 4.8,
    totalOrders: 120,
    avgRating: 4.7,
    totalEarnings: 9600
  };
  
  const mockAdminStats = {
    totalUsers: 45,
    deliveryUsersCount: 30,
    totalOrders: 1250,
    todayOrders: 85
  };
  
  const mockSettings = {
    systemName: '外卖配送管理系统',
    supportPhone: '400-123-4567',
    autoAssignOrders: true,
    allowRegistration: true
  };
  
  // 拦截登录请求
  const originalPost = axios.post;
  // @ts-ignore
  axios.post = async function(url: string, data: any) {
    // 处理登录请求
    if (url.includes('/api/auth/') && url.includes('/login')) {
      const { username, password } = data;
      const type = url.includes('/delivery/') ? 'delivery' : 'admin';
      
      // 密码验证，123456是正确密码
      if (password !== '123456') {
        return Promise.reject({
          response: {
            status: 401,
            data: { message: '密码错误' }
          }
        });
      }
      
      // 查找用户
      const user = mockUsers.find(u => u.username === username && u.userType === type);
      
      if (!user) {
        return Promise.reject({
          response: {
            status: 401,
            data: { message: '用户不存在' }
          }
        });
      }
      
      return Promise.resolve({
        data: {
          token: `mock-jwt-token-${Date.now()}`,
          user
        }
      });
    }
    
    // 处理订单状态更新
    if (url.match(/\/api\/delivery\/orders\/.*\/(accept|complete|reject)/)) {
      const orderId = url.split('/')[4];
      const action = url.split('/').pop();
      
      const orderIndex = mockOrders.findIndex(o => o.id === orderId);
      if (orderIndex !== -1) {
        if (action === 'accept') {
          mockOrders[orderIndex].status = 'accepted';
          mockOrders[orderIndex].deliveryPerson = 'delivery1';
        } else if (action === 'complete') {
          mockOrders[orderIndex].status = 'completed';
        } else if (action === 'reject') {
          mockOrders[orderIndex].status = 'rejected';
        }
        
        return Promise.resolve({ data: { success: true } });
      }
    }
    
    // 处理用户状态更新
    if (url.match(/\/api\/admin\/users\/.*\/status/)) {
      const userId = url.split('/')[4];
      const { isActive } = data;
      
      const userIndex = mockUsers.findIndex(u => u.id === userId);
      if (userIndex !== -1) {
        mockUsers[userIndex].isActive = isActive;
        return Promise.resolve({ data: { success: true } });
      }
    }
    
    // 处理系统设置更新
    if (url === '/api/admin/settings') {
      Object.assign(mockSettings, data);
      return Promise.resolve({ data: mockSettings });
    }
    
    // 对于其他请求，使用原始方法
    return originalPost(url, data);
  };
  
  // 拦截GET请求
  const originalGet = axios.get;
  // @ts-ignore
  axios.get = async function(url: string) {
    // 处理配送员统计数据
    if (url === '/api/delivery/stats') {
      return Promise.resolve({ data: mockDeliveryStats });
    }
    
    // 处理配送员订单列表
    if (url === '/api/delivery/orders') {
      const deliveryOrders = mockOrders.filter(order => 
        order.status === 'pending' || 
        order.deliveryPerson === 'delivery1'
      );
      return Promise.resolve({ data: deliveryOrders });
    }
    
    // 处理管理员统计数据
    if (url === '/api/admin/stats') {
      return Promise.resolve({ data: mockAdminStats });
    }
    
    // 处理管理员用户列表
    if (url === '/api/admin/users') {
      return Promise.resolve({ data: mockUsers });
    }
    
    // 处理管理员订单列表
    if (url === '/api/admin/orders') {
      return Promise.resolve({ data: mockOrders });
    }
    
    // 处理系统设置
    if (url === '/api/admin/settings') {
      return Promise.resolve({ data: mockSettings });
    }
    
    // 对于其他请求，使用原始方法
    return originalGet(url);
  };
  
  // 拦截PUT请求
  const originalPut = axios.put;
  // @ts-ignore
  axios.put = async function(url: string, data: any) {
    // 处理系统设置更新
    if (url === '/api/admin/settings') {
      Object.assign(mockSettings, data);
      return Promise.resolve({ data: mockSettings });
    }
    
    // 处理用户状态更新
    if (url.match(/\/api\/admin\/users\/.*\/status/)) {
      const userId = url.split('/')[4];
      const { isActive } = data;
      
      const userIndex = mockUsers.findIndex(u => u.id === userId);
      if (userIndex !== -1) {
        mockUsers[userIndex].isActive = isActive;
        return Promise.resolve({ data: { success: true } });
      }
    }
    
    // 对于其他请求，使用原始方法
    return originalPut(url, data);
  };
}

// 请求拦截器，添加 token 到请求头
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器，处理未授权错误
axios.interceptors.response.use(
  response => response,
  error => {
    // 如果是API调用的错误
    if (error.response && error.response.status === 401) {
      // 清除本地存储的用户信息
      localStorage.removeItem('token');
      localStorage.removeItem('userType');
      localStorage.removeItem('user');
      
      // 重定向到登录页
      router.push({ name: 'Login' });
    }
    return Promise.reject(error);
  }
);

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(pinia)
app.mount('#app')

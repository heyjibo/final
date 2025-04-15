/**
 * API Mock服务
 * 此文件提供模拟API响应数据，用于前端开发测试
 * 在实际接入后端API时，请移除此文件的使用
 */

import type { User } from '../store/auth';
import type { LoginResponse, LoginCredentials, RegisterData } from './authService';
import type { Order, OrderStatus } from './deliveryService';

// 模拟延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// 模拟用户数据
const mockUsers: User[] = [
  {
    id: '1',
    username: 'delivery1',
    email: 'delivery1@example.com',
    phone: '13800138001',
    userType: 'delivery'
  },
  {
    id: '2',
    username: 'admin1',
    email: 'admin1@example.com',
    userType: 'admin'
  }
];

// 模拟订单数据
const mockOrders: Order[] = [
  {
    id: '1',
    orderNumber: 'ORD-2023-001',
    customerName: '张三',
    customerAddress: '北京市海淀区中关村大街1号',
    customerPhone: '13900139000',
    items: [
      { id: '1', name: '炸鸡', quantity: 2, price: 30 },
      { id: '2', name: '可乐', quantity: 1, price: 10 }
    ],
    totalAmount: 70,
    status: 'pending',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  },
  {
    id: '2',
    orderNumber: 'ORD-2023-002',
    customerName: '李四',
    customerAddress: '北京市朝阳区朝阳公园',
    customerPhone: '13800138000',
    items: [
      { id: '3', name: '汉堡', quantity: 1, price: 25 },
      { id: '4', name: '薯条', quantity: 1, price: 15 }
    ],
    totalAmount: 40,
    status: 'accepted',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

/**
 * Mock认证服务
 */
export const mockAuthService = {
  // 模拟登录
  login: async (credentials: LoginCredentials, type: 'delivery' | 'admin'): Promise<LoginResponse> => {
    // 模拟网络延迟
    await delay(1000);
    
    // 检查用户名和密码
    if (credentials.username === 'delivery1' && credentials.password === '123456' && type === 'delivery') {
      return {
        token: 'mock_delivery_token_123',
        user: mockUsers[0]
      };
    }
    
    if (credentials.username === 'admin1' && credentials.password === '123456' && type === 'admin') {
      return {
        token: 'mock_admin_token_123',
        user: mockUsers[1]
      };
    }
    
    // 登录失败
    throw new Error('用户名或密码错误');
  },
  
  // 模拟注册
  register: async (data: RegisterData, type: 'delivery' | 'admin'): Promise<{success: boolean, message: string}> => {
    // 模拟网络延迟
    await delay(1000);
    
    // 简单验证
    if (!data.username || !data.password || !data.email) {
      throw new Error('请填写所有必填字段');
    }
    
    // 检查用户名是否已存在
    if (mockUsers.some(user => user.username === data.username)) {
      throw new Error('用户名已被使用');
    }
    
    // 模拟成功注册
    return {
      success: true,
      message: '注册成功'
    };
  },
  
  // 模拟获取用户信息
  getUserInfo: async (): Promise<User> => {
    await delay(500);
    return mockUsers[0];
  },
  
  // 模拟登出
  logout: async (): Promise<{success: boolean}> => {
    await delay(500);
    return { success: true };
  }
};

/**
 * Mock配送服务
 */
export const mockDeliveryService = {
  // 获取待配送订单
  getPendingOrders: async (): Promise<Order[]> => {
    await delay(800);
    return mockOrders.filter(order => order.status === 'pending');
  },
  
  // 获取我的配送订单
  getMyOrders: async (): Promise<Order[]> => {
    await delay(800);
    return mockOrders.filter(order => order.status === 'accepted' || order.status === 'picked');
  },
  
  // 获取订单详情
  getOrderDetail: async (orderId: string): Promise<Order> => {
    await delay(500);
    const order = mockOrders.find(o => o.id === orderId);
    if (!order) {
      throw new Error('订单不存在');
    }
    return order;
  },
  
  // 接受订单
  acceptOrder: async (orderId: string): Promise<Order> => {
    await delay(1000);
    const order = mockOrders.find(o => o.id === orderId);
    if (!order) {
      throw new Error('订单不存在');
    }
    order.status = 'accepted';
    order.updatedAt = new Date().toISOString();
    return order;
  },
  
  // 标记为已取餐
  pickupOrder: async (orderId: string): Promise<Order> => {
    await delay(1000);
    const order = mockOrders.find(o => o.id === orderId);
    if (!order) {
      throw new Error('订单不存在');
    }
    order.status = 'picked';
    order.updatedAt = new Date().toISOString();
    return order;
  },
  
  // 标记为已送达
  deliverOrder: async (orderId: string): Promise<Order> => {
    await delay(1000);
    const order = mockOrders.find(o => o.id === orderId);
    if (!order) {
      throw new Error('订单不存在');
    }
    order.status = 'delivered';
    order.updatedAt = new Date().toISOString();
    return order;
  }
};

/**
 * 如何使用Mock服务
 * 
 * 在没有后端API的情况下，可以将mock服务导入并用于替代实际API服务：
 * 
 * 例如，在auth.ts中：
 * ```
 * // 使用真实API
 * import { authService } from '../services';
 * 
 * // 使用Mock API (开发测试时)
 * import { mockAuthService as authService } from '../services/mockService';
 * ```
 */

export default {
  auth: mockAuthService,
  delivery: mockDeliveryService
}; 
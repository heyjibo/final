import api from './api';
import type { Order } from './deliveryService';
import type { User } from '../store/auth';

export interface DeliveryUser extends User {
  totalDeliveries: number;
  rating: number;
  isActive: boolean;
}

export interface OrderStatistics {
  total: number;
  pending: number;
  inProgress: number;
  delivered: number;
  canceled: number;
  avgDeliveryTime: number;
}

export interface UserStatistics {
  totalUsers: number;
  deliveryUsers: number;
  adminUsers: number;
  newUsersToday: number;
}

/**
 * 管理员服务
 */
const adminService = {
  /**
   * 获取所有订单列表
   * @param page 页码
   * @param limit 每页数量
   * @param status 订单状态筛选
   * @returns 订单列表
   */
  getOrders: (page = 1, limit = 10, status?: string): Promise<{
    orders: Order[];
    total: number;
    page: number;
    limit: number;
  }> => {
    return api.get('/admin/orders', {
      params: { page, limit, status }
    });
  },

  /**
   * 获取订单详情
   * @param orderId 订单ID
   * @returns 订单详情
   */
  getOrderDetail: (orderId: string): Promise<Order> => {
    return api.get(`/admin/orders/${orderId}`);
  },

  /**
   * 更新订单状态
   * @param orderId 订单ID
   * @param status 订单状态
   * @returns 更新后的订单
   */
  updateOrderStatus: (orderId: string, status: string): Promise<Order> => {
    return api.put(`/admin/orders/${orderId}/status`, { status });
  },

  /**
   * 获取配送员列表
   * @param page 页码
   * @param limit 每页数量
   * @returns 配送员列表
   */
  getDeliveryUsers: (page = 1, limit = 10): Promise<{
    users: DeliveryUser[];
    total: number;
    page: number;
    limit: number;
  }> => {
    return api.get('/admin/users/delivery', {
      params: { page, limit }
    });
  },

  /**
   * 获取管理员列表
   * @param page 页码
   * @param limit 每页数量
   * @returns 管理员列表
   */
  getAdminUsers: (page = 1, limit = 10): Promise<{
    users: User[];
    total: number;
    page: number;
    limit: number;
  }> => {
    return api.get('/admin/users/admin', {
      params: { page, limit }
    });
  },

  /**
   * 更新用户状态
   * @param userId 用户ID
   * @param isActive 是否启用
   * @returns 更新结果
   */
  updateUserStatus: (userId: string, isActive: boolean): Promise<any> => {
    return api.put(`/admin/users/${userId}/status`, { isActive });
  },

  /**
   * 获取订单统计信息
   * @returns 订单统计信息
   */
  getOrderStatistics: (): Promise<OrderStatistics> => {
    return api.get('/admin/statistics/orders');
  },

  /**
   * 获取用户统计信息
   * @returns 用户统计信息
   */
  getUserStatistics: (): Promise<UserStatistics> => {
    return api.get('/admin/statistics/users');
  },

  /**
   * 获取收入统计信息
   * @param period 时间段 (day, week, month, year)
   * @returns 收入统计信息
   */
  getRevenueStatistics: (period: 'day' | 'week' | 'month' | 'year'): Promise<{
    labels: string[];
    data: number[];
    total: number;
  }> => {
    return api.get('/admin/statistics/revenue', {
      params: { period }
    });
  },

  /**
   * 导出订单数据 CSV
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @returns CSV数据
   */
  exportOrdersCSV: (startDate?: string, endDate?: string): Promise<Blob> => {
    return api.get('/admin/export/orders', {
      params: { startDate, endDate },
      responseType: 'blob'
    });
  }
};

export default adminService; 
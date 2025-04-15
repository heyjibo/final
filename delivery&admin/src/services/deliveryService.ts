import api from './api';

export interface Order {
  id: string;
  orderNumber: string;
  customerName: string;
  customerAddress: string;
  customerPhone: string;
  items: OrderItem[];
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  id: string;
  name: string;
  quantity: number;
  price: number;
}

export type OrderStatus = 'pending' | 'accepted' | 'picked' | 'delivered' | 'canceled';

/**
 * 配送服务
 */
const deliveryService = {
  /**
   * 获取待配送订单列表
   * @returns 订单列表
   */
  getPendingOrders: (): Promise<Order[]> => {
    return api.get('/delivery/orders/pending');
  },

  /**
   * 获取我的配送订单列表
   * @returns 订单列表
   */
  getMyOrders: (): Promise<Order[]> => {
    return api.get('/delivery/orders/my');
  },

  /**
   * 获取订单详情
   * @param orderId 订单ID
   * @returns 订单详情
   */
  getOrderDetail: (orderId: string): Promise<Order> => {
    return api.get(`/delivery/orders/${orderId}`);
  },

  /**
   * 接受订单
   * @param orderId 订单ID
   * @returns 更新后的订单
   */
  acceptOrder: (orderId: string): Promise<Order> => {
    return api.put(`/delivery/orders/${orderId}/accept`);
  },

  /**
   * 标记为已取餐
   * @param orderId 订单ID
   * @returns 更新后的订单
   */
  pickupOrder: (orderId: string): Promise<Order> => {
    return api.put(`/delivery/orders/${orderId}/pickup`);
  },

  /**
   * 标记为已送达
   * @param orderId 订单ID
   * @returns 更新后的订单
   */
  deliverOrder: (orderId: string): Promise<Order> => {
    return api.put(`/delivery/orders/${orderId}/deliver`);
  },

  /**
   * 获取配送统计信息
   * @returns 统计信息
   */
  getDeliveryStats: (): Promise<{
    totalDeliveries: number;
    completedToday: number;
    averageDeliveryTime: number;
    earnings: {
      today: number;
      thisWeek: number;
      thisMonth: number;
    }
  }> => {
    return api.get('/delivery/stats');
  },

  /**
   * 更新个人信息
   * @param data 个人信息
   * @returns 更新结果
   */
  updateProfile: (data: {
    phone?: string;
    email?: string;
    avatar?: string;
  }): Promise<any> => {
    return api.put('/delivery/profile', data);
  }
};

export default deliveryService; 
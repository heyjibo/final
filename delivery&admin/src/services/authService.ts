import api from './api';
import type { User } from '../store/auth';

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface RegisterData {
  username: string;
  password: string;
  email: string;
  phone?: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

/**
 * 认证服务
 */
const authService = {
  /**
   * 配送人员登录
   * @param credentials 登录凭证
   * @returns 登录响应
   */
  loginDelivery: (credentials: LoginCredentials): Promise<LoginResponse> => {
    return api.post('/auth/delivery/login', credentials);
  },

  /**
   * 管理员登录
   * @param credentials 登录凭证
   * @returns 登录响应
   */
  loginAdmin: (credentials: LoginCredentials): Promise<LoginResponse> => {
    return api.post('/auth/admin/login', credentials);
  },

  /**
   * 配送人员注册
   * @param data 注册数据
   * @returns 注册响应
   */
  registerDelivery: (data: RegisterData): Promise<any> => {
    return api.post('/auth/delivery/register', data);
  },

  /**
   * 管理员注册
   * @param data 注册数据
   * @returns 注册响应
   */
  registerAdmin: (data: RegisterData): Promise<any> => {
    return api.post('/auth/admin/register', data);
  },

  /**
   * 获取当前用户信息
   * @returns 用户信息
   */
  getUserInfo: (): Promise<User> => {
    return api.get('/auth/user');
  },

  /**
   * 登出
   * @returns 登出响应
   */
  logout: (): Promise<any> => {
    return api.post('/auth/logout');
  },

  /**
   * 根据用户类型登录
   * @param credentials 登录凭证
   * @param type 用户类型
   * @returns 登录响应
   */
  login: (credentials: LoginCredentials, type: 'delivery' | 'admin'): Promise<LoginResponse> => {
    return type === 'delivery' 
      ? authService.loginDelivery(credentials) 
      : authService.loginAdmin(credentials);
  },

  /**
   * 根据用户类型注册
   * @param data 注册数据
   * @param type 用户类型
   * @returns 注册响应
   */
  register: (data: RegisterData, type: 'delivery' | 'admin'): Promise<any> => {
    return type === 'delivery' 
      ? authService.registerDelivery(data) 
      : authService.registerAdmin(data);
  }
};

export default authService; 
import axios from 'axios';
import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 从环境变量获取baseURL，默认为'/api'
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  }
});

// 请求拦截器
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    
    // 如果有token，添加到请求头
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data;
  },
  (error) => {
    if (error.response) {
      // 处理错误响应
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并重定向到登录页
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          localStorage.removeItem('userType');
          window.location.href = '/login';
          break;
        case 403:
          // 禁止访问
          console.error('Access forbidden');
          break;
        case 404:
          // 资源未找到
          console.error('Resource not found');
          break;
        case 500:
          // 服务器错误
          console.error('Server error');
          break;
        default:
          console.error(`Error with status code: ${error.response.status}`);
      }
      
      return Promise.reject(error.response.data);
    }
    
    // 网络错误、请求超时等其他错误
    console.error('Network error:', error.message);
    return Promise.reject({
      message: '网络错误，请检查您的网络连接'
    });
  }
);

export default api; 
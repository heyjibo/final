import api from './api';
import authService from './authService';
import deliveryService from './deliveryService';
import adminService from './adminService';

export {
  api,
  authService,
  deliveryService,
  adminService
};

// 默认导出所有服务
export default {
  api,
  auth: authService,
  delivery: deliveryService,
  admin: adminService
}; 
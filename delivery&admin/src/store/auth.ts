import { defineStore } from 'pinia';
import { ref } from 'vue';
import router from '../router';
import { authService } from '../services';
import type { LoginCredentials, RegisterData } from '../services/authService';

export interface User {
  id: string;
  username: string;
  email: string;
  phone?: string;
  userType: 'delivery' | 'admin';
}

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref<User | null>(null);
  const token = ref<string | null>(null);
  const userType = ref<string | null>(null);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  // 初始化状态
  const initialize = () => {
    const storedToken = localStorage.getItem('token');
    const storedUserType = localStorage.getItem('userType');
    const storedUser = localStorage.getItem('user');

    if (storedToken) {
      token.value = storedToken;
    }

    if (storedUserType) {
      userType.value = storedUserType;
    }

    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser);
      } catch (e) {
        // 解析失败，清除存储
        localStorage.removeItem('user');
      }
    }
  };

  // 登录
  const login = async (username: string, password: string, type: 'delivery' | 'admin') => {
    isLoading.value = true;
    error.value = null;

    try {
      const credentials: LoginCredentials = { username, password };
      const response = await authService.login(credentials, type);
      
      // 存储 token 和用户信息
      token.value = response.token;
      userType.value = type;
      user.value = response.user;
      
      localStorage.setItem('token', response.token);
      localStorage.setItem('userType', type);
      localStorage.setItem('user', JSON.stringify(response.user));
      
      // 登录成功后跳转
      if (type === 'delivery') {
        router.push({ name: 'DeliveryDashboard' });
      } else {
        router.push({ name: 'AdminDashboard' });
      }
      
      return true;
    } catch (e: any) {
      console.error('登录失败:', e);
      error.value = e.message || '登录失败，请检查用户名和密码';
      return false;
    } finally {
      isLoading.value = false;
    }
  };

  // 注册
  const register = async (
    username: string, 
    password: string, 
    email: string, 
    type: 'delivery' | 'admin', 
    phone?: string
  ) => {
    isLoading.value = true;
    error.value = null;

    try {
      const userData: RegisterData = {
        username,
        password,
        email,
        phone
      };
      
      await authService.register(userData, type);
      
      // 注册成功后跳转到登录页
      router.push({ name: 'Login' });
      
      return true;
    } catch (e: any) {
      console.error('注册失败:', e);
      error.value = e.message || '注册失败，请稍后再试';
      return false;
    } finally {
      isLoading.value = false;
    }
  };

  // 获取用户信息
  const fetchUserInfo = async () => {
    if (!token.value) return false;
    
    isLoading.value = true;
    
    try {
      const userData = await authService.getUserInfo();
      user.value = userData;
      localStorage.setItem('user', JSON.stringify(userData));
      return true;
    } catch (e) {
      console.error('获取用户信息失败:', e);
      return false;
    } finally {
      isLoading.value = false;
    }
  };

  // 退出登录
  const logout = async () => {
    isLoading.value = true;
    
    try {
      // 调用登出API
      await authService.logout();
    } catch (e) {
      console.error('登出API调用失败:', e);
    } finally {
      // 无论API调用成功与否，都清除本地存储
      token.value = null;
      userType.value = null;
      user.value = null;
      
      localStorage.removeItem('token');
      localStorage.removeItem('userType');
      localStorage.removeItem('user');
      
      isLoading.value = false;
      
      // 跳转到登录页
      router.push({ name: 'Login' });
    }
  };

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!token.value;
  };

  // 获取当前用户类型
  const getUserType = () => {
    return userType.value;
  };

  // 初始化
  initialize();

  return {
    user,
    token,
    userType,
    isLoading,
    error,
    login,
    register,
    logout,
    isLoggedIn,
    getUserType,
    fetchUserInfo
  };
}); 
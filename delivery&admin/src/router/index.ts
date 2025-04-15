import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

// Auth views
import Login from '../views/auth/Login.vue';
import Register from '../views/auth/Register.vue';

// Delivery views
import DeliveryDashboard from '../views/delivery/Dashboard.vue';

// Admin views
import AdminDashboard from '../views/admin/Dashboard.vue';

// Routes
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/delivery',
    name: 'DeliveryDashboard',
    component: DeliveryDashboard,
    meta: { requiresAuth: true, userType: 'delivery' }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, userType: 'admin' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guards
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token') !== null;
  const userType = localStorage.getItem('userType');
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 如果路由需要验证
    if (!isAuthenticated) {
      next({ name: 'Login' });
    } else {
      // 验证用户类型
      if (to.meta.userType && to.meta.userType !== userType) {
        // 如果用户类型不匹配
        if (userType === 'delivery') {
          next({ name: 'DeliveryDashboard' });
        } else if (userType === 'admin') {
          next({ name: 'AdminDashboard' });
        } else {
          next({ name: 'Login' });
        }
      } else {
        // 用户已验证且类型匹配
        next();
      }
    }
  } else {
    // 路由不需要验证
    if (isAuthenticated && (to.name === 'Login' || to.name === 'Register')) {
      // 如果用户已登录，尝试访问登录或注册页面，重定向到相应的仪表板
      if (userType === 'delivery') {
        next({ name: 'DeliveryDashboard' });
      } else if (userType === 'admin') {
        next({ name: 'AdminDashboard' });
      } else {
        next();
      }
    } else {
      next();
    }
  }
});

export default router; 
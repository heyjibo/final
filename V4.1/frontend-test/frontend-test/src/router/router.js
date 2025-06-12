import Vue from 'vue'
import VueRouter from 'vue-router'
import LoginPage from '../components/LoginPage.vue'
import RegisterPage from '../components/RegisterPage.vue'
import RiderRegister from '../components/RiderRegister.vue'
import AdminRegister from '../components/AdminRegister.vue'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginPage
  },
  {
    path: '/register',
    name: 'RegisterType',
    component: RegisterPage
  },
  {
    path: '/register/rider',
    name: 'RiderRegister',
    component: RiderRegister
  },
  {
    path: '/register/admin',
    name: 'AdminRegister',
    component: AdminRegister
  },
  {
    path: '*',
    redirect: '/login'
  },
  {
    path: '/rider/orders',
    name: 'RiderOrders',
    component: () => import('../components/RiderOrders.vue'), // 确保路径正确
    meta: {
      requiresAuth: true,
      requiresRider: true
    }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('../components/AdminOrders.vue'), // 确保组件路径正确
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/rider/profile',
    name: 'RiderProfile',
    component: () => import('../components/RiderProfile.vue'),
    meta: {
      requiresAuth: true,
      requiresRider: true
    }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/components/AdminDashboard.vue'),
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/rider/ranking',
    name: 'RiderRanking',
    component: () => import('../components/RiderRanking.vue'),
    meta: {
      requiresAuth: true,
      requiresRider: true
    }
  },
  {
    path: '/admin/riders',
    name: 'AdminRiders',
    component: () => import('../components/AdminRidersOverview.vue'),
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/register/merchant',
    name: 'MerchantRegister',
    component: () => import('../components/MerchantRegister.vue')
  },
  {
    path: '/merchant/dashboard',
    name: 'MerchantDashboard',
    component: () => import('../components/MerchantDashboard.vue'),
    meta: { requiresAuth: true, requiresMerchant: true }
  },
  {
    path: '/merchant/dishes',
    name: 'DishManagement',
    component: () => import('../components/DishManagement.vue'),
    meta: {
      requiresAuth: true,
      requiresMerchant: true
    }
  },
  {
    path: '/dish/add',
    name: 'DishAdd',
    component: () => import('../components/DishAddForm.vue'),
    meta: { requiresAuth: true, requiresMerchant: true }
  },
  {
    path: '/dish/edit/:id',
    name: 'DishEdit',
    component: () => import('../components/DishEditForm.vue'),
    meta: { requiresAuth: true, requiresMerchant: true }
  },
  {
    path: '/customer/profile',
    name: 'CustomerProfile',
    component: () => import('../components/CustomerProfile.vue'),
    meta: {
      requiresAuth: true,
      requiresCustomer: true
    }
  },
  {
    path: '/register/customer',
    name: 'CustomerRegister',
    component: () => import('../components/CustomerRegister.vue')
  },
  {
    path: '/customer/order',
    name: 'CustomerOrder',
    component: () => import('../components/CustomerOrder.vue'),
    meta: {
      requiresAuth: true,
      requiresCustomer: true
    }
  },
  {
    path: '/customer/cart',
    name: 'CustomerCart',
    component: () => import('../components/CustomerCart.vue'),
    meta: {
      requiresAuth: true,
      requiresCustomer: true
    }
  },
  {
    path: '/merchant/orders',
    name: 'MerchantOrders',
    component: () => import('../components/MerchantOrderManagement.vue'),
    meta: {
      requiresAuth: true,
      requiresMerchant: true
    }
  },
  {
    path: '/merchant/:id',
    name: 'MerchantMenu',
    component: () => import('../components/MerchantMenu.vue'),
    meta: {
      requiresAuth: true,
      requiresCustomer: true
    }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userType = localStorage.getItem('userType')

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next('/login')
    } else {
      // 检查角色权限
      if (to.meta.requiresRider && userType !== 'rider') {
        localStorage.removeItem('token')
        localStorage.removeItem('userType')
        next('/login?error=invalid_role')
        return
      }

      if (to.meta.requiresAdmin && userType !== 'admin') {
        localStorage.removeItem('token')
        localStorage.removeItem('userType')
        next('/login?error=invalid_role')
        return
      }

      next()
    }
  } else {
    next()
  }
})

export default router

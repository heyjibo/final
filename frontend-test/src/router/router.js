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

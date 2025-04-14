import Vue from 'vue'
import VueRouter from 'vue-router'
import { getUserInfo } from '../api/auth'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { title: 'Login' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { title: 'Register' }
  },
  {
    path: '/rider',
    component: () => import('../views/rider/Layout.vue'),
    meta: { requiresAuth: true, userType: 'rider' },
    children: [
      {
        path: 'dashboard',
        name: 'RiderDashboard',
        component: () => import('../views/rider/Dashboard.vue'),
        meta: { title: 'Rider Center' }
      }
    ]
  },
  {
    path: '*',
    redirect: '/login'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes
})

// Global router guard
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '校园外卖配送系统'
  
  // 检查是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 获取本地存储的token
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    
    // 如果没有token，重定向到登录页
    if (!currentUser.token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 如果有token但没有用户信息，尝试获取用户信息
    if (!currentUser.username) {
      getUserInfo().then(response => {
        if (response.code === 200) {
          // 更新用户信息
          localStorage.setItem('currentUser', JSON.stringify({
            ...currentUser,
            ...response.data
          }))
          next()
        } else {
          // 获取用户信息失败，可能是token已过期
          localStorage.removeItem('currentUser')
          next({
            path: '/login',
            query: { redirect: to.fullPath }
          })
        }
      }).catch(() => {
        // 请求出错，清除token
        localStorage.removeItem('currentUser')
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
      })
    } else {
      // 有用户信息，检查用户类型是否匹配
      const userType = currentUser.userType
      const requiredType = to.meta.userType
      
      if (requiredType && userType !== requiredType) {
        // 用户类型不匹配，重定向到对应的首页
        next({
          path: userType === 'admin' ? '/admin/dashboard' : '/rider/dashboard'
        })
        return
      }
      
      next()
    }
  } else {
    // 不需要登录权限的页面直接通过
    next()
  }
})

export default router 
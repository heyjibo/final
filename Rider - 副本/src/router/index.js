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
  // Set page title
  document.title = to.meta.title || 'Campus Food Delivery System'
  
  // Check if authentication is required
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // Get token from local storage
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    
    // If no token, redirect to login page
    if (!currentUser.token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // If has token but no user info, try to get user info
    if (!currentUser.username) {
      getUserInfo().then(response => {
        if (response.code === 200) {
          // Update user information
          localStorage.setItem('currentUser', JSON.stringify({
            ...currentUser,
            ...response.data
          }))
          next()
        } else {
          // Failed to get user info, token may be expired
          localStorage.removeItem('currentUser')
          next({
            path: '/login',
            query: { redirect: to.fullPath }
          })
        }
      }).catch(() => {
        // Request error, clear token
        localStorage.removeItem('currentUser')
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
      })
    } else {
      // Has user info, check if user type matches
      const userType = currentUser.userType
      const requiredType = to.meta.userType
      
      if (requiredType && userType !== requiredType) {
        // User type doesn't match, redirect to corresponding homepage
        next({
          path: userType === 'admin' ? '/admin/dashboard' : '/rider/dashboard'
        })
        return
      }
      
      next()
    }
  } else {
    // Pages that don't require authentication pass directly
    next()
  }
})

export default router 
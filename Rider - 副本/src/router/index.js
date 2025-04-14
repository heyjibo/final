import Vue from 'vue'
import VueRouter from 'vue-router'

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
  base: import.meta.env.BASE_URL || '/',
  routes
})

// Global router guard
router.beforeEach((to, from, next) => {
  // Check if authentication is required
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // Get current user info
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    
    // If not logged in, redirect to login page
    if (!currentUser.token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  next()
})

export default router 
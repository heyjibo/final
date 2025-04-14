import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

// Create axios instance
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api', 
  timeout: 10000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    // Add token to request headers
    const token = localStorage.getItem('currentUser') ? JSON.parse(localStorage.getItem('currentUser')).token : null
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // If the status code is not 200, there is a problem with the API
    if (res.code !== 200) {
      Message({
        message: res.message || 'Request failed',
        type: 'error',
        duration: 5 * 1000
      })
      
      // If 401 or 403, token is invalid or insufficient permissions, need to login again
      if (res.code === 401 || res.code === 403) {
        // Redirect to login
        localStorage.removeItem('currentUser')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || 'Request failed'))
    } else {
      return res
    }
  },
  error => {
    console.error('Request error', error)
    
    // Handle HTTP error status codes
    let message = 'Network error, please try again later'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = 'Bad request'
          break
        case 401:
          message = 'Unauthorized, please login again'
          // Clear token and redirect to login page
          localStorage.removeItem('currentUser')
          router.push('/login')
          break
        case 403:
          message = 'Access denied'
          break
        case 404:
          message = 'Resource not found'
          break
        case 408:
          message = 'Request timeout'
          break
        case 500:
          message = 'Internal server error'
          break
        case 501:
          message = 'Service not implemented'
          break
        case 502:
          message = 'Gateway error'
          break
        case 503:
          message = 'Service unavailable'
          break
        case 504:
          message = 'Gateway timeout'
          break
        case 505:
          message = 'HTTP version not supported'
          break
        default:
          message = `Unknown error: ${error.response.status}`
      }
    }
    
    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(error)
  }
)

export default service 
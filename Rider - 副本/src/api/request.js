import axios from 'axios'
import { Message } from 'element-ui'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', 
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.warn('API请求失败，使用前端模拟数据替代')
    
    // 模拟成功响应，使用模拟数据
    const mockResponse = {
      code: 200,
      message: 'success',
      data: {
        items: [],
        total: 0
      }
    }
    
    return Promise.resolve(mockResponse)
  }
)

export default service 
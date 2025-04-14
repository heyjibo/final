import request from './request'
import { riderStatistics, pendingOrders } from './mockData'

// Get rider statistics
export function getRiderStatistics() {
  // Mock API request, returns local mock data
  return Promise.resolve({
    code: 200,
    data: riderStatistics
  })
}

// Get pending orders
export function getPendingOrders() {
  // Mock API request, returns local mock data
  return Promise.resolve({
    code: 200,
    data: {
      items: pendingOrders,
      total: pendingOrders.length
    }
  })
}

// Accept order operation
export function takeOrder(orderId) {
  // Mock order acceptance
  return Promise.resolve({
    code: 200,
    message: 'Order accepted successfully',
    data: null
  })
}

// Get rider info
export function getRiderInfo() {
  return Promise.resolve({
    code: 200,
    data: {
      id: 'R001',
      name: 'John Doe',
      phone: '13800138000',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      status: 'Online',
      level: 'Gold Rider'
    }
  })
} 
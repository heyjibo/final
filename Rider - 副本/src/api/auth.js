import request from './request'

// Default admin and rider accounts
const defaultAccounts = [
  {
    id: 'admin1',
    username: 'admin',
    password: 'admin123',
    userType: 'admin',
    phone: '13800138000'
  },
  {
    id: 'rider1',
    username: 'rider',
    password: 'rider123',
    userType: 'rider',
    phone: '13900139000'
  }
]

// Ensure default accounts are added to local storage
function ensureDefaultAccounts() {
  const localUsers = JSON.parse(localStorage.getItem('users') || '[]')
  
  // Check if default accounts already exist
  defaultAccounts.forEach(account => {
    const existingAccount = localUsers.find(user => 
      user.username === account.username && user.userType === account.userType
    )
    
    if (!existingAccount) {
      localUsers.push({
        ...account,
        createTime: new Date().toISOString()
      })
    }
  })
  
  localStorage.setItem('users', JSON.stringify(localUsers))
}

// Initialize to ensure default accounts exist
ensureDefaultAccounts()

/**
 * User login
 * @param {Object} data - Login information
 * @param {string} data.username - Username
 * @param {string} data.password - Password
 * @param {string} data.userType - User type (rider/admin)
 * @returns {Promise} Login result
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * User registration
 * @param {Object} data - Registration information
 * @param {string} data.username - Username
 * @param {string} data.password - Password
 * @param {string} data.phone - Phone number
 * @param {string} data.userType - User type (rider/admin)
 * @param {string} data.adminCode - Admin invitation code (only required for admin registration)
 * @returns {Promise} Registration result
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * Get current user information
 * @returns {Promise} User information
 */
export function getUserInfo() {
  return request({
    url: '/auth/getUserInfo',
    method: 'get'
  })
}

/**
 * Logout
 * @returns {Promise} Logout result
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
} 
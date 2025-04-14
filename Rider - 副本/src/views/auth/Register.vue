<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-title">
        <h2>Campus Food Delivery System</h2>
        <p>Account Registration</p>
      </div>
      
      <el-form :model="registerForm" :rules="registerRules" ref="registerForm" label-width="0px">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            prefix-icon="el-icon-user" 
            placeholder="Username">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            prefix-icon="el-icon-lock" 
            placeholder="Password" 
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            prefix-icon="el-icon-lock" 
            placeholder="Confirm Password" 
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            prefix-icon="el-icon-mobile-phone" 
            placeholder="Phone Number">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="userType" class="user-type-selector">
          <el-radio-group v-model="registerForm.userType">
            <el-radio label="rider">Rider</el-radio>
            <el-radio label="admin">Admin</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="registerForm.userType === 'admin'" prop="adminCode">
          <el-input 
            v-model="registerForm.adminCode" 
            prefix-icon="el-icon-key" 
            placeholder="Admin Invitation Code">
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" class="register-button">
            Register
          </el-button>
        </el-form-item>
        
        <div class="register-options">
          <span>Already have an account?</span>
          <span class="login-link" @click="goToLogin">Back to Login</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth'

export default {
  name: 'Register',
  data() {
    // Custom validation rule - Confirm Password
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('Passwords do not match'))
      } else {
        callback()
      }
    }
    
    // Custom validation rule - Phone Number
    const validatePhone = (rule, value, callback) => {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(value)) {
        callback(new Error('Please enter a valid phone number'))
      } else {
        callback()
      }
    }
    
    // Custom validation rule - Admin Invitation Code
    const validateAdminCode = (rule, value, callback) => {
      if (this.registerForm.userType === 'admin' && !value) {
        callback(new Error('Please enter admin invitation code'))
      } else {
        callback()
      }
    }
    
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        userType: 'rider', // Default as rider
        adminCode: ''
      },
      registerRules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur' },
          { min: 3, max: 20, message: 'Length should be 3 to 20 characters', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' },
          { min: 6, max: 20, message: 'Length should be 6 to 20 characters', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: 'Please confirm password', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { required: true, message: 'Please enter phone number', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        userType: [
          { required: true, message: 'Please select user type', trigger: 'change' }
        ],
        adminCode: [
          { validator: validateAdminCode, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  watch: {
    'registerForm.userType': function(val) {
      // Validate admin code when user type changes to admin
      if (val === 'admin') {
        this.$refs.registerForm && this.$refs.registerForm.validateField('adminCode')
      }
    }
  },
  methods: {
    // Register handler
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          // Prepare registration data
          const registerData = {
            username: this.registerForm.username,
            password: this.registerForm.password,
            phone: this.registerForm.phone,
            userType: this.registerForm.userType
          }
          
          // Add admin code if user type is admin
          if (this.registerForm.userType === 'admin') {
            registerData.adminCode = this.registerForm.adminCode
          }
          
          // Call registration API
          register(registerData).then(response => {
            if (response.code === 200) {
              // Registration successful
              this.$message.success('Registration successful, please login')
              this.$router.push('/login')
            } else {
              // Registration failed
              this.$message.error(response.message || 'Registration failed')
            }
            this.loading = false
          }).catch(error => {
            console.error('Registration failed:', error)
            this.$message.warning('Backend API connection failed, using local registration mode')
            this.handleRegisterFallback(registerData)
          })
        } else {
          return false
        }
      })
    },
    
    // Local registration fallback
    handleRegisterFallback(registerData) {
      // Get registered users from local storage
      const localUsers = JSON.parse(localStorage.getItem('users') || '[]')
      
      // Check if username already exists
      const existingUser = localUsers.find(u => u.username === registerData.username)
      if (existingUser) {
        this.$message.error('Username already exists')
        this.loading = false
        return
      }
      
      // Validate admin invitation code
      if (registerData.userType === 'admin' && registerData.adminCode !== 'admin123') {
        this.$message.error('Invalid admin invitation code')
        this.loading = false
        return
      }
      
      // Create new user
      const newUser = {
        id: `user-${Date.now()}`,
        username: registerData.username,
        password: registerData.password,
        phone: registerData.phone,
        userType: registerData.userType,
        createTime: new Date().toISOString()
      }
      
      // Save to local storage
      localUsers.push(newUser)
      localStorage.setItem('users', JSON.stringify(localUsers))
      
      this.$message.success('Local registration successful, please login')
      this.$router.push('/login')
      
      this.loading = false
    },
    
    // Go to login page
    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
  background-repeat: no-repeat;
  background-position: center 110px;
  background-size: 100%;
}

.register-box {
  width: 400px;
  padding: 30px 35px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
}

.register-title h2 {
  font-weight: 600;
  font-size: 24px;
  color: #303133;
  margin: 0 0 10px;
}

.register-title p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.register-button {
  width: 100%;
}

.register-options {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.login-link {
  color: #409EFF;
  cursor: pointer;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

.user-type-selector {
  text-align: center;
  margin-bottom: 20px;
}
</style> 
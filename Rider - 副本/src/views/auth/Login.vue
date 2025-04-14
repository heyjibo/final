<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>Campus Food Delivery System</h2>
        <p>Welcome to Login</p>
      </div>
      
      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" label-width="0px">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            prefix-icon="el-icon-user" 
            placeholder="Username"
            @keyup.enter.native="handleLogin">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            prefix-icon="el-icon-lock" 
            placeholder="Password" 
            show-password
            @keyup.enter.native="handleLogin">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="userType" class="user-type-selector">
          <el-radio-group v-model="loginForm.userType">
            <el-radio label="rider">Rider</el-radio>
            <el-radio label="admin">Admin</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-button">
            Login
          </el-button>
        </el-form-item>
        
        <div class="login-options">
          <el-checkbox v-model="rememberMe">Remember Me</el-checkbox>
          <span class="register-link" @click="goToRegister">Don't have an account? Register</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        userType: 'rider' // Default as rider
      },
      loginRules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur' },
          { min: 3, max: 20, message: 'Length should be 3 to 20 characters', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' },
          { min: 6, max: 20, message: 'Length should be 6 to 20 characters', trigger: 'blur' }
        ],
        userType: [
          { required: true, message: 'Please select user type', trigger: 'change' }
        ]
      },
      loading: false,
      rememberMe: false,
      // Default accounts
      defaultAccounts: {
        admin: {
          username: 'admin',
          password: 'admin123'
        },
        rider: {
          username: 'rider',
          password: 'rider123'
        }
      }
    }
  },
  methods: {
    // Handle API login error fallback - use mock data when backend API is unavailable
    handleLoginFallback() {
      const accounts = [
        {
          id: 'admin1',
          username: 'admin',
          password: 'admin123',
          userType: 'admin'
        },
        {
          id: 'rider1',
          username: 'rider',
          password: 'rider123',
          userType: 'rider'
        }
      ];
      
      const user = accounts.find(u => 
        u.username === this.loginForm.username && 
        u.password === this.loginForm.password &&
        u.userType === this.loginForm.userType
      );
      
      if (user) {
        // Login success
        const userData = {
          id: user.id,
          username: user.username,
          userType: user.userType,
          token: `mock-token-${Date.now()}`
        };
        
        // Save to local storage
        localStorage.setItem('currentUser', JSON.stringify(userData));
        
        // Success message
        this.$message.success('Login successful (local mode)');
        
        // Redirect to appropriate page
        if (this.loginForm.userType === 'admin') {
          this.$router.push('/admin/dashboard');
        } else {
          this.$router.push('/rider/dashboard');
        }
      } else {
        this.$message.error('Invalid username or password');
      }
      
      this.loading = false;
    },
    
    // Login handler
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          // Call login API
          login(this.loginForm).then(response => {
            if (response.code === 200) {
              // Login successful
              const userData = {
                id: response.data.id,
                username: response.data.username,
                userType: response.data.userType,
                token: response.data.token
              }
              
              // Save to local storage
              localStorage.setItem('currentUser', JSON.stringify(userData))
              
              // If remember me is checked, save login info
              if (this.rememberMe) {
                const savedInfo = {
                  username: this.loginForm.username,
                  userType: this.loginForm.userType,
                  rememberMe: true
                }
                localStorage.setItem('savedLoginInfo', JSON.stringify(savedInfo))
              } else {
                localStorage.removeItem('savedLoginInfo')
              }
              
              // Success message
              this.$message.success('Login successful')
              
              // Redirect based on user type
              if (this.loginForm.userType === 'admin') {
                this.$router.push('/admin/dashboard')
              } else {
                this.$router.push('/rider/dashboard')
              }
            } else {
              // Login failed
              this.$message.error(response.message || 'Login failed')
            }
            this.loading = false
          }).catch(error => {
            console.error('Login failed:', error)
            this.$message.warning('Backend API connection failed, using local login mode')
            // Use fallback method
            this.handleLoginFallback()
          })
        } else {
          return false
        }
      })
    },
    
    // Go to register page
    goToRegister() {
      this.$router.push('/register')
    }
  },
  mounted() {
    // Check if there's saved login info
    const savedUser = localStorage.getItem('savedLoginInfo')
    if (savedUser) {
      const { username, userType, rememberMe } = JSON.parse(savedUser)
      this.loginForm.username = username
      this.loginForm.userType = userType
      this.rememberMe = rememberMe
    }
    
    // If already logged in, redirect to appropriate page
    const currentUser = localStorage.getItem('currentUser')
    if (currentUser) {
      const { userType } = JSON.parse(currentUser)
      if (userType === 'admin') {
        this.$router.push('/admin/dashboard')
      } else {
        this.$router.push('/rider/dashboard')
      }
    }
  }
}
</script>

<style scoped>
.login-container {
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

.login-box {
  width: 400px;
  padding: 30px 35px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
}

.login-title h2 {
  font-weight: 600;
  font-size: 24px;
  color: #303133;
  margin: 0 0 10px;
}

.login-title p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-button {
  width: 100%;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.register-link {
  color: #409EFF;
  cursor: pointer;
}

.register-link:hover {
  text-decoration: underline;
}

.user-type-selector {
  text-align: center;
  margin-bottom: 20px;
}
</style> 
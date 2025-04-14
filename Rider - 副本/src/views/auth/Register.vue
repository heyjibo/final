<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-title">
        <h2>校园外卖配送系统</h2>
        <p>账号注册</p>
      </div>
      
      <el-form :model="registerForm" :rules="registerRules" ref="registerForm" label-width="0px">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            prefix-icon="el-icon-user" 
            placeholder="请输入用户名">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            prefix-icon="el-icon-lock" 
            placeholder="请输入密码" 
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            prefix-icon="el-icon-lock" 
            placeholder="请确认密码" 
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            prefix-icon="el-icon-mobile-phone" 
            placeholder="请输入手机号">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="userType" class="user-type-selector">
          <el-radio-group v-model="registerForm.userType">
            <el-radio label="rider">骑手</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="registerForm.userType === 'admin'" prop="adminCode">
          <el-input 
            v-model="registerForm.adminCode" 
            prefix-icon="el-icon-key" 
            placeholder="请输入管理员邀请码">
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" class="register-button">
            注册
          </el-button>
        </el-form-item>
        
        <div class="register-options">
          <span>已有账号？</span>
          <span class="login-link" @click="goToLogin">返回登录</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    // 自定义校验规则 - 确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    // 自定义校验规则 - 手机号
    const validatePhone = (rule, value, callback) => {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }
    
    // 自定义校验规则 - 管理员邀请码
    const validateAdminCode = (rule, value, callback) => {
      if (this.registerForm.userType === 'admin' && value !== 'admin123') {
        callback(new Error('管理员邀请码错误'))
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
        userType: 'rider', // 默认为骑手注册
        adminCode: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
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
      // 当用户类型变更时，如果是管理员，需要验证管理员邀请码
      if (val === 'admin') {
        this.$refs.registerForm && this.$refs.registerForm.validateField('adminCode')
      }
    }
  },
  methods: {
    // 注册处理
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          // 实际项目中这里应该调用后端API进行注册
          // 这里使用本地存储模拟注册
          setTimeout(() => {
            const localUsers = JSON.parse(localStorage.getItem('users') || '[]')
            
            // 检查用户名是否已存在
            const existingUser = localUsers.find(u => u.username === this.registerForm.username)
            if (existingUser) {
              this.$message.error('用户名已存在')
              this.loading = false
              return
            }
            
            // 添加新用户
            const newUser = {
              id: Date.now().toString(),
              username: this.registerForm.username,
              password: this.registerForm.password,
              phone: this.registerForm.phone,
              userType: this.registerForm.userType,
              createTime: new Date().toISOString()
            }
            
            localUsers.push(newUser)
            localStorage.setItem('users', JSON.stringify(localUsers))
            
            this.$message.success('注册成功，请登录')
            this.$router.push('/login')
            
            this.loading = false
          }, 1000)
        } else {
          return false
        }
      })
    },
    
    // 跳转到登录页
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
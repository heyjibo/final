<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>校园外卖配送系统</h2>
        <p>欢迎登录</p>
      </div>
      
      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" label-width="0px">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            prefix-icon="el-icon-user" 
            placeholder="请输入用户名"
            @keyup.enter.native="handleLogin">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            prefix-icon="el-icon-lock" 
            placeholder="请输入密码" 
            show-password
            @keyup.enter.native="handleLogin">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="userType" class="user-type-selector">
          <el-radio-group v-model="loginForm.userType">
            <el-radio label="rider">骑手</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-button">
            登录
          </el-button>
        </el-form-item>
        
        <div class="login-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <span class="register-link" @click="goToRegister">还没有账号？立即注册</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        userType: 'rider' // 默认为骑手登录
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ]
      },
      loading: false,
      rememberMe: false
    }
  },
  methods: {
    // 登录处理
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          // 实际项目中这里应该调用后端API进行登录验证
          // 这里使用本地存储的账号进行模拟登录
          setTimeout(() => {
            const localUsers = JSON.parse(localStorage.getItem('users') || '[]')
            console.log('localUsers:', localUsers)
            
            const user = localUsers.find(u => 
              u.username === this.loginForm.username && 
              u.password === this.loginForm.password &&
              u.userType === this.loginForm.userType
            )
            
            if (user) {
              // 登录成功
              const userData = {
                id: user.id,
                username: user.username,
                userType: user.userType,
                token: `token-${Date.now()}`
              }
              
              // 保存到本地存储
              localStorage.setItem('currentUser', JSON.stringify(userData))
              
              // 根据用户类型跳转不同页面
              this.$message.success('登录成功')
              
              console.log('登录成功，用户类型：', this.loginForm.userType)
              
              // 延迟一下再跳转，确保本地存储已更新
              setTimeout(() => {
                if (this.loginForm.userType === 'admin') {
                  this.$router.push('/admin/dashboard')
                } else {
                  this.$router.push('/rider/dashboard')
                }
              }, 100)
            } else {
              // 尝试添加一个默认用户
              if (localUsers.length === 0) {
                const defaultUsers = [
                  {
                    id: 'admin1',
                    username: 'admin',
                    password: 'admin123',
                    userType: 'admin',
                    createTime: new Date().toISOString()
                  },
                  {
                    id: 'rider1',
                    username: 'rider',
                    password: 'rider123',
                    userType: 'rider',
                    createTime: new Date().toISOString()
                  }
                ]
                localStorage.setItem('users', JSON.stringify(defaultUsers))
                this.$message.info('系统中没有用户，已添加默认用户。请使用默认账号重新登录。')
                this.loading = false
                return
              }
              
              this.$message.error('用户名或密码错误')
            }
            
            this.loading = false
          }, 1000)
        } else {
          return false
        }
      })
    },
    
    // 跳转到注册页
    goToRegister() {
      this.$router.push('/register')
    }
  },
  mounted() {
    // 检查本地存储是否有保存的登录信息
    const savedUser = localStorage.getItem('savedLoginInfo')
    if (savedUser) {
      const { username, userType, rememberMe } = JSON.parse(savedUser)
      this.loginForm.username = username
      this.loginForm.userType = userType
      this.rememberMe = rememberMe
    }
    
    // 如果已登录，直接跳转到对应页面
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
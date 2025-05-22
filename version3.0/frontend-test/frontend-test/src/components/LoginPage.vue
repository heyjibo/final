<template>
  <div class="login-container">
    <div class="login-box">
      <h2 style="text-align: center; margin-bottom: 30px; color: #303133;">外卖系统登录</h2>


      <div v-if="loginError" class="error">
        {{ loginError }}
      </div>

      <div v-if="$route.query.error === 'invalid_role'" class="error">
        角色权限错误，请使用正确的账号登录
      </div>


      <div class="input-group">
        <input
            type="text"
            v-model="username"
            :placeholder="usernamePlaceholder">
        <div v-if="errors.username" class="error">{{ errors.username }}</div>
      </div>

      <div class="input-group">
        <input type="password" v-model="password" placeholder="密码">
        <div v-if="errors.password" class="error">{{ errors.password }}</div>
      </div>

      <div class="input-group">
        <select v-model="userType" class="user-type-select">
          <option value="customer">顾客登录</option>
          <option value="rider">骑手登录</option>
          <option value="merchant">商家登录</option>
          <option value="admin">平台管理员登录</option>
        </select>
      </div>


      <div class="terms">
        <input type="checkbox" v-model="agreeTerms">
        <span>我已阅读并接受条款：
          <a @click="showTerms">《服务条款》</a>
          <a @click="showPrivacy">《隐私政策》</a>
        </span>
        <div v-if="errors.agreeTerms" class="error">{{ errors.agreeTerms }}</div>
      </div>


      <div class="button-group">
        <button @click="handleLogin" class="login-btn">登 录</button>
        <button @click="handleRegister" class="register-btn">注 册</button>
      </div>


      <div v-if="showTermsDialog" class="dialog-mask" @click.self="closeDialog">
        <div class="dialog-container">
          <div class="dialog-header">
            <h3>服务条款</h3>
            <button @click="closeDialog" class="close-btn">&times;</button>
          </div>
          <div class="dialog-content">
            <h4>一、总则</h4>
            <p>1. 用户在使用本平台服务前，应当仔细阅读并遵守本服务条款</p>
            <h4>二、服务内容</h4>
            <p>1. 本平台提供外卖订餐相关的信息服务...</p>
            <h4>三、用户义务</h4>
            <p>1. 用户应保证提供的信息真实、准确、完整</p>
          </div>
        </div>
      </div>


      <div v-if="showPrivacyDialog" class="dialog-mask" @click.self="closeDialog">
        <div class="dialog-container">
          <div class="dialog-header">
            <h3>隐私政策</h3>
            <button @click="closeDialog" class="close-btn">&times;</button>
          </div>
          <div class="dialog-content">
            <h4>信息收集与使用</h4>
            <p>1. 我们仅会出于本政策所述的以下目的，收集和使用您的个人信息</p>
            <h4>数据安全</h4>
            <p>1. 我们采用行业标准的安全防护措施保护您的个人信息</p>
            <h4>信息共享</h4>
            <p>1. 我们不会向任何第三方共享或出售您的个人信息</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userType: 'customer',
      username: '',
      password: '',
      agreeTerms: false,
      loginError: '',
      showTermsDialog: false,
      showPrivacyDialog: false,
      errors: {
        username: '',
        password: '',
        agreeTerms: ''
      }
    }
  },
  computed: {
    usernamePlaceholder() {
      const placeholders = {
        customer: '输入用户ID/手机号',
        rider: '输入工号/手机号',
        merchant: '输入商家ID/手机号',
        admin: '用户名'
      }
      return placeholders[this.userType]
    }
  },
  methods: {
    parseJwt(token) {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(atob(base64));
      } catch (e) {
        return null;
      }
    },

    validateForm() {
      this.errors = {
        username: '',
        password: '',
        agreeTerms: '',
        isLoading: false
      }

      let isValid = true


      if (['customer', 'rider', 'merchant'].includes(this.userType)) {
        if (!this.username.trim()) {
          this.errors.username = this.getUserTypeError('username')
          isValid = false
        }
      } else {
        if (!this.username.trim()) {
          this.errors.username = '用户名不能为空'
          isValid = false
        }
      }


      if (!this.password) {
        this.errors.password = '密码不能为空'
        isValid = false
      } else if (this.password.length < 6) {
        this.errors.password = '密码长度不能少于8位'
        isValid = false
      }


      if (!this.agreeTerms) {
        this.errors.agreeTerms = '请先阅读并同意服务条款'
        isValid = false
      }

      return isValid
    },

    getUserTypeError(field) {
      if (field === 'username') {
        return {
          customer: '请输入用户ID或手机号',
          rider: '请输入工号或手机号',
          merchant: '请输入商家ID或手机号'
        }[this.userType]
      }
      return ''
    },

    async handleLogin() {
      this.loginError = '';
      if (!this.validateForm()) return;

      try {
        const endpointMap = {
          rider: '/api/deliver/login',
          merchant: '/api/merchant/login',
          admin: '/api/admin/login',
          customer: '/api/customer/login'
        };

        const usernameFieldMap = {
          rider: 'workId',
          merchant: 'id',
          admin: 'id',
          customer: 'customerID'
        };

        const endpoint = endpointMap[this.userType];
        const usernameField = usernameFieldMap[this.userType];

        const response = await this.$axios.post(
            endpoint,
            {
              [usernameField]: this.username,
              password: this.password
            },
            {
              headers: {
                'Content-Type': 'application/json'
              }
            }
        );

        const token = response.data;
        localStorage.setItem('token', token);
        localStorage.setItem('userType', this.userType);

        const payload = this.parseJwt(token);
        localStorage.setItem('userId', payload.sub);

        this.$message({
          message: '登录成功',
          type: 'success',
          duration: 1500
        });

        const routeMap = {
          rider: '/rider/orders',
          merchant: '/merchant/dashboard',
          admin: '/admin/riders',
          customer: '/customer/profile'
        };

        setTimeout(async () => {
          await this.$router.push(routeMap[this.userType]).catch(() => {});
        }, 800);

      } catch (error) {
        if (error.response) {
          if (this.userType === 'merchant' && error.response.status === 401) {
            this.loginError = '商家ID或密码错误';
          } else {
            this.loginError = error.response.data || '登录失败';
          }
          console.error('后端返回错误:', error.response.status, error.response.data);
        } else {
          this.loginError = '网络错误，请检查连接';
          console.error('网络错误:', error.message);
        }

        this.$message({
          message: this.loginError,
          type: 'error',
          duration: 2000
        });
      }
    },

    handleRegister() {
      this.$router.push('/register')
    },

    showTerms() {
      this.showTermsDialog = true
      this.showPrivacyDialog = false
    },

    showPrivacy() {
      this.showPrivacyDialog = true
      this.showTermsDialog = false
    },

    closeDialog() {
      this.showTermsDialog = false
      this.showPrivacyDialog = false
    }
  }
}
</script>

<style scoped>
.error {
  color: #f56c6c;
  font-size: 14px;
  margin-bottom: 15px;
  padding: 8px;
  background: #fef0f0;
  border-radius: 4px;
  border: 1px solid #fbc4c4;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}

.login-box {
  background: white;
  padding: 40px 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
  transition: all 0.3s;
  position: relative;
}

.user-type-select {
  width: 40%;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  background-color: white;
  background-size: 12px;
  background-position: right 8px center;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2360667c' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpath d='M6 9l6 6 6-6'/%3e%3c/svg%3e");
  background-repeat: no-repeat;
  transition: all 0.3s;
  margin-left: 0;
  display: block;
}

.input-group {
  margin: 0 auto 20px;
  width: 85%;
  position: relative;
}

.input-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.input-group input:focus {
  border-color: #409EFF;
  outline: none;
  box-shadow: 0 0 4px rgba(64,158,255,.3);
}

.terms {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px auto;
  color: #606266;
  font-size: 13px;
}

.terms input[type="checkbox"] {
  margin-right: 8px;
  width: 16px;
  height: 16px;
}

.button-group {
  width: 85%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

button {
  width: 50%;
  padding: 12px 20px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #66b1ff;
}


.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.dialog-container {
  background: white;
  border-radius: 8px;
  width:65%;
  max-width: 475px;
  max-height: 70vh;
  min-width: 280px;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
}

.dialog-content {
  padding: 20px;
  overflow-y: auto;
  line-height: 1.5;
}

.dialog-content h4 {
  color: #303133;
  margin: 15px 0 10px;
}

.dialog-content p {
  margin: 8px 0;
  color: #606266;
}

.close-btn {
  font-size: 20px;
  padding: 0 4px;
  width: 32px;
  height: 32px;
  line-height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}


.close-btn:hover {
  background: #f5f5f5;
  border-radius: 50%;
}

a {
  color: #409EFF;
  text-decoration: none;
  cursor: pointer;
  margin: 0 2px;
}

a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }

  button {
    width: 100%;
  }

  .dialog-container {
    width: 90%;
  }
}
</style>
<template>
  <div class="login-container">
    <div class="login-box">
      <h2 style="text-align: center; margin-bottom: 30px; color: #303133;">系统登录</h2>

      <!-- 错误提示区域 -->
      <div v-if="loginError" class="error">
        {{ loginError }}
      </div>

      <div v-if="$route.query.error === 'invalid_role'" class="error">
        角色权限错误，请使用正确的账号登录
      </div>

      <div class="input-group">
        <input type="text" v-model="username" placeholder="用户名">
        <div v-if="errors.username" class="error">{{ errors.username }}</div>
      </div>

      <div class="input-group">
        <input type="password" v-model="password" placeholder="密码">
        <div v-if="errors.password" class="error">{{ errors.password }}</div>
      </div>

      <div class="input-group">
        <select v-model="userType" class="user-type-select">
          <option value="rider">骑手登录</option>
          <option value="admin">平台管理员登录</option>
        </select>
      </div>

      <div class="terms">
        <input type="checkbox" v-model="agreeTerms">
        <span>我已阅读并接受条款：《服务条款》《隐私政策》</span>
        <div v-if="errors.agreeTerms" class="error">{{ errors.agreeTerms }}</div>
      </div>

      <div class="button-group">
        <button @click="handleLogin" class="login-btn">登 录</button>
        <button @click="handleRegister" class="register-btn">注 册</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userType: 'rider',
      username: '',
      password: '',
      agreeTerms: false,
      loginError: '',
      errors: {
        username: '',
        password: '',
        agreeTerms: ''
      }
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


      if (!this.username.trim()) {
        this.errors.username = '用户名不能为空'
        isValid = false
      } else if (!this.password) {
        this.errors.password = '密码不能为空'
        isValid = false
      } else if (this.password.length < 6) {
        this.errors.password = '密码长度不能少于6位'
        isValid = false
      } else if (!this.agreeTerms) {
        this.errors.agreeTerms = '请先阅读并同意服务条款'
        isValid = false
      }

      return isValid
    },

    async handleLogin() {
      this.loginError = '';
      if (!this.validateForm()) return;

      try {
        const endpoint = this.userType === 'admin'
            ? '/api/admin/login'
            : '/api/deliver/login';

        // 明确指定请求头和请求体格式
        const response = await this.$axios.post(
            endpoint,
            {
              [this.userType === 'admin' ? 'id' : 'workId']: this.username,
              password: this.password
            },
            {
              headers: {
                'Content-Type': 'application/json' // 关键修复点
              }
            }
        );

        const token = response.data;
        localStorage.setItem('token', token);
        localStorage.setItem('userType', this.userType);

        // 解析 Token 获取用户ID
        const payload = this.parseJwt(token);

        localStorage.setItem('userId', payload.sub);

        if (this.userType === 'rider') {
          await this.$router.push('/rider/orders').catch(() => {});
        } else if (this.userType === 'admin') {
          await this.$router.push('/admin/riders').catch(() => {});
        }

      } catch (error) {
        if (error.response) {
          this.loginError = error.response.data || '登录失败';
          console.error('后端返回错误:', error.response.status, error.response.data);
        } else {
          this.loginError = '网络错误，请检查连接';
        }
      }
    },

    handleRegister: function () {
      this.$router.push('/register')
    },

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

.button-group {
  width: 85%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
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
}

.user-type-select {
  width:40%;
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


.user-type select:focus {
  border-color: #409EFF;
  outline: none;
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

@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }
  button {
    width: 100%;
  }
}
</style>
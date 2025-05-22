<template>
  <div class="login-container">
    <div class="login-box">
      <h2 style="text-align: center; margin-bottom: 30px; color: #303133;">管理员注册</h2>

      <div v-if="errorMessage" class="error">
        {{ errorMessage }}
      </div>

      <div class="input-group">
        <input type="text" v-model="username" placeholder="用户名">
      </div>

      <div class="input-group">
        <input type="password" v-model="password" placeholder="请输入密码（8位以上，含大小写/符号至少一种）">
      </div>

      <div class="input-group">
        <input type="password" v-model="confirmPassword" placeholder="确认密码">
      </div>

      <div class="terms">
        <input type="checkbox" v-model="agreeTerms">
        <span>我已阅读并接受条款：《服务条款》《隐私政策》</span>
      </div>

      <div class="button-group">
        <button @click="handleRegister" class="register-btn">立即注册</button>
      </div>

      <div class="bottom-links">
        <router-link to="/register" class="back-link">返回注册类型选择</router-link>
        <button @click="goToLogin" class="login-btn">已有账号？去登录</button>
      </div>
    </div>
  </div>
</template>

<script>

import axios from 'axios'

export default {
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      agreeTerms: false,
      errorMessage: ''
    }
  },
  methods: {

    async handleRegister() {

      this.errorMessage = ''


      if (!this.validateForm()) return

      try {
        const response = await axios.post('/api/admin/register', {
          id: this.username,         // 字段名与后端对应
          password: this.password,
          confirmPassword: this.confirmPassword,
          agreeTerms: this.agreeTerms
        })


        if (response.data.success) {
          alert('注册成功！')
          await this.$router.push('/login')
        }
      } catch (error) {

        this.errorMessage = error.response?.data?.message || '注册失败，请稍后重试'
      }
    },


    validateForm() {
      if (!this.username.trim()) {
        this.errorMessage = '用户名不能为空'
        return false
      }
      if (!this.password) {
        this.errorMessage = '密码不能为空'
        return false
      }
      if (this.password.length < 6) {
        this.errorMessage = '密码长度不能少于8位'
        return false
      }
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])|(?=.*\W).{8,}$/;
      if (!passwordRegex.test(this.password)) {
        this.errorMessage = '密码需要包含大小写/符号至少一种）';
        return false;
      }
      if (this.password !== this.confirmPassword) {
        this.errorMessage = '两次输入的密码不一致'
        return false
      }
      if (!this.agreeTerms) {
        this.errorMessage = '请先阅读并同意服务条款'
        return false
      }
      return true
    },

    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
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

.error {
  color: #f56c6c;
  font-size: 14px;
  margin-bottom: 15px;
  padding: 8px;
  background: #fef0f0;
  border-radius: 4px;
  border: 1px solid #fbc4c4;
}
.bottom-links {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 85%;
  margin: 20px auto 0;
}

.back-link {
  color: #409EFF;
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
}

.login-btn {
  background: transparent !important;
  color: #409EFF !important;
  border: none;
  padding: 0 !important;
  width: auto !important;
}
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }
  button {
    width: 100%;
  }
  .bottom-links {
    flex-direction: column;
    align-items: flex-end;
    gap: 10px;
  }
  .back-link {
    order: 2;
  }
  .login-btn {
    order: 1;
    width: 100% !important;
    text-align: right;
  }
}
</style>
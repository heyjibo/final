<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">商家注册</h2>

      <div v-if="errorMessage" class="error">
        {{ errorMessage }}
      </div>

      <div v-if="successInfo" class="success-info">
        <h3>注册成功！</h3>
        <p>商家ID：<strong>{{ successInfo.workId }}</strong></p>
        <p>登录密码：<strong>{{ successInfo.password }}</strong></p>
      </div>

      <form v-else @submit.prevent="handleSubmit" class="register-form">
        <div class="input-group">
          <input
              type="text"
              v-model="form.name"
              placeholder="商家名称"
              class="form-input"
          >
          <div v-if="errors.name" class="error-msg">{{ errors.name }}</div>
        </div>

        <div class="input-group">
          <input
              type="password"
              v-model="form.password"
              placeholder="请输入密码（8位以上，含大小写/符号至少一种）"
              class="form-input"
          >
          <div v-if="errors.password" class="error-msg">{{ errors.password }}</div>
        </div>

        <div class="input-group">
          <input
              type="password"
              v-model="form.confirmPassword"
              placeholder="确认密码"
              class="form-input"
          >
          <div v-if="errors.confirmPassword" class="error-msg">{{ errors.confirmPassword }}</div>
        </div>

        <div class="input-group">
          <input
              type="tel"
              v-model="form.phonenumber"
              placeholder="请输入手机号"
              class="form-input"
          >
          <div v-if="errors.phonenumber" class="error-msg">{{ errors.phonenumber }}</div>
        </div>

        <div class="terms">
          <input type="checkbox" v-model="form.agreeTerms">
          <span>我已阅读并接受条款：《服务条款》《隐私政策》</span>
        </div>

        <button type="submit" class="register-btn">立即注册</button>
      </form>

      <div class="bottom-links">
        <router-link to="/register" class="back-link">返回注册类型选择</router-link>
        <button @click="goToLogin" class="login-btn">已有账号？去登录</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        name: '',
        password: '',
        confirmPassword: '',
        phonenumber: '',
        agreeTerms: false
      },
      errors: {},
      successInfo: null,
      errorMessage: ''
    }
  },
  methods: {
    goToLogin() {
      this.$router.push('/login')
    },
    validateForm() {
      this.errors = {}
      let isValid = true

      if (!this.form.name.trim()) {
        this.errors.name = '商家名称不能为空'
        isValid = false
      }

      if (this.form.password.length < 8) {
        this.errors.password = '密码至少8位'
        isValid = false
      } else if (!/^(?=.*[a-zA-Z])(?=.*[\W_])|(?=.*[a-z])(?=.*[A-Z])/.test(this.form.password)) {
        this.errors.password = '密码需要包含大小写/符号至少一种）'
        isValid = false
      }
      const phoneReg = /^1[3-9]\d{9}$/;
      if (!this.form.phonenumber) {
        this.errors.phonenumber = '手机号不能为空';
        isValid = false;
      } else if (!phoneReg.test(this.form.phonenumber)) {
        this.errors.phonenumber = '手机号格式不正确';
        isValid = false;
      }
      return isValid
    },
    async handleSubmit() {
      if (!this.validateForm()) return

      try {
        const res = await this.$axios.post('/api/merchant/register', {
          name: this.form.name,
          password: this.form.password,
          phonenumber: this.form.phonenumber
        })

        this.successInfo = {
          workId: res.data.workId,
          password: this.form.password
        }

        this.form = {
          name: '',
          password: '',
          confirmPassword: '',
          agreeTerms: false
        }
      } catch (error) {
        this.errorMessage = error.response?.data?.error || '注册失败'
      }
    }
  }
}
</script>

<style scoped>
.register-btn {
  width: 85%;
  padding: 12px 20px;
  margin: 20px auto;
  background-color: #409EFF !important;
  color: white !important;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-btn:hover {
  background-color: #66b1ff !important;
}

.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}

.register-box {
  background: white;
  padding: 40px 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
}


.input-group {
  margin: 0 auto 20px;
  width: 85%;
  position: relative;
}

.form-input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-input:focus {
  border-color: #409EFF;
  outline: none;
  box-shadow: 0 0 4px rgba(64,158,255,.3);
}


.error {
  color: #f56c6c;
  font-size: 14px;
  padding: 8px;
  background: #fef0f0;
  border-radius: 4px;
  border: 1px solid #fbc4c4;
  width: 85%;
  margin: 0 auto 20px;
}

.error-msg {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 6px;
  padding-left: 4px;
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
}


@media (max-width: 480px) {
  .register-box {
    width: 90%;
    padding: 30px 20px;
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
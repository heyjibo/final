<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">骑手注册</h2>

      <!-- 统一错误提示位置 -->
      <div v-if="errorMessage" class="error">
        {{ errorMessage }}
      </div>

      <!-- 成功提示 -->
      <div v-if="successInfo" class="success-info">
        <h3>注册成功！</h3>
        <p>您的工号：<strong>{{ successInfo.workId }}</strong></p>
        <p>登录密码：<strong>{{ successInfo.password }}</strong></p>
        <p class="warning">请妥善保管您的工号，这是您的登录凭证</p>
      </div>

      <!-- 注册表单 -->
      <form v-else @submit.prevent="handleSubmit" class="register-form">
        <!-- 姓名 -->
        <div class="input-group">
          <input
              type="text"
              v-model="form.name"
              placeholder="请输入姓名"
              class="form-input"
          >
          <div v-if="errors.name" class="error-msg">{{ errors.name }}</div>
        </div>

        <!-- 年龄 -->
        <div class="input-group">
          <input
              type="number"
              v-model.number="form.age"
              placeholder="请输入年龄"
              class="form-input"
              min="18"
              max="60"
          >
          <div v-if="errors.age" class="error-msg">{{ errors.age }}</div>
        </div>

        <!-- 密码 -->
        <div class="input-group">
          <input
              type="password"
              v-model="form.password"
              placeholder="请输入密码"
              class="form-input"
          >
          <div v-if="errors.password" class="error-msg">{{ errors.password }}</div>
        </div>

        <!-- 确认密码 -->
        <div class="input-group">
          <input
              type="password"
              v-model="form.confirmPassword"
              placeholder="请再次输入密码"
              class="form-input"
          >
          <div v-if="errors.confirmPassword" class="error-msg">{{ errors.confirmPassword }}</div>
        </div>

        <!-- 服务条款 -->
        <div class="terms">
          <input type="checkbox" v-model="form.agreeTerms">
          <span>我已阅读并接受条款：《服务条款》《隐私政策》</span>
        </div>

        <button type="submit" class="register-btn">立即注册</button>
      </form>

      <!-- 统一底部链接 -->
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
          age: null,
          password: '',
          confirmPassword: '',
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
      this.errors = {};
      let isValid = true;

      // 姓名验证
      if (!this.form.name.trim()) {
        this.errors.name = '姓名不能为空';
        isValid = false;
      }

      // 年龄验证
      if (!this.form.age || this.form.age < 18 || this.form.age > 60) {
        this.errors.age = '年龄需在18-60岁之间';
        isValid = false;
      }

      // 密码验证
      if (this.form.password.length < 6) {
        this.errors.password = '密码至少6位';
        isValid = false;
      } else if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = '两次输入密码不一致';
        isValid = false;
      }

      return isValid;
    },

    async handleSubmit() {
      if (!this.validateForm()) return;

      try {
        const response = await this.$axios.post('/api/deliver/register', {
          name: this.form.name,
          age: this.form.age,
          password: this.form.password
        });

        // 显示成功信息
        this.successInfo = {
          workId: response.data.workId,
          password: this.form.password
        };

        // 清空表单
        this.form = {
          name: '',
          age: null,
          password: '',
          confirmPassword: ''
        };

      } catch (error) {
        if (error.response) {
          alert(`注册失败：${error.response.data.error || '未知错误'}`);
        } else {
          alert('网络错误，请检查连接');
        }
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

/* 统一输入框样式 */
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

/* 统一错误提示样式 */
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

/* 统一底部链接样式 */
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

/* 移动端适配 */
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
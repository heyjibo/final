<template>
  <div class="login-container">
    <div class="login-box">
      <h2 style="text-align: center; margin-bottom: 30px;">选择注册类型</h2>

      <div class="type-buttons">
        <router-link
            to="/register/rider"
            class="type-btn"
        >
          骑手注册
        </router-link>

        <router-link
            to="/register/admin"
            class="type-btn"
        >
          管理员注册
        </router-link>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentType: this.getCurrentType()
    }
  },
  watch: {
    '$route': {
      handler(to) {
        this.currentType = to.name === 'AdminRegister' ? 'admin' : 'rider'
      },
      immediate: true
    }
  },
  methods: {
    getCurrentType() {
      return this.$route.name === 'AdminRegister' ? 'admin' : 'rider'
    }
  }
}
</script>

<style scoped>
.type-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.type-btn {
  padding: 12px 30px;
  border-radius: 4px;
  background: #f0f2f5;
  border: 1px solid #dcdfe6;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}

@media (max-width: 768px) {
  .login-box {
    width: 95%;
    padding: 20px;
  }
  .type-btn {
    padding: 10px 20px;
  }
}
</style>
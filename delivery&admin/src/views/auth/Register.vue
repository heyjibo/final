<template>
  <div class="register-container">
    <div class="register-card">
      <h1>注册账号</h1>
      
      <div class="register-type-selector">
        <button 
          :class="['type-btn', { active: registerType === 'delivery' }]" 
          @click="registerType = 'delivery'"
        >
          外卖配送注册
        </button>
        <button 
          :class="['type-btn', { active: registerType === 'admin' }]" 
          @click="registerType = 'admin'"
        >
          管理员注册
        </button>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="username" 
            required 
            placeholder="请设置用户名"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            required 
            placeholder="请设置密码"
          />
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="confirmPassword" 
            required 
            placeholder="请再次输入密码"
          />
        </div>
        
        <div class="form-group">
          <label for="email">电子邮箱</label>
          <input 
            type="email" 
            id="email" 
            v-model="email" 
            required 
            placeholder="请输入电子邮箱"
          />
        </div>
        
        <div v-if="registerType === 'delivery'" class="form-group">
          <label for="phone">手机号码</label>
          <input 
            type="tel" 
            id="phone" 
            v-model="phone" 
            required 
            placeholder="请输入手机号码"
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-register">注册</button>
          <router-link :to="{ name: 'Login' }" class="btn-link">
            已有账号？立即登录
          </router-link>
        </div>
      </form>
      
      <div v-if="validationError" class="error-message">
        {{ validationError }}
      </div>
      
      <div v-if="authStore.error" class="error-message">
        {{ authStore.error }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../store/auth';

const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const email = ref('');
const phone = ref('');
const registerType = ref('delivery'); // 默认为外卖配送注册

const validationError = ref('');

const handleRegister = async () => {
  // 表单验证
  if (password.value !== confirmPassword.value) {
    validationError.value = '两次输入的密码不一致';
    return;
  }
  
  validationError.value = '';
  
  const success = await authStore.register(
    username.value,
    password.value,
    email.value,
    registerType.value as 'delivery' | 'admin',
    registerType.value === 'delivery' ? phone.value : undefined
  );
  
  if (success) {
    alert('注册成功，请登录');
    router.push({ name: 'Login' });
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.register-card {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.register-type-selector {
  display: flex;
  margin-bottom: 25px;
  border-radius: 5px;
  overflow: hidden;
  border: 1px solid #ddd;
}

.type-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.type-btn.active {
  background: #4a6cf7;
  color: white;
  font-weight: bold;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

input:focus {
  outline: none;
  border-color: #4a6cf7;
  box-shadow: 0 0 0 2px rgba(74, 108, 247, 0.2);
}

.form-actions {
  margin-top: 25px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
}

.btn-register {
  width: 100%;
  padding: 12px;
  background: #4a6cf7;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-register:hover {
  background: #3a5bdf;
}

.btn-link {
  color: #4a6cf7;
  text-decoration: none;
  font-size: 14px;
}

.btn-link:hover {
  text-decoration: underline;
}

.error-message {
  margin-top: 20px;
  color: #e53935;
  text-align: center;
  padding: 10px;
  background: rgba(229, 57, 53, 0.1);
  border-radius: 5px;
}
</style> 
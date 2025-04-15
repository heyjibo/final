<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Login</h1>
      
      <div class="login-type-selector">
        <button 
          :class="['type-btn', { active: loginType === 'delivery' }]" 
          @click="loginType = 'delivery'"
        >
          Delivery Login
        </button>
        <button 
          :class="['type-btn', { active: loginType === 'admin' }]" 
          @click="loginType = 'admin'"
        >
          Admin Login
        </button>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username</label>
          <input 
            type="text" 
            id="username" 
            v-model="username" 
            required 
            placeholder="Please enter username"
          />
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            required 
            placeholder="Please enter password"
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-login">Login</button>
          <router-link :to="{ name: 'Register' }" class="btn-link">
            Don't have an account? Register now
          </router-link>
        </div>
      </form>
      
      <div v-if="authStore.error" class="error-message">
        {{ authStore.error }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../store/auth';

const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const loginType = ref('delivery'); // Default to delivery login

const handleLogin = async () => {
  const success = await authStore.login(
    username.value,
    password.value,
    loginType.value as 'delivery' | 'admin'
  );
  
  if (success) {
    router.push({ 
      name: loginType.value === 'delivery' ? 'DeliveryHome' : 'AdminHome' 
    });
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
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

.login-type-selector {
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

.btn-login {
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

.btn-login:hover {
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
<template>
  <nav class="admin-navbar">
    <div class="nav-left">
      <span class="username">管理员：{{ username }}</span>
    </div>
    <div class="nav-center">
      <router-link
          :to="{ name: 'AdminDashboard' }"
          :class="{ active: $route.path === '/admin/dashboard' }"
          class="nav-link"
      >
        数据看板
      </router-link>
      <router-link
          :to="{ name: 'AdminRiders' }"
          :class="{ active: $route.path === '/admin/riders' }"
          class="nav-link"
      >
        骑手管理
      </router-link>
      <router-link
          :to="{ name: 'AdminOrders' }"
          :class="{ active: $route.path === '/admin/orders' }"
          class="nav-link"
      >
        订单管理
      </router-link>
    </div>
    <div class="nav-right">
      <button @click="handleLogout" class="logout-btn">登出</button>
    </div>
  </nav>
</template>

<script>
export default {
  computed: {
    username() {
      return localStorage.getItem('adminName') || '管理员'
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('userType')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.admin-navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-center {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: #606266;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.nav-link:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}

.nav-link.active {
  background-color: #409EFF;
  color: white;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #f78989;
}
</style>
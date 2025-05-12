<template>
  <nav class="navbar">
    <div class="nav-left">
      <span class="username">用户名：{{ username }}</span>
    </div>
    <div class="nav-center">
      <router-link
          :to="{ name: 'RiderOrders' }"
          :class="{ active: $route.path === '/rider/orders' }"
          class="nav-link"
      >
        我的订单
      </router-link>
      <router-link
          :to="{ name: 'RiderRanking' }"
          :class="{ active: $route.path === '/rider/ranking' }"
          class="nav-link"
      >
        骑手排行榜
      </router-link>
      <router-link
          :to="{ name: 'RiderProfile' }"
          :class="{ active: $route.path === '/rider/profile' }"
          class="nav-link"
      >
        个人主页
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
      return localStorage.getItem('username') || '骑手'
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('userType')
      localStorage.removeItem('userId')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.navbar {
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

.username {
  color: #606266;
  font-size: 16px;
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


<template>
  <el-menu
      :default-active="activePath"
      mode="horizontal"
      router
      class="customer-nav-container"
      background-color="#ffffff"
      text-color="#303133"
      active-text-color="#409EFF">

    <div class="nav-left">
      <el-menu-item index="/rider/orders">
        <i class="el-icon-s-order"></i>
        <span slot="title">我的订单</span>
      </el-menu-item>

      <el-menu-item index="/rider/ranking">
        <i class="el-icon-medal"></i>
        <span slot="title">骑手排行榜</span>
      </el-menu-item>

      <el-menu-item index="/rider/profile">
        <i class="el-icon-user"></i>
        <span slot="title">个人主页</span>
      </el-menu-item>
    </div>

    <div class="nav-right">
      <el-submenu index="rider-menu">
        <template slot="title">
          <i class="el-icon-user-solid"></i>
          <span>{{ username }}</span>
        </template>
        <el-menu-item @click="handleLogout">
          <i class="el-icon-switch-button"></i>
          <span slot="title">登出</span>
        </el-menu-item>
      </el-submenu>
    </div>
  </el-menu>
</template>

<script>
export default {
  computed: {
    username() {
      return  '你好, 骑手'
    },
    activePath() {
      return this.$route.path
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

.customer-nav-container {
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  height: 60px;
}

.nav-left {
  display: flex;
}

.nav-right {
  margin-left: auto;
}

.el-menu-item [class^=el-icon-] {
  vertical-align: middle;
  margin-right: 5px;
}

.el-menu--horizontal >>> .el-submenu__title {
  height: 60px;
  line-height: 60px;
}
</style>

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
      <el-menu-item index="/merchant/dashboard">
        <i class="el-icon-monitor"></i>
        <span slot="title">我的主页</span>
      </el-menu-item>

      <el-menu-item index="/merchant/dishes">
        <i class="el-icon-tableware"></i>
        <span slot="title">菜品管理</span>
      </el-menu-item>
      <el-menu-item index="/merchant/orders">
        <i class="el-icon-s-order"></i>
        <span slot="title">订单处理</span>
      </el-menu-item>
    </div>

    <div class="nav-right">
      <el-submenu index="merchant-menu">
        <template slot="title">
          <i class="el-icon-shop"></i>
          <span>{{ merchantName }}</span>
        </template>
        <el-menu-item @click="handleLogout">
          <i class="el-icon-switch-button"></i>
          <span slot="title">退出登录</span>
        </el-menu-item>
      </el-submenu>
    </div>
  </el-menu>
</template>

<script>
export default {
  computed: {
    merchantName() {
      return localStorage.getItem('name') || '商家'
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
      localStorage.removeItem('name')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.customer-nav-container {
  display: flex;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
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

.el-menu-item {
  padding: 0 12px !important;  /* 减少左右内边距 */
  min-width: auto !important;  /* 取消默认最小宽度 */
}

.el-menu-item [class^=el-icon-] {
  vertical-align: middle;
  margin-right: 5px;
  width: 24px;
}

.el-menu--horizontal >>> .el-submenu__title {
  height: 60px;
  line-height: 60px;
  padding: 0 12px !important;  /* 与菜单项内边距一致 */
}
</style>
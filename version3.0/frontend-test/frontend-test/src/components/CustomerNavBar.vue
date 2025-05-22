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
      <el-menu-item index="/customer/profile">
        <i class="el-icon-user"></i>
        <span slot="title">个人信息</span>
      </el-menu-item>
      <el-menu-item index="/customer/order">
        <i class="el-icon-s-order"></i>
        <span slot="title">点单页面</span>
      </el-menu-item>
      <el-menu-item index="/customer/cart" >
        <i class="el-icon-shopping-cart-2"></i>
        <span slot="title">购物车</span>
      </el-menu-item>
    </div>


    <div class="nav-right">
      <el-submenu index="customer-menu">
        <template slot="title">
          <i class="el-icon-user-solid"></i>
          <span>{{ customerName }}</span>
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
  name: 'CustomerNavBar',
  data() {
    return {
      customerName: '加载中...',
      activePath: '/'
    }
  },
  mounted() {
    this.loadCustomerInfo()
    this.activePath = this.$route.path
  },
  watch: {
    $route(newVal) {
      this.activePath = newVal.path
    }
  },
  methods: {
    async loadCustomerInfo() {
      try {
        const userId = localStorage.getItem('userId')
        const res = await this.$axios.get(`/api/customer/${userId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.customerName = res.data.name || '尊贵客户'
      } catch (error) {
        console.error('获取客户信息失败:', error)
        this.customerName = '客户用户'
      }
    },

    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('userType')
      localStorage.removeItem('userId')
      this.$router.push('/login')
      this.$message({
        message: '已安全登出',
        type: 'success',
        duration: 1500
      })
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

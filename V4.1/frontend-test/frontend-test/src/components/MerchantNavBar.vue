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
          <span :class="['status-indicator', onlineStatusClass]"></span>
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
  props: {
    isOnline: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      localIsOnline: this.isOnline
    }
  },
  watch: {
    isOnline(newVal) {
      this.localIsOnline = newVal;
    }
  },
  computed: {
    merchantName() {
      return localStorage.getItem('name') || '商家'
    },
    activePath() {
      return this.$route.path
    },
    onlineStatusClass() {
      return this.localIsOnline ? 'online' : 'offline';
    }
  },
  async mounted() {
    // 如果父组件没有传递状态，则从本地获取
    if (this.isOnline === undefined) {
      await this.fetchOnlineStatus();
    }
  },
  methods: {
    async fetchOnlineStatus() {
      try {
        const savedStatus = localStorage.getItem('merchantOnlineStatus');
        if (savedStatus !== null) {
          this.localIsOnline = savedStatus === 'true';
          return;
        }

        const merchantId = localStorage.getItem('userId');
        const response = await this.$axios.get(`/api/merchant/${merchantId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        this.localIsOnline = response.data.isOnline;

        localStorage.setItem('merchantOnlineStatus', this.localIsOnline);
      } catch (error) {
        console.error('获取在线状态失败:', error);
      }
    },
    handleLogout() {
      localStorage.removeItem('merchantOnlineStatus');
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
  padding: 0 12px !important;
  min-width: auto !important;
}

.el-menu-item [class^=el-icon-] {
  vertical-align: middle;
  margin-right: 5px;
  width: 24px;
}

.el-menu--horizontal >>> .el-submenu__title {
  height: 60px;
  line-height: 60px;
  padding: 0 12px !important;
}

.status-indicator {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-left: 8px;
}

.status-indicator.online {
  background-color: #67c23a;
}

.status-indicator.offline {
  background-color: #909399;
}
</style>
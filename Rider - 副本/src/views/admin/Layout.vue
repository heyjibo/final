<template>
  <div class="admin-layout">
    <el-container class="full-height">
      <!-- 侧边栏导航 -->
      <el-aside :width="sidebarWidth" class="admin-sidebar">
        <div class="logo">
          <h3>校园外卖管理系统</h3>
        </div>
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          router
          background-color="#001529"
          text-color="#fff"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-s-home"></i>
            <span slot="title">管理中心</span>
          </el-menu-item>
          <el-menu-item index="/admin/riders">
            <i class="el-icon-user"></i>
            <span slot="title">骑手管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">
            <i class="el-icon-data-line"></i>
            <span slot="title">数据统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/work-orders">
            <i class="el-icon-s-order"></i>
            <span slot="title">工单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/settings">
            <i class="el-icon-setting"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主要内容区域 -->
      <el-container>
        <el-header style="height: 60px;">
          <div class="header-left">
            <i class="el-icon-s-fold fold-icon" @click="toggleSidebar"></i>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta && $route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <img class="avatar" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="Avatar">
                {{ userName }}
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">账户设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main>
          <!-- 路由视图 -->
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  data() {
    return {
      isCollapse: false,
      userName: '管理员',
      sidebarWidth: '220px'
    }
  },
  mounted() {
    // 获取登录用户信息
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    console.log('AdminLayout 获取到的用户信息:', currentUser)
    
    if (currentUser.username) {
      this.userName = currentUser.username
    }
    
    // 检查是否有登录
    if (!currentUser.token) {
      console.log('未登录，跳转到登录页')
      this.$message.error('请先登录')
      this.$router.push('/login')
      return
    }
    
    // 检查是否有权限访问管理员系统
    if (currentUser.userType !== 'admin') {
      console.log('无权访问管理员系统，用户类型:', currentUser.userType)
      this.$message.error('无权访问管理员系统')
      this.$router.push('/login')
      return
    }
    
    console.log('管理员布局组件初始化成功')
    
    // 初始检查屏幕尺寸设置响应式
    this.checkScreenSize();
    // 监听窗口大小变化
    window.addEventListener('resize', this.checkScreenSize);
  },
  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('resize', this.checkScreenSize);
  },
  methods: {
    // 切换侧边栏
    toggleSidebar() {
      this.isCollapse = !this.isCollapse
      // 调整侧边栏宽度
      this.sidebarWidth = this.isCollapse ? '64px' : '220px';
    },
    
    // 检查屏幕尺寸自动调整布局
    checkScreenSize() {
      if (window.innerWidth < 768) {
        this.isCollapse = true;
        this.sidebarWidth = '64px';
      } else {
        this.isCollapse = false;
        this.sidebarWidth = '220px';
      }
    },
    
    // 处理菜单命令
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确认退出登录吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 清除登录信息
          localStorage.removeItem('currentUser')
          this.$message.success('已退出登录')
          this.$router.push('/login')
        }).catch(() => {
          // 取消退出
        })
      } else if (command === 'profile') {
        // 个人资料
        this.$message.info('个人资料功能待开发')
      } else if (command === 'settings') {
        // 账户设置
        this.$message.info('账户设置功能待开发')
      }
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100%;
  width: 100%;
  overflow: hidden;
}
.full-height {
  height: 100vh;
  width: 100vw;
}
.admin-sidebar {
  background-color: #001529;
  color: #fff;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  transition: width 0.3s;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #002140;
  color: #fff;
  font-weight: bold;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  padding: 0 15px;
}
.logo h3 {
  margin: 0;
  font-size: 18px;
}
.el-menu-vertical {
  border-right: none;
}
.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}
.el-menu-item {
  position: relative;
  z-index: 1;
}
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #eee;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;
}
.header-left {
  display: flex;
  align-items: center;
}
.fold-icon {
  font-size: 20px;
  margin-right: 20px;
  cursor: pointer;
}
.header-right {
  margin-right: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 8px;
}
.el-dropdown-link {
  color: #333;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}
.el-dropdown-link:hover {
  color: #409EFF;
}
.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  position: relative;
  height: calc(100vh - 60px);
}

/* 确保下拉菜单层级高于其他元素 */
.el-dropdown-menu {
  z-index: 100 !important;
}

/* 修复小屏幕下的布局 */
@media screen and (max-width: 768px) {
  .header-left .el-breadcrumb {
    display: none;
  }
  
  .el-main {
    padding: 10px;
  }
  
  .el-header {
    padding: 0 10px;
  }
  
  .header-right {
    margin-right: 5px;
  }
  
  .avatar {
    width: 25px;
    height: 25px;
  }
  
  .el-menu--collapse {
    width: 64px;
  }
}

/* 超小屏幕适配 */
@media screen and (max-width: 480px) {
  .el-dropdown-link span {
    display: none;
  }
}
</style> 
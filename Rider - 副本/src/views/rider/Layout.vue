<template>
  <div class="rider-layout">
    <el-container class="full-height">
      <!-- Sidebar navigation -->
      <el-aside width="220px" class="rider-sidebar">
        <div class="logo">
          <h3>Campus Food Delivery System</h3>
        </div>
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/rider/dashboard">
            <i class="el-icon-s-home"></i>
            <span slot="title">Rider Center</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- Main content area -->
      <el-container>
        <el-header style="height: 60px;">
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <img class="avatar" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="Avatar">
                {{ userName }}
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main>
          <!-- Router view -->
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'RiderLayout',
  data() {
    return {
      userName: 'Rider'
    }
  },
  mounted() {
    // Get user info
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
    
    if (currentUser.username) {
      this.userName = currentUser.username
    }
    
    // Check if logged in
    if (!currentUser.token) {
      this.$message.error('Please login first')
      this.$router.push('/login')
      return
    }
  },
  methods: {
    // Handle menu commands
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('Are you sure you want to logout?', 'Notice', {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          // Clear login info
          localStorage.removeItem('currentUser')
          this.$message.success('Logged out successfully')
          this.$router.push('/login')
        }).catch(() => {
          // Cancel logout
        })
      }
    }
  }
}
</script>

<style scoped>
.rider-layout {
  height: 100%;
  width: 100%;
}
.full-height {
  height: 100vh;
}
.rider-sidebar {
  background-color: #304156;
  color: #fff;
  height: 100%;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #263445;
  color: #fff;
  font-weight: bold;
  padding: 0 15px;
}
.logo h3 {
  margin: 0;
  font-size: 18px;
}
.el-menu-vertical {
  border-right: none;
}
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  padding: 0 15px;
}
.header-right {
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
</style> 
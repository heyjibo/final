<template>
  <div class="profile-container">
    <NavBar />
    <div class="profile-content">
      <h2 class="profile-title">骑手个人信息</h2>
      <div class="profile-card">
        <div class="info-item">
          <label class="info-label">工号：</label>
          <span class="info-value">{{ riderInfo.workId }}</span>
        </div>
        <div class="info-item">
          <label class="info-label">姓名：</label>
          <span class="info-value">{{ riderInfo.name }}</span>
        </div>
        <div class="info-item">
          <label class="info-label">年龄：</label>
          <span class="info-value">{{ riderInfo.age }}</span>
        </div>
        <div class="info-item">
          <label class="info-label">累计接单数：</label>
          <span class="info-value">{{ riderInfo.numofindent }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from './NavBar.vue'

export default {
  components: {
    NavBar
  },
  data() {
    return {
      riderInfo: {}
    }
  },
  async mounted() {
    await this.fetchRiderProfile()
  },
  methods: {
    async fetchRiderProfile() {
      try {
        const response = await this.$axios.get('/api/deliver/profile', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.riderInfo = response.data
        localStorage.setItem('username', this.riderInfo.name)
      } catch (error) {
        console.error('获取骑手信息失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-content {
  padding: 2rem;
  margin-top: 20px;
}

.profile-title {
  text-align: left;
  color: #303133;
  margin-bottom: 2rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #409EFF;
}

.profile-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  text-align: left;
}

.info-item {
  margin: 1.5rem 0;
  font-size: 16px;
  display: flex;
  align-items: baseline;
  gap: 1rem;
}

.info-label {
  font-weight: bold;
  color: #606266;
  min-width: 120px;
  text-align: right;
}

.info-value {
  color: #303133;
  flex-grow: 1;
  padding-left: 10px;
  border-left: 2px solid #eee;
}
</style>

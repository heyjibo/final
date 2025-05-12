<template>
  <div class="ranking-container">
    <NavBar />
    <div class="ranking-content">
      <h2 class="ranking-title">骑手排行榜</h2>

      <!-- 个人排名卡片 -->
      <div class="personal-rank-card">
        <div class="compact-rank-info">
          <span class="rank-title">您当前排名</span>
          <span class="rank-number">#{{ currentUserRank }}</span>
          <span class="divider">|</span>
          <span class="rider-detail">
      员工名字：<strong>{{ currentUser.name }}</strong>
      <span class="work-id">  (工号{{ currentUser.workId }})</span>
    </span>
        </div>
      </div>


      <!-- 完整排行榜 -->
      <div class="full-ranking">
        <div class="ranking-header">
          <span class="rank-col">排名</span>
          <span class="workid-col">工号</span>
          <span class="name-col">姓名</span>
          <span class="count-col">接单量</span>
        </div>
        <div
            v-for="(rider, index) in sortedRiders"
            :key="rider.workId"
            class="ranking-item"
            :class="{ 'current-user': rider.workId === currentUser.workId }"
        >
          <span class="rank-col">#{{ index + 1 }}</span>
          <span class="workid-col">{{ rider.workId }}</span>
          <span class="name-col">{{ rider.name }}</span>
          <span class="count-col">{{ rider.numofindent }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from './NavBar.vue';

export default {
  components: {
    NavBar
  },
  data() {
    return {
      riders: [],
      currentUser: {}
    };
  },
  computed: {
    sortedRiders() {
      return [...this.riders].sort((a, b) => b.numofindent - a.numofindent);
    },
    currentUserRank() {
      const sorted = this.sortedRiders.map(r => r.workId);
      return sorted.indexOf(this.currentUser.workId) + 1;
    }
  },
  async mounted() {
    await this.fetchRiders();
    await this.fetchCurrentUser();
  },
  methods: {
    async fetchRiders() {
      try {
        const response = await this.$axios.get('/api/deliver/delivers');
        this.riders = response.data;
      } catch (error) {
        console.error('获取骑手数据失败:', error);
      }
    },
    async fetchCurrentUser() {
      try {
        const response = await this.$axios.get('/api/deliver/profile', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        this.currentUser = response.data;
      } catch (error) {
        console.error('获取当前用户信息失败:', error);
      }
    }
  }
};
</script>

<style scoped>
.rider-detail strong,
.work-id strong {
  font-weight: 700;
  color: #303133; /* 保持原有颜色 */
}

.work-id {
  font-size: 0.9em;
  color: #909399;
}
.rank-header h3 {
  margin: 0;
  font-size: 18px;
  color: #606266;
}

.ranking-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.ranking-content {
  padding: 2rem;
  margin-top: 60px;
}

.rank-title {
  font-weight: 600;
  color: #606266;
}

.compact-rank-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.personal-rank-card h3 {
  color: #606266;
  margin-bottom: 1rem;
}
.rank-number {
  font-size: 24px;
  color: #409EFF;
  font-weight: bold;
  margin-right: 0.5rem;
}
.divider {
  color: #dcdfe6;
  padding: 0 8px;
}
.rider-detail {
  color: #303133;
}

.work-id {
  color: #909399;
  font-size: 0.9em;
}
.full-ranking {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.ranking-header {
  display: flex;
  padding: 1rem;
  background: #f5f7fa;
  border-radius: 8px 8px 0 0;
  font-weight: bold;
  color: #606266;
}

.ranking-item {
  display: flex;
  padding: 1rem;
  border-bottom: 1px solid #ebeef5;
}

.ranking-item.current-user {
  background-color: #f0faff;
}

.rank-col { width: 15%; }
.workid-col { width: 25%; }
.name-col { width: 30%; }
.count-col { width: 30%; }

@media (max-width: 768px) {
  .ranking-header,
  .ranking-item {
    font-size: 14px;
  }
  .rank-number {
    margin: 0 auto;
  }
}
</style>
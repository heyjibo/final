<template>
  <div class="admin-container">
    <AdminNavBar />

    <div class="content-wrapper">
    <h2>骑手状态总览</h2>

    <div v-if="loading" class="loading">数据加载中...</div>

    <div v-else class="rider-list">
      <div class="statistics">
        <div class="stat-item">
          <h3>总骑手数</h3>
          <span class="stat-value">{{ totalRiders }}</span>
        </div>
        <div class="stat-item">
          <h3>总完成订单</h3>
          <span class="stat-value">{{ totalCompletedOrders }}</span>
        </div>
        <div class="stat-item">
          <h3>总收益金额</h3>
          <span class="stat-value">¥{{ totalRevenue.toFixed(2) }}</span>
        </div>
      </div>

      <div class="rider-card" v-for="rider in riders" :key="rider.workId">
        <div class="rider-info">
          <div class="info-item">
            <label>工号：</label>
            <span>{{ rider.workId }}</span>
          </div>
          <div class="info-item">
            <label>姓名：</label>
            <span>{{ rider.name }}</span>
          </div>
          <div class="info-item">
            <label>年龄：</label>
            <span>{{ rider.age }}</span>
          </div>
        </div>
        <div class="rider-stats">
          <div class="stat">
            <span class="stat-label">完成订单</span>
            <span class="stat-value">{{ rider.numofindent }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">总收益</span>
            <span class="stat-value">¥{{ rider.totalRevenue.toFixed(2) }}</span>
          </div>

        </div>
      </div>
     </div>
    </div>
  </div>
</template>

<script>
import AdminNavBar from '@/components/AdminNavBar.vue';

export default {
  components: {
    AdminNavBar
  },

  data() {
    return {
      loading: true,
      riders: [],
      totalRiders: 0,
      totalCompletedOrders: 0,
      totalRevenue: 0
    }
  },
  async mounted() {
    try {
      const response = await this.$axios.get('/api/admin/riders');
      this.riders = response.data.riders;
      this.totalRiders = response.data.totalRiders;
      this.totalCompletedOrders = response.data.totalCompletedOrders;
      this.totalRevenue = response.data.totalRevenue; // 确保字段名一致
    } catch (error) {
      console.error('获取数据失败:', error);
    } finally {
      this.loading = false;
    }
  },
  methods: {

  }
}
</script>

<style scoped>
.admin-container {
  padding: 60px 2rem 2rem 2rem;
  max-width: 1200px;
  margin: 2rem auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.statistics {
  display: flex;
  gap: 2rem;
  margin: 2rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409EFF;
}

.rider-card {
  margin: 1rem 0;
  padding: 1.5rem;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rider-info {
  flex: 1;
}

.info-item {
  margin: 0.5rem 0;
}

.info-item label {
  color: #909399;
  min-width: 80px;
  display: inline-block;
}

.rider-stats {
  display: flex;
  gap: 2rem;
}

.stat {
  text-align: center;
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.stat-label {
  display: block;
  color: #909399;
  font-size: 0.9em;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #909399;
}

.info-item span {
  text-align: left;
}

@media (max-width: 768px) {
  .rider-info {
    grid-template-columns: 1fr;
  }
  .content-wrapper {
    padding: 1rem;
  }

  .statistics {
    flex-direction: column;
    gap: 1rem;
  }

  .rider-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .rider-stats {
    width: 100%;
    justify-content: space-between;
    margin-top: 1rem;
  }
}
</style>
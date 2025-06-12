<template>
  <div class="admin-container">
    <AdminNavBar />

    <div class="content-wrapper">
      <h2>骑手状态总览</h2>

      <div v-if="loading" class="loading">数据加载中...</div>

      <div v-else>
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

        <!-- 一行两个骑手卡片 -->
        <div class="rider-grid">
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
              <div class="stat">
                <span class="stat-label">平均评分</span>
                <div class="rating-container">
                  <el-rate
                      v-model="rider.score"
                      disabled
                      :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                      :max="5"
                      show-score
                      text-color="#ff9900"
                      :score-template="rider.score ? rider.score.toFixed(1) : '未评分'"
                  />
                </div>
              </div>
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

      // 获取骑手评分数据
      this.riders = await Promise.all(
          response.data.riders.map(async rider => {
            try {
              const detailRes = await this.$axios.get(`/api/deliver/${rider.workId}`);
              // 直接使用5分制分数
              return {
                ...rider,
                score: detailRes.data.score || 0
              };
            } catch (error) {
              console.error(`获取骑手${rider.workId}详情失败:`, error);
              return {
                ...rider,
                score: 0
              };
            }
          })
      );

      this.totalRiders = response.data.totalRiders;
      this.totalCompletedOrders = response.data.totalCompletedOrders;
      this.totalRevenue = response.data.totalRevenue;
    } catch (error) {
      console.error('获取数据失败:', error);
    } finally {
      this.loading = false;
    }
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

/* 每行两个骑手卡片 */
.rider-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.rider-card {
  padding: 1.5rem;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
}

.rider-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.rider-info {
  margin-bottom: 1rem;
}

.info-item {
  margin: 0.5rem 0;
  display: flex;
}

.info-item label {
  color: #909399;
  min-width: 50px;
}

.rider-stats {
  display: flex;
  gap: 1.5rem;
  justify-content: space-between;
  margin-top: auto;
}

.stat {
  text-align: center;
  padding: 0.5rem 0.8rem;
  background: #f8f9fa;
  border-radius: 4px;
  flex: 1;
}

.stat-label {
  display: block;
  color: #909399;
  font-size: 0.85em;
  margin-bottom: 5px;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #909399;
}

.rating-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.el-rate {
  margin-top: 5px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .rider-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .statistics {
    flex-direction: column;
    gap: 1rem;
  }

  .rider-stats {
    flex-direction: column;
    gap: 0.8rem;
  }
}
</style>
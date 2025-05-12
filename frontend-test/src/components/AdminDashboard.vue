<template>
  <div class="admin-dashboard">
    <AdminNavBar />

    <div class="dashboard-content">
      <h2 class="dashboard-title">平台数据概览</h2>

      <!-- 数据统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon total-riders">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.totalRiders }}</div>
            <div class="stat-label">注册骑手</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon completed-orders">
            <i class="el-icon-sold-out"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.totalCompletedOrders }}</div>
            <div class="stat-label">完成订单</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon revenue">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ overview.totalRevenue }}</div>
            <div class="stat-label">平台收入</div>
          </div>
        </div>
      </div>

      <!-- 骑手排行榜 -->
      <div class="rider-ranking">
        <h3 class="section-title">骑手接单排行榜</h3>
        <el-table
            :data="riders"
            style="width: 100%"
            :default-sort="{ prop: 'numofindent', order: 'descending' }"
        >
          <el-table-column prop="workId" label="工号" width="120"></el-table-column>
          <el-table-column prop="name" label="姓名"></el-table-column>
          <el-table-column prop="numofindent" label="接单数" sortable></el-table-column>
          <el-table-column label="创收金额" sortable prop="totalRevenue">
            <template #default="{ row }">
              ¥{{ row.totalRevenue?.toFixed(2) || 0 }} <!-- 安全处理 -->
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import AdminNavBar from './AdminNavBar.vue'

export default {
  components: {
    AdminNavBar: AdminNavBar
  },
  data() {
    return {
      overview: {
        totalRiders: 0,
        totalCompletedOrders: 0,
        totalRevenue: 0
      },
      riders: []
    }
  },
  async mounted() {
    await this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        // 获取概览数据（包含骑手详情）
        const overviewRes = await this.$axios.get('/api/admin/riders');
        this.overview = {
          totalRiders: overviewRes.data.totalRiders,
          totalCompletedOrders: overviewRes.data.totalCompletedOrders,
          totalRevenue: overviewRes.data.totalRevenue
        };

        // 使用概览接口中的 riders 数据
        this.riders = overviewRes.data.riders
            .sort((a, b) => b.numofindent - a.numofindent)
            .slice(0, 10); // 只显示前10名

      } catch (error) {
        console.error('数据获取失败:', error);
        this.riders = [];
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 60px 2rem 2rem 2rem;
  max-width: 1200px;
  margin: 2rem auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.dashboard-title {
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 0.8rem;
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 1.5rem;
}

.stat-icon i {
  font-size: 28px;
  color: white;
}

.total-riders { background-color: #409EFF; }
.completed-orders { background-color: #67c23a; }
.revenue { background-color: #e6a23c; }

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.section-title {
  color: #606266;
  margin: 2rem 0 1rem;
  padding-left: 0.5rem;
  border-left: 4px solid #409EFF;
}

.rider-ranking {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>
<template>
  <div class="page-container">
    <CustomerNavBar />
    <div class="content-container">
      <div class="section-header">
        <h2 class="section-title">商家列表</h2>
        <el-input
            v-model="searchQuery"
            placeholder="搜索商家"
            clearable
            class="search-input"
            @clear="clearSearch">
          <i slot="prefix" class="el-icon-search"></i>
        </el-input>
      </div>

      <div class="merchant-list">
        <div
            v-for="merchant in filteredMerchants"
            :key="merchant.id"
            :class="['common-card', 'merchant-item', {'offline-merchant': !merchant.isOnline}]"
            @click="handleMerchantClick(merchant)">

          <div class="merchant-content">
            <div class="merchant-info">
              <h3 class="merchant-name">{{ merchant.name }}</h3>
              <div class="meta-tags">
                <el-tag type="warning" effect="plain">
                  <i class="el-icon-star-on"></i>
                  评分 {{ merchant.score.toFixed(1) }}
                </el-tag>
                <el-tag :type="merchant.isOnline ? 'success' : 'danger'" effect="plain">
                  <i class="el-icon-circle-check"></i>
                  {{ merchant.isOnline ? '营业中' : '已打烊' }}
                </el-tag>
              </div>
            </div>
            <i class="el-icon-arrow-right link-icon"></i>
          </div>

          <div v-if="!merchant.isOnline" class="offline-overlay">
            <span>商家已打烊，暂不接单</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CustomerNavBar from '@/components/CustomerNavBar.vue'

export default {
  name: 'CustomerOrder',
  components: {
    CustomerNavBar
  },
  data() {
    return {
      merchants: [],
      searchQuery: ''
    }
  },
  computed: {
    filteredMerchants() {
      return this.merchants.filter(merchant =>
          merchant.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      )
    }
  },
  async mounted() {
    try {
      const res = await this.$axios.get('/api/merchant')
      this.merchants = res.data.map(m => ({
        ...m,
        score: m.score || 0,
        isOnline: m.isOnline || false
      }))
      console.log('商家列表数据:', this.merchants) // 调试信息
    } catch (error) {
      console.error('获取商家列表失败:', error)
      this.$message.error('获取商家信息失败')
    }
  },
  methods: {
    clearSearch() {
      this.searchQuery = ''
    },
    handleMerchantClick(merchant) {
      if (!merchant.isOnline) {
        this.$message.warning('商家已打烊，暂不接单')
        return
      }
      this.$router.push(`/merchant/${merchant.id}`)
    }
  }
}
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-title {
  color: var(--primary-color);
  font-size: 1.5rem;
  margin: 0;
}

.search-input {
  width: 300px;
}

.merchant-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.common-card {
  background: #fff;
  border-radius: var(--card-radius);
  box-shadow: var(--box-shadow);
  transition: var(--transition);
}

.merchant-item {
  padding: 20px;
  position: relative;
  cursor: pointer;
}

.merchant-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.merchant-name {
  color: var(--text-primary);
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.meta-tags {
  display: flex;
  gap: 12px;
}

.link-icon {
  color: var(--text-secondary);
  font-size: 1.3rem;
  margin-left: 15px;
}

.offline-merchant {
  opacity: 0.7;
  cursor: not-allowed;
}

.offline-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
  z-index: 10;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }

  .search-input {
    width: 100%;
  }

  .merchant-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .link-icon {
    display: none;
  }

  .meta-tags {
    flex-wrap: wrap;
  }
}
</style>
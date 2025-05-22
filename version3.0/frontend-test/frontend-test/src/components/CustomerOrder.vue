<template>
  <div class="order-container">
    <CustomerNavBar />
    <div class="header">
      <h2>商家菜品列表</h2>
      <div class="filters">
        <el-select v-model="sortType" placeholder="排序方式" @change="applySort">
          <el-option label="默认排序" value="default"></el-option>
          <el-option label="价格升序" value="priceAsc"></el-option>
          <el-option label="价格降序" value="priceDesc"></el-option>
        </el-select>
        <el-select v-model="selectedMerchant" placeholder="筛选商家" @change="filterByMerchant">
          <el-option label="全部商家" value=""></el-option>
          <el-option
              v-for="merchant in merchants"
              :key="merchant.id"
              :label="merchant.name"
              :value="merchant.id">
          </el-option>
        </el-select>
      </div>
    </div>

    <div class="merchant-list">
      <div v-for="merchant in filteredMerchants" :key="merchant.id" class="merchant-card">
        <h3 class="merchant-name">{{ merchant.name }}</h3>
        <div class="dish-list">
          <div v-for="dish in merchant.dishes" :key="dish.ID" class="dish-item">
            <div class="dish-info">
              <span class="dish-name">{{ dish.name }}</span>
              <span class="dish-price">¥{{ dish.unitPrice }}</span>
            </div>
            <el-button
                type="primary"
                size="mini"
                @click="addToCart(dish)">
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CustomerNavBar from '@/components/CustomerNavBar.vue'

export default {
  components: {
    CustomerNavBar
  },
  data() {
    return {
      allDishes: [],
      merchants: [],
      selectedMerchant: '',
      sortType: 'default',
      cartItems: JSON.parse(localStorage.getItem('cart')) || []
    }
  },
  computed: {
    filteredMerchants() {
      let merchants = this.merchants.map(merchant => {
        return {
          ...merchant,
          dishes: this.allDishes.filter(dish =>
              dish.merchantID === merchant.id &&
              (this.selectedMerchant ? dish.merchantID === this.selectedMerchant : true)
          )
        }
      }).filter(merchant => merchant.dishes.length > 0)


      if (this.sortType === 'priceAsc') {
        merchants.forEach(merchant => {
          merchant.dishes.sort((a, b) => a.unitPrice - b.unitPrice)
        })
      } else if (this.sortType === 'priceDesc') {
        merchants.forEach(merchant => {
          merchant.dishes.sort((a, b) => b.unitPrice - a.unitPrice)
        })
      }

      return merchants
    }
  },
  async mounted() {
    await this.fetchData()
  },
  methods: {
    async fetchData() {
      try {

        const dishesRes = await this.$axios.get('/api/dish')
        this.allDishes = dishesRes.data


        const merchantsRes = await this.$axios.get('/api/merchant')
        this.merchants = merchantsRes.data
      } catch (error) {
        console.error('数据获取失败:', error)
        this.$message.error('数据加载失败，请稍后重试')
      }
    },

    applySort() {

    },

    filterByMerchant() {

    },

    addToCart(dish) {
      const existing = this.cartItems.find(item => item.id === dish.id)
      if (existing) {
        existing.quantity += 1
      } else {
        this.cartItems.push({
          id: dish.id,
          name: dish.name,
          unitPrice: dish.unitPrice,
          merchantID: dish.merchantID,
          quantity: 1
        })
      }
      this.$message({
        message: `${dish.name} 已加入购物车`,
        type: 'success',
        duration: 1000
      })
      localStorage.setItem('cart', JSON.stringify(this.cartItems))
    }
  }
}
</script>

<style scoped>
.order-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 15px;
}

.merchant-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 20px;
  padding: 20px;
}

.merchant-name {
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.dish-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
}

.dish-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.dish-info {
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-weight: 500;
  color: #303133;
}

.dish-price {
  color: #f56c6c;
  font-size: 14px;
}
</style>
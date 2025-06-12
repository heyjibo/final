<template>
  <div class="page-container">
    <CustomerNavBar />
    <div class="content-container">
      <div class="section-header">
        <router-link to="/customer/order" class="back-link">
          <i class="el-icon-arrow-left"></i>
          <span>返回商家列表</span>
        </router-link>
        <h1 class="section-title">{{ merchant.name }}</h1>
        <div class="meta-tags">
          <el-tag type="warning" effect="plain">
            <i class="el-icon-star-on"></i>
            评分 {{ merchant.score.toFixed(1) }}
          </el-tag>
          <el-tag type="success" effect="plain">
            <i class="el-icon-clock"></i>
            {{ merchant.deliveryTime || '30分钟送达' }}
          </el-tag>
        </div>
      </div>

      <div class="dish-list">
        <div
            v-for="dish in dishes"
            :key="dish.ID"
            class="common-card dish-item">

          <div class="dish-content">
            <div class="dish-info">
              <h3 class="dish-name">{{ dish.name }}</h3>
              <p class="dish-desc">{{ dish.description || '暂无菜品描述' }}</p>
            </div>
            <div class="dish-actions">
              <span class="price">¥{{ dish.unitPrice }}</span>
              <el-button
                  type="primary"
                  size="small"
                  class="action-button"
                  @click="addToCart(dish)">
                <i class="el-icon-shopping-cart-full"></i>
                加入购物车
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CustomerNavBar from '@/components/CustomerNavBar.vue'

export default {
  name: 'MerchantMenu',
  components: {
    CustomerNavBar
  },
  data() {
    return {
      merchant: {
        score: 0
      },
      dishes: []
    }
  },
  async mounted() {
    const merchantId = this.$route.params.id
    try {
      const [merchantRes, dishesRes] = await Promise.all([
        this.$axios.get(`/api/merchant/${merchantId}`),
        this.$axios.get(`/api/dish/getByMerchant/${merchantId}`)
      ])

      this.merchant = {
        ...merchantRes.data,
        score: merchantRes.data.score || 0
      }

      this.dishes = dishesRes.data
      // 打印菜品 ID 以确认唯一性
      this.dishes.forEach(dish => {
        console.log('Dish ID:', dish.ID);
      });
    } catch (error) {
      console.error('获取商家信息失败:', error)
      this.$message.error('加载商家信息失败')
    }
  },
  methods: {
    addToCart(dish) {
      // 1. 统一使用小写id作为唯一标识（假设后端返回的是id字段）
      const itemId = dish.id || dish.ID;
      if (!itemId) {
        this.$message.error('商品ID缺失，无法添加到购物车');
        return;
      }

      // 2. 读取购物车数据
      let cart = JSON.parse(localStorage.getItem('cart')) || [];

      // 3. 查找已存在的商品项（使用统一的id字段）
      const existItem = cart.find(item => item.id === itemId);

      if (existItem) {
        // 4. 存在则增加数量
        existItem.quantity++;
      } else {
        // 5. 不存在则添加新商品（深拷贝避免引用问题）
        cart.push({
          ...dish,              // 浅拷贝基本属性
          id: itemId,           // 确保id字段存在
          quantity: 1,          // 初始数量为1
          // 深拷贝嵌套属性（如有）
          details: dish.details ? {...dish.details} : {}
        });
      }

      // 6. 存储更新后的购物车
      localStorage.setItem('cart', JSON.stringify(cart));

      this.$message.success('已加入购物车');
    }
  }
}
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-container {
  padding: 20px;
}

.section-header {
  margin-bottom: 30px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  color: #606266;
  margin-bottom: 15px;
  transition: color 0.2s;
}

.back-link:hover {
  color: #409EFF;
}

.section-title {
  font-size: 24px;
  margin-bottom: 15px;
}

.meta-tags {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.dish-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.common-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.dish-item {
  padding: 20px;
}

.dish-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dish-info {
  flex: 1;
}

.dish-name {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.dish-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}

.dish-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.action-button {
  background: linear-gradient(135deg, #253858 20%, #3a5078 100%);
  border: none;
  color: white;
  transition: all 0.3s;
}

.action-button:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .dish-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .dish-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
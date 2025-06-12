<template>
  <div class="cart-container">
    <CustomerNavBar />
    <div class="cart-content">
      <h2 class="cart-title">我的购物车</h2>
      <div v-if="cartItems.length === 0" class="empty-cart">
        <i class="el-icon-shopping-cart-2"></i>
        <p>购物车是空的，快去添加商品吧~</p>
      </div>
      <div v-else>
        <div v-for="(item, index) in cartItems" :key="index" class="cart-item">
          <div class="item-info">
            <span class="dish-name">{{ item.name }}</span>
            <span class="dish-price">¥{{ item.unitPrice }}</span>
          </div>
          <div class="item-actions">
            <el-button-group>
              <el-button
                  size="mini"
                  @click="updateQuantity(index, -1)"
                  :disabled="item.quantity <= 1">
                -
              </el-button>
              <el-button size="mini" disabled>{{ item.quantity }}</el-button>
              <el-button size="mini" @click="updateQuantity(index, 1)">+</el-button>
            </el-button-group>
            <el-button
                type="danger"
                size="mini"
                icon="el-icon-delete"
                @click="removeItem(index)">
            </el-button>
          </div>
        </div>

        <div class="cart-summary">
          <div class="total-price">
            总计：¥{{ totalPrice }}
          </div>
          <el-button
              type="primary"
              size="medium"
              @click="submitOrder"
              :loading="isSubmitting">
            立即结算
          </el-button>
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
      cartItems: [],
      isSubmitting: false
    }
  },
  computed: {
    totalPrice() {
      return this.cartItems.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0)
    }
  },
  mounted() {
    this.loadCartItems()
  },
  methods: {
    loadCartItems() {
      const cartData = localStorage.getItem('cart');
      this.cartItems = cartData ? JSON.parse(cartData) : [];
      // 打印解析后的购物车数据，确认属性名（如ID是否为小写id）
      console.log('购物车数据解析后:', this.cartItems);
    },
    updateQuantity(index, delta) {
      this.cartItems[index].quantity += delta
      this.saveCart()
    },
    removeItem(index) {
      this.cartItems.splice(index, 1)
      this.saveCart()
    },
    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.cartItems))
    },
    async submitOrder() {
      this.isSubmitting = true;
      try {
        const orderData = {
          customerID: localStorage.getItem('userId'),
          content: this.cartItems.map(item => item.name).join(','),
          merchantID: this.cartItems[0]?.merchantID,

        };


        if (!this.validateMerchantConsistency()) {
          this.$message.error('购物车中存在不同商家的商品');
          return;
        }

        await this.$axios.put('/api/indent/creatNewIndent', orderData, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        });

        localStorage.removeItem('cart');
        this.$message.success('订单提交成功！');
        this.$router.push('/customer/order');
      } catch (error) {
        console.error('订单提交失败:', error);
        this.$message.error('订单提交失败，请稍后重试');
      } finally {
        this.isSubmitting = false;
      }
    },


    validateMerchantConsistency() {
      if (this.cartItems.length === 0) return true;
      const firstMerchantID = this.cartItems[0].merchantID;
      return this.cartItems.every(item => item.merchantID === firstMerchantID);
    }
  }
}
</script>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.cart-title {
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.empty-cart {
  text-align: center;
  padding: 50px 0;
  color: #909399;
}

.empty-cart i {
  font-size: 80px;
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  margin: 10px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.item-info {
  flex: 1;
}

.dish-name {
  font-weight: 500;
  margin-right: 20px;
}

.dish-price {
  color: #f56c6c;
  font-size: 16px;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.cart-summary {
  margin-top: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
}

.total-price {
  font-size: 18px;
  color: #303133;
  font-weight: 500;
}
</style>
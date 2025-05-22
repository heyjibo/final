<template>
  <div>
    <MerchantNavBar />
    <div class="dish-edit-container">
      <el-card class="form-card">
        <h2>新增菜品</h2>
        <el-form :model="dishForm" label-width="100px" label-position="left">
          <el-form-item label="菜品名称" required>
            <el-input
                v-model="dishForm.name"
                placeholder="请输入菜品名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="单价" required>
            <el-input-number
                v-model="dishForm.unitPrice"
                :min="1"
                :precision="2"
                controls-position="right"
            style="width: 200px"
            ></el-input-number>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="cancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import MerchantNavBar from '@/components/MerchantNavBar.vue'

export default {
  components: { MerchantNavBar },
  data() {
    return {
      dishForm: {
        name: '',
        unitPrice: 0,
        merchantID: localStorage.getItem('userId'),
        salesVolume: 0
      }
    }
  },
  methods: {
    async submitForm() {
      try {
        await this.$axios.post('/api/dish/createNewDish', this.dishForm, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        })
        this.$message.success('新增成功')
        this.$router.push('/merchant/dishes')
      } catch (error) {
        this.$message.error('操作失败: ' + error.response?.data?.message)
      }
    },
    cancel() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.dish-edit-container {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}
.form-card {
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
h2 {
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
}
</style>
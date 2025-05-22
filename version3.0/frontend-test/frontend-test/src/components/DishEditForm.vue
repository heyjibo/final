<template>
  <div>
    <MerchantNavBar />
    <div class="dish-edit-container">
      <el-card class="form-card">
        <h2>{{ formTitle }}</h2>
        <el-form :model="dishForm" label-width="100px" label-position="left">
          <el-form-item label="菜品名称" required>
            <el-input v-model="dishForm.name" placeholder="请输入菜品名称"></el-input>
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
        id: '',
        name: '',
        unitPrice: 0,
        merchantID: localStorage.getItem('userId'),
        salesVolume: 0
      }
    }
  },
  computed: {
    formTitle() {
      return this.$route.params.id ? '编辑菜品' : '新增菜品'
    }
  },
  async mounted() {
    if (this.$route.params.id) {
      await this.fetchDishData()
    }
  },
  methods: {
    async fetchDishData() {
      try {

        const res = await this.$axios.get(`/api/dish/${this.$route.params.id}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        });
        this.dishForm = {
          ...res.data,
          id: res.data.id,
          name: res.data.name,
          unitPrice: res.data.unitPrice,
          merchantID: res.data.merchantID
        };
      } catch (error) {
        this.$message.error('获取菜品数据失败');
      }
    },
    async submitForm() {
      try {
        await this.$axios.put('/api/dish/update', {
          id: this.dishForm.id,
          name: this.dishForm.name,
          unitPrice: this.dishForm.unitPrice,
          merchantID: this.dishForm.merchantID,
          salesVolume: this.dishForm.salesVolume // 保留原有销量
        }, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        });

        this.$message.success('修改成功');
        this.$router.push('/merchant/dishes');
      } catch (error) {
        this.$message.error('操作失败: ' +
            (error.response?.data?.error || '服务器错误'));
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
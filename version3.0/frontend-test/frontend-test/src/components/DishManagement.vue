<template>
  <div>
    <MerchantNavBar />
    <div class="dish-management">
      <el-card class="container">
        <div class="header">
          <h2>菜品列表</h2>
          <el-button
              type="primary"
              icon="el-icon-plus"
              @click="goToAdd"
          >
            新增菜品
          </el-button>
        </div>

        <el-table :data="dishes" style="width: 100%">
          <el-table-column
              prop="id"
              label="ID"
              v-if="false"
          ></el-table-column>
          <el-table-column prop="name" label="菜品名称" width="180"></el-table-column>
          <el-table-column label="单价">
            <template slot-scope="scope">
              ￥{{ scope.row.unitPrice.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="salesVolume" label="销量"></el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button
                  size="mini"
                  @click="handleEdit(scope.row.id)"
              >编辑</el-button>
              <el-button
                  size="mini"
                  type="danger"
                  @click="handleDelete(scope.row.id)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
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
      dishes: []
    }
  },
  async mounted() {
    await this.fetchDishes()
  },
  methods: {
    async fetchDishes() {
      try {
        const merchantID = localStorage.getItem('userId')
        const res = await this.$axios.get(`/api/dish/getByMerchant/${merchantID}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        this.dishes = res.data
      } catch (error) {
        this.$message.error('获取菜品列表失败')
      }
    },
    handleEdit(id) {
      this.$router.push(`/dish/edit/${id}`)
    },
    goToAdd() {
      this.$router.push('/dish/add')
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确认删除该菜品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await this.$axios.delete(`/api/dish/delete/${id}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })

        this.$message.success('删除成功')
        await this.fetchDishes()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.dish-management {
  padding: 40px;
  max-width: 1000px;
  margin: 0 auto;
}

.container {
  padding: 30px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.el-button--mini {
  padding: 5px 10px;
}
</style>
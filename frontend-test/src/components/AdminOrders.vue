<template>
  <div class="admin-orders-container">
    <AdminNavBar />
    <h2>订单管理</h2>

    <!-- 订单列表 -->
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="订单号"></el-table-column>
      <el-table-column prop="content" label="内容"></el-table-column>
      <el-table-column label="顾客信息">
        <template slot-scope="scope">
          <div v-if="scope.row.customer">
            <p>姓名: {{ scope.row.customer.name }}</p>
            <p>年龄: {{ scope.row.customer.age }}</p>
            <p>地址: {{ scope.row.customer.address }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="state" label="状态"></el-table-column>
      <el-table-column prop="completionTime" label="完成时间">
        <template slot-scope="scope">
          {{ formatTime(scope.row.completionTime) }}
        </template>
      </el-table-column>
      <el-table-column label="配送员">
        <template slot-scope="scope">
          <el-select
              v-model="scope.row.deliverID"
              :disabled="!isEditable(scope.row.state)"
              @change="updateDeliver(scope.row)"
          >
            <el-option
                v-for="deliver in delivers"
                :key="deliver.workId"
                :label="deliver.name"
                :value="deliver.workId"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import AdminNavBar from './AdminNavBar.vue';
import axios from 'axios';

export default {
  components: { AdminNavBar },
  data() {
    return {
      orders: [],
      delivers: []
    };
  },
  async mounted() {
    await this.fetchOrders();
    await this.fetchDelivers();
  },
  methods: {
    isEditable(state) {
      return ['Pending', 'Delivering'].includes(state);
    },
    formatTime(time) {
      if (!time) return '未完成';
      return new Date(time).toLocaleString();
    },
    async fetchOrders() {
      try {
        const res = await axios.get('/api/indent', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        this.orders = await Promise.all(res.data.map(async order => {
          const customerRes = await axios.get(`/api/customer/${order.customerID}`);
          return { ...order, customer: customerRes.data };
        }));
      } catch (error) {
        console.error('获取订单失败:', error);
      }
    },
    async fetchDelivers() {
      try {
        const res = await axios.get('/api/deliver');
        this.delivers = res.data;
      } catch (error) {
        console.error('获取骑手列表失败:', error);
      }
    },
    async updateDeliver(order) {
      if (!this.isEditable(order.state)) return;

      try {
        await axios.put(`/api/indent/change-deliver/${order.id}`, {
          deliverID: order.deliverID
        }, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        this.$message.success('配送员更新成功');
        await this.fetchOrders();
      } catch (error) {
        this.$message.error('更新失败: ' + (error.response?.data || error.message));
      }
    }

  }
};
</script>

<style scoped>
.admin-orders-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 60px 2rem 2rem;
}
</style>
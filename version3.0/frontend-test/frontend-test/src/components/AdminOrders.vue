<template>
  <div class="admin-orders-container">
    <AdminNavBar />
    <h2>订单管理</h2>


    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="订单号"></el-table-column>
      <el-table-column prop="content" label="内容"></el-table-column>
      <el-table-column label="顾客信息">
        <template slot-scope="scope">
          <div v-if="scope.row.customer">
            <p>姓名: {{ scope.row.customer.name }}</p>
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
      <el-table-column label="商家电话">
        <template slot-scope="scope">
          {{ scope.row.merchantPhone }}
        </template>
      </el-table-column>
      <el-table-column label="顾客电话">
        <template slot-scope="scope">
          {{ scope.row.customerPhone }}
        </template>
      </el-table-column>
      <el-table-column label="骑手电话">
        <template slot-scope="scope">
          {{ scope.row.deliverPhone || '未分配' }}
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
      delivers: [],
      refreshTimer: null
    };
  },
  async mounted() {
    await this.fetchOrders();
    this.refreshTimer = setInterval(async () => {
      await this.fetchOrders();
      await this.fetchDelivers();
    }, 60000);
    await this.fetchDelivers();
  },
  beforeDestroy() {
    clearInterval(this.refreshTimer);
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

        this.orders = await Promise.all(
            res.data.map(async order => {
              try {
                const [customerRes, merchantRes, deliverRes] = await Promise.all([
                  axios.get(`/api/customer/${order.customerID}`),
                  axios.get(`/api/merchant/phonenumber/${order.merchantID}`),
                  order.deliverID ?
                      axios.get(`/api/deliver/phonenumber/${order.deliverID}`)
                      : Promise.resolve({ data: null })
                ]);

                return {
                  ...order,
                  customer: customerRes.data,
                  customerPhone: customerRes.data.phonenumber, // 新增此行
                  merchantPhone: merchantRes.data,
                  deliverPhone: deliverRes?.data || '未分配'
                };
              } catch (error) {
                console.error(`获取订单${order.id}电话信息失败:`, error);
                return {
                  ...order,
                  customer: {},
                  customerPhone: '获取失败', // 新增失败处理
                  merchantPhone: '获取失败',
                  deliverPhone: '获取失败'
                };
              }
            })
        );
      } catch (error) {
        console.error('获取订单失败:', error);
        this.$message.error('订单数据加载失败');
      }
    },
    async fetchDelivers() {
      try {
        const res = await axios.get('/api/deliver');
        this.delivers = res.data;
      } catch (error) {
        console.error('获取骑手列表失败:', error);
        this.$message.error('骑手列表加载失败');
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
        const errorMsg = error.response?.data?.error || error.message;
        console.error('更新失败:', errorMsg);
        this.$message.error(`更新失败: ${errorMsg}`);
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
<template>
  <div class="work-order-management">
    <el-card>
      <div slot="header" class="clearfix">
        <span>Work Order Management</span>
        <el-button style="float: right" type="primary" size="small">Create Work Order</el-button>
      </div>
      
      <!-- Search and Filter -->
      <div class="filter-container">
        <el-input
          v-model="listQuery.search"
          placeholder="Work Order ID/Rider Name"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleFilter"
          clearable
        />
        <el-select
          v-model="listQuery.status"
          placeholder="Order Status"
          clearable
          style="width: 120px"
          class="filter-item"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-model="listQuery.type"
          placeholder="Order Type"
          clearable
          style="width: 120px"
          class="filter-item"
        >
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-search"
          @click="handleFilter"
        >
          Search
        </el-button>
        <el-button
          class="filter-item"
          type="info"
          icon="el-icon-refresh"
          @click="resetFilter"
        >
          Reset
        </el-button>
      </div>
      
      <!-- Work Order List -->
      <el-table
        :data="workOrderList"
        border
        style="width: 100%"
        v-loading="listLoading"
      >
        <el-table-column prop="id" label="Work Order ID" width="120" align="center" />
        <el-table-column prop="title" label="Work Order Title" min-width="200" />
        <el-table-column prop="type" label="Type" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rider" label="Rider" width="100" align="center" />
        <el-table-column prop="createTime" label="Created Time" width="150" align="center" />
        <el-table-column prop="updateTime" label="Updated Time" width="150" align="center" />
        <el-table-column prop="status" label="Status" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleView(scope.row)"
            >
              View
            </el-button>
            <el-button
              size="mini"
              type="primary"
              @click="handleProcess(scope.row)"
              :disabled="scope.row.status === 'Closed'"
            >
              Process
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="listQuery.page"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="listQuery.limit"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'WorkOrderManagement',
  data() {
    return {
      listLoading: false,
      workOrderList: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        search: '',
        status: '',
        type: ''
      },
      statusOptions: [
        { label: 'Pending', value: 'Pending' },
        { label: 'Processing', value: 'Processing' },
        { label: 'Resolved', value: 'Resolved' },
        { label: 'Closed', value: 'Closed' }
      ],
      typeOptions: [
        { label: 'Device Issue', value: 'Device Issue' },
        { label: 'System Failure', value: 'System Failure' },
        { label: 'Account Problem', value: 'Account Problem' },
        { label: 'Order Dispute', value: 'Order Dispute' },
        { label: 'Other Issue', value: 'Other Issue' }
      ]
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      
      // Simulate API request
      setTimeout(() => {
        // Mock data
        const mockData = [
          { id: 'WO20230601', title: 'Delivery Device Location Error', type: 'Device Issue', rider: 'Zhang San', createTime: '2023-06-01 09:15', updateTime: '2023-06-01 10:30', status: 'Resolved' },
          { id: 'WO20230602', title: 'Rider App Login Failure', type: 'System Failure', rider: 'Li Si', createTime: '2023-06-02 11:20', updateTime: '2023-06-02 14:45', status: 'Closed' },
          { id: 'WO20230603', title: 'Salary Settlement Abnormality', type: 'Account Problem', rider: 'Wang Wu', createTime: '2023-06-03 08:30', updateTime: '2023-06-03 08:30', status: 'Pending' },
          { id: 'WO20230604', title: 'Customer Complaint About Delivery Delay', type: 'Order Dispute', rider: 'Zhao Liu', createTime: '2023-06-04 15:40', updateTime: '2023-06-04 16:20', status: 'Processing' },
          { id: 'WO20230605', title: 'Leave Request Approval', type: 'Other Issue', rider: 'Qian Qi', createTime: '2023-06-05 10:15', updateTime: '2023-06-05 11:00', status: 'Resolved' }
        ]
        
        // Filter data
        let filtered = [...mockData]
        
        if (this.listQuery.search) {
          const search = this.listQuery.search.toLowerCase()
          filtered = filtered.filter(item => 
            item.id.toLowerCase().includes(search) || 
            item.rider.toLowerCase().includes(search)
          )
        }
        
        if (this.listQuery.status) {
          filtered = filtered.filter(item => item.status === this.listQuery.status)
        }
        
        if (this.listQuery.type) {
          filtered = filtered.filter(item => item.type === this.listQuery.type)
        }
        
        this.total = filtered.length
        
        // Pagination
        const start = (this.listQuery.page - 1) * this.listQuery.limit
        const end = start + this.listQuery.limit
        this.workOrderList = filtered.slice(start, end)
        
        this.listLoading = false
      }, 500)
    },
    
    // Handle search
    handleFilter() {
      this.listQuery.page = 1
      this.fetchData()
    },
    
    // Reset search
    resetFilter() {
      this.listQuery = {
        page: 1,
        limit: 10,
        search: '',
        status: '',
        type: ''
      }
      this.fetchData()
    },
    
    // Handle page size change
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.fetchData()
    },
    
    // Handle page number change
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.fetchData()
    },
    
    // Get status tag type
    getStatusTagType(status) {
      const statusMap = {
        'Pending': 'danger',
        'Processing': 'warning',
        'Resolved': 'success',
        'Closed': 'info'
      }
      return statusMap[status] || 'info'
    },
    
    // Get work order type tag
    getTypeTagType(type) {
      const typeMap = {
        'Device Issue': 'primary',
        'System Failure': 'danger',
        'Account Problem': 'warning',
        'Order Dispute': 'info',
        'Other Issue': 'success'
      }
      return typeMap[type] || 'info'
    },
    
    // View work order details
    handleView(row) {
      // Implementation for viewing work order details
      console.log('View work order:', row)
    },
    
    // Process work order
    handleProcess(row) {
      // Implementation for processing work order
      console.log('Process work order:', row)
    }
  }
}
</script>

<style scoped>
.work-order-management {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 
<template>
  <div class="work-order-management">
    <el-card>
      <div slot="header" class="clearfix">
        <span>工单管理</span>
        <el-button style="float: right" type="primary" size="small">创建工单</el-button>
      </div>
      
      <!-- 搜索和筛选 -->
      <div class="filter-container">
        <el-input
          v-model="listQuery.search"
          placeholder="工单编号/骑手名称"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleFilter"
          clearable
        />
        <el-select
          v-model="listQuery.status"
          placeholder="工单状态"
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
          placeholder="工单类型"
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
          搜索
        </el-button>
        <el-button
          class="filter-item"
          type="info"
          icon="el-icon-refresh"
          @click="resetFilter"
        >
          重置
        </el-button>
      </div>
      
      <!-- 工单列表 -->
      <el-table
        :data="workOrderList"
        border
        style="width: 100%"
        v-loading="listLoading"
      >
        <el-table-column prop="id" label="工单编号" width="120" align="center" />
        <el-table-column prop="title" label="工单标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rider" label="骑手" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="150" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="150" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="mini"
              type="primary"
              @click="handleProcess(scope.row)"
              :disabled="scope.row.status === '已关闭'"
            >
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
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
        { label: '未处理', value: '未处理' },
        { label: '处理中', value: '处理中' },
        { label: '已处理', value: '已处理' },
        { label: '已关闭', value: '已关闭' }
      ],
      typeOptions: [
        { label: '设备问题', value: '设备问题' },
        { label: '系统故障', value: '系统故障' },
        { label: '账户问题', value: '账户问题' },
        { label: '订单纠纷', value: '订单纠纷' },
        { label: '其他问题', value: '其他问题' }
      ]
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      
      // 模拟API请求
      setTimeout(() => {
        // 假数据
        const mockData = [
          { id: 'WO20230601', title: '配送设备定位异常', type: '设备问题', rider: '张三', createTime: '2023-06-01 09:15', updateTime: '2023-06-01 10:30', status: '已处理' },
          { id: 'WO20230602', title: '骑手App无法登录', type: '系统故障', rider: '李四', createTime: '2023-06-02 11:20', updateTime: '2023-06-02 14:45', status: '已关闭' },
          { id: 'WO20230603', title: '工资结算异常', type: '账户问题', rider: '王五', createTime: '2023-06-03 08:30', updateTime: '2023-06-03 08:30', status: '未处理' },
          { id: 'WO20230604', title: '顾客投诉送餐延迟', type: '订单纠纷', rider: '赵六', createTime: '2023-06-04 15:40', updateTime: '2023-06-04 16:20', status: '处理中' },
          { id: 'WO20230605', title: '申请休假审批', type: '其他问题', rider: '钱七', createTime: '2023-06-05 10:15', updateTime: '2023-06-05 11:00', status: '已处理' }
        ]
        
        // 过滤数据
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
        
        // 分页
        const start = (this.listQuery.page - 1) * this.listQuery.limit
        const end = start + this.listQuery.limit
        this.workOrderList = filtered.slice(start, end)
        
        this.listLoading = false
      }, 500)
    },
    
    // 处理搜索
    handleFilter() {
      this.listQuery.page = 1
      this.fetchData()
    },
    
    // 重置搜索
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
    
    // 处理分页大小变化
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.fetchData()
    },
    
    // 处理页码变化
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.fetchData()
    },
    
    // 获取状态标签类型
    getStatusTagType(status) {
      const statusMap = {
        '未处理': 'danger',
        '处理中': 'warning',
        '已处理': 'success',
        '已关闭': 'info'
      }
      return statusMap[status] || 'info'
    },
    
    // 获取工单类型标签
    getTypeTagType(type) {
      const typeMap = {
        '设备问题': 'primary',
        '系统故障': 'danger',
        '账户问题': 'warning',
        '订单纠纷': 'info',
        '其他问题': ''
      }
      return typeMap[type] || ''
    },
    
    // 查看工单
    handleView(row) {
      this.$message.info('查看工单详情功能待开发')
      console.log('查看工单', row)
    },
    
    // 处理工单
    handleProcess(row) {
      this.$message.info('处理工单功能待开发')
      console.log('处理工单', row)
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
  display: flex;
  flex-wrap: wrap;
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
<template>
  <div class="rider-management">
    <el-card>
      <div slot="header" class="clearfix">
        <span>骑手管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAddRider">添加骑手</el-button>
      </div>
      
      <!-- 搜索和筛选 -->
      <div class="filter-container">
        <el-input
          v-model="listQuery.search"
          placeholder="搜索骑手姓名/手机号"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleFilter"
          clearable
        />
        <el-select
          v-model="listQuery.status"
          placeholder="骑手状态"
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
          v-model="listQuery.performance"
          placeholder="绩效评级"
          clearable
          style="width: 120px"
          class="filter-item"
        >
          <el-option
            v-for="item in performanceOptions"
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
      
      <!-- 骑手列表 -->
      <el-table
        :data="riderList"
        border
        style="width: 100%"
        v-loading="listLoading"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="骑手信息" min-width="200">
          <template slot-scope="scope">
            <div class="rider-info">
              <img :src="scope.row.avatar" class="rider-avatar">
              <div class="rider-detail">
                <div class="rider-name">{{ scope.row.name }}</div>
                <div class="rider-contact">{{ scope.row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '在线' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinDate" label="入职日期" width="120" align="center" />
        <el-table-column prop="orders" label="订单数" width="100" align="center" />
        <el-table-column prop="rating" label="评分" width="100" align="center">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.rating"
              disabled
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column prop="performance" label="绩效" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getPerformanceType(scope.row.performance)">
              {{ scope.row.performance }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewRider(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="mini"
              type="primary"
              @click="handleEditRider(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDeleteRider(scope.row)"
            >
              删除
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
    
    <!-- 添加/编辑骑手对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
      <el-form :model="riderForm" :rules="riderRules" ref="riderForm" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="riderForm.name" placeholder="请输入骑手姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="riderForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="riderForm.status" placeholder="请选择状态">
            <el-option label="在线" value="在线"></el-option>
            <el-option label="离线" value="离线"></el-option>
            <el-option label="休假" value="休假"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="绩效" prop="performance">
          <el-select v-model="riderForm.performance" placeholder="请选择绩效">
            <el-option label="优秀" value="优秀"></el-option>
            <el-option label="良好" value="良好"></el-option>
            <el-option label="一般" value="一般"></el-option>
            <el-option label="较差" value="较差"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="joinDate">
          <el-date-picker
            v-model="riderForm.joinDate"
            type="date"
            placeholder="选择日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitRiderForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'RiderManagement',
  data() {
    // 自定义校验规则 - 手机号
    const validatePhone = (rule, value, callback) => {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }
    
    return {
      listLoading: false,
      riderList: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        search: '',
        status: '',
        performance: ''
      },
      statusOptions: [
        { label: '在线', value: '在线' },
        { label: '离线', value: '离线' },
        { label: '休假', value: '休假' }
      ],
      performanceOptions: [
        { label: '优秀', value: '优秀' },
        { label: '良好', value: '良好' },
        { label: '一般', value: '一般' },
        { label: '较差', value: '较差' }
      ],
      dialogVisible: false,
      dialogTitle: '添加骑手',
      riderForm: {
        id: null,
        name: '',
        phone: '',
        status: '在线',
        performance: '良好',
        joinDate: new Date().toISOString().substr(0, 10)
      },
      riderRules: {
        name: [
          { required: true, message: '请输入骑手姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ],
        performance: [
          { required: true, message: '请选择绩效', trigger: 'change' }
        ],
        joinDate: [
          { required: true, message: '请选择入职日期', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.fetchRiderList()
  },
  methods: {
    // 获取骑手列表数据
    fetchRiderList() {
      this.listLoading = true
      
      // 从本地存储获取数据，如果没有则使用默认数据
      const savedRiders = localStorage.getItem('ridersList');
      let ridersList = [];
      
      if (savedRiders) {
        try {
          ridersList = JSON.parse(savedRiders);
          console.log('已从本地存储加载骑手列表:', ridersList);
        } catch (error) {
          console.error('解析骑手数据失败:', error);
          ridersList = this.getDefaultRiders();
        }
      } else {
        ridersList = this.getDefaultRiders();
        // 首次加载，保存默认数据到本地存储
        localStorage.setItem('ridersList', JSON.stringify(ridersList));
      }
      
      // 过滤数据
      let filtered = [...ridersList];
      
      if (this.listQuery.search) {
        const search = this.listQuery.search.toLowerCase();
        filtered = filtered.filter(rider => 
          rider.name.toLowerCase().includes(search) || 
          rider.phone.includes(search)
        );
      }
      
      if (this.listQuery.status) {
        filtered = filtered.filter(rider => rider.status === this.listQuery.status);
      }
      
      if (this.listQuery.performance) {
        filtered = filtered.filter(rider => rider.performance === this.listQuery.performance);
      }
      
      this.total = filtered.length;
      
      // 分页
      const start = (this.listQuery.page - 1) * this.listQuery.limit;
      const end = start + this.listQuery.limit;
      this.riderList = filtered.slice(start, end);
      
      setTimeout(() => {
        this.listLoading = false;
      }, 300);
    },
    
    // 获取默认骑手数据
    getDefaultRiders() {
      return [
        { id: '001', name: '张三', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', phone: '13800138000', status: '在线', joinDate: '2022-09-15', orders: 356, rating: 4.8, performance: '优秀' },
        { id: '002', name: '李四', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', phone: '13900139000', status: '在线', joinDate: '2022-10-20', orders: 289, rating: 4.5, performance: '良好' },
        { id: '003', name: '王五', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', phone: '13700137000', status: '离线', joinDate: '2022-11-05', orders: 210, rating: 4.6, performance: '良好' },
        { id: '004', name: '赵六', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', phone: '13600136000', status: '在线', joinDate: '2023-01-12', orders: 178, rating: 4.2, performance: '一般' },
        { id: '005', name: '钱七', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', phone: '13500135000', status: '休假', joinDate: '2023-02-18', orders: 145, rating: 4.7, performance: '良好' }
      ];
    },
    
    // 更新本地存储中的骑手列表
    updateLocalStorage() {
      // 获取当前完整的骑手列表（包括分页中没有显示的）
      const savedRiders = localStorage.getItem('ridersList');
      let allRiders = [];
      
      if (savedRiders) {
        try {
          allRiders = JSON.parse(savedRiders);
        } catch (error) {
          console.error('解析骑手数据失败:', error);
          return;
        }
      }
      
      // 合并当前页面的更改到完整列表
      if (this.riderList.length > 0) {
        this.riderList.forEach(rider => {
          const index = allRiders.findIndex(r => r.id === rider.id);
          if (index !== -1) {
            allRiders[index] = { ...rider };
          } else {
            allRiders.push({ ...rider });
          }
        });
      }
      
      // 保存更新后的完整列表
      localStorage.setItem('ridersList', JSON.stringify(allRiders));
      console.log('骑手列表已更新到本地存储:', allRiders);
    },
    
    // 处理搜索
    handleFilter() {
      this.listQuery.page = 1
      this.fetchRiderList()
    },
    
    // 重置搜索
    resetFilter() {
      this.listQuery = {
        page: 1,
        limit: 10,
        search: '',
        status: '',
        performance: ''
      }
      this.fetchRiderList()
    },
    
    // 处理分页大小变化
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.fetchRiderList()
    },
    
    // 处理页码变化
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.fetchRiderList()
    },
    
    // 获取绩效标签类型
    getPerformanceType(performance) {
      const typeMap = {
        '优秀': 'success',
        '良好': 'primary',
        '一般': 'warning',
        '较差': 'danger'
      }
      return typeMap[performance] || 'info'
    },
    
    // 查看骑手
    handleViewRider(row) {
      this.$message.info('查看骑手详情功能待开发')
      console.log('查看骑手', row)
    },
    
    // 添加骑手
    handleAddRider() {
      this.dialogTitle = '添加骑手'
      this.riderForm = {
        id: null,
        name: '',
        phone: '',
        status: '在线',
        performance: '良好',
        joinDate: new Date().toISOString().substr(0, 10)
      }
      this.dialogVisible = true
      
      // 等待DOM更新后重置表单验证状态
      this.$nextTick(() => {
        this.$refs.riderForm && this.$refs.riderForm.clearValidate()
      })
    },
    
    // 编辑骑手
    handleEditRider(row) {
      this.dialogTitle = '编辑骑手'
      this.riderForm = Object.assign({}, row)
      this.dialogVisible = true
      
      // 等待DOM更新后重置表单验证状态
      this.$nextTick(() => {
        this.$refs.riderForm && this.$refs.riderForm.clearValidate()
      })
    },
    
    // 删除骑手
    handleDeleteRider(row) {
      this.$confirm('此操作将永久删除该骑手, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 实际项目中应该调用API进行删除
        setTimeout(() => {
          this.riderList = this.riderList.filter(item => item.id !== row.id)
          this.total--
          this.updateLocalStorage()
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
        }, 500)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })          
      })
    },
    
    // 提交骑手表单
    submitRiderForm() {
      this.$refs.riderForm.validate(valid => {
        if (valid) {
          // 模拟提交
          setTimeout(() => {
            if (this.riderForm.id) {
              // 编辑
              const index = this.riderList.findIndex(item => item.id === this.riderForm.id)
              if (index !== -1) {
                // 保留不在表单中的字段
                const { avatar, orders, rating } = this.riderList[index]
                this.riderList[index] = Object.assign({}, this.riderForm, { avatar, orders, rating })
              }
              this.updateLocalStorage()
              this.$message.success('编辑骑手成功')
            } else {
              // 添加
              const newRider = Object.assign({}, this.riderForm, {
                id: Date.now().toString().substr(-3),
                avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                orders: 0,
                rating: 5.0
              })
              this.riderList.unshift(newRider)
              this.total++
              this.updateLocalStorage()
              this.$message.success('添加骑手成功')
            }
            
            this.dialogVisible = false
          }, 500)
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.rider-management {
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
.rider-info {
  display: flex;
  align-items: center;
}
.rider-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}
.rider-detail {
  display: flex;
  flex-direction: column;
}
.rider-name {
  font-weight: 500;
  color: #303133;
}
.rider-contact {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
.dialog-footer {
  text-align: right;
}
</style> 
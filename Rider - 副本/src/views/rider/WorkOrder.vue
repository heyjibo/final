<template>
  <div class="work-order-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>我的工单</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="fetchWorkOrders">刷新</el-button>
          </div>
          
          <!-- 工单表格 -->
          <el-table :data="workOrders" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="工单号" width="100"></el-table-column>
            <el-table-column prop="title" label="工单标题" width="200"></el-table-column>
            <el-table-column prop="type" label="工单类型" width="120">
              <template slot-scope="scope">
                <el-tag :type="getOrderTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="100">
              <template slot-scope="scope">
                <el-tag :type="getPriorityTag(scope.row.priority)">{{ scope.row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button size="mini" @click="viewWorkOrder(scope.row)">查看</el-button>
                <el-button size="mini" type="primary" @click="updateWorkOrder(scope.row)" v-if="scope.row.status !== '已完成'">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-sizes="[5, 10, 20, 50]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>工单统计</span>
          </div>
          <div id="work-order-chart" style="height: 300px;"></div>
        </el-card>
        
        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>快速操作</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" icon="el-icon-plus" @click="createWorkOrder">创建工单</el-button>
            <el-button type="success" icon="el-icon-refresh" @click="fetchWorkOrders">刷新工单</el-button>
            <el-button type="warning" icon="el-icon-document" @click="exportWorkOrders">导出工单</el-button>
          </div>
          
          <el-divider content-position="center">筛选器</el-divider>
          
          <el-form :model="filterForm" size="small">
            <el-form-item label="工单类型">
              <el-select v-model="filterForm.type" placeholder="全部类型" style="width: 100%;" @change="applyFilter">
                <el-option label="全部类型" value=""></el-option>
                <el-option label="配送异常" value="配送异常"></el-option>
                <el-option label="客户投诉" value="客户投诉"></el-option>
                <el-option label="设备故障" value="设备故障"></el-option>
                <el-option label="系统问题" value="系统问题"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="工单状态">
              <el-select v-model="filterForm.status" placeholder="全部状态" style="width: 100%;" @change="applyFilter">
                <el-option label="全部状态" value=""></el-option>
                <el-option label="待处理" value="待处理"></el-option>
                <el-option label="处理中" value="处理中"></el-option>
                <el-option label="已完成" value="已完成"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%;"
                @change="applyFilter">
              </el-date-picker>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="applyFilter" style="width: 100%;">应用筛选</el-button>
              <el-button @click="resetFilter" style="width: 100%; margin-top: 10px;">重置筛选</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 工单详情对话框 -->
    <el-dialog title="工单详情" :visible.sync="detailDialogVisible" width="60%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单号">{{ selectedOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="工单类型">
          <el-tag :type="getOrderTypeTag(selectedOrder.type)">{{ selectedOrder.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="工单状态">
          <el-tag :type="getStatusTag(selectedOrder.status)">{{ selectedOrder.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityTag(selectedOrder.priority)">{{ selectedOrder.priority }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联订单" v-if="selectedOrder.relatedOrder">{{ selectedOrder.relatedOrder }}</el-descriptions-item>
        <el-descriptions-item label="工单标题" :span="2">{{ selectedOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="工单描述" :span="2">{{ selectedOrder.description }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="center">处理记录</el-divider>
      
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in selectedOrder.activities"
          :key="index"
          :timestamp="activity.time"
          :type="activity.type">
          <el-card>
            <h4>{{ activity.title }}</h4>
            <p>{{ activity.content }}</p>
            <div v-if="activity.operator">
              <small>操作人: {{ activity.operator }}</small>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
    
    <!-- 创建工单对话框 -->
    <el-dialog title="创建工单" :visible.sync="createDialogVisible" width="50%">
      <el-form :model="workOrderForm" :rules="workOrderRules" ref="workOrderForm" label-width="100px">
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="workOrderForm.title" placeholder="请输入工单标题"></el-input>
        </el-form-item>
        
        <el-form-item label="工单类型" prop="type">
          <el-select v-model="workOrderForm.type" placeholder="请选择工单类型" style="width: 100%;">
            <el-option label="配送异常" value="配送异常"></el-option>
            <el-option label="客户投诉" value="客户投诉"></el-option>
            <el-option label="设备故障" value="设备故障"></el-option>
            <el-option label="系统问题" value="系统问题"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="workOrderForm.priority" placeholder="请选择优先级" style="width: 100%;">
            <el-option label="低" value="低"></el-option>
            <el-option label="中" value="中"></el-option>
            <el-option label="高" value="高"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="关联订单">
          <el-select v-model="workOrderForm.relatedOrder" filterable placeholder="请选择关联订单" style="width: 100%;">
            <el-option
              v-for="item in orderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="工单描述" prop="description">
          <el-input type="textarea" v-model="workOrderForm.description" :rows="4" placeholder="请输入工单描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitWorkOrderForm">确 定</el-button>
      </div>
    </el-dialog>
    
    <!-- 处理工单对话框 -->
    <el-dialog title="处理工单" :visible.sync="updateDialogVisible" width="50%">
      <el-form :model="updateForm" :rules="updateRules" ref="updateForm" label-width="100px">
        <el-form-item label="工单状态" prop="status">
          <el-select v-model="updateForm.status" placeholder="请选择工单状态" style="width: 100%;">
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="处理记录" prop="content">
          <el-input type="textarea" v-model="updateForm.content" :rows="4" placeholder="请输入处理记录"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitUpdateForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'WorkOrder',
  data() {
    return {
      loading: false,
      workOrders: [],
      currentPage: 1,
      pageSize: 5,
      total: 0,
      workOrderChart: null,
      
      // 筛选表单
      filterForm: {
        type: '',
        status: '',
        dateRange: null
      },
      
      // 工单详情对话框
      detailDialogVisible: false,
      selectedOrder: {
        activities: []
      },
      
      // 创建工单对话框
      createDialogVisible: false,
      workOrderForm: {
        title: '',
        type: '',
        priority: '中',
        relatedOrder: '',
        description: ''
      },
      workOrderRules: {
        title: [
          { required: true, message: '请输入工单标题', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择工单类型', trigger: 'change' }
        ],
        priority: [
          { required: true, message: '请选择优先级', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入工单描述', trigger: 'blur' },
          { min: 10, message: '描述至少10个字符', trigger: 'blur' }
        ]
      },
      
      // 处理工单对话框
      updateDialogVisible: false,
      updateForm: {
        id: '',
        status: '',
        content: ''
      },
      updateRules: {
        status: [
          { required: true, message: '请选择工单状态', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入处理记录', trigger: 'blur' },
          { min: 5, message: '处理记录至少5个字符', trigger: 'blur' }
        ]
      },
      
      // 订单选项
      orderOptions: []
    }
  },
  mounted() {
    this.fetchWorkOrders()
    this.fetchOrderOptions()
    this.initWorkOrderChart()
  },
  beforeDestroy() {
    if (this.workOrderChart) {
      this.workOrderChart.dispose()
    }
  },
  methods: {
    // 初始化工单统计图表
    initWorkOrderChart() {
      this.workOrderChart = echarts.init(document.getElementById('work-order-chart'))
      
      const option = {
        title: {
          text: '工单状态统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['待处理', '处理中', '已完成']
        },
        series: [
          {
            name: '工单状态',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
              { value: 3, name: '待处理' },
              { value: 2, name: '处理中' },
              { value: 5, name: '已完成' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      
      this.workOrderChart.setOption(option)
      
      // 监听窗口变化，更新图表大小
      window.addEventListener('resize', () => {
        this.workOrderChart.resize()
      })
    },
    // 获取订单选项
    fetchOrderOptions() {
      // 模拟API请求
      setTimeout(() => {
        this.orderOptions = [
          { value: 'O2023041501', label: 'O2023041501 - 浙江大学玉泉校区18号楼' },
          { value: 'O2023041502', label: 'O2023041502 - 黄龙体育中心' },
          { value: 'O2023041503', label: 'O2023041503 - 西湖文化广场' },
          { value: 'O2023041504', label: 'O2023041504 - 杭州东站' }
        ]
      }, 500)
    },
    // 获取工单列表
    fetchWorkOrders() {
      this.loading = true
      
      // 从localStorage获取工单列表，如果没有则使用默认数据
      const savedWorkOrders = localStorage.getItem('workOrders');
      let workOrders = [];
      
      if (savedWorkOrders) {
        try {
          workOrders = JSON.parse(savedWorkOrders);
          console.log('已从本地存储加载工单列表:', workOrders);
        } catch (error) {
          console.error('解析工单数据失败:', error);
          workOrders = this.getDefaultWorkOrders();
        }
      } else {
        workOrders = this.getDefaultWorkOrders();
        // 首次加载，保存默认数据到本地存储
        localStorage.setItem('workOrders', JSON.stringify(workOrders));
      }
      
      // 过滤数据
      let filtered = [...workOrders];
      
      // 应用筛选条件
      if (this.filterForm.type) {
        filtered = filtered.filter(order => order.type === this.filterForm.type);
      }
      
      if (this.filterForm.status) {
        filtered = filtered.filter(order => order.status === this.filterForm.status);
      }
      
      if (this.filterForm.dateRange && this.filterForm.dateRange.length === 2) {
        const startDate = new Date(this.filterForm.dateRange[0]).getTime();
        const endDate = new Date(this.filterForm.dateRange[1]).getTime();
        filtered = filtered.filter(order => {
          const orderDate = new Date(order.createTime).getTime();
          return orderDate >= startDate && orderDate <= endDate;
        });
      }
      
      this.total = filtered.length;
      
      // 分页
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.workOrders = filtered.slice(start, end);
      
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
    // 获取默认工单数据
    getDefaultWorkOrders() {
      return [
        {
          id: 'WO20230415001',
          title: '配送延迟投诉处理',
          type: '配送异常',
          priority: '中',
          status: '已完成',
          createTime: '2023-04-15 10:30:22',
          updateTime: '2023-04-15 14:20:30',
          relatedOrder: 'O2023041501',
          description: '客户反映订单配送时间过长，要求赔偿。',
          handler: '张三',
          logs: [
            {
              time: '2023-04-15 10:30:22',
              title: '创建工单',
              content: '系统自动创建工单',
              operator: '系统',
              type: 'primary'
            },
            {
              time: '2023-04-15 11:15:40',
              title: '开始处理',
              content: '已联系客户，正在协商解决方案。',
              operator: '张三',
              type: 'warning'
            },
            {
              time: '2023-04-15 14:20:30',
              title: '完成处理',
              content: '已向客户道歉并提供代金券赔偿，客户已接受。',
              operator: '张三',
              type: 'success'
            }
          ]
        },
        {
          id: 'WO20230414001',
          title: '设备GPS定位异常',
          type: '设备故障',
          priority: '高',
          status: '处理中',
          createTime: '2023-04-14 09:15:10',
          updateTime: '2023-04-14 15:40:22',
          relatedOrder: '',
          description: 'GPS定位信号不稳定，影响配送路线规划。',
          handler: '李四',
          logs: [
            {
              time: '2023-04-14 09:15:10',
              title: '创建工单',
              content: '手动创建工单',
              operator: '李四',
              type: 'primary'
            },
            {
              time: '2023-04-14 10:30:45',
              title: '开始处理',
              content: '正在检查GPS模块。',
              operator: '李四',
              type: 'warning'
            }
          ]
        },
        {
          id: 'WO20230413001',
          title: '订单显示异常',
          type: '系统问题',
          priority: '低',
          status: '待处理',
          createTime: '2023-04-13 16:45:30',
          updateTime: '2023-04-13 16:45:30',
          relatedOrder: 'O2023041302',
          description: '接单页面显示订单信息不完整。',
          handler: '',
          logs: [
            {
              time: '2023-04-13 16:45:30',
              title: '创建工单',
              content: '手动创建工单',
              operator: '王五',
              type: 'primary'
            }
          ]
        },
        {
          id: 'WO20230412001',
          title: '充电器故障',
          type: '设备故障',
          priority: '中',
          status: '已完成',
          createTime: '2023-04-12 08:30:20',
          updateTime: '2023-04-12 11:20:15',
          relatedOrder: '',
          description: '设备充电器无法正常充电。',
          handler: '赵六',
          logs: [
            {
              time: '2023-04-12 08:30:20',
              title: '创建工单',
              content: '手动创建工单',
              operator: '赵六',
              type: 'primary'
            },
            {
              time: '2023-04-12 09:45:30',
              title: '开始处理',
              content: '检查充电器和设备接口。',
              operator: '赵六',
              type: 'warning'
            },
            {
              time: '2023-04-12 11:20:15',
              title: '完成处理',
              content: '已更换充电器，问题解决。',
              operator: '赵六',
              type: 'success'
            }
          ]
        },
        {
          id: 'WO20230411001',
          title: '客户投诉处理',
          type: '客户投诉',
          priority: '高',
          status: '已完成',
          createTime: '2023-04-11 12:10:05',
          updateTime: '2023-04-11 15:30:45',
          relatedOrder: 'O2023041105',
          description: '客户投诉餐品洒落。',
          handler: '张三',
          logs: [
            {
              time: '2023-04-11 12:10:05',
              title: '创建工单',
              content: '系统自动创建工单',
              operator: '系统',
              type: 'primary'
            },
            {
              time: '2023-04-11 13:20:30',
              title: '开始处理',
              content: '已联系客户，确认情况。',
              operator: '张三',
              type: 'warning'
            },
            {
              time: '2023-04-11 15:30:45',
              title: '完成处理',
              content: '已为客户重新配送，并赠送代金券。',
              operator: '张三',
              type: 'success'
            }
          ]
        }
      ];
    },
    // 查看工单详情
    viewWorkOrder(row) {
      this.selectedOrder = row
      this.detailDialogVisible = true
    },
    // 处理工单
    updateWorkOrder(row) {
      this.updateForm = {
        id: row.id,
        status: '处理中',
        content: ''
      }
      this.updateDialogVisible = true
    },
    // 创建工单
    createWorkOrder() {
      this.workOrderForm = {
        title: '',
        type: '',
        priority: '中',
        relatedOrder: '',
        description: ''
      }
      this.createDialogVisible = true
    },
    // 提交工单表单
    submitWorkOrderForm() {
      this.$refs.workOrderForm.validate((valid) => {
        if (valid) {
          // 从localStorage获取现有工单列表
          const savedWorkOrders = localStorage.getItem('workOrders');
          let workOrders = [];
          
          if (savedWorkOrders) {
            try {
              workOrders = JSON.parse(savedWorkOrders);
            } catch (error) {
              console.error('解析工单数据失败:', error);
              workOrders = this.getDefaultWorkOrders();
            }
          } else {
            workOrders = this.getDefaultWorkOrders();
          }
          
          // 创建新工单
          const now = new Date();
          const nowStr = now.toISOString().replace('T', ' ').substring(0, 19);
          const yearStr = now.getFullYear().toString();
          const monthStr = (now.getMonth() + 1).toString().padStart(2, '0');
          const dayStr = now.getDate().toString().padStart(2, '0');
          const orderCount = workOrders.filter(o => 
            o.id.includes(`WO${yearStr}${monthStr}${dayStr}`)
          ).length;
          
          const newOrderId = `WO${yearStr}${monthStr}${dayStr}${(orderCount + 1).toString().padStart(3, '0')}`;
          
          const newOrder = {
            id: newOrderId,
            title: this.workOrderForm.title,
            type: this.workOrderForm.type,
            priority: this.workOrderForm.priority,
            status: '待处理',
            createTime: nowStr,
            updateTime: nowStr,
            relatedOrder: this.workOrderForm.relatedOrder || '',
            description: this.workOrderForm.description,
            handler: '',
            logs: [
              {
                time: nowStr,
                title: '创建工单',
                content: '手动创建工单',
                operator: JSON.parse(localStorage.getItem('currentUser') || '{}').username || '系统',
                type: 'primary'
              }
            ]
          };
          
          // 添加到工单列表
          workOrders.unshift(newOrder);
          
          // 保存到localStorage
          localStorage.setItem('workOrders', JSON.stringify(workOrders));
          
          this.$message({
            type: 'success',
            message: '工单创建成功!'
          });
          this.createDialogVisible = false;
          this.fetchWorkOrders();
        } else {
          return false;
        }
      });
    },
    // 提交工单处理表单
    submitUpdateForm() {
      this.$refs.updateForm.validate((valid) => {
        if (valid) {
          // 从localStorage获取现有工单列表
          const savedWorkOrders = localStorage.getItem('workOrders');
          let workOrders = [];
          
          if (savedWorkOrders) {
            try {
              workOrders = JSON.parse(savedWorkOrders);
            } catch (error) {
              console.error('解析工单数据失败:', error);
              return;
            }
          } else {
            this.$message.error('工单数据不存在');
            return;
          }
          
          // 查找并更新工单
          const now = new Date();
          const nowStr = now.toISOString().replace('T', ' ').substring(0, 19);
          const index = workOrders.findIndex(order => order.id === this.updateForm.id);
          
          if (index !== -1) {
            const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}').username || '系统';
            
            // 添加处理日志
            const logTitle = this.updateForm.status === '已完成' ? '完成处理' : '处理更新';
            const logType = this.updateForm.status === '已完成' ? 'success' : 'warning';
            
            workOrders[index].logs.push({
              time: nowStr,
              title: logTitle,
              content: this.updateForm.content,
              operator: currentUser,
              type: logType
            });
            
            // 更新工单状态和处理人
            workOrders[index].status = this.updateForm.status;
            workOrders[index].updateTime = nowStr;
            if (!workOrders[index].handler) {
              workOrders[index].handler = currentUser;
            }
            
            // 保存回localStorage
            localStorage.setItem('workOrders', JSON.stringify(workOrders));
            
            this.$message({
              type: 'success',
              message: '工单更新成功!'
            });
            this.updateDialogVisible = false;
            this.fetchWorkOrders();
          } else {
            this.$message.error('未找到指定工单');
          }
        } else {
          return false;
        }
      });
    },
    // 导出工单
    exportWorkOrders() {
      this.$message({
        type: 'success',
        message: '工单导出成功!'
      })
    },
    // 应用筛选
    applyFilter() {
      this.fetchWorkOrders()
    },
    // 重置筛选
    resetFilter() {
      this.filterForm = {
        type: '',
        status: '',
        dateRange: null
      }
      this.fetchWorkOrders()
    },
    // 获取工单类型标签
    getOrderTypeTag(type) {
      const typeMap = {
        '配送异常': 'warning',
        '客户投诉': 'danger',
        '设备故障': 'info',
        '系统问题': 'primary'
      }
      return typeMap[type] || 'info'
    },
    // 获取优先级标签
    getPriorityTag(priority) {
      const priorityMap = {
        '低': 'info',
        '中': 'warning',
        '高': 'danger'
      }
      return priorityMap[priority] || 'info'
    },
    // 获取状态标签
    getStatusTag(status) {
      const statusMap = {
        '待处理': 'info',
        '处理中': 'warning',
        '已完成': 'success'
      }
      return statusMap[status] || 'info'
    },
    // 分页相关方法
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchWorkOrders()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchWorkOrders()
    }
  }
}
</script>

<style scoped>
.work-order-container {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.quick-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>
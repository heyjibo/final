<template>
  <div class="work-order-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>My Work Orders</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="fetchWorkOrders">Refresh</el-button>
          </div>
          
          <!-- Work Order Table -->
          <el-table :data="workOrders" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="Order ID" width="100"></el-table-column>
            <el-table-column prop="title" label="Order Title" width="200"></el-table-column>
            <el-table-column prop="type" label="Order Type" width="120">
              <template slot-scope="scope">
                <el-tag :type="getOrderTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="Priority" width="100">
              <template slot-scope="scope">
                <el-tag :type="getPriorityTag(scope.row.priority)">{{ scope.row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="Created Time" width="180"></el-table-column>
            <el-table-column prop="status" label="Status" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="Actions" width="200">
              <template slot-scope="scope">
                <el-button size="mini" @click="viewWorkOrder(scope.row)">View</el-button>
                <el-button size="mini" type="primary" @click="updateWorkOrder(scope.row)" v-if="scope.row.status !== 'Completed'">Process</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- Pagination -->
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
            <span>Work Order Statistics</span>
          </div>
          <div id="work-order-chart" style="height: 300px;"></div>
        </el-card>
        
        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>Quick Actions</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" icon="el-icon-plus" @click="createWorkOrder">Create Order</el-button>
            <el-button type="success" icon="el-icon-refresh" @click="fetchWorkOrders">Refresh Orders</el-button>
            <el-button type="warning" icon="el-icon-document" @click="exportWorkOrders">Export Orders</el-button>
          </div>
          
          <el-divider content-position="center">Filters</el-divider>
          
          <el-form :model="filterForm" size="small">
            <el-form-item label="Order Type">
              <el-select v-model="filterForm.type" placeholder="All Types" style="width: 100%;" @change="applyFilter">
                <el-option label="All Types" value=""></el-option>
                <el-option label="Delivery Exception" value="Delivery Exception"></el-option>
                <el-option label="Customer Complaint" value="Customer Complaint"></el-option>
                <el-option label="Device Failure" value="Device Failure"></el-option>
                <el-option label="System Issue" value="System Issue"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="Order Status">
              <el-select v-model="filterForm.status" placeholder="All Statuses" style="width: 100%;" @change="applyFilter">
                <el-option label="All Statuses" value=""></el-option>
                <el-option label="Pending" value="Pending"></el-option>
                <el-option label="Processing" value="Processing"></el-option>
                <el-option label="Completed" value="Completed"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="Date Range">
              <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="to"
                start-placeholder="Start Date"
                end-placeholder="End Date"
                style="width: 100%;"
                @change="applyFilter">
              </el-date-picker>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="applyFilter" style="width: 100%;">Apply Filter</el-button>
              <el-button @click="resetFilter" style="width: 100%; margin-top: 10px;">Reset Filter</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- Work Order Detail Dialog -->
    <el-dialog title="Work Order Details" :visible.sync="detailDialogVisible" width="60%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="Order ID">{{ selectedOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="Order Type">
          <el-tag :type="getOrderTypeTag(selectedOrder.type)">{{ selectedOrder.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Created Time">{{ selectedOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="Order Status">
          <el-tag :type="getStatusTag(selectedOrder.status)">{{ selectedOrder.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Priority">
          <el-tag :type="getPriorityTag(selectedOrder.priority)">{{ selectedOrder.priority }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Related Order" v-if="selectedOrder.relatedOrder">{{ selectedOrder.relatedOrder }}</el-descriptions-item>
        <el-descriptions-item label="Order Title" :span="2">{{ selectedOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="Order Description" :span="2">{{ selectedOrder.description }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="center">Processing Records</el-divider>
      
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
              <small>Operator: {{ activity.operator }}</small>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
    
    <!-- Create Work Order Dialog -->
    <el-dialog title="Create Work Order" :visible.sync="createDialogVisible" width="50%">
      <el-form :model="workOrderForm" :rules="workOrderRules" ref="workOrderForm" label-width="100px">
        <el-form-item label="Order Title" prop="title">
          <el-input v-model="workOrderForm.title" placeholder="Please enter order title"></el-input>
        </el-form-item>
        
        <el-form-item label="Order Type" prop="type">
          <el-select v-model="workOrderForm.type" placeholder="Please select order type" style="width: 100%;">
            <el-option label="Delivery Exception" value="Delivery Exception"></el-option>
            <el-option label="Customer Complaint" value="Customer Complaint"></el-option>
            <el-option label="Device Failure" value="Device Failure"></el-option>
            <el-option label="System Issue" value="System Issue"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="Priority" prop="priority">
          <el-select v-model="workOrderForm.priority" placeholder="Please select priority" style="width: 100%;">
            <el-option label="Low" value="Low"></el-option>
            <el-option label="Medium" value="Medium"></el-option>
            <el-option label="High" value="High"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="Related Order">
          <el-select v-model="workOrderForm.relatedOrder" filterable placeholder="Please select related order" style="width: 100%;">
            <el-option
              v-for="item in orderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="Description" prop="description">
          <el-input type="textarea" v-model="workOrderForm.description" :rows="4" placeholder="Please enter order description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitWorkOrderForm">Confirm</el-button>
      </div>
    </el-dialog>
    
    <!-- Process Work Order Dialog -->
    <el-dialog title="Process Work Order" :visible.sync="updateDialogVisible" width="50%">
      <el-form :model="updateForm" :rules="updateRules" ref="updateForm" label-width="100px">
        <el-form-item label="Order Status" prop="status">
          <el-select v-model="updateForm.status" placeholder="Please select order status" style="width: 100%;">
            <el-option label="Processing" value="Processing"></el-option>
            <el-option label="Completed" value="Completed"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="Process Record" prop="content">
          <el-input type="textarea" v-model="updateForm.content" :rows="4" placeholder="Please enter process record"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitUpdateForm">Confirm</el-button>
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
      
      // Filter form
      filterForm: {
        type: '',
        status: '',
        dateRange: null
      },
      
      // Work order detail dialog
      detailDialogVisible: false,
      selectedOrder: {
        activities: []
      },
      
      // Create work order dialog
      createDialogVisible: false,
      workOrderForm: {
        title: '',
        type: '',
        priority: 'Medium',
        relatedOrder: '',
        description: ''
      },
      workOrderRules: {
        title: [
          { required: true, message: 'Please enter work order title', trigger: 'blur' }
        ],
        type: [
          { required: true, message: 'Please select work order type', trigger: 'change' }
        ],
        priority: [
          { required: true, message: 'Please select priority', trigger: 'change' }
        ],
        description: [
          { required: true, message: 'Please enter work order description', trigger: 'blur' },
          { min: 10, message: 'Description must be at least 10 characters', trigger: 'blur' }
        ]
      },
      
      // Process work order dialog
      updateDialogVisible: false,
      updateForm: {
        id: '',
        status: '',
        content: ''
      },
      updateRules: {
        status: [
          { required: true, message: 'Please select work order status', trigger: 'change' }
        ],
        content: [
          { required: true, message: 'Please enter process record', trigger: 'blur' },
          { min: 5, message: 'Process record must be at least 5 characters', trigger: 'blur' }
        ]
      },
      
      // Order options
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
    // Initialize work order chart
    initWorkOrderChart() {
      this.workOrderChart = echarts.init(document.getElementById('work-order-chart'))
      
      const option = {
        title: {
          text: 'Work Order Status Statistics',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['Pending', 'Processing', 'Completed']
        },
        series: [
          {
            name: 'Order Status',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
              { value: 3, name: 'Pending' },
              { value: 2, name: 'Processing' },
              { value: 5, name: 'Completed' }
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
      
      // Listen for window resize, update chart size
      window.addEventListener('resize', () => {
        this.workOrderChart.resize()
      })
    },
    // Get order options
    fetchOrderOptions() {
      // Simulate API request
      setTimeout(() => {
        this.orderOptions = [
          { value: 'O2023041501', label: 'O2023041501 - Zhejiang University Yuquan Campus Building 18' },
          { value: 'O2023041502', label: 'O2023041502 - Huanglong Sports Center' },
          { value: 'O2023041503', label: 'O2023041503 - West Lake Cultural Square' },
          { value: 'O2023041504', label: 'O2023041504 - Hangzhou East Station' }
        ]
      }, 500)
    },
    // Get work order list
    fetchWorkOrders() {
      this.loading = true
      
      // Load work orders from localStorage, use default data if none exists
      const savedWorkOrders = localStorage.getItem('workOrders');
      let workOrders = [];
      
      if (savedWorkOrders) {
        try {
          workOrders = JSON.parse(savedWorkOrders);
          console.log('Loaded work orders from local storage:', workOrders);
        } catch (error) {
          console.error('Failed to parse work order data:', error);
          workOrders = this.getDefaultWorkOrders();
        }
      } else {
        workOrders = this.getDefaultWorkOrders();
        // First load, save default data to localStorage
        localStorage.setItem('workOrders', JSON.stringify(workOrders));
      }
      
      // Filter data
      let filtered = [...workOrders];
      
      // Apply filters
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
      
      // Pagination
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.workOrders = filtered.slice(start, end);
      
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
    // Get default work order data
    getDefaultWorkOrders() {
      return [
        {
          id: 'WO20230415001',
          title: 'Delivery Delay Complaint Handling',
          type: 'Delivery Exception',
          priority: 'Medium',
          status: 'Completed',
          createTime: '2023-04-15 10:30:22',
          updateTime: '2023-04-15 14:20:30',
          relatedOrder: 'O2023041501',
          description: 'Customer reported long delivery time and requested compensation.',
          handler: 'Zhang San',
          logs: [
            {
              time: '2023-04-15 10:30:22',
              title: 'Create Work Order',
              content: 'System automatically created work order',
              operator: 'System',
              type: 'primary'
            },
            {
              time: '2023-04-15 11:15:40',
              title: 'Start Processing',
              content: 'Contacted customer, negotiating solution.',
              operator: 'Zhang San',
              type: 'warning'
            },
            {
              time: '2023-04-15 14:20:30',
              title: 'Completed Processing',
              content: 'Apologized to customer and provided voucher compensation, customer accepted.',
              operator: 'Zhang San',
              type: 'success'
            }
          ]
        },
        {
          id: 'WO20230414001',
          title: 'Device GPS Location Abnormal',
          type: 'Device Failure',
          priority: 'High',
          status: 'Processing',
          createTime: '2023-04-14 09:15:10',
          updateTime: '2023-04-14 15:40:22',
          relatedOrder: '',
          description: 'GPS location signal is unstable, affecting delivery route planning.',
          handler: 'Li Si',
          logs: [
            {
              time: '2023-04-14 09:15:10',
              title: 'Create Work Order',
              content: 'Manually created work order',
              operator: 'Li Si',
              type: 'primary'
            },
            {
              time: '2023-04-14 10:30:45',
              title: 'Start Processing',
              content: 'Checking GPS module.',
              operator: 'Li Si',
              type: 'warning'
            }
          ]
        },
        {
          id: 'WO20230413001',
          title: 'Order Display Abnormal',
          type: 'System Issue',
          priority: 'Low',
          status: 'Pending',
          createTime: '2023-04-13 16:45:30',
          updateTime: '2023-04-13 16:45:30',
          relatedOrder: 'O2023041302',
          description: 'Order information incomplete on order acceptance page.',
          handler: '',
          logs: [
            {
              time: '2023-04-13 16:45:30',
              title: 'Create Work Order',
              content: 'Manually created work order',
              operator: 'Wang Wu',
              type: 'primary'
            }
          ]
        },
        {
          id: 'WO20230412001',
          title: 'Charger Failure',
          type: 'Device Failure',
          priority: 'Medium',
          status: 'Completed',
          createTime: '2023-04-12 08:30:20',
          updateTime: '2023-04-12 11:20:15',
          relatedOrder: '',
          description: 'Device charger cannot charge normally.',
          handler: 'Zhao Liu',
          logs: [
            {
              time: '2023-04-12 08:30:20',
              title: 'Create Work Order',
              content: 'Manually created work order',
              operator: 'Zhao Liu',
              type: 'primary'
            },
            {
              time: '2023-04-12 09:45:30',
              title: 'Start Processing',
              content: 'Examining charger and device interfaces.',
              operator: 'Zhao Liu',
              type: 'warning'
            },
            {
              time: '2023-04-12 11:20:15',
              title: 'Completed Processing',
              content: 'Replaced charger, problem solved.',
              operator: 'Zhao Liu',
              type: 'success'
            }
          ]
        },
        {
          id: 'WO20230411001',
          title: 'Customer Complaint Handling',
          type: 'Customer Complaint',
          priority: 'High',
          status: 'Completed',
          createTime: '2023-04-11 12:10:05',
          updateTime: '2023-04-11 15:30:45',
          relatedOrder: 'O2023041105',
          description: 'Customer complained about food spillage.',
          handler: 'Zhang San',
          logs: [
            {
              time: '2023-04-11 12:10:05',
              title: 'Create Work Order',
              content: 'System automatically created work order',
              operator: 'System',
              type: 'primary'
            },
            {
              time: '2023-04-11 13:20:30',
              title: 'Start Processing',
              content: 'Contacted customer, confirmed situation.',
              operator: 'Zhang San',
              type: 'warning'
            },
            {
              time: '2023-04-11 15:30:45',
              title: 'Completed Processing',
              content: 'Redelivered order to customer and provided voucher.',
              operator: 'Zhang San',
              type: 'success'
            }
          ]
        }
      ];
    },
    // View work order details
    viewWorkOrder(row) {
      this.selectedOrder = row
      this.detailDialogVisible = true
    },
    // Process work order
    updateWorkOrder(row) {
      this.updateForm = {
        id: row.id,
        status: 'Processing',
        content: ''
      }
      this.updateDialogVisible = true
    },
    // Create work order
    createWorkOrder() {
      this.workOrderForm = {
        title: '',
        type: '',
        priority: 'Medium',
        relatedOrder: '',
        description: ''
      }
      this.createDialogVisible = true
    },
    // Submit work order form
    submitWorkOrderForm() {
      this.$refs.workOrderForm.validate((valid) => {
        if (valid) {
          // Load existing work orders from localStorage
          const savedWorkOrders = localStorage.getItem('workOrders');
          let workOrders = [];
          
          if (savedWorkOrders) {
            try {
              workOrders = JSON.parse(savedWorkOrders);
            } catch (error) {
              console.error('Failed to parse work order data:', error);
              workOrders = this.getDefaultWorkOrders();
            }
          } else {
            workOrders = this.getDefaultWorkOrders();
          }
          
          // Create new work order
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
            status: 'Pending',
            createTime: nowStr,
            updateTime: nowStr,
            relatedOrder: this.workOrderForm.relatedOrder || '',
            description: this.workOrderForm.description,
            handler: '',
            logs: [
              {
                time: nowStr,
                title: 'Create Work Order',
                content: 'Manually created work order',
                operator: JSON.parse(localStorage.getItem('currentUser') || '{}').username || 'System',
                type: 'primary'
              }
            ]
          };
          
          // Add to work order list
          workOrders.unshift(newOrder);
          
          // Save to localStorage
          localStorage.setItem('workOrders', JSON.stringify(workOrders));
          
          this.$message({
            type: 'success',
            message: 'Work order created successfully!'
          });
          this.createDialogVisible = false;
          this.fetchWorkOrders();
        } else {
          return false;
        }
      });
    },
    // Submit work order process form
    submitUpdateForm() {
      this.$refs.updateForm.validate((valid) => {
        if (valid) {
          // Load existing work orders from localStorage
          const savedWorkOrders = localStorage.getItem('workOrders');
          let workOrders = [];
          
          if (savedWorkOrders) {
            try {
              workOrders = JSON.parse(savedWorkOrders);
            } catch (error) {
              console.error('Failed to parse work order data:', error);
              return;
            }
          } else {
            this.$message.error('Work order data not found');
            return;
          }
          
          // Find and update work order
          const now = new Date();
          const nowStr = now.toISOString().replace('T', ' ').substring(0, 19);
          const index = workOrders.findIndex(order => order.id === this.updateForm.id);
          
          if (index !== -1) {
            const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}').username || 'System';
            
            // Add processing record
            const logTitle = this.updateForm.status === 'Completed' ? 'Completed Processing' : 'Processing Update';
            const logType = this.updateForm.status === 'Completed' ? 'success' : 'warning';
            
            workOrders[index].logs.push({
              time: nowStr,
              title: logTitle,
              content: this.updateForm.content,
              operator: currentUser,
              type: logType
            });
            
            // Update work order status and handler
            workOrders[index].status = this.updateForm.status;
            workOrders[index].updateTime = nowStr;
            if (!workOrders[index].handler) {
              workOrders[index].handler = currentUser;
            }
            
            // Save back to localStorage
            localStorage.setItem('workOrders', JSON.stringify(workOrders));
            
            this.$message({
              type: 'success',
              message: 'Work order updated successfully!'
            });
            this.updateDialogVisible = false;
            this.fetchWorkOrders();
          } else {
            this.$message.error('Specified work order not found');
          }
        } else {
          return false;
        }
      });
    },
    // Export work orders
    exportWorkOrders() {
      this.$message({
        type: 'success',
        message: 'Work orders exported successfully!'
      })
    },
    // Apply filter
    applyFilter() {
      this.fetchWorkOrders()
    },
    // Reset filter
    resetFilter() {
      this.filterForm = {
        type: '',
        status: '',
        dateRange: null
      }
      this.fetchWorkOrders()
    },
    // Get order type tag
    getOrderTypeTag(type) {
      const typeMap = {
        'Delivery Exception': 'warning',
        'Customer Complaint': 'danger',
        'Device Failure': 'info',
        'System Issue': 'primary'
      }
      return typeMap[type] || 'info'
    },
    // Get priority tag
    getPriorityTag(priority) {
      const priorityMap = {
        'Low': 'info',
        'Medium': 'warning',
        'High': 'danger'
      }
      return priorityMap[priority] || 'info'
    },
    // Get status tag
    getStatusTag(status) {
      const statusMap = {
        'Pending': 'info',
        'Processing': 'warning',
        'Completed': 'success'
      }
      return statusMap[status] || 'info'
    },
    // Pagination related methods
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
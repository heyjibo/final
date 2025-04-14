<template>
  <div class="event-report-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>创建事件报告</span>
          </div>
          
          <el-form :model="eventForm" :rules="rules" ref="eventForm" label-width="100px">
            <el-form-item label="事件类型" prop="eventType">
              <el-select v-model="eventForm.eventType" placeholder="请选择事件类型" style="width: 100%;">
                <el-option label="交通事故" value="交通事故"></el-option>
                <el-option label="订单异常" value="订单异常"></el-option>
                <el-option label="客户投诉" value="客户投诉"></el-option>
                <el-option label="设备故障" value="设备故障"></el-option>
                <el-option label="道路阻塞" value="道路阻塞"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="关联订单" prop="orderId">
              <el-select v-model="eventForm.orderId" filterable placeholder="请选择关联订单" style="width: 100%;">
                <el-option
                  v-for="item in orderOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="事件级别" prop="level">
              <el-radio-group v-model="eventForm.level">
                <el-radio :label="1">一般</el-radio>
                <el-radio :label="2">重要</el-radio>
                <el-radio :label="3">紧急</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="事件位置" prop="location">
              <el-input v-model="eventForm.location" placeholder="请输入事件发生位置"></el-input>
              <el-button type="text" style="margin-left: 10px;" @click="useCurrentLocation">使用当前位置</el-button>
            </el-form-item>
            
            <el-form-item label="事件描述" prop="description">
              <el-input type="textarea" v-model="eventForm.description" :rows="4" placeholder="请详细描述事件发生的情况"></el-input>
            </el-form-item>
            
            <el-form-item label="上传图片">
              <el-upload
                action="#"
                list-type="picture-card"
                :file-list="eventForm.images"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
                :auto-upload="false"
                :limit="5">
                <i class="el-icon-plus"></i>
              </el-upload>
              <el-dialog :visible.sync="dialogVisible">
                <img width="100%" :src="dialogImageUrl" alt="">
              </el-dialog>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitForm('eventForm')">提交报告</el-button>
              <el-button @click="resetForm('eventForm')">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>事件报告须知</span>
          </div>
          <div class="notice-container">
            <p><i class="el-icon-info"></i> 事件报告用于记录配送过程中发生的各类异常情况</p>
            <el-divider></el-divider>
            <h4>报告说明：</h4>
            <ol>
              <li>请准确选择事件类型，如遇到无法归类的情况，可选择"其他"</li>
              <li>尽可能详细描述事件发生的时间、地点和经过</li>
              <li>紧急情况请立即联系客服热线：<strong>400-123-4567</strong></li>
              <li>如有图片证据，请上传清晰的照片（最多5张）</li>
              <li>提交后无法修改，请仔细核对信息</li>
            </ol>
            <el-divider></el-divider>
            <h4>紧急情况处理流程：</h4>
            <p>1. 确保人身安全 → 2. 联系客服 → 3. 提交事件报告</p>
          </div>
        </el-card>
        
        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>本月事件统计</span>
          </div>
          <div id="event-chart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>历史事件报告</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="fetchEventList">刷新</el-button>
      </div>
      
      <el-table :data="eventList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="报告ID" width="80"></el-table-column>
        <el-table-column prop="eventType" label="事件类型" width="120"></el-table-column>
        <el-table-column prop="orderId" label="关联订单" width="150"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column prop="location" label="事件位置" width="200"></el-table-column>
        <el-table-column prop="level" label="紧急程度" width="100">
          <template slot-scope="scope">
            <el-tag :type="getEventLevelType(scope.row.level)">
              {{ getEventLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '已处理' ? 'success' : scope.row.status === '处理中' ? 'warning' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewEventDetail(scope.row)">查看详情</el-button>
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
    
    <!-- 事件详情对话框 -->
    <el-dialog title="事件详情" :visible.sync="detailDialogVisible" width="60%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报告ID">{{ selectedEvent.id }}</el-descriptions-item>
        <el-descriptions-item label="事件类型">{{ selectedEvent.eventType }}</el-descriptions-item>
        <el-descriptions-item label="关联订单">{{ selectedEvent.orderId }}</el-descriptions-item>
        <el-descriptions-item label="事件级别">
          <el-tag :type="getEventLevelType(selectedEvent.level)">
            {{ getEventLevelText(selectedEvent.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedEvent.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="selectedEvent.status === '已处理' ? 'success' : selectedEvent.status === '处理中' ? 'warning' : 'info'">
            {{ selectedEvent.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="事件位置" :span="2">{{ selectedEvent.location }}</el-descriptions-item>
        <el-descriptions-item label="事件描述" :span="2">{{ selectedEvent.description }}</el-descriptions-item>
      </el-descriptions>
      
      <div v-if="selectedEvent.images && selectedEvent.images.length" style="margin-top: 20px;">
        <h4>事件图片：</h4>
        <el-carousel :interval="4000" type="card" height="200px">
          <el-carousel-item v-for="(item, index) in selectedEvent.images" :key="index">
            <img :src="item.url" style="width: 100%; height: 100%; object-fit: cover;" />
          </el-carousel-item>
        </el-carousel>
      </div>
      
      <div v-if="selectedEvent.feedback" style="margin-top: 20px;">
        <h4>处理反馈：</h4>
        <el-alert
          :title="selectedEvent.feedback"
          type="success"
          :closable="false">
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'EventReport',
  data() {
    return {
      loading: false,
      eventForm: {
        eventType: '',
        orderId: '',
        level: 1,
        location: '',
        description: '',
        images: []
      },
      rules: {
        eventType: [
          { required: true, message: '请选择事件类型', trigger: 'change' }
        ],
        orderId: [
          { required: true, message: '请选择关联订单', trigger: 'change' }
        ],
        level: [
          { required: true, message: '请选择事件级别', trigger: 'change' }
        ],
        location: [
          { required: true, message: '请输入事件位置', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入事件描述', trigger: 'blur' },
          { min: 10, message: '描述至少10个字符', trigger: 'blur' }
        ]
      },
      orderOptions: [],
      dialogImageUrl: '',
      dialogVisible: false,
      eventChart: null,
      
      // 历史事件数据
      eventList: [],
      currentPage: 1,
      pageSize: 5,
      total: 0,
      
      // 事件详情对话框
      detailDialogVisible: false,
      selectedEvent: {}
    }
  },
  mounted() {
    this.fetchOrderOptions()
    this.fetchEventList()
    this.initEventChart()
  },
  beforeDestroy() {
    if (this.eventChart) {
      this.eventChart.dispose()
    }
  },
  methods: {
    // 初始化事件统计图表
    initEventChart() {
      this.eventChart = echarts.init(document.getElementById('event-chart'))
      
      const option = {
        title: {
          text: '本月事件分类统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['交通事故', '订单异常', '客户投诉', '设备故障', '道路阻塞', '其他']
        },
        series: [
          {
            name: '事件类型',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
              { value: 2, name: '交通事故' },
              { value: 5, name: '订单异常' },
              { value: 3, name: '客户投诉' },
              { value: 4, name: '设备故障' },
              { value: 3, name: '道路阻塞' },
              { value: 1, name: '其他' }
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
      
      this.eventChart.setOption(option)
      
      // 监听窗口变化，更新图表大小
      window.addEventListener('resize', () => {
        this.eventChart.resize()
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
    // 获取历史事件列表
    fetchEventList() {
      this.loading = true
      // 模拟API请求
      setTimeout(() => {
        this.eventList = [
          {
            id: 1,
            eventType: '道路阻塞',
            orderId: 'O2023041201',
            createTime: '2023-04-12 14:30:20',
            location: '杭州市西湖区文三路553号',
            level: 2,
            status: '已处理',
            description: '文三路与万塘路交叉口发生交通事故，道路拥堵严重，预计延迟送达30分钟。',
            images: [
              { url: 'https://img.yzcdn.cn/vant/cat.jpeg' },
              { url: 'https://img.yzcdn.cn/vant/tree.jpeg' }
            ],
            feedback: '已通知客户，客户表示理解并同意延迟送达。'
          },
          {
            id: 2,
            eventType: '设备故障',
            orderId: 'O2023041102',
            createTime: '2023-04-11 10:15:33',
            location: '杭州市西湖区古墩路',
            level: 1,
            status: '已处理',
            description: '配送箱温控系统故障，导致食品温度无法保持。',
            images: [
              { url: 'https://img.yzcdn.cn/vant/cat.jpeg' }
            ],
            feedback: '已更换配送设备，重新配送。'
          },
          {
            id: 3,
            eventType: '客户投诉',
            orderId: 'O2023041001',
            createTime: '2023-04-10 18:45:10',
            location: '杭州市江干区凯旋路',
            level: 2,
            status: '处理中',
            description: '客户投诉食品包装破损，要求退款。',
            images: [
              { url: 'https://img.yzcdn.cn/vant/tree.jpeg' }
            ]
          },
          {
            id: 4,
            eventType: '交通事故',
            orderId: 'O2023040801',
            createTime: '2023-04-08 09:20:45',
            location: '杭州市下城区体育场路',
            level: 3,
            status: '已处理',
            description: '与一辆电动车轻微碰撞，无人员伤亡，已报警处理。',
            images: [
              { url: 'https://img.yzcdn.cn/vant/cat.jpeg' },
              { url: 'https://img.yzcdn.cn/vant/tree.jpeg' }
            ],
            feedback: '交警已现场处理，双方已达成和解。'
          },
          {
            id: 5,
            eventType: '订单异常',
            orderId: 'O2023040702',
            createTime: '2023-04-07 12:10:32',
            location: '杭州市上城区河坊街',
            level: 1,
            status: '已处理',
            description: '商家包装错误，与订单不符。',
            images: [],
            feedback: '已联系商家重新准备，并为客户道歉。'
          }
        ]
        this.total = 5
        this.loading = false
      }, 1000)
    },
    // 使用当前位置
    useCurrentLocation() {
      // 模拟获取当前位置
      this.eventForm.location = '杭州市西湖区文三路553号'
    },
    // 提交表单
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 这里应该调用后端API保存数据
          this.$message({
            type: 'success',
            message: '事件报告提交成功!'
          })
          this.resetForm(formName)
          this.fetchEventList()
        } else {
          return false
        }
      })
    },
    // 重置表单
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.eventForm.images = []
    },
    // 查看事件详情
    viewEventDetail(row) {
      this.selectedEvent = row
      this.detailDialogVisible = true
    },
    // 处理图片预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    // 处理图片移除
    handleRemove(file, fileList) {
      this.eventForm.images = fileList
    },
    // 获取事件级别文本
    getEventLevelText(level) {
      const levelMap = {
        1: '一般',
        2: '重要',
        3: '紧急'
      }
      return levelMap[level] || '未知'
    },
    // 获取事件级别标签类型
    getEventLevelType(level) {
      const typeMap = {
        1: 'info',
        2: 'warning',
        3: 'danger'
      }
      return typeMap[level] || 'info'
    },
    // 分页相关方法
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchEventList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchEventList()
    }
  }
}
</script>

<style scoped>
.event-report-container {
  padding: 20px;
}
.notice-container {
  padding: 10px;
}
.notice-container i {
  color: #409EFF;
  margin-right: 5px;
}
.notice-container ol {
  padding-left: 20px;
  line-height: 1.8;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 
<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409EFF;">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">24</p>
              <p class="stat-title">骑手总数</p>
            </div>
          </div>
          <el-divider></el-divider>
          <div class="stat-footer">
            <span>在线: <strong>18</strong></span>
            <span>同比 <strong style="color: #67C23A;">↑ 12%</strong></span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67C23A;">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">356</p>
              <p class="stat-title">今日订单</p>
            </div>
          </div>
          <el-divider></el-divider>
          <div class="stat-footer">
            <span>完成率: <strong>96%</strong></span>
            <span>同比 <strong style="color: #67C23A;">↑ 8%</strong></span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">12</p>
              <p class="stat-title">待处理事件</p>
            </div>
          </div>
          <el-divider></el-divider>
          <div class="stat-footer">
            <span>紧急: <strong>3</strong></span>
            <span>同比 <strong style="color: #F56C6C;">↑ 15%</strong></span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #F56C6C;">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">¥18,650</p>
              <p class="stat-title">今日营收</p>
            </div>
          </div>
          <el-divider></el-divider>
          <div class="stat-footer">
            <span>订单均价: <strong>¥52.4</strong></span>
            <span>同比 <strong style="color: #67C23A;">↑ 5%</strong></span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表和表格区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :md="16">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>订单趋势</span>
            <el-radio-group v-model="chartTimeRange" size="mini" style="float: right;">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">全年</el-radio-button>
            </el-radio-group>
          </div>
          <div id="order-trend-chart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card>
          <div slot="header" class="clearfix">
            <span>骑手排行榜</span>
            <el-button style="float: right; padding: 3px 0" type="text">查看全部</el-button>
          </div>
          <div class="rider-ranking">
            <div class="ranking-item" v-for="(rider, index) in topRiders" :key="rider.id">
              <div class="ranking-info">
                <span class="ranking-number" :class="{'top-three': index < 3}">{{ index + 1 }}</span>
                <img class="ranking-avatar" :src="rider.avatar" :alt="rider.name">
                <span class="ranking-name">{{ rider.name }}</span>
              </div>
              <div class="ranking-score">
                <span>{{ rider.orders }} 单</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单和事件 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :lg="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>最近订单</span>
            <el-button style="float: right; padding: 3px 0" type="text">查看更多</el-button>
          </div>
          <el-table :data="recentOrders" style="width: 100%" size="mini">
            <el-table-column prop="id" label="订单号" width="120"></el-table-column>
            <el-table-column prop="rider" label="骑手" width="100"></el-table-column>
            <el-table-column prop="address" label="配送地址"></el-table-column>
            <el-table-column prop="time" label="下单时间" width="150"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span>系统事件</span>
            <el-button style="float: right; padding: 3px 0" type="text">查看更多</el-button>
          </div>
          <div class="event-list">
            <div class="event-item" v-for="(event, index) in systemEvents" :key="index">
              <div class="event-time">{{ event.time }}</div>
              <div class="event-content">
                <div class="event-tag" :class="'event-tag-' + event.type">
                  <i :class="getEventIcon(event.type)"></i>
                </div>
                <div class="event-detail">
                  <div class="event-title">{{ event.title }}</div>
                  <div class="event-description">{{ event.description }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      chartTimeRange: 'week',
      orderTrendChart: null,
      topRiders: [
        { id: 1, name: '张三', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', orders: 56 },
        { id: 2, name: '李四', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', orders: 48 },
        { id: 3, name: '王五', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', orders: 42 },
        { id: 4, name: '赵六', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', orders: 36 },
        { id: 5, name: '钱七', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', orders: 30 }
      ],
      recentOrders: [
        { id: 'O2023060101', rider: '张三', address: '浙江大学玉泉校区18号楼', time: '2023-06-01 09:15', status: '已完成' },
        { id: 'O2023060102', rider: '李四', address: '黄龙体育中心南门', time: '2023-06-01 10:22', status: '配送中' },
        { id: 'O2023060103', rider: '王五', address: '西湖文化广场地铁站', time: '2023-06-01 11:05', status: '已完成' },
        { id: 'O2023060104', rider: '赵六', address: '杭州师范大学仓前校区', time: '2023-06-01 12:30', status: '已接单' },
        { id: 'O2023060105', rider: '钱七', address: '之江实验室', time: '2023-06-01 13:45', status: '已完成' }
      ],
      systemEvents: [
        { time: '09:30', type: 'warning', title: '系统警告', description: '检测到多个区域订单配送延迟' },
        { time: '11:20', type: 'info', title: '系统消息', description: '今日系统自动更新完成' },
        { time: '12:45', type: 'success', title: '活动上线', description: '端午节配送折扣活动已上线' },
        { time: '14:30', type: 'error', title: '系统错误', description: '短时间内地图服务异常，已恢复' },
        { time: '16:15', type: 'info', title: '系统通知', description: '新版骑手APP已发布，请通知骑手更新' }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initOrderTrendChart()
    })
    
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart)
    if (this.orderTrendChart) {
      this.orderTrendChart.dispose()
    }
  },
  watch: {
    chartTimeRange(val) {
      this.updateOrderTrendChart(val)
    }
  },
  methods: {
    // 初始化订单趋势图表
    initOrderTrendChart() {
      this.orderTrendChart = echarts.init(document.getElementById('order-trend-chart'))
      this.updateOrderTrendChart(this.chartTimeRange)
    },
    
    // 更新订单趋势图表
    updateOrderTrendChart(timeRange) {
      if (!this.orderTrendChart) return
      
      let xData = []
      let yData1 = [] // 订单数量
      let yData2 = [] // 完成订单
      
      if (timeRange === 'week') {
        xData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        yData1 = [320, 352, 301, 334, 390, 430, 410]
        yData2 = [302, 341, 290, 330, 380, 420, 395]
      } else if (timeRange === 'month') {
        xData = Array.from({length: 30}, (_, i) => `${i + 1}日`)
        yData1 = Array.from({length: 30}, () => Math.floor(Math.random() * 200) + 300)
        yData2 = yData1.map(value => Math.floor(value * (0.9 + Math.random() * 0.1)))
      } else {
        xData = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
        yData1 = [9200, 8300, 9800, 8800, 9500, 10000, 10500, 11000, 10300, 9600, 8900, 9000]
        yData2 = [8900, 8000, 9500, 8500, 9200, 9700, 10200, 10700, 10000, 9300, 8600, 8700]
      }
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['总订单', '完成订单'],
          right: 10,
          top: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总订单',
            type: 'line',
            data: yData1,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '完成订单',
            type: 'line',
            data: yData2,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#67C23A'
            },
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }
      
      this.orderTrendChart.setOption(option)
    },
    
    // 调整图表大小
    resizeChart() {
      if (this.orderTrendChart) {
        this.orderTrendChart.resize()
      }
    },
    
    // 获取订单状态对应的类型
    getStatusType(status) {
      const statusMap = {
        '已完成': 'success',
        '配送中': 'primary',
        '已接单': 'warning',
        '待配送': 'info',
        '已取消': 'danger'
      }
      return statusMap[status] || 'info'
    },
    
    // 获取事件对应的图标
    getEventIcon(type) {
      const iconMap = {
        'success': 'el-icon-check',
        'warning': 'el-icon-warning',
        'error': 'el-icon-close',
        'info': 'el-icon-info'
      }
      return iconMap[type] || 'el-icon-info'
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 10px;
}

/* 统计卡片样式 */
.stat-card {
  height: 100%;
  margin-bottom: 20px;
}
.stat-content {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
  flex-shrink: 0;
}
.stat-icon i {
  color: #fff;
  font-size: 30px;
}
.stat-info {
  flex: 1;
  min-width: 0;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  line-height: 1.2;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin: 5px 0 0;
}
.stat-footer {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 13px;
}
.stat-footer span {
  white-space: nowrap;
}

/* 骑手排行榜样式 */
.rider-ranking {
  max-height: 350px;
  overflow-y: auto;
}
.ranking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #EBEEF5;
}
.ranking-item:last-child {
  border-bottom: none;
}
.ranking-info {
  display: flex;
  align-items: center;
}
.ranking-number {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #F2F6FC;
  border-radius: 4px;
  margin-right: 10px;
  font-size: 14px;
  color: #606266;
}
.ranking-number.top-three {
  background-color: #409EFF;
  color: white;
}
.ranking-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
}
.ranking-name {
  font-weight: 500;
  color: #303133;
}
.ranking-score {
  color: #409EFF;
  font-weight: 500;
}

/* 事件列表样式 */
.event-list {
  max-height: 350px;
  overflow-y: auto;
}
.event-item {
  padding: 10px 0;
  border-bottom: 1px solid #EBEEF5;
}
.event-item:last-child {
  border-bottom: none;
}
.event-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}
.event-content {
  display: flex;
}
.event-tag {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
  flex-shrink: 0;
}
.event-tag i {
  color: #fff;
  font-size: 16px;
}
.event-tag-success {
  background-color: #67C23A;
}
.event-tag-warning {
  background-color: #E6A23C;
}
.event-tag-error {
  background-color: #F56C6C;
}
.event-tag-info {
  background-color: #909399;
}
.event-detail {
  flex: 1;
  min-width: 0;
}
.event-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}
.event-description {
  font-size: 13px;
  color: #606266;
  white-space: normal;
  word-break: break-all;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 5px;
  }
  .stat-value {
    font-size: 20px;
  }
  .event-description {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style> 
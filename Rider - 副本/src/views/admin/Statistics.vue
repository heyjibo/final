<template>
  <div class="statistics-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>数据统计</span>
        <el-radio-group v-model="timeRange" size="small" style="float: right;">
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
          <el-radio-button label="year">全年</el-radio-button>
        </el-radio-group>
      </div>
      
      <el-row :gutter="20">
        <el-col :xs="24" :md="12">
          <div id="order-chart" style="width: 100%; height: 400px;"></div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div id="income-chart" style="width: 100%; height: 400px;"></div>
        </el-col>
      </el-row>
      
      <el-divider>区域分布</el-divider>
      
      <el-row :gutter="20">
        <el-col :xs="24" :md="12">
          <div id="area-chart" style="width: 100%; height: 400px;"></div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div id="time-chart" style="width: 100%; height: 400px;"></div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Statistics',
  data() {
    return {
      timeRange: 'week',
      orderChart: null,
      incomeChart: null,
      areaChart: null,
      timeChart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
    
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.disposeCharts()
  },
  watch: {
    timeRange() {
      this.updateCharts()
    }
  },
  methods: {
    initCharts() {
      // 初始化订单图表
      this.orderChart = echarts.init(document.getElementById('order-chart'))
      
      // 初始化收入图表
      this.incomeChart = echarts.init(document.getElementById('income-chart'))
      
      // 初始化区域图表
      this.areaChart = echarts.init(document.getElementById('area-chart'))
      
      // 初始化时间图表
      this.timeChart = echarts.init(document.getElementById('time-chart'))
      
      this.updateCharts()
    },
    
    updateCharts() {
      this.updateOrderChart()
      this.updateIncomeChart()
      this.updateAreaChart()
      this.updateTimeChart()
    },
    
    updateOrderChart() {
      const option = {
        title: {
          text: '订单量统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['总订单', '完成订单', '取消订单'],
          bottom: 10
        },
        xAxis: {
          type: 'category',
          data: this.getXAxisData()
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总订单',
            type: 'bar',
            data: this.generateRandomData(100, 500)
          },
          {
            name: '完成订单',
            type: 'bar',
            data: this.generateRandomData(80, 450)
          },
          {
            name: '取消订单',
            type: 'bar',
            data: this.generateRandomData(10, 50)
          }
        ]
      }
      
      this.orderChart.setOption(option)
    },
    
    updateIncomeChart() {
      const option = {
        title: {
          text: '收入统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总收入', '骑手收入', '平台收入'],
          bottom: 10
        },
        xAxis: {
          type: 'category',
          data: this.getXAxisData()
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value} 元'
          }
        },
        series: [
          {
            name: '总收入',
            type: 'line',
            data: this.generateRandomData(5000, 20000),
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            }
          },
          {
            name: '骑手收入',
            type: 'line',
            data: this.generateRandomData(3000, 15000),
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#67C23A'
            }
          },
          {
            name: '平台收入',
            type: 'line',
            data: this.generateRandomData(2000, 5000),
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#E6A23C'
            }
          }
        ]
      }
      
      this.incomeChart.setOption(option)
    },
    
    updateAreaChart() {
      const option = {
        title: {
          text: '区域订单分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          top: 'center',
          data: ['南湖区', '西湖区', '拱墅区', '江干区', '下城区', '上城区', '其他区域']
        },
        series: [
          {
            name: '订单区域',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: 1048, name: '南湖区' },
              { value: 735, name: '西湖区' },
              { value: 580, name: '拱墅区' },
              { value: 484, name: '江干区' },
              { value: 300, name: '下城区' },
              { value: 250, name: '上城区' },
              { value: 100, name: '其他区域' }
            ]
          }
        ]
      }
      
      this.areaChart.setOption(option)
    },
    
    updateTimeChart() {
      const option = {
        title: {
          text: '订单时段分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        xAxis: {
          type: 'category',
          data: Array.from({ length: 24 }, (_, i) => `${i}:00`)
        },
        yAxis: {
          type: 'value',
          name: '订单量',
          min: 0
        },
        series: [
          {
            name: '订单量',
            type: 'bar',
            data: [
              10, 8, 5, 3, 2, 5, 20, 40, 58, 50, 42, 60, 
              75, 65, 48, 42, 50, 85, 90, 80, 70, 50, 30, 20
            ],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 1, color: '#188df0' }
              ])
            }
          },
          {
            name: '订单变化趋势',
            type: 'line',
            data: [
              10, 8, 5, 3, 2, 5, 20, 40, 58, 50, 42, 60, 
              75, 65, 48, 42, 50, 85, 90, 80, 70, 50, 30, 20
            ],
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#F56C6C'
            },
            symbol: 'circle',
            symbolSize: 8
          }
        ]
      }
      
      this.timeChart.setOption(option)
    },
    
    resizeCharts() {
      this.orderChart && this.orderChart.resize()
      this.incomeChart && this.incomeChart.resize()
      this.areaChart && this.areaChart.resize()
      this.timeChart && this.timeChart.resize()
    },
    
    disposeCharts() {
      this.orderChart && this.orderChart.dispose()
      this.incomeChart && this.incomeChart.dispose()
      this.areaChart && this.areaChart.dispose()
      this.timeChart && this.timeChart.dispose()
    },
    
    getXAxisData() {
      if (this.timeRange === 'week') {
        return ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      } else if (this.timeRange === 'month') {
        return Array.from({ length: 30 }, (_, i) => `${i + 1}日`)
      } else {
        return ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
      }
    },
    
    generateRandomData(min, max) {
      const length = this.timeRange === 'week' ? 7 : (this.timeRange === 'month' ? 30 : 12)
      return Array.from({ length }, () => Math.floor(Math.random() * (max - min + 1)) + min)
    }
  }
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}
</style> 
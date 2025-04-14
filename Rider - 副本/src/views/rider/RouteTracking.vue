<template>
  <div class="route-tracking-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>实时路线跟踪</span>
            <el-button-group style="float: right;">
              <el-button size="small" type="primary" @click="startTracking" :disabled="isTracking">开始跟踪</el-button>
              <el-button size="small" type="danger" @click="stopTracking" :disabled="!isTracking">停止跟踪</el-button>
            </el-button-group>
          </div>
          <!-- 地图容器 -->
          <div id="map-container" style="height: 500px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>当前配送信息</span>
          </div>
          <div v-if="currentDelivery">
            <p><strong>订单编号:</strong> {{ currentDelivery.orderId }}</p>
            <p><strong>配送状态:</strong> {{ currentDelivery.status }}</p>
            <p><strong>配送地址:</strong> {{ currentDelivery.address }}</p>
            <p><strong>预计送达:</strong> {{ currentDelivery.estimatedTime }}</p>
            <p><strong>已行驶:</strong> {{ currentDelivery.distance }} 公里</p>
            <p><strong>配送时长:</strong> {{ currentDelivery.duration }} 分钟</p>
            <el-divider></el-divider>
            <div class="gps-info">
              <p><strong>当前坐标:</strong> {{ gpsData.latitude }}, {{ gpsData.longitude }}</p>
              <p><strong>速度:</strong> {{ gpsData.speed }} km/h</p>
              <p><strong>方向:</strong> {{ gpsData.direction }}°</p>
              <p><strong>最后更新:</strong> {{ gpsData.lastUpdate }}</p>
            </div>
            <el-progress :percentage="currentDelivery.progressPercentage" :color="customColorMethod" :text-inside="true" :stroke-width="20"></el-progress>
          </div>
          <div v-else class="no-data">
            <i class="el-icon-warning-outline"></i>
            <p>当前没有进行中的配送</p>
          </div>
        </el-card>
        
        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>速度统计</span>
          </div>
          <!-- 速度统计图表 -->
          <div id="speed-chart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>历史配送路线</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshRouteHistory">刷新</el-button>
      </div>
      <el-table :data="routeHistory" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="date" label="日期" width="180"></el-table-column>
        <el-table-column prop="orderId" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="startAddress" label="起点" width="200"></el-table-column>
        <el-table-column prop="endAddress" label="终点" width="200"></el-table-column>
        <el-table-column prop="distance" label="距离(km)" width="100"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="120"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewRouteDetail(scope.row)">查看详情</el-button>
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
    
    <!-- 路线详情对话框 -->
    <el-dialog title="路线详情" :visible.sync="dialogVisible" width="70%">
      <div id="detail-map-container" style="height: 400px;"></div>
      <div style="margin-top: 20px;">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ selectedRoute.orderId }}</el-descriptions-item>
          <el-descriptions-item label="配送日期">{{ selectedRoute.date }}</el-descriptions-item>
          <el-descriptions-item label="起点">{{ selectedRoute.startAddress }}</el-descriptions-item>
          <el-descriptions-item label="终点">{{ selectedRoute.endAddress }}</el-descriptions-item>
          <el-descriptions-item label="配送距离">{{ selectedRoute.distance }}km</el-descriptions-item>
          <el-descriptions-item label="配送时长">{{ selectedRoute.duration }}分钟</el-descriptions-item>
          <el-descriptions-item label="配送状态">{{ selectedRoute.status }}</el-descriptions-item>
          <el-descriptions-item label="骑手">{{ selectedRoute.rider || '未知' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import AMapLoader from '@amap/amap-jsapi-loader'

export default {
  name: 'RouteTracking',
  data() {
    return {
      loading: false,
      map: null,
      mapKey: '你的高德地图Key', // 需要替换为实际的高德地图Key
      isTracking: false,
      trackingTimer: null,
      speedChart: null,
      mockPositions: [],
      currentPositionIndex: 0,
      polyline: null,
      marker: null,
      // 当前配送信息
      currentDelivery: {
        orderId: 'O2023041501',
        status: '配送中',
        address: '浙江大学玉泉校区18号楼',
        estimatedTime: '15:30',
        distance: 3.2,
        duration: 15,
        progressPercentage: 60
      },
      // GPS数据
      gpsData: {
        latitude: 30.259924,
        longitude: 120.132254,
        speed: 15,
        direction: 90,
        lastUpdate: '2023-04-12 15:20:30'
      },
      // 历史路线记录
      routeHistory: [],
      currentPage: 1,
      pageSize: 5,
      total: 0,
      // 路线详情对话框
      dialogVisible: false,
      selectedRoute: {}
    }
  },
  mounted() {
    this.initMap()
    this.initSpeedChart()
    this.fetchRouteHistory()
    // 生成模拟路线数据
    this.generateMockPositions()
  },
  beforeDestroy() {
    if (this.trackingTimer) {
      clearInterval(this.trackingTimer)
    }
    if (this.speedChart) {
      this.speedChart.dispose()
    }
  },
  methods: {
    async initMap() {
      try {
        const AMap = await AMapLoader.load({
          key: this.mapKey,
          version: '2.0',
          plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.Driving']
        })
        
        this.map = new AMap.Map('map-container', {
          zoom: 13,
          center: [120.132254, 30.259924]  // 杭州坐标
        })
        
        // 添加地图控件
        this.map.addControl(new AMap.Scale())
        this.map.addControl(new AMap.ToolBar())
        
        // 初始化骑手标记
        this.marker = new AMap.Marker({
          position: [120.132254, 30.259924],
          icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
          offset: new AMap.Pixel(-13, -30)
        })
        this.map.add(this.marker)
        
        // 初始化路线
        this.polyline = new AMap.Polyline({
          path: [],
          strokeColor: '#3498db',
          strokeWeight: 6,
          strokeOpacity: 0.8
        })
        this.map.add(this.polyline)
        
      } catch (e) {
        console.error('地图加载失败', e)
      }
    },
    initSpeedChart() {
      this.speedChart = echarts.init(document.getElementById('speed-chart'))
      
      const option = {
        title: {
          text: '配送速度变化',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['15:00', '15:05', '15:10', '15:15', '15:20', '15:25', '15:30']
        },
        yAxis: {
          type: 'value',
          name: '速度 (km/h)'
        },
        series: [{
          data: [10, 15, 8, 20, 12, 15, 18],
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#5dade2'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(93, 173, 226, 0.5)'
              }, {
                offset: 1, color: 'rgba(93, 173, 226, 0.1)'
              }]
            }
          }
        }]
      }
      
      this.speedChart.setOption(option)
      
      // 监听窗口变化，更新图表大小
      window.addEventListener('resize', () => {
        this.speedChart.resize()
      })
    },
    // 生成模拟路线数据
    generateMockPositions() {
      // 杭州浙大玉泉到西湖的路线模拟
      this.mockPositions = [
        [120.132254, 30.259924], // 浙大玉泉校区
        [120.130254, 30.257924],
        [120.128254, 30.254924],
        [120.125254, 30.252924],
        [120.122254, 30.252924],
        [120.119254, 30.251924],
        [120.117254, 30.252924],
        [120.115254, 30.256924],
        [120.112254, 30.259924],
        [120.109254, 30.262924],
        [120.106254, 30.264924],
        [120.103254, 30.266924],
        [120.100254, 30.267924],
        [120.097254, 30.269924], // 西湖
      ]
    },
    // 开始路线跟踪
    startTracking() {
      if (this.isTracking) return
      
      this.isTracking = true
      this.polyline.setPath([])
      this.currentPositionIndex = 0
      
      this.trackingTimer = setInterval(() => {
        if (this.currentPositionIndex >= this.mockPositions.length) {
          this.stopTracking()
          return
        }
        
        const position = this.mockPositions[this.currentPositionIndex]
        
        // 更新GPS数据
        this.gpsData.longitude = position[0]
        this.gpsData.latitude = position[1]
        this.gpsData.speed = Math.floor(Math.random() * 15) + 10
        this.gpsData.direction = Math.floor(Math.random() * 360)
        this.gpsData.lastUpdate = new Date().toLocaleString()
        
        // 更新标记位置
        this.marker.setPosition(position)
        
        // 更新路线
        const path = this.polyline.getPath()
        path.push(position)
        this.polyline.setPath(path)
        
        // 更新地图视图
        this.map.setCenter(position)
        
        // 更新配送进度
        const progress = Math.min(Math.round((this.currentPositionIndex / (this.mockPositions.length - 1)) * 100), 100)
        this.currentDelivery.progressPercentage = progress
        this.currentDelivery.distance = (3.2 * progress / 100).toFixed(1)
        this.currentDelivery.duration = Math.floor(15 * progress / 100)
        
        this.currentPositionIndex++
        
        // 更新速度图表
        this.updateSpeedChart(this.gpsData.speed)
        
      }, 2000)
    },
    // 停止路线跟踪
    stopTracking() {
      if (this.trackingTimer) {
        clearInterval(this.trackingTimer)
        this.trackingTimer = null
      }
      this.isTracking = false
    },
    // 更新速度图表
    updateSpeedChart(newSpeed) {
      const option = this.speedChart.getOption()
      const xData = option.xAxis[0].data
      const yData = option.series[0].data
      
      // 移除最早的数据
      xData.shift()
      yData.shift()
      
      // 添加新数据
      const now = new Date()
      const timeStr = `${now.getHours()}:${now.getMinutes()}`
      xData.push(timeStr)
      yData.push(newSpeed)
      
      this.speedChart.setOption({
        xAxis: {
          data: xData
        },
        series: [{
          data: yData
        }]
      })
    },
    // 获取历史路线记录
    fetchRouteHistory() {
      this.loading = true
      // 模拟API请求
      setTimeout(() => {
        this.routeHistory = [
          {
            id: 1,
            date: '2023-04-12',
            orderId: 'O2023041201',
            startAddress: '浙江大学玉泉校区',
            endAddress: '西湖文化广场',
            distance: 5.2,
            duration: 25,
            status: '已完成',
            rider: '张三'
          },
          {
            id: 2,
            date: '2023-04-11',
            orderId: 'O2023041102',
            startAddress: '黄龙体育中心',
            endAddress: '杭州东站',
            distance: 8.5,
            duration: 35,
            status: '已完成',
            rider: '张三'
          },
          {
            id: 3,
            date: '2023-04-11',
            orderId: 'O2023041101',
            startAddress: '浙江大学紫金港校区',
            endAddress: '西溪湿地',
            distance: 6.3,
            duration: 30,
            status: '已完成',
            rider: '张三'
          },
          {
            id: 4,
            date: '2023-04-10',
            orderId: 'O2023041002',
            startAddress: '杭州师范大学',
            endAddress: '杭州动物园',
            distance: 12.1,
            duration: 45,
            status: '已完成',
            rider: '张三'
          },
          {
            id: 5,
            date: '2023-04-10',
            orderId: 'O2023041001',
            startAddress: '浙江工业大学',
            endAddress: '武林广场',
            distance: 7.8,
            duration: 32,
            status: '已完成',
            rider: '张三'
          }
        ]
        this.total = 5
        this.loading = false
      }, 1000)
    },
    // 刷新历史路线记录
    refreshRouteHistory() {
      this.fetchRouteHistory()
    },
    // 查看路线详情
    async viewRouteDetail(row) {
      this.selectedRoute = row
      this.dialogVisible = true
      
      // 等待对话框显示后初始化详情地图
      await this.$nextTick()
      this.initDetailMap()
    },
    // 初始化详情地图
    async initDetailMap() {
      try {
        const AMap = await AMapLoader.load({
          key: this.mapKey,
          version: '2.0',
          plugins: ['AMap.Scale', 'AMap.Driving']
        })
        
        const detailMap = new AMap.Map('detail-map-container', {
          zoom: 12,
          center: [120.132254, 30.259924]  // 杭州坐标
        })
        
        // 添加起点和终点标记
        const startMarker = new AMap.Marker({
          position: [120.132254, 30.259924], // 示例起点，实际应该从API获取
          icon: 'https://webapi.amap.com/theme/v1.3/markers/n/start.png',
          offset: new AMap.Pixel(-13, -30)
        })
        
        const endMarker = new AMap.Marker({
          position: [120.097254, 30.269924], // 示例终点，实际应该从API获取
          icon: 'https://webapi.amap.com/theme/v1.3/markers/n/end.png',
          offset: new AMap.Pixel(-13, -30)
        })
        
        detailMap.add([startMarker, endMarker])
        
        // 使用驾车规划
        const driving = new AMap.Driving({
          map: detailMap,
          policy: AMap.DrivingPolicy.LEAST_TIME
        })
        
        driving.search(
          [120.132254, 30.259924], // 起点坐标
          [120.097254, 30.269924], // 终点坐标
          (status, result) => {
            if (status === 'complete') {
              console.log('路线规划成功')
            } else {
              console.error('路线规划失败', result)
            }
          }
        )
      } catch (e) {
        console.error('详情地图加载失败', e)
      }
    },
    // 自定义进度条颜色
    customColorMethod(percentage) {
      if (percentage < 30) {
        return '#909399'
      } else if (percentage < 70) {
        return '#e6a23c'
      } else {
        return '#67c23a'
      }
    },
    // 分页相关方法
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchRouteHistory()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchRouteHistory()
    }
  }
}
</script>

<style scoped>
.route-tracking-container {
  padding: 20px;
}
.gps-info {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}
.no-data {
  text-align: center;
  padding: 20px 0;
  color: #909399;
}
.no-data i {
  font-size: 30px;
  margin-bottom: 10px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 
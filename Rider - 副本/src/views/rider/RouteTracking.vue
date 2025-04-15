<template>
  <div class="route-tracking-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Real-time Route Tracking</span>
            <el-button-group style="float: right;">
              <el-button size="small" type="primary" @click="startTracking" :disabled="isTracking">Start Tracking</el-button>
              <el-button size="small" type="danger" @click="stopTracking" :disabled="!isTracking">Stop Tracking</el-button>
            </el-button-group>
          </div>
          <!-- Map container -->
          <div id="map-container" style="height: 500px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Current Delivery Information</span>
          </div>
          <div v-if="currentDelivery">
            <p><strong>Order ID:</strong> {{ currentDelivery.orderId }}</p>
            <p><strong>Delivery Status:</strong> {{ currentDelivery.status }}</p>
            <p><strong>Delivery Address:</strong> {{ currentDelivery.address }}</p>
            <p><strong>Estimated Arrival:</strong> {{ currentDelivery.estimatedTime }}</p>
            <p><strong>Distance Traveled:</strong> {{ currentDelivery.distance }} km</p>
            <p><strong>Delivery Duration:</strong> {{ currentDelivery.duration }} minutes</p>
            <el-divider></el-divider>
            <div class="gps-info">
              <p><strong>Current Coordinates:</strong> {{ gpsData.latitude }}, {{ gpsData.longitude }}</p>
              <p><strong>Speed:</strong> {{ gpsData.speed }} km/h</p>
              <p><strong>Direction:</strong> {{ gpsData.direction }}°</p>
              <p><strong>Last Update:</strong> {{ gpsData.lastUpdate }}</p>
            </div>
            <el-progress :percentage="currentDelivery.progressPercentage" :color="customColorMethod" :text-inside="true" :stroke-width="20"></el-progress>
          </div>
          <div v-else class="no-data">
            <i class="el-icon-warning-outline"></i>
            <p>No ongoing deliveries</p>
          </div>
        </el-card>
        
        <el-card class="box-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span>Speed Statistics</span>
          </div>
          <!-- Speed chart -->
          <div id="speed-chart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>Delivery History</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshRouteHistory">Refresh</el-button>
      </div>
      <el-table :data="routeHistory" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="date" label="Date" width="180"></el-table-column>
        <el-table-column prop="orderId" label="Order ID" width="150"></el-table-column>
        <el-table-column prop="startAddress" label="Start" width="200"></el-table-column>
        <el-table-column prop="endAddress" label="End" width="200"></el-table-column>
        <el-table-column prop="distance" label="Distance (km)" width="100"></el-table-column>
        <el-table-column prop="duration" label="Duration (minutes)" width="120"></el-table-column>
        <el-table-column prop="status" label="Status">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'Completed' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Action" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewRouteDetail(scope.row)">View Details</el-button>
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
    
    <!-- Route details dialog -->
    <el-dialog title="Route Details" :visible.sync="dialogVisible" width="70%">
      <div id="detail-map-container" style="height: 400px;"></div>
      <div style="margin-top: 20px;">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Order ID">{{ selectedRoute.orderId }}</el-descriptions-item>
          <el-descriptions-item label="Delivery Date">{{ selectedRoute.date }}</el-descriptions-item>
          <el-descriptions-item label="Start">{{ selectedRoute.startAddress }}</el-descriptions-item>
          <el-descriptions-item label="End">{{ selectedRoute.endAddress }}</el-descriptions-item>
          <el-descriptions-item label="Delivery Distance">{{ selectedRoute.distance }}km</el-descriptions-item>
          <el-descriptions-item label="Delivery Duration">{{ selectedRoute.duration }}minutes</el-descriptions-item>
          <el-descriptions-item label="Delivery Status">{{ selectedRoute.status }}</el-descriptions-item>
          <el-descriptions-item label="Rider">{{ selectedRoute.rider || 'Unknown' }}</el-descriptions-item>
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
      mapKey: 'Your Amap Key', // Replace with actual Amap Key
      isTracking: false,
      trackingTimer: null,
      speedChart: null,
      mockPositions: [],
      currentPositionIndex: 0,
      polyline: null,
      marker: null,
      // Current delivery information
      currentDelivery: {
        orderId: 'O2023041501',
        status: 'In Progress',
        address: 'Zhejiang University Yuquan Campus, Building 18',
        estimatedTime: '15:30',
        distance: 3.2,
        duration: 15,
        progressPercentage: 60
      },
      // GPS data
      gpsData: {
        latitude: 30.259924,
        longitude: 120.132254,
        speed: 15,
        direction: 90,
        lastUpdate: '2023-04-12 15:20:30'
      },
      // Route history record
      routeHistory: [],
      currentPage: 1,
      pageSize: 5,
      total: 0,
      // Route details dialog
      dialogVisible: false,
      selectedRoute: {}
    }
  },
  mounted() {
    this.initMap()
    this.initSpeedChart()
    this.fetchRouteHistory()
    // Generate mock route data
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
          center: [120.132254, 30.259924]  // Hangzhou coordinates
        })
        
        // Add map controls
        this.map.addControl(new AMap.Scale())
        this.map.addControl(new AMap.ToolBar())
        
        // Initialize rider marker
        this.marker = new AMap.Marker({
          position: [120.132254, 30.259924],
          icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
          offset: new AMap.Pixel(-13, -30)
        })
        this.map.add(this.marker)
        
        // Initialize route
        this.polyline = new AMap.Polyline({
          path: [],
          strokeColor: '#3498db',
          strokeWeight: 6,
          strokeOpacity: 0.8
        })
        this.map.add(this.polyline)
        
      } catch (e) {
        console.error('Map loading failed', e)
      }
    },
    initSpeedChart() {
      this.speedChart = echarts.init(document.getElementById('speed-chart'))
      
      const option = {
        title: {
          text: 'Delivery Speed Variation',
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
          name: 'Speed (km/h)'
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
      
      // Listen for window resize, update chart size
      window.addEventListener('resize', () => {
        this.speedChart.resize()
      })
    },
    // Generate mock route data
    generateMockPositions() {
      // Mock route data from Zhejiang University Yuquan to West Lake
      this.mockPositions = [
        [120.132254, 30.259924], // Zhejiang University Yuquan Campus
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
        [120.097254, 30.269924], // West Lake
      ]
    },
    // Start route tracking
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
        
        // Update GPS data
        this.gpsData.longitude = position[0]
        this.gpsData.latitude = position[1]
        this.gpsData.speed = Math.floor(Math.random() * 15) + 10
        this.gpsData.direction = Math.floor(Math.random() * 360)
        this.gpsData.lastUpdate = new Date().toLocaleString()
        
        // Update marker position
        this.marker.setPosition(position)
        
        // Update route
        const path = this.polyline.getPath()
        path.push(position)
        this.polyline.setPath(path)
        
        // Update map view
        this.map.setCenter(position)
        
        // Update delivery progress
        const progress = Math.min(Math.round((this.currentPositionIndex / (this.mockPositions.length - 1)) * 100), 100)
        this.currentDelivery.progressPercentage = progress
        this.currentDelivery.distance = (3.2 * progress / 100).toFixed(1)
        this.currentDelivery.duration = Math.floor(15 * progress / 100)
        
        this.currentPositionIndex++
        
        // Update speed chart
        this.updateSpeedChart(this.gpsData.speed)
        
      }, 2000)
    },
    // Stop route tracking
    stopTracking() {
      if (this.trackingTimer) {
        clearInterval(this.trackingTimer)
        this.trackingTimer = null
      }
      this.isTracking = false
    },
    // Update speed chart
    updateSpeedChart(newSpeed) {
      const option = this.speedChart.getOption()
      const xData = option.xAxis[0].data
      const yData = option.series[0].data
      
      // Remove oldest data
      xData.shift()
      yData.shift()
      
      // Add new data
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
    // Get route history record
    fetchRouteHistory() {
      this.loading = true
      // Simulate API request
      setTimeout(() => {
        this.routeHistory = [
          {
            id: 1,
            date: '2023-04-12',
            orderId: 'O2023041201',
            startAddress: 'Zhejiang University Yuquan Campus',
            endAddress: 'West Lake Cultural Square',
            distance: 5.2,
            duration: 25,
            status: 'Completed',
            rider: 'Zhang San'
          },
          {
            id: 2,
            date: '2023-04-11',
            orderId: 'O2023041102',
            startAddress: 'Huanglong Sports Center',
            endAddress: 'Hangzhou East Station',
            distance: 8.5,
            duration: 35,
            status: 'Completed',
            rider: 'Zhang San'
          },
          {
            id: 3,
            date: '2023-04-11',
            orderId: 'O2023041101',
            startAddress: 'Zhejiang University Zijingang Campus',
            endAddress: 'Xixi Wetland',
            distance: 6.3,
            duration: 30,
            status: 'Completed',
            rider: 'Zhang San'
          },
          {
            id: 4,
            date: '2023-04-10',
            orderId: 'O2023041002',
            startAddress: 'Hangzhou Normal University',
            endAddress: 'Hangzhou Zoo',
            distance: 12.1,
            duration: 45,
            status: 'Completed',
            rider: 'Zhang San'
          },
          {
            id: 5,
            date: '2023-04-10',
            orderId: 'O2023041001',
            startAddress: 'Zhejiang University of Technology',
            endAddress: 'Wulin Square',
            distance: 7.8,
            duration: 32,
            status: 'Completed',
            rider: 'Zhang San'
          }
        ]
        this.total = 5
        this.loading = false
      }, 1000)
    },
    // Refresh route history record
    refreshRouteHistory() {
      this.fetchRouteHistory()
    },
    // View route details
    async viewRouteDetail(row) {
      this.selectedRoute = row
      this.dialogVisible = true
      
      // Wait for dialog to show before initializing detail map
      await this.$nextTick()
      this.initDetailMap()
    },
    // Initialize detail map
    async initDetailMap() {
      try {
        const AMap = await AMapLoader.load({
          key: this.mapKey,
          version: '2.0',
          plugins: ['AMap.Scale', 'AMap.Driving']
        })
        
        const detailMap = new AMap.Map('detail-map-container', {
          zoom: 12,
          center: [120.132254, 30.259924]  // Hangzhou coordinates
        })
        
        // Add start and end marker
        const startMarker = new AMap.Marker({
          position: [120.132254, 30.259924], // Example start, should be fetched from API
          icon: 'https://webapi.amap.com/theme/v1.3/markers/n/start.png',
          offset: new AMap.Pixel(-13, -30)
        })
        
        const endMarker = new AMap.Marker({
          position: [120.097254, 30.269924], // Example end, should be fetched from API
          icon: 'https://webapi.amap.com/theme/v1.3/markers/n/end.png',
          offset: new AMap.Pixel(-13, -30)
        })
        
        detailMap.add([startMarker, endMarker])
        
        // Use driving planning
        const driving = new AMap.Driving({
          map: detailMap,
          policy: AMap.DrivingPolicy.LEAST_TIME
        })
        
        driving.search(
          [120.132254, 30.259924], // Start coordinate
          [120.097254, 30.269924], // End coordinate
          (status, result) => {
            if (status === 'complete') {
              console.log('Route planning successful')
            } else {
              console.error('Route planning failed', result)
            }
          }
        )
      } catch (e) {
        console.error('Detail map loading failed', e)
      }
    },
    // Custom progress bar color
    customColorMethod(percentage) {
      if (percentage < 30) {
        return '#909399'
      } else if (percentage < 70) {
        return '#e6a23c'
      } else {
        return '#67c23a'
      }
    },
    // Pagination related methods
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
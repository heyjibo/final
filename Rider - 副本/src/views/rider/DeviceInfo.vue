<template>
  <div class="device-info-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleAddDevice">添加设备</el-button>
      </div>
      
      <!-- 设备信息表格 -->
      <el-table :data="deviceList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="deviceName" label="设备名称" width="150"></el-table-column>
        <el-table-column prop="imei" label="IMEI" width="180"></el-table-column>
        <el-table-column prop="sn" label="SN号" width="180"></el-table-column>
        <el-table-column prop="model" label="设备型号" width="150"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bindTime" label="绑定时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 添加/编辑设备对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
      <el-form :model="deviceForm" :rules="rules" ref="deviceForm" label-width="100px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="deviceForm.deviceName" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="IMEI" prop="imei">
          <el-input v-model="deviceForm.imei" placeholder="请输入IMEI号"></el-input>
        </el-form-item>
        <el-form-item label="SN号" prop="sn">
          <el-input v-model="deviceForm.sn" placeholder="请输入SN号"></el-input>
        </el-form-item>
        <el-form-item label="设备型号" prop="model">
          <el-input v-model="deviceForm.model" placeholder="请输入设备型号"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="deviceForm.status" placeholder="请选择设备状态">
            <el-option label="正常" value="正常"></el-option>
            <el-option label="故障" value="故障"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDeviceList, addDevice, updateDevice, deleteDevice } from '@/api/rider'

export default {
  name: 'DeviceInfo',
  data() {
    return {
      loading: false,
      deviceList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '添加设备',
      deviceForm: {
        id: null,
        deviceName: '',
        imei: '',
        sn: '',
        model: '',
        status: '正常',
        bindTime: ''
      },
      rules: {
        deviceName: [
          { required: true, message: '请输入设备名称', trigger: 'blur' }
        ],
        imei: [
          { required: true, message: '请输入IMEI号', trigger: 'blur' },
          { pattern: /^\d{15}$/, message: 'IMEI号格式不正确', trigger: 'blur' }
        ],
        sn: [
          { required: true, message: '请输入SN号', trigger: 'blur' }
        ],
        model: [
          { required: true, message: '请输入设备型号', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchDeviceList()
  },
  methods: {
    // 获取设备列表
    fetchDeviceList() {
      this.loading = true
      
      getDeviceList({
        page: this.currentPage,
        limit: this.pageSize
      }).then(response => {
        if (response.data) {
          this.deviceList = response.data.items || []
          this.total = response.data.total || 0
        }
        console.log('获取设备列表成功:', response)
      }).catch(error => {
        console.error('获取设备列表失败:', error)
      }).finally(() => {
        this.loading = false
      })
    },
    // 添加设备
    handleAddDevice() {
      this.dialogTitle = '添加设备'
      this.deviceForm = {
        id: null,
        deviceName: '',
        imei: '',
        sn: '',
        model: '',
        status: '正常',
        bindTime: ''
      }
      this.dialogVisible = true
    },
    // 编辑设备
    handleEdit(row) {
      this.dialogTitle = '编辑设备'
      this.deviceForm = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },
    // 解绑设备
    handleDelete(row) {
      this.$confirm('确认解绑该设备吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteDevice(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '解绑成功!'
          })
          this.fetchDeviceList()
        }).catch(error => {
          console.error('解绑设备失败:', error)
          this.$message({
            type: 'error',
            message: '解绑失败，请重试'
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消解绑'
        })          
      })
    },
    // 提交表单
    submitForm() {
      this.$refs.deviceForm.validate((valid) => {
        if (valid) {
          if (this.deviceForm.id) {
            // 编辑操作
            updateDevice(this.deviceForm.id, this.deviceForm).then(() => {
              this.$message({
                type: 'success',
                message: '编辑成功!'
              })
              this.dialogVisible = false
              this.fetchDeviceList()
            }).catch(error => {
              console.error('更新设备失败:', error)
              this.$message({
                type: 'error',
                message: '编辑失败，请重试'
              })
            })
          } else {
            // 添加操作
            this.deviceForm.bindTime = new Date().toLocaleString()
            addDevice(this.deviceForm).then(() => {
              this.$message({
                type: 'success',
                message: '添加成功!'
              })
              this.dialogVisible = false
              this.fetchDeviceList()
            }).catch(error => {
              console.error('添加设备失败:', error)
              this.$message({
                type: 'error',
                message: '添加失败，请重试'
              })
            })
          }
        } else {
          return false
        }
      })
    },
    // 分页相关方法
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchDeviceList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchDeviceList()
    }
  }
}
</script>

<style scoped>
.device-info-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 修复表格相关样式 */
.el-table {
  width: 100% !important;
}

/* 确保按钮可点击 */
.el-button {
  position: relative;
  z-index: 1;
}

/* 修复在小屏幕上表格内容溢出的问题 */
.el-table__body-wrapper {
  overflow-x: auto;
}

/* 修正对话框位置 */
.el-dialog {
  margin-top: 5vh !important;
}

/* 修复表单间距 */
.el-form-item {
  margin-bottom: 20px;
}

/* 适当调整表格列宽 */
.el-table .cell {
  word-break: break-word;
  line-height: 1.5;
}
</style> 
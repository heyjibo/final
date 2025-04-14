<template>
  <div class="system-settings">
    <el-card>
      <div slot="header" class="clearfix">
        <span>系统设置</span>
      </div>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础设置" name="basic">
          <el-form :model="basicForm" :rules="basicRules" ref="basicForm" label-width="120px" class="settings-form">
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="basicForm.systemName"></el-input>
            </el-form-item>
            
            <el-form-item label="系统Logo">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeLogoUpload">
                <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div class="upload-tip">建议尺寸: 200px * 60px</div>
            </el-form-item>
            
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="basicForm.contactPhone"></el-input>
            </el-form-item>
            
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="basicForm.contactEmail"></el-input>
            </el-form-item>
            
            <el-form-item label="公司地址" prop="companyAddress">
              <el-input v-model="basicForm.companyAddress"></el-input>
            </el-form-item>
            
            <el-form-item label="版权信息" prop="copyright">
              <el-input v-model="basicForm.copyright"></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="骑手设置" name="rider">
          <el-form :model="riderForm" :rules="riderRules" ref="riderForm" label-width="120px" class="settings-form">
            <el-form-item label="工资基数" prop="baseSalary">
              <el-input-number v-model="riderForm.baseSalary" :min="0" :step="100"></el-input-number>
              <span class="form-tip">元/月</span>
            </el-form-item>
            
            <el-form-item label="单量提成比例" prop="orderCommission">
              <el-input-number v-model="riderForm.orderCommission" :min="0" :max="100" :step="0.5"></el-input-number>
              <span class="form-tip">%</span>
            </el-form-item>
            
            <el-form-item label="超时扣款" prop="timeoutPenalty">
              <el-input-number v-model="riderForm.timeoutPenalty" :min="0" :step="1"></el-input-number>
              <span class="form-tip">元/单</span>
            </el-form-item>
            
            <el-form-item label="注册审核">
              <el-switch
                v-model="riderForm.registerReview"
                active-text="需要审核"
                inactive-text="无需审核">
              </el-switch>
            </el-form-item>
            
            <el-form-item label="休假申请">
              <el-switch
                v-model="riderForm.leaveRequest"
                active-text="需要审批"
                inactive-text="无需审批">
              </el-switch>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveRiderSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="系统参数" name="system">
          <el-form :model="systemForm" :rules="systemRules" ref="systemForm" label-width="120px" class="settings-form">
            <el-form-item label="超时时间" prop="orderTimeout">
              <el-input-number v-model="systemForm.orderTimeout" :min="1" :step="5"></el-input-number>
              <span class="form-tip">分钟</span>
            </el-form-item>
            
            <el-form-item label="配送半径" prop="deliveryRadius">
              <el-input-number v-model="systemForm.deliveryRadius" :min="0.5" :step="0.5"></el-input-number>
              <span class="form-tip">公里</span>
            </el-form-item>
            
            <el-form-item label="短信通知">
              <el-switch
                v-model="systemForm.smsNotification"
                active-text="启用"
                inactive-text="禁用">
              </el-switch>
            </el-form-item>
            
            <el-form-item label="订单自动派发">
              <el-switch
                v-model="systemForm.autoDispatch"
                active-text="启用"
                inactive-text="禁用">
              </el-switch>
            </el-form-item>
            
            <el-form-item label="系统维护模式">
              <el-switch
                v-model="systemForm.maintenanceMode"
                active-text="开启"
                inactive-text="关闭">
              </el-switch>
            </el-form-item>
            
            <el-form-item label="地图服务商" prop="mapProvider">
              <el-select v-model="systemForm.mapProvider">
                <el-option label="高德地图" value="amap"></el-option>
                <el-option label="百度地图" value="baidu"></el-option>
                <el-option label="腾讯地图" value="tencent"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveSystemSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SystemSettings',
  data() {
    return {
      activeTab: 'basic',
      
      // 基础设置表单
      basicForm: {
        systemName: '校园外卖骑手系统',
        logoUrl: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        contactPhone: '400-123-4567',
        contactEmail: 'support@example.com',
        companyAddress: '浙江省杭州市西湖区',
        copyright: '© 2023 校园外卖骑手系统'
      },
      basicRules: {
        systemName: [
          { required: true, message: '请输入系统名称', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ],
        contactEmail: [
          { required: true, message: '请输入联系邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        companyAddress: [
          { required: true, message: '请输入公司地址', trigger: 'blur' }
        ],
        copyright: [
          { required: true, message: '请输入版权信息', trigger: 'blur' }
        ]
      },
      
      // 骑手设置表单
      riderForm: {
        baseSalary: 3000,
        orderCommission: 80,
        timeoutPenalty: 5,
        registerReview: true,
        leaveRequest: true
      },
      riderRules: {
        baseSalary: [
          { required: true, message: '请输入工资基数', trigger: 'blur' }
        ],
        orderCommission: [
          { required: true, message: '请输入单量提成比例', trigger: 'blur' }
        ],
        timeoutPenalty: [
          { required: true, message: '请输入超时扣款金额', trigger: 'blur' }
        ]
      },
      
      // 系统参数表单
      systemForm: {
        orderTimeout: 30,
        deliveryRadius: 3,
        smsNotification: true,
        autoDispatch: true,
        maintenanceMode: false,
        mapProvider: 'amap'
      },
      systemRules: {
        orderTimeout: [
          { required: true, message: '请输入超时时间', trigger: 'blur' }
        ],
        deliveryRadius: [
          { required: true, message: '请输入配送半径', trigger: 'blur' }
        ],
        mapProvider: [
          { required: true, message: '请选择地图服务商', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    // 加载保存的设置
    this.loadSettings();
  },
  methods: {
    // 加载已保存的设置
    loadSettings() {
      try {
        // 加载基础设置
        const savedBasicSettings = localStorage.getItem('basicSettings');
        if (savedBasicSettings) {
          this.basicForm = { ...this.basicForm, ...JSON.parse(savedBasicSettings) };
        }
        
        // 加载骑手设置
        const savedRiderSettings = localStorage.getItem('riderSettings');
        if (savedRiderSettings) {
          this.riderForm = { ...this.riderForm, ...JSON.parse(savedRiderSettings) };
        }
        
        // 加载系统参数
        const savedSystemSettings = localStorage.getItem('systemSettings');
        if (savedSystemSettings) {
          this.systemForm = { ...this.systemForm, ...JSON.parse(savedSystemSettings) };
        }
        
        console.log('设置加载成功');
      } catch (error) {
        console.error('加载设置失败:', error);
      }
    },
    
    // 保存基础设置
    saveBasicSettings() {
      this.$refs.basicForm.validate(valid => {
        if (valid) {
          // 保存到本地存储
          localStorage.setItem('basicSettings', JSON.stringify(this.basicForm));
          console.log('基础设置已保存:', this.basicForm);
          this.$message.success('基础设置保存成功');
        } else {
          return false;
        }
      });
    },
    
    // 保存骑手设置
    saveRiderSettings() {
      this.$refs.riderForm.validate(valid => {
        if (valid) {
          // 保存到本地存储
          localStorage.setItem('riderSettings', JSON.stringify(this.riderForm));
          console.log('骑手设置已保存:', this.riderForm);
          this.$message.success('骑手设置保存成功');
        } else {
          return false;
        }
      });
    },
    
    // 保存系统参数
    saveSystemSettings() {
      this.$refs.systemForm.validate(valid => {
        if (valid) {
          // 保存到本地存储
          localStorage.setItem('systemSettings', JSON.stringify(this.systemForm));
          console.log('系统参数已保存:', this.systemForm);
          this.$message.success('系统参数保存成功');
        } else {
          return false;
        }
      });
    },
    
    // 上传Logo前的处理
    beforeLogoUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      
      if (!isImage) {
        this.$message.error('上传Logo图片只能是图片格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传Logo图片大小不能超过 2MB!');
      }
      
      if (isImage && isLt2M) {
        // 模拟上传成功，实际项目中应该调用上传API
        this.basicForm.logoUrl = URL.createObjectURL(file);
        // 上传成功后也要保存设置
        this.saveBasicSettings();
        this.$message.success('Logo上传成功');
      }
      
      // 阻止默认上传行为
      return false;
    }
  }
}
</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.settings-form {
  max-width: 600px;
  margin-top: 20px;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 60px;
  line-height: 60px;
  text-align: center;
}

.avatar {
  max-width: 200px;
  max-height: 60px;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style> 
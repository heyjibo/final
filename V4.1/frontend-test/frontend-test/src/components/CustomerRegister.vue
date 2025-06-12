<template>
  <div class="register-container">
    <div class="register-box">
      <h2>顾客注册</h2>

      <el-form ref="form" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码（8位以上，含大小写/符号至少一种）"></el-input>
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入配送地址"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phonenumber">
          <el-input
              v-model="form.phonenumber"
              placeholder="请输入手机号"
              maxlength="11"
              show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">立即注册</el-button>
          <el-button @click="$router.go(-1)">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        name: '',
        password: '',
        address: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 8, message: '密码长度不能少于8位', trigger: 'blur' },
          {
            pattern: /^(?=.*[a-zA-Z])(?=.*[\W_])|(?=.*[a-z])(?=.*[A-Z]).{8,}$/,
            message: '密码需要包含大小写/符号至少一种）',
            trigger: 'blur'
          }
        ],
        address: [
          { required: true, message: '请输入配送地址', trigger: 'blur' }
        ],
        phonenumber: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async submitForm() {
      try {
        const valid = await this.$refs.form.validate()
        if (!valid) return

        const response = await this.$axios.post('/api/customer/register', this.form)

        this.$message.success('注册成功！您的顾客ID：' + response.data.workId)
        this.$router.push('/login')
      } catch (error) {
        this.$message.error(error.response?.data?.error || '注册失败')
      }
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.register-box {
  width: 100%;
  max-width: 500px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
</style>
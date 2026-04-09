<template>
  <div class="config-container">
    <div class="page-header">
      <h1 class="page-title">系统配置</h1>
      <p class="page-subtitle">管理系统参数和配置</p>
    </div>

    <!-- 配置选项卡 -->
    <el-card>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 收费配置 -->
        <el-tab-pane label="收费配置" name="property">
          <el-form 
            :model="propertyConfig" 
            :rules="propertyRules" 
            ref="propertyFormRef" 
            label-width="150px"
            v-loading="loading"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="物业费单价" prop="propertyFeePerSqm">
                  <el-input-number 
                    v-model="propertyConfig.propertyFeePerSqm" 
                    :min="0" 
                    :precision="2"
                    :step="0.1"
                    placeholder="请输入物业费单价"
                  />
                  <span class="unit">元/㎡·月</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="停车费" prop="parkingFeeMonthly">
                  <el-input-number 
                    v-model="propertyConfig.parkingFeeMonthly" 
                    :min="0" 
                    :precision="2"
                    :step="10"
                    placeholder="请输入停车费"
                  />
                  <span class="unit">元/月</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-alert 
              title="提示" 
              description="当前仅支持物业费与停车费配置，修改后将影响新生成的账单金额，历史账单不受影响。" 
              type="info" 
              :closable="false"
              style="margin-bottom: 20px"
            />
            <el-form-item>
              <el-button type="primary" @click="savePropertyConfig">
                <el-icon><Select /></el-icon>
                <span>保存配置</span>
              </el-button>
              <el-button @click="resetPropertyConfig">
                <el-icon><RefreshLeft /></el-icon>
                <span>恢复默认</span>
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 邮件配置 -->
        <el-tab-pane label="邮件配置" name="email">
          <el-form 
            :model="emailConfig" 
            :rules="emailRules" 
            ref="emailFormRef" 
            label-width="150px"
            v-loading="loading"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="SMTP服务器" prop="host">
                  <el-input v-model="emailConfig.host" placeholder="例如：smtp.qq.com" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="SMTP端口" prop="port">
                  <el-input-number 
                    v-model="emailConfig.port" 
                    :min="1" 
                    :max="65535"
                    placeholder="例如：587"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="发件人邮箱" prop="username">
                  <el-input v-model="emailConfig.username" placeholder="例如：service@example.com" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱密码" prop="password">
                  <el-input 
                    v-model="emailConfig.password" 
                    type="password" 
                    show-password
                    placeholder="请输入邮箱密码或授权码" 
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="发件人名称" prop="from">
                  <el-input v-model="emailConfig.from" placeholder="例如：物业管理系统" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用SSL">
                  <el-switch v-model="emailConfig.ssl" />
                  <span class="tip">建议开启以提高安全性</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-alert 
              title="配置说明" 
              type="warning" 
              :closable="false"
              style="margin-bottom: 20px"
            >
              <template #default>
                <div>1. SMTP服务器和端口请根据邮箱服务商提供的信息填写</div>
                <div>2. 密码通常为邮箱的授权码，而非登录密码</div>
                <div>3. 配置完成后请先测试，确保邮件能正常发送</div>
              </template>
            </el-alert>
            <el-form-item>
              <el-button type="primary" @click="saveEmailConfig">
                <el-icon><Select /></el-icon>
                <span>保存配置</span>
              </el-button>
              <el-button @click="testEmailConfig">
                <el-icon><Promotion /></el-icon>
                <span>测试邮件</span>
              </el-button>
              <el-button @click="resetEmailConfig">
                <el-icon><RefreshLeft /></el-icon>
                <span>恢复默认</span>
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 测试邮件对话框 -->
    <el-dialog v-model="testEmailVisible" title="发送测试邮件" width="500px">
      <el-form :model="testEmailForm" label-width="100px">
        <el-form-item label="收件人邮箱">
          <el-input v-model="testEmailForm.to" placeholder="请输入收件人邮箱" />
        </el-form-item>
        <el-form-item label="邮件主题">
          <el-input v-model="testEmailForm.subject" placeholder="请输入邮件主题" />
        </el-form-item>
        <el-form-item label="邮件内容">
          <el-input 
            v-model="testEmailForm.content" 
            type="textarea" 
            :rows="4"
            placeholder="请输入邮件内容" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testEmailVisible = false">取消</el-button>
        <el-button type="primary" @click="sendTestEmail">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Select, RefreshLeft, Promotion } from '@element-plus/icons-vue'
import { 
  getPropertyFeeConfig, 
  updatePropertyFeeConfig,
  getEmailConfig,
  updateEmailConfig,
  testEmailConfig as testEmail
} from '@/api/config'

const activeTab = ref('property')
const loading = ref(false)

// 物业费配置
const propertyFormRef = ref(null)
const propertyConfig = reactive({
  propertyFeePerSqm: 2.5,
  parkingFeeMonthly: 200
})
const propertyRules = {
  propertyFeePerSqm: [{ required: true, message: '请输入物业费单价', trigger: 'blur' }],
  parkingFeeMonthly: [{ required: true, message: '请输入停车费', trigger: 'blur' }]
}

// 邮件配置
const emailFormRef = ref(null)
const emailConfig = reactive({
  host: '',
  port: 587,
  username: '',
  password: '',
  from: '',
  ssl: true
})
const emailRules = {
  host: [{ required: true, message: '请输入SMTP服务器', trigger: 'blur' }],
  port: [{ required: true, message: '请输入SMTP端口', trigger: 'blur' }],
  username: [
    { required: true, message: '请输入发件人邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入邮箱密码', trigger: 'blur' }],
  from: [{ required: true, message: '请输入发件人名称', trigger: 'blur' }]
}

// 测试邮件对话框
const testEmailVisible = ref(false)
const testEmailForm = reactive({
  to: '',
  subject: '测试邮件',
  content: '这是一封测试邮件，如果您收到此邮件，说明邮件配置正确。'
})

// 加载配置数据
const loadConfigs = async () => {
  loading.value = true
  try {
    const propertyRes = await getPropertyFeeConfig()
    if (propertyRes.code === 200 && propertyRes.data) {
      Object.assign(propertyConfig, {
        propertyFeePerSqm: Number(propertyRes.data.propertyFeePerSqm) || 2.5,
        parkingFeeMonthly: Number(propertyRes.data.parkingFeeMonthly) || 200
      })
    }

    const emailRes = await getEmailConfig()
    if (emailRes.code === 200 && emailRes.data) {
      Object.assign(emailConfig, {
        host: emailRes.data.host || '',
        port: Number(emailRes.data.port) || 587,
        username: emailRes.data.username || '',
        password: emailRes.data.password || '',
        from: emailRes.data.from || '',
        ssl: emailRes.data.ssl === 'true' || emailRes.data.ssl === true
      })
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabClick = () => {}

const savePropertyConfig = async () => {
  const valid = await propertyFormRef.value.validate()
  if (!valid) return

  try {
    const res = await updatePropertyFeeConfig({
      propertyFeePerSqm: String(propertyConfig.propertyFeePerSqm),
      parkingFeeMonthly: String(propertyConfig.parkingFeeMonthly)
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      loadConfigs()
    }
  } catch (error) {
    console.error('保存物业费配置失败:', error)
    ElMessage.error('保存失败')
  }
}

const resetPropertyConfig = () => {
  ElMessageBox.confirm('确认恢复为默认配置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    Object.assign(propertyConfig, {
      propertyFeePerSqm: 2.5,
      parkingFeeMonthly: 200
    })
    ElMessage.success('已恢复默认配置')
  })
}

const saveEmailConfig = async () => {
  const valid = await emailFormRef.value.validate()
  if (!valid) return

  try {
    const res = await updateEmailConfig({
      host: emailConfig.host,
      port: String(emailConfig.port),
      username: emailConfig.username,
      password: emailConfig.password,
      from: emailConfig.from,
      ssl: String(emailConfig.ssl)
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      loadConfigs()
    }
  } catch (error) {
    console.error('保存邮件配置失败:', error)
    ElMessage.error('保存失败')
  }
}

const testEmailConfig = () => {
  testEmailVisible.value = true
}

const sendTestEmail = async () => {
  if (!testEmailForm.to) {
    ElMessage.warning('请输入收件人邮箱')
    return
  }

  try {
    const res = await testEmail({
      to: testEmailForm.to,
      subject: testEmailForm.subject,
      content: testEmailForm.content
    })
    if (res.code === 200) {
      ElMessage.success('测试邮件发送成功，请查收')
      testEmailVisible.value = false
    }
  } catch (error) {
    console.error('发送测试邮件失败:', error)
    ElMessage.error('发送测试邮件失败')
  }
}

const resetEmailConfig = () => {
  ElMessageBox.confirm('确认恢复为默认配置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    Object.assign(emailConfig, {
      host: 'smtp.example.com',
      port: 587,
      username: '',
      password: '',
      from: '',
      ssl: true
    })
    ElMessage.success('已恢复默认配置')
  })
}

onMounted(() => {
  loadConfigs()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.config-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
  
  .page-title {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 8px 0;
  }
  
  .page-subtitle {
    font-size: 14px;
    color: $text-secondary;
    margin: 0;
  }
}

.unit {
  margin-left: 8px;
  color: $text-secondary;
  font-size: 14px;
}

.tip {
  margin-left: 8px;
  color: $text-muted;
  font-size: 12px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-input-number) {
  width: 200px;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style>

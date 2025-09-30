<template>
  <div class="activity-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
      <h1 class="page-title">活动管理</h1>
      <p class="page-subtitle">创建和管理社区活动</p>
    </div>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        创建活动
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card total">
        <div class="stat-icon"><el-icon><Calendar /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total || 0 }}</div>
          <div class="stat-label">总活动数</div>
        </div>
      </div>
      <div class="stat-card ongoing">
        <div class="stat-icon"><el-icon><Clock /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.ongoing || 0 }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card upcoming">
        <div class="stat-icon"><el-icon><Timer /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.upcoming || 0 }}</div>
          <div class="stat-label">未开始</div>
        </div>
      </div>
      <div class="stat-card participants">
        <div class="stat-icon"><el-icon><User /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalParticipants || 0 }}</div>
          <div class="stat-label">总参与人数</div>
        </div>
      </div>
    </div>

    <!-- 筛选搜索 -->
    <el-card class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item label="活动类型">
          <el-select v-model="filters.type" placeholder="全部" clearable style="width: 140px">
            <el-option label="文体活动" value="SPORTS" />
            <el-option label="节日庆典" value="FESTIVAL" />
            <el-option label="公益活动" value="CHARITY" />
            <el-option label="教育培训" value="EDUCATION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待发布" value="DRAFT" />
            <el-option label="报名中" value="REGISTRATION" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.keyword" placeholder="搜索活动标题" prefix-icon="Search" style="width: 200px" @keyup.enter="loadActivities" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadActivities">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 活动列表 -->
    <el-card class="table-card">
      <el-table :data="activities" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="活动标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="150" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="maxParticipants" label="人数" width="100" align="center">
          <template #default="{ row }">
            {{ row.currentParticipants || 0 }}/{{ row.maxParticipants || '不限' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewActivity(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="viewRegistrations(row)">报名</el-button>
            <el-button link type="warning" size="small" @click="editActivity(row)">编辑</el-button>
            <el-button link :type="row.status === 'DRAFT' ? 'success' : 'info'" size="small" @click="toggleStatus(row)" v-if="row.status === 'DRAFT' || row.status === 'REGISTRATION'">
              {{ row.status === 'DRAFT' ? '发布' : '取消' }}
          </el-button>
            <el-button link type="danger" size="small" @click="deleteActivity(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadActivities"
        @current-change="loadActivities"
        class="pagination"
      />
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="活动标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入活动标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                <el-option label="文体活动" value="SPORTS" />
                <el-option label="节日庆典" value="FESTIVAL" />
                <el-option label="公益活动" value="CHARITY" />
                <el-option label="教育培训" value="EDUCATION" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动地点" prop="location">
              <el-input v-model="form.location" placeholder="请输入活动地点" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名开始" prop="registrationStart">
              <el-date-picker v-model="form.registrationStart" type="datetime" placeholder="报名开始时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名截止" prop="registrationEnd">
              <el-date-picker v-model="form.registrationEnd" type="datetime" placeholder="报名截止时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人数限制" prop="maxParticipants">
              <el-input-number v-model="form.maxParticipants" :min="0" :max="10000" placeholder="0表示不限" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动简介" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入活动简介（显示在列表中）" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动摘要">
              <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入活动摘要" maxlength="300" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动详情">
              <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入活动详细内容" maxlength="5000" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否免费">
              <el-radio-group v-model="form.isFree">
                <el-radio :label="1">免费</el-radio>
                <el-radio :label="0">收费</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.isFree === 0">
            <el-form-item label="活动费用">
              <el-input-number v-model="form.fee" :min="0" :precision="2" placeholder="费用" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动图片">
              <el-input v-model="form.imageUrl" placeholder="请输入活动主图URL" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveActivity" :loading="saving">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>

    <!-- 活动详情对话框 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动标题" :span="2">{{ currentActivity.title }}</el-descriptions-item>
        <el-descriptions-item label="活动类型"><el-tag :type="getTypeColor(currentActivity.type)">{{ getTypeLabel(currentActivity.type) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="getStatusColor(currentActivity.status)">{{ getStatusLabel(currentActivity.status) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="活动地点" :span="2">{{ currentActivity.location }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentActivity.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentActivity.endTime }}</el-descriptions-item>
        <el-descriptions-item label="报名开始">{{ currentActivity.registrationStart }}</el-descriptions-item>
        <el-descriptions-item label="报名截止">{{ currentActivity.registrationEnd }}</el-descriptions-item>
        <el-descriptions-item label="参与人数">{{ currentActivity.currentParticipants || 0 }}/{{ currentActivity.maxParticipants || '不限' }}</el-descriptions-item>
        <el-descriptions-item label="活动简介" :span="2">{{ currentActivity.description }}</el-descriptions-item>
        <el-descriptions-item label="活动摘要" :span="2" v-if="currentActivity.summary">{{ currentActivity.summary }}</el-descriptions-item>
        <el-descriptions-item label="活动详情" :span="2" v-if="currentActivity.content">{{ currentActivity.content }}</el-descriptions-item>
        <el-descriptions-item label="是否免费">{{ currentActivity.isFree ? '免费' : '收费' }}</el-descriptions-item>
        <el-descriptions-item label="费用" v-if="!currentActivity.isFree">{{ currentActivity.fee || 0 }}元</el-descriptions-item>
        <el-descriptions-item label="组织者">{{ currentActivity.organizerName }}</el-descriptions-item>
        <el-descriptions-item label="活动图片" :span="2" v-if="currentActivity.imageUrl">
          <el-image :src="currentActivity.imageUrl" style="width: 100px; height: 100px" fit="cover" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentActivity.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 报名列表对话框 -->
    <el-dialog v-model="registrationVisible" title="报名列表" width="1000px">
      <el-table :data="registrations" v-loading="registrationLoading" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="userName" label="报名人" width="120" />
        <el-table-column prop="userPhone" label="联系电话" width="140" />
        <el-table-column prop="participants" label="参与人数" width="100" align="center" />
        <el-table-column prop="registrationTime" label="报名时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getRegistrationStatusColor(row.status)">{{ getRegistrationStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" size="small" @click="confirmRegistration(row)" v-if="row.status === 'PENDING'">确认</el-button>
            <el-button link type="danger" size="small" @click="cancelRegistrationItem(row)" v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="registrationPagination.pageNum"
        v-model:page-size="registrationPagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="registrationPagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadRegistrations"
        @current-change="loadRegistrations"
        class="pagination"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar, Clock, Timer, User, Search, Edit, Delete, View, Document, Warning, Check, Close } from '@element-plus/icons-vue'
import { getActivityPage, getActivityDetail, createActivity, updateActivity, deleteActivity as deleteActivityApi, publishActivity, cancelActivity, getSignUpList, confirmRegistration as confirmRegistrationApi, cancelSignUp } from '@/api/activity'

const loading = ref(false)
const saving = ref(false)
const activities = ref([])
const stats = reactive({ total: 0, ongoing: 0, upcoming: 0, totalParticipants: 0 })

const filters = reactive({ type: '', status: '', keyword: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('创建活动')
const isEdit = ref(false)
const detailVisible = ref(false)
const registrationVisible = ref(false)

const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  type: '',
  organizerId: null,
  location: '',
  startTime: '',
  endTime: '',
  registrationStart: '',
  registrationEnd: '',
  maxParticipants: 0,
  currentParticipants: 0,
  description: '',
  content: '',
  summary: '',
  status: 'DRAFT',
  isFree: 1,
  fee: null,
  imageUrl: '',
  images: ''
})

const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  registrationEnd: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }]
}

const currentActivity = ref({})
const registrations = ref([])
const registrationLoading = ref(false)
const registrationPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const currentActivityId = ref(null)

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await getActivityPage({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...filters })
    if (res.code === 200) {
      activities.value = res.data.records || []
      pagination.total = res.data.total || 0
      calculateStats()
    }
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载活动列表失败')
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  const now = new Date()
  stats.total = pagination.total
  stats.ongoing = activities.value.filter(a => {
    if (!a.startTime || !a.endTime) return false
    return new Date(a.startTime) <= now && now <= new Date(a.endTime) && a.status === 'ONGOING'
  }).length
  stats.upcoming = activities.value.filter(a => {
    if (!a.startTime) return false
    return new Date(a.startTime) > now && (a.status === 'REGISTRATION' || a.status === 'DRAFT')
  }).length
  stats.totalParticipants = activities.value.reduce((sum, a) => sum + (a.currentParticipants || 0), 0)
}

const resetFilters = () => {
  Object.assign(filters, { type: '', status: '', keyword: '' })
  loadActivities()
}

const showCreateDialog = () => {
  isEdit.value = false
  dialogTitle.value = '创建活动'
  resetForm()
  dialogVisible.value = true
}

const editActivity = async (row) => {
  try {
    const res = await getActivityDetail(row.id)
    if (res.code === 200) {
      isEdit.value = true
      dialogTitle.value = '编辑活动'
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载活动详情失败')
  }
}

const saveActivity = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      // 准备提交数据
      const submitData = { ...form }
      
      // 如果是创建新活动且没有组织者ID，从本地存储获取当前用户ID
      if (!isEdit.value && !submitData.organizerId) {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        submitData.organizerId = userInfo.id || 1001
      }
      
      const res = isEdit.value ? await updateActivity(form.id, submitData) : await createActivity(submitData)
      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadActivities()
      }
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    id: null,
    title: '',
    type: '',
    organizerId: null,
    location: '',
    startTime: '',
    endTime: '',
    registrationStart: '',
    registrationEnd: '',
    maxParticipants: 0,
    currentParticipants: 0,
    description: '',
    content: '',
    summary: '',
    status: 'DRAFT',
    isFree: 1,
    fee: null,
    imageUrl: '',
    images: ''
  })
}

const viewActivity = async (row) => {
  try {
    const res = await getActivityDetail(row.id)
    if (res.code === 200) {
      currentActivity.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
  }
}

const deleteActivity = (row) => {
  ElMessageBox.confirm('确定要删除该活动吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await deleteActivityApi(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadActivities()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const toggleStatus = (row) => {
  const action = row.status === 'DRAFT' ? '发布' : '取消'
  ElMessageBox.confirm(`确定要${action}该活动吗？`, '提示', { type: 'warning' }).then(async () => {
    try {
      const res = row.status === 'DRAFT' ? await publishActivity(row.id) : await cancelActivity(row.id)
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        loadActivities()
      }
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

const viewRegistrations = async (row) => {
  currentActivityId.value = row.id
  registrationPagination.pageNum = 1
  registrationVisible.value = true
  await loadRegistrations()
}

const loadRegistrations = async () => {
  registrationLoading.value = true
  try {
    const res = await getSignUpList(currentActivityId.value, { pageNum: registrationPagination.pageNum, pageSize: registrationPagination.pageSize })
    if (res.code === 200) {
      registrations.value = res.data.records || []
      registrationPagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载报名列表失败')
  } finally {
    registrationLoading.value = false
  }
}

const confirmRegistration = (row) => {
  ElMessageBox.confirm('确定要确认该报名吗？', '提示', { type: 'info' }).then(async () => {
    try {
      const res = await confirmRegistrationApi(row.id)
      if (res.code === 200) {
        ElMessage.success('确认成功')
        loadRegistrations()
      }
    } catch (error) {
      ElMessage.error('确认失败')
    }
  })
}

const cancelRegistrationItem = (row) => {
  ElMessageBox.prompt('请输入取消原因', '取消报名', {
    inputPattern: /.+/,
    inputErrorMessage: '请输入取消原因'
  }).then(async ({ value }) => {
    try {
      const res = await cancelSignUp(currentActivityId.value, value)
      if (res.code === 200) {
        ElMessage.success('取消成功')
        loadRegistrations()
      }
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

const getTypeColor = (type) => ({ SPORTS: 'success', FESTIVAL: 'danger', CHARITY: 'warning', EDUCATION: 'primary', OTHER: 'info' }[type] || 'info')
const getTypeLabel = (type) => ({ SPORTS: '文体活动', FESTIVAL: '节日庆典', CHARITY: '公益活动', EDUCATION: '教育培训', OTHER: '其他' }[type] || type)
const getStatusColor = (status) => ({ DRAFT: 'info', REGISTRATION: 'warning', ONGOING: 'success', COMPLETED: '', CANCELLED: 'danger' }[status] || 'info')
const getStatusLabel = (status) => ({ DRAFT: '待发布', REGISTRATION: '报名中', ONGOING: '进行中', COMPLETED: '已结束', CANCELLED: '已取消' }[status] || status)
const getRegistrationStatusColor = (status) => ({ PENDING: 'warning', CONFIRMED: 'success', CANCELLED: 'info', ATTENDED: 'primary' }[status] || 'info')
const getRegistrationStatusLabel = (status) => ({ PENDING: '待确认', CONFIRMED: '已确认', CANCELLED: '已取消', ATTENDED: '已参加' }[status] || status)

onMounted(() => { loadActivities() })
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.activity-container {
  width: 100%;
  max-width: 100%;
  padding: 20px;
  box-sizing: border-box;
  overflow-x: hidden;

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      background: $bg-secondary;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: 0 2px 8px $shadow-light;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
      }

      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: $text-primary;
        }

        .stat-label {
          font-size: 14px;
          color: $text-secondary;
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
      }

      &.ongoing .stat-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
      }

      &.upcoming .stat-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        color: white;
      }

      &.participants .stat-icon {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        color: white;
      }
    }
  }

  .filter-card, .table-card {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

@media screen and (max-width: 1200px) {
  .activity-container .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .activity-container {
    padding: 10px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    .stats-cards {
      grid-template-columns: 1fr;
    }

    :deep(.el-form-item) {
      display: block;
      margin-bottom: 12px;
    }
  }
}
</style>

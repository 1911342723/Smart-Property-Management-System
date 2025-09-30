<template>
  <div class="workorder-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.total }}</div>
              <div class="stats-label">总工单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pending }}</div>
              <div class="stats-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon processing">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.processing }}</div>
              <div class="stats-label">处理中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon completed">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.completed }}</div>
              <div class="stats-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工单类别">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable style="width: 160px">
            <el-option label="报修" value="REPAIR" />
            <el-option label="投诉" value="COMPLAINT" />
            <el-option label="咨询" value="INQUIRY" />
            <el-option label="建议" value="SUGGESTION" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 160px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择" clearable style="width: 160px">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
            <el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>查询</span>
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工单列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>工单列表</span>
          <el-tag type="info" size="small">
            <el-icon><InfoFilled /></el-icon>
            业主提交工单、维修师傅处理工单、业主评价均在小程序端操作
          </el-tag>
        </div>
      </template>

      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="orderNo" label="工单编号" width="140" fixed="left" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)">
              {{ getCategoryLabel(row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">
              {{ getPriorityLabel(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitterName" label="提交人" width="100" />
        <el-table-column prop="assigneeName" label="处理人" width="100" />
        <el-table-column prop="roomAddress" label="房屋地址" width="180" show-overflow-tooltip />
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              详情
            </el-button>
            <el-button 
              link 
              type="warning" 
              size="small" 
              @click="handleAssign(row)"
              v-if="row.status === 'PENDING'"
            >
              分配
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 工单详情对话框 -->
    <el-dialog 
      v-model="detailVisible" 
      title="工单详情" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="工单编号">
              {{ detailData.orderNo }}
            </el-descriptions-item>
            <el-descriptions-item label="工单类别">
              <el-tag :type="getCategoryType(detailData.category)">
                {{ getCategoryLabel(detailData.category) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="标题" :span="2">
              {{ detailData.title }}
            </el-descriptions-item>
            <el-descriptions-item label="内容" :span="2">
              <div class="content-text">{{ detailData.content }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="提交人">
              {{ detailData.submitterName }}
            </el-descriptions-item>
            <el-descriptions-item label="房屋地址">
              {{ detailData.roomAddress }}
            </el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="getPriorityType(detailData.priority)">
                {{ getPriorityLabel(detailData.priority) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(detailData.status)">
                {{ getStatusLabel(detailData.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理人">
              {{ detailData.assigneeName || '未分配' }}
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">
              {{ detailData.submitTime }}
            </el-descriptions-item>
            <el-descriptions-item label="费用" v-if="detailData.cost">
              ¥{{ detailData.cost }}
            </el-descriptions-item>
            <el-descriptions-item label="评分" v-if="detailData.rating">
              <el-rate v-model="detailData.rating" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="反馈意见" :span="2" v-if="detailData.feedback">
              <div class="content-text">{{ detailData.feedback }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="处理流程" name="timeline">
          <el-timeline>
            <el-timeline-item 
              :timestamp="detailData.submitTime" 
              placement="top"
              color="#409EFF"
            >
              <h4>工单提交</h4>
              <p>提交人：{{ detailData.submitterName }}</p>
              <p>工单类别：{{ getCategoryLabel(detailData.category) }}</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.assigneeName"
              :timestamp="detailData.assignTime" 
              placement="top"
              color="#E6A23C"
            >
              <h4>分配处理人</h4>
              <p>处理人：{{ detailData.assigneeName }}</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.status === 'PROCESSING' || detailData.status === 'COMPLETED'"
              :timestamp="detailData.startTime" 
              placement="top"
              color="#409EFF"
            >
              <h4>开始处理</h4>
              <p>{{ detailData.assigneeName }} 正在处理工单...</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.completeTime"
              :timestamp="detailData.completeTime" 
              placement="top"
              color="#67C23A"
            >
              <h4>处理完成</h4>
              <p v-if="detailData.cost">费用：¥{{ detailData.cost }}</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.feedback"
              timestamp="已评价" 
              placement="top"
              color="#67C23A"
            >
              <h4>用户评价</h4>
              <p>评分：<el-rate v-model="detailData.rating" disabled size="small" /></p>
              <p>反馈：{{ detailData.feedback }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 分配对话框 -->
    <el-dialog 
      v-model="assignVisible" 
      title="分配工单" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.assigneeId" placeholder="请选择处理人" style="width: 100%">
            <el-option 
              v-for="user in workerList" 
              :key="user.id" 
              :label="user.realName" 
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getWorkOrderList, 
  getWorkOrderDetail, 
  assignWorkOrder, 
  deleteWorkOrder
} from '@/api/workorder'
import { getUserList } from '@/api/user'

// 统计数据
const stats = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0
})

// 搜索表单
const searchForm = reactive({
  category: '',
  status: '',
  priority: ''
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})
const activeTab = ref('info')

// 分配对话框
const assignVisible = ref(false)
const assignForm = reactive({
  orderId: null,
  assigneeId: null
})
const workerList = ref([])

// 获取统计数据
const fetchStats = () => {
  // 从列表数据中计算统计
  const total = tableData.value.length
  const pending = tableData.value.filter(item => item.status === 'PENDING').length
  const processing = tableData.value.filter(item => item.status === 'PROCESSING').length
  const completed = tableData.value.filter(item => item.status === 'COMPLETED').length
  
  stats.total = pagination.total || total
  stats.pending = pending
  stats.processing = processing
  stats.completed = completed
}

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getWorkOrderList(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
      fetchStats()
    } else {
      ElMessage.warning(res.msg || '获取工单列表失败')
    }
  } catch (error) {
    console.error('获取工单列表失败:', error)
    ElMessage.error('获取工单列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    category: '',
    status: '',
    priority: ''
  })
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getWorkOrderDetail(row.id)
    if (res.code === 200) {
      detailData.value = res.data
      activeTab.value = 'info'
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取工单详情失败:', error)
    ElMessage.error('获取工单详情失败')
  }
}

// 获取处理人列表
const fetchWorkerList = async () => {
  try {
    const res = await getUserList({ role: 'WORKER' })
    if (res.code === 200) {
      workerList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取处理人列表失败:', error)
  }
}

// 分配工单
const handleAssign = (row) => {
  assignForm.orderId = row.id
  assignForm.assigneeId = null
  assignVisible.value = true
  
  if (workerList.value.length === 0) {
    fetchWorkerList()
  }
}

// 提交分配
const submitAssign = async () => {
  if (!assignForm.assigneeId) {
    ElMessage.warning('请选择处理人')
    return
  }
  
  try {
    const res = await assignWorkOrder(assignForm.orderId, assignForm.assigneeId)
    if (res.code === 200) {
      ElMessage.success('分配成功')
      assignVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('分配工单失败:', error)
    ElMessage.error('分配工单失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该工单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteWorkOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      console.error('删除工单失败:', error)
      ElMessage.error('删除工单失败')
    }
  })
}

// 分页改变
const handleSizeChange = () => {
  fetchData()
}

const handleCurrentChange = () => {
  fetchData()
}

// 获取类别类型
const getCategoryType = (category) => {
  const typeMap = {
    REPAIR: '',
    COMPLAINT: 'danger',
    INQUIRY: 'info',
    SUGGESTION: 'success'
  }
  return typeMap[category] || ''
}

// 获取类别标签
const getCategoryLabel = (category) => {
  const labelMap = {
    REPAIR: '报修',
    COMPLAINT: '投诉',
    INQUIRY: '咨询',
    SUGGESTION: '建议'
  }
  return labelMap[category] || category
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const typeMap = {
    LOW: 'info',
    MEDIUM: '',
    HIGH: 'warning',
    URGENT: 'danger'
  }
  return typeMap[priority] || ''
}

// 获取优先级标签
const getPriorityLabel = (priority) => {
  const labelMap = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
    URGENT: '紧急'
  }
  return labelMap[priority] || priority
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    PROCESSING: '',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return typeMap[status] || ''
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return labelMap[status] || status
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.workorder-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stats-content {
  display: flex;
  align-items: center;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  margin-right: 20px;
}

.stats-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-icon.processing {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stats-icon.completed {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .el-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content-text {
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>

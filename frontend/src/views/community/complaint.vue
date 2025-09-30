<template>
  <div class="complaint-container">
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
              <div class="stats-label">总投诉数</div>
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
            <div class="stats-icon resolved">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.resolved }}</div>
              <div class="stats-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="投诉类型">
          <el-select v-model="searchForm.complaintType" placeholder="请选择" clearable style="width: 160px">
            <el-option label="设施维护" value="设施维护" />
            <el-option label="环境卫生" value="环境卫生" />
            <el-option label="安全问题" value="安全问题" />
            <el-option label="噪音扰民" value="噪音扰民" />
            <el-option label="停车问题" value="停车问题" />
            <el-option label="服务态度" value="服务态度" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 160px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已分配" value="ASSIGNED" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="searchForm.urgencyLevel" placeholder="请选择" clearable style="width: 160px">
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

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>投诉列表</span>
          <el-tag type="info" size="small">
            <el-icon><InfoFilled /></el-icon>
            业主通过小程序端提交投诉
          </el-tag>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="complaintNo" label="投诉编号" width="150" />
        <el-table-column prop="complaintType" label="投诉类型" width="120" />
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="complainantName" label="投诉人" width="100" />
        <el-table-column prop="urgencyLevel" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgencyLevel)">
              {{ getUrgencyLabel(row.urgencyLevel) }}
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
        <el-table-column prop="handlerName" label="处理人" width="100" />
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
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
            <el-button 
              link 
              type="primary" 
              size="small" 
              @click="handleStart(row)"
              v-if="row.status === 'ASSIGNED'"
            >
              开始处理
            </el-button>
            <el-button 
              link 
              type="success" 
              size="small" 
              @click="handleComplete(row)"
              v-if="row.status === 'PROCESSING'"
            >
              完成
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="投诉详情" width="900px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="投诉编号">
              {{ detailData.complaintNo }}
            </el-descriptions-item>
            <el-descriptions-item label="投诉类型">
              {{ detailData.complaintType }}
            </el-descriptions-item>
            <el-descriptions-item label="投诉标题" :span="2">
              {{ detailData.title }}
            </el-descriptions-item>
            <el-descriptions-item label="投诉内容" :span="2">
              <div class="content-text">{{ detailData.content }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="投诉人">
              {{ detailData.complainantName }}
            </el-descriptions-item>
            <el-descriptions-item label="联系方式">
              {{ detailData.complainantPhone }}
            </el-descriptions-item>
            <el-descriptions-item label="紧急程度">
              <el-tag :type="getUrgencyType(detailData.urgencyLevel)">
                {{ getUrgencyLabel(detailData.urgencyLevel) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(detailData.status)">
                {{ getStatusLabel(detailData.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理人">
              {{ detailData.handlerName || '未分配' }}
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">
              {{ detailData.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="处理结果" :span="2" v-if="detailData.handleResult">
              <div class="content-text">{{ detailData.handleResult }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="满意度评分" v-if="detailData.satisfactionRating">
              <el-rate v-model="detailData.satisfactionRating" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="反馈" :span="2" v-if="detailData.feedback">
              <div class="content-text">{{ detailData.feedback }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="处理流程" name="timeline">
          <el-timeline>
            <el-timeline-item 
              :timestamp="detailData.createTime" 
              placement="top"
              color="#409EFF"
            >
              <h4>投诉提交</h4>
              <p>投诉人：{{ detailData.complainantName }}</p>
              <p>投诉类型：{{ detailData.complaintType }}</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.handlerName"
              timestamp="已分配" 
              placement="top"
              color="#E6A23C"
            >
              <h4>分配处理人</h4>
              <p>处理人：{{ detailData.handlerName }}</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.status === 'PROCESSING' || detailData.status === 'RESOLVED' || detailData.status === 'CLOSED'"
              timestamp="处理中" 
              placement="top"
              color="#409EFF"
            >
              <h4>开始处理</h4>
              <p>{{ detailData.handlerName }} 正在处理投诉...</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.handleResult"
              :timestamp="detailData.handleTime" 
              placement="top"
              color="#67C23A"
            >
              <h4>处理完成</h4>
              <p>处理结果：{{ detailData.handleResult }}</p>
            </el-timeline-item>
            
            <el-timeline-item 
              v-if="detailData.status === 'CLOSED'"
              timestamp="已关闭" 
              placement="top"
              color="#909399"
            >
              <h4>投诉关闭</h4>
              <p v-if="detailData.satisfactionRating">满意度：<el-rate v-model="detailData.satisfactionRating" disabled size="small" /></p>
              <p v-if="detailData.feedback">用户反馈：{{ detailData.feedback }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 分配对话框 -->
    <el-dialog v-model="assignVisible" title="分配处理人" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.handlerId" placeholder="请选择处理人">
            <el-option 
              v-for="user in handlerList" 
              :key="user.id" 
              :label="user.name" 
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

    <!-- 完成对话框 -->
    <el-dialog v-model="completeVisible" title="完成处理" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input 
            v-model="completeForm.handleResult" 
            type="textarea" 
            :rows="4"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getComplaintList, 
  getComplaintDetail, 
  assignHandler,
  startHandling, 
  completeHandling, 
  deleteComplaint,
  getComplaintStats
} from '@/api/complaint'
import { getUserList } from '@/api/user'

// 统计数据
const stats = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  resolved: 0,
  closed: 0
})

// 搜索表单
const searchForm = reactive({
  complaintType: '',
  status: '',
  urgencyLevel: ''
})

// 分页数据
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
  complaintId: null,
  handlerId: null
})
const handlerList = ref([])

// 完成对话框
const completeVisible = ref(false)
const completeForm = reactive({
  complaintId: null,
  handlerId: null,
  handleResult: ''
})

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await getComplaintStats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
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
    const res = await getComplaintList(params)
    if (res.code === 200) {
      // 后端返回的字段是 list，不是 records
      tableData.value = res.data.list || res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.warning(res.msg || '获取投诉列表失败')
    }
  } catch (error) {
    console.error('获取投诉列表失败:', error)
    ElMessage.error('获取投诉列表失败，请检查网络连接')
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
    complaintType: '',
    status: '',
    urgencyLevel: ''
  })
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getComplaintDetail(row.id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取投诉详情失败:', error)
  }
}

// 分配处理人
const handleAssign = async (row) => {
  assignForm.complaintId = row.id
  assignForm.handlerId = null
  
  // 获取处理人列表
  try {
    const res = await getUserList({ role: 'ADMIN' })
    if (res.code === 200) {
      handlerList.value = res.data.list || res.data.records || []
    }
  } catch (error) {
    console.error('获取处理人列表失败:', error)
  }
  
  assignVisible.value = true
}

// 提交分配
const submitAssign = async () => {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择处理人')
    return
  }
  
  try {
    const res = await assignHandler(assignForm.complaintId, assignForm.handlerId)
    if (res.code === 200) {
      ElMessage.success('分配成功')
      assignVisible.value = false
      fetchStats()
      fetchData()
    }
  } catch (error) {
    console.error('分配处理人失败:', error)
  }
}

// 开始处理
const handleStart = async (row) => {
  ElMessageBox.confirm('确定开始处理该投诉吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await startHandling(row.id, row.handlerId)
      if (res.code === 200) {
        ElMessage.success('已开始处理')
        fetchStats()
        fetchData()
      }
    } catch (error) {
      console.error('开始处理失败:', error)
      ElMessage.error('开始处理失败')
    }
  })
}

// 完成处理
const handleComplete = (row) => {
  completeForm.complaintId = row.id
  completeForm.handlerId = row.handlerId
  completeForm.handleResult = ''
  completeVisible.value = true
}

// 提交完成
const submitComplete = async () => {
  if (!completeForm.handleResult) {
    ElMessage.warning('请输入处理结果')
    return
  }
  
  try {
    const res = await completeHandling(
      completeForm.complaintId, 
      completeForm.handlerId, 
      completeForm.handleResult
    )
    if (res.code === 200) {
      ElMessage.success('处理完成')
      completeVisible.value = false
      fetchStats()
      fetchData()
    }
  } catch (error) {
    console.error('完成处理失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该投诉记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteComplaint(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchStats()
        fetchData()
      }
    } catch (error) {
      console.error('删除投诉失败:', error)
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

// 获取紧急程度类型
const getUrgencyType = (urgency) => {
  const typeMap = {
    LOW: 'info',
    MEDIUM: '',
    HIGH: 'warning',
    URGENT: 'danger'
  }
  return typeMap[urgency] || ''
}

// 获取紧急程度标签
const getUrgencyLabel = (urgency) => {
  const labelMap = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
    URGENT: '紧急'
  }
  return labelMap[urgency] || urgency
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    ASSIGNED: 'warning',
    PROCESSING: '',
    RESOLVED: 'success',
    CLOSED: 'info'
  }
  return typeMap[status] || ''
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    PENDING: '待处理',
    ASSIGNED: '已分配',
    PROCESSING: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return labelMap[status] || status
}

onMounted(() => {
  fetchStats()
  fetchData()
})
</script>

<style lang="scss" scoped>
.complaint-container {
  padding: 20px;
  
  .stats-row {
    margin-bottom: 20px;
    
    .stats-card {
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
      
      :deep(.el-card__body) {
        padding: 20px;
      }
      
      .stats-content {
        display: flex;
        align-items: center;
        
        .stats-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          
          .el-icon {
            font-size: 28px;
            color: #fff;
          }
          
          &.total {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }
          
          &.pending {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          }
          
          &.processing {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          }
          
          &.resolved {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          }
        }
        
        .stats-info {
          flex: 1;
          
          .stats-value {
            font-size: 28px;
            font-weight: bold;
            color: #303133;
            line-height: 1;
            margin-bottom: 8px;
          }
          
          .stats-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 20px;
  }
  
  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .el-tag {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
  
  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
  
  .content-text {
    white-space: pre-wrap;
    word-wrap: break-word;
    line-height: 1.6;
    max-height: 200px;
    overflow-y: auto;
  }
  
  :deep(.el-timeline) {
    padding-left: 10px;
    
    h4 {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
    
    p {
      margin: 4px 0;
      font-size: 14px;
      color: #606266;
    }
  }
}

// 响应式布局
@media screen and (max-width: 768px) {
  .complaint-container {
    padding: 10px;
    
    .search-form {
      :deep(.el-form-item) {
        display: block;
        margin-right: 0;
        margin-bottom: 10px;
        
        .el-form-item__content {
          margin-left: 0 !important;
        }
      }
    }
    
    :deep(.el-table) {
      font-size: 12px;
    }
    
    .pagination {
      :deep(.el-pagination) {
        .el-pagination__sizes,
        .el-pagination__jump {
          display: none;
        }
      }
    }
  }
}
</style>




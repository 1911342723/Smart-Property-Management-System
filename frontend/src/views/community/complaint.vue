<template>
  <div class="complaint-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="投诉类型">
          <el-select v-model="searchForm.complaintType" placeholder="请选择" clearable>
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
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="searchForm.urgencyLevel" placeholder="请选择" clearable>
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
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            <span>新增投诉</span>
          </el-button>
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
        <el-table-column prop="submitTime" label="提交时间" width="160" />
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
    <el-dialog v-model="detailVisible" title="投诉详情" width="800px">
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
          {{ detailData.content }}
        </el-descriptions-item>
        <el-descriptions-item label="投诉人">
          {{ detailData.complainantName }}
        </el-descriptions-item>
        <el-descriptions-item label="联系方式">
          {{ detailData.contactInfo }}
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
          {{ detailData.submitTime }}
        </el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="detailData.handleResult">
          {{ detailData.handleResult }}
        </el-descriptions-item>
        <el-descriptions-item label="满意度评分" v-if="detailData.satisfactionRating">
          <el-rate v-model="detailData.satisfactionRating" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="反馈" :span="2" v-if="detailData.feedback">
          {{ detailData.feedback }}
        </el-descriptions-item>
      </el-descriptions>
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
  completeHandling, 
  deleteComplaint 
} from '@/api/complaint'
import { getUserList } from '@/api/user'

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
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取投诉列表失败:', error)
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

// 新增
const handleAdd = () => {
  ElMessage.info('该功能由业主在小程序端提交')
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
      handlerList.value = res.data.records
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
      fetchData()
    }
  } catch (error) {
    console.error('分配处理人失败:', error)
  }
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
    PROCESSING: '',
    COMPLETED: 'success',
    CLOSED: 'info'
  }
  return typeMap[status] || ''
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    COMPLETED: '已完成',
    CLOSED: '已关闭'
  }
  return labelMap[status] || status
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.complaint-container {
  padding: 20px;
  
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
    }
  }
  
  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
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




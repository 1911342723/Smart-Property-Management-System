<template>
  <div class="log-container">
    <div class="page-header">
      <h1 class="page-title">操作日志</h1>
      <p class="page-subtitle">查看系统操作日志和审计信息</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total || 0 }}</div>
              <div class="stat-label">总日志数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon :size="32"><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.successCount || 0 }}</div>
              <div class="stat-label">成功</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon failure">
              <el-icon :size="32"><CircleCloseFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.failureCount || 0 }}</div>
              <div class="stat-label">失败</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon time">
              <el-icon :size="32"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ Math.round(stats.avgExecutionTime || 0) }}ms</div>
              <div class="stat-label">平均耗时</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作模块">
          <el-input v-model="searchForm.module" placeholder="请输入模块名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="查询" value="SELECT" />
            <el-option label="新增" value="INSERT" />
            <el-option label="修改" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="操作状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
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
          <el-button type="danger" @click="handleClear">
            <el-icon><Delete /></el-icon>
            <span>清空日志</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>日志列表</span>
          <el-button 
            type="danger" 
            :disabled="selectedLogs.length === 0"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            <span>批量删除</span>
          </el-button>
        </div>
      </template>

      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="module" label="操作模块" width="120" show-overflow-tooltip />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ getOperationTypeLabel(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="executionTime" label="耗时" width="80">
          <template #default="{ row }">
            {{ row.executionTime }}ms
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              详情
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
    <el-dialog v-model="detailVisible" title="日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作模块" :span="1">
          {{ detailData.module }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型" :span="1">
          <el-tag :type="getOperationTypeTag(detailData.operationType)">
            {{ getOperationTypeLabel(detailData.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">
          {{ detailData.description }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">
          {{ detailData.method }}
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">
          {{ detailData.requestUrl }}
        </el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <el-scrollbar max-height="200px">
            <pre>{{ detailData.requestParams }}</pre>
          </el-scrollbar>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <el-scrollbar max-height="200px">
            <pre>{{ detailData.responseResult }}</pre>
          </el-scrollbar>
        </el-descriptions-item>
        <el-descriptions-item label="操作人" :span="1">
          {{ detailData.username }}
        </el-descriptions-item>
        <el-descriptions-item label="用户ID" :span="1">
          {{ detailData.userId }}
        </el-descriptions-item>
        <el-descriptions-item label="IP地址" :span="1">
          {{ detailData.ipAddress }}
        </el-descriptions-item>
        <el-descriptions-item label="操作地点" :span="1">
          {{ detailData.location || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="浏览器" :span="1">
          {{ detailData.browser || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作系统" :span="1">
          {{ detailData.os || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="执行时长" :span="1">
          {{ detailData.executionTime }}ms
        </el-descriptions-item>
        <el-descriptions-item label="操作状态" :span="1">
          <el-tag :type="detailData.status === 1 ? 'success' : 'danger'">
            {{ detailData.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="detailData.errorMsg">
          {{ detailData.errorMsg }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">
          {{ formatDateTime(detailData.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 清空日志对话框 -->
    <el-dialog v-model="clearVisible" title="清空日志" width="400px">
      <el-form :model="clearForm" label-width="120px">
        <el-form-item label="保留天数">
          <el-input-number 
            v-model="clearForm.days" 
            :min="1" 
            :max="365"
            placeholder="请输入保留天数"
          />
          <div style="margin-top: 8px; color: #909399; font-size: 12px;">
            将删除 {{ clearForm.days }} 天前的所有日志
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="clearVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmClear">确认清空</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, Refresh, Delete, Document, SuccessFilled, CircleCloseFilled, Timer
} from '@element-plus/icons-vue'
import { getLogList, getLogDetail, batchDeleteLog, clearLogs, getLogStats } from '@/api/log'

// 搜索表单
const searchForm = reactive({
  module: '',
  operationType: '',
  username: '',
  status: null
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
const selectedLogs = ref([])

// 统计数据
const stats = ref({})

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})

// 清空对话框
const clearVisible = ref(false)
const clearForm = reactive({
  days: 30
})

// 获取统计信息
const fetchStats = async () => {
  try {
    const res = await getLogStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
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
    const res = await getLogList(params)
    if (res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取日志列表失败:', error)
    ElMessage.error('获取日志列表失败')
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
    module: '',
    operationType: '',
    username: '',
    status: null
  })
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getLogDetail(row.id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取日志详情失败:', error)
    ElMessage.error('获取日志详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await batchDeleteLog([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
        fetchStats()
      }
    } catch (error) {
      console.error('删除日志失败:', error)
      ElMessage.error('删除日志失败')
    }
  })
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedLogs.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认删除选中的 ${selectedLogs.value.length} 条日志吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const ids = selectedLogs.value.map(log => log.id)
    try {
      const res = await batchDeleteLog(ids)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
        fetchStats()
      }
    } catch (error) {
      console.error('批量删除日志失败:', error)
      ElMessage.error('批量删除日志失败')
    }
  })
}

// 清空日志
const handleClear = () => {
  clearVisible.value = true
}

// 确认清空
const confirmClear = () => {
  ElMessageBox.confirm(
    `确认清空 ${clearForm.days} 天前的所有日志吗？此操作不可恢复！`, 
    '警告', 
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const res = await clearLogs(clearForm.days)
      if (res.code === 200) {
        ElMessage.success('清空成功')
        clearVisible.value = false
        fetchData()
        fetchStats()
      }
    } catch (error) {
      console.error('清空日志失败:', error)
      ElMessage.error('清空日志失败')
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

// 获取操作类型标签
const getOperationTypeTag = (type) => {
  const tagMap = {
    SELECT: 'info',
    INSERT: 'success',
    UPDATE: 'warning',
    DELETE: 'danger',
    LOGIN: 'primary',
    LOGOUT: ''
  }
  return tagMap[type] || ''
}

// 获取操作类型标签文字
const getOperationTypeLabel = (type) => {
  const labelMap = {
    SELECT: '查询',
    INSERT: '新增',
    UPDATE: '修改',
    DELETE: '删除',
    LOGIN: '登录',
    LOGOUT: '登出'
  }
  return labelMap[type] || type
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  if (typeof dateTime === 'string') return dateTime
  // 处理LocalDateTime格式
  if (dateTime.year) {
    const pad = (n) => String(n).padStart(2, '0')
    return `${dateTime.year}-${pad(dateTime.monthValue)}-${pad(dateTime.dayOfMonth)} ${pad(dateTime.hour)}:${pad(dateTime.minute)}:${pad(dateTime.second)}`
  }
  return String(dateTime)
}

onMounted(() => {
  fetchData()
  fetchStats()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.log-container {
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

.stats-row {
  margin-bottom: 20px;
  
  .stat-card {
    margin-bottom: 20px;
    
    :deep(.el-card__body) {
      padding: 20px;
    }
    
    .stat-content {
      display: flex;
      align-items: center;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        
        &.total {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
        }
        
        &.success {
          background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
          color: white;
        }
        
        &.failure {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
          color: white;
        }
        
        &.time {
          background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
          color: #333;
        }
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: $text-primary;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: $text-secondary;
        }
      }
    }
  }
}

.search-card {
  margin-bottom: 20px;
  
  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}

pre {
  margin: 0;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
<template>
  <div class="visitor-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待访" value="PENDING" />
            <el-option label="已签到" value="ARRIVED" />
            <el-option label="已签退" value="DEPARTED" />
            <el-option label="已过期" value="EXPIRED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="searchForm.startDate"
            type="datetime"
            placeholder="选择开始日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="searchForm.endDate"
            type="datetime"
            placeholder="选择结束日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
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
          <span>访客列表</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="visitorName" label="访客姓名" width="100" />
        <el-table-column prop="visitorPhone" label="访客电话" width="120" />
        <el-table-column prop="ownerName" label="被访业主" width="100" />
        <el-table-column prop="roomNumber" label="房号" width="120" />
        <el-table-column prop="visitPurpose" label="来访事由" min-width="150" show-overflow-tooltip />
        <el-table-column prop="expectedArrivalTime" label="预计到访时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="arrivalTime" label="签到时间" width="160" />
        <el-table-column prop="departureTime" label="签退时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              详情
            </el-button>
            <el-button 
              link 
              type="success" 
              size="small" 
              @click="handleCheckIn(row)"
              v-if="row.status === 'PENDING'"
            >
              签到
            </el-button>
            <el-button 
              link 
              type="warning" 
              size="small" 
              @click="handleCheckOut(row)"
              v-if="row.status === 'ARRIVED'"
            >
              签退
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
    <el-dialog v-model="detailVisible" title="访客详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="访客姓名">
          {{ detailData.visitorName }}
        </el-descriptions-item>
        <el-descriptions-item label="访客电话">
          {{ detailData.visitorPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="访客身份证">
          {{ detailData.visitorIdCard }}
        </el-descriptions-item>
        <el-descriptions-item label="车牌号">
          {{ detailData.carNumber || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="被访业主">
          {{ detailData.ownerName }}
        </el-descriptions-item>
        <el-descriptions-item label="房号">
          {{ detailData.roomNumber }}
        </el-descriptions-item>
        <el-descriptions-item label="来访事由" :span="2">
          {{ detailData.visitPurpose }}
        </el-descriptions-item>
        <el-descriptions-item label="预计到访时间">
          {{ detailData.expectedArrivalTime }}
        </el-descriptions-item>
        <el-descriptions-item label="预计离开时间">
          {{ detailData.expectedDepartureTime }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusLabel(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ detailData.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="签到时间" v-if="detailData.arrivalTime">
          {{ detailData.arrivalTime }}
        </el-descriptions-item>
        <el-descriptions-item label="签退时间" v-if="detailData.departureTime">
          {{ detailData.departureTime }}
        </el-descriptions-item>
        <el-descriptions-item label="签到保安" v-if="detailData.checkInGuardName">
          {{ detailData.checkInGuardName }}
        </el-descriptions-item>
        <el-descriptions-item label="签退保安" v-if="detailData.checkOutGuardName">
          {{ detailData.checkOutGuardName }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div v-if="detailData.qrCode" class="qrcode-section">
        <h4>访客二维码</h4>
        <img :src="detailData.qrCode" alt="二维码" style="width: 200px; height: 200px;" />
      </div>
    </el-dialog>

    <!-- 签到对话框 -->
    <el-dialog v-model="checkInVisible" title="访客签到" width="500px">
      <el-form :model="checkInForm" label-width="100px">
        <el-form-item label="访客姓名">
          {{ checkInForm.visitorName }}
        </el-form-item>
        <el-form-item label="访客电话">
          {{ checkInForm.visitorPhone }}
        </el-form-item>
        <el-form-item label="被访业主">
          {{ checkInForm.ownerName }}
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckIn">确认签到</el-button>
      </template>
    </el-dialog>

    <!-- 签退对话框 -->
    <el-dialog v-model="checkOutVisible" title="访客签退" width="500px">
      <el-form :model="checkOutForm" label-width="100px">
        <el-form-item label="访客姓名">
          {{ checkOutForm.visitorName }}
        </el-form-item>
        <el-form-item label="访客电话">
          {{ checkOutForm.visitorPhone }}
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkOutVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckOut">确认签退</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getVisitorList, 
  getVisitorDetail, 
  checkInVisitor, 
  checkOutVisitor, 
  deleteVisitor 
} from '@/api/visitor'

// 搜索表单
const searchForm = reactive({
  status: '',
  startDate: '',
  endDate: ''
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

// 签到对话框
const checkInVisible = ref(false)
const checkInForm = ref({})

// 签退对话框
const checkOutVisible = ref(false)
const checkOutForm = ref({})

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getVisitorList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取访客列表失败:', error)
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
    status: '',
    startDate: '',
    endDate: ''
  })
  handleSearch()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getVisitorDetail(row.id)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取访客详情失败:', error)
  }
}

// 签到
const handleCheckIn = (row) => {
  checkInForm.value = { ...row }
  checkInVisible.value = true
}

// 提交签到
const submitCheckIn = async () => {
  try {
    // 这里需要获取当前登录保安的ID，暂时使用1
    const guardId = 1
    const res = await checkInVisitor(checkInForm.value.id, guardId)
    if (res.code === 200) {
      ElMessage.success('签到成功')
      checkInVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('签到失败:', error)
  }
}

// 签退
const handleCheckOut = (row) => {
  checkOutForm.value = { ...row }
  checkOutVisible.value = true
}

// 提交签退
const submitCheckOut = async () => {
  try {
    // 这里需要获取当前登录保安的ID，暂时使用1
    const guardId = 1
    const res = await checkOutVisitor(checkOutForm.value.id, guardId)
    if (res.code === 200) {
      ElMessage.success('签退成功')
      checkOutVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('签退失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该访客记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteVisitor(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      console.error('删除访客失败:', error)
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

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    ARRIVED: 'success',
    DEPARTED: 'info',
    EXPIRED: 'danger',
    CANCELLED: 'info'
  }
  return typeMap[status] || ''
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    PENDING: '待访',
    ARRIVED: '已签到',
    DEPARTED: '已签退',
    EXPIRED: '已过期',
    CANCELLED: '已取消'
  }
  return labelMap[status] || status
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.visitor-container {
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
  
  .qrcode-section {
    margin-top: 20px;
    text-align: center;
    
    h4 {
      margin-bottom: 10px;
    }
  }
}

// 响应式布局
@media screen and (max-width: 768px) {
  .visitor-container {
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






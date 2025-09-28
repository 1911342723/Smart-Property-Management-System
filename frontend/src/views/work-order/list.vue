<template>
  <div class="work-order-container">
    <div class="page-header">
      <h1 class="page-title">工单列表</h1>
      <p class="page-subtitle">查看和管理所有服务工单</p>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="工单号">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入工单号"
            clearable
            style="width: 160px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="queryParams.type" placeholder="请选择类型" clearable style="width: 140px">
            <el-option label="维修类" value="repair" />
            <el-option label="投诉类" value="complaint" />
            <el-option label="咨询类" value="inquiry" />
            <el-option label="建议类" value="suggestion" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="queryParams.priority" placeholder="请选择紧急程度" clearable style="width: 120px">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="urgent" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待接单" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="待验收" value="reviewing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理人">
          <el-select v-model="queryParams.assignee" placeholder="请选择处理人" clearable style="width: 140px">
            <el-option
              v-for="employee in employeeList"
              :key="employee.id"
              :label="employee.name"
              :value="employee.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 340px"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xl="6" :lg="12" :md="12" :sm="24">
          <div class="stat-card pending">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待接单</div>
            </div>
          </div>
        </el-col>
        <el-col :xl="6" :lg="12" :md="12" :sm="24">
          <div class="stat-card processing">
            <div class="stat-icon">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.processing }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </div>
        </el-col>
        <el-col :xl="6" :lg="12" :md="12" :sm="24">
          <div class="stat-card urgent">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.urgent }}</div>
              <div class="stat-label">紧急工单</div>
            </div>
          </div>
        </el-col>
        <el-col :xl="6" :lg="12" :md="12" :sm="24">
          <div class="stat-card overdue">
            <div class="stat-icon">
              <el-icon><AlarmClock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.overdue }}</div>
              <div class="stat-label">超时工单</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="success" :disabled="!multipleSelection.length" @click="handleBatchAssign">
          <el-icon><User /></el-icon>
          批量分配
        </el-button>
        <el-button type="warning" :disabled="!multipleSelection.length" @click="handleBatchClose">
          <el-icon><CircleClose /></el-icon>
          批量关闭
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="workOrderList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="工单号" width="140" />
        <el-table-column label="工单类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)" size="small">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column label="紧急程度" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityTagType(scope.row.priority)" size="small">
              {{ getPriorityText(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交人" width="120">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :src="scope.row.submitter.avatar" :size="24">
                {{ scope.row.submitter.name.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ scope.row.submitter.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="处理人" width="120">
          <template #default="scope">
            <div v-if="scope.row.assignee" class="user-info">
              <el-avatar :src="scope.row.assignee.avatar" :size="24">
                {{ scope.row.assignee.name.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ scope.row.assignee.name }}</span>
            </div>
            <span v-else class="text-muted">未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="120">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.progress"
              :color="getProgressColor(scope.row.progress)"
              :stroke-width="8"
              text-inside
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="剩余时间" width="120">
          <template #default="scope">
            <div :class="getTimeLeftClass(scope.row.timeLeft)">
              {{ scope.row.timeLeft }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="!scope.row.assignee"
              type="success"
              size="small"
              @click="handleAssign(scope.row)"
            >
              分配
            </el-button>
            <el-button
              v-if="scope.row.status === 'processing'"
              type="warning"
              size="small"
              @click="handleProgress(scope.row)"
            >
              进度
            </el-button>
            <el-button
              v-if="scope.row.status !== 'closed'"
              type="info"
              size="small"
              @click="handleClose(scope.row)"
            >
              关闭
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 工单详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="工单详情"
      width="900px"
      :close-on-click-modal="false"
    >
      <div v-if="currentWorkOrder" class="work-order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="工单号">{{ currentWorkOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="工单类型">
            <el-tag :type="getTypeTagType(currentWorkOrder.type)">
              {{ getTypeText(currentWorkOrder.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="标题" :span="2">{{ currentWorkOrder.title }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">
            <el-tag :type="getPriorityTagType(currentWorkOrder.priority)">
              {{ getPriorityText(currentWorkOrder.priority) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusTagType(currentWorkOrder.status)">
              {{ getStatusText(currentWorkOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentWorkOrder.submitter.name }}</el-descriptions-item>
          <el-descriptions-item label="处理人">
            {{ currentWorkOrder.assignee ? currentWorkOrder.assignee.name : '未分配' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentWorkOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="预计完成时间">{{ currentWorkOrder.expectedTime }}</el-descriptions-item>
          <el-descriptions-item label="详细描述" :span="2">
            {{ currentWorkOrder.description }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 附件列表 -->
        <div v-if="currentWorkOrder.attachments.length > 0" class="attachment-section">
          <h3>附件</h3>
          <div class="attachment-list">
            <div
              v-for="(attachment, index) in currentWorkOrder.attachments"
              :key="index"
              class="attachment-item"
            >
              <el-image
                v-if="isImage(attachment)"
                :src="attachment"
                :preview-src-list="currentWorkOrder.attachments.filter(isImage)"
                fit="cover"
                class="attachment-image"
              />
              <div v-else class="attachment-file">
                <el-icon><Document /></el-icon>
                <span>{{ attachment.split('/').pop() }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 处理记录 -->
        <div class="process-section">
          <h3>处理记录</h3>
          <el-timeline>
            <el-timeline-item
              v-for="(record, index) in currentWorkOrder.processRecords"
              :key="index"
              :timestamp="record.time"
              :type="getRecordType(record.action)"
            >
              <div class="record-content">
                <div class="record-action">{{ record.action }}</div>
                <div class="record-operator">操作人：{{ record.operator }}</div>
                <div v-if="record.remark" class="record-remark">{{ record.remark }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="currentWorkOrder && !currentWorkOrder.assignee" type="success" @click="handleAssign(currentWorkOrder)">
          分配处理人
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配处理人弹窗 -->
    <el-dialog
      v-model="showAssignDialog"
      title="分配处理人"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.assigneeId" placeholder="请选择处理人" style="width: 100%">
            <el-option
              v-for="employee in employeeList"
              :key="employee.id"
              :label="`${employee.name} (${employee.department})`"
              :value="employee.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预计完成时间">
          <el-date-picker
            v-model="assignForm.expectedTime"
            type="datetime"
            placeholder="选择预计完成时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="assignForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入分配备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  User,
  CircleClose,
  Download,
  Clock,
  Loading,
  Warning,
  AlarmClock,
  Document
} from '@element-plus/icons-vue'

export default {
  name: 'WorkOrderList',
  components: {
    Search,
    Refresh,
    User,
    CircleClose,
    Download,
    Clock,
    Loading,
    Warning,
    AlarmClock,
    Document
  },
  setup() {
    const loading = ref(false)
    const workOrderList = ref([])
    const employeeList = ref([])
    const total = ref(0)
    const multipleSelection = ref([])
    const showDetailDialog = ref(false)
    const showAssignDialog = ref(false)
    const currentWorkOrder = ref(null)
    const dateRange = ref([])
    
    const queryParams = reactive({
      pageNum: 1,
      pageSize: 20,
      orderNo: '',
      type: '',
      priority: '',
      status: '',
      assignee: '',
      startTime: '',
      endTime: ''
    })
    
    const assignForm = reactive({
      workOrderId: null,
      assigneeId: null,
      expectedTime: null,
      remark: ''
    })
    
    const stats = reactive({
      pending: 12,
      processing: 8,
      urgent: 3,
      overdue: 2
    })
    
    // 模拟数据
    const mockWorkOrders = [
      {
        id: 1,
        orderNo: 'WO202401250001',
        type: 'repair',
        title: '电梯故障维修',
        description: 'A栋2号电梯无法正常运行，需要紧急维修',
        priority: 'urgent',
        status: 'processing',
        progress: 60,
        submitter: {
          id: 1,
          name: '张三',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        },
        assignee: {
          id: 2,
          name: '李维修',
          avatar: ''
        },
        createTime: '2024-01-25 08:30:00',
        expectedTime: '2024-01-25 18:00:00',
        timeLeft: '2小时',
        attachments: [
          '/static/images/elevator1.jpg',
          '/static/images/elevator2.jpg'
        ],
        processRecords: [
          {
            action: '工单创建',
            operator: '张三',
            time: '2024-01-25 08:30:00',
            remark: '电梯突然停止运行'
          },
          {
            action: '分配处理人',
            operator: '管理员',
            time: '2024-01-25 08:45:00',
            remark: '分配给李维修处理'
          },
          {
            action: '开始处理',
            operator: '李维修',
            time: '2024-01-25 09:00:00',
            remark: '已到现场检查'
          }
        ]
      },
      {
        id: 2,
        orderNo: 'WO202401250002',
        type: 'complaint',
        title: '楼下噪音投诉',
        description: '楼下住户夜间噪音过大，影响正常休息',
        priority: 'medium',
        status: 'pending',
        progress: 0,
        submitter: {
          id: 3,
          name: '王五',
          avatar: ''
        },
        assignee: null,
        createTime: '2024-01-25 10:15:00',
        expectedTime: '',
        timeLeft: '6小时',
        attachments: [],
        processRecords: [
          {
            action: '工单创建',
            operator: '王五',
            time: '2024-01-25 10:15:00',
            remark: '噪音问题需要协调解决'
          }
        ]
      }
    ]
    
    const mockEmployees = [
      { id: 1, name: '李维修', department: '维修部' },
      { id: 2, name: '张保安', department: '保安部' },
      { id: 3, name: '王客服', department: '客服部' },
      { id: 4, name: '赵保洁', department: '保洁部' }
    ]
    
    const getList = () => {
      loading.value = true
      setTimeout(() => {
        workOrderList.value = mockWorkOrders
        total.value = mockWorkOrders.length
        loading.value = false
      }, 500)
    }
    
    const handleQuery = () => {
      queryParams.pageNum = 1
      getList()
    }
    
    const resetQuery = () => {
      Object.assign(queryParams, {
        pageNum: 1,
        pageSize: 20,
        orderNo: '',
        type: '',
        priority: '',
        status: '',
        assignee: '',
        startTime: '',
        endTime: ''
      })
      dateRange.value = []
      getList()
    }
    
    const handleDateChange = (dates) => {
      if (dates && dates.length === 2) {
        queryParams.startTime = dates[0]
        queryParams.endTime = dates[1]
      } else {
        queryParams.startTime = ''
        queryParams.endTime = ''
      }
    }
    
    const handleView = (row) => {
      currentWorkOrder.value = row
      showDetailDialog.value = true
    }
    
    const handleAssign = (row) => {
      currentWorkOrder.value = row
      assignForm.workOrderId = row.id
      showAssignDialog.value = true
    }
    
    const handleProgress = (row) => {
      ElMessage.info('进度更新功能开发中...')
    }
    
    const handleClose = async (row) => {
      try {
        await ElMessageBox.confirm('确定要关闭该工单吗？', '确认操作')
        row.status = 'closed'
        row.progress = 100
        ElMessage.success('工单已关闭')
      } catch {
        // 用户取消
      }
    }
    
    const handleBatchAssign = () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要分配的工单')
        return
      }
      ElMessage.info('批量分配功能开发中...')
    }
    
    const handleBatchClose = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要关闭的工单')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量关闭 ${multipleSelection.value.length} 个工单吗？`, '确认操作')
        multipleSelection.value.forEach(workOrder => {
          workOrder.status = 'closed'
          workOrder.progress = 100
        })
        ElMessage.success('批量关闭成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleExport = () => {
      ElMessage.info('导出功能开发中...')
    }
    
    const handleSelectionChange = (selection) => {
      multipleSelection.value = selection
    }
    
    const handleSizeChange = (val) => {
      queryParams.pageSize = val
      getList()
    }
    
    const handleCurrentChange = (val) => {
      queryParams.pageNum = val
      getList()
    }
    
    const confirmAssign = () => {
      if (!assignForm.assigneeId) {
        ElMessage.error('请选择处理人')
        return
      }
      
      const employee = employeeList.value.find(e => e.id === assignForm.assigneeId)
      if (employee && currentWorkOrder.value) {
        currentWorkOrder.value.assignee = {
          id: employee.id,
          name: employee.name,
          avatar: ''
        }
        currentWorkOrder.value.status = 'processing'
        
        // 添加处理记录
        currentWorkOrder.value.processRecords.push({
          action: '分配处理人',
          operator: '管理员',
          time: new Date().toLocaleString(),
          remark: assignForm.remark || `分配给${employee.name}处理`
        })
        
        ElMessage.success('分配成功')
        showAssignDialog.value = false
        showDetailDialog.value = false
        
        // 重置表单
        Object.assign(assignForm, {
          workOrderId: null,
          assigneeId: null,
          expectedTime: null,
          remark: ''
        })
      }
    }
    
    const getTypeText = (type) => {
      const typeMap = {
        repair: '维修类',
        complaint: '投诉类',
        inquiry: '咨询类',
        suggestion: '建议类',
        other: '其他'
      }
      return typeMap[type] || '未知'
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        repair: 'danger',
        complaint: 'warning',
        inquiry: 'info',
        suggestion: 'success',
        other: ''
      }
      return typeMap[type] || ''
    }
    
    const getPriorityText = (priority) => {
      const priorityMap = {
        low: '低',
        medium: '中',
        high: '高',
        urgent: '紧急'
      }
      return priorityMap[priority] || '未知'
    }
    
    const getPriorityTagType = (priority) => {
      const priorityMap = {
        low: 'info',
        medium: 'success',
        high: 'warning',
        urgent: 'danger'
      }
      return priorityMap[priority] || 'info'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        pending: '待接单',
        processing: '处理中',
        reviewing: '待验收',
        completed: '已完成',
        closed: '已关闭'
      }
      return statusMap[status] || '未知'
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        pending: 'warning',
        processing: 'primary',
        reviewing: 'info',
        completed: 'success',
        closed: ''
      }
      return statusMap[status] || ''
    }
    
    const getProgressColor = (progress) => {
      if (progress < 30) return '#f56c6c'
      if (progress < 70) return '#e6a23c'
      return '#67c23a'
    }
    
    const getTimeLeftClass = (timeLeft) => {
      if (timeLeft.includes('已超时')) return 'time-overdue'
      if (timeLeft.includes('小时') && parseInt(timeLeft) <= 2) return 'time-urgent'
      return 'time-normal'
    }
    
    const getRecordType = (action) => {
      if (action.includes('创建')) return 'primary'
      if (action.includes('分配')) return 'success'
      if (action.includes('完成')) return 'success'
      if (action.includes('关闭')) return 'info'
      return 'primary'
    }
    
    const isImage = (url) => {
      return /\.(jpg|jpeg|png|gif|webp)$/i.test(url)
    }
    
    onMounted(() => {
      getList()
      employeeList.value = mockEmployees
    })
    
    return {
      loading,
      workOrderList,
      employeeList,
      total,
      multipleSelection,
      showDetailDialog,
      showAssignDialog,
      currentWorkOrder,
      dateRange,
      queryParams,
      assignForm,
      stats,
      getList,
      handleQuery,
      resetQuery,
      handleDateChange,
      handleView,
      handleAssign,
      handleProgress,
      handleClose,
      handleBatchAssign,
      handleBatchClose,
      handleExport,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      confirmAssign,
      getTypeText,
      getTypeTagType,
      getPriorityText,
      getPriorityTagType,
      getStatusText,
      getStatusTagType,
      getProgressColor,
      getTimeLeftClass,
      getRecordType,
      isImage
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.work-order-container {
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

.filter-card {
  margin-bottom: 20px;
  
  .filter-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
}

.stats-cards {
  margin-bottom: 20px;
  
  .stat-card {
    background: linear-gradient(135deg, $bg-tertiary 0%, lighten($bg-tertiary, 2%) 100%);
    border-radius: 16px;
    padding: 24px;
    border: 1px solid $border-color;
    display: flex;
    align-items: center;
    gap: 20px;
    transition: all 0.3s ease;
    height: 100px;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    }
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: $text-primary;
    }
    
    .stat-info {
      flex: 1;
    }
    
    .stat-value {
      font-size: 32px;
      font-weight: 700;
      color: $text-primary;
      line-height: 1;
      margin-bottom: 8px;
    }
    
    .stat-label {
      font-size: 14px;
      color: $text-secondary;
    }
    
    &.pending {
      border-left: 4px solid #faa61a;
      
      .stat-icon {
        background: rgba(250, 166, 26, 0.1);
        color: #faa61a;
      }
    }
    
    &.processing {
      border-left: 4px solid #5865f2;
      
      .stat-icon {
        background: rgba(88, 101, 242, 0.1);
        color: #5865f2;
      }
    }
    
    &.urgent {
      border-left: 4px solid #ed4245;
      
      .stat-icon {
        background: rgba(237, 66, 69, 0.1);
        color: #ed4245;
      }
    }
    
    &.overdue {
      border-left: 4px solid #c97064;
      
      .stat-icon {
        background: rgba(201, 112, 100, 0.1);
        color: #c97064;
      }
    }
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .toolbar-left,
  .toolbar-right {
    display: flex;
    gap: 12px;
  }
}

.table-card {
  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .user-name {
    font-size: 14px;
    color: $text-primary;
  }
}

.time-normal {
  color: $text-primary;
}

.time-urgent {
  color: $warning-color;
  font-weight: 600;
}

.time-overdue {
  color: $error-color;
  font-weight: 600;
}

.work-order-detail {
  .attachment-section {
    margin-top: 24px;
    
    h3 {
      color: $text-primary;
      margin-bottom: 16px;
      font-size: 16px;
      font-weight: 600;
    }
    
    .attachment-list {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      
      .attachment-item {
        .attachment-image {
          width: 100px;
          height: 100px;
          border-radius: 8px;
        }
        
        .attachment-file {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px;
          background: $bg-secondary;
          border-radius: 8px;
          color: $text-primary;
        }
      }
    }
  }
  
  .process-section {
    margin-top: 24px;
    
    h3 {
      color: $text-primary;
      margin-bottom: 16px;
      font-size: 16px;
      font-weight: 600;
    }
    
    .record-content {
      .record-action {
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 4px;
      }
      
      .record-operator {
        font-size: 14px;
        color: $text-secondary;
        margin-bottom: 4px;
      }
      
      .record-remark {
        font-size: 14px;
        color: $text-muted;
        font-style: italic;
      }
    }
  }
}

.text-muted {
  color: $text-muted;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 12px;
    
    .toolbar-left,
    .toolbar-right {
      width: 100%;
      justify-content: center;
    }
  }
  
  .filter-form {
    .el-form-item {
      width: 100%;
      margin-bottom: 16px;
    }
  }
  
  .stats-cards .stat-card {
    margin-bottom: 16px;
  }
}
</style>

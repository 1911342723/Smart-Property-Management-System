<template>
  <div class="work-order-container responsive-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">工单管理</h1>
        <p class="page-subtitle">管理和处理所有服务工单</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          创建工单
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards responsive-grid grid-4">
      <div class="stat-card responsive-card pending">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </div>
      <div class="stat-card responsive-card processing">
        <div class="stat-icon">
          <el-icon><Loading /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.processing }}</div>
          <div class="stat-label">处理中</div>
        </div>
      </div>
      <div class="stat-card responsive-card completed">
        <div class="stat-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card responsive-card urgent">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.urgent }}</div>
          <div class="stat-label">紧急工单</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section responsive-card">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item label="工单类型">
          <el-select v-model="filters.type" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="设备维修" value="facility" />
            <el-option label="安全事故" value="security" />
            <el-option label="火灾隐患" value="fire" />
            <el-option label="人员纠纷" value="dispute" />
            <el-option label="环境问题" value="environment" />
            <el-option label="投诉建议" value="complaint" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-select v-model="filters.urgency" placeholder="全部等级" clearable style="width: 120px">
            <el-option label="一般" value="low" />
            <el-option label="紧急" value="medium" />
            <el-option label="非常紧急" value="high" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理人员">
          <el-select v-model="filters.assignee" placeholder="全部人员" clearable style="width: 150px">
            <el-option v-for="staff in staffList" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索工单内容"
            prefix-icon="Search"
            style="width: 200px"
            @keyup.enter="loadWorkOrders"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadWorkOrders">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 工单列表 -->
    <div class="work-order-list responsive-card">
      <el-table
        :data="workOrders"
        v-loading="loading"
        stripe
        @row-click="viewWorkOrder"
        class="work-order-table"
      >
        <el-table-column prop="id" label="工单编号" width="120">
          <template #default="{ row }">
            <span class="order-id">#{{ row.id }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="order-title">
              <span>{{ row.title }}</span>
              <el-tag v-if="row.urgency === 'high'" type="danger" size="small" class="urgent-tag">
                紧急
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="location" label="位置" width="150" />
        
        <el-table-column prop="reporter" label="报告人" width="100">
          <template #default="{ row }">
            <div class="reporter-info">
              <el-avatar :size="24" :src="row.reporterAvatar">{{ row.reporter.charAt(0) }}</el-avatar>
              <span>{{ row.reporter }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="assignee" label="处理人" width="100">
          <template #default="{ row }">
            <div v-if="row.assignee" class="assignee-info">
              <el-avatar :size="24" :src="row.assigneeAvatar">{{ row.assignee.charAt(0) }}</el-avatar>
              <span>{{ row.assignee }}</span>
            </div>
            <el-button v-else type="text" size="small" @click.stop="assignWorkOrder(row)">
              分配
            </el-button>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click.stop="viewWorkOrder(row)">
              查看
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)" trigger="click">
              <el-button size="small" type="text">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="assign">分配</el-dropdown-item>
                  <el-dropdown-item command="close" v-if="row.status !== 'completed'">关闭</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadWorkOrders"
          @current-change="loadWorkOrders"
        />
      </div>
    </div>

    <!-- 工单详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="`工单详情 - #${selectedOrder?.id}`"
      width="800px"
      class="work-order-dialog"
    >
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-header">
          <div class="order-info">
            <h3>{{ selectedOrder.title }}</h3>
            <div class="order-meta">
              <el-tag :type="getTypeTagType(selectedOrder.type)">{{ getTypeName(selectedOrder.type) }}</el-tag>
              <el-tag :type="getStatusTagType(selectedOrder.status)">{{ getStatusName(selectedOrder.status) }}</el-tag>
              <el-tag v-if="selectedOrder.urgency === 'high'" type="danger">紧急</el-tag>
            </div>
          </div>
          <div class="order-actions">
            <el-button v-if="selectedOrder.status === 'pending'" type="primary" @click="startProcessing">
              开始处理
            </el-button>
            <el-button v-if="selectedOrder.status === 'processing'" type="success" @click="completeOrder">
              完成工单
            </el-button>
          </div>
        </div>
        
        <el-divider />
        
        <div class="detail-content">
          <div class="content-section">
            <h4>基本信息</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="工单编号">#{{ selectedOrder.id }}</el-descriptions-item>
              <el-descriptions-item label="类型">{{ getTypeName(selectedOrder.type) }}</el-descriptions-item>
              <el-descriptions-item label="位置">{{ selectedOrder.location }}</el-descriptions-item>
              <el-descriptions-item label="紧急程度">{{ getUrgencyName(selectedOrder.urgency) }}</el-descriptions-item>
              <el-descriptions-item label="报告人">{{ selectedOrder.reporter }}</el-descriptions-item>
              <el-descriptions-item label="处理人">{{ selectedOrder.assignee || '未分配' }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDate(selectedOrder.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDate(selectedOrder.updatedAt) }}</el-descriptions-item>
            </el-descriptions>
          </div>
          
          <div class="content-section">
            <h4>问题描述</h4>
            <div class="description">{{ selectedOrder.description }}</div>
          </div>
          
          <div v-if="selectedOrder.photos && selectedOrder.photos.length" class="content-section">
            <h4>相关图片</h4>
            <div class="photo-gallery">
              <el-image
                v-for="(photo, index) in selectedOrder.photos"
                :key="index"
                :src="photo"
                :preview-src-list="selectedOrder.photos"
                :initial-index="index"
                fit="cover"
                class="photo-item"
              />
            </div>
          </div>
          
          <div v-if="selectedOrder.voiceRecords && selectedOrder.voiceRecords.length" class="content-section">
            <h4>语音记录</h4>
            <div class="voice-records">
              <div v-for="(voice, index) in selectedOrder.voiceRecords" :key="index" class="voice-item">
                <el-button type="text" @click="playVoice(voice)">
                  <el-icon><VideoPlay /></el-icon>
                  语音记录 {{ index + 1 }} ({{ voice.duration }}s)
                </el-button>
              </div>
            </div>
          </div>
          
          <div class="content-section">
            <h4>处理记录</h4>
            <el-timeline>
              <el-timeline-item
                v-for="log in selectedOrder.logs"
                :key="log.id"
                :timestamp="formatDate(log.createdAt)"
                :type="getLogType(log.action)"
              >
                <div class="log-content">
                  <strong>{{ log.operator }}</strong> {{ log.action }}
                  <div v-if="log.comment" class="log-comment">{{ log.comment }}</div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 创建工单对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建工单" width="600px">
      <el-form :model="newOrder" :rules="orderRules" ref="orderForm" label-width="100px">
        <el-form-item label="工单类型" prop="type">
          <el-select v-model="newOrder.type" placeholder="请选择工单类型">
            <el-option label="设备维修" value="facility" />
            <el-option label="安全事故" value="security" />
            <el-option label="火灾隐患" value="fire" />
            <el-option label="人员纠纷" value="dispute" />
            <el-option label="环境问题" value="environment" />
            <el-option label="投诉建议" value="complaint" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="newOrder.title" placeholder="请输入工单标题" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="newOrder.location" placeholder="请输入具体位置" />
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgency">
          <el-radio-group v-model="newOrder.urgency">
            <el-radio label="low">一般</el-radio>
            <el-radio label="medium">紧急</el-radio>
            <el-radio label="high">非常紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="newOrder.description" type="textarea" :rows="4" placeholder="请详细描述问题" />
        </el-form-item>
        <el-form-item label="分配给">
          <el-select v-model="newOrder.assignee" placeholder="请选择处理人员" clearable>
            <el-option v-for="staff in staffList" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createWorkOrder" :loading="creating">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Clock, Loading, Check, Warning, Search, ArrowDown, VideoPlay } from '@element-plus/icons-vue'

export default {
  name: 'WorkOrderList',
  components: {
    Plus, Clock, Loading, Check, Warning, Search, ArrowDown, VideoPlay
  },
  setup() {
    const loading = ref(false)
    const creating = ref(false)
    const showDetailDialog = ref(false)
    const showCreateDialog = ref(false)
    const selectedOrder = ref(null)
    
    // 统计数据
    const stats = reactive({
      pending: 12,
      processing: 8,
      completed: 45,
      urgent: 3
    })
    
    // 筛选条件
    const filters = reactive({
      type: '',
      status: '',
      urgency: '',
      assignee: '',
      keyword: ''
    })
    
    // 分页
    const pagination = reactive({
      current: 1,
      size: 20,
      total: 0
    })
    
    // 工单列表
    const workOrders = ref([])
    
    // 员工列表
    const staffList = ref([
      { id: 1, name: '张维修', role: '维修工' },
      { id: 2, name: '李保安', role: '保安' },
      { id: 3, name: '王清洁', role: '清洁工' },
      { id: 4, name: '赵管理', role: '物业经理' }
    ])
    
    // 新建工单表单
    const newOrder = reactive({
      type: '',
      title: '',
      location: '',
      urgency: 'medium',
      description: '',
      assignee: ''
    })
    
    // 表单验证规则
    const orderRules = {
      type: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
      title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
      location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
      description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
    }
    
    // 模拟数据
    const mockWorkOrders = [
      {
        id: 'WO202312001',
        type: 'facility',
        title: '电梯故障无法正常运行',
        location: 'A栋3号电梯',
        description: '电梯在2楼和3楼之间停止运行，显示屏显示故障代码E03',
        reporter: '李住户',
        reporterAvatar: '',
        assignee: '张维修',
        assigneeAvatar: '',
        status: 'processing',
        urgency: 'high',
        createdAt: '2023-12-01 09:30:00',
        updatedAt: '2023-12-01 10:15:00',
        photos: ['/api/photos/elevator1.jpg', '/api/photos/elevator2.jpg'],
        voiceRecords: [
          { url: '/api/voice/record1.mp3', duration: 15 }
        ],
        logs: [
          { id: 1, operator: '系统', action: '工单创建', createdAt: '2023-12-01 09:30:00' },
          { id: 2, operator: '赵管理', action: '分配给张维修', createdAt: '2023-12-01 09:45:00' },
          { id: 3, operator: '张维修', action: '开始处理', comment: '已到现场检查，正在排查故障原因', createdAt: '2023-12-01 10:15:00' }
        ]
      },
      {
        id: 'WO202312002',
        type: 'security',
        title: '停车场发现可疑人员',
        location: '地下停车场B区',
        description: '发现陌生人在停车场徘徊，行为可疑，已拍照记录',
        reporter: '李保安',
        reporterAvatar: '',
        assignee: '',
        assigneeAvatar: '',
        status: 'pending',
        urgency: 'medium',
        createdAt: '2023-12-01 14:20:00',
        updatedAt: '2023-12-01 14:20:00',
        photos: ['/api/photos/security1.jpg'],
        voiceRecords: [],
        logs: [
          { id: 1, operator: '系统', action: '工单创建', createdAt: '2023-12-01 14:20:00' }
        ]
      },
      {
        id: 'WO202312003',
        type: 'environment',
        title: '绿化带垃圾清理',
        location: '小区中央花园',
        description: '绿化带内有大量落叶和垃圾需要清理',
        reporter: '王住户',
        reporterAvatar: '',
        assignee: '王清洁',
        assigneeAvatar: '',
        status: 'completed',
        urgency: 'low',
        createdAt: '2023-11-30 16:00:00',
        updatedAt: '2023-12-01 08:30:00',
        photos: [],
        voiceRecords: [],
        logs: [
          { id: 1, operator: '系统', action: '工单创建', createdAt: '2023-11-30 16:00:00' },
          { id: 2, operator: '赵管理', action: '分配给王清洁', createdAt: '2023-11-30 16:30:00' },
          { id: 3, operator: '王清洁', action: '开始处理', createdAt: '2023-12-01 08:00:00' },
          { id: 4, operator: '王清洁', action: '完成工单', comment: '已清理完成，现场整洁', createdAt: '2023-12-01 08:30:00' }
        ]
      }
    ]
    
    // 加载工单列表
    const loadWorkOrders = async () => {
      loading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        workOrders.value = mockWorkOrders
        pagination.total = mockWorkOrders.length
      } catch (error) {
        ElMessage.error('加载工单列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 重置筛选条件
    const resetFilters = () => {
      Object.keys(filters).forEach(key => {
        filters[key] = ''
      })
      loadWorkOrders()
    }
    
    // 查看工单详情
    const viewWorkOrder = (order) => {
      selectedOrder.value = order
      showDetailDialog.value = true
    }
    
    // 创建工单
    const createWorkOrder = async () => {
      // 这里应该添加表单验证
      creating.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('工单创建成功')
        showCreateDialog.value = false
        loadWorkOrders()
      } catch (error) {
        ElMessage.error('创建工单失败')
      } finally {
        creating.value = false
      }
    }
    
    // 分配工单
    const assignWorkOrder = (order) => {
      // 打开分配对话框
      ElMessage.info('分配功能开发中')
    }
    
    // 处理下拉菜单命令
    const handleCommand = (command, order) => {
      switch (command) {
        case 'edit':
          ElMessage.info('编辑功能开发中')
          break
        case 'assign':
          assignWorkOrder(order)
          break
        case 'close':
          ElMessageBox.confirm('确定要关闭此工单吗？', '确认操作', {
            type: 'warning'
          }).then(() => {
            ElMessage.success('工单已关闭')
            loadWorkOrders()
          })
          break
        case 'delete':
          ElMessageBox.confirm('确定要删除此工单吗？', '确认删除', {
            type: 'warning'
          }).then(() => {
            ElMessage.success('工单已删除')
            loadWorkOrders()
          })
          break
      }
    }
    
    // 开始处理工单
    const startProcessing = () => {
      selectedOrder.value.status = 'processing'
      ElMessage.success('已开始处理工单')
    }
    
    // 完成工单
    const completeOrder = () => {
      selectedOrder.value.status = 'completed'
      ElMessage.success('工单已完成')
    }
    
    // 播放语音
    const playVoice = (voice) => {
      ElMessage.info('语音播放功能开发中')
    }
    
    // 工具函数
    const getTypeName = (type) => {
      const typeMap = {
        facility: '设备维修',
        security: '安全事故',
        fire: '火灾隐患',
        dispute: '人员纠纷',
        environment: '环境问题',
        complaint: '投诉建议',
        other: '其他'
      }
      return typeMap[type] || type
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        facility: 'primary',
        security: 'danger',
        fire: 'danger',
        dispute: 'warning',
        environment: 'success',
        complaint: 'info',
        other: ''
      }
      return typeMap[type] || ''
    }
    
    const getStatusName = (status) => {
      const statusMap = {
        pending: '待处理',
        processing: '处理中',
        completed: '已完成',
        closed: '已关闭'
      }
      return statusMap[status] || status
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        pending: 'warning',
        processing: 'primary',
        completed: 'success',
        closed: 'info'
      }
      return statusMap[status] || ''
    }
    
    const getUrgencyName = (urgency) => {
      const urgencyMap = {
        low: '一般',
        medium: '紧急',
        high: '非常紧急'
      }
      return urgencyMap[urgency] || urgency
    }
    
    const getLogType = (action) => {
      if (action.includes('创建')) return 'primary'
      if (action.includes('完成')) return 'success'
      if (action.includes('分配') || action.includes('处理')) return 'warning'
      return ''
    }
    
    const formatDate = (dateStr) => {
      return new Date(dateStr).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      loadWorkOrders()
    })
    
    return {
      loading,
      creating,
      showDetailDialog,
      showCreateDialog,
      selectedOrder,
      stats,
      filters,
      pagination,
      workOrders,
      staffList,
      newOrder,
      orderRules,
      loadWorkOrders,
      resetFilters,
      viewWorkOrder,
      createWorkOrder,
      assignWorkOrder,
      handleCommand,
      startProcessing,
      completeOrder,
      playVoice,
      getTypeName,
      getTypeTagType,
      getStatusName,
      getStatusTagType,
      getUrgencyName,
      getLogType,
      formatDate
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.work-order-container {
  padding: 20px;
  min-height: calc(100vh - 50px);
  background: $bg-primary;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  
  .header-left {
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
  
  @include mobile {
    flex-direction: column;
    gap: 16px;
    
    .header-right {
      width: 100%;
      
      .el-button {
        width: 100%;
      }
    }
  }
}

.stats-cards {
  margin-bottom: 24px;
  
  .stat-card {
    background: linear-gradient(135deg, $bg-tertiary 0%, lighten($bg-tertiary, 2%) 100%);
    border: 1px solid $border-color;
    border-radius: $border-radius-lg;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px $shadow-medium;
    }
    
    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: $border-radius;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      color: white;
    }
    
    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: $text-primary;
        line-height: 1;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 14px;
        color: $text-secondary;
      }
    }
    
    &.pending .stat-icon {
      background: linear-gradient(135deg, #faa61a, #f0932b);
    }
    
    &.processing .stat-icon {
      background: linear-gradient(135deg, #5865f2, #4752c4);
    }
    
    &.completed .stat-icon {
      background: linear-gradient(135deg, #3ba55c, #27ae60);
    }
    
    &.urgent .stat-icon {
      background: linear-gradient(135deg, #ed4245, #e74c3c);
    }
  }
}

.filter-section {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  padding: 20px;
  margin-bottom: 20px;
  
  .filter-form {
    @include mobile {
      .el-form-item {
        width: 100%;
        margin-right: 0;
        margin-bottom: 16px;
        
        :deep(.el-form-item__content) {
          width: 100%;
          
          .el-select,
          .el-input {
            width: 100% !important;
          }
        }
      }
    }
  }
}

.work-order-list {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  padding: 20px;
  
  .work-order-table {
    :deep(.el-table) {
      background: transparent;
      
      .el-table__header {
        background: $bg-secondary;
        
        th {
          background: $bg-secondary;
          color: $text-primary;
          border-bottom: 1px solid $border-color;
        }
      }
      
      .el-table__body {
        tr {
          background: transparent;
          
          &:hover td {
            background: $bg-quaternary;
          }
          
          td {
            border-bottom: 1px solid $border-color;
            color: $text-primary;
          }
        }
      }
    }
    
    .order-id {
      font-family: 'Monaco', 'Menlo', monospace;
      font-weight: 600;
      color: $primary-color;
    }
    
    .order-title {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .urgent-tag {
        flex-shrink: 0;
      }
    }
    
    .reporter-info,
    .assignee-info {
      display: flex;
      align-items: center;
      gap: 8px;
      
      span {
        font-size: 12px;
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

.work-order-dialog {
  :deep(.el-dialog) {
    background: $bg-tertiary;
    
    .el-dialog__header {
      background: $bg-secondary;
      border-bottom: 1px solid $border-color;
      
      .el-dialog__title {
        color: $text-primary;
      }
    }
    
    .el-dialog__body {
      background: $bg-tertiary;
      color: $text-primary;
    }
  }
  
  .order-detail {
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;
      
      .order-info {
        h3 {
          margin: 0 0 12px 0;
          color: $text-primary;
        }
        
        .order-meta {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }
      }
      
      @include mobile {
        flex-direction: column;
        gap: 16px;
        
        .order-actions {
          width: 100%;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
    
    .detail-content {
      .content-section {
        margin-bottom: 24px;
        
        h4 {
          margin: 0 0 12px 0;
          color: $text-primary;
          font-size: 16px;
        }
        
        .description {
          background: $bg-secondary;
          padding: 16px;
          border-radius: $border-radius;
          border: 1px solid $border-color;
          color: $text-primary;
          line-height: 1.6;
        }
        
        .photo-gallery {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
          gap: 12px;
          
          .photo-item {
            width: 120px;
            height: 120px;
            border-radius: $border-radius;
            overflow: hidden;
          }
        }
        
        .voice-records {
          .voice-item {
            background: $bg-secondary;
            padding: 12px;
            border-radius: $border-radius;
            border: 1px solid $border-color;
            margin-bottom: 8px;
            
            .el-button {
              color: $primary-color;
            }
          }
        }
        
        .log-content {
          color: $text-primary;
          
          .log-comment {
            margin-top: 8px;
            padding: 8px 12px;
            background: $bg-secondary;
            border-radius: $border-radius;
            font-size: 14px;
            color: $text-secondary;
          }
        }
      }
    }
  }
}

:deep(.el-descriptions) {
  .el-descriptions__header {
    .el-descriptions__title {
      color: $text-primary;
    }
  }
  
  .el-descriptions__body {
    .el-descriptions__table {
      .el-descriptions__cell {
        background: $bg-secondary;
        border: 1px solid $border-color;
        color: $text-primary;
        
        &.is-bordered-label {
          background: lighten($bg-secondary, 3%);
          font-weight: 600;
        }
      }
    }
  }
}

:deep(.el-timeline) {
  .el-timeline-item__wrapper {
    .el-timeline-item__timestamp {
      color: $text-secondary;
    }
  }
}
</style>
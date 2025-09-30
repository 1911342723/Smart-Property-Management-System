<template>
  <div class="message-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="消息类型">
          <el-select v-model="searchForm.messageType" placeholder="请选择" clearable>
            <el-option label="系统消息" value="SYSTEM" />
            <el-option label="服务消息" value="SERVICE" />
            <el-option label="公告消息" value="NOTICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否已读">
          <el-select v-model="searchForm.isRead" placeholder="请选择" clearable>
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
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
          <el-button type="success" @click="handleBatchRead" :disabled="selectedMessages.length === 0">
            <el-icon><Check /></el-icon>
            <span>批量已读</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-label">全部消息</div>
          <div class="stat-value">{{ unreadStats.all || 0 }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">系统消息</div>
          <div class="stat-value">{{ unreadStats.system || 0 }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">服务消息</div>
          <div class="stat-value">{{ unreadStats.service || 0 }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">公告消息</div>
          <div class="stat-value">{{ unreadStats.notice || 0 }}</div>
        </div>
      </div>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>消息列表</span>
          <el-button type="primary" @click="handleSendMessage">
            <el-icon><Plus /></el-icon>
            <span>发送消息</span>
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
        <el-table-column prop="messageType" label="消息类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getMessageTypeTag(row.messageType)">
              {{ getMessageTypeLabel(row.messageType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'success' : 'warning'">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="160" />
        <el-table-column prop="readTime" label="读取时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              详情
            </el-button>
            <el-button 
              link 
              type="success" 
              size="small" 
              @click="handleMarkRead(row)"
              v-if="!row.isRead"
            >
              标记已读
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
    <el-dialog v-model="detailVisible" title="消息详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="消息类型">
          <el-tag :type="getMessageTypeTag(detailData.messageType)">
            {{ getMessageTypeLabel(detailData.messageType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题">
          {{ detailData.title }}
        </el-descriptions-item>
        <el-descriptions-item label="内容">
          {{ detailData.content }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.isRead ? 'success' : 'warning'">
            {{ detailData.isRead ? '已读' : '未读' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">
          {{ detailData.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="读取时间" v-if="detailData.readTime">
          {{ detailData.readTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 发送消息对话框 -->
    <el-dialog v-model="sendVisible" title="发送消息" width="600px">
      <el-form :model="sendForm" :rules="sendRules" ref="sendFormRef" label-width="100px">
        <el-form-item label="消息类型" prop="messageType">
          <el-select v-model="sendForm.messageType" placeholder="请选择消息类型">
            <el-option label="系统消息" value="SYSTEM" />
            <el-option label="服务消息" value="SERVICE" />
            <el-option label="公告消息" value="NOTICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="sendForm.content" 
            type="textarea" 
            :rows="5"
            placeholder="请输入消息内容"
          />
        </el-form-item>
        <el-form-item label="接收人" prop="receiverId">
          <el-select v-model="sendForm.receiverId" placeholder="请选择接收人" filterable>
            <el-option 
              v-for="user in userList" 
              :key="user.id" 
              :label="user.name" 
              :value="user.id" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getMessageList, 
  markAsRead, 
  batchMarkAsRead, 
  getUnreadCount,
  sendSystemMessage
} from '@/api/message'
import { getUserList } from '@/api/user'

// 搜索表单
const searchForm = reactive({
  messageType: '',
  isRead: ''
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
const selectedMessages = ref([])

// 统计数据
const unreadStats = ref({})

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})

// 发送消息对话框
const sendVisible = ref(false)
const sendFormRef = ref(null)
const sendForm = reactive({
  messageType: 'SYSTEM',
  title: '',
  content: '',
  receiverId: null
})
const sendRules = {
  messageType: [{ required: true, message: '请选择消息类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  receiverId: [{ required: true, message: '请选择接收人', trigger: 'change' }]
}
const userList = ref([])

// 获取未读统计
const fetchUnreadStats = async () => {
  try {
    const res = await getUnreadCount()
    if (res.code === 200) {
      unreadStats.value = res.data
    }
  } catch (error) {
    console.error('获取未读统计失败:', error)
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
    const res = await getMessageList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取消息列表失败:', error)
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
    messageType: '',
    isRead: ''
  })
  handleSearch()
}

// 查看详情
const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
  
  // 如果是未读消息，标记为已读
  if (!row.isRead) {
    handleMarkRead(row)
  }
}

// 标记已读
const handleMarkRead = async (row) => {
  try {
    const res = await markAsRead(row.id)
    if (res.code === 200) {
      ElMessage.success('已标记为已读')
      fetchData()
      fetchUnreadStats()
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedMessages.value = selection
}

// 批量已读
const handleBatchRead = async () => {
  const messageIds = selectedMessages.value.map(msg => msg.id)
  try {
    const res = await batchMarkAsRead(messageIds)
    if (res.code === 200) {
      ElMessage.success('批量标记成功')
      fetchData()
      fetchUnreadStats()
    }
  } catch (error) {
    console.error('批量标记失败:', error)
  }
}

// 发送消息
const handleSendMessage = async () => {
  // 获取用户列表
  try {
    const res = await getUserList({})
    if (res.code === 200) {
      userList.value = res.data.records
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
  
  sendVisible.value = true
}

// 提交发送
const submitSend = async () => {
  const valid = await sendFormRef.value.validate()
  if (!valid) return
  
  try {
    const res = await sendSystemMessage(
      sendForm.title, 
      sendForm.content, 
      sendForm.receiverId
    )
    if (res.code === 200) {
      ElMessage.success('发送成功')
      sendVisible.value = false
      Object.assign(sendForm, {
        messageType: 'SYSTEM',
        title: '',
        content: '',
        receiverId: null
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

// 分页改变
const handleSizeChange = () => {
  fetchData()
}

const handleCurrentChange = () => {
  fetchData()
}

// 获取消息类型标签
const getMessageTypeTag = (type) => {
  const tagMap = {
    SYSTEM: 'danger',
    SERVICE: 'warning',
    NOTICE: 'primary'
  }
  return tagMap[type] || ''
}

// 获取消息类型标签文字
const getMessageTypeLabel = (type) => {
  const labelMap = {
    SYSTEM: '系统消息',
    SERVICE: '服务消息',
    NOTICE: '公告消息'
  }
  return labelMap[type] || type
}

onMounted(() => {
  fetchData()
  fetchUnreadStats()
})
</script>

<style lang="scss" scoped>
.message-container {
  padding: 20px;
  
  .search-card {
    margin-bottom: 20px;
  }
  
  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }
  
  .stats-card {
    margin-bottom: 20px;
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      
      .stat-item {
        text-align: center;
        padding: 15px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 8px;
        color: white;
        
        &:nth-child(2) {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        &:nth-child(3) {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        &:nth-child(4) {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
        
        .stat-label {
          font-size: 14px;
          margin-bottom: 8px;
          opacity: 0.9;
        }
        
        .stat-value {
          font-size: 28px;
          font-weight: bold;
        }
      }
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
  .message-container {
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
    
    .stats-grid {
      grid-template-columns: repeat(2, 1fr) !important;
      gap: 10px !important;
      
      .stat-item {
        padding: 10px !important;
        
        .stat-label {
          font-size: 12px !important;
        }
        
        .stat-value {
          font-size: 20px !important;
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




<template>
  <div class="notice-container">
    <div class="page-header">
      <h1 class="page-title">公告管理</h1>
      <p class="page-subtitle">发布和管理社区公告通知</p>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="公告标题">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入公告标题"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="queryParams.type" placeholder="请选择类型" clearable style="width: 140px">
            <el-option label="重要通知" value="important" />
            <el-option label="一般通知" value="normal" />
            <el-option label="紧急通知" value="urgent" />
            <el-option label="活动通知" value="activity" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="草稿" value="draft" />
            <el-option label="已发布" value="published" />
            <el-option label="已下线" value="offline" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
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

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增公告
        </el-button>
        <el-button type="success" :disabled="!multipleSelection.length" @click="handleBatchPublish">
          <el-icon><Promotion /></el-icon>
          批量发布
        </el-button>
        <el-button type="warning" :disabled="!multipleSelection.length" @click="handleBatchOffline">
          <el-icon><SwitchButton /></el-icon>
          批量下线
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
        :data="noticeList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="250" show-overflow-tooltip />
        <el-table-column label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)" size="small">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isTop"
              @change="handleTopChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="author" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="scope.row.status === 'draft'"
              type="warning"
              size="small"
              @click="handlePublish(scope.row)"
            >
              发布
            </el-button>
            <el-button
              v-if="scope.row.status === 'published'"
              type="info"
              size="small"
              @click="handleOffline(scope.row)"
            >
              下线
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
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

    <!-- 公告详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="公告详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentNotice" class="notice-detail">
        <div class="notice-header">
          <h2 class="notice-title">{{ currentNotice.title }}</h2>
          <div class="notice-meta">
            <el-tag :type="getTypeTagType(currentNotice.type)">
              {{ getTypeText(currentNotice.type) }}
            </el-tag>
            <span class="notice-author">发布人：{{ currentNotice.author }}</span>
            <span class="notice-time">发布时间：{{ currentNotice.publishTime }}</span>
            <span class="notice-views">浏览量：{{ currentNotice.viewCount }}</span>
          </div>
        </div>
        <div class="notice-content" v-html="currentNotice.content"></div>
        <div v-if="currentNotice.attachments.length > 0" class="notice-attachments">
          <h3>附件</h3>
          <div class="attachment-list">
            <div
              v-for="(attachment, index) in currentNotice.attachments"
              :key="index"
              class="attachment-item"
            >
              <el-icon><Document /></el-icon>
              <span>{{ attachment.name }}</span>
              <el-button type="primary" size="small" text>下载</el-button>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑公告弹窗 -->
    <el-dialog
      v-model="showFormDialog"
      :title="formTitle"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择公告类型" style="width: 100%">
                <el-option label="重要通知" value="important" />
                <el-option label="一般通知" value="normal" />
                <el-option label="紧急通知" value="urgent" />
                <el-option label="活动通知" value="activity" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否置顶">
              <el-switch v-model="form.isTop" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            v-model:file-list="form.attachments"
            action="#"
            :auto-upload="false"
            multiple
          >
            <el-button>选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持上传jpg/png/pdf等格式文件，且不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" @click="submitForm">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Promotion,
  SwitchButton,
  Download,
  Document
} from '@element-plus/icons-vue'

export default {
  name: 'Notice',
  components: {
    Search,
    Refresh,
    Plus,
    Promotion,
    SwitchButton,
    Download,
    Document
  },
  setup() {
    const loading = ref(false)
    const noticeList = ref([])
    const total = ref(0)
    const multipleSelection = ref([])
    const showDetailDialog = ref(false)
    const showFormDialog = ref(false)
    const currentNotice = ref(null)
    const formRef = ref(null)
    const dateRange = ref([])
    
    const queryParams = reactive({
      pageNum: 1,
      pageSize: 20,
      title: '',
      type: '',
      status: '',
      startTime: '',
      endTime: ''
    })
    
    const form = reactive({
      id: null,
      title: '',
      type: '',
      content: '',
      isTop: false,
      attachments: []
    })
    
    const formRules = {
      title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
      type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
      content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
    }
    
    const formTitle = computed(() => {
      return form.id ? '编辑公告' : '新增公告'
    })
    
    // 模拟数据
    const mockNotices = [
      {
        id: 1,
        title: '关于小区停水通知',
        type: 'important',
        status: 'published',
        isTop: true,
        author: '物业管理处',
        publishTime: '2024-01-25 09:00:00',
        viewCount: 256,
        content: '<p>尊敬的业主：</p><p>因市政管网维修需要，本小区将于2024年1月26日上午8:00-18:00停水，请各位业主提前做好储水准备。给您带来的不便，敬请谅解。</p><p>物业管理处<br>2024年1月25日</p>',
        attachments: []
      },
      {
        id: 2,
        title: '春节期间物业服务安排',
        type: 'normal',
        status: 'published',
        isTop: false,
        author: '物业管理处',
        publishTime: '2024-01-20 14:30:00',
        viewCount: 189,
        content: '<p>亲爱的业主朋友们：</p><p>春节将至，物业服务中心特将节日期间的服务安排通知如下：</p><ul><li>保安24小时值班</li><li>保洁服务正常</li><li>维修服务电话保持畅通</li></ul><p>祝大家新春快乐！</p>',
        attachments: [
          { name: '春节值班安排表.pdf', size: '1.2MB' }
        ]
      },
      {
        id: 3,
        title: '消防安全演练通知',
        type: 'urgent',
        status: 'draft',
        isTop: false,
        author: '安全管理员',
        publishTime: '',
        viewCount: 0,
        content: '<p>为提高全体业主的消防安全意识，定于本月28日下午2点进行消防安全演练...</p>',
        attachments: []
      }
    ]
    
    const getList = () => {
      loading.value = true
      setTimeout(() => {
        noticeList.value = mockNotices
        total.value = mockNotices.length
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
        title: '',
        type: '',
        status: '',
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
    
    const handleAdd = () => {
      resetForm()
      showFormDialog.value = true
    }
    
    const handleView = (row) => {
      currentNotice.value = row
      showDetailDialog.value = true
    }
    
    const handleEdit = (row) => {
      Object.assign(form, {
        ...row,
        attachments: []
      })
      showFormDialog.value = true
    }
    
    const handlePublish = async (row) => {
      try {
        await ElMessageBox.confirm('确定要发布该公告吗？', '确认操作')
        row.status = 'published'
        row.publishTime = new Date().toLocaleString()
        ElMessage.success('发布成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleOffline = async (row) => {
      try {
        await ElMessageBox.confirm('确定要下线该公告吗？', '确认操作')
        row.status = 'offline'
        ElMessage.success('下线成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该公告吗？删除后不可恢复！', '确认删除', {
          type: 'warning'
        })
        const index = noticeList.value.findIndex(item => item.id === row.id)
        if (index > -1) {
          noticeList.value.splice(index, 1)
        }
        ElMessage.success('删除成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleTopChange = (row) => {
      ElMessage.success(row.isTop ? '已置顶' : '已取消置顶')
    }
    
    const handleBatchPublish = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要发布的公告')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量发布 ${multipleSelection.value.length} 个公告吗？`, '确认操作')
        multipleSelection.value.forEach(notice => {
          if (notice.status === 'draft') {
            notice.status = 'published'
            notice.publishTime = new Date().toLocaleString()
          }
        })
        ElMessage.success('批量发布成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleBatchOffline = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要下线的公告')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量下线 ${multipleSelection.value.length} 个公告吗？`, '确认操作')
        multipleSelection.value.forEach(notice => {
          if (notice.status === 'published') {
            notice.status = 'offline'
          }
        })
        ElMessage.success('批量下线成功')
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
    
    const resetForm = () => {
      Object.assign(form, {
        id: null,
        title: '',
        type: '',
        content: '',
        isTop: false,
        attachments: []
      })
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    }
    
    const saveDraft = () => {
      formRef.value.validate((valid) => {
        if (valid) {
          if (form.id) {
            // 编辑
            const index = noticeList.value.findIndex(item => item.id === form.id)
            if (index > -1) {
              Object.assign(noticeList.value[index], {
                ...form,
                status: 'draft'
              })
            }
            ElMessage.success('保存成功')
          } else {
            // 新增
            const newNotice = {
              ...form,
              id: Date.now(),
              status: 'draft',
              author: '管理员',
              publishTime: '',
              viewCount: 0
            }
            noticeList.value.unshift(newNotice)
            ElMessage.success('保存草稿成功')
          }
          showFormDialog.value = false
          resetForm()
        }
      })
    }
    
    const submitForm = () => {
      formRef.value.validate((valid) => {
        if (valid) {
          if (form.id) {
            // 编辑
            const index = noticeList.value.findIndex(item => item.id === form.id)
            if (index > -1) {
              Object.assign(noticeList.value[index], {
                ...form,
                status: 'published',
                publishTime: new Date().toLocaleString()
              })
            }
            ElMessage.success('发布成功')
          } else {
            // 新增
            const newNotice = {
              ...form,
              id: Date.now(),
              status: 'published',
              author: '管理员',
              publishTime: new Date().toLocaleString(),
              viewCount: 0
            }
            noticeList.value.unshift(newNotice)
            ElMessage.success('发布成功')
          }
          showFormDialog.value = false
          resetForm()
        }
      })
    }
    
    const getTypeText = (type) => {
      const typeMap = {
        important: '重要通知',
        normal: '一般通知',
        urgent: '紧急通知',
        activity: '活动通知'
      }
      return typeMap[type] || '未知'
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        important: 'danger',
        normal: 'primary',
        urgent: 'warning',
        activity: 'success'
      }
      return typeMap[type] || 'primary'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        draft: '草稿',
        published: '已发布',
        offline: '已下线'
      }
      return statusMap[status] || '未知'
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        draft: 'info',
        published: 'success',
        offline: 'warning'
      }
      return statusMap[status] || 'info'
    }
    
    onMounted(() => {
      getList()
    })
    
    return {
      loading,
      noticeList,
      total,
      multipleSelection,
      showDetailDialog,
      showFormDialog,
      currentNotice,
      formRef,
      dateRange,
      queryParams,
      form,
      formRules,
      formTitle,
      getList,
      handleQuery,
      resetQuery,
      handleDateChange,
      handleAdd,
      handleView,
      handleEdit,
      handlePublish,
      handleOffline,
      handleDelete,
      handleTopChange,
      handleBatchPublish,
      handleBatchOffline,
      handleExport,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      resetForm,
      saveDraft,
      submitForm,
      getTypeText,
      getTypeTagType,
      getStatusText,
      getStatusTagType
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.notice-container {
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

.notice-detail {
  .notice-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid $border-color;
    
    .notice-title {
      font-size: 24px;
      font-weight: 700;
      color: $text-primary;
      margin: 0 0 16px 0;
    }
    
    .notice-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      flex-wrap: wrap;
      
      span {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
  
  .notice-content {
    line-height: 1.8;
    color: $text-primary;
    margin-bottom: 24px;
    
    :deep(p) {
      margin-bottom: 16px;
    }
    
    :deep(ul), :deep(ol) {
      padding-left: 20px;
      margin-bottom: 16px;
    }
  }
  
  .notice-attachments {
    h3 {
      color: $text-primary;
      margin-bottom: 16px;
      font-size: 16px;
      font-weight: 600;
    }
    
    .attachment-list {
      .attachment-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        background: $bg-secondary;
        border-radius: 8px;
        margin-bottom: 8px;
        
        span {
          flex: 1;
          color: $text-primary;
        }
      }
    }
  }
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
  
  .notice-detail .notice-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>

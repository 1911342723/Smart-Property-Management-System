<template>
  <div class="community-container responsive-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">社区公告</h1>
        <p class="page-subtitle">发布和管理小区公告通知</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards responsive-grid grid-4">
      <div class="stat-card responsive-card total">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总公告数</div>
        </div>
      </div>
      <div class="stat-card responsive-card published">
        <div class="stat-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.published }}</div>
          <div class="stat-label">已发布</div>
        </div>
      </div>
      <div class="stat-card responsive-card pinned">
        <div class="stat-icon">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pinned }}</div>
          <div class="stat-label">置顶公告</div>
        </div>
      </div>
      <div class="stat-card responsive-card views">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ formatNumber(stats.totalViews) }}</div>
          <div class="stat-label">总浏览量</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section responsive-card">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item label="公告类型">
          <el-select v-model="filters.type" placeholder="全部类型" clearable style="width: 120px">
            <el-option label="重要通知" value="important" />
            <el-option label="物业公告" value="property" />
            <el-option label="活动通知" value="activity" />
            <el-option label="温馨提示" value="reminder" />
            <el-option label="停水停电" value="utility" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
            <el-option label="已下线" value="offline" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶状态">
          <el-select v-model="filters.pinned" placeholder="全部" clearable style="width: 100px">
            <el-option label="已置顶" value="true" />
            <el-option label="未置顶" value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索公告标题、内容"
            prefix-icon="Search"
            style="width: 200px"
            @keyup.enter="loadNotices"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadNotices">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 公告列表 -->
    <div class="notice-list responsive-card">
      <div class="list-header">
        <div class="bulk-actions">
          <el-button type="danger" size="small" :disabled="selectedNotices.length === 0" @click="batchDelete">
            批量删除
          </el-button>
          <el-button type="warning" size="small" :disabled="selectedNotices.length === 0" @click="batchOffline">
            批量下线
          </el-button>
        </div>
        <div class="view-toggle">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="list">列表视图</el-radio-button>
            <el-radio-button label="card">卡片视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 列表视图 -->
      <el-table
        v-if="viewMode === 'list'"
        :data="notices"
        v-loading="loading"
        stripe
        @selection-change="handleSelectionChange"
        @row-click="viewNotice"
        class="notice-table"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="公告信息" min-width="300">
          <template #default="{ row }">
            <div class="notice-info">
              <div class="notice-title">
                <span>{{ row.title }}</span>
                <el-tag v-if="row.pinned" type="warning" size="small" class="pin-tag">
                  <el-icon><Star /></el-icon>
                  置顶
                </el-tag>
                <el-tag v-if="row.urgent" type="danger" size="small">紧急</el-tag>
              </div>
              <div class="notice-summary">{{ row.summary }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="author" label="发布人" width="100">
          <template #default="{ row }">
            <div class="author-info">
              <el-avatar :size="24" :src="row.authorAvatar">{{ row.author.charAt(0) }}</el-avatar>
              <span>{{ row.author }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="views" label="浏览量" width="100">
          <template #default="{ row }">
            <span class="view-count">{{ row.views }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="publishedAt" label="发布时间" width="150">
          <template #default="{ row }">
            <span v-if="row.publishedAt">{{ formatDate(row.publishedAt) }}</span>
            <span v-else class="text-muted">未发布</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click.stop="viewNotice(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click.stop="editNotice(row)">
              编辑
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)" trigger="click">
              <el-button size="small" type="text">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="!row.pinned" command="pin">置顶</el-dropdown-item>
                  <el-dropdown-item v-else command="unpin">取消置顶</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 'draft'" command="publish">发布</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 'published'" command="offline">下线</el-dropdown-item>
                  <el-dropdown-item command="copy">复制</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 卡片视图 -->
      <div v-else class="notice-cards responsive-grid grid-2">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-card responsive-card"
          :class="{ 'pinned': notice.pinned, 'urgent': notice.urgent }"
          @click="viewNotice(notice)"
        >
          <div class="card-header">
            <div class="notice-meta">
              <el-tag :type="getTypeTagType(notice.type)" size="small">
                {{ getTypeName(notice.type) }}
              </el-tag>
              <el-tag :type="getStatusTagType(notice.status)" size="small">
                {{ getStatusName(notice.status) }}
              </el-tag>
            </div>
            <div class="notice-badges">
              <el-tag v-if="notice.pinned" type="warning" size="small">
                <el-icon><Star /></el-icon>
                置顶
              </el-tag>
              <el-tag v-if="notice.urgent" type="danger" size="small">紧急</el-tag>
            </div>
          </div>
          
          <div class="card-content">
            <h3 class="notice-title">{{ notice.title }}</h3>
            <p class="notice-summary">{{ notice.summary }}</p>
            
            <div class="notice-stats">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ notice.views }} 浏览</span>
              </div>
              <div class="stat-item">
                <el-icon><User /></el-icon>
                <span>{{ notice.author }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDate(notice.publishedAt || notice.createdAt) }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-actions" @click.stop>
            <el-button type="primary" size="small" @click="viewNotice(notice)">查看</el-button>
            <el-button type="warning" size="small" @click="editNotice(notice)">编辑</el-button>
            <el-button 
              v-if="!notice.pinned" 
              type="success" 
              size="small" 
              @click="togglePin(notice)"
            >
              置顶
            </el-button>
            <el-button 
              v-else 
              type="info" 
              size="small" 
              @click="togglePin(notice)"
            >
              取消置顶
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadNotices"
          @current-change="loadNotices"
        />
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="selectedNotice?.title"
      width="800px"
      class="notice-dialog"
    >
      <div v-if="selectedNotice" class="notice-detail">
        <div class="detail-header">
          <div class="notice-meta">
            <el-tag :type="getTypeTagType(selectedNotice.type)">{{ getTypeName(selectedNotice.type) }}</el-tag>
            <el-tag :type="getStatusTagType(selectedNotice.status)">{{ getStatusName(selectedNotice.status) }}</el-tag>
            <el-tag v-if="selectedNotice.pinned" type="warning">置顶</el-tag>
            <el-tag v-if="selectedNotice.urgent" type="danger">紧急</el-tag>
          </div>
          <div class="notice-stats">
            <span><el-icon><View /></el-icon> {{ selectedNotice.views }} 浏览</span>
            <span><el-icon><User /></el-icon> {{ selectedNotice.author }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(selectedNotice.publishedAt || selectedNotice.createdAt) }}</span>
          </div>
        </div>
        
        <el-divider />
        
        <div class="detail-content">
          <div class="notice-content" v-html="selectedNotice.content"></div>
          
          <div v-if="selectedNotice.attachments && selectedNotice.attachments.length" class="attachments">
            <h4>附件</h4>
            <div class="attachment-list">
              <div v-for="file in selectedNotice.attachments" :key="file.id" class="attachment-item">
                <el-icon><Document /></el-icon>
                <span>{{ file.name }}</span>
                <el-button type="text" size="small">下载</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="warning" @click="editNotice(selectedNotice)">编辑</el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑公告对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingNotice ? '编辑公告' : '发布公告'"
      width="900px"
      class="create-dialog"
    >
      <el-form :model="noticeForm" :rules="noticeRules" ref="noticeFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="公告标题" prop="title">
              <el-input v-model="noticeForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="noticeForm.type" placeholder="请选择类型">
                <el-option label="重要通知" value="important" />
                <el-option label="物业公告" value="property" />
                <el-option label="活动通知" value="activity" />
                <el-option label="温馨提示" value="reminder" />
                <el-option label="停水停电" value="utility" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="公告摘要" prop="summary">
          <el-input 
            v-model="noticeForm.summary" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入公告摘要（将显示在列表中）"
            maxlength="200" 
            show-word-limit 
          />
        </el-form-item>
        
        <el-form-item label="公告内容" prop="content">
          <div class="editor-container">
            <el-input 
              v-model="noticeForm.content" 
              type="textarea" 
              :rows="10" 
              placeholder="请输入公告详细内容"
            />
          </div>
        </el-form-item>
        
        <el-form-item label="设置选项">
          <el-checkbox-group v-model="noticeForm.options">
            <el-checkbox label="pinned">置顶显示</el-checkbox>
            <el-checkbox label="urgent">紧急公告</el-checkbox>
            <el-checkbox label="notify">推送通知</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="发布时间">
          <el-radio-group v-model="noticeForm.publishType">
            <el-radio label="now">立即发布</el-radio>
            <el-radio label="scheduled">定时发布</el-radio>
            <el-radio label="draft">保存草稿</el-radio>
          </el-radio-group>
          <el-date-picker
            v-if="noticeForm.publishType === 'scheduled'"
            v-model="noticeForm.scheduledAt"
            type="datetime"
            placeholder="选择发布时间"
            style="margin-left: 20px;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNotice" :loading="saving">
          {{ editingNotice ? '保存修改' : '发布公告' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Document, Check, Star, View, Search, ArrowDown, 
  User, Clock 
} from '@element-plus/icons-vue'

export default {
  name: 'CommunityNotice',
  components: {
    Plus, Document, Check, Star, View, Search, ArrowDown, 
    User, Clock
  },
  setup() {
    const loading = ref(false)
    const saving = ref(false)
    const showDetailDialog = ref(false)
    const showCreateDialog = ref(false)
    const selectedNotice = ref(null)
    const editingNotice = ref(null)
    const selectedNotices = ref([])
    const viewMode = ref('list')
    const noticeFormRef = ref(null)
    
    // 统计数据
    const stats = reactive({
      total: 45,
      published: 38,
      pinned: 3,
      totalViews: 15420
    })
    
    // 筛选条件
    const filters = reactive({
      type: '',
      status: '',
      pinned: '',
      keyword: ''
    })
    
    // 分页
    const pagination = reactive({
      current: 1,
      size: 20,
      total: 0
    })
    
    // 公告表单
    const noticeForm = reactive({
      title: '',
      type: '',
      summary: '',
      content: '',
      options: [],
      publishType: 'now',
      scheduledAt: ''
    })
    
    // 表单验证规则
    const noticeRules = {
      title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
      type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
      summary: [{ required: true, message: '请输入公告摘要', trigger: 'blur' }],
      content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
    }
    
    // 公告列表
    const notices = ref([])
    
    // 模拟数据
    const mockNotices = [
      {
        id: 1,
        title: '关于小区停水停电维修的紧急通知',
        type: 'utility',
        summary: '因设备维修需要，明日上午9:00-12:00将停水停电，请业主提前做好准备。',
        content: '<p>尊敬的业主：</p><p>因小区供水设备维修需要，定于2023年12月2日上午9:00-12:00进行停水停电维修。届时将影响全小区正常用水用电。</p><p>请各位业主提前储备生活用水，合理安排用电设备。给您带来的不便，敬请谅解。</p><p>物业服务中心<br>2023年12月1日</p>',
        author: '物业管理处',
        authorAvatar: '',
        status: 'published',
        pinned: true,
        urgent: true,
        views: 1250,
        createdAt: '2023-12-01 10:00:00',
        publishedAt: '2023-12-01 10:30:00',
        attachments: []
      },
      {
        id: 2,
        title: '小区春节联欢活动邀请函',
        type: 'activity',
        summary: '诚邀各位业主参加小区春节联欢活动，共度佳节，增进邻里感情。',
        content: '<p>亲爱的业主朋友们：</p><p>春节将至，为增进邻里感情，营造和谐社区氛围，物业服务中心特举办春节联欢活动。</p><p>活动时间：2024年2月8日晚7:00</p><p>活动地点：小区中央广场</p><p>活动内容：文艺表演、游戏互动、抽奖等</p><p>欢迎各位业主踊跃参加！</p>',
        author: '社区委员会',
        authorAvatar: '',
        status: 'published',
        pinned: false,
        urgent: false,
        views: 856,
        createdAt: '2023-11-28 15:20:00',
        publishedAt: '2023-11-28 16:00:00',
        attachments: [
          { id: 1, name: '活动详情.pdf' },
          { id: 2, name: '报名表.xlsx' }
        ]
      },
      {
        id: 3,
        title: '12月物业费缴费通知',
        type: 'property',
        summary: '12月物业费已开始收取，请各位业主及时缴费，避免产生滞纳金。',
        content: '<p>各位业主：</p><p>12月份物业管理费现已开始收取，请于本月25日前完成缴费。</p><p>缴费方式：</p><ul><li>微信小程序在线缴费</li><li>物业服务中心现场缴费</li><li>银行转账</li></ul><p>逾期未缴费将产生滞纳金，请及时缴费。</p>',
        author: '财务部',
        authorAvatar: '',
        status: 'published',
        pinned: true,
        urgent: false,
        views: 2180,
        createdAt: '2023-12-01 09:00:00',
        publishedAt: '2023-12-01 09:30:00',
        attachments: []
      }
    ]
    
    // 加载公告列表
    const loadNotices = async () => {
      loading.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 500))
        notices.value = mockNotices
        pagination.total = mockNotices.length
      } catch (error) {
        ElMessage.error('加载公告列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 重置筛选条件
    const resetFilters = () => {
      Object.keys(filters).forEach(key => {
        filters[key] = ''
      })
      loadNotices()
    }
    
    // 查看公告详情
    const viewNotice = (notice) => {
      selectedNotice.value = notice
      showDetailDialog.value = true
      // 增加浏览量
      notice.views++
    }
    
    // 编辑公告
    const editNotice = (notice) => {
      editingNotice.value = notice
      noticeForm.title = notice.title
      noticeForm.type = notice.type
      noticeForm.summary = notice.summary
      noticeForm.content = notice.content.replace(/<[^>]*>/g, '') // 简单去除HTML标签
      noticeForm.options = []
      if (notice.pinned) noticeForm.options.push('pinned')
      if (notice.urgent) noticeForm.options.push('urgent')
      noticeForm.publishType = notice.status === 'draft' ? 'draft' : 'now'
      showCreateDialog.value = true
    }
    
    // 保存公告
    const saveNotice = async () => {
      if (!noticeFormRef.value) return
      
      try {
        await noticeFormRef.value.validate()
        saving.value = true
        
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(editingNotice.value ? '公告修改成功' : '公告发布成功')
        showCreateDialog.value = false
        resetForm()
        loadNotices()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        saving.value = false
      }
    }
    
    // 重置表单
    const resetForm = () => {
      editingNotice.value = null
      Object.keys(noticeForm).forEach(key => {
        if (Array.isArray(noticeForm[key])) {
          noticeForm[key] = []
        } else {
          noticeForm[key] = ''
        }
      })
      noticeForm.publishType = 'now'
    }
    
    // 切换置顶状态
    const togglePin = async (notice) => {
      try {
        await new Promise(resolve => setTimeout(resolve, 500))
        notice.pinned = !notice.pinned
        ElMessage.success(notice.pinned ? '置顶成功' : '取消置顶成功')
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
    
    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedNotices.value = selection
    }
    
    // 批量删除
    const batchDelete = () => {
      ElMessageBox.confirm(`确定要删除选中的${selectedNotices.value.length}条公告吗？`, '确认删除', {
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
        loadNotices()
      })
    }
    
    // 批量下线
    const batchOffline = () => {
      ElMessageBox.confirm(`确定要下线选中的${selectedNotices.value.length}条公告吗？`, '确认下线', {
        type: 'warning'
      }).then(() => {
        ElMessage.success('下线成功')
        loadNotices()
      })
    }
    
    // 处理下拉菜单命令
    const handleCommand = async (command, notice) => {
      switch (command) {
        case 'pin':
        case 'unpin':
          togglePin(notice)
          break
        case 'publish':
          try {
            await new Promise(resolve => setTimeout(resolve, 500))
            notice.status = 'published'
            notice.publishedAt = new Date().toISOString()
            ElMessage.success('发布成功')
          } catch (error) {
            ElMessage.error('发布失败')
          }
          break
        case 'offline':
          ElMessageBox.confirm('确定要下线此公告吗？', '确认下线', {
            type: 'warning'
          }).then(() => {
            notice.status = 'offline'
            ElMessage.success('下线成功')
          })
          break
        case 'copy':
          editNotice({ ...notice, id: null, title: notice.title + ' (副本)' })
          break
        case 'delete':
          ElMessageBox.confirm('确定要删除此公告吗？', '确认删除', {
            type: 'warning'
          }).then(() => {
            ElMessage.success('删除成功')
            loadNotices()
          })
          break
      }
    }
    
    // 工具函数
    const getTypeName = (type) => {
      const typeMap = {
        important: '重要通知',
        property: '物业公告',
        activity: '活动通知',
        reminder: '温馨提示',
        utility: '停水停电',
        other: '其他'
      }
      return typeMap[type] || type
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        important: 'danger',
        property: 'primary',
        activity: 'success',
        reminder: 'info',
        utility: 'warning',
        other: ''
      }
      return typeMap[type] || ''
    }
    
    const getStatusName = (status) => {
      const statusMap = {
        published: '已发布',
        draft: '草稿',
        offline: '已下线'
      }
      return statusMap[status] || status
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        published: 'success',
        draft: 'info',
        offline: 'warning'
      }
      return statusMap[status] || ''
    }
    
    const formatNumber = (num) => {
      return num.toLocaleString('zh-CN')
    }
    
    const formatDate = (dateStr) => {
      return new Date(dateStr).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      loadNotices()
    })
    
    return {
      loading,
      saving,
      showDetailDialog,
      showCreateDialog,
      selectedNotice,
      editingNotice,
      selectedNotices,
      viewMode,
      noticeFormRef,
      stats,
      filters,
      pagination,
      noticeForm,
      noticeRules,
      notices,
      loadNotices,
      resetFilters,
      viewNotice,
      editNotice,
      saveNotice,
      resetForm,
      togglePin,
      handleSelectionChange,
      batchDelete,
      batchOffline,
      handleCommand,
      getTypeName,
      getTypeTagType,
      getStatusName,
      getStatusTagType,
      formatNumber,
      formatDate
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.community-container {
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
    text-align: center;
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
      margin: 0 auto 16px;
    }
    
    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: $text-primary;
        line-height: 1;
        margin-bottom: 8px;
      }
      
      .stat-label {
        font-size: 14px;
        color: $text-secondary;
      }
    }
    
    &.total .stat-icon {
      background: linear-gradient(135deg, #5865f2, #4752c4);
    }
    
    &.published .stat-icon {
      background: linear-gradient(135deg, #3ba55c, #27ae60);
    }
    
    &.pinned .stat-icon {
      background: linear-gradient(135deg, #faa61a, #f0932b);
    }
    
    &.views .stat-icon {
      background: linear-gradient(135deg, #667eea, #764ba2);
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

.notice-list {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  padding: 20px;
  
  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .bulk-actions {
      display: flex;
      gap: 12px;
    }
    
    @include mobile {
      flex-direction: column;
      gap: 16px;
      
      .bulk-actions,
      .view-toggle {
        width: 100%;
        justify-content: center;
      }
    }
  }
  
  .notice-table {
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
          cursor: pointer;
          
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
    
    .notice-info {
      .notice-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 8px;
        
        .pin-tag {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
      
      .notice-summary {
        font-size: 12px;
        color: $text-secondary;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }
    
    .author-info {
      display: flex;
      align-items: center;
      gap: 8px;
      
      span {
        font-size: 14px;
      }
    }
    
    .view-count {
      font-weight: 600;
      color: $primary-color;
    }
  }
  
  .notice-cards {
    .notice-card {
      background: $bg-secondary;
      border: 1px solid $border-color;
      border-radius: $border-radius-lg;
      padding: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px $shadow-medium;
      }
      
      &.pinned {
        border-color: $warning-color;
        background: linear-gradient(135deg, $bg-secondary 0%, rgba(250, 166, 26, 0.1) 100%);
      }
      
      &.urgent {
        border-color: $error-color;
        background: linear-gradient(135deg, $bg-secondary 0%, rgba(237, 66, 69, 0.1) 100%);
      }
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .notice-meta {
          display: flex;
          gap: 8px;
        }
        
        .notice-badges {
          display: flex;
          gap: 4px;
          
          .el-tag {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
      
      .card-content {
        .notice-title {
          font-size: 16px;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 12px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .notice-summary {
          font-size: 14px;
          color: $text-secondary;
          line-height: 1.5;
          margin-bottom: 16px;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .notice-stats {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: $text-muted;
          margin-bottom: 16px;
          
          .stat-item {
            display: flex;
            align-items: center;
            gap: 4px;
          }
          
          @include mobile {
            flex-direction: column;
            gap: 8px;
          }
        }
      }
      
      .card-actions {
        display: flex;
        gap: 8px;
        padding-top: 16px;
        border-top: 1px solid $border-color;
        
        @include mobile {
          flex-wrap: wrap;
          
          .el-button {
            flex: 1;
            min-width: auto;
          }
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

.notice-dialog {
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
  
  .notice-detail {
    .detail-header {
      .notice-meta {
        display: flex;
        gap: 8px;
        margin-bottom: 12px;
      }
      
      .notice-stats {
        display: flex;
        gap: 20px;
        font-size: 14px;
        color: $text-secondary;
        
        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
        
        @include mobile {
          flex-direction: column;
          gap: 8px;
        }
      }
    }
    
    .detail-content {
      .notice-content {
        color: $text-primary;
        line-height: 1.8;
        font-size: 14px;
        
        :deep(p) {
          margin-bottom: 16px;
        }
        
        :deep(ul) {
          padding-left: 20px;
          margin-bottom: 16px;
        }
      }
      
      .attachments {
        margin-top: 24px;
        
        h4 {
          margin: 0 0 12px 0;
          color: $text-primary;
        }
        
        .attachment-list {
          .attachment-item {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            background: $bg-secondary;
            border: 1px solid $border-color;
            border-radius: $border-radius;
            margin-bottom: 8px;
            
            span {
              flex: 1;
              color: $text-primary;
            }
          }
        }
      }
    }
  }
}

.create-dialog {
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
  
  .editor-container {
    border: 1px solid $border-color;
    border-radius: $border-radius;
    overflow: hidden;
  }
}
</style>
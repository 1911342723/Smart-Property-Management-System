<template>
  <div class="owner-container">
    <div class="page-header">
      <h1 class="page-title">业主管理</h1>
      <p class="page-subtitle">管理业主信息、房产绑定和审核状态</p>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="业主姓名">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入业主姓名"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="房产绑定">
          <el-select v-model="queryParams.hasProperty" placeholder="绑定状态" clearable style="width: 140px">
            <el-option label="已绑定" value="true" />
            <el-option label="未绑定" value="false" />
          </el-select>
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
          新增业主
        </el-button>
        <el-button type="success" :disabled="!multipleSelection.length" @click="handleBatchApprove">
          <el-icon><Check /></el-icon>
          批量通过
        </el-button>
        <el-button type="warning" :disabled="!multipleSelection.length" @click="handleBatchReject">
          <el-icon><Close /></el-icon>
          批量拒绝
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
        :data="ownerList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" :size="40">
              {{ scope.row.name.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="idCard" label="身份证号" width="180" show-overflow-tooltip />
        <el-table-column label="房产信息" width="200">
          <template #default="scope">
            <div v-if="scope.row.properties.length > 0">
              <el-tag
                v-for="property in scope.row.properties"
                :key="property.id"
                size="small"
                style="margin: 2px"
              >
                {{ property.building }}栋{{ property.unit }}单元{{ property.room }}室
              </el-tag>
            </div>
            <span v-else class="text-muted">未绑定房产</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="120">
          <template #default="scope">
            <el-tag
              :type="getStatusTagType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
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
              type="success"
              size="small"
              v-if="scope.row.status === 'pending'"
              @click="handleApprove(scope.row)"
            >
              通过
            </el-button>
            <el-button
              type="warning"
              size="small"
              v-if="scope.row.status === 'pending'"
              @click="handleReject(scope.row)"
            >
              拒绝
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleBindProperty(scope.row)"
            >
              绑定房产
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

    <!-- 业主详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="业主详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentOwner" class="owner-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="头像">
            <el-avatar :src="currentOwner.avatar" :size="60">
              {{ currentOwner.name.charAt(0) }}
            </el-avatar>
          </el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentOwner.name }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentOwner.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ currentOwner.idCard }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentOwner.email || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人">{{ currentOwner.emergencyContact || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系电话">{{ currentOwner.emergencyPhone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="getStatusTagType(currentOwner.status)">
              {{ getStatusText(currentOwner.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">{{ currentOwner.createTime }}</el-descriptions-item>
        </el-descriptions>

        <div class="property-section">
          <h3>绑定房产</h3>
          <div v-if="currentOwner.properties.length > 0" class="property-list">
            <el-card
              v-for="property in currentOwner.properties"
              :key="property.id"
              class="property-card"
            >
              <div class="property-info">
                <h4>{{ property.building }}栋{{ property.unit }}单元{{ property.room }}室</h4>
                <p>面积：{{ property.area }}㎡</p>
                <p>类型：{{ property.type }}</p>
                <p>绑定时间：{{ property.bindTime }}</p>
              </div>
            </el-card>
          </div>
          <div v-else class="no-property">
            <el-empty description="暂无绑定房产" />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="currentOwner && currentOwner.status === 'pending'" type="success" @click="handleApprove(currentOwner)">
          通过审核
        </el-button>
        <el-button v-if="currentOwner && currentOwner.status === 'pending'" type="warning" @click="handleReject(currentOwner)">
          拒绝审核
        </el-button>
      </template>
    </el-dialog>

    <!-- 房产绑定弹窗 -->
    <el-dialog
      v-model="showBindDialog"
      title="绑定房产"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="bindForm" label-width="80px">
        <el-form-item label="选择房产">
          <el-select
            v-model="bindForm.propertyId"
            placeholder="请选择要绑定的房产"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="property in availableProperties"
              :key="property.id"
              :label="`${property.building}栋${property.unit}单元${property.room}室`"
              :value="property.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定类型">
          <el-radio-group v-model="bindForm.bindType">
            <el-radio label="owner">业主</el-radio>
            <el-radio label="tenant">租户</el-radio>
            <el-radio label="family">家属</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="bindForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmBind">确定绑定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Check, Close, Download } from '@element-plus/icons-vue'

export default {
  name: 'Owner',
  components: {
    Search,
    Refresh,
    Plus,
    Check,
    Close,
    Download
  },
  setup() {
    const loading = ref(false)
    const ownerList = ref([])
    const total = ref(0)
    const multipleSelection = ref([])
    const showDetailDialog = ref(false)
    const showBindDialog = ref(false)
    const currentOwner = ref(null)
    const availableProperties = ref([])
    
    const queryParams = reactive({
      pageNum: 1,
      pageSize: 20,
      name: '',
      phone: '',
      status: '',
      hasProperty: ''
    })
    
    const bindForm = reactive({
      ownerId: null,
      propertyId: null,
      bindType: 'owner',
      remark: ''
    })
    
    // 模拟数据
    const mockOwners = [
      {
        id: 1,
        name: '张三',
        phone: '13800138001',
        idCard: '110101199001011234',
        email: 'zhangsan@example.com',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        status: 'approved',
        emergencyContact: '李四',
        emergencyPhone: '13800138002',
        createTime: '2024-01-15 10:30:00',
        properties: [
          {
            id: 1,
            building: 'A',
            unit: '1',
            room: '101',
            area: 120,
            type: '三室两厅',
            bindTime: '2024-01-16 09:00:00'
          }
        ]
      },
      {
        id: 2,
        name: '王五',
        phone: '13800138003',
        idCard: '110101199002022345',
        email: '',
        avatar: '',
        status: 'pending',
        emergencyContact: '',
        emergencyPhone: '',
        createTime: '2024-01-20 14:20:00',
        properties: []
      },
      {
        id: 3,
        name: '赵六',
        phone: '13800138004',
        idCard: '110101199003033456',
        email: 'zhaoliu@example.com',
        avatar: '',
        status: 'rejected',
        emergencyContact: '钱七',
        emergencyPhone: '13800138005',
        createTime: '2024-01-18 16:45:00',
        properties: []
      }
    ]
    
    const mockProperties = [
      { id: 1, building: 'A', unit: '1', room: '102', area: 95, type: '两室一厅' },
      { id: 2, building: 'A', unit: '2', room: '201', area: 110, type: '三室一厅' },
      { id: 3, building: 'B', unit: '1', room: '101', area: 85, type: '两室一厅' },
      { id: 4, building: 'B', unit: '2', room: '202', area: 130, type: '三室两厅' }
    ]
    
    const getList = () => {
      loading.value = true
      // 模拟API调用
      setTimeout(() => {
        ownerList.value = mockOwners
        total.value = mockOwners.length
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
        name: '',
        phone: '',
        status: '',
        hasProperty: ''
      })
      getList()
    }
    
    const handleAdd = () => {
      ElMessage.info('新增业主功能开发中...')
    }
    
    const handleView = (row) => {
      currentOwner.value = row
      showDetailDialog.value = true
    }
    
    const handleApprove = async (row) => {
      try {
        await ElMessageBox.confirm('确定要通过该业主的审核吗？', '确认操作')
        // 模拟API调用
        row.status = 'approved'
        ElMessage.success('审核通过成功')
        showDetailDialog.value = false
      } catch {
        // 用户取消
      }
    }
    
    const handleReject = async (row) => {
      try {
        await ElMessageBox.confirm('确定要拒绝该业主的审核吗？', '确认操作')
        // 模拟API调用
        row.status = 'rejected'
        ElMessage.success('审核拒绝成功')
        showDetailDialog.value = false
      } catch {
        // 用户取消
      }
    }
    
    const handleBindProperty = (row) => {
      currentOwner.value = row
      bindForm.ownerId = row.id
      availableProperties.value = mockProperties
      showBindDialog.value = true
    }
    
    const confirmBind = () => {
      if (!bindForm.propertyId) {
        ElMessage.error('请选择要绑定的房产')
        return
      }
      
      // 模拟绑定操作
      const property = availableProperties.value.find(p => p.id === bindForm.propertyId)
      if (property) {
        currentOwner.value.properties.push({
          ...property,
          bindTime: new Date().toLocaleString()
        })
        ElMessage.success('房产绑定成功')
        showBindDialog.value = false
        // 重置表单
        Object.assign(bindForm, {
          ownerId: null,
          propertyId: null,
          bindType: 'owner',
          remark: ''
        })
      }
    }
    
    const handleBatchApprove = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要操作的业主')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量通过 ${multipleSelection.value.length} 个业主的审核吗？`, '确认操作')
        // 模拟批量操作
        multipleSelection.value.forEach(owner => {
          if (owner.status === 'pending') {
            owner.status = 'approved'
          }
        })
        ElMessage.success('批量审核通过成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleBatchReject = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要操作的业主')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量拒绝 ${multipleSelection.value.length} 个业主的审核吗？`, '确认操作')
        // 模拟批量操作
        multipleSelection.value.forEach(owner => {
          if (owner.status === 'pending') {
            owner.status = 'rejected'
          }
        })
        ElMessage.success('批量审核拒绝成功')
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
    
    const getStatusTagType = (status) => {
      const statusMap = {
        pending: 'warning',
        approved: 'success',
        rejected: 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已拒绝'
      }
      return statusMap[status] || '未知'
    }
    
    onMounted(() => {
      getList()
    })
    
    return {
      loading,
      ownerList,
      total,
      multipleSelection,
      showDetailDialog,
      showBindDialog,
      currentOwner,
      availableProperties,
      queryParams,
      bindForm,
      getList,
      handleQuery,
      resetQuery,
      handleAdd,
      handleView,
      handleApprove,
      handleReject,
      handleBindProperty,
      confirmBind,
      handleBatchApprove,
      handleBatchReject,
      handleExport,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      getStatusTagType,
      getStatusText
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.owner-container {
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

.owner-detail {
  .property-section {
    margin-top: 24px;
    
    h3 {
      color: $text-primary;
      margin-bottom: 16px;
      font-size: 16px;
      font-weight: 600;
    }
    
    .property-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
      
      .property-card {
        .property-info {
          h4 {
            color: $text-primary;
            margin: 0 0 8px 0;
            font-size: 16px;
          }
          
          p {
            color: $text-secondary;
            margin: 4px 0;
            font-size: 14px;
          }
        }
      }
    }
    
    .no-property {
      text-align: center;
      padding: 40px;
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
  
  .owner-detail .property-list {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <div class="bill-container">
    <div class="page-header">
      <h1 class="page-title">账单管理</h1>
      <p class="page-subtitle">管理物业费、水电费等各类账单</p>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="账单类型">
          <el-select v-model="queryParams.type" placeholder="请选择类型" clearable style="width: 140px">
            <el-option label="物业费" value="property" />
            <el-option label="水费" value="water" />
            <el-option label="电费" value="electricity" />
            <el-option label="燃气费" value="gas" />
            <el-option label="停车费" value="parking" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="未缴费" value="unpaid" />
            <el-option label="已缴费" value="paid" />
            <el-option label="已逾期" value="overdue" />
          </el-select>
        </el-form-item>
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="queryParams.month"
            type="month"
            placeholder="选择月份"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="房屋地址">
          <el-input
            v-model="queryParams.address"
            placeholder="请输入房屋地址"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
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
        <el-button type="primary" @click="handleGenerate">
          <el-icon><Plus /></el-icon>
          生成账单
        </el-button>
        <el-button type="success" @click="handleBatchGenerate">
          <el-icon><Document /></el-icon>
          批量生成
        </el-button>
        <el-button type="warning" :disabled="!multipleSelection.length" @click="handleBatchSend">
          <el-icon><Message /></el-icon>
          批量发送
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
        :data="billList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="billNo" label="账单号" width="160" />
        <el-table-column label="账单类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)" size="small">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="房屋信息" width="180">
          <template #default="scope">
            <div class="property-info">
              <div class="property-address">
                {{ scope.row.building }}栋{{ scope.row.unit }}单元{{ scope.row.room }}室
              </div>
              <div class="owner-name">{{ scope.row.ownerName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="period" label="账单周期" width="120" />
        <el-table-column label="金额" width="120">
          <template #default="scope">
            <div class="amount">¥{{ scope.row.amount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="缴费状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期时间" width="160" />
        <el-table-column prop="payTime" label="缴费时间" width="160">
          <template #default="scope">
            {{ scope.row.payTime || '-' }}
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
              v-if="scope.row.status === 'unpaid'"
              type="success"
              size="small"
              @click="handlePay(scope.row)"
            >
              标记缴费
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleSend(scope.row)"
            >
              发送
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
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Document, Message, Download } from '@element-plus/icons-vue'

export default {
  name: 'Bill',
  components: {
    Search,
    Refresh,
    Plus,
    Document,
    Message,
    Download
  },
  setup() {
    const loading = ref(false)
    const billList = ref([])
    const total = ref(0)
    const multipleSelection = ref([])
    
    const queryParams = reactive({
      pageNum: 1,
      pageSize: 20,
      type: '',
      status: '',
      month: null,
      address: ''
    })
    
    // 模拟数据
    const mockBills = [
      {
        id: 1,
        billNo: 'B202401001',
        type: 'property',
        building: 'A',
        unit: '1',
        room: '101',
        ownerName: '张三',
        period: '2024年1月',
        amount: 1200.00,
        status: 'paid',
        dueDate: '2024-01-31 23:59:59',
        payTime: '2024-01-15 10:30:00',
        createTime: '2024-01-01 09:00:00'
      },
      {
        id: 2,
        billNo: 'B202401002',
        type: 'water',
        building: 'A',
        unit: '1',
        room: '102',
        ownerName: '李四',
        period: '2024年1月',
        amount: 85.50,
        status: 'unpaid',
        dueDate: '2024-01-31 23:59:59',
        payTime: null,
        createTime: '2024-01-01 09:00:00'
      },
      {
        id: 3,
        billNo: 'B202401003',
        type: 'electricity',
        building: 'B',
        unit: '2',
        room: '201',
        ownerName: '王五',
        period: '2024年1月',
        amount: 150.80,
        status: 'overdue',
        dueDate: '2024-01-31 23:59:59',
        payTime: null,
        createTime: '2024-01-01 09:00:00'
      }
    ]
    
    const getList = () => {
      loading.value = true
      setTimeout(() => {
        billList.value = mockBills
        total.value = mockBills.length
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
        type: '',
        status: '',
        month: null,
        address: ''
      })
      getList()
    }
    
    const handleGenerate = () => {
      ElMessage.info('生成账单功能开发中...')
    }
    
    const handleBatchGenerate = () => {
      ElMessage.info('批量生成账单功能开发中...')
    }
    
    const handleBatchSend = () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要发送的账单')
        return
      }
      ElMessage.info('批量发送功能开发中...')
    }
    
    const handleExport = () => {
      ElMessage.info('导出功能开发中...')
    }
    
    const handleView = (row) => {
      ElMessage.info('查看账单详情功能开发中...')
    }
    
    const handlePay = async (row) => {
      try {
        await ElMessageBox.confirm('确定要标记该账单为已缴费吗？', '确认操作')
        row.status = 'paid'
        row.payTime = new Date().toLocaleString()
        ElMessage.success('标记成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleSend = (row) => {
      ElMessage.info('发送账单功能开发中...')
    }
    
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该账单吗？删除后不可恢复！', '确认删除', {
          type: 'warning'
        })
        const index = billList.value.findIndex(item => item.id === row.id)
        if (index > -1) {
          billList.value.splice(index, 1)
        }
        ElMessage.success('删除成功')
      } catch {
        // 用户取消
      }
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
    
    const getTypeText = (type) => {
      const typeMap = {
        property: '物业费',
        water: '水费',
        electricity: '电费',
        gas: '燃气费',
        parking: '停车费'
      }
      return typeMap[type] || '未知'
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        property: 'primary',
        water: 'info',
        electricity: 'warning',
        gas: 'success',
        parking: 'danger'
      }
      return typeMap[type] || 'info'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        unpaid: '未缴费',
        paid: '已缴费',
        overdue: '已逾期'
      }
      return statusMap[status] || '未知'
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        unpaid: 'warning',
        paid: 'success',
        overdue: 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    onMounted(() => {
      getList()
    })
    
    return {
      loading,
      billList,
      total,
      multipleSelection,
      queryParams,
      getList,
      handleQuery,
      resetQuery,
      handleGenerate,
      handleBatchGenerate,
      handleBatchSend,
      handleExport,
      handleView,
      handlePay,
      handleSend,
      handleDelete,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
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

.bill-container {
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

.property-info {
  .property-address {
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 4px;
  }
  
  .owner-name {
    font-size: 12px;
    color: $text-secondary;
  }
}

.amount {
  font-weight: 600;
  color: $text-primary;
  font-size: 16px;
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
}
</style>

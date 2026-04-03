<template>
  <div class="finance-container responsive-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">财务管理</h1>
        <p class="page-subtitle">管理物业费用、账单和收缴情况</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showBatchBillDialog = true">
          <el-icon><Plus /></el-icon>
          批量生成账单
        </el-button>
        <el-button type="success" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 财务统计卡片 -->
    <div class="stats-cards responsive-grid grid-4">
      <div class="stat-card responsive-card revenue">
        <div class="stat-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatMoney(stats.totalRevenue) }}</div>
          <div class="stat-label">本月收入</div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            {{ stats.revenueTrend }}%
          </div>
        </div>
      </div>
      <div class="stat-card responsive-card unpaid">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatMoney(stats.unpaidAmount) }}</div>
          <div class="stat-label">待收金额</div>
          <div class="stat-trend down">
            <el-icon><ArrowDown /></el-icon>
            {{ stats.unpaidTrend }}%
          </div>
        </div>
      </div>
      <div class="stat-card responsive-card collection-rate">
        <div class="stat-icon">
          <el-icon><PieChart /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.collectionRate }}%</div>
          <div class="stat-label">收缴率</div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            {{ stats.collectionTrend }}%
          </div>
        </div>
      </div>
      <div class="stat-card responsive-card overdue">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.overdueCount }}</div>
          <div class="stat-label">逾期账单</div>
          <div class="stat-trend down">
            <el-icon><ArrowDown /></el-icon>
            {{ stats.overdueTrend }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section responsive-card">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item label="费用类型">
          <el-select v-model="filters.type" placeholder="全部类型" clearable style="width: 140px">
            <el-option label="物业费" value="property" />
            <el-option label="停车费" value="parking" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待缴费" value="pending" />
            <el-option label="已缴费" value="paid" />
            <el-option label="已逾期" value="overdue" />
            <el-option label="部分缴费" value="partial" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费周期">
          <el-date-picker
            v-model="filters.period"
            type="month"
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="filters.building" placeholder="全部楼栋" clearable style="width: 120px">
            <el-option label="A栋" value="A" />
            <el-option label="B栋" value="B" />
            <el-option label="C栋" value="C" />
            <el-option label="D栋" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索业主姓名、房号"
            prefix-icon="Search"
            style="width: 220px"
            @keyup.enter="loadBills"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadBills">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetFilters">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 账单列表 -->
    <div class="bill-list responsive-card">
      <div class="list-header">
        <div class="bulk-actions">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
          <el-button type="primary" size="small" :disabled="selectedBills.length === 0" @click="batchRemind">
            批量催缴
          </el-button>
          <el-button type="success" size="small" :disabled="selectedBills.length === 0" @click="batchExport">
            批量导出
          </el-button>
        </div>
        <div class="view-toggle">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="table">表格视图</el-radio-button>
            <el-radio-button label="card">卡片视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 表格视图 -->
      <el-table
        v-if="viewMode === 'table'"
        :data="bills"
        v-loading="loading"
        stripe
        @selection-change="handleSelectionChange"
        class="bill-table"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="billNo" label="账单编号" width="150">
          <template #default="{ row }">
            <span class="bill-no">{{ row.billNo }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="房产信息" width="120">
          <template #default="{ row }">
            <div class="property-info">
              <div class="property-address">{{ row.building }}栋{{ row.unit }}-{{ row.room }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="ownerName" label="业主" width="100">
          <template #default="{ row }">
            <div class="owner-info">
              <el-avatar :size="24" :src="row.ownerAvatar">{{ row.ownerName.charAt(0) }}</el-avatar>
              <span>{{ row.ownerName }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="type" label="费用类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="period" label="缴费周期" width="100" />
        
        <el-table-column prop="amount" label="应缴金额" width="100">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="paidAmount" label="已缴金额" width="100">
          <template #default="{ row }">
            <span class="paid-amount">¥{{ row.paidAmount || 0 }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="dueDate" label="截止日期" width="120">
          <template #default="{ row }">
            <span :class="{ 'overdue-date': isOverdue(row.dueDate) }">
              {{ formatDate(row.dueDate) }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="paidAt" label="缴费时间" width="150">
          <template #default="{ row }">
            <span v-if="row.paidAt">{{ formatDate(row.paidAt) }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewBill(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.status === 'pending' || row.status === 'overdue'" 
              type="warning" 
              size="small" 
              @click="sendReminder(row)"
            >
              催缴
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)" trigger="click">
              <el-button size="small" type="text">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="payment">记录缴费</el-dropdown-item>
                  <el-dropdown-item command="export">导出账单</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 卡片视图 -->
      <div v-else class="bill-cards responsive-grid grid-3">
        <div
          v-for="bill in bills"
          :key="bill.id"
          class="bill-card responsive-card"
          :class="{ 'overdue': bill.status === 'overdue' }"
        >
          <div class="card-header">
            <div class="bill-info">
              <div class="bill-no">{{ bill.billNo }}</div>
              <el-tag :type="getStatusTagType(bill.status)" size="small">
                {{ getStatusName(bill.status) }}
              </el-tag>
            </div>
            <el-checkbox v-model="bill.selected" @change="updateSelection" />
          </div>
          
          <div class="card-content">
            <div class="property-section">
              <div class="property-address">
                <el-icon><House /></el-icon>
                {{ bill.building }}栋{{ bill.unit }}-{{ bill.room }}
              </div>
              <div class="owner-name">{{ bill.ownerName }}</div>
            </div>
            
            <div class="bill-details">
              <div class="detail-item">
                <span class="label">费用类型：</span>
                <el-tag :type="getTypeTagType(bill.type)" size="small">
                  {{ getTypeName(bill.type) }}
                </el-tag>
              </div>
              <div class="detail-item">
                <span class="label">缴费周期：</span>
                <span>{{ bill.period }}</span>
              </div>
              <div class="detail-item">
                <span class="label">应缴金额：</span>
                <span class="amount">¥{{ bill.amount }}</span>
              </div>
              <div class="detail-item">
                <span class="label">截止日期：</span>
                <span :class="{ 'overdue-date': isOverdue(bill.dueDate) }">
                  {{ formatDate(bill.dueDate) }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="card-actions">
            <el-button type="primary" size="small" @click="viewBill(bill)">详情</el-button>
            <el-button 
              v-if="bill.status === 'pending' || bill.status === 'overdue'" 
              type="warning" 
              size="small" 
              @click="sendReminder(bill)"
            >
              催缴
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
          @size-change="loadBills"
          @current-change="loadBills"
        />
      </div>
    </div>

    <!-- 账单详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="`账单详情 - ${selectedBill?.billNo}`"
      width="700px"
      class="bill-dialog"
    >
      <div v-if="selectedBill" class="bill-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单编号">{{ selectedBill.billNo }}</el-descriptions-item>
          <el-descriptions-item label="房产地址">{{ selectedBill.building }}栋{{ selectedBill.unit }}-{{ selectedBill.room }}</el-descriptions-item>
          <el-descriptions-item label="业主姓名">{{ selectedBill.ownerName }}</el-descriptions-item>
          <el-descriptions-item label="费用类型">{{ getTypeName(selectedBill.type) }}</el-descriptions-item>
          <el-descriptions-item label="缴费周期">{{ selectedBill.period }}</el-descriptions-item>
          <el-descriptions-item label="应缴金额">¥{{ selectedBill.amount }}</el-descriptions-item>
          <el-descriptions-item label="已缴金额">¥{{ selectedBill.paidAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="账单状态">
            <el-tag :type="getStatusTagType(selectedBill.status)">{{ getStatusName(selectedBill.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="生成时间">{{ formatDate(selectedBill.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="截止日期">{{ formatDate(selectedBill.dueDate) }}</el-descriptions-item>
          <el-descriptions-item label="缴费时间">{{ selectedBill.paidAt ? formatDate(selectedBill.paidAt) : '未缴费' }}</el-descriptions-item>
          <el-descriptions-item label="缴费方式">{{ selectedBill.paymentMethod || '未缴费' }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="selectedBill.paymentHistory && selectedBill.paymentHistory.length" class="payment-history">
          <h4>缴费记录</h4>
          <el-timeline>
            <el-timeline-item
              v-for="payment in selectedBill.paymentHistory"
              :key="payment.id"
              :timestamp="formatDate(payment.paidAt)"
            >
              缴费 ¥{{ payment.amount }} - {{ payment.method }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedBill?.status !== 'paid'" type="primary" @click="recordPayment">记录缴费</el-button>
      </template>
    </el-dialog>

    <!-- 批量生成账单对话框 -->
    <el-dialog v-model="showBatchBillDialog" title="批量生成账单" width="600px">
      <el-form :model="batchBillForm" label-width="100px">
        <el-form-item label="费用类型" required>
          <el-select v-model="batchBillForm.type" placeholder="请选择费用类型">
            <el-option label="物业费" value="property" />
            <el-option label="停车费" value="parking" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费周期" required>
          <el-date-picker
            v-model="batchBillForm.period"
            type="month"
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
          />
        </el-form-item>
        <el-form-item label="截止日期" required>
          <el-date-picker
            v-model="batchBillForm.dueDate"
            type="date"
            placeholder="选择截止日期"
          />
        </el-form-item>
        <el-form-item label="生成范围">
          <el-radio-group v-model="batchBillForm.scope">
            <el-radio label="all">全部房产</el-radio>
            <el-radio label="building">指定楼栋</el-radio>
            <el-radio label="custom">自定义选择</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="batchBillForm.scope === 'building'" label="选择楼栋">
          <el-select v-model="batchBillForm.buildings" multiple placeholder="请选择楼栋">
            <el-option label="A栋" value="A" />
            <el-option label="B栋" value="B" />
            <el-option label="C栋" value="C" />
            <el-option label="D栋" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="batchBillForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBatchBillDialog = false">取消</el-button>
        <el-button type="primary" @click="generateBatchBills" :loading="generating">生成账单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Download, Money, Warning, PieChart, Clock, ArrowUp, ArrowDown, 
  Search, Refresh, ArrowDown as ArrowDownIcon, House 
} from '@element-plus/icons-vue'
import { getBillList, getBillStats, createBillsBatch, payBill, exportBills } from '@/api/bill'

export default {
  name: 'FinanceBill',
  components: {
    Plus, Download, Money, Warning, PieChart, Clock, ArrowUp, ArrowDown, 
    Search, Refresh, ArrowDown: ArrowDownIcon, House
  },
  setup() {
    const loading = ref(false)
    const generating = ref(false)
    const showDetailDialog = ref(false)
    const showBatchBillDialog = ref(false)
    const selectedBill = ref(null)
    const selectAll = ref(false)
    const selectedBills = ref([])
    const viewMode = ref('table')
    
    // 统计数据
    const stats = reactive({
      totalRevenue: 458900,
      revenueTrend: 8.5,
      unpaidAmount: 89600,
      unpaidTrend: -12.3,
      collectionRate: 94.8,
      collectionTrend: 2.1,
      overdueCount: 23,
      overdueTrend: -15.6
    })
    
    // 筛选条件
    const filters = reactive({
      type: '',
      status: '',
      period: '',
      building: '',
      keyword: ''
    })
    
    // 分页
    const pagination = reactive({
      current: 1,
      size: 20,
      total: 0
    })
    
    // 批量生成账单表单
    const batchBillForm = reactive({
      type: '',
      period: '',
      dueDate: '',
      scope: 'all',
      buildings: [],
      remark: ''
    })
    
    // 账单列表
    const bills = ref([])
    
    // 模拟数据
    const mockBills = [
      {
        id: 1,
        billNo: 'BILL202312001',
        building: 'A',
        unit: '1',
        room: '101',
        ownerName: '张三',
        ownerAvatar: '',
        type: 'property',
        period: '2023-12',
        amount: 280,
        paidAmount: 280,
        status: 'paid',
        dueDate: '2023-12-31',
        createdAt: '2023-12-01 09:00:00',
        paidAt: '2023-12-15 14:30:00',
        paymentMethod: '微信支付',
        paymentHistory: [
          { id: 1, amount: 280, method: '微信支付', paidAt: '2023-12-15 14:30:00' }
        ],
        selected: false
      },
      {
        id: 2,
        billNo: 'BILL202312002',
        building: 'B',
        unit: '2',
        room: '205',
        ownerName: '李四',
        ownerAvatar: '',
        type: 'property',
        period: '2023-12',
        amount: 280,
        paidAmount: 0,
        status: 'overdue',
        dueDate: '2023-12-31',
        createdAt: '2023-12-01 09:00:00',
        paidAt: null,
        paymentMethod: null,
        paymentHistory: [],
        selected: false
      },
      {
        id: 3,
        billNo: 'BILL202312003',
        building: 'A',
        unit: '1',
        room: '102',
        ownerName: '王五',
        ownerAvatar: '',
        type: 'parking',
        period: '2023-12',
        amount: 200,
        paidAmount: 0,
        status: 'pending',
        dueDate: '2024-01-15',
        createdAt: '2023-12-01 09:00:00',
        paidAt: null,
        paymentMethod: null,
        paymentHistory: [],
        selected: false
      }
    ]
    
    // 加载账单列表
    const loadBills = async () => {
      loading.value = true
      try {
        const params = {
          pageNum: pagination.page,
          pageSize: pagination.pageSize,
          billType: mapFilterType(filters.type),
          status: mapFilterStatus(filters.status),
          billingPeriod: filters.period
        }
        
        console.log('请求账单列表参数:', params)
        
        const response = await getBillList(params)
        console.log('API响应:', response)
        
        if (response && response.data) {
          const result = response.data
          const list = result.list || result.records || []
          
          // 映射后端数据到前端格式
          bills.value = list.map(bill => ({
            id: bill.id,
            billNo: bill.billNo,
            building: extractBuilding(bill.roomAddress),
            unit: extractUnit(bill.roomAddress),
            room: extractRoom(bill.roomAddress),
            ownerName: bill.ownerName || '未知',
            ownerAvatar: '',
            type: mapBillType(bill.billType),
            period: bill.billingPeriod,
            amount: bill.amount || 0,
            paidAmount: bill.paidAmount || 0,
            status: mapBillStatus(bill.status),
            dueDate: formatDate(bill.dueDate),
            createdAt: formatDateTime(bill.createTime),
            paidAt: bill.paidDate ? formatDateTime(bill.paidDate) : null,
            paymentMethod: bill.paymentMethod || null,
            paymentHistory: [],
            selected: false
          }))
          
          pagination.total = result.total || 0
          console.log('账单列表加载完成:', bills.value)
        }
      } catch (error) {
        console.error('加载账单列表失败:', error)
        ElMessage.error('加载账单列表失败: ' + (error.message || '未知错误'))
        // 使用模拟数据作为后备
        bills.value = mockBills.map(bill => ({ ...bill, selected: false }))
        pagination.total = mockBills.length
      } finally {
        loading.value = false
      }
    }
    
    // 类型映射函数
    const mapFilterType = (type) => {
      const typeMap = {
        'property': 'PROPERTY_FEE',
        'parking': 'PARKING_FEE'
      }
      return typeMap[type] || ''
    }
    
    const mapFilterStatus = (status) => {
      const statusMap = {
        'pending': 'UNPAID',
        'paid': 'PAID',
        'overdue': 'OVERDUE',
        'partial': 'PARTIAL'
      }
      return statusMap[status] || ''
    }
    
    const mapBillType = (billType) => {
      const typeMap = {
        'PROPERTY_FEE': 'property',
        'PARKING_FEE': 'parking'
      }
      return typeMap[billType] || 'property'
    }
    
    const mapBillStatus = (status) => {
      const statusMap = {
        'UNPAID': 'pending',
        'PAID': 'paid',
        'OVERDUE': 'overdue',
        'PARTIAL': 'partial'
      }
      return statusMap[status] || 'pending'
    }
    
    const extractBuilding = (address) => {
      if (!address) return 'A'
      const match = address.match(/([A-Z])\u680b/)
      return match ? match[1] : 'A'
    }
    
    const extractUnit = (address) => {
      if (!address) return '1'
      const match = address.match(/(\d+)\u5355\u5143/)
      return match ? match[1] : '1'
    }
    
    const extractRoom = (address) => {
      if (!address) return '101'
      const match = address.match(/(\d+)\u5ba4/)
      return match ? match[1] : '101'
    }
    
    const formatDateTime = (dateTime) => {
      if (!dateTime) return ''
      if (typeof dateTime === 'string') return dateTime
      
      // 处理LocalDateTime对象
      if (dateTime.year) {
        return `${dateTime.year}-${String(dateTime.monthValue).padStart(2, '0')}-${String(dateTime.dayOfMonth).padStart(2, '0')} ${String(dateTime.hour).padStart(2, '0')}:${String(dateTime.minute).padStart(2, '0')}:${String(dateTime.second).padStart(2, '0')}`
      }
      
      return String(dateTime)
    }
    
    // 重置筛选条件
    const resetFilters = () => {
      Object.keys(filters).forEach(key => {
        filters[key] = ''
      })
      loadBills()
    }
    
    // 查看账单详情
    const viewBill = (bill) => {
      selectedBill.value = bill
      showDetailDialog.value = true
    }
    
    // 批量生成账单
    const generateBatchBills = async () => {
      generating.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1500))
        ElMessage.success('账单生成成功')
        showBatchBillDialog.value = false
        loadBills()
      } catch (error) {
        ElMessage.error('生成账单失败')
      } finally {
        generating.value = false
      }
    }
    
    // 发送催缴通知
    const sendReminder = async (bill) => {
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success(`已向${bill.ownerName}发送催缴通知`)
      } catch (error) {
        ElMessage.error('发送催缴通知失败')
      }
    }
    
    // 记录缴费
    const recordPayment = () => {
      ElMessage.info('记录缴费功能开发中')
    }
    
    // 导出报表
    const exportReport = async () => {
      try {
        ElMessage.info('正在导出账单数据，请稍候...')
        
        const params = {
          billType: filters.type,
          status: filters.status,
          billingPeriod: filters.period
        }
        
        const response = await exportBills(params)
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        const fileName = `账单列表_${new Date().getTime()}.xlsx`
        link.setAttribute('download', fileName)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        ElMessage.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        ElMessage.error('导出失败: ' + (error.message || '未知错误'))
      }
    }
    
    // 批量催缴
    const batchRemind = async () => {
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success(`已向${selectedBills.value.length}位业主发送催缴通知`)
      } catch (error) {
        ElMessage.error('批量催缴失败')
      }
    }
    
    // 批量导出
    const batchExport = async () => {
      if (selectedBills.value.length === 0) {
        ElMessage.warning('请先选择要导出的账单')
        return
      }
      
      try {
        ElMessage.info('正在导出选中的账单，请稍候...')
        
        // 使用当前筛选条件导出（实际应该支持按选中ID导出，这里简化处理）
        const params = {
          billType: filters.type,
          status: filters.status,
          billingPeriod: filters.period
        }
        
        const response = await exportBills(params)
        
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `选中账单_${new Date().getTime()}.xlsx`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        ElMessage.success(`已导出${selectedBills.value.length}条账单记录`)
      } catch (error) {
        console.error('导出失败:', error)
        ElMessage.error('导出失败: ' + (error.message || '未知错误'))
      }
    }
    
    // 处理全选
    const handleSelectAll = (checked) => {
      bills.value.forEach(bill => {
        bill.selected = checked
      })
      updateSelection()
    }
    
    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedBills.value = selection
    }
    
    // 更新选择状态
    const updateSelection = () => {
      selectedBills.value = bills.value.filter(bill => bill.selected)
      selectAll.value = bills.value.length > 0 && bills.value.every(bill => bill.selected)
    }
    
    // 处理下拉菜单命令
    const handleCommand = (command, bill) => {
      switch (command) {
        case 'edit':
          ElMessage.info('编辑功能开发中')
          break
        case 'payment':
          recordPayment()
          break
        case 'export':
          ElMessage.success('账单导出成功')
          break
        case 'delete':
          ElMessageBox.confirm('确定要删除此账单吗？', '确认删除', {
            type: 'warning'
          }).then(() => {
            ElMessage.success('账单已删除')
            loadBills()
          })
          break
      }
    }
    
    // 工具函数
    const getTypeName = (type) => {
      const typeMap = {
        property: '物业费',
        parking: '停车费'
      }
      return typeMap[type] || type
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        property: 'primary',
        parking: 'success'
      }
      return typeMap[type] || ''
    }
    
    const getStatusName = (status) => {
      const statusMap = {
        pending: '待缴费',
        paid: '已缴费',
        overdue: '已逾期',
        partial: '部分缴费'
      }
      return statusMap[status] || status
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        pending: 'warning',
        paid: 'success',
        overdue: 'danger',
        partial: 'info'
      }
      return statusMap[status] || ''
    }
    
    const formatMoney = (amount) => {
      return amount.toLocaleString('zh-CN')
    }
    
    const formatDate = (dateStr) => {
      return new Date(dateStr).toLocaleDateString('zh-CN')
    }
    
    const isOverdue = (dueDate) => {
      return new Date(dueDate) < new Date()
    }
    
    // 加载统计数据
    const loadStats = async () => {
      try {
        const response = await getBillStats()
        if (response && response.data) {
          const data = response.data
          stats.totalRevenue = data.paidAmount || 0
          stats.unpaidAmount = (data.totalAmount || 0) - (data.paidAmount || 0)
          stats.collectionRate = data.paymentRate || 0
          stats.overdueCount = data.totalBills - data.paidBills || 0
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    }
    
    onMounted(async () => {
      await loadStats()
      await loadBills()
    })
    
    return {
      loading,
      generating,
      showDetailDialog,
      showBatchBillDialog,
      selectedBill,
      selectAll,
      selectedBills,
      viewMode,
      stats,
      filters,
      pagination,
      batchBillForm,
      bills,
      loadBills,
      resetFilters,
      viewBill,
      generateBatchBills,
      sendReminder,
      recordPayment,
      exportReport,
      batchRemind,
      batchExport,
      handleSelectAll,
      handleSelectionChange,
      updateSelection,
      handleCommand,
      getTypeName,
      getTypeTagType,
      getStatusName,
      getStatusTagType,
      formatMoney,
      formatDate,
      isOverdue
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.finance-container {
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
  
  .header-right {
    display: flex;
    gap: 12px;
  }
  
  @include mobile {
    flex-direction: column;
    gap: 16px;
    
    .header-right {
      width: 100%;
      
      .el-button {
        flex: 1;
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
      margin-bottom: 16px;
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
        margin-bottom: 8px;
      }
      
      .stat-trend {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        font-weight: 600;
        
        &.up {
          color: $success-color;
        }
        
        &.down {
          color: $error-color;
        }
      }
    }
    
    &.revenue .stat-icon {
      background: linear-gradient(135deg, #3ba55c, #27ae60);
    }
    
    &.unpaid .stat-icon {
      background: linear-gradient(135deg, #ed4245, #e74c3c);
    }
    
    &.collection-rate .stat-icon {
      background: linear-gradient(135deg, #5865f2, #4752c4);
    }
    
    &.overdue .stat-icon {
      background: linear-gradient(135deg, #faa61a, #f0932b);
    }
  }
}

.filter-section {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  padding: 24px;
  margin-bottom: 24px;
  
  .filter-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
      margin-right: 20px;
    }
    
    @include mobile {
      .el-form-item {
        width: 100%;
        margin-right: 0;
        margin-bottom: 16px;
        
        :deep(.el-form-item__content) {
          width: 100%;
          
          .el-select,
          .el-input,
          .el-date-editor {
            width: 100% !important;
          }
        }
      }
    }
  }
}

.bill-list {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  padding: 24px;
  
  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .bulk-actions {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .el-checkbox {
        margin-right: 4px;
      }
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
  
  .bill-table {
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
    
    .bill-no {
      font-family: 'Monaco', 'Menlo', monospace;
      font-weight: 600;
      color: $primary-color;
    }
    
    .property-info {
      .property-address {
        font-weight: 600;
        color: $text-primary;
      }
    }
    
    .owner-info {
      display: flex;
      align-items: center;
      gap: 8px;
      
      span {
        font-size: 14px;
      }
    }
    
    .amount {
      font-weight: 600;
      color: $text-primary;
    }
    
    .paid-amount {
      font-weight: 600;
      color: $success-color;
    }
    
    .overdue-date {
      color: $error-color;
      font-weight: 600;
    }
  }
  
  .bill-cards {
    .bill-card {
      background: $bg-secondary;
      border: 1px solid $border-color;
      border-radius: $border-radius-lg;
      padding: 20px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px $shadow-medium;
      }
      
      &.overdue {
        border-color: $error-color;
        background: linear-gradient(135deg, $bg-secondary 0%, rgba(237, 66, 69, 0.1) 100%);
      }
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .bill-info {
          .bill-no {
            font-family: 'Monaco', 'Menlo', monospace;
            font-weight: 600;
            color: $primary-color;
            margin-bottom: 8px;
          }
        }
      }
      
      .card-content {
        .property-section {
          margin-bottom: 16px;
          
          .property-address {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            color: $text-primary;
            margin-bottom: 8px;
          }
          
          .owner-name {
            color: $text-secondary;
            font-size: 14px;
          }
        }
        
        .bill-details {
          .detail-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            font-size: 14px;
            
            .label {
              color: $text-secondary;
            }
            
            .amount {
              font-weight: 600;
              color: $text-primary;
            }
            
            .overdue-date {
              color: $error-color;
              font-weight: 600;
            }
          }
        }
      }
      
      .card-actions {
        display: flex;
        gap: 8px;
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid $border-color;
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

.bill-dialog {
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
  
  .bill-detail {
    .payment-history {
      margin-top: 24px;
      
      h4 {
        margin: 0 0 16px 0;
        color: $text-primary;
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
    
    .el-timeline-item__content {
      color: $text-primary;
    }
  }
}
</style>
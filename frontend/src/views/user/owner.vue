<template>
  <div class="owner-container responsive-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">业主管理</h1>
        <p class="page-subtitle">管理小区业主信息和房产绑定</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          添加业主
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards responsive-grid grid-4">
      <div class="stat-card responsive-card total">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总业主数</div>
        </div>
      </div>
      <div class="stat-card responsive-card verified">
        <div class="stat-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.verified }}</div>
          <div class="stat-label">已认证</div>
        </div>
      </div>
      <div class="stat-card responsive-card pending">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card responsive-card occupied">
        <div class="stat-icon">
          <el-icon><House /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.occupied }}</div>
          <div class="stat-label">已入住</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section responsive-card">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item label="认证状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="已认证" value="verified" />
            <el-option label="待审核" value="pending" />
            <el-option label="未认证" value="unverified" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住状态">
          <el-select v-model="filters.occupancy" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="已入住" value="occupied" />
            <el-option label="未入住" value="vacant" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="filters.building" placeholder="全部楼栋" clearable style="width: 100px">
            <el-option label="A栋" value="A" />
            <el-option label="B栋" value="B" />
            <el-option label="C栋" value="C" />
            <el-option label="D栋" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索业主姓名、手机号"
            prefix-icon="Search"
            style="width: 200px"
            @keyup.enter="loadOwners"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOwners">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 业主列表 -->
    <div class="owner-list responsive-card">
      <el-table
        :data="owners"
        v-loading="loading"
        stripe
        @row-click="viewOwner"
        class="owner-table"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="业主信息" min-width="200">
          <template #default="{ row }">
            <div class="owner-info">
              <el-avatar :size="40" :src="row.avatar">{{ row.name.charAt(0) }}</el-avatar>
              <div class="info-content">
                <div class="name">{{ row.name }}</div>
                <div class="phone">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="properties" label="房产信息" min-width="180">
          <template #default="{ row }">
            <div class="properties">
              <el-tag
                v-for="property in row.properties"
                :key="property.id"
                size="small"
                :type="property.occupied ? 'success' : ''"
                class="property-tag"
              >
                {{ property.building }}栋{{ property.unit }}单元{{ property.room }}室
                <el-icon v-if="property.occupied"><House /></el-icon>
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="认证状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="registeredAt" label="注册时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.registeredAt) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="lastLogin" label="最后登录" width="150">
          <template #default="{ row }">
            <span v-if="row.lastLogin">{{ formatDate(row.lastLogin) }}</span>
            <span v-else class="text-muted">从未登录</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click.stop="viewOwner(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="success" 
              size="small" 
              @click.stop="approveOwner(row)"
            >
              审核
            </el-button>
            <el-dropdown @command="(command) => handleCommand(command, row)" trigger="click">
              <el-button size="small" type="text">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="property">房产管理</el-dropdown-item>
                  <el-dropdown-item command="reset">重置密码</el-dropdown-item>
                  <el-dropdown-item command="disable" v-if="row.status === 'verified'">禁用</el-dropdown-item>
                  <el-dropdown-item command="enable" v-if="row.status === 'disabled'">启用</el-dropdown-item>
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
          @size-change="loadOwners"
          @current-change="loadOwners"
        />
      </div>
    </div>

    <!-- 业主详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="`业主详情 - ${selectedOwner?.name}`"
      width="800px"
      class="owner-dialog"
    >
      <div v-if="selectedOwner" class="owner-detail">
        <div class="detail-header">
          <div class="owner-basic">
            <el-avatar :size="80" :src="selectedOwner.avatar">{{ selectedOwner.name.charAt(0) }}</el-avatar>
            <div class="basic-info">
              <h3>{{ selectedOwner.name }}</h3>
              <p>{{ selectedOwner.phone }}</p>
              <div class="status-tags">
                <el-tag :type="getStatusTagType(selectedOwner.status)">{{ getStatusName(selectedOwner.status) }}</el-tag>
                <el-tag v-if="selectedOwner.vip" type="warning">VIP业主</el-tag>
              </div>
            </div>
          </div>
          <div class="detail-actions">
            <el-button v-if="selectedOwner.status === 'pending'" type="success" @click="approveOwner(selectedOwner)">
              审核通过
            </el-button>
            <el-button type="primary" @click="editOwner(selectedOwner)">编辑信息</el-button>
          </div>
        </div>
        
        <el-divider />
        
        <div class="detail-content">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="姓名">{{ selectedOwner.name }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ selectedOwner.phone }}</el-descriptions-item>
                <el-descriptions-item label="身份证号">{{ selectedOwner.idCard || '未填写' }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ selectedOwner.email || '未填写' }}</el-descriptions-item>
                <el-descriptions-item label="认证状态">
                  <el-tag :type="getStatusTagType(selectedOwner.status)">{{ getStatusName(selectedOwner.status) }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">{{ formatDate(selectedOwner.registeredAt) }}</el-descriptions-item>
                <el-descriptions-item label="最后登录">{{ selectedOwner.lastLogin ? formatDate(selectedOwner.lastLogin) : '从未登录' }}</el-descriptions-item>
                <el-descriptions-item label="登录次数">{{ selectedOwner.loginCount || 0 }}次</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            
            <el-tab-pane label="房产信息" name="property">
              <div class="property-list">
                <div v-for="property in selectedOwner.properties" :key="property.id" class="property-item">
                  <div class="property-info">
                    <h4>{{ property.building }}栋{{ property.unit }}单元{{ property.room }}室</h4>
                    <div class="property-details">
                      <span>面积：{{ property.area }}㎡</span>
                      <span>类型：{{ property.type }}</span>
                      <span>状态：{{ property.occupied ? '已入住' : '空置' }}</span>
                    </div>
                  </div>
                  <div class="property-actions">
                    <el-button size="small" type="text">编辑</el-button>
                    <el-button size="small" type="text" @click="unbindProperty(property)">解绑</el-button>
                  </div>
                </div>
                <el-button type="primary" @click="bindProperty">绑定新房产</el-button>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="缴费记录" name="payment">
              <el-table :data="selectedOwner.payments" stripe>
                <el-table-column prop="type" label="费用类型" width="120" />
                <el-table-column prop="amount" label="金额" width="100">
                  <template #default="{ row }">
                    ¥{{ row.amount }}
                  </template>
                </el-table-column>
                <el-table-column prop="period" label="缴费周期" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'paid' ? 'success' : 'warning'" size="small">
                      {{ row.status === 'paid' ? '已缴费' : '待缴费' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="paidAt" label="缴费时间" width="150">
                  <template #default="{ row }">
                    {{ row.paidAt ? formatDate(row.paidAt) : '-' }}
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="工单记录" name="orders">
              <el-table :data="selectedOwner.workOrders" stripe>
                <el-table-column prop="id" label="工单号" width="120" />
                <el-table-column prop="type" label="类型" width="100" />
                <el-table-column prop="title" label="标题" min-width="200" />
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
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>

    <!-- 添加业主对话框 -->
    <el-dialog v-model="showAddDialog" title="添加业主" width="600px">
      <el-form :model="newOwner" :rules="ownerRules" ref="ownerForm" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="newOwner.name" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="newOwner.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="newOwner.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="newOwner.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="房产信息">
          <div class="property-selector">
            <el-select v-model="newOwner.building" placeholder="楼栋" style="width: 80px">
              <el-option label="A栋" value="A" />
              <el-option label="B栋" value="B" />
              <el-option label="C栋" value="C" />
              <el-option label="D栋" value="D" />
            </el-select>
            <el-select v-model="newOwner.unit" placeholder="单元" style="width: 80px">
              <el-option v-for="i in 6" :key="i" :label="`${i}单元`" :value="i" />
            </el-select>
            <el-select v-model="newOwner.room" placeholder="房间" style="width: 100px">
              <el-option v-for="i in 20" :key="i" :label="`${i}0${i % 2 + 1}`" :value="`${i}0${i % 2 + 1}`" />
            </el-select>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addOwner" :loading="adding">添加</el-button>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="showApprovalDialog" title="业主审核" width="500px">
      <div v-if="approvalOwner" class="approval-content">
        <div class="owner-info">
          <el-avatar :size="60" :src="approvalOwner.avatar">{{ approvalOwner.name.charAt(0) }}</el-avatar>
          <div class="info">
            <h4>{{ approvalOwner.name }}</h4>
            <p>{{ approvalOwner.phone }}</p>
          </div>
        </div>
        <el-form :model="approvalForm" label-width="80px">
          <el-form-item label="审核结果">
            <el-radio-group v-model="approvalForm.result">
              <el-radio label="approved">通过</el-radio>
              <el-radio label="rejected">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input v-model="approvalForm.comment" type="textarea" :rows="3" placeholder="请输入审核意见" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showApprovalDialog = false">取消</el-button>
        <el-button type="primary" @click="submitApproval" :loading="approving">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, User, Check, Clock, House, Search, ArrowDown } from '@element-plus/icons-vue'
import { getOwnerList, createOwner, updateOwner, deleteOwner, getUserRooms, resetOwnerPassword } from '@/api/owner'

export default {
  name: 'OwnerManagement',
  components: {
    Plus, User, Check, Clock, House, Search, ArrowDown
  },
  setup() {
    const loading = ref(false)
    const adding = ref(false)
    const approving = ref(false)
    const showDetailDialog = ref(false)
    const showAddDialog = ref(false)
    const showApprovalDialog = ref(false)
    const selectedOwner = ref(null)
    const approvalOwner = ref(null)
    const activeTab = ref('basic')
    const ownerForm = ref(null)
    
    // 统计数据
    const stats = reactive({
      total: 0,
      verified: 0,
      pending: 0,
      occupied: 0
    })
    
    // 筛选条件
    const filters = reactive({
      status: '',
      occupancy: '',
      building: '',
      keyword: ''
    })
    
    // 分页
    const pagination = reactive({
      current: 1,
      size: 20,
      total: 0
    })
    
    // 业主列表
    const owners = ref([])
    
    // 新建业主表单
    const newOwner = reactive({
      name: '',
      username: '',
      realName: '',
      phone: '',
      password: '123456',
      email: '',
      idCard: '',
      building: '',
      unit: '',
      room: '',
      userType: 'OWNER',
      status: 1
    })
    
    // 审核表单
    const approvalForm = reactive({
      result: 'approved',
      comment: ''
    })
    
    // 表单验证规则
    const ownerRules = {
      name: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ]
    }
    
    // 模拟数据
    const mockOwners = [
      {
        id: 1,
        name: '张三',
        phone: '13800138001',
        idCard: '110101199001011234',
        email: 'zhangsan@example.com',
        avatar: '',
        status: 'verified',
        registeredAt: '2023-10-15 09:30:00',
        lastLogin: '2023-12-01 15:20:00',
        loginCount: 45,
        vip: true,
        properties: [
          { id: 1, building: 'A', unit: 1, room: '101', area: 85, type: '两室一厅', occupied: true }
        ],
        payments: [
          { type: '物业费', amount: 280, period: '2023-12', status: 'paid', paidAt: '2023-12-01 10:30:00' },
          { type: '停车费', amount: 200, period: '2023-12', status: 'paid', paidAt: '2023-12-01 10:30:00' }
        ],
        workOrders: [
          { id: 'WO202312001', type: '设备维修', title: '水龙头漏水', status: 'completed', createdAt: '2023-11-28 14:20:00' }
        ]
      },
      {
        id: 2,
        name: '李四',
        phone: '13800138002',
        idCard: '110101199002021234',
        email: '',
        avatar: '',
        status: 'pending',
        registeredAt: '2023-12-01 16:45:00',
        lastLogin: null,
        loginCount: 0,
        vip: false,
        properties: [
          { id: 2, building: 'B', unit: 2, room: '205', area: 95, type: '三室一厅', occupied: false }
        ],
        payments: [],
        workOrders: []
      },
      {
        id: 3,
        name: '王五',
        phone: '13800138003',
        idCard: '110101199003031234',
        email: 'wangwu@example.com',
        avatar: '',
        status: 'verified',
        registeredAt: '2023-09-20 11:15:00',
        lastLogin: '2023-11-30 20:10:00',
        loginCount: 23,
        vip: false,
        properties: [
          { id: 3, building: 'A', unit: 1, room: '102', area: 85, type: '两室一厅', occupied: true },
          { id: 4, building: 'C', unit: 3, room: '301', area: 120, type: '三室两厅', occupied: false }
        ],
        payments: [
          { type: '物业费', amount: 280, period: '2023-12', status: 'pending', paidAt: null }
        ],
        workOrders: []
      }
    ]
    
    // 加载业主列表
    const loadOwners = async () => {
      loading.value = true
      try {
        const params = {
          pageNum: pagination.page,
          pageSize: pagination.pageSize,
          keyword: filters.keyword
        }
        
        console.log('请求业主列表参数:', params)
        
        const response = await getOwnerList(params)
        console.log('API响应:', response)
        
        if (response && response.data) {
          const result = response.data
          const list = result.list || result.records || []
          
          // 映射后端数据到前端格式
          owners.value = list.map(user => ({
            id: user.id,
            name: user.realName || user.username,
            phone: user.phone,
            idCard: user.idCard || '',
            email: user.email || '',
            avatar: user.avatar || '',
            status: getOwnerStatus(user.status),
            registeredAt: formatDateTime(user.createTime),
            lastLogin: formatDateTime(user.lastLoginTime) || '从未登录',
            loginCount: user.loginCount || 0,
            vip: false,
            properties: [], // 需要额外加载
            payments: [], // 需要额外加载
            workOrders: [], // 需要额外加载
            userType: user.userType,
            gender: user.gender,
            birthday: user.birthday
          }))
          
          pagination.total = result.total || 0
          
          // 更新统计数据
          updateStats()
        }
      } catch (error) {
        console.error('加载业主列表失败:', error)
        ElMessage.error('加载业主列表失败: ' + (error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    // 根据status获取业主状态
    const getOwnerStatus = (status) => {
      if (status === 1) return 'verified'
      return 'unverified'
    }
    
    // 格式化日期时间
    const formatDateTime = (dateTime) => {
      if (!dateTime) return ''
      if (typeof dateTime === 'string') return dateTime
      
      // 处理LocalDateTime对象
      if (dateTime.year) {
        return `${dateTime.year}-${String(dateTime.monthValue).padStart(2, '0')}-${String(dateTime.dayOfMonth).padStart(2, '0')} ${String(dateTime.hour).padStart(2, '0')}:${String(dateTime.minute).padStart(2, '0')}:${String(dateTime.second).padStart(2, '0')}`
      }
      
      return String(dateTime)
    }
    
    // 更新统计数据
    const updateStats = () => {
      stats.total = owners.value.length
      stats.verified = owners.value.filter(o => o.status === 'verified').length
      stats.pending = owners.value.filter(o => o.status === 'pending').length
      stats.occupied = owners.value.filter(o => o.properties && o.properties.length > 0).length
    }
    
    // 重置筛选条件
    const resetFilters = () => {
      Object.keys(filters).forEach(key => {
        filters[key] = ''
      })
      loadOwners()
    }
    
    // 查看业主详情
    const viewOwner = (owner) => {
      selectedOwner.value = owner
      showDetailDialog.value = true
      activeTab.value = 'basic'
    }
    
    // 添加业主
    const addOwner = async () => {
      if (!ownerForm.value) return
      
      const valid = await ownerForm.value.validate().catch(() => false)
      if (!valid) return
      
      adding.value = true
      try {
        const data = {
          username: newOwner.phone, // 使用手机号作为用户名
          realName: newOwner.name,
          phone: newOwner.phone,
          email: newOwner.email,
          idCard: newOwner.idCard,
          password: '123456', // 默认密码
          userType: 'OWNER'
        }
        
        const response = await createOwner(data)
        console.log('添加业主响应:', response)
        
        ElMessage.success('业主添加成功')
        showAddDialog.value = false
        
        // 重置表单
        ownerForm.value.resetFields()
        Object.keys(newOwner).forEach(key => {
          if (key === 'password') {
            newOwner[key] = '123456'
          } else if (key === 'userType') {
            newOwner[key] = 'OWNER'
          } else if (key === 'status') {
            newOwner[key] = 1
          } else {
            newOwner[key] = ''
          }
        })
        
        loadOwners()
      } catch (error) {
        console.error('添加业主失败:', error)
        ElMessage.error(error.message || error.response?.data?.message || '添加业主失败')
      } finally {
        adding.value = false
      }
    }
    
    // 审核业主
    const approveOwner = (owner) => {
      approvalOwner.value = owner
      showApprovalDialog.value = true
    }
    
    // 提交审核
    const submitApproval = async () => {
      approving.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('审核提交成功')
        showApprovalDialog.value = false
        loadOwners()
      } catch (error) {
        ElMessage.error('审核提交失败')
      } finally {
        approving.value = false
      }
    }
    
    // 编辑业主
    const editOwner = (owner) => {
      ElMessage.info('编辑功能开发中')
    }
    
    // 绑定房产
    const bindProperty = () => {
      ElMessage.info('绑定房产功能开发中')
    }
    
    // 解绑房产
    const unbindProperty = (property) => {
      ElMessageBox.confirm('确定要解绑此房产吗？', '确认操作', {
        type: 'warning'
      }).then(() => {
        ElMessage.success('房产解绑成功')
      })
    }
    
    // 处理下拉菜单命令
    const handleCommand = async (command, owner) => {
      try {
        switch (command) {
        case 'edit':
          editOwner(owner)
          break
        case 'property':
          viewOwner(owner)
          activeTab.value = 'property'
          break
        case 'reset':
          await ElMessageBox.confirm('确定要重置此业主的密码吗？', '确认操作', {
            type: 'warning'
          })
          await resetOwnerPassword(owner.id, '123456')
          ElMessage.success('密码重置成功，新密码为：123456')
          break
        case 'disable':
          await ElMessageBox.confirm('确定要禁用此业主吗？', '确认操作', {
            type: 'warning'
          })
          await updateOwner(owner.id, { status: 0 })
          ElMessage.success('业主已禁用')
          loadOwners()
          break
        case 'enable':
          await ElMessageBox.confirm('确定要启用此业主吗？', '确认操作', {
            type: 'warning'
          })
          await updateOwner(owner.id, { status: 1 })
          ElMessage.success('业主已启用')
          loadOwners()
          break
        case 'delete':
          await ElMessageBox.confirm('确定要删除此业主吗？', '确认删除', {
            type: 'warning'
          })
          await deleteOwner(owner.id)
          ElMessage.success('业主已删除')
          loadOwners()
          break
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('操作失败:', error)
        ElMessage.error(error.message || '操作失败')
      }
    }
  }
    
    // 工具函数
    const getStatusName = (status) => {
      const statusMap = {
        verified: '已认证',
        pending: '待审核',
        unverified: '未认证',
        disabled: '已禁用'
      }
      return statusMap[status] || status
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        verified: 'success',
        pending: 'warning',
        unverified: 'info',
        disabled: 'danger'
      }
      return statusMap[status] || ''
    }
    
    const formatDate = (dateStr) => {
      return new Date(dateStr).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      loadOwners()
    })
    
    return {
      loading,
      adding,
      approving,
      showDetailDialog,
      showAddDialog,
      showApprovalDialog,
      selectedOwner,
      approvalOwner,
      activeTab,
      stats,
      filters,
      pagination,
      owners,
      newOwner,
      approvalForm,
      ownerRules,
      loadOwners,
      formatDateTime,
      getOwnerStatus,
      updateStats,
      resetFilters,
      viewOwner,
      addOwner,
      approveOwner,
      submitApproval,
      editOwner,
      bindProperty,
      unbindProperty,
      handleCommand,
      getStatusName,
      getStatusTagType,
      formatDate,
      ownerForm
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.owner-container {
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
    
    &.total .stat-icon {
      background: linear-gradient(135deg, #5865f2, #4752c4);
    }
    
    &.verified .stat-icon {
      background: linear-gradient(135deg, #3ba55c, #27ae60);
    }
    
    &.pending .stat-icon {
      background: linear-gradient(135deg, #faa61a, #f0932b);
    }
    
    &.occupied .stat-icon {
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

.owner-list {
  background: $bg-tertiary;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  padding: 20px;
  
  .owner-table {
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
    
    .owner-info {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .info-content {
        .name {
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 4px;
        }
        
        .phone {
          font-size: 12px;
          color: $text-secondary;
        }
      }
    }
    
    .properties {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
      
      .property-tag {
        display: flex;
        align-items: center;
        gap: 4px;
        
        .el-icon {
          font-size: 12px;
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

.owner-dialog {
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
  
  .owner-detail {
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;
      
      .owner-basic {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .basic-info {
          h3 {
            margin: 0 0 8px 0;
            color: $text-primary;
          }
          
          p {
            margin: 0 0 12px 0;
            color: $text-secondary;
          }
          
          .status-tags {
            display: flex;
            gap: 8px;
          }
        }
      }
      
      @include mobile {
        flex-direction: column;
        gap: 16px;
        
        .detail-actions {
          width: 100%;
          
          .el-button {
            width: 100%;
            margin-bottom: 8px;
          }
        }
      }
    }
    
    .detail-content {
      .property-list {
        .property-item {
          background: $bg-secondary;
          border: 1px solid $border-color;
          border-radius: $border-radius;
          padding: 16px;
          margin-bottom: 12px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .property-info {
            h4 {
              margin: 0 0 8px 0;
              color: $text-primary;
            }
            
            .property-details {
              display: flex;
              gap: 16px;
              font-size: 14px;
              color: $text-secondary;
            }
          }
          
          .property-actions {
            display: flex;
            gap: 8px;
          }
        }
      }
    }
  }
}

.approval-content {
  .owner-info {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;
    padding: 16px;
    background: $bg-secondary;
    border-radius: $border-radius;
    
    .info {
      h4 {
        margin: 0 0 4px 0;
        color: $text-primary;
      }
      
      p {
        margin: 0;
        color: $text-secondary;
      }
    }
  }
}

.property-selector {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  
  @include mobile {
    .el-select {
      flex: 1;
      min-width: 80px;
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

:deep(.el-tabs) {
  .el-tabs__header {
    .el-tabs__nav {
      background: transparent;
      
      .el-tabs__item {
        color: $text-secondary;
        
        &.is-active {
          color: $primary-color;
        }
      }
    }
    
    .el-tabs__active-bar {
      background: $primary-color;
    }
  }
  
  .el-tabs__content {
    color: $text-primary;
  }
}
</style>
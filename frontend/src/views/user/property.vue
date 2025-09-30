<template>
  <div class="property-container">
    <div class="page-header">
      <h1 class="page-title">房产管理</h1>
      <p class="page-subtitle">管理楼栋、单元、房屋信息和业主绑定关系</p>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="楼栋">
          <el-select v-model="queryParams.building" placeholder="请选择楼栋" clearable style="width: 120px">
            <el-option v-for="building in buildingOptions" :key="building" :label="`${building}栋`" :value="building" />
          </el-select>
        </el-form-item>
        <el-form-item label="单元">
          <el-select v-model="queryParams.unit" placeholder="请选择单元" clearable style="width: 120px">
            <el-option v-for="unit in unitOptions" :key="unit" :label="`${unit}单元`" :value="unit" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input
            v-model="queryParams.room"
            placeholder="请输入房间号"
            clearable
            style="width: 120px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="房屋类型">
          <el-select v-model="queryParams.type" placeholder="请选择类型" clearable style="width: 140px">
            <el-option label="一室一厅" value="1室1厅" />
            <el-option label="两室一厅" value="2室1厅" />
            <el-option label="两室两厅" value="2室2厅" />
            <el-option label="三室一厅" value="3室1厅" />
            <el-option label="三室两厅" value="3室2厅" />
            <el-option label="四室两厅" value="4室2厅" />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定状态">
          <el-select v-model="queryParams.bindStatus" placeholder="绑定状态" clearable style="width: 120px">
            <el-option label="已绑定" value="bound" />
            <el-option label="未绑定" value="unbound" />
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
          新增房产
        </el-button>
        <el-button type="success" @click="handleBatchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
        <el-button type="warning" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button @click="handleTemplate">
          <el-icon><Document /></el-icon>
          下载模板
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="propertyList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="房屋信息" width="200">
          <template #default="scope">
            <div class="property-info">
              <div class="property-address">
                <strong>{{ scope.row.building }}栋{{ scope.row.unit }}单元{{ scope.row.room }}室</strong>
              </div>
              <div class="property-details">
                {{ scope.row.area }}㎡ · {{ scope.row.type }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column label="朝向" width="80">
          <template #default="scope">
            <el-tag size="small" type="info">{{ scope.row.orientation }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="装修状态" width="100">
          <template #default="scope">
            <el-tag :type="getDecorationTagType(scope.row.decoration)" size="small">
              {{ getDecorationText(scope.row.decoration) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绑定业主" width="200">
          <template #default="scope">
            <div v-if="scope.row.owners.length > 0">
              <div
                v-for="owner in scope.row.owners"
                :key="owner.id"
                class="owner-item"
              >
                <el-avatar :src="owner.avatar" :size="24">
                  {{ owner.name.charAt(0) }}
                </el-avatar>
                <span class="owner-name">{{ owner.name }}</span>
                <el-tag :type="getOwnerTypeTag(owner.type)" size="small">
                  {{ getOwnerTypeText(owner.type) }}
                </el-tag>
              </div>
            </div>
            <span v-else class="text-muted">未绑定业主</span>
          </template>
        </el-table-column>
        <el-table-column label="物业费" width="120">
          <template #default="scope">
            <div class="fee-info">
              <div>{{ scope.row.propertyFee }}/月</div>
              <div class="fee-status" :class="scope.row.feeStatus">
                {{ getFeeStatusText(scope.row.feeStatus) }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
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
              type="info"
              size="small"
              @click="handleBind(scope.row)"
            >
              绑定
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

    <!-- 房产详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="房产详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentProperty" class="property-detail">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="房屋地址" :span="2">
            {{ currentProperty.building }}栋{{ currentProperty.unit }}单元{{ currentProperty.room }}室
          </el-descriptions-item>
          <el-descriptions-item label="楼层">{{ currentProperty.floor }}层</el-descriptions-item>
          <el-descriptions-item label="建筑面积">{{ currentProperty.area }}㎡</el-descriptions-item>
          <el-descriptions-item label="房屋类型">{{ currentProperty.type }}</el-descriptions-item>
          <el-descriptions-item label="朝向">{{ currentProperty.orientation }}</el-descriptions-item>
          <el-descriptions-item label="装修状态">
            <el-tag :type="getDecorationTagType(currentProperty.decoration)">
              {{ getDecorationText(currentProperty.decoration) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="物业费">{{ currentProperty.propertyFee }}/月</el-descriptions-item>
          <el-descriptions-item label="缴费状态">
            <el-tag :type="getFeeStatusTagType(currentProperty.feeStatus)">
              {{ getFeeStatusText(currentProperty.feeStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="3">{{ currentProperty.createTime }}</el-descriptions-item>
        </el-descriptions>

        <div class="owner-section">
          <h3>绑定业主</h3>
          <div v-if="currentProperty.owners.length > 0" class="owner-list">
            <el-card
              v-for="owner in currentProperty.owners"
              :key="owner.id"
              class="owner-card"
            >
              <div class="owner-info">
                <el-avatar :src="owner.avatar" :size="40">
                  {{ owner.name.charAt(0) }}
                </el-avatar>
                <div class="owner-details">
                  <h4>{{ owner.name }}</h4>
                  <p>{{ owner.phone }}</p>
                  <el-tag :type="getOwnerTypeTag(owner.type)" size="small">
                    {{ getOwnerTypeText(owner.type) }}
                  </el-tag>
                </div>
                <div class="owner-actions">
                  <el-button size="small" @click="handleUnbind(owner)">解绑</el-button>
                </div>
              </div>
            </el-card>
          </div>
          <div v-else class="no-owner">
            <el-empty description="暂无绑定业主" />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="handleBind(currentProperty)">绑定业主</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑房产弹窗 -->
    <el-dialog
      v-model="showFormDialog"
      :title="formTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="楼栋" prop="building">
              <el-input v-model="form.building" placeholder="如：A" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单元" prop="unit">
              <el-input v-model="form.unit" placeholder="如：1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间号" prop="room">
              <el-input v-model="form.room" placeholder="如：101" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="form.floor" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="area">
              <el-input-number v-model="form.area" :min="1" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房屋类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择房屋类型" style="width: 100%">
                <el-option label="一室一厅" value="1室1厅" />
                <el-option label="两室一厅" value="2室1厅" />
                <el-option label="两室两厅" value="2室2厅" />
                <el-option label="三室一厅" value="3室1厅" />
                <el-option label="三室两厅" value="3室2厅" />
                <el-option label="四室两厅" value="4室2厅" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="朝向" prop="orientation">
              <el-select v-model="form.orientation" placeholder="请选择朝向" style="width: 100%">
                <el-option label="南" value="南" />
                <el-option label="北" value="北" />
                <el-option label="东" value="东" />
                <el-option label="西" value="西" />
                <el-option label="南北" value="南北" />
                <el-option label="东西" value="东西" />
                <el-option label="东南" value="东南" />
                <el-option label="西南" value="西南" />
                <el-option label="东北" value="东北" />
                <el-option label="西北" value="西北" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="装修状态" prop="decoration">
              <el-select v-model="form.decoration" placeholder="请选择装修状态" style="width: 100%">
                <el-option label="毛坯" value="rough" />
                <el-option label="简装" value="simple" />
                <el-option label="精装" value="fine" />
                <el-option label="豪装" value="luxury" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物业费" prop="propertyFee">
              <el-input v-model="form.propertyFee" placeholder="如：1200">
                <template #append>元/月</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定业主弹窗 -->
    <el-dialog
      v-model="showBindDialog"
      title="绑定业主"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="bindForm" label-width="100px">
        <el-form-item label="选择业主">
          <el-select
            v-model="bindForm.ownerId"
            placeholder="请选择要绑定的业主"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="owner in availableOwners"
              :key="owner.id"
              :label="`${owner.name} (${owner.phone})`"
              :value="owner.id"
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Upload, Delete, Download, Document } from '@element-plus/icons-vue'
import { getPropertyList, getPropertyDetail, createProperty, updateProperty, deleteProperty, batchDeleteProperty, bindOwner, unbindOwner } from '@/api/property'
import { getOwnerList } from '@/api/owner'

export default {
  name: 'Property',
  components: {
    Search,
    Refresh,
    Plus,
    Upload,
    Delete,
    Download,
    Document
  },
  setup() {
    const loading = ref(false)
    const propertyList = ref([])
    const total = ref(0)
    const multipleSelection = ref([])
    const showDetailDialog = ref(false)
    const showFormDialog = ref(false)
    const showBindDialog = ref(false)
    const currentProperty = ref(null)
    const formRef = ref(null)
    const availableOwners = ref([])
    
    const queryParams = reactive({
      pageNum: 1,
      pageSize: 20,
      building: '',
      unit: '',
      room: '',
      type: '',
      bindStatus: ''
    })
    
    const form = reactive({
      id: null,
      building: '',
      unit: '',
      room: '',
      floor: 1,
      area: null,
      type: '',
      orientation: '',
      decoration: '',
      propertyFee: '',
      remark: ''
    })
    
    const bindForm = reactive({
      propertyId: null,
      ownerId: null,
      bindType: 'owner',
      remark: ''
    })
    
    const formRules = {
      building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }],
      unit: [{ required: true, message: '请输入单元', trigger: 'blur' }],
      room: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
      floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
      area: [{ required: true, message: '请输入建筑面积', trigger: 'blur' }],
      type: [{ required: true, message: '请选择房屋类型', trigger: 'change' }],
      orientation: [{ required: true, message: '请选择朝向', trigger: 'change' }],
      decoration: [{ required: true, message: '请选择装修状态', trigger: 'change' }],
      propertyFee: [{ required: true, message: '请输入物业费', trigger: 'blur' }]
    }
    
    const buildingOptions = ['A', 'B', 'C', 'D', 'E']
    const unitOptions = ['1', '2', '3', '4']
    
    const formTitle = computed(() => {
      return form.id ? '编辑房产' : '新增房产'
    })
    
    // 模拟数据
    const mockProperties = [
      {
        id: 1,
        building: 'A',
        unit: '1',
        room: '101',
        floor: 1,
        area: 120.5,
        type: '3室2厅',
        orientation: '南',
        decoration: 'fine',
        propertyFee: '1200',
        feeStatus: 'paid',
        createTime: '2024-01-15 10:00:00',
        owners: [
          {
            id: 1,
            name: '张三',
            phone: '13800138001',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
            type: 'owner'
          }
        ]
      },
      {
        id: 2,
        building: 'A',
        unit: '1',
        room: '102',
        floor: 1,
        area: 95.0,
        type: '2室1厅',
        orientation: '北',
        decoration: 'simple',
        propertyFee: '950',
        feeStatus: 'unpaid',
        createTime: '2024-01-15 10:01:00',
        owners: []
      },
      {
        id: 3,
        building: 'B',
        unit: '2',
        room: '201',
        floor: 2,
        area: 110.0,
        type: '3室1厅',
        orientation: '南北',
        decoration: 'luxury',
        propertyFee: '1500',
        feeStatus: 'overdue',
        createTime: '2024-01-15 10:02:00',
        owners: [
          {
            id: 2,
            name: '李四',
            phone: '13800138002',
            avatar: '',
            type: 'tenant'
          }
        ]
      }
    ]
    
    const mockOwners = [
      { id: 1, name: '张三', phone: '13800138001' },
      { id: 2, name: '李四', phone: '13800138002' },
      { id: 3, name: '王五', phone: '13800138003' },
      { id: 4, name: '赵六', phone: '13800138004' }
    ]
    
    const getList = async () => {
      loading.value = true
      try {
        const params = {
          pageNum: queryParams.pageNum,
          pageSize: queryParams.pageSize,
          building: queryParams.building,
          unit: queryParams.unit,
          room: queryParams.room,
          type: queryParams.type,
          bindStatus: queryParams.bindStatus
        }
        
        console.log('请求房产列表参数:', params)
        
        const response = await getPropertyList(params)
        console.log('API响应:', response)
        
        if (response && response.data) {
          const result = response.data
          const list = result.list || result.records || []
          
          // 映射后端数据到前端格式
          propertyList.value = list.map(room => ({
            id: room.id,
            building: room.buildingName || room.building || 'A',
            unit: room.unitName || room.unit || '1',
            room: room.roomNo || room.room || '101',
            floor: room.floor || 1,
            area: room.area || 0,
            type: room.roomType || room.type || '未知',
            orientation: room.orientation || '南',
            decoration: room.decoration || 'simple',
            propertyFee: room.propertyFee || 0,
            feeStatus: room.feeStatus || 'unpaid',
            createTime: formatDateTime(room.createTime),
            owners: room.owners || [],
            status: room.status || 'VACANT',
            ownerName: room.ownerName
          }))
          
          total.value = result.total || 0
          console.log('房产列表加载完成:', propertyList.value)
        }
      } catch (error) {
        console.error('获取房产列表失败:', error)
        ElMessage.error('获取房产列表失败: ' + (error.message || '未知错误'))
        // 使用模拟数据作为后备
        propertyList.value = mockProperties
        total.value = mockProperties.length
      } finally {
        loading.value = false
      }
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
    
    const handleQuery = () => {
      queryParams.pageNum = 1
      getList()
    }
    
    const resetQuery = () => {
      Object.assign(queryParams, {
        pageNum: 1,
        pageSize: 20,
        building: '',
        unit: '',
        room: '',
        type: '',
        bindStatus: ''
      })
      getList()
    }
    
    const handleAdd = () => {
      resetForm()
      showFormDialog.value = true
    }
    
    const handleView = (row) => {
      currentProperty.value = row
      showDetailDialog.value = true
    }
    
    const handleEdit = (row) => {
      Object.assign(form, row)
      showFormDialog.value = true
    }
    
    const handleBind = async (row) => {
      currentProperty.value = row
      bindForm.propertyId = row.id
      
      // 从后端获取业主列表
      try {
        const response = await getOwnerList({ pageNum: 1, pageSize: 100 })
        if (response && response.data) {
          const result = response.data
          const list = result.list || result.records || []
          availableOwners.value = list.map(user => ({
            id: user.id,
            name: user.realName || user.username,
            phone: user.phone
          }))
        }
      } catch (error) {
        console.error('获取业主列表失败:', error)
        availableOwners.value = mockOwners
      }
      
      showBindDialog.value = true
    }
    
    const handleUnbind = async (owner) => {
      try {
        await ElMessageBox.confirm(`确定要解绑业主 ${owner.name} 吗？`, '确认操作')
        await unbindOwner(currentProperty.value.id, owner.id)
        ElMessage.success('解绑成功')
        // 刷新当前房产详情
        const response = await getPropertyDetail(currentProperty.value.id)
        if (response && response.data) {
          currentProperty.value = response.data
        }
        getList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('解绑失败:', error)
          ElMessage.error(error.message || '解绑失败')
        }
      }
    }
    
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该房产吗？删除后不可恢复！', '确认删除', {
          type: 'warning'
        })
        await deleteProperty(row.id)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error(error.message || '删除失败')
        }
      }
    }
    
    const handleBatchImport = () => {
      ElMessage.info('批量导入功能开发中...')
    }
    
    const handleBatchDelete = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要删除的房产')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要删除 ${multipleSelection.value.length} 个房产吗？删除后不可恢复！`, '确认删除', {
          type: 'warning'
        })
        const ids = multipleSelection.value.map(item => item.id)
        await batchDeleteProperty(ids)
        ElMessage.success('批量删除成功')
        getList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除失败:', error)
          ElMessage.error(error.message || '批量删除失败')
        }
      }
    }
    
    const handleExport = () => {
      ElMessage.info('导出功能开发中...')
    }
    
    const handleTemplate = () => {
      ElMessage.info('下载模板功能开发中...')
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
        building: '',
        unit: '',
        room: '',
        floor: 1,
        area: null,
        type: '',
        orientation: '',
        decoration: '',
        propertyFee: '',
        remark: ''
      })
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    }
    
    const submitForm = async () => {
      if (!formRef.value) return
      
      const valid = await formRef.value.validate().catch(() => false)
      if (!valid) return
      
      try {
        const data = {
          buildingName: form.building,
          unitName: form.unit,
          roomNo: form.room,
          floor: form.floor,
          area: form.area,
          roomType: form.type,
          orientation: form.orientation,
          decoration: form.decoration,
          propertyFee: form.propertyFee,
          remark: form.remark
        }
        
        if (form.id) {
          // 编辑
          await updateProperty(form.id, data)
          ElMessage.success('编辑成功')
        } else {
          // 新增
          await createProperty(data)
          ElMessage.success('新增成功')
        }
        showFormDialog.value = false
        resetForm()
        getList()
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error(error.message || '保存失败')
      }
    }
    
    const confirmBind = async () => {
      if (!bindForm.ownerId) {
        ElMessage.error('请选择要绑定的业主')
        return
      }
      
      try {
        await bindOwner(bindForm.propertyId, bindForm.ownerId, bindForm.bindType.toUpperCase())
        ElMessage.success('绑定成功')
        showBindDialog.value = false
        
        // 重置表单
        Object.assign(bindForm, {
          propertyId: null,
          ownerId: null,
          bindType: 'owner',
          remark: ''
        })
        
        // 刷新房产详情
        if (currentProperty.value) {
          const response = await getPropertyDetail(currentProperty.value.id)
          if (response && response.data) {
            currentProperty.value = response.data
          }
        }
        
        // 刷新列表
        getList()
      } catch (error) {
        console.error('绑定失败:', error)
        ElMessage.error(error.message || '绑定失败')
      }
    }
    
    const getDecorationText = (decoration) => {
      const decorationMap = {
        rough: '毛坯',
        simple: '简装',
        fine: '精装',
        luxury: '豪装'
      }
      return decorationMap[decoration] || '未知'
    }
    
    const getDecorationTagType = (decoration) => {
      const decorationMap = {
        rough: 'info',
        simple: 'warning',
        fine: 'success',
        luxury: 'danger'
      }
      return decorationMap[decoration] || 'info'
    }
    
    const getOwnerTypeText = (type) => {
      const typeMap = {
        owner: '业主',
        tenant: '租户',
        family: '家属'
      }
      return typeMap[type] || '未知'
    }
    
    const getOwnerTypeTag = (type) => {
      const typeMap = {
        owner: 'success',
        tenant: 'warning',
        family: 'info'
      }
      return typeMap[type] || 'info'
    }
    
    const getFeeStatusText = (status) => {
      const statusMap = {
        paid: '已缴费',
        unpaid: '未缴费',
        overdue: '已逾期'
      }
      return statusMap[status] || '未知'
    }
    
    const getFeeStatusTagType = (status) => {
      const statusMap = {
        paid: 'success',
        unpaid: 'warning',
        overdue: 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    onMounted(() => {
      getList()
    })
    
    return {
      loading,
      propertyList,
      total,
      multipleSelection,
      showDetailDialog,
      showFormDialog,
      showBindDialog,
      currentProperty,
      formRef,
      availableOwners,
      queryParams,
      form,
      bindForm,
      formRules,
      buildingOptions,
      unitOptions,
      formTitle,
      getList,
      handleQuery,
      resetQuery,
      handleAdd,
      handleView,
      handleEdit,
      handleBind,
      handleUnbind,
      handleDelete,
      handleBatchImport,
      handleBatchDelete,
      handleExport,
      handleTemplate,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      resetForm,
      submitForm,
      confirmBind,
      getDecorationText,
      getDecorationTagType,
      getOwnerTypeText,
      getOwnerTypeTag,
      getFeeStatusText,
      getFeeStatusTagType
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.property-container {
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
  
  .property-details {
    font-size: 12px;
    color: $text-secondary;
  }
}

.owner-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  
  .owner-name {
    font-size: 14px;
    color: $text-primary;
  }
}

.fee-info {
  .fee-status {
    font-size: 12px;
    
    &.paid {
      color: $success-color;
    }
    
    &.unpaid {
      color: $warning-color;
    }
    
    &.overdue {
      color: $error-color;
    }
  }
}

.property-detail {
  .owner-section {
    margin-top: 24px;
    
    h3 {
      color: $text-primary;
      margin-bottom: 16px;
      font-size: 16px;
      font-weight: 600;
    }
    
    .owner-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
      
      .owner-card {
        .owner-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .owner-details {
            flex: 1;
            
            h4 {
              color: $text-primary;
              margin: 0 0 4px 0;
              font-size: 16px;
            }
            
            p {
              color: $text-secondary;
              margin: 0 0 8px 0;
              font-size: 14px;
            }
          }
        }
      }
    }
    
    .no-owner {
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
  
  .property-detail .owner-list {
    grid-template-columns: 1fr;
  }
}
</style>

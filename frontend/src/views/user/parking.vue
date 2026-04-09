<template>
  <div class="parking-container">
    <div class="page-header">
      <h1 class="page-title">车位管理</h1>
      <p class="page-subtitle">管理固定车位、临停车位和业主绑定关系</p>
    </div>

    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="区域">
          <el-input v-model="queryParams.areaCode" placeholder="请输入区域，如地下B1" clearable style="width: 160px" @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item label="车位类型">
          <el-select v-model="queryParams.spaceType" placeholder="全部类型" clearable style="width: 140px">
            <el-option label="固定车位" value="FIXED" />
            <el-option label="临停车位" value="TEMPORARY" />
            <el-option label="充电车位" value="CHARGING" />
            <el-option label="访客车位" value="VISITOR" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="空闲" value="AVAILABLE" />
            <el-option label="已出租" value="OCCUPIED" />
            <el-option label="预留" value="RESERVED" />
            <el-option label="停用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.keyword" placeholder="搜索车位编号、车牌号" clearable style="width: 220px" @keyup.enter="loadList" />
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

    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增车位
        </el-button>
        <el-button type="danger" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <el-card class="table-card">
      <el-table v-loading="loading" :data="parkingList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="spaceNo" label="车位编号" width="120" />
        <el-table-column prop="areaCode" label="区域" width="140" />
        <el-table-column label="类型" width="120">
          <template #default="scope">
            <el-tag size="small">{{ getSpaceTypeText(scope.row.spaceType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="vehicleNo" label="车牌号" width="120">
          <template #default="scope">
            {{ scope.row.vehicleNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="绑定业主" min-width="160">
          <template #default="scope">
            <div v-if="scope.row.ownerName">
              <div>{{ scope.row.ownerName }}</div>
              <div class="sub-text">{{ scope.row.ownerPhone || '未维护手机号' }}</div>
            </div>
            <span v-else class="sub-text">未绑定</span>
          </template>
        </el-table-column>
        <el-table-column label="月租" width="100">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.monthlyFee) }}
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="到期日期" width="120">
          <template #default="scope">
            {{ scope.row.expireDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadList"
          @current-change="loadList"
        />
      </div>
    </el-card>

    <el-dialog v-model="showFormDialog" :title="form.id ? '编辑车位' : '新增车位'" width="640px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车位编号" prop="spaceNo">
              <el-input v-model="form.spaceNo" placeholder="例如 B1-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="areaCode">
              <el-input v-model="form.areaCode" placeholder="例如 地下B1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车位类型" prop="spaceType">
              <el-select v-model="form.spaceType" style="width: 100%">
                <el-option label="固定车位" value="FIXED" />
                <el-option label="临停车位" value="TEMPORARY" />
                <el-option label="充电车位" value="CHARGING" />
                <el-option label="访客车位" value="VISITOR" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="空闲" value="AVAILABLE" />
                <el-option label="已出租" value="OCCUPIED" />
                <el-option label="预留" value="RESERVED" />
                <el-option label="停用" value="DISABLED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="绑定业主">
              <el-select v-model="form.ownerId" clearable filterable placeholder="请选择业主" style="width: 100%">
                <el-option v-for="owner in ownerOptions" :key="owner.id" :label="`${owner.realName} (${owner.phone})`" :value="owner.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号">
              <el-input v-model="form.vehicleNo" placeholder="请输入车牌号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="月租费用">
              <el-input-number v-model="form.monthlyFee" :min="0" :precision="2" :step="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期">
              <el-date-picker v-model="form.expireDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择到期日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import {
  batchDeleteParkingSpace,
  createParkingSpace,
  deleteParkingSpace,
  getParkingSpaceList,
  updateParkingSpace
} from '@/api/parking'
import { getOwnerList } from '@/api/owner'

const loading = ref(false)
const total = ref(0)
const parkingList = ref([])
const multipleSelection = ref([])
const ownerOptions = ref([])
const showFormDialog = ref(false)
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  areaCode: '',
  spaceType: '',
  status: '',
  keyword: ''
})

const form = reactive({
  id: null,
  spaceNo: '',
  areaCode: '',
  spaceType: 'FIXED',
  status: 'AVAILABLE',
  ownerId: null,
  vehicleNo: '',
  monthlyFee: 200,
  expireDate: '',
  remark: ''
})

const formRules = {
  spaceNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }],
  areaCode: [{ required: true, message: '请输入所属区域', trigger: 'blur' }],
  spaceType: [{ required: true, message: '请选择车位类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const response = await getParkingSpaceList({ ...queryParams })
    const result = response.data || {}
    parkingList.value = result.list || result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error('加载车位列表失败:', error)
    ElMessage.error(error.message || '加载车位列表失败')
  } finally {
    loading.value = false
  }
}

const loadOwners = async () => {
  try {
    const response = await getOwnerList({ pageNum: 1, pageSize: 500 })
    const result = response.data || {}
    ownerOptions.value = result.list || result.records || []
  } catch (error) {
    console.error('加载业主列表失败:', error)
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  loadList()
}

const resetQuery = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 20,
    areaCode: '',
    spaceType: '',
    status: '',
    keyword: ''
  })
  loadList()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    spaceNo: '',
    areaCode: '',
    spaceType: 'FIXED',
    status: 'AVAILABLE',
    ownerId: null,
    vehicleNo: '',
    monthlyFee: 200,
    expireDate: '',
    remark: ''
  })
  formRef.value?.clearValidate()
}

const handleAdd = () => {
  resetForm()
  showFormDialog.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    spaceNo: row.spaceNo,
    areaCode: row.areaCode,
    spaceType: row.spaceType || 'FIXED',
    status: row.status || 'AVAILABLE',
    ownerId: row.ownerId || null,
    vehicleNo: row.vehicleNo || '',
    monthlyFee: Number(row.monthlyFee || 0),
    expireDate: row.expireDate || '',
    remark: row.remark || ''
  })
  showFormDialog.value = true
}

const submitForm = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  const payload = {
    spaceNo: form.spaceNo,
    areaCode: form.areaCode,
    spaceType: form.spaceType,
    status: form.status,
    ownerId: form.ownerId || null,
    vehicleNo: form.vehicleNo || null,
    monthlyFee: Number(form.monthlyFee || 0),
    expireDate: form.expireDate || null,
    remark: form.remark || null
  }

  try {
    if (form.id) {
      await updateParkingSpace(form.id, payload)
      ElMessage.success('更新车位成功')
    } else {
      await createParkingSpace(payload)
      ElMessage.success('新增车位成功')
    }
    showFormDialog.value = false
    resetForm()
    loadList()
  } catch (error) {
    console.error('保存车位失败:', error)
    ElMessage.error(error.message || '保存车位失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除车位 ${row.spaceNo} 吗？`, '确认删除', { type: 'warning' })
    await deleteParkingSpace(row.id)
    ElMessage.success('删除车位成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除车位失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请先选择车位')
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${multipleSelection.value.length} 个车位吗？`, '批量删除', { type: 'warning' })
    await batchDeleteParkingSpace(multipleSelection.value.map(item => item.id))
    ElMessage.success('批量删除车位成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除车位失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

const getSpaceTypeText = (type) => {
  const map = {
    FIXED: '固定车位',
    TEMPORARY: '临停车位',
    CHARGING: '充电车位',
    VISITOR: '访客车位'
  }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = {
    AVAILABLE: '空闲',
    OCCUPIED: '已出租',
    RESERVED: '预留',
    DISABLED: '停用'
  }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = {
    AVAILABLE: 'success',
    OCCUPIED: 'warning',
    RESERVED: 'info',
    DISABLED: 'danger'
  }
  return map[status] || ''
}

const formatMoney = (value) => Number(value || 0).toFixed(2)

onMounted(async () => {
  await Promise.all([loadOwners(), loadList()])
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.parking-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

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

.filter-card,
.table-card {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.toolbar-left {
  display: flex;
  gap: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.sub-text {
  color: $text-secondary;
  font-size: 12px;
}
</style>
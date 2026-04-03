<template>
  <div class="parking-container responsive-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">车位管理</h1>
        <p class="page-subtitle">管理小区的停车位及绑定状态</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增车位
        </el-button>
        <el-button type="success" :disabled="selectedSpaces.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section responsive-card">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索车位号、业主姓名"
            prefix-icon="Search"
            style="width: 250px"
            clearable
            @keyup.enter="loadData"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetFilters">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 列表 -->
    <div class="list-section responsive-card">
      <el-table
        :data="parkingList"
        v-loading="loading"
        stripe
        @selection-change="handleSelectionChange"
        class="parking-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="spaceNo" label="车位号" width="120" />
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="绑定业主" width="120">
          <template #default="{ row }">
            {{ row.ownerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="ownerPhone" label="联系电话" width="150">
          <template #default="{ row }">
            {{ row.ownerPhone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="vehicleNo" label="绑定车牌" width="120">
          <template #default="{ row }">
            {{ row.vehicleNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="parkingFee" label="管理费(元/月)" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 表单对话框 -->
    <el-dialog :title="dialogType === 'add' ? '新增车位' : '编辑车位'" v-model="dialogVisible" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="车位编号" prop="spaceNo">
          <el-input v-model="formData.spaceNo" placeholder="请输入车位编号，如 A-001" />
        </el-form-item>
        <el-form-item label="车位位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入车位位置" />
        </el-form-item>
        <el-form-item label="车位状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" value="IDLE" />
            <el-option label="使用中" value="OCCUPIED" />
            <el-option label="已售" value="SOLD" />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定业主ID" prop="ownerId">
          <el-input-number v-model="formData.ownerId" :min="1" placeholder="请填写业主ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="绑定车牌" prop="vehicleNo">
          <el-input v-model="formData.vehicleNo" placeholder="请输入车牌号" />
        </el-form-item>
        <el-form-item label="停车管理费" prop="parkingFee">
          <el-input-number v-model="formData.parkingFee" :precision="2" :step="10" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParkingList, addParking, updateParking, deleteParking } from '@/api/parking'

export default {
  name: 'ParkingManagement',
  components: { Plus, Search },
  setup() {
    const loading = ref(false)
    const submitting = ref(false)
    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const formRef = ref(null)
    const selectedSpaces = ref([])
    const parkingList = ref([])

    const filters = reactive({
      keyword: ''
    })

    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    const formData = reactive({
      id: null,
      spaceNo: '',
      location: '',
      status: 'IDLE',
      ownerId: null,
      vehicleNo: '',
      parkingFee: 0
    })

    const rules = {
      spaceNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }],
      status: [{ required: true, message: '请选择车位状态', trigger: 'change' }]
    }

    const loadData = async () => {
      loading.value = true
      try {
        const res = await getParkingList({
          pageNum: pagination.current,
          pageSize: pagination.size,
          keyword: filters.keyword
        })
        if (res.data) {
          parkingList.value = res.data.list || []
          pagination.total = res.data.total || 0
        }
      } catch (error) {
        console.error('获取车位列表失败', error)
      } finally {
        loading.value = false
      }
    }

    const resetFilters = () => {
      filters.keyword = ''
      loadData()
    }

    const handleSelectionChange = (selection) => {
      selectedSpaces.value = selection
    }

    const handleAdd = () => {
      dialogType.value = 'add'
      resetForm()
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogType.value = 'edit'
      resetForm()
      Object.assign(formData, row)
      dialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm('确认删除该车位?', '提示', { type: 'warning' }).then(async () => {
        try {
          await deleteParking(row.id)
          ElMessage.success('删除成功')
          loadData()
        } catch (error) {
          ElMessage.error('删除失败')
        }
      })
    }

    const handleBatchDelete = () => {
      const ids = selectedSpaces.value.map(item => item.id).join(',')
      ElMessageBox.confirm('确认删除选中的车位?', '提示', { type: 'warning' }).then(async () => {
        try {
          await deleteParking(ids)
          ElMessage.success('删除成功')
          loadData()
        } catch (error) {
          ElMessage.error('删除失败')
        }
      })
    }

    const submitForm = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true
          try {
            if (dialogType.value === 'add') {
              await addParking(formData)
              ElMessage.success('添加成功')
            } else {
              await updateParking(formData)
              ElMessage.success('修改成功')
            }
            dialogVisible.value = false
            loadData()
          } catch (error) {
            ElMessage.error('保存失败')
          } finally {
            submitting.value = false
          }
        }
      })
    }

    const resetForm = () => {
      formData.id = null
      formData.spaceNo = ''
      formData.location = ''
      formData.status = 'IDLE'
      formData.ownerId = null
      formData.vehicleNo = ''
      formData.parkingFee = 0
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    }

    const getStatusName = (status) => {
      switch (status) {
        case 'IDLE': return '空闲'
        case 'OCCUPIED': return '使用中'
        case 'SOLD': return '已售'
        default: return status
      }
    }

    const getStatusTag = (status) => {
      switch (status) {
        case 'IDLE': return 'success'
        case 'OCCUPIED': return 'warning'
        case 'SOLD': return 'danger'
        default: return 'info'
      }
    }

    onMounted(() => {
      loadData()
    })

    return {
      loading,
      submitting,
      dialogVisible,
      dialogType,
      formRef,
      selectedSpaces,
      parkingList,
      filters,
      pagination,
      formData,
      rules,
      loadData,
      resetFilters,
      handleSelectionChange,
      handleAdd,
      handleEdit,
      handleDelete,
      handleBatchDelete,
      submitForm,
      getStatusName,
      getStatusTag
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.filter-section {
  padding: 18px;
  background-color: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}
.list-section {
  padding: 18px;
  background-color: #fff;
  border-radius: 4px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>

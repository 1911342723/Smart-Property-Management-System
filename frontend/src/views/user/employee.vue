<template>
  <div class="employee-container">
    <div class="page-header">
      <h1 class="page-title">员工管理</h1>
      <p class="page-subtitle">管理员工账号、角色权限和工作状态</p>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :model="queryParams" :inline="true" class="filter-form">
        <el-form-item label="员工姓名">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入员工姓名"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="queryParams.department" placeholder="请选择部门" clearable style="width: 160px">
            <el-option label="管理部" value="management" />
            <el-option label="维修部" value="maintenance" />
            <el-option label="保安部" value="security" />
            <el-option label="保洁部" value="cleaning" />
            <el-option label="客服部" value="service" />
          </el-select>
        </el-form-item>
        <el-form-item label="员工类型">
          <el-select v-model="queryParams.role" placeholder="请选择员工类型" clearable style="width: 140px">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="维修工" value="WORKER" />
            <el-option label="保安" value="GUARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
            <el-option label="离职" value="resigned" />
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
          新增员工
        </el-button>
        <el-button type="warning" :disabled="!multipleSelection.length" @click="handleBatchDisable">
          <el-icon><Lock /></el-icon>
          批量禁用
        </el-button>
        <el-button type="success" :disabled="!multipleSelection.length" @click="handleBatchEnable">
          <el-icon><Unlock /></el-icon>
          批量启用
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
        :data="employeeList"
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
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="部门" width="100">
          <template #default="scope">
            <el-tag size="small">{{ getDepartmentText(scope.row.department) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)" size="small">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限" width="200">
          <template #default="scope">
            <el-tag
              v-for="permission in scope.row.permissions"
              :key="permission"
              size="small"
              style="margin: 2px"
            >
              {{ getPermissionText(permission) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusTagType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handlePermission(scope.row)"
            >
              权限
            </el-button>
            <el-button
              :type="scope.row.status === 'active' ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'active' ? '禁用' : '启用' }}
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

    <!-- 新增/编辑员工弹窗 -->
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
          <el-col :span="12">
            <el-form-item label="员工姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入员工姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-select v-model="form.department" placeholder="请选择部门" style="width: 100%">
                <el-option label="管理部" value="management" />
                <el-option label="维修部" value="maintenance" />
                <el-option label="保安部" value="security" />
                <el-option label="保洁部" value="cleaning" />
                <el-option label="客服部" value="service" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="管理员" value="ADMIN" />
                <el-option label="维修工" value="WORKER" />
                <el-option label="保安" value="GUARD" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="!form.id" label="初始密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入初始密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限设置弹窗 -->
    <el-dialog
      v-model="showPermissionDialog"
      title="权限设置"
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="currentEmployee">
        <h3>{{ currentEmployee.name }} - 权限设置</h3>
        <el-checkbox-group v-model="selectedPermissions">
          <el-checkbox
            v-for="permission in allPermissions"
            :key="permission.value"
            :label="permission.value"
          >
            {{ permission.label }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="showPermissionDialog = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Lock, Unlock, Download } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser, resetPassword } from '@/api/user'

export default {
  name: 'Employee',
  components: {
    Search,
    Refresh,
    Plus,
    Lock,
    Unlock,
    Download
  },
  setup() {
    const loading = ref(false)
    const employeeList = ref([])
    const total = ref(0)
    const multipleSelection = ref([])
    const showFormDialog = ref(false)
    const showPermissionDialog = ref(false)
    const currentEmployee = ref(null)
    const formRef = ref(null)
    const selectedPermissions = ref([])
    
    const queryParams = reactive({
      pageNum: 1,
      pageSize: 20,
      name: '',
      department: '',
      role: '',
      status: ''
    })
    
    const form = reactive({
      id: null,
      name: '',
      username: '',
      phone: '',
      email: '',
      department: '',
      role: '',
      password: '',
      status: 'active'
    })
    
    const formRules = {
      name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      department: [{ required: true, message: '请选择部门', trigger: 'change' }],
      role: [{ required: true, message: '请选择角色', trigger: 'change' }],
      password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
    }
    
    const allPermissions = [
      { label: '用户管理', value: 'user_manage' },
      { label: '房产管理', value: 'property_manage' },
      { label: '工单管理', value: 'workorder_manage' },
      { label: '财务管理', value: 'finance_manage' },
      { label: '公告管理', value: 'notice_manage' },
      { label: '活动管理', value: 'activity_manage' },
      { label: '系统设置', value: 'system_config' },
      { label: '数据统计', value: 'data_stats' }
    ]
    
    const formTitle = computed(() => {
      return form.id ? '编辑员工' : '新增员工'
    })
    
    // 获取员工列表
    const getList = async () => {
      loading.value = true
      try {
        const params = {
          pageNum: queryParams.pageNum,
          pageSize: queryParams.pageSize,
          keyword: queryParams.name
        }
        
        // 根据选择的角色过滤，如果没选则查所有员工类型
        if (queryParams.role) {
          params.role = queryParams.role
        }
        
        console.log('请求参数:', params)
        
        const response = await getUserList(params)
        console.log('API响应:', response)
        
        if (response && response.data) {
          const result = response.data
          
          // 处理返回的数据列表
          const list = result.list || result.records || []
          
          // 映射后端数据到前端格式
          employeeList.value = list.map(user => ({
            id: user.id,
            name: user.realName || user.username,
            username: user.username,
            phone: user.phone,
            email: user.email || '',
            avatar: user.avatar || '',
            department: getUserDepartment(user.userType),
            role: getUserRole(user.userType),
            userType: user.userType, // 保存原始类型
            status: user.status === 1 ? 'active' : 'disabled',
            permissions: [], // TODO: 从权限系统获取
            lastLoginTime: formatDateTime(user.lastLoginTime) || '从未登录',
            skillTags: user.skillTags || [], // 维修工的技能标签
            gender: user.gender,
            birthday: user.birthday,
            signature: user.signature,
            emergencyContact: user.emergencyContact,
            emergencyPhone: user.emergencyPhone,
            createTime: user.createTime,
            updateTime: user.updateTime
          }))
          
          total.value = result.total || 0
          console.log('映射后的员工列表:', employeeList.value)
        }
      } catch (error) {
        console.error('获取员工列表失败:', error)
        ElMessage.error('获取员工列表失败: ' + (error.message || '未知错误'))
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
    
    // 根据用户类型获取部门
    const getUserDepartment = (userType) => {
      const departmentMap = {
        'ADMIN': 'management',
        'WORKER': 'maintenance',
        'GUARD': 'security',
        'OWNER': 'owner'
      }
      return departmentMap[userType] || 'other'
    }
    
    // 根据用户类型获取角色
    const getUserRole = (userType) => {
      return userType || 'WORKER'
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
        department: '',
        role: '',
        status: ''
      })
      getList()
    }
    
    const handleAdd = () => {
      resetForm()
      showFormDialog.value = true
    }
    
    const handleEdit = (row) => {
      Object.assign(form, {
        ...row,
        role: row.userType || row.role,
        password: ''
      })
      showFormDialog.value = true
    }
    
    const handlePermission = (row) => {
      currentEmployee.value = row
      selectedPermissions.value = [...row.permissions]
      showPermissionDialog.value = true
    }
    
    const handleToggleStatus = async (row) => {
      const action = row.status === 'active' ? '禁用' : '启用'
      const newStatus = row.status === 'active' ? 0 : 1
      
      try {
        await ElMessageBox.confirm(`确定要${action}该员工吗？`, '确认操作')
        
        await updateUser(row.id, {
          status: newStatus
        })
        
        row.status = newStatus === 1 ? 'active' : 'disabled'
        ElMessage.success(`${action}成功`)
      } catch (error) {
        if (error !== 'cancel') {
          console.error(`${action}失败:`, error)
          ElMessage.error(error.message || `${action}失败`)
        }
      }
    }
    
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该员工吗？删除后不可恢复！', '确认删除', {
          type: 'warning'
        })
        
        await deleteUser(row.id)
        ElMessage.success('删除成功')
        getList() // 刷新列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error(error.message || '删除失败')
        }
      }
    }
    
    const handleBatchDisable = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要操作的员工')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量禁用 ${multipleSelection.value.length} 个员工吗？`, '确认操作')
        multipleSelection.value.forEach(employee => {
          employee.status = 'disabled'
        })
        ElMessage.success('批量禁用成功')
      } catch {
        // 用户取消
      }
    }
    
    const handleBatchEnable = async () => {
      if (multipleSelection.value.length === 0) {
        ElMessage.error('请先选择要操作的员工')
        return
      }
      
      try {
        await ElMessageBox.confirm(`确定要批量启用 ${multipleSelection.value.length} 个员工吗？`, '确认操作')
        multipleSelection.value.forEach(employee => {
          employee.status = 'active'
        })
        ElMessage.success('批量启用成功')
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
        name: '',
        username: '',
        phone: '',
        email: '',
        department: '',
        role: '',
        password: '',
        status: 'active'
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
        loading.value = true
        
        // 构建提交数据
        const submitData = {
          username: form.username,
          realName: form.name,
          phone: form.phone,
          email: form.email,
          userType: form.role || getDepartmentUserType(form.department),
          status: form.status === 'active' ? 1 : 0
        }
        
        if (form.id) {
          // 编辑用户
          await updateUser(form.id, submitData)
          ElMessage.success('编辑成功')
        } else {
          // 新增用户
          submitData.password = form.password || '123456' // 默认密码
          await createUser(submitData)
          ElMessage.success('新增成功')
        }
        
        showFormDialog.value = false
        resetForm()
        getList() // 刷新列表
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
    
    // 根据部门获取用户类型
    const getDepartmentUserType = (department) => {
      const typeMap = {
        'management': 'ADMIN',
        'maintenance': 'WORKER',
        'security': 'GUARD',
        'owner': 'OWNER'
      }
      return typeMap[department] || 'WORKER'
    }
    
    const savePermissions = () => {
      if (currentEmployee.value) {
        currentEmployee.value.permissions = [...selectedPermissions.value]
        ElMessage.success('权限设置成功')
        showPermissionDialog.value = false
      }
    }
    
    const getDepartmentText = (department) => {
      const departmentMap = {
        management: '管理部',
        maintenance: '维修部',
        security: '保安部',
        cleaning: '保洁部',
        service: '客服部'
      }
      return departmentMap[department] || '未知'
    }
    
    const getRoleText = (role) => {
      const roleMap = {
        ADMIN: '管理员',
        WORKER: '维修工',
        GUARD: '保安'
      }
      return roleMap[role] || '未知'
    }
    
    const getRoleTagType = (role) => {
      const roleMap = {
        ADMIN: 'danger',
        WORKER: 'success',
        GUARD: 'warning'
      }
      return roleMap[role] || 'info'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        active: '正常',
        disabled: '禁用',
        resigned: '离职'
      }
      return statusMap[status] || '未知'
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        active: 'success',
        disabled: 'warning',
        resigned: 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    const getPermissionText = (permission) => {
      const permissionMap = {
        user_manage: '用户管理',
        property_manage: '房产管理',
        workorder_manage: '工单管理',
        finance_manage: '财务管理',
        notice_manage: '公告管理',
        activity_manage: '活动管理',
        system_config: '系统设置',
        data_stats: '数据统计'
      }
      return permissionMap[permission] || '未知权限'
    }
    
    onMounted(() => {
      getList()
    })
    
    return {
      loading,
      employeeList,
      total,
      multipleSelection,
      showFormDialog,
      showPermissionDialog,
      currentEmployee,
      formRef,
      selectedPermissions,
      queryParams,
      form,
      formRules,
      allPermissions,
      formTitle,
      getList,
      formatDateTime,
      getUserDepartment,
      getUserRole,
      getDepartmentUserType,
      handleQuery,
      resetQuery,
      handleAdd,
      handleEdit,
      handlePermission,
      handleToggleStatus,
      handleDelete,
      handleBatchDisable,
      handleBatchEnable,
      handleExport,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      resetForm,
      submitForm,
      savePermissions,
      getDepartmentText,
      getRoleText,
      getRoleTagType,
      getStatusText,
      getStatusTagType,
      getPermissionText
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.employee-container {
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

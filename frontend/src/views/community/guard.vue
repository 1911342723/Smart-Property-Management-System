<template>
  <div class="guard-container responsive-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">保安管理</h1>
        <p class="page-subtitle">管理保安人员排班与巡查异常上报记录</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAddGuard">
          <el-icon><Plus /></el-icon>
          登记保安
        </el-button>
        <el-button type="warning" @click="handleRecordPatrol">
          <el-icon><EditPen /></el-icon>
          登记巡查记录
        </el-button>
      </div>
    </div>

    <!-- 顶部统计 -->
    <div class="stats-cards responsive-grid grid-4" style="margin-bottom: 20px;">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-label">在岗保安人数</div>
          <div class="stat-value">8</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-label">今日完成巡查 (次)</div>
          <div class="stat-value" style="color: #67C23A;">15</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-label">今日发现异常 (件)</div>
          <div class="stat-value" style="color: #E6A23C;">2</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-content">
          <div class="stat-label">已处理异常</div>
          <div class="stat-value" style="color: #409EFF;">1</div>
        </div>
      </el-card>
    </div>

    <el-tabs v-model="activeTab" class="guard-tabs" type="border-card">
      <!-- 异常上报/巡查记录 -->
      <el-tab-pane label="巡查与异常上报" name="patrol">
        <div class="filter-section" style="margin-bottom: 15px;">
          <el-form :model="patrolFilters" inline>
            <el-form-item label="状态">
              <el-select v-model="patrolFilters.status" placeholder="全部状态" style="width: 120px">
                <el-option label="正常" value="NORMAL" />
                <el-option label="存在异常" value="ABNORMAL" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="patrolFilters.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 260px"
              />
            </el-form-item>
            <el-form-item>
              <el-input v-model="patrolFilters.keyword" placeholder="搜索汇报保安姓名/地点" style="width: 200px;" prefix-icon="Search"/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="fetchPatrolData">搜索</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table :data="patrolData" stripe border>
          <el-table-column prop="no" label="上报编号" width="150" />
          <el-table-column prop="guardName" label="上报人 (保安)" width="120" />
          <el-table-column prop="location" label="巡查地点" width="180" />
          <el-table-column prop="checkTime" label="巡查时间" width="160" />
          <el-table-column prop="status" label="巡查状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                {{ row.status === 'NORMAL' ? '正常' : '异常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="情况说明" min-width="200" show-overflow-tooltip />
          <el-table-column prop="handlerName" label="处理人" width="100" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handlePatrolDetail(row)">详情</el-button>
              <el-button type="success" link size="small" v-if="row.status === 'ABNORMAL'">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container" style="text-align: right; margin-top: 15px;">
           <el-pagination layout="total, prev, pager, next" :total="patrolTotal" />
        </div>
      </el-tab-pane>

      <!-- 保安人员列表 -->
      <el-tab-pane label="保安人员档案" name="personnel">
        <el-table :data="guardList" stripe>
          <el-table-column prop="empNo" label="工号" width="120" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="phone" label="联系电话" width="150" />
          <el-table-column prop="shift" label="当前班次" width="120">
            <template #default="{ row }">
              <el-tag size="small" :type="row.shift === '早班' ? 'warning' : (row.shift === '中班' ? 'primary' : 'info')">{{ row.shift }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="在职状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
                {{ row.status === 'ACTIVE' ? '在职' : '离职' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="entryDate" label="入职时间" width="150" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button type="primary" link size="small">排班</el-button>
              <el-button type="primary" link size="small">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 简化版详情弹窗 -->
    <el-dialog title="巡查记录详情" v-model="patrolDialogVisible" width="500px">
      <el-descriptions :column="1" border v-if="selectedPatrol">
        <el-descriptions-item label="上报编号">{{ selectedPatrol.no }}</el-descriptions-item>
        <el-descriptions-item label="巡查人">{{ selectedPatrol.guardName }}</el-descriptions-item>
        <el-descriptions-item label="发生时间">{{ selectedPatrol.checkTime }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ selectedPatrol.location }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedPatrol.status === 'NORMAL' ? 'success' : 'danger'">
            {{ selectedPatrol.status === 'NORMAL' ? '正常' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述详情">{{ selectedPatrol.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="patrolDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { Plus, EditPen, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'GuardManagement',
  components: { Plus, EditPen, Search },
  setup() {
    const activeTab = ref('patrol')
    const patrolFilters = reactive({ status: '', keyword: '', dateRange: [] })
    
    // 模拟数据
    const guardList = ref([
      { id: 1, empNo: 'G001', name: '保安小刘', phone: '13800000201', shift: '早班', status: 'ACTIVE', entryDate: '2022-03-15' },
      { id: 2, empNo: 'G002', name: '保安小赵', phone: '13800000202', shift: '中班', status: 'ACTIVE', entryDate: '2023-01-10' },
      { id: 3, empNo: 'G003', name: '李保安', phone: '13911112222', shift: '夜班', status: 'ACTIVE', entryDate: '2021-11-05' }
    ])

    const patrolData = ref([
      { id: 1, no: 'P20231024001', guardName: '保安小刘', location: 'A栋地下车库', checkTime: '2023-10-24 08:30:00', status: 'NORMAL', description: '设备正常，无异常人员', handlerName: '-' },
      { id: 2, no: 'P20231024002', guardName: '保安小赵', location: '西门门禁', checkTime: '2023-10-24 14:15:00', status: 'ABNORMAL', description: '门禁闸机无法正常抬杆', handlerName: '王维修' },
      { id: 3, no: 'P20231024003', guardName: '李保安', location: 'C栋楼道', checkTime: '2023-10-24 23:45:00', status: 'ABNORMAL', description: '楼道消防栓玻璃破损', handlerName: '-' },
    ])
    const patrolTotal = ref(3)

    const patrolDialogVisible = ref(false)
    const selectedPatrol = ref(null)

    const fetchPatrolData = () => {
      // 模拟查询
      ElMessage.success('查询成功')
    }

    const handleAddGuard = () => {
      ElMessage.info('功能开发中，将调起员工入职登记表单')
    }

    const handleRecordPatrol = () => {
      ElMessage.info('功能开发中，用于手工补录巡查记录')
    }

    const handlePatrolDetail = (row) => {
      selectedPatrol.value = row
      patrolDialogVisible.value = true
    }

    onMounted(() => {
      // 初始化数据加载
    })

    return {
      activeTab,
      patrolFilters,
      guardList,
      patrolData,
      patrolTotal,
      patrolDialogVisible,
      selectedPatrol,
      fetchPatrolData,
      handleAddGuard,
      handleRecordPatrol,
      handlePatrolDetail
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
.stat-card {
  text-align: center;
}
.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
}
</style>

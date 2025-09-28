<template>
  <div class="assign-container">
    <div class="page-header">
      <h1 class="page-title">工单分配</h1>
      <p class="page-subtitle">智能分配和手动分配工单给合适的处理人员</p>
    </div>

    <el-card>
      <div class="assign-content">
        <el-empty description="工单分配功能开发中...">
          <el-button type="primary" @click="$router.push('/work-order/list')">
            返回工单列表
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'WorkOrderAssign'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.assign-container {
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

.assign-content {
  padding: 60px 0;
  text-align: center;
}
</style>

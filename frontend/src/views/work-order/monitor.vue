<template>
  <div class="monitor-container">
    <div class="page-header">
      <h1 class="page-title">进度监控</h1>
      <p class="page-subtitle">实时监控工单处理进度和状态</p>
    </div>

    <el-card>
      <div class="monitor-content">
        <el-empty description="进度监控功能开发中...">
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
  name: 'WorkOrderMonitor'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.monitor-container {
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

.monitor-content {
  padding: 60px 0;
  text-align: center;
}
</style>

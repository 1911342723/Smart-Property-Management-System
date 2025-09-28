<template>
  <div class="report-container">
    <div class="page-header">
      <h1 class="page-title">财务报表</h1>
      <p class="page-subtitle">生成和导出各类财务报表</p>
    </div>

    <el-card>
      <div class="report-content">
        <el-empty description="财务报表功能开发中...">
          <el-button type="primary" @click="$router.push('/finance/bill')">
            返回账单管理
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'FinanceReport'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.report-container {
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

.report-content {
  padding: 60px 0;
  text-align: center;
}
</style>

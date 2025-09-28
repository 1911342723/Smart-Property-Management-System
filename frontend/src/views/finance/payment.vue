<template>
  <div class="payment-container">
    <div class="page-header">
      <h1 class="page-title">收费监控</h1>
      <p class="page-subtitle">监控收费状态，管理催缴通知</p>
    </div>

    <el-card>
      <div class="payment-content">
        <el-empty description="收费监控功能开发中...">
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
  name: 'Payment'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.payment-container {
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

.payment-content {
  padding: 60px 0;
  text-align: center;
}
</style>

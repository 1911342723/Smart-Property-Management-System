<template>
  <div class="log-container">
    <div class="page-header">
      <h1 class="page-title">操作日志</h1>
      <p class="page-subtitle">查看系统操作日志和审计信息</p>
    </div>

    <el-card>
      <div class="log-content">
        <el-empty description="操作日志功能开发中...">
          <el-button type="primary" @click="$router.push('/dashboard')">
            返回首页
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SystemLog'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.log-container {
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

.log-content {
  padding: 60px 0;
  text-align: center;
}
</style>

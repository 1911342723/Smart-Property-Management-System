<template>
  <div class="config-container">
    <div class="page-header">
      <h1 class="page-title">系统配置</h1>
      <p class="page-subtitle">管理系统参数和配置</p>
    </div>

    <el-card>
      <div class="config-content">
        <el-empty description="系统配置功能开发中...">
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
  name: 'SystemConfig'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.config-container {
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

.config-content {
  padding: 60px 0;
  text-align: center;
}
</style>

<template>
  <div class="activity-container">
    <div class="page-header">
      <h1 class="page-title">活动管理</h1>
      <p class="page-subtitle">创建和管理社区活动</p>
    </div>

    <el-card>
      <div class="activity-content">
        <el-empty description="活动管理功能开发中...">
          <el-button type="primary" @click="$router.push('/community/notice')">
            返回公告管理
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Activity'
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.activity-container {
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

.activity-content {
  padding: 60px 0;
  text-align: center;
}
</style>

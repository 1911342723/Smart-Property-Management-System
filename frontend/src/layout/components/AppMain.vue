<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <router-view :key="key" />
    </transition>
  </section>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'AppMain',
  setup() {
    const route = useRoute()
    
    const key = computed(() => route.path)
    
    return {
      key
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.app-main {
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
  background-color: $bg-primary;
  margin-left: 210px;
  transition: margin-left 0.28s;
  padding: 20px;
}

.hideSidebar .app-main {
  margin-left: 54px;
}

.mobile .app-main {
  margin-left: 0;
  padding: 16px;
}

// 平板适配
@include tablet {
  .app-main {
    margin-left: 180px;
    padding: 16px;
  }
  
  .hideSidebar .app-main {
    margin-left: 54px;
  }
}

// 手机适配
@include mobile {
  .app-main {
    margin-left: 0 !important;
    padding: 12px !important;
    padding-top: 62px !important; // 为固定导航栏留出空间
    min-height: calc(100vh - 50px) !important;
  }
}

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>

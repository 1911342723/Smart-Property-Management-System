<template>
  <div class="app-wrapper" :class="classObj">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <sidebar class="sidebar-container" />
    <div class="main-container">
      <div class="fixed-header">
        <navbar />
      </div>
      <app-main />
    </div>
  </div>
</template>

<script>
import { computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar.vue'
import AppMain from './components/AppMain.vue'

export default {
  name: 'Layout',
  components: {
    Sidebar,
    Navbar,
    AppMain
  },
  setup() {
    const store = useStore()
    
    const sidebar = computed(() => store.state.app.sidebar)
    const device = computed(() => store.state.app.device)
    
    const classObj = computed(() => ({
      hideSidebar: !sidebar.value.opened,
      openSidebar: sidebar.value.opened,
      withoutAnimation: sidebar.value.withoutAnimation,
      mobile: device.value === 'mobile'
    }))
    
    const handleClickOutside = () => {
      store.dispatch('app/closeSideBar', { withoutAnimation: false })
    }
    
    // 监听窗口大小变化
    const handleResize = () => {
      const width = document.documentElement.clientWidth
      if (width < 768) {
        store.dispatch('app/toggleDevice', 'mobile')
        store.dispatch('app/closeSideBar', { withoutAnimation: true })
      } else {
        store.dispatch('app/toggleDevice', 'desktop')
      }
    }
    
    // 组件挂载时检查设备类型
    onMounted(() => {
      handleResize()
      window.addEventListener('resize', handleResize)
    })
    
    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
    })
    
    return {
      sidebar,
      device,
      classObj,
      handleClickOutside
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  
  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: rgba(0, 0, 0, 0.3);
  width: 100%;
  top: 0;
  height: 100%;
  position: fixed;
  z-index: 1001;
  backdrop-filter: blur(2px);
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 1000;
  width: calc(100% - 210px);
  transition: width 0.28s;
  height: 50px;
  background: $bg-primary;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

// 平板适配
@include tablet {
  .fixed-header {
    width: calc(100% - 180px);
  }
  
  .hideSidebar .fixed-header {
    width: calc(100% - 54px);
  }
}

// 手机适配
@include mobile {
  .fixed-header {
    width: 100% !important;
  }
}
</style>

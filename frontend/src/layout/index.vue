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
import { computed } from 'vue'
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

.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  
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
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - 210px);
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.mobile .fixed-header {
  width: 100%;
}
</style>

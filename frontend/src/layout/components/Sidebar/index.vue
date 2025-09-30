<template>
  <div class="sidebar">
    <div class="logo-container">
      <div class="logo">
        <el-icon :size="32" class="logo-icon">
          <House />
        </el-icon>
        <h1 v-show="!isCollapse" class="logo-title">物业管理系统</h1>
      </div>
    </div>
    
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="false"
        :collapse-transition="false"
        mode="vertical"
        router
      >
        <sidebar-item
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { House } from '@element-plus/icons-vue'
import SidebarItem from './SidebarItem.vue'

export default {
  name: 'Sidebar',
  components: {
    SidebarItem,
    House
  },
  setup() {
    const route = useRoute()
    const store = useStore()
    
    const sidebar = computed(() => store.state.app.sidebar)
    const isCollapse = computed(() => !sidebar.value.opened)
    
    const routes = computed(() => {
      // 从 router 中获取路由，过滤掉隐藏的路由
      const router = require('@/router').default
      return router.options.routes.filter(route => !route.hidden)
    })
    
    const activeMenu = computed(() => {
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    })
    
    return {
      sidebar,
      isCollapse,
      routes,
      activeMenu
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.sidebar {
  height: 100vh;
  background-color: $bg-secondary;
  width: 210px;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
  transition: width 0.28s;
  
  .logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid $border-lighter;
    background: linear-gradient(135deg, $primary-color 0%, $primary-hover 100%);
    
    .logo {
      display: flex;
      align-items: center;
      gap: 12px;
      width: 100%;
      
      .logo-icon {
        color: #ffffff;
        font-size: 32px;
        flex-shrink: 0;
      }
      
      .logo-title {
        font-size: 18px;
        font-weight: 600;
        color: #ffffff;
        margin: 0;
        white-space: nowrap;
      }
    }
  }
  
  .scrollbar-wrapper {
    height: calc(100vh - 60px);
  }
}

// 收起状态
.app-wrapper.hideSidebar .sidebar {
  width: 54px;
  
  .logo-container {
    padding: 0 11px;
    
    .logo-title {
      display: none;
    }
  }
}

// 移动端
.app-wrapper.mobile .sidebar {
  transition: transform 0.28s;
  width: 210px !important;
  position: fixed;
  z-index: 1002;
}

.app-wrapper.mobile.hideSidebar .sidebar {
  pointer-events: none;
  transition-duration: 0.3s;
  transform: translate3d(-210px, 0, 0);
}

// 平板适配
@include tablet {
  .sidebar {
    width: 180px;
  }
  
  .app-wrapper.hideSidebar .sidebar {
    width: 54px;
  }
}

// 手机适配
@include mobile {
  .sidebar {
    position: fixed !important;
    z-index: 1002 !important;
    height: 100vh !important;
    top: 0 !important;
    left: 0 !important;
    width: 250px !important;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .app-wrapper.mobile.openSidebar .sidebar {
    transform: translateX(0);
  }
  
  .logo-container {
    padding: 0 16px !important;
    height: 60px !important;
    
    .logo-title {
      font-size: 16px !important;
    }
  }
  
  .el-menu {
    border-right: none !important;
  }
  
  .el-menu-item,
  .el-sub-menu__title {
    height: 50px !important;
    line-height: 50px !important;
    padding: 0 20px !important;
    margin: 2px 8px !important;
    font-size: 14px !important;
  }
}
</style>

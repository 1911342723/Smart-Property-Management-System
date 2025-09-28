<template>
  <div class="sidebar">
    <div class="logo-container">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Logo" class="logo-img">
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
import SidebarItem from './SidebarItem.vue'

export default {
  name: 'Sidebar',
  components: {
    SidebarItem
  },
  setup() {
    const route = useRoute()
    const store = useStore()
    
    const sidebar = computed(() => store.state.app.sidebar)
    const isCollapse = computed(() => !sidebar.value.opened)
    
    const routes = computed(() => {
      // 过滤掉隐藏的路由
      return [
        {
          path: '/',
          redirect: '/dashboard',
          children: [
            {
              path: 'dashboard',
              name: 'Dashboard',
              meta: { title: '数据仪表盘', icon: 'DataAnalysis' }
            }
          ]
        },
        {
          path: '/user',
          redirect: '/user/owner',
          meta: { title: '用户与房产管理', icon: 'User' },
          children: [
            {
              path: '/user/owner',
              name: 'Owner',
              meta: { title: '业主管理', icon: 'UserFilled' }
            },
            {
              path: '/user/employee',
              name: 'Employee',
              meta: { title: '员工管理', icon: 'Avatar' }
            },
            {
              path: '/user/property',
              name: 'Property',
              meta: { title: '房产管理', icon: 'House' }
            }
          ]
        },
        {
          path: '/work-order',
          redirect: '/work-order/list',
          meta: { title: '服务工单管理', icon: 'List' },
          children: [
            {
              path: '/work-order/list',
              name: 'WorkOrderList',
              meta: { title: '工单列表', icon: 'Document' }
            },
            {
              path: '/work-order/assign',
              name: 'WorkOrderAssign',
              meta: { title: '工单分配', icon: 'Connection' }
            },
            {
              path: '/work-order/monitor',
              name: 'WorkOrderMonitor',
              meta: { title: '进度监控', icon: 'Monitor' }
            }
          ]
        },
        {
          path: '/finance',
          redirect: '/finance/bill',
          meta: { title: '财务管理', icon: 'Money' },
          children: [
            {
              path: '/finance/bill',
              name: 'Bill',
              meta: { title: '账单管理', icon: 'Receipt' }
            },
            {
              path: '/finance/payment',
              name: 'Payment',
              meta: { title: '收费监控', icon: 'CreditCard' }
            },
            {
              path: '/finance/report',
              name: 'FinanceReport',
              meta: { title: '财务报表', icon: 'TrendCharts' }
            }
          ]
        },
        {
          path: '/community',
          redirect: '/community/notice',
          meta: { title: '社区管理', icon: 'ChatDotRound' },
          children: [
            {
              path: '/community/notice',
              name: 'Notice',
              meta: { title: '公告管理', icon: 'Bell' }
            },
            {
              path: '/community/activity',
              name: 'Activity',
              meta: { title: '活动管理', icon: 'Calendar' }
            }
          ]
        },
        {
          path: '/system',
          redirect: '/system/log',
          meta: { title: '系统设置', icon: 'Setting' },
          children: [
            {
              path: '/system/log',
              name: 'SystemLog',
              meta: { title: '操作日志', icon: 'DocumentCopy' }
            },
            {
              path: '/system/config',
              name: 'SystemConfig',
              meta: { title: '系统配置', icon: 'Tools' }
            }
          ]
        }
      ]
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
    border-bottom: 1px solid $border-color;
    
    .logo {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .logo-img {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        background-color: $primary-color;
        padding: 6px;
      }
      
      .logo-title {
        font-size: 18px;
        font-weight: 600;
        color: $text-primary;
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
}

.app-wrapper.mobile.hideSidebar .sidebar {
  pointer-events: none;
  transition-duration: 0.3s;
  transform: translate3d(-210px, 0, 0);
}
</style>

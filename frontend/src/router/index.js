import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据仪表盘', icon: 'DataAnalysis' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/owner',
    meta: { title: '用户与房产管理', icon: 'User' },
    children: [
      {
        path: 'owner',
        name: 'Owner',
        component: () => import('@/views/user/owner.vue'),
        meta: { title: '业主管理', icon: 'UserFilled' }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/user/employee.vue'),
        meta: { title: '员工管理', icon: 'Avatar' }
      },
      {
        path: 'property',
        name: 'Property',
        component: () => import('@/views/user/property.vue'),
        meta: { title: '房产管理', icon: 'House' }
      }
    ]
  },
  {
    path: '/work-order',
    component: Layout,
    redirect: '/work-order/list',
    meta: { title: '服务工单管理', icon: 'List' },
    children: [
      {
        path: 'list',
        name: 'WorkOrderList',
        component: () => import('@/views/work-order/list.vue'),
        meta: { title: '工单列表', icon: 'Document' }
      },
      {
        path: 'assign',
        name: 'WorkOrderAssign',
        component: () => import('@/views/work-order/assign.vue'),
        meta: { title: '工单分配', icon: 'Connection' }
      },
      {
        path: 'monitor',
        name: 'WorkOrderMonitor',
        component: () => import('@/views/work-order/monitor.vue'),
        meta: { title: '进度监控', icon: 'Monitor' }
      }
    ]
  },
  {
    path: '/finance',
    component: Layout,
    redirect: '/finance/bill',
    meta: { title: '财务管理', icon: 'Money' },
    children: [
      {
        path: 'bill',
        name: 'Bill',
        component: () => import('@/views/finance/bill.vue'),
        meta: { title: '账单管理', icon: 'Receipt' }
      },
      {
        path: 'payment',
        name: 'Payment',
        component: () => import('@/views/finance/payment.vue'),
        meta: { title: '收费监控', icon: 'CreditCard' }
      },
      {
        path: 'report',
        name: 'FinanceReport',
        component: () => import('@/views/finance/report.vue'),
        meta: { title: '财务报表', icon: 'TrendCharts' }
      }
    ]
  },
  {
    path: '/community',
    component: Layout,
    redirect: '/community/notice',
    meta: { title: '社区管理', icon: 'ChatDotRound' },
    children: [
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/community/notice.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/community/activity.vue'),
        meta: { title: '活动管理', icon: 'Calendar' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/log',
    meta: { title: '系统设置', icon: 'Setting' },
    children: [
      {
        path: 'log',
        name: 'SystemLog',
        component: () => import('@/views/system/log.vue'),
        meta: { title: '操作日志', icon: 'DocumentCopy' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/system/config.vue'),
        meta: { title: '系统配置', icon: 'Tools' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router

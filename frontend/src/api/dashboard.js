import request from '@/utils/request'

// 数据仪表盘API

/**
 * 获取仪表盘总览数据
 */
export function getDashboardOverview() {
  return request({
    url: '/dashboard/overview',
    method: 'get'
  })
}

/**
 * 获取工单趋势数据
 */
export function getWorkOrderTrend(days = 7) {
  return request({
    url: '/dashboard/workorder-trend',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取财务趋势数据
 */
export function getFinanceTrend(days = 7) {
  return request({
    url: '/dashboard/finance-trend',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取最近活动
 */
export function getRecentActivities(limit = 10) {
  return request({
    url: '/dashboard/recent-activities',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取待办事项
 */
export function getTodoItems() {
  return request({
    url: '/dashboard/todos',
    method: 'get'
  })
}



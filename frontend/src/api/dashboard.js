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
 * 获取工单统计数据
 */
export function getWorkOrderStats() {
  return request({
    url: '/dashboard/workorder-stats',
    method: 'get'
  })
}

/**
 * 获取工单类型分布
 */
export function getWorkOrderDistribution() {
  return request({
    url: '/dashboard/workorder-distribution',
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
 * 获取账单统计数据
 */
export function getBillStats() {
  return request({
    url: '/dashboard/bill-stats',
    method: 'get'
  })
}

/**
 * 获取房产统计数据
 */
export function getPropertyStats() {
  return request({
    url: '/dashboard/property-stats',
    method: 'get'
  })
}

/**
 * 获取入住率趋势
 */
export function getOccupancyTrend(months = 6) {
  return request({
    url: '/dashboard/occupancy-trend',
    method: 'get',
    params: { months }
  })
}

/**
 * 获取月度收费统计
 */
export function getMonthlyPayment(months = 6) {
  return request({
    url: '/dashboard/monthly-payment',
    method: 'get',
    params: { months }
  })
}






import request from '@/utils/request'

// 操作日志API

/**
 * 分页查询操作日志列表
 */
export function getLogList(params) {
  return request({
    url: '/system/log/list',
    method: 'get',
    params
  })
}

/**
 * 获取日志详情
 */
export function getLogDetail(id) {
  return request({
    url: `/system/log/${id}`,
    method: 'get'
  })
}

/**
 * 批量删除日志
 */
export function batchDeleteLog(ids) {
  return request({
    url: '/system/log/batch-delete',
    method: 'delete',
    data: ids
  })
}

/**
 * 清空日志
 */
export function clearLogs(days) {
  return request({
    url: '/system/log/clear',
    method: 'delete',
    params: { days }
  })
}

/**
 * 导出日志
 */
export function exportLogs(params) {
  return request({
    url: '/system/log/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取操作统计
 */
export function getLogStats() {
  return request({
    url: '/system/log/stats',
    method: 'get'
  })
}

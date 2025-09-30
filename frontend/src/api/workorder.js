import request from '@/utils/request'

// 工单管理API

/**
 * 分页查询工单列表
 */
export function getWorkOrderList(params) {
  return request({
    url: '/workorder/list',
    method: 'get',
    params
  })
}

/**
 * 获取工单详情
 */
export function getWorkOrderDetail(id) {
  return request({
    url: `/workorder/${id}`,
    method: 'get'
  })
}

/**
 * 提交工单
 */
export function submitWorkOrder(data) {
  return request({
    url: '/workorder',
    method: 'post',
    data
  })
}

/**
 * 分配工单
 */
export function assignWorkOrder(id, assigneeId) {
  return request({
    url: `/workorder/${id}/assign`,
    method: 'put',
    params: { assigneeId }
  })
}

/**
 * 开始处理工单
 */
export function startWorkOrder(id) {
  return request({
    url: `/workorder/${id}/start`,
    method: 'put'
  })
}

/**
 * 完成工单
 */
export function completeWorkOrder(id, completionNote, actualCost) {
  return request({
    url: `/workorder/${id}/complete`,
    method: 'put',
    params: { completionNote, actualCost }
  })
}

/**
 * 取消工单
 */
export function cancelWorkOrder(id) {
  return request({
    url: `/workorder/${id}/cancel`,
    method: 'put'
  })
}

/**
 * 评价工单
 */
export function rateWorkOrder(id, rating, comment) {
  return request({
    url: `/workorder/${id}/rate`,
    method: 'put',
    params: { rating, comment }
  })
}

/**
 * 删除工单
 */
export function deleteWorkOrder(id) {
  return request({
    url: `/workorder/${id}`,
    method: 'delete'
  })
}

/**
 * 获取工单统计信息
 */
export function getWorkOrderStats() {
  return request({
    url: '/workorder/stats',
    method: 'get'
  })
}




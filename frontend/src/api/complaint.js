import request from '@/utils/request'

// 投诉建议管理API

/**
 * 分页查询投诉列表
 */
export function getComplaintList(params) {
  return request({
    url: '/complaint/list',
    method: 'get',
    params
  })
}

/**
 * 获取投诉详情
 */
export function getComplaintDetail(id) {
  return request({
    url: `/complaint/${id}`,
    method: 'get'
  })
}

/**
 * 提交投诉
 */
export function submitComplaint(data) {
  return request({
    url: '/complaint',
    method: 'post',
    data
  })
}

/**
 * 分配处理人
 */
export function assignHandler(complaintId, handlerId) {
  return request({
    url: '/complaint/assign',
    method: 'post',
    params: { complaintId, handlerId }
  })
}

/**
 * 开始处理投诉
 */
export function startHandling(complaintId, handlerId) {
  return request({
    url: '/complaint/start',
    method: 'post',
    params: { complaintId, handlerId }
  })
}

/**
 * 完成投诉处理
 */
export function completeHandling(complaintId, handlerId, handleResult) {
  return request({
    url: '/complaint/complete',
    method: 'post',
    params: { complaintId, handlerId, handleResult }
  })
}

/**
 * 评价投诉处理
 */
export function rateComplaint(data) {
  return request({
    url: '/complaint/rate',
    method: 'post',
    data
  })
}

/**
 * 获取投诉统计信息
 */
export function getComplaintStats(complainantId) {
  return request({
    url: '/complaint/stats',
    method: 'get',
    params: complainantId ? { complainantId } : {}
  })
}

/**
 * 更新投诉
 */
export function updateComplaint(id, data) {
  return request({
    url: `/complaint/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除投诉
 */
export function deleteComplaint(id) {
  return request({
    url: `/complaint/${id}`,
    method: 'delete'
  })
}




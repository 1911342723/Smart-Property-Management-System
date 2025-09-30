import request from '@/utils/request'

// 访客管理API

/**
 * 分页查询访客列表
 */
export function getVisitorList(params) {
  return request({
    url: '/visitor/list',
    method: 'get',
    params
  })
}

/**
 * 获取访客详情
 */
export function getVisitorDetail(id) {
  return request({
    url: `/visitor/${id}`,
    method: 'get'
  })
}

/**
 * 创建访客预约
 */
export function createVisitor(data) {
  return request({
    url: '/visitor',
    method: 'post',
    data
  })
}

/**
 * 更新访客状态
 */
export function updateVisitorStatus(visitorId, status, guardId) {
  return request({
    url: '/visitor/status',
    method: 'post',
    params: { visitorId, status, guardId }
  })
}

/**
 * 根据二维码获取访客信息
 */
export function getVisitorByQrCode(qrCode) {
  return request({
    url: `/visitor/qrcode/${qrCode}`,
    method: 'get'
  })
}

/**
 * 访客到达签到
 */
export function visitorArrival(qrCode, guardId) {
  return request({
    url: '/visitor/arrival',
    method: 'post',
    params: { qrCode, guardId }
  })
}

/**
 * 访客离开签退
 */
export function visitorDeparture(visitorId, guardId) {
  return request({
    url: '/visitor/departure',
    method: 'post',
    params: { visitorId, guardId }
  })
}

/**
 * 获取访客统计信息
 */
export function getVisitorStats(ownerId) {
  return request({
    url: '/visitor/stats',
    method: 'get',
    params: { ownerId }
  })
}

/**
 * 获取今日访客列表
 */
export function getTodayVisitors(ownerId) {
  return request({
    url: '/visitor/today',
    method: 'get',
    params: { ownerId }
  })
}

/**
 * 扫码验证访客
 */
export function scanVerifyVisitor(data) {
  return request({
    url: '/visitor/scan-verify',
    method: 'post',
    data
  })
}

/**
 * 访客签到
 */
export function checkInVisitor(visitorId, guardId) {
  return request({
    url: '/visitor/check-in',
    method: 'post',
    params: { visitorId, guardId }
  })
}

/**
 * 访客签退
 */
export function checkOutVisitor(visitorId, guardId) {
  return request({
    url: '/visitor/check-out',
    method: 'post',
    params: { visitorId, guardId }
  })
}

/**
 * 删除访客记录
 */
export function deleteVisitor(id) {
  return request({
    url: `/visitor/${id}`,
    method: 'delete'
  })
}




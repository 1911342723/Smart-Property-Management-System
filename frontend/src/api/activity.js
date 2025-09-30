import request from '@/utils/request'

// 活动管理API

/**
 * 分页查询活动列表
 */
export function getActivityPage(params) {
  return request({
    url: '/community/activities',
    method: 'get',
    params
  })
}

/**
 * 获取活动详情
 */
export function getActivityDetail(id) {
  return request({
    url: `/community/activities/${id}`,
    method: 'get'
  })
}

/**
 * 创建活动
 */
export function createActivity(data) {
  return request({
    url: '/community/activities',
    method: 'post',
    data
  })
}

/**
 * 更新活动
 */
export function updateActivity(id, data) {
  return request({
    url: `/community/activities/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除活动
 */
export function deleteActivity(id) {
  return request({
    url: `/community/activities/${id}`,
    method: 'delete'
  })
}

/**
 * 发布活动
 */
export function publishActivity(id) {
  return request({
    url: `/community/activities/${id}/status`,
    method: 'post',
    params: { status: 'PUBLISHED' }
  })
}

/**
 * 取消活动
 */
export function cancelActivity(id) {
  return request({
    url: `/community/activities/${id}/status`,
    method: 'post',
    params: { status: 'CANCELLED' }
  })
}

/**
 * 报名活动
 */
export function signUpActivity(id, data = {}) {
  return request({
    url: `/community/activities/${id}/register`,
    method: 'post',
    data
  })
}

/**
 * 取消报名
 */
export function cancelSignUp(id, reason = '') {
  return request({
    url: `/community/activities/${id}/cancel`,
    method: 'post',
    params: { reason }
  })
}

/**
 * 获取活动报名列表
 */
export function getSignUpList(activityId, params = {}) {
  return request({
    url: `/community/activities/${activityId}/registrations`,
    method: 'get',
    params
  })
}

/**
 * 获取活动统计信息
 */
export function getActivityStats() {
  return request({
    url: '/community/activities/stats',
    method: 'get'
  })
}

/**
 * 确认报名
 */
export function confirmRegistration(registrationId) {
  return request({
    url: `/community/activities/registrations/${registrationId}/confirm`,
    method: 'post'
  })
}


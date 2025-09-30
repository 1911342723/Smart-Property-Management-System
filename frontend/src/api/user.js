import request from '@/utils/request'

// 用户管理API

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/userinfo',
    method: 'get'
  })
}

/**
 * 分页查询用户列表
 */
export function getUserList(params) {
  return request({
    url: '/auth/users',
    method: 'get',
    params
  })
}

/**
 * 创建用户
 */
export function createUser(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息
 */
export function updateUser(id, data) {
  return request({
    url: `/auth/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/auth/users/${id}`,
    method: 'delete'
  })
}

/**
 * 修改密码
 */
export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/auth/change-password',
    method: 'post',
    data: { oldPassword, newPassword }
  })
}

/**
 * 重置密码
 */
export function resetPassword(userId, newPassword) {
  return request({
    url: `/auth/users/${userId}/reset-password`,
    method: 'post',
    data: { newPassword }
  })
}

/**
 * 获取用户统计信息
 */
export function getUserStats() {
  return request({
    url: '/auth/stats',
    method: 'get'
  })
}




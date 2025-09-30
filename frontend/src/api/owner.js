import request from '@/utils/request'

// 业主管理API

/**
 * 获取业主列表（分页）
 */
export function getOwnerList(params) {
  return request({
    url: '/auth/users',
    method: 'get',
    params: {
      role: 'OWNER',
      ...params
    }
  })
}

/**
 * 获取业主统计信息
 */
export function getOwnerStats() {
  return request({
    url: '/owner/stats',
    method: 'get'
  })
}

/**
 * 添加业主
 */
export function createOwner(data) {
  return request({
    url: '/auth/users',
    method: 'post',
    data: {
      ...data,
      userType: 'OWNER'
    }
  })
}

/**
 * 更新业主信息
 */
export function updateOwner(id, data) {
  return request({
    url: `/auth/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除业主
 */
export function deleteOwner(id) {
  return request({
    url: `/auth/users/${id}`,
    method: 'delete'
  })
}

/**
 * 重置业主密码
 */
export function resetOwnerPassword(userId, newPassword) {
  return request({
    url: `/auth/users/${userId}/reset-password`,
    method: 'post',
    data: { newPassword }
  })
}

/**
 * 获取业主房屋信息列表
 */
export function getUserRooms(userId) {
  return request({
    url: `/owner/rooms/${userId}`,
    method: 'get'
  })
}

/**
 * 获取当前用户房屋信息
 */
export function getMyRooms() {
  return request({
    url: '/owner/my-rooms',
    method: 'get'
  })
}

/**
 * 获取房屋详情
 */
export function getRoomDetail(roomId) {
  return request({
    url: `/owner/rooms/detail/${roomId}`,
    method: 'get'
  })
}

/**
 * 更新房屋信息
 */
export function updateRoom(roomId, data) {
  return request({
    url: `/owner/rooms/${roomId}`,
    method: 'put',
    data
  })
}

/**
 * 验证房屋所有权
 */
export function verifyRoomOwnership(roomId) {
  return request({
    url: `/owner/rooms/${roomId}/verify`,
    method: 'get'
  })
}

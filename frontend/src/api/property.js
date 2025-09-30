import request from '@/utils/request'

// 房产管理API

/**
 * 获取房产列表（分页）
 */
export function getPropertyList(params) {
  return request({
    url: '/property/list',
    method: 'get',
    params
  })
}

/**
 * 获取房产详情
 */
export function getPropertyDetail(id) {
  return request({
    url: `/property/${id}`,
    method: 'get'
  })
}

/**
 * 添加房产
 */
export function createProperty(data) {
  return request({
    url: '/property',
    method: 'post',
    data
  })
}

/**
 * 更新房产信息
 */
export function updateProperty(id, data) {
  return request({
    url: `/property/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除房产
 */
export function deleteProperty(id) {
  return request({
    url: `/property/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除房产
 */
export function batchDeleteProperty(ids) {
  return request({
    url: '/property/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 绑定业主
 */
export function bindOwner(propertyId, ownerId, relationType = 'OWNER') {
  return request({
    url: '/property/bind',
    method: 'post',
    params: {
      propertyId,
      ownerId,
      relationType
    }
  })
}

/**
 * 解绑业主
 */
export function unbindOwner(propertyId, ownerId) {
  return request({
    url: '/property/unbind',
    method: 'post',
    params: {
      propertyId,
      ownerId
    }
  })
}

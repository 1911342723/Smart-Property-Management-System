import request from '@/utils/request'

// 公告管理API

/**
 * 分页查询公告列表
 */
export function getAnnouncementPage(params) {
  return request({
    url: '/announcement/page',
    method: 'get',
    params
  })
}

/**
 * 获取公告统计信息
 */
export function getAnnouncementStats() {
  return request({
    url: '/announcement/stats',
    method: 'get'
  })
}

/**
 * 创建公告
 */
export function createAnnouncement(data) {
  return request({
    url: '/announcement',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 */
export function updateAnnouncement(id, data) {
  return request({
    url: `/announcement/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取公告详情
 */
export function getAnnouncementDetail(id) {
  return request({
    url: `/announcement/${id}`,
    method: 'get'
  })
}

/**
 * 发布公告
 */
export function publishAnnouncement(id) {
  return request({
    url: `/announcement/${id}/publish`,
    method: 'post'
  })
}

/**
 * 下线公告
 */
export function offlineAnnouncement(id) {
  return request({
    url: `/announcement/${id}/offline`,
    method: 'post'
  })
}

/**
 * 置顶/取消置顶公告
 */
export function toggleTopAnnouncement(id) {
  return request({
    url: `/announcement/${id}/toggle-top`,
    method: 'post'
  })
}

/**
 * 删除公告
 */
export function deleteAnnouncement(id) {
  return request({
    url: `/announcement/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除公告
 */
export function batchDeleteAnnouncements(ids) {
  return request({
    url: '/announcement/batch',
    method: 'delete',
    data: ids
  })
}





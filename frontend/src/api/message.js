import request from '@/utils/request'

// 消息管理API

/**
 * 分页查询消息列表
 */
export function getMessageList(params) {
  return request({
    url: '/message/list',
    method: 'get',
    params
  })
}

/**
 * 发送消息
 */
export function sendMessage(data) {
  return request({
    url: '/message',
    method: 'post',
    data
  })
}

/**
 * 标记消息为已读
 */
export function markAsRead(id) {
  return request({
    url: `/message/${id}/read`,
    method: 'put'
  })
}

/**
 * 批量标记消息为已读
 */
export function batchMarkAsRead(messageIds) {
  return request({
    url: '/message/batch-read',
    method: 'put',
    data: messageIds
  })
}

/**
 * 获取未读消息统计
 */
export function getUnreadCount() {
  return request({
    url: '/message/unread-count',
    method: 'get'
  })
}

/**
 * 发送系统消息
 */
export function sendSystemMessage(title, content, receiverId) {
  return request({
    url: '/message/system',
    method: 'post',
    params: { title, content, receiverId }
  })
}

/**
 * 发送公告消息
 */
export function sendNoticeMessage(title, content, receiverIds) {
  return request({
    url: '/message/notice',
    method: 'post',
    params: { title, content },
    data: receiverIds
  })
}



import request from '@/utils/request'

// 支付记录管理API

/**
 * 分页查询支付记录列表
 */
export function getPaymentList(params) {
  return request({
    url: '/payment/list',
    method: 'get',
    params
  })
}

/**
 * 获取支付记录详情
 */
export function getPaymentDetail(id) {
  return request({
    url: `/payment/${id}`,
    method: 'get'
  })
}

/**
 * 创建支付记录
 */
export function createPayment(data) {
  return request({
    url: '/payment',
    method: 'post',
    data
  })
}

/**
 * 更新支付记录
 */
export function updatePayment(id, data) {
  return request({
    url: `/payment/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除支付记录
 */
export function deletePayment(id) {
  return request({
    url: `/payment/${id}`,
    method: 'delete'
  })
}






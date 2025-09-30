import request from '@/utils/request'

// 账单管理API

/**
 * 分页查询账单列表
 */
export function getBillList(params) {
  return request({
    url: '/bill/list',
    method: 'get',
    params
  })
}

/**
 * 获取账单详情
 */
export function getBillDetail(id) {
  return request({
    url: `/bill/${id}`,
    method: 'get'
  })
}

/**
 * 创建账单
 */
export function createBill(data) {
  return request({
    url: '/bill',
    method: 'post',
    data
  })
}

/**
 * 批量创建账单
 */
export function createBillsBatch(data) {
  return request({
    url: '/bill/batch',
    method: 'post',
    data
  })
}

/**
 * 缴费
 */
export function payBill(billId, paymentAmount, paymentMethod) {
  return request({
    url: '/bill/pay',
    method: 'post',
    params: { billId, paymentAmount, paymentMethod }
  })
}

/**
 * 更新账单
 */
export function updateBill(id, data) {
  return request({
    url: `/bill/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除账单
 */
export function deleteBill(id) {
  return request({
    url: `/bill/${id}`,
    method: 'delete'
  })
}

/**
 * 获取账单统计信息
 */
export function getBillStats(ownerId) {
  return request({
    url: '/bill/stats',
    method: 'get',
    params: ownerId ? { ownerId } : {}
  })
}

/**
 * 获取未缴账单
 */
export function getUnpaidBills(ownerId) {
  return request({
    url: '/bill/unpaid',
    method: 'get',
    params: { ownerId }
  })
}

/**
 * 导出账单
 */
export function exportBills(params) {
  return request({
    url: '/bill/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}



import request from '@/utils/request'

// 系统配置API

/**
 * 获取所有系统配置
 */
export function getSystemConfig() {
  return request({
    url: '/system/config',
    method: 'get'
  })
}

/**
 * 获取指定配置
 */
export function getConfigByKey(key) {
  return request({
    url: `/system/config/${key}`,
    method: 'get'
  })
}

/**
 * 更新系统配置
 */
export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

/**
 * 批量更新配置
 */
export function batchUpdateConfig(data) {
  return request({
    url: '/system/config/batch',
    method: 'put',
    data
  })
}

/**
 * 重置配置为默认值
 */
export function resetConfig(key) {
  return request({
    url: `/system/config/${key}/reset`,
    method: 'put'
  })
}

/**
 * 获取物业费配置
 */
export function getPropertyFeeConfig() {
  return request({
    url: '/system/config/property-fee',
    method: 'get'
  })
}

/**
 * 更新物业费配置
 */
export function updatePropertyFeeConfig(data) {
  return request({
    url: '/system/config/property-fee',
    method: 'put',
    data
  })
}

/**
 * 获取邮件配置
 */
export function getEmailConfig() {
  return request({
    url: '/system/config/email',
    method: 'get'
  })
}

/**
 * 更新邮件配置
 */
export function updateEmailConfig(data) {
  return request({
    url: '/system/config/email',
    method: 'put',
    data
  })
}

/**
 * 测试邮件配置
 */
export function testEmailConfig(data) {
  return request({
    url: '/system/config/email/test',
    method: 'post',
    data
  })
}

/**
 * 网络请求工具类
 * 统一处理API请求、错误处理、拦截器等
 */

const app = getApp()
const config = require('../config/index.js')

// API基础配置
const CONFIG = {
  baseURL: config.api.baseURL,
  timeout: config.api.timeout,
  header: {
    'Content-Type': 'application/json'
  }
}

/**
 * 基础请求方法
 * @param {Object} options 请求配置
 * @returns {Promise}
 */
function request(options) {
  return new Promise((resolve, reject) => {
    // 显示loading
    if (options.loading !== false) {
      wx.showLoading({
        title: options.loadingText || '加载中...',
        mask: true
      })
    }

    // 构建请求配置
    const config = {
      url: CONFIG.baseURL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        ...CONFIG.header,
        ...options.header
      },
      timeout: options.timeout || CONFIG.timeout
    }

    // 添加token
    const token = wx.getStorageSync('token')
    if (token) {
      config.header['Authorization'] = `Bearer ${token}`
    }

    // 发起请求
    console.log('发送请求:', config)
    
    wx.request({
      ...config,
      success: (res) => {
        console.log('API Response:', res)
        
        if (res.statusCode >= 200 && res.statusCode < 300) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else {
            const error = new Error(res.data.message || '请求失败')
            error.code = res.data.code
            reject(error)
          }
        } else if (res.statusCode === 401) {
          // token过期，清除登录状态
          console.error('401认证失败，可能是token过期或无效')
          clearAuth()
          wx.showModal({
            title: '登录过期',
            content: '您的登录已过期，请重新登录',
            showCancel: false,
            success: () => {
              wx.reLaunch({
                url: '/pages/login/login'
              })
            }
          })
          reject(new Error('登录已过期'))
        } else if (res.statusCode === 400) {
          console.error('400错误详情:', res.data)
          const errorMsg = res.data?.message || res.data?.error || '请求参数错误'
          reject(new Error(`请求失败：${errorMsg}`))
        } else if (res.statusCode === 403) {
          console.error('403权限不足:', res.data)
          reject(new Error('权限不足，无法访问该资源'))
        } else {
          console.error(`${res.statusCode}错误详情:`, res.data)
          reject(new Error(`请求失败：${res.statusCode}${res.data?.message ? ' - ' + res.data.message : ''}`))
        }
      },
      fail: (error) => {
        console.error('Request failed:', error)
        let errorMessage = '网络请求失败'
        
        if (error.errMsg) {
          if (error.errMsg.includes('timeout')) {
            errorMessage = '请求超时，请检查网络连接'
          } else if (error.errMsg.includes('fail')) {
            errorMessage = '网络连接失败，请检查网络设置'
          }
        }
        
        reject(new Error(errorMessage))
      },
      complete: () => {
        if (options.loading !== false) {
          wx.hideLoading()
        }
      }
    })
  })
}

/**
 * GET请求
 */
function get(url, params = {}, options = {}) {
  // 过滤掉undefined、null和空字符串的参数
  const filteredParams = Object.keys(params)
    .filter(key => params[key] !== undefined && params[key] !== null && params[key] !== '')
    .reduce((obj, key) => {
      obj[key] = params[key]
      return obj
    }, {})
  
  const queryString = Object.keys(filteredParams)
    .map(key => `${key}=${encodeURIComponent(filteredParams[key])}`)
    .join('&')
  
  const finalUrl = queryString ? `${url}?${queryString}` : url
  
  return request({
    url: finalUrl,
    method: 'GET',
    ...options
  })
}

/**
 * POST请求
 */
function post(url, data = {}, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * PUT请求
 */
function put(url, data = {}, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * DELETE请求
 */
function del(url, options = {}) {
  return request({
    url,
    method: 'DELETE',
    ...options
  })
}

/**
 * 文件上传
 */
function upload(url, filePath, formData = {}, options = {}) {
  return new Promise((resolve, reject) => {
    if (options.loading !== false) {
      wx.showLoading({
        title: '上传中...',
        mask: true
      })
    }

    const token = wx.getStorageSync('token')
    const header = {}
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    wx.uploadFile({
      url: CONFIG.baseURL + url,
      filePath,
      name: 'file',
      formData,
      header,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (e) {
          reject(new Error('响应数据解析失败'))
        }
      },
      fail: (error) => {
        reject(new Error('上传失败'))
      },
      complete: () => {
        if (options.loading !== false) {
          wx.hideLoading()
        }
      }
    })
  })
}

/**
 * 清除认证信息
 */
function clearAuth() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
  
  if (app && app.globalData) {
    app.globalData.isLoggedIn = false
    app.globalData.token = ''
    app.globalData.userInfo = {}
  }
}

/**
 * 检查网络状态
 */
function checkNetwork() {
  return new Promise((resolve, reject) => {
    wx.getNetworkType({
      success: (res) => {
        if (res.networkType === 'none') {
          reject(new Error('网络连接不可用'))
        } else {
          resolve(res.networkType)
        }
      },
      fail: () => {
        reject(new Error('获取网络状态失败'))
      }
    })
  })
}

/**
 * 错误处理
 */
function handleError(error, showToast = true) {
  console.error('API Error:', error)
  
  if (showToast) {
    wx.showToast({
      title: error.message || '操作失败',
      icon: 'none',
      duration: 2000
    })
  }
  
  return error
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  upload,
  clearAuth,
  checkNetwork,
  handleError,
  CONFIG
}

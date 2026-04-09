// pages/test/api-test.js
// 用于测试API连接的页面
const api = require('../../utils/api.js')
const config = require('../../config/index.js')

Page({
  data: {
    testResults: [],
    testing: false,
    apiBaseUrl: config.api.baseURL
  },

  onLoad() {
    console.log('API测试页面加载')
    console.log('当前API地址:', this.data.apiBaseUrl)
  },

  // 测试系统健康检查
  async testHealthCheck() {
    this.addTestResult('系统健康检查', '测试中...')
    
    try {
      const response = await api.healthCheck()
      this.updateTestResult('系统健康检查', '成功', response)
    } catch (error) {
      this.updateTestResult('系统健康检查', '失败', error.message)
    }
  },

  // 测试登录API
  async testLogin() {
    this.addTestResult('登录测试', '测试中...')
    
    try {
      const response = await api.login({
        username: 'admin',
        password: '123456',
        userType: 'ADMIN'
      })
      this.updateTestResult('登录测试', '成功', response)
    } catch (error) {
      this.updateTestResult('登录测试', '失败', error.message)
    }
  },

  // 测试获取工单列表
  async testWorkOrderList() {
    this.addTestResult('工单列表测试', '测试中...')
    
    try {
      const response = await api.getWorkOrderList({
        pageNum: 1,
        pageSize: 5
      })
      this.updateTestResult('工单列表测试', '成功', response)
    } catch (error) {
      this.updateTestResult('工单列表测试', '失败', error.message)
    }
  },

  // 测试获取用户信息
  async testUserInfo() {
    this.addTestResult('用户信息测试', '测试中...')
    
    try {
      const response = await api.getUserInfo()
      this.updateTestResult('用户信息测试', '成功', response)
    } catch (error) {
      this.updateTestResult('用户信息测试', '失败', error.message)
    }
  },

  // 运行所有测试
  async runAllTests() {
    if (this.data.testing) return
    
    this.setData({
      testing: true,
      testResults: []
    })

    await this.testHealthCheck()
    await this.delay(1000)
    
    await this.testLogin()
    await this.delay(1000)
    
    await this.testUserInfo()
    await this.delay(1000)
    
    await this.testWorkOrderList()
    
    this.setData({ testing: false })
    
    wx.showToast({
      title: '测试完成',
      icon: 'success'
    })
  },

  // 清除测试结果
  clearResults() {
    this.setData({
      testResults: []
    })
  },

  // 复制API地址
  copyApiUrl() {
    wx.setClipboardData({
      data: this.data.apiBaseUrl,
      success: () => {
        wx.showToast({
          title: '已复制到剪贴板',
          icon: 'success'
        })
      }
    })
  },

  // 添加测试结果
  addTestResult(name, status, data = null) {
    const testResults = this.data.testResults
    testResults.push({
      id: Date.now(),
      name,
      status,
      data,
      timestamp: new Date().toLocaleTimeString()
    })
    
    this.setData({ testResults })
  },

  // 更新测试结果
  updateTestResult(name, status, data = null) {
    const testResults = this.data.testResults
    const index = testResults.findIndex(item => item.name === name)
    
    if (index !== -1) {
      testResults[index].status = status
      testResults[index].data = data
      testResults[index].timestamp = new Date().toLocaleTimeString()
      
      this.setData({ testResults })
    }
  },

  // 查看测试详情
  viewTestDetail(e) {
    const test = e.currentTarget.dataset.test
    
    let content = `状态: ${test.status}\n时间: ${test.timestamp}`
    
    if (test.data) {
      if (typeof test.data === 'object') {
        content += `\n响应: ${JSON.stringify(test.data, null, 2)}`
      } else {
        content += `\n响应: ${test.data}`
      }
    }
    
    wx.showModal({
      title: test.name,
      content: content,
      showCancel: false
    })
  },

  // 延迟函数
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
})







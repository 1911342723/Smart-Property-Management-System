// pages/test/network-test.js
// 网络连接诊断工具

const api = require('../../utils/api.js')
const config = require('../../config/index.js')

Page({
  data: {
    apiBaseURL: config.api.baseURL,
    testResults: [],
    testing: false,
    networkType: '',
    userInfo: {},
    domainStatus: {
      isValid: false,
      inDevTools: false,
      canBypass: false
    }
  },

  onLoad() {
    console.log('网络诊断页面加载')
    this.checkNetworkType()
    this.loadUserInfo()
    this.checkDomainValidity()
  },

  // 检查网络类型
  checkNetworkType() {
    wx.getNetworkType({
      success: (res) => {
        this.setData({
          networkType: res.networkType
        })
        console.log('网络类型:', res.networkType)
      }
    })
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {}
    const token = wx.getStorageSync('token') || ''
    
    this.setData({
      userInfo: {
        ...userInfo,
        hasToken: !!token,
        token: token ? token.substring(0, 20) + '...' : '无'
      }
    })
  },

  // 检查域名合法性
  checkDomainValidity() {
    const isValid = config.isValidDomain(this.data.apiBaseURL)
    const inDevTools = config.isInDevTools()
    const canBypass = inDevTools && !isValid
    
    this.setData({
      domainStatus: {
        isValid,
        inDevTools,
        canBypass
      }
    })

    // 显示域名状态提示
    if (!isValid) {
      if (canBypass) {
        console.warn('当前使用HTTP域名，仅在开发者工具中可用')
      } else {
        console.error('域名不合法！小程序仅支持HTTPS域名')
        wx.showModal({
          title: '域名配置错误',
          content: '当前API地址不符合小程序要求，请使用HTTPS域名或在开发者工具中关闭域名检查',
          showCancel: false
        })
      }
    }
  },

  // 运行所有测试
  async runAllTests() {
    if (this.data.testing) return
    
    this.setData({
      testing: true,
      testResults: []
    })

    // 首先检查域名状态
    await this.testDomainValidity()
    await this.delay(500)

    await this.testBasicNetwork()
    await this.delay(500)
    
    await this.testAPIConnection()
    await this.delay(500)
    
    await this.testHealthCheck()
    await this.delay(500)
    
    await this.testWorkOrderAPI()
    await this.delay(500)
    
    await this.testLoginAPI()
    await this.delay(500)
    
    this.setData({ testing: false })
    
    wx.showToast({
      title: '诊断完成',
      icon: 'success'
    })
  },

  // 测试域名合法性
  async testDomainValidity() {
    this.addTestResult('域名检查', '检查中...')
    
    const { isValid, inDevTools, canBypass } = this.data.domainStatus
    const currentEnv = config.CURRENT_ENV
    
    if (isValid) {
      this.updateTestResult('域名检查', `✅ 域名合法 (${this.data.apiBaseURL})`)
    } else {
      if (canBypass) {
        this.updateTestResult('域名检查', `⚠️ HTTP域名，仅开发工具可用 (环境: ${currentEnv})`)
      } else {
        this.updateTestResult('域名检查', `❌ 域名不合法！需要HTTPS域名 (环境: ${currentEnv})`)
      }
    }
    
    // 环境提示
    const envInfo = `当前环境: ${currentEnv}\n开发工具: ${inDevTools ? '是' : '否'}\n域名: ${this.data.apiBaseURL}`
    this.addTestResult('环境信息', envInfo)
  },

  // 测试基础网络连接
  async testBasicNetwork() {
    this.addTestResult('基础网络检查', '检查中...')
    
    try {
      // 测试微信API
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'https://www.baidu.com',
          method: 'GET',
          timeout: 5000,
          success: resolve,
          fail: reject
        })
      })
      
      this.updateTestResult('基础网络检查', '✅ 网络连接正常')
    } catch (error) {
      this.updateTestResult('基础网络检查', `❌ 网络连接失败: ${error.errMsg}`)
    }
  },

  // 测试API服务器连接
  async testAPIConnection() {
    this.addTestResult('API服务器连接', '检查中...')
    
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: this.data.apiBaseURL + '/test/health',
          method: 'GET',
          timeout: 5000,
          success: resolve,
          fail: reject
        })
      })
      
      if (res.statusCode === 200) {
        this.updateTestResult('API服务器连接', '✅ API服务器连接正常')
      } else {
        this.updateTestResult('API服务器连接', `❌ API服务器响应异常: ${res.statusCode}`)
      }
    } catch (error) {
      this.updateTestResult('API服务器连接', `❌ 无法连接API服务器: ${error.errMsg}`)
    }
  },

  // 测试健康检查API
  async testHealthCheck() {
    this.addTestResult('健康检查API', '检查中...')
    
    try {
      const response = await api.healthCheck()
      this.updateTestResult('健康检查API', `✅ 健康检查成功: ${response.message}`)
    } catch (error) {
      this.updateTestResult('健康检查API', `❌ 健康检查失败: ${error.message}`)
    }
  },

  // 测试工单API
  async testWorkOrderAPI() {
    this.addTestResult('工单API测试', '检查中...')
    
    try {
      const response = await api.getWorkOrderList({
        pageNum: 1,
        pageSize: 1
      })
      this.updateTestResult('工单API测试', `✅ 工单API正常，数据总数: ${response.data?.total || 0}`)
    } catch (error) {
      this.updateTestResult('工单API测试', `❌ 工单API失败: ${error.message}`)
    }
  },

  // 测试特定工单详情
  async testWorkOrderDetail() {
    this.setData({ testing: true })
    this.addTestResult('工单详情测试', '检查中...')
    
    try {
      // 先获取工单列表
      const listResponse = await api.getWorkOrderList({
        pageNum: 1,
        pageSize: 1
      })
      
      if (listResponse.data?.list?.length > 0) {
        const workOrderId = listResponse.data.list[0].id
        const detailResponse = await api.getWorkOrderDetail(workOrderId)
        this.updateTestResult('工单详情测试', `✅ 工单详情获取成功: ${detailResponse.data.title}`)
      } else {
        this.updateTestResult('工单详情测试', '❌ 没有可用的工单数据')
      }
    } catch (error) {
      this.updateTestResult('工单详情测试', `❌ 工单详情获取失败: ${error.message}`)
    } finally {
      this.setData({ testing: false })
    }
  },

  // 测试登录API
  async testLoginAPI() {
    this.addTestResult('登录API测试', '检查中...')
    
    try {
      // 测试演示账号
      const testAccounts = [
        { username: 'owner001', userType: 'OWNER', role: '业主' },
        { username: 'worker001', userType: 'WORKER', role: '维修工' },
        { username: 'guard001', userType: 'GUARD', role: '保安' }
      ]
      
      let successCount = 0
      let errorDetails = []
      
      for (const account of testAccounts) {
        try {
          const response = await api.login({
            username: account.username,
            password: '123456',
            userType: account.userType
          })
          
          if (response.code === 200) {
            successCount++
          } else {
            errorDetails.push(`${account.role}(${account.username}): ${response.message}`)
          }
        } catch (error) {
          errorDetails.push(`${account.role}(${account.username}): ${error.message}`)
        }
      }
      
      if (successCount === testAccounts.length) {
        this.updateTestResult('登录API测试', `✅ 所有测试账号登录正常 (${successCount}/${testAccounts.length})`)
      } else {
        this.updateTestResult('登录API测试', `⚠️ 部分账号登录失败 (${successCount}/${testAccounts.length})\n${errorDetails.join('\n')}`)
      }
    } catch (error) {
      this.updateTestResult('登录API测试', `❌ 登录测试失败: ${error.message}`)
    }
  },

  // 测试特定账号登录
  async testSpecificLogin() {
    this.setData({ testing: true })
    this.addTestResult('手动登录测试', '请选择测试账号...')
    
    const testAccounts = [
      { name: 'owner001(业主)', username: 'owner001', userType: 'OWNER' },
      { name: 'worker001(维修工)', username: 'worker001', userType: 'WORKER' },
      { name: 'guard001(保安)', username: 'guard001', userType: 'GUARD' },
      { name: 'admin(管理员)', username: 'admin', userType: 'ADMIN' }
    ]
    
    wx.showActionSheet({
      itemList: testAccounts.map(acc => acc.name),
      success: async (res) => {
        const selectedAccount = testAccounts[res.tapIndex]
        await this.performLoginTest(selectedAccount)
        this.setData({ testing: false })
      },
      fail: () => {
        this.setData({ testing: false })
      }
    })
  },

  async performLoginTest(account) {
    this.updateTestResult('手动登录测试', `测试 ${account.name} 登录中...`)
    
    try {
      const response = await api.login({
        username: account.username,
        password: '123456',
        userType: account.userType
      })
      
      if (response.code === 200 && response.data) {
        const userInfo = response.data
        this.updateTestResult('手动登录测试', 
          `✅ ${account.name} 登录成功\n` +
          `用户ID: ${userInfo.id}\n` +
          `真实姓名: ${userInfo.realName}\n` +
          `用户类型: ${userInfo.userType}\n` +
          `Token: ${userInfo.token.substring(0, 20)}...`)
      } else {
        this.updateTestResult('手动登录测试', 
          `❌ ${account.name} 登录失败\n` +
          `错误信息: ${response.message || '未知错误'}`)
      }
    } catch (error) {
      this.updateTestResult('手动登录测试', 
        `❌ ${account.name} 登录异常\n` +
        `错误详情: ${error.message}`)
    }
  },

  // 修复网络问题
  async fixNetworkIssues() {
    wx.showLoading({
      title: '正在修复...'
    })

    try {
      // 1. 清除缓存
      wx.clearStorageSync()
      
      // 2. 重新检查网络
      await new Promise(resolve => setTimeout(resolve, 1000))
      this.checkNetworkType()
      
      // 3. 重新测试
      await this.testAPIConnection()
      
      wx.showToast({
        title: '修复完成，请重新测试',
        icon: 'success'
      })
    } catch (error) {
      wx.showToast({
        title: '修复失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 查看配置信息
  showConfig() {
    const { isValid, inDevTools, canBypass } = this.data.domainStatus
    const configInfo = `
环境: ${config.CURRENT_ENV}
API地址: ${this.data.apiBaseURL}
域名状态: ${isValid ? '✅ 合法' : (canBypass ? '⚠️ 开发可用' : '❌ 不合法')}
开发工具: ${inDevTools ? '是' : '否'}
网络类型: ${this.data.networkType}
用户ID: ${this.data.userInfo.id || '未登录'}
Token: ${this.data.userInfo.hasToken ? '已设置' : '未设置'}
    `.trim()

    wx.showModal({
      title: '配置信息',
      content: configInfo,
      showCancel: false
    })
  },

  // 显示环境切换选项
  showEnvironmentOptions() {
    const envNames = {
      'development': '开发环境(localhost)',
      'dev_remote': '开发环境(远程)',
      'test': '测试环境',
      'production': '生产环境'
    }
    
    const actions = Object.keys(config.ENV_CONFIG).map(env => envNames[env] || env)
    
    wx.showActionSheet({
      itemList: actions,
      success: (res) => {
        const envKeys = Object.keys(config.ENV_CONFIG)
        const selectedEnv = envKeys[res.tapIndex]
        this.showEnvironmentInfo(selectedEnv)
      }
    })
  },

  // 显示环境信息
  showEnvironmentInfo(env) {
    const envConfig = config.ENV_CONFIG[env]
    const isCurrentEnv = env === config.CURRENT_ENV
    const isValid = config.isValidDomain(envConfig.baseURL)
    
    const info = `
环境: ${env}
地址: ${envConfig.baseURL}
调试: ${envConfig.debug ? '开启' : '关闭'}
域名: ${isValid ? '✅ 合法' : '❌ 不合法'}
${isCurrentEnv ? '\n🔸 当前使用的环境' : ''}

${!isValid ? '\n⚠️ 此环境需要在开发者工具中关闭域名检查' : ''}
    `.trim()

    wx.showModal({
      title: '环境信息',
      content: info,
      confirmText: isCurrentEnv ? '确定' : '切换',
      success: (res) => {
        if (res.confirm && !isCurrentEnv) {
          wx.showModal({
            title: '切换环境',
            content: '需要手动修改config/index.js中的CURRENT_ENV变量，然后重新编译',
            showCancel: false
          })
        }
      }
    })
  },

  // 复制API地址
  copyAPIUrl() {
    wx.setClipboardData({
      data: this.data.apiBaseURL,
      success: () => {
        wx.showToast({
          title: '已复制到剪贴板',
          icon: 'success'
        })
      }
    })
  },

  // 工具方法
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

  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  },

  // 查看详细结果
  viewTestDetail(e) {
    const test = e.currentTarget.dataset.test
    
    wx.showModal({
      title: test.name,
      content: `状态: ${test.status}\n时间: ${test.timestamp}`,
      showCancel: false
    })
  },

  // 清除结果
  clearResults() {
    this.setData({
      testResults: []
    })
  }
})


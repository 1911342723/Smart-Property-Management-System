// app.js
App({
  onLaunch() {
    console.log('智慧物业小程序启动')
    
    // 检查登录状态
    this.checkLoginStatus()
    
    // 获取用户信息
    this.getUserProfile()
  },
  
  onShow() {
    console.log('智慧物业小程序显示')
  },
  
  onHide() {
    console.log('智慧物业小程序隐藏')
  },
  
  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      this.globalData.isLoggedIn = true
      this.globalData.userInfo = userInfo
      this.globalData.token = token
    } else {
      this.globalData.isLoggedIn = false
    }
  },
  
  // 获取用户信息
  getUserProfile() {
    if (this.globalData.isLoggedIn) {
      return
    }
    
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          wx.getUserInfo({
            success: res => {
              this.globalData.userInfo = res.userInfo
            }
          })
        }
      }
    })
  },
  
  // 登录
  login(userInfo) {
    const api = require('/utils/api.js')
    
    return new Promise(async (resolve, reject) => {
      try {
        // 构建登录请求数据 - 直接使用手机号登录
        const loginData = {
          username: userInfo.phone,  // 直接用手机号作为用户名
          password: userInfo.password
        }
        
        // 调用真实登录API
        const response = await api.login(loginData)
        
        if (response.code === 200 && response.data) {
          const data = response.data
          
          // 保存到本地存储
          wx.setStorageSync('token', data.token)
          wx.setStorageSync('userInfo', data)
          
          // 更新全局数据
          this.globalData.isLoggedIn = true
          this.globalData.token = data.token
          this.globalData.userInfo = data
          
          resolve(data)
        } else {
          reject(new Error(response.message || '登录失败'))
        }
      } catch (error) {
        console.error('Login error:', error)
        reject(error)
      }
    })
  },
  
  // 退出登录
  logout() {
    const api = require('/utils/api.js')
    
    // 调用后端登出接口
    api.logout().catch(err => {
      console.error('Logout API error:', err)
    })
    
    // 清除本地数据
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    
    this.globalData.isLoggedIn = false
    this.globalData.token = ''
    this.globalData.userInfo = {}
    
    wx.reLaunch({
      url: '/pages/login/login'
    })
  },

  
  
  // 全局数据
  globalData: {
    isLoggedIn: false,
    userInfo: {},
    token: '',
    version: '1.0.0'
  }
})








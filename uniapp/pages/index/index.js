// pages/index/index.js
const app = getApp()

Page({
  data: {
    loadingText: '正在加载...',
    particles: []
  },

  onLoad() {
    console.log('启动页加载')
    this.initParticles()
    this.startLoadingAnimation()
    
    // 3秒后检查登录状态并跳转
    setTimeout(() => {
      this.checkAndNavigate()
    }, 3000)
  },

  // 初始化粒子
  initParticles() {
    const particles = []
    for (let i = 0; i < 20; i++) {
      const size = Math.random() * 4 + 2
      const left = Math.random() * 100
      const animationDelay = Math.random() * 3
      const animationDuration = Math.random() * 3 + 2
      
      particles.push({
        style: `width: ${size}rpx; height: ${size}rpx; left: ${left}%; animation-delay: ${animationDelay}s; animation-duration: ${animationDuration}s;`
      })
    }
    
    this.setData({
      particles
    })
  },

  // 开始加载动画
  startLoadingAnimation() {
    const loadingTexts = [
      '正在加载...',
      '初始化应用...',
      '检查更新...',
      '准备就绪...'
    ]
    
    let textIndex = 0
    const interval = setInterval(() => {
      textIndex = (textIndex + 1) % loadingTexts.length
      this.setData({
        loadingText: loadingTexts[textIndex]
      })
    }, 800)
    
    // 3秒后停止动画
    setTimeout(() => {
      clearInterval(interval)
    }, 3000)
  },

  // 检查登录状态并跳转
  checkAndNavigate() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      // 已登录，设置全局数据并跳转到对应首页
      app.globalData.isLoggedIn = true
      app.globalData.token = token
      app.globalData.userInfo = userInfo
      
      const rolePages = {
        owner: '/pages/owner/home',
        worker: '/pages/worker/dashboard',
        guard: '/pages/guard/tasks'
      }
      
      const targetPage = rolePages[userInfo.role] || '/pages/owner/home'
      
      wx.reLaunch({
        url: targetPage
      })
    } else {
      // 未登录，跳转到登录页
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  }
})
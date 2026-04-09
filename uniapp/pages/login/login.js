// pages/login/login.js
const app = getApp()

Page({
  data: {
    loginForm: {
      phone: '',
      password: ''
    },
    showPassword: false,
    loading: false,
    showDemoModal: false,
    canLogin: false,
    rememberMe: false,
    demoAccounts: [
      { phone: '13800000101', password: '123456', roleText: '业主账号' },
      { phone: '13800000301', password: '123456', roleText: '维修工账号' }
    ]
  },

  onLoad() {
    console.log('登录页加载')
    this.loadRememberedLogin()
  },

  // 加载记住的登录信息
  loadRememberedLogin() {
    const rememberedLogin = wx.getStorageSync('rememberedLogin')
    if (rememberedLogin && rememberedLogin.expireTime > Date.now()) {
      this.setData({
        'loginForm.phone': rememberedLogin.phone,
        'loginForm.password': rememberedLogin.password,
        rememberMe: true
      }, () => {
        this.checkCanLogin()
      })
    }
  },

  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      'loginForm.phone': e.detail.value
    }, () => {
      this.checkCanLogin()
    })
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      'loginForm.password': e.detail.value
    }, () => {
      this.checkCanLogin()
    })
  },

  // 检查是否可以登录
  checkCanLogin() {
    const { phone, password } = this.data.loginForm
    const phoneRegex = /^1[3-9]\d{9}$/
    const canLogin = phone && password && phoneRegex.test(phone)
    
    this.setData({
      canLogin
    })
  },

  // 切换密码显示
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  // 切换记住我
  toggleRememberMe() {
    this.setData({
      rememberMe: !this.data.rememberMe
    })
  },

  // 处理登录
  async handleLogin() {
    if (!this.data.canLogin || this.data.loading) return
    
    const { phone, password } = this.data.loginForm
    
    // 验证手机号
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      })
      return
    }
    
    this.setData({ loading: true })
    
    try {
      const result = await app.login({
        phone,
        password
      })

      // 如果选择记住我，保存登录信息
      if (this.data.rememberMe) {
        const expireTime = Date.now() + 30 * 24 * 60 * 60 * 1000 // 30天
        wx.setStorageSync('rememberedLogin', {
          phone,
          password,
          expireTime
        })
      } else {
        // 如果没选择记住我，清除之前保存的信息
        wx.removeStorageSync('rememberedLogin')
      }
      
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      })
      
      // 根据用户类型跳转到对应页面
      const rolePages = {
        'OWNER': '/pages/owner/home',
        'WORKER': '/pages/worker/dashboard', 
        'GUARD': '/pages/guard/tasks'
      }
      
      setTimeout(() => {
        // 跳转到对应页面
        wx.switchTab({
          url: rolePages[result.userType] || '/pages/owner/home'
        })
      }, 1000)
      
    } catch (error) {
      console.error('Login error:', error)
      wx.showToast({
        title: error.message || '登录失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 显示演示账号
  showDemoLogin() {
    this.setData({
      showDemoModal: true
    })
  },

  // 关闭演示账号弹窗
  closeDemoModal() {
    this.setData({
      showDemoModal: false
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空函数，阻止事件冒泡
  },

  // 使用演示账号
  useDemoAccount(e) {
    const account = e.currentTarget.dataset.account
    this.setData({
      loginForm: {
        phone: account.phone,
        password: account.password
      },
      showDemoModal: false
    }, () => {
      this.checkCanLogin()
    })
    
    wx.showToast({
      title: '已填入演示账号',
      icon: 'success'
    })
  },

  // 显示注册
  showRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    })
  },

  // 显示忘记密码
  showForgotPassword() {
    wx.navigateTo({
      url: '/pages/forgot-password/forgot-password'
    })
  },

})
// pages/register/register.js
const api = require('../../utils/api.js')

Page({
  data: {
    form: {
      realName: '',
      phone: '',
      password: '',
      confirmPassword: '',
      userType: 'OWNER'
    },
    showPassword: false,
    showConfirmPassword: false,
    loading: false,
    canRegister: false,
    userTypes: [
      { label: '业主', value: 'OWNER' },
      { label: '维修工', value: 'WORKER' }
    ],
    userTypeIndex: 0
  },

  onLoad() {
    console.log('注册页面加载')
  },

  // 真实姓名输入
  onRealNameInput(e) {
    this.setData({
      'form.realName': e.detail.value
    }, () => {
      this.checkCanRegister()
    })
  },

  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      'form.phone': e.detail.value
    }, () => {
      this.checkCanRegister()
    })
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      'form.password': e.detail.value
    }, () => {
      this.checkCanRegister()
    })
  },

  // 确认密码输入
  onConfirmPasswordInput(e) {
    this.setData({
      'form.confirmPassword': e.detail.value
    }, () => {
      this.checkCanRegister()
    })
  },

  // 用户类型选择
  onUserTypeChange(e) {
    const index = e.detail.value
    this.setData({
      userTypeIndex: index,
      'form.userType': this.data.userTypes[index].value
    })
  },

  // 切换密码显示
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  // 切换确认密码显示
  toggleConfirmPassword() {
    this.setData({
      showConfirmPassword: !this.data.showConfirmPassword
    })
  },

  // 检查是否可以注册
  checkCanRegister() {
    const { realName, phone, password, confirmPassword } = this.data.form
    const phoneRegex = /^1[3-9]\d{9}$/
    const canRegister = realName && phone && password && confirmPassword &&
                       phoneRegex.test(phone) && password.length >= 6
    
    this.setData({ canRegister })
  },

  // 处理注册
  async handleRegister() {
    if (!this.data.canRegister || this.data.loading) return

    const { realName, phone, password, confirmPassword, userType } = this.data.form

    // 验证手机号
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      })
      return
    }

    // 验证密码长度
    if (password.length < 6) {
      wx.showToast({
        title: '密码至少6位',
        icon: 'none'
      })
      return
    }

    // 验证两次密码是否一致
    if (password !== confirmPassword) {
      wx.showToast({
        title: '两次密码不一致',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })

    try {
      const result = await api.register({
        realName,
        phone,
        password,
        username: phone, // 使用手机号作为用户名
        userType,
        status: 1 // 启用状态
      })

      if (result.code === 200) {
        wx.showToast({
          title: '注册成功',
          icon: 'success'
        })

        // 1秒后跳转到登录页
        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/login/login'
          })
        }, 1000)
      } else {
        wx.showToast({
          title: result.message || '注册失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('注册失败:', error)
      wx.showToast({
        title: error.message || '注册失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 返回
  goBack() {
    wx.navigateBack()
  },

  // 跳转到登录页
  goToLogin() {
    wx.redirectTo({
      url: '/pages/login/login'
    })
  }
})

// pages/forgot-password/forgot-password.js
const api = require('../../utils/api.js')

Page({
  data: {
    form: {
      phone: '',
      newPassword: '',
      confirmPassword: ''
    },
    showPassword: false,
    showConfirmPassword: false,
    loading: false,
    canReset: false
  },

  onLoad() {
    console.log('忘记密码页面加载')
  },

  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      'form.phone': e.detail.value
    }, () => {
      this.checkCanReset()
    })
  },

  // 新密码输入
  onPasswordInput(e) {
    this.setData({
      'form.newPassword': e.detail.value
    }, () => {
      this.checkCanReset()
    })
  },

  // 确认密码输入
  onConfirmPasswordInput(e) {
    this.setData({
      'form.confirmPassword': e.detail.value
    }, () => {
      this.checkCanReset()
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

  // 检查是否可以重置
  checkCanReset() {
    const { phone, newPassword, confirmPassword } = this.data.form
    const phoneRegex = /^1[3-9]\d{9}$/
    const canReset = phone && newPassword && confirmPassword &&
                    phoneRegex.test(phone) && newPassword.length >= 6
    
    this.setData({ canReset })
  },

  // 处理重置密码
  async handleReset() {
    if (!this.data.canReset || this.data.loading) return

    const { phone, newPassword, confirmPassword } = this.data.form

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
    if (newPassword.length < 6) {
      wx.showToast({
        title: '密码至少6位',
        icon: 'none'
      })
      return
    }

    // 验证两次密码是否一致
    if (newPassword !== confirmPassword) {
      wx.showToast({
        title: '两次密码不一致',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })

    try {
      const result = await api.forgotPassword({
        phone,
        newPassword
      })

      if (result.code === 200) {
        wx.showToast({
          title: '密码重置成功',
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
          title: result.message || '重置失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('重置密码失败:', error)
      wx.showToast({
        title: error.message || '重置失败，请重试',
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

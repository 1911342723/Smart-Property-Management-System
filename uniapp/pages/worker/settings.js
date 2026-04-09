// pages/worker/settings.js
const app = getApp()

Page({
  data: {
    userInfo: null,
    settings: {
      notificationEnabled: true,
      soundEnabled: true,
      vibrationEnabled: true,
      autoAcceptOrder: false,
      workRadius: 5 // 接单范围（公里）
    },
    version: '1.0.0'
  },

  onLoad(options) {
    console.log('设置页面加载')
    this.getUserInfo()
    this.loadSettings()
  },

  // 获取用户信息
  getUserInfo() {
    const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ userInfo })
    }
  },

  // 加载设置
  loadSettings() {
    const settings = wx.getStorageSync('workerSettings')
    if (settings) {
      this.setData({ settings })
    }
  },

  // 保存设置
  saveSettings() {
    wx.setStorageSync('workerSettings', this.data.settings)
    wx.showToast({
      title: '设置已保存',
      icon: 'success'
    })
  },

  // 切换通知开关
  toggleNotification(e) {
    const enabled = e.detail.value
    this.setData({
      'settings.notificationEnabled': enabled
    })
    this.saveSettings()
  },

  // 切换声音开关
  toggleSound(e) {
    const enabled = e.detail.value
    this.setData({
      'settings.soundEnabled': enabled
    })
    this.saveSettings()
  },

  // 切换震动开关
  toggleVibration(e) {
    const enabled = e.detail.value
    this.setData({
      'settings.vibrationEnabled': enabled
    })
    this.saveSettings()
  },

  // 切换自动接单
  toggleAutoAccept(e) {
    const enabled = e.detail.value
    
    if (enabled) {
      wx.showModal({
        title: '自动接单',
        content: '开启后，系统会自动为您接收范围内的工单。请确保您有充足时间处理工单。',
        success: (res) => {
          if (res.confirm) {
            this.setData({
              'settings.autoAcceptOrder': true
            })
            this.saveSettings()
          } else {
            // 用户取消，重置开关
            this.setData({
              'settings.autoAcceptOrder': false
            })
          }
        }
      })
    } else {
      this.setData({
        'settings.autoAcceptOrder': false
      })
      this.saveSettings()
    }
  },

  // 设置接单范围
  setWorkRadius() {
    const radiusOptions = ['1公里', '3公里', '5公里', '10公里', '15公里', '不限']
    const radiusValues = [1, 3, 5, 10, 15, 999]
    
    wx.showActionSheet({
      itemList: radiusOptions,
      success: (res) => {
        const selectedRadius = radiusValues[res.tapIndex]
        this.setData({
          'settings.workRadius': selectedRadius
        })
        this.saveSettings()
        
        wx.showToast({
          title: `接单范围已设为${radiusOptions[res.tapIndex]}`,
          icon: 'success'
        })
      }
    })
  },

  // 修改密码
  changePassword() {
    wx.showModal({
      title: '修改密码',
      content: '请前往个人中心网页版修改密码，或联系管理员',
      showCancel: false
    })
  },

  // 清除缓存
  clearCache() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除所有缓存数据吗？这不会影响您的登录状态和个人设置。',
      success: (res) => {
        if (res.confirm) {
          try {
            // 保留登录信息和设置
            const userInfo = wx.getStorageSync('userInfo')
            const token = wx.getStorageSync('token')
            const settings = wx.getStorageSync('workerSettings')
            
            // 清除所有缓存
            wx.clearStorageSync()
            
            // 恢复登录信息和设置
            if (userInfo) wx.setStorageSync('userInfo', userInfo)
            if (token) wx.setStorageSync('token', token)
            if (settings) wx.setStorageSync('workerSettings', settings)
            
            wx.showToast({
              title: '缓存已清除',
              icon: 'success'
            })
          } catch (error) {
            console.error('清除缓存失败:', error)
            wx.showToast({
              title: '清除失败',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  // 关于我们
  aboutUs() {
    wx.showModal({
      title: '智慧物业',
      content: `版本：${this.data.version}\n\n智慧物业维修工端，为维修师傅提供便捷的工单管理和服务支持。\n\n© 2025 智慧物业`,
      showCancel: false
    })
  },

  // 用户协议
  viewAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '正在打开用户协议...',
      showCancel: false
    })
  },

  // 隐私政策
  viewPrivacy() {
    wx.showModal({
      title: '隐私政策',
      content: '正在打开隐私政策...',
      showCancel: false
    })
  },

  // 意见反馈
  feedback() {
    wx.navigateTo({
      url: '/pages/common/feedback',
      fail: () => {
        wx.showModal({
          title: '意见反馈',
          content: '感谢您的反馈！请联系客服：400-123-4567',
          showCancel: false
        })
      }
    })
  },

  // 联系客服
  contactService() {
    wx.showActionSheet({
      itemList: ['拨打客服电话', '在线客服'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: '400-123-4567'
          })
        } else if (res.tapIndex === 1) {
          wx.navigateTo({
            url: '/pages/common/ai-chat'
          })
        }
      }
    })
  }
})

// 设置页面逻辑
Page({
  data: {
    settings: {
      systemNotification: true,
      workReminder: true,
      autoLogin: false
    }
  },

  onLoad() {
    this.loadSettings()
  },

  // 加载设置
  loadSettings() {
    const settings = wx.getStorageSync('guardSettings') || this.data.settings
    this.setData({ settings })
  },

  // 切换设置
  toggleSetting(e) {
    const key = e.currentTarget.dataset.key
    const value = e.detail.value
    
    this.setData({
      [`settings.${key}`]: value
    })
    
    // 保存到本地存储
    wx.setStorageSync('guardSettings', this.data.settings)
    
    wx.showToast({
      title: '设置已保存',
      icon: 'success'
    })
  },

  // 编辑个人资料
  editProfile() {
    wx.navigateTo({
      url: '/pages/guard/edit-profile'
    })
  },

  // 清理缓存
  clearCache() {
    wx.showModal({
      title: '清理缓存',
      content: '确定要清理应用缓存吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '清理中...' })
          
          setTimeout(() => {
            wx.hideLoading()
            wx.showToast({
              title: '缓存清理完成',
              icon: 'success'
            })
          }, 2000)
        }
      }
    })
  },

  // 检查更新
  checkUpdate() {
    wx.showLoading({ title: '检查中...' })
    
    setTimeout(() => {
      wx.hideLoading()
      wx.showToast({
        title: '已是最新版本',
        icon: 'success'
      })
    }, 2000)
  },

  // 关于我们
  showAbout() {
    wx.showModal({
      title: '关于智慧物业',
      content: '版本：v1.0.0\n\n智慧物业管理系统，提供完整的物业服务解决方案。',
      showCancel: false
    })
  },

  // 意见反馈
  contactSupport() {
    wx.showActionSheet({
      itemList: ['电话联系', '邮件反馈', '在线客服'],
      success: (res) => {
        switch (res.tapIndex) {
          case 0:
            wx.makePhoneCall({
              phoneNumber: '400-123-4567'
            })
            break
          case 1:
            wx.setClipboardData({
              data: 'support@property.com',
              success: () => {
                wx.showToast({
                  title: '邮箱已复制',
                  icon: 'success'
                })
              }
            })
            break
          case 2:
            wx.showToast({
              title: '功能开发中',
              icon: 'none'
            })
            break
        }
      }
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('userInfo')
          wx.removeStorageSync('token')
          
          wx.reLaunch({
            url: '/pages/login/login'
          })
        }
      }
    })
  }
})











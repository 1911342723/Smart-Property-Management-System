// pages/owner/profile.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: {},
    userStats: {
      repairs: 0,
      payments: 0,
      complaints: 0,
      visitors: 0,
      activities: 0
    },
    loading: false
  },

  onLoad() {
    console.log('个人中心页面加载')
    this.loadUserInfo()
    this.loadUserStats()
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(3)
    }
  },

  async loadUserInfo() {
    try {
      this.setData({ loading: true })
      
      // 从缓存中获取用户信息
      const cachedUserInfo = app.globalData.userInfo || {}
      
      if (cachedUserInfo.id) {
        // 如果有缓存的用户信息，先显示缓存数据
        this.displayUserInfo(cachedUserInfo)
      }
      
      // 检查登录状态
      const token = wx.getStorageSync('token')
      
      if (!token) {
        wx.reLaunch({
          url: '/pages/login/login'
        })
        return
      }
      
      // 从API获取最新用户信息
      const response = await api.getUserInfo()
      
      if (response.code === 200 && response.data) {
        const userInfo = response.data
        
        // 更新全局数据
        app.globalData.userInfo = userInfo
        wx.setStorageSync('userInfo', userInfo)
        
        // 显示最新用户信息
        this.displayUserInfo(userInfo)
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      
      // 如果是401错误，可能需要重新登录
      if (error.message && error.message.includes('用户不存在')) {
        wx.showModal({
          title: '用户信息异常',
          content: '用户信息可能已失效，是否重新登录？',
          confirmText: '重新登录',
          cancelText: '稍后再试',
          success: (res) => {
            if (res.confirm) {
              wx.reLaunch({
                url: '/pages/login/login'
              })
            } else {
              // 使用缓存数据
              const cachedUserInfo = app.globalData.userInfo || {}
              this.displayUserInfo(cachedUserInfo)
            }
          }
        })
      } else {
        // 其他错误，使用缓存数据
        const cachedUserInfo = app.globalData.userInfo || {}
        this.displayUserInfo(cachedUserInfo)
        
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        })
      }
    } finally {
      this.setData({ loading: false })
    }
  },

  displayUserInfo(userInfo) {
    const userTypeMap = {
      'OWNER': '业主',
      'WORKER': '维修工',
      'GUARD': '保安',
      'ADMIN': '管理员'
    }
    
    const genderMap = {
      'MALE': '男',
      'FEMALE': '女',
      'OTHER': '其他'
    }
    
    this.setData({
      userInfo: {
        id: userInfo.id,
        name: userInfo.realName || userInfo.username || '用户',
        avatar: userInfo.avatar || '/static/images/default-avatar.png',
        roleText: userTypeMap[userInfo.userType] || '用户',
        phone: userInfo.phone,
        email: userInfo.email,
        gender: genderMap[userInfo.gender] || '',
        birthday: userInfo.birthday || '',
        signature: userInfo.signature || '',
        emergencyContact: userInfo.emergencyContact || '',
        emergencyPhone: userInfo.emergencyPhone || ''
      }
    })
  },

  async loadUserStats() {
    try {
      const response = await api.getUserStats()
      if (response.code === 200 && response.data) {
        const stats = response.data
        this.setData({
          userStats: {
            repairs: stats.repairCount || 0,
            payments: stats.paymentCount || 0,
            complaints: stats.complaintCount || 0,
            visitors: stats.visitorCount || 0,
            activities: stats.activityCount || 0
          }
        })
      }
    } catch (error) {
      console.error('获取统计数据失败:', error)
      // 如果API失败，显示默认值
      this.setData({
        userStats: {
          repairs: 0,
          payments: 0,
          complaints: 0,
          visitors: 0,
          activities: 0
        }
      })
      
      wx.showToast({
        title: '获取统计数据失败',
        icon: 'none'
      })
    }
  },

  editProfile() {
    console.log('点击编辑资料按钮')
    // 导航到编辑资料页面
    wx.navigateTo({
      url: '/pages/owner/edit-profile',
      success: () => {
        console.log('导航到编辑资料页面成功')
      },
      fail: (error) => {
        console.error('导航到编辑资料页面失败:', error)
        wx.showToast({
          title: '页面跳转失败: ' + error.errMsg,
          icon: 'none'
        })
      }
    })
  },

  houseInfo() {
    wx.navigateTo({
      url: '/pages/owner/house-info'
    })
  },

  repairHistory() {
    wx.navigateTo({
      url: '/pages/owner/repair-list'
    })
  },

  paymentHistory() {
    wx.navigateTo({
      url: '/pages/owner/payment-history'
    })
  },

  complaintHistory() {
    wx.navigateTo({
      url: '/pages/owner/complaint-list'
    })
  },

  settings() {
    wx.showModal({
      title: '功能开发中',
      content: '通用设置功能正在开发中，敬请期待',
      showCancel: false,
      confirmText: '知道了'
    })
  },

  feedback() {
    wx.navigateTo({
      url: '/pages/owner/complaint-form?type=feedback'
    })
  },

  about() {
    wx.showModal({
      title: '关于智慧物业',
      content: '智慧物业管理系统 v1.0.0\n\n让物业管理更智能\n\n© 2024 智慧物业团队',
      showCancel: false,
      confirmText: '确定'
    })
  },

  logout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
        }
      }
    })
  }
})
// pages/guard/scan-visitor.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    visitorInfo: null,
    scanning: false,
    verifying: false
  },

  onLoad(options) {
    console.log('保安扫码页面加载')
  },

  onShow() {
    // 开始扫码
    this.startScan()
  },

  // 开始扫码
  startScan() {
    if (this.data.scanning) return
    
    this.setData({ scanning: true })
    
    wx.scanCode({
      success: (res) => {
        console.log('扫码成功:', res.result)
        this.verifyVisitor(res.result)
      },
      fail: (err) => {
        console.error('扫码失败:', err)
        wx.showToast({
          title: '扫码失败',
          icon: 'none'
        })
        this.setData({ scanning: false })
      }
    })
  },

  // 验证访客
  async verifyVisitor(qrContent) {
    if (this.data.verifying) return
    
    this.setData({ verifying: true })
    
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || userInfo.userType !== 'GUARD') {
        wx.showToast({
          title: '权限不足',
          icon: 'none'
        })
        return
      }

      const response = await api.scanVerifyVisitor(qrContent, userInfo.id)
      
      if (response.code === 200) {
        this.setData({
          visitorInfo: response.data,
          scanning: false
        })
        
        wx.showToast({
          title: '验证成功',
          icon: 'success'
        })
      } else {
        wx.showModal({
          title: '验证失败',
          content: response.message || '无效的访客二维码',
          showCancel: false
        })
        
        this.setData({ scanning: false })
      }
    } catch (error) {
      console.error('验证访客失败:', error)
      wx.showToast({
        title: '验证失败，请重试',
        icon: 'none'
      })
      
      this.setData({ scanning: false })
    } finally {
      this.setData({ verifying: false })
    }
  },

  // 确认签到
  async confirmCheckIn() {
    const { visitorInfo } = this.data
    if (!visitorInfo) return

    try {
      const userInfo = app.globalData.userInfo
      const response = await api.checkInVisitor(visitorInfo.id, userInfo.id)
      
      if (response.code === 200) {
        wx.showToast({
          title: '签到成功',
          icon: 'success'
        })
        
        // 更新访客状态
        this.setData({
          'visitorInfo.status': 'ARRIVED',
          'visitorInfo.actualArrival': new Date().toISOString()
        })
      } else {
        wx.showToast({
          title: response.message || '签到失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('签到失败:', error)
      wx.showToast({
        title: '签到失败，请重试',
        icon: 'none'
      })
    }
  },

  // 确认签退
  async confirmCheckOut() {
    const { visitorInfo } = this.data
    if (!visitorInfo) return

    try {
      const userInfo = app.globalData.userInfo
      const response = await api.checkOutVisitor(visitorInfo.id, userInfo.id)
      
      if (response.code === 200) {
        wx.showToast({
          title: '签退成功',
          icon: 'success'
        })
        
        // 更新访客状态
        this.setData({
          'visitorInfo.status': 'DEPARTED',
          'visitorInfo.actualDeparture': new Date().toISOString()
        })
      } else {
        wx.showToast({
          title: response.message || '签退失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('签退失败:', error)
      wx.showToast({
        title: '签退失败，请重试',
        icon: 'none'
      })
    }
  },

  // 重新扫码
  rescan() {
    this.setData({
      visitorInfo: null,
      scanning: false
    })
    this.startScan()
  },

  // 格式化时间
  formatTime(timeStr) {
    if (!timeStr) return ''
    
    const date = new Date(timeStr)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    
    return `${month}-${day} ${hour}:${minute}`
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待审核',
      'APPROVED': '已批准',
      'ARRIVED': '已到达',
      'DEPARTED': '已离开',
      'EXPIRED': '已过期',
      'REJECTED': '已拒绝'
    }
    return statusMap[status] || status
  },

  // 获取状态颜色
  getStatusColor(status) {
    const colorMap = {
      'PENDING': '#f6ad55',
      'APPROVED': '#68d391',
      'ARRIVED': '#4299e1',
      'DEPARTED': '#9ca3af',
      'EXPIRED': '#f56565',
      'REJECTED': '#f56565'
    }
    return colorMap[status] || '#9ca3af'
  }
})





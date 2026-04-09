// pages/owner/visitor.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    showQRModal: false,
    currentVisitor: {},
    todayVisitors: [],
    visitorStats: {
      total: 0,
      today: 0,
      thisWeek: 0,
      thisMonth: 0
    },
    recentHistory: [],
    loading: false
  },

  onLoad(options) {
    console.log('访客通行页面加载')
    this.loadVisitorData()
  },

  onShow() {
    // 刷新数据
    this.loadVisitorData()
  },

  // 加载访客数据
  async loadVisitorData() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        console.log('用户信息不存在')
        return
      }

      // 并行加载数据
      await Promise.all([
        this.loadTodayVisitors(userInfo.id),
        this.loadVisitorStats(userInfo.id),
        this.loadRecentHistory(userInfo.id)
      ])
    } catch (error) {
      console.error('加载访客数据失败:', error)
      wx.showToast({
        title: '数据加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 加载今日访客
  async loadTodayVisitors(ownerId) {
    try {
      const response = await api.getTodayVisitors(ownerId)
      
      if (response.code === 200 && response.data) {
        const visitors = this.processVisitorData(response.data)
        this.setData({
          todayVisitors: visitors
        })
      }
    } catch (error) {
      console.error('加载今日访客失败:', error)
    }
  },

  // 加载访客统计
  async loadVisitorStats(ownerId) {
    try {
      const response = await api.getVisitorStats(ownerId)
      
      if (response.code === 200 && response.data) {
        this.setData({
          visitorStats: {
            total: response.data.total || 0,
            today: response.data.today || 0,
            thisWeek: response.data.thisWeek || 0,
            thisMonth: response.data.thisMonth || 0
          }
        })
      }
    } catch (error) {
      console.error('加载访客统计失败:', error)
    }
  },

  // 加载最近访客历史
  async loadRecentHistory(ownerId) {
    try {
      const response = await api.getVisitorList({
        pageNum: 1,
        pageSize: 5,
        ownerId: ownerId,
        status: 'DEPARTED'
      })
      
      if (response.code === 200 && response.data && response.data.list) {
        const history = response.data.list.map(visitor => ({
          id: visitor.id,
          visitorName: visitor.visitorName,
          description: visitor.visitPurpose || '访客拜访',
          timeText: this.formatRelativeTime(new Date(visitor.actualDeparture || visitor.createTime)),
          statusColor: '#38a169',
          iconUrl: '/static/icons/check-circle.svg'
        }))
        
        this.setData({
          recentHistory: history
        })
      }
    } catch (error) {
      console.error('加载访客历史失败:', error)
    }
  },

  // 处理访客数据
  processVisitorData(visitors) {
    return visitors.map(visitor => ({
      id: visitor.id,
      name: visitor.visitorName,
      nameInitial: visitor.visitorName ? visitor.visitorName.charAt(0) : '访',
      phone: this.maskPhone(visitor.visitorPhone),
      visitTime: this.formatVisitTime(visitor.plannedArrival, visitor.plannedDeparture),
      status: this.getVisitorStatus(visitor.status),
      statusText: this.getStatusText(visitor.status),
      expireTime: this.formatExpireTime(visitor.plannedDeparture),
      qrCode: visitor.qrCode,
      sourceData: visitor
    }))
  },

  // 获取访客状态
  getVisitorStatus(status) {
    const statusMap = {
      'PENDING': 'waiting',
      'APPROVED': 'waiting',
      'ARRIVED': 'arrived',
      'DEPARTED': 'completed',
      'EXPIRED': 'expired',
      'REJECTED': 'rejected'
    }
    return statusMap[status] || 'waiting'
  },

  // 获取状态文本
  getStatusText(status) {
    const textMap = {
      'PENDING': '待审核',
      'APPROVED': '已批准',
      'ARRIVED': '已到达',
      'DEPARTED': '已离开',
      'EXPIRED': '已过期',
      'REJECTED': '已拒绝'
    }
    return textMap[status] || '等待中'
  },

  // 格式化访问时间
  formatVisitTime(arrival, departure) {
    if (!arrival) return ''
    
    const arrivalTime = new Date(arrival)
    const departureTime = departure ? new Date(departure) : null
    
    const arrivalStr = this.formatTime(arrivalTime)
    const departureStr = departureTime ? this.formatTime(departureTime) : '待定'
    
    return `${arrivalStr}-${departureStr}`
  },

  // 格式化过期时间
  formatExpireTime(departure) {
    if (!departure) return '无限制'
    
    const departureTime = new Date(departure)
    const today = new Date()
    
    if (departureTime.toDateString() === today.toDateString()) {
      return `今日${this.formatTime(departureTime)}`
    } else {
      return this.formatDate(departureTime)
    }
  },

  // 格式化时间
  formatTime(date) {
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${hours}:${minutes}`
  },

  // 格式化日期
  formatDate(date) {
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${month}-${day} ${hours}:${minutes}`
  },

  // 格式化相对时间
  formatRelativeTime(time) {
    const now = new Date()
    const diff = now - time
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    const hours = Math.floor(diff / (60 * 60 * 1000))
    const minutes = Math.floor(diff / (60 * 1000))

    if (days > 0) {
      return `${days}天前`
    } else if (hours > 0) {
      return `${hours}小时前`
    } else if (minutes > 0) {
      return `${minutes}分钟前`
    } else {
      return '刚刚'
    }
  },

  // 手机号脱敏
  maskPhone(phone) {
    if (!phone) return ''
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  },

  // 邀请访客
  inviteVisitor() {
    console.log('邀请访客')
    wx.navigateTo({
      url: '/pages/owner/visitor-invite'
    })
  },

  // 查看访客详情
  viewVisitorDetail(e) {
    const visitor = e.currentTarget.dataset.visitor
    console.log('查看访客详情:', visitor)
    
    wx.navigateTo({
      url: `/pages/owner/visitor-detail?id=${visitor.id}`
    })
  },

  // 显示二维码
  showQRCode(e) {
    e.stopPropagation() // 阻止事件冒泡
    const visitor = e.currentTarget.dataset.visitor
    console.log('显示二维码:', visitor)
    
    this.setData({
      showQRModal: true,
      currentVisitor: visitor
    })
  },

  // 隐藏二维码
  hideQRCode() {
    this.setData({
      showQRModal: false,
      currentVisitor: {}
    })
  },

  // 分享二维码
  shareQRCode() {
    console.log('分享二维码给访客')
    
    const visitor = this.data.currentVisitor
    
    // 生成分享内容
    const shareContent = `您好，我已为您生成访客通行码。
访客：${visitor.name}
访问时间：${visitor.visitTime}
有效期至：${visitor.expireTime}
请保存此二维码，到达时出示给门卫即可。`

    // 复制到剪贴板
    wx.setClipboardData({
      data: shareContent,
      success: () => {
        wx.showToast({
          title: '访客信息已复制',
          icon: 'success'
        })
        this.hideQRCode()
      }
    })
  },

  // 查看全部历史记录
  viewAllHistory() {
    console.log('查看全部访客历史')
    wx.navigateTo({
      url: '/pages/owner/visitor-history'
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadVisitorData()
    wx.stopPullDownRefresh()
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '智慧物业访客通行',
      path: '/pages/owner/visitor'
    }
  }
})
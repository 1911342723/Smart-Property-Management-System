// pages/owner/notice-detail.js
const api = require('../../utils/api.js')

Page({
  data: {
    noticeId: null,
    notice: null,
    loading: true,
    error: false,
    errorMessage: ''
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ noticeId: options.id })
      this.loadNoticeDetail()
    } else {
      this.setData({
        loading: false,
        error: true,
        errorMessage: '公告ID不能为空'
      })
    }
  },

  onShow() {
    // 更新导航栏
    wx.setNavigationBarTitle({
      title: '公告详情'
    })
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  },

  // 加载公告详情
  loadNoticeDetail() {
    const self = this
    
    this.setData({ 
      loading: true, 
      error: false 
    })

    api.getAnnouncementDetail(this.data.noticeId).then(result => {
      if (result.code === 200 && result.data) {
        const notice = result.data
        
        // 处理数据
        const processedNotice = Object.assign({}, notice, {
          publishTimeText: self.formatDate(notice.publishTime, 'YYYY-MM-DD HH:mm'),
          typeText: self.getNoticeTypeText(notice.type),
          priorityText: self.getPriorityText(notice.priority)
        })
        
        self.setData({
          notice: processedNotice,
          loading: false,
          error: false
        })
        
        // 增加浏览量
        self.increaseViewCount()
        
      } else {
        throw new Error(result.message || '获取公告详情失败')
      }
    }).catch(error => {
      console.error('Load notice detail error:', error)
      self.setData({
        loading: false,
        error: true,
        errorMessage: error.message || '加载失败，请稍后重试'
      })
    })
  },

  // 增加浏览量
  increaseViewCount() {
    // 静默增加浏览量，不处理错误
    api.increaseAnnouncementView(this.data.noticeId).catch(() => {
      // 忽略错误
    })
  },

  // 工具方法
  formatDate(dateString, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!dateString) return ''
    
    const date = new Date(dateString.replace(/-/g, '/'))
    if (isNaN(date.getTime())) return dateString
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  },

  getNoticeTypeText(type) {
    const typeMap = {
      'SYSTEM': '系统公告',
      'MAINTENANCE': '维护公告',
      'EVENT': '活动公告',
      'EMERGENCY': '紧急通知',
      'GENERAL': '一般通知',
      'NOTICE': '通知公告',
      'ACTIVITY': '社区活动'
    }
    return typeMap[type] || '通知'
  },

  getPriorityText(priority) {
    const priorityMap = {
      'HIGH': '重要',
      'NORMAL': '普通',
      'LOW': '一般'
    }
    return priorityMap[priority] || '普通'
  }
})







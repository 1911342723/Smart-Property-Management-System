// pages/owner/complaint-detail.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    complaintId: null,
    complaint: {},
    loading: true,
    showRatingModal: false,
    rating: 0,
    feedback: '',
    ratingSubmitting: false
  },

  onLoad(options) {
    console.log('投诉详情页面加载')
    
    if (options.id) {
      this.setData({
        complaintId: parseInt(options.id)
      })
      this.loadComplaintDetail()
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
  },

  onShow() {
    console.log('投诉详情页面显示')
  },

  // 加载投诉详情
  async loadComplaintDetail() {
    this.setData({ loading: true })
    
    try {
      console.log('加载投诉详情，ID:', this.data.complaintId)
      const response = await api.getComplaintDetail(this.data.complaintId)
      
      console.log('投诉详情响应:', response)
      
      if (response.code === 200 && response.data) {
        const complaint = this.processComplaintData(response.data)
        this.setData({
          complaint: complaint
        })
      } else {
        wx.showToast({
          title: response.message || '加载失败',
          icon: 'none'
        })
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    } catch (error) {
      console.error('加载投诉详情失败:', error)
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } finally {
      this.setData({ loading: false })
    }
  },

  // 处理投诉数据
  processComplaintData(data) {
    return {
      id: data.id,
      complaintNo: data.complaintNo,
      title: data.title,
      content: data.content,
      complaintType: data.complaintType,
      typeText: this.getTypeText(data.complaintType),
      urgencyLevel: data.urgencyLevel,
      urgencyText: this.getUrgencyText(data.urgencyLevel),
      urgencyColor: this.getUrgencyColor(data.urgencyLevel),
      status: data.status,
      statusText: this.getStatusText(data.status),
      statusColor: this.getStatusColor(data.status),
      images: data.images ? JSON.parse(data.images) : [],
      complainantName: data.complainantName,
      complainantPhone: data.complainantPhone,
      roomAddress: data.roomAddress,
      handlerName: data.handlerName,
      handleResult: data.handleResult,
      handleTime: data.handleTime,
      handleTimeText: data.handleTime ? this.formatDateTime(data.handleTime) : null,
      submitTimeText: this.formatDateTime(data.createTime),
      satisfactionRating: data.satisfactionRating,
      feedback: data.feedback,
      sourceData: data
    }
  },

  // 获取类型文本
  getTypeText(type) {
    const typeMap = {
      'NOISE': '噪音投诉',
      'HYGIENE': '环境卫生',
      'FACILITY': '设施维护',
      'SERVICE': '物业服务',
      'SECURITY': '安全问题',
      'OTHER': '其他问题'
    }
    return typeMap[type] || type
  },

  // 获取紧急程度文本
  getUrgencyText(urgency) {
    const urgencyMap = {
      'LOW': '低',
      'NORMAL': '普通',
      'HIGH': '高',
      'URGENT': '紧急'
    }
    return urgencyMap[urgency] || urgency
  },

  // 获取紧急程度颜色
  getUrgencyColor(urgency) {
    const colorMap = {
      'LOW': '#38a169',
      'NORMAL': '#3182ce',
      'HIGH': '#d69e2e',
      'URGENT': '#e53e3e'
    }
    return colorMap[urgency] || '#718096'
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待处理',
      'ASSIGNED': '已分配',
      'PROCESSING': '处理中',
      'RESOLVED': '已解决',
      'CLOSED': '已关闭'
    }
    return statusMap[status] || status
  },

  // 获取状态颜色
  getStatusColor(status) {
    const colorMap = {
      'PENDING': '#d69e2e',
      'ASSIGNED': '#2d3748',
      'PROCESSING': '#3182ce',
      'RESOLVED': '#38a169',
      'CLOSED': '#718096'
    }
    return colorMap[status] || '#718096'
  },

  // 格式化日期时间
  formatDateTime(dateTime) {
    if (!dateTime) return ''
    
    const date = new Date(dateTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hour}:${minute}`
  },

  // 预览图片
  previewImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.complaint.images
    
    wx.previewImage({
      current: images[index],
      urls: images
    })
  },

  // 显示评价弹窗
  showRating() {
    console.log('显示评价弹窗')
    this.setData({
      showRatingModal: true,
      rating: 0,
      feedback: ''
    })
  },

  // 关闭评价弹窗
  closeRatingModal() {
    this.setData({
      showRatingModal: false,
      rating: 0,
      feedback: ''
    })
  },

  // 选择评分
  selectRating(e) {
    const rating = e.currentTarget.dataset.rating
    console.log('选择评分:', rating)
    this.setData({
      rating: rating
    })
  },

  // 反馈输入
  onFeedbackInput(e) {
    this.setData({
      feedback: e.detail.value
    })
  },

  // 提交评价
  async submitRating() {
    if (this.data.rating === 0) {
      wx.showToast({
        title: '请选择评分',
        icon: 'none'
      })
      return
    }
    
    if (this.data.ratingSubmitting) {
      return
    }
    
    this.setData({ ratingSubmitting: true })
    
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }

      const params = {
        complaintId: this.data.complaintId,
        complainantId: userInfo.id,
        satisfactionRating: this.data.rating,
        feedback: this.data.feedback.trim()
      }
      
      console.log('提交评价参数:', params)
      
      const response = await api.rateComplaint(params)
      console.log('评价响应:', response)
      
      if (response.code === 200) {
        wx.showToast({
          title: '评价成功',
          icon: 'success'
        })
        
        this.closeRatingModal()
        // 重新加载详情
        this.loadComplaintDetail()
      } else {
        wx.showToast({
          title: response.message || '评价失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('提交评价失败:', error)
      wx.showToast({
        title: '评价失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ ratingSubmitting: false })
    }
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '投诉详情',
      path: `/pages/owner/complaint-detail?id=${this.data.complaintId}`
    }
  }
})







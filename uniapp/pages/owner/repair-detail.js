// pages/owner/repair-detail.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    workOrderId: null,
    workOrder: null,
    loading: true,
    canRate: false,
    showRatingModal: false,
    rating: 5,
    feedback: '',
    ratingSubmitting: false
  },

  onLoad(options) {
    console.log('报修详情页面加载', options)
    
    const id = options.id
    if (!id) {
      wx.showToast({
        title: '工单ID无效',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }
    
    this.setData({ workOrderId: id })
    this.loadWorkOrderDetail()
  },

  onShow() {
    // 每次显示时刷新数据
    if (this.data.workOrderId) {
      this.loadWorkOrderDetail()
    }
  },

  // 加载工单详情
  async loadWorkOrderDetail() {
    try {
      this.setData({ loading: true })
      
      const response = await api.getWorkOrderDetail(this.data.workOrderId)
      
      if (response.code === 200 && response.data) {
        const workOrder = this.processWorkOrderData(response.data)
        
        this.setData({
          workOrder,
          canRate: workOrder.status === 'COMPLETED' && !workOrder.rating,
          loading: false
        })
        
        // 设置导航栏标题
        wx.setNavigationBarTitle({
          title: `工单详情 - ${workOrder.orderNo || workOrder.id}`
        })
      } else {
        throw new Error(response.message || '获取工单详情失败')
      }
      
    } catch (error) {
      console.error('Load work order detail error:', error)
      this.setData({ loading: false })
      
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  // 处理工单数据
  processWorkOrderData(data) {
    return {
      ...data,
      statusText: this.getStatusText(data.status),
      statusColor: this.getStatusColor(data.status),
      priorityText: this.getPriorityText(data.priority),
      priorityColor: this.getPriorityColor(data.priority),
      submitTimeText: this.formatDateTime(data.submitTime),
      assignTimeText: data.assignTime ? this.formatDateTime(data.assignTime) : null,
      startTimeText: data.startTime ? this.formatDateTime(data.startTime) : null,
      completeTimeText: data.completeTime ? this.formatDateTime(data.completeTime) : null,
      images: data.images ? this.parseImages(data.images) : [],
      costText: data.cost ? `¥${data.cost}` : '免费'
    }
  },

  // 解析图片
  parseImages(imagesStr) {
    try {
      return JSON.parse(imagesStr)
    } catch (e) {
      return []
    }
  },

  // 格式化日期时间
  formatDateTime(datetime) {
    if (!datetime) return ''
    
    const date = new Date(datetime)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const day = 24 * 60 * 60 * 1000
    
    if (diff < day) {
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    } else {
      return date.toLocaleDateString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待处理',
      'PROCESSING': '处理中',
      'COMPLETED': '已完成',
      'CLOSED': '已关闭',
      'CANCELLED': '已取消'
    }
    return statusMap[status] || status
  },

  // 获取状态颜色
  getStatusColor(status) {
    const colorMap = {
      'PENDING': '#d69e2e',
      'PROCESSING': '#2d3748',
      'COMPLETED': '#38a169',
      'CLOSED': '#718096',
      'CANCELLED': '#e53e3e'
    }
    return colorMap[status] || '#718096'
  },

  // 获取优先级文本
  getPriorityText(priority) {
    const priorityMap = {
      'LOW': '低',
      'MEDIUM': '中',
      'HIGH': '高',
      'URGENT': '紧急'
    }
    return priorityMap[priority] || priority
  },

  // 获取优先级颜色
  getPriorityColor(priority) {
    const colorMap = {
      'LOW': '#718096',
      'MEDIUM': '#d69e2e',
      'HIGH': '#e53e3e',
      'URGENT': '#c53030'
    }
    return colorMap[priority] || '#718096'
  },

  // 预览图片
  previewImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.workOrder.images
    
    wx.previewImage({
      current: images[index],
      urls: images
    })
  },

  // 联系维修工
  contactWorker() {
    const workOrder = this.data.workOrder
    
    if (!workOrder.assigneeName) {
      wx.showToast({
        title: '暂未分配维修工',
        icon: 'none'
      })
      return
    }
    
    wx.showActionSheet({
      itemList: ['拨打电话', '发送消息'],
      success: (res) => {
        if (res.tapIndex === 0) {
          // 拨打电话（这里需要后端提供维修工电话）
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        } else if (res.tapIndex === 1) {
          // 发送消息
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        }
      }
    })
  },

  // 跳转到评价页面
  async showRating() {
    // 先检查是否已经评价过
    try {
      const response = await api.getRatingByOrderId(this.data.workOrderId)
      
      if (response.code === 200 && response.data) {
        // 已经有评价了
        wx.showModal({
          title: '提示',
          content: '您已经评价过该工单了',
          showCancel: false,
          confirmText: '我知道了'
        })
        return
      }
    } catch (error) {
      console.log('检查评价状态失败，继续跳转:', error)
    }
    
    // 跳转到评价页面
    wx.navigateTo({
      url: `/pages/owner/rate-order?orderId=${this.data.workOrderId}`
    })
  },

  // 关闭评价弹窗
  closeRatingModal() {
    this.setData({
      showRatingModal: false
    })
  },

  // 选择评分
  selectRating(e) {
    const rating = e.currentTarget.dataset.rating
    this.setData({ rating })
  },

  // 输入反馈
  onFeedbackInput(e) {
    this.setData({
      feedback: e.detail.value
    })
  },

  // 提交评价
  async submitRating() {
    if (this.data.ratingSubmitting) return
    
    const { rating, feedback } = this.data
    
    try {
      this.setData({ ratingSubmitting: true })
      
      const response = await api.rateWorkOrder(this.data.workOrderId, rating, feedback)
      
      if (response.code === 200) {
        wx.showToast({
          title: '评价成功',
          icon: 'success'
        })
        
        this.setData({
          showRatingModal: false,
          canRate: false
        })
        
        // 重新加载工单详情
        this.loadWorkOrderDetail()
      } else {
        throw new Error(response.message || '评价失败')
      }
      
    } catch (error) {
      console.error('Submit rating error:', error)
      wx.showToast({
        title: error.message || '评价失败',
        icon: 'none'
      })
    } finally {
      this.setData({ ratingSubmitting: false })
    }
  },

  // 刷新数据
  onRefresh() {
    this.loadWorkOrderDetail()
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  }
})
// pages/owner/complaint-list.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    complaints: [],
    loading: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    selectedStatus: 'ALL',
    selectedType: 'ALL',
    showFilterModal: false,
    statusOptions: [
      { key: 'ALL', name: '全部状态', color: '#718096' },
      { key: 'PENDING', name: '待处理', color: '#d69e2e' },
      { key: 'ASSIGNED', name: '已分配', color: '#2d3748' },
      { key: 'PROCESSING', name: '处理中', color: '#3182ce' },
      { key: 'RESOLVED', name: '已解决', color: '#38a169' },
      { key: 'CLOSED', name: '已关闭', color: '#718096' }
    ],
    typeOptions: [
      { key: 'ALL', name: '全部类型' },
      { key: 'NOISE', name: '噪音投诉' },
      { key: 'HYGIENE', name: '环境卫生' },
      { key: 'FACILITY', name: '设施维护' },
      { key: 'SERVICE', name: '物业服务' },
      { key: 'SECURITY', name: '安全问题' },
      { key: 'OTHER', name: '其他问题' }
    ]
  },

  onLoad(options) {
    console.log('投诉列表页面加载')
    this.loadComplaintList(true)
  },

  onShow() {
    console.log('投诉列表页面显示')
  },

  // 加载投诉列表
  async loadComplaintList(refresh = false) {
    if (this.data.loading) return
    
    if (refresh) {
      this.setData({
        pageNum: 1,
        complaints: [],
        hasMore: true,
        total: 0
      })
    }
    
    if (!this.data.hasMore && !refresh) return
    
    this.setData({ loading: true })
    
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
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        complainantId: userInfo.id
      }
      
      // 添加筛选条件
      if (this.data.selectedStatus !== 'ALL') {
        params.status = this.data.selectedStatus
      }
      
      if (this.data.selectedType !== 'ALL') {
        params.complaintType = this.data.selectedType
      }
      
      console.log('加载投诉列表参数:', params)
      
      const response = await api.getComplaintList(params)
      console.log('投诉列表响应:', response)
      
      if (response.code === 200 && response.data) {
        const newComplaints = this.processComplaintsData(response.data.list || [])
        const complaints = refresh ? newComplaints : [...this.data.complaints, ...newComplaints]
        
        this.setData({
          complaints: complaints,
          total: response.data.total || 0,
          hasMore: complaints.length < (response.data.total || 0),
          pageNum: this.data.pageNum + 1
        })
      } else {
        wx.showToast({
          title: response.message || '加载失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('加载投诉列表失败:', error)
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 处理投诉数据
  processComplaintsData(complaints) {
    return complaints.map(item => ({
      id: item.id,
      complaintNo: item.complaintNo,
      title: item.title,
      content: item.content,
      complaintType: item.complaintType,
      typeText: this.getTypeText(item.complaintType),
      urgencyLevel: item.urgencyLevel,
      urgencyText: this.getUrgencyText(item.urgencyLevel),
      urgencyColor: this.getUrgencyColor(item.urgencyLevel),
      status: item.status,
      statusText: this.getStatusText(item.status),
      statusColor: this.getStatusColor(item.status),
      iconUrl: this.getTypeIcon(item.complaintType),
      createTime: new Date(item.createTime),
      timeText: this.formatRelativeTime(new Date(item.createTime)),
      sourceData: item
    }))
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

  // 获取类型图标
  getTypeIcon(type) {
    const iconMap = {
      'NOISE': '/static/icons/bell.svg',
      'HYGIENE': '/static/icons/settings.svg',
      'FACILITY': '/static/icons/wrench.svg',
      'SERVICE': '/static/icons/feedback.svg',
      'SECURITY': '/static/icons/alert-circle.svg',
      'OTHER': '/static/icons/info.svg'
    }
    return iconMap[type] || '/static/icons/info.svg'
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

  // 查看投诉详情
  viewComplaintDetail(e) {
    const complaint = e.currentTarget.dataset.complaint
    console.log('查看投诉详情:', complaint)
    
    wx.navigateTo({
      url: `/pages/owner/complaint-detail?id=${complaint.id}`
    })
  },

  // 显示筛选弹窗
  showFilter() {
    console.log('显示筛选弹窗')
    this.setData({
      showFilterModal: true
    })
  },

  // 关闭筛选弹窗
  closeFilter() {
    this.setData({
      showFilterModal: false
    })
  },

  // 选择状态筛选
  selectStatus(e) {
    const status = e.currentTarget.dataset.status
    console.log('选择状态筛选:', status)
    this.setData({
      selectedStatus: status
    })
  },

  // 选择类型筛选
  selectType(e) {
    const type = e.currentTarget.dataset.type
    console.log('选择类型筛选:', type)
    this.setData({
      selectedType: type
    })
  },

  // 应用筛选
  applyFilter() {
    console.log('应用筛选')
    this.closeFilter()
    this.loadComplaintList(true)
  },

  // 重置筛选
  resetFilter() {
    console.log('重置筛选')
    this.setData({
      selectedStatus: 'ALL',
      selectedType: 'ALL'
    })
  },

  // 新建投诉
  newComplaint() {
    console.log('新建投诉')
    wx.navigateTo({
      url: '/pages/owner/complaint-form'
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadComplaintList(true)
    wx.stopPullDownRefresh()
  },

  // 触底加载更多
  onReachBottom() {
    this.loadComplaintList(false)
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '我的投诉列表',
      path: '/pages/owner/complaint-list'
    }
  }
})





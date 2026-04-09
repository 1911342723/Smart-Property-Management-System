// pages/owner/complaint.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    complaintTypes: [
      {
        id: 1,
        name: '噪音投诉',
        description: '邻居噪音、装修噪音等',
        icon: '/static/icons/bell.svg',
        gradient: 'linear-gradient(135deg, #e53e3e 0%, #c53030 100%)',
        category: 'noise'
      },
      {
        id: 2,
        name: '环境卫生',
        description: '垃圾清理、卫生问题等',
        icon: '/static/icons/settings.svg',
        gradient: 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)',
        category: 'hygiene'
      },
      {
        id: 3,
        name: '设施维护',
        description: '电梯、门禁、照明等',
        icon: '/static/icons/wrench.svg',
        gradient: 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)',
        category: 'facility'
      },
      {
        id: 4,
        name: '物业服务',
        description: '服务态度、响应速度等',
        icon: '/static/icons/feedback.svg',
        gradient: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        category: 'service'
      },
      {
        id: 5,
        name: '安全问题',
        description: '安保、消防、治安等',
        icon: '/static/icons/alert-circle.svg',
        gradient: 'linear-gradient(135deg, #e53e3e 0%, #c53030 100%)',
        category: 'security'
      },
      {
        id: 6,
        name: '其他问题',
        description: '其他相关问题反馈',
        icon: '/static/icons/info.svg',
        gradient: 'linear-gradient(135deg, #718096 0%, #4a5568 100%)',
        category: 'other'
      }
    ],
    recentComplaints: [],
    complaintStats: {
      total: 0,
      pending: 0,
      processing: 0,
      resolved: 0
    }
  },

  onLoad(options) {
    console.log('投诉建议页面加载')
    this.initData()
  },

  onShow() {
    // 刷新数据
    this.initData()
  },

  // 初始化数据
  async initData() {
    this.loadComplaints()
    this.loadComplaintStats()
  },

  // 加载投诉数据
  async loadComplaints() {
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        console.log('用户信息不存在，使用模拟数据')
        this.setMockData()
        return
      }

      console.log('加载投诉列表，用户ID:', userInfo.id)
      const response = await api.getComplaintList({
        pageNum: 1,
        pageSize: 3,
        complainantId: userInfo.id,
        status: 'PENDING,PROCESSING,RESOLVED'
      })
      
      console.log('投诉列表响应:', response)
      
      if (response.code === 200 && response.data) {
        const complaints = this.processComplaintsData(response.data.list || [])
        this.setData({
          recentComplaints: complaints
        })
      } else {
        console.log('投诉列表加载失败，使用模拟数据')
        this.setMockData()
      }
    } catch (error) {
      console.error('加载投诉列表失败:', error)
      this.setMockData()
    }
  },

  // 加载投诉统计
  async loadComplaintStats() {
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        return
      }

      console.log('加载投诉统计，用户ID:', userInfo.id)
      const response = await api.getComplaintStats(userInfo.id)
      
      console.log('投诉统计响应:', response)
      
      if (response.code === 200 && response.data) {
        this.setData({
          complaintStats: {
            total: response.data.total || 0,
            pending: response.data.pending || 0,
            processing: response.data.processing || 0,
            resolved: response.data.resolved || 0
          }
        })
      }
    } catch (error) {
      console.error('加载投诉统计失败:', error)
    }
  },

  // 处理投诉数据
  processComplaintsData(complaints) {
    return complaints.map(item => ({
      id: item.id,
      title: item.title,
      description: item.content,
      category: item.complaintType.toLowerCase(),
      status: item.status.toLowerCase(),
      statusText: this.getStatusText(item.status),
      statusColor: this.getStatusColor(item.status),
      iconUrl: this.getTypeIcon(item.complaintType),
      createTime: new Date(item.createTime),
      timeText: this.formatRelativeTime(new Date(item.createTime)),
      sourceData: item
    }))
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

  // 设置模拟数据
  setMockData() {
    const complaints = [
      {
        id: 1,
        title: '楼上住户深夜噪音',
        description: '楼上住户经常深夜制造噪音，影响休息',
        category: 'noise',
        status: 'processing',
        statusText: '处理中',
        statusColor: '#2d3748',
        iconUrl: '/static/icons/bell.svg',
        createTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
        timeText: '2天前'
      },
      {
        id: 2,
        title: '电梯故障频繁',
        description: '6号楼电梯经常故障，给业主出行带来不便',
        category: 'facility',
        status: 'resolved',
        statusText: '已解决',
        statusColor: '#38a169',
        iconUrl: '/static/icons/wrench.svg',
        createTime: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
        timeText: '5天前'
      },
      {
        id: 3,
        title: '垃圾清理不及时',
        description: '小区垃圾桶经常满溢，清理不及时',
        category: 'hygiene',
        status: 'pending',
        statusText: '待处理',
        statusColor: '#d69e2e',
        iconUrl: '/static/icons/settings.svg',
        createTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
        timeText: '1天前'
      }
    ]

    // 处理时间显示
    const processedComplaints = complaints.map(item => ({
      ...item,
      timeText: this.formatRelativeTime(item.createTime)
    }))

    // 计算统计数据
    const stats = {
      total: complaints.length,
      pending: complaints.filter(c => c.status === 'pending').length,
      processing: complaints.filter(c => c.status === 'processing').length,
      resolved: complaints.filter(c => c.status === 'resolved').length
    }

    this.setData({
      recentComplaints: processedComplaints.slice(0, 3), // 只显示最近3条
      complaintStats: stats
    })
  },

  // 选择投诉类型
  selectComplaintType(e) {
    const type = e.currentTarget.dataset.type
    console.log('选择投诉类型:', type)
    
    wx.navigateTo({
      url: `/pages/owner/complaint-form?category=${type.category}&title=${type.name}`
    })
  },

  // 新建投诉
  newComplaint() {
    console.log('新建投诉')
    wx.navigateTo({
      url: '/pages/owner/complaint-form'
    })
  },

  // 查看投诉详情
  viewComplaintDetail(e) {
    const complaint = e.currentTarget.dataset.complaint
    console.log('查看投诉详情:', complaint)
    
    wx.navigateTo({
      url: `/pages/owner/complaint-detail?id=${complaint.id}`
    })
  },

  // 查看全部投诉
  viewAllComplaints() {
    console.log('查看全部投诉')
    wx.navigateTo({
      url: '/pages/owner/complaint-list'
    })
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

  // 下拉刷新
  onPullDownRefresh() {
    this.loadComplaints()
    wx.stopPullDownRefresh()
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '智慧物业投诉建议',
      path: '/pages/owner/complaint'
    }
  }
})
// pages/owner/notices.js
const api = require('../../utils/api.js')

Page({
  data: {
    notices: [],
    loading: false,
    refreshing: false,
    noMore: false,
    
    // 分页参数
    pageNum: 1,
    pageSize: 10,
    
    // 筛选参数
    activeType: 'ALL',
    filterType: 'ALL',
    filterPriority: 'ALL',
    showFilterModal: false,
    
    // 筛选选项
    noticeTypes: [
      { label: '全部', value: 'ALL' },
      { label: '通知', value: 'NOTICE' },
      { label: '活动', value: 'ACTIVITY' },
      { label: '维护', value: 'MAINTENANCE' },
      { label: '紧急', value: 'EMERGENCY' }
    ],
    
    allNoticeTypes: [
      { label: '全部类型', value: 'ALL' },
      { label: '通知公告', value: 'NOTICE' },
      { label: '社区活动', value: 'ACTIVITY' },
      { label: '设备维护', value: 'MAINTENANCE' },
      { label: '紧急通知', value: 'EMERGENCY' }
    ],
    
    priorityOptions: [
      { label: '全部优先级', value: 'ALL' },
      { label: '高优先级', value: 'HIGH' },
      { label: '普通优先级', value: 'NORMAL' },
      { label: '低优先级', value: 'LOW' }
    ]
  },

  onLoad(options) {
    // 如果从首页传来类型参数，设置筛选条件
    if (options.type) {
      this.setData({ activeType: options.type })
    }
    
    this.loadNotices(true)
  },

  onShow() {
    // 更新导航栏
    wx.setNavigationBarTitle({
      title: '公告通知'
    })
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  },

  // 切换公告类型
  switchType(e) {
    const type = e.currentTarget.dataset.type
    if (type === this.data.activeType) return
    
    this.setData({
      activeType: type,
      notices: [],
      pageNum: 1,
      noMore: false
    })
    
    this.loadNotices(true)
  },

  // 加载公告列表
  loadNotices(reset = false) {
    if (this.data.loading) return
    
    const self = this
    
    if (reset) {
      this.setData({
        notices: [],
        pageNum: 1,
        noMore: false
      })
    }
    
    this.setData({ loading: true })
    
    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }
    
    // 添加类型筛选
    if (this.data.activeType !== 'ALL') {
      params.type = this.data.activeType
    }
    
    // 添加优先级筛选
    if (this.data.filterPriority !== 'ALL') {
      params.priority = this.data.filterPriority
    }

    api.getAnnouncementList(params).then(result => {
      if (result.code === 200) {
        // 支持多种返回数据格式：分页数据(records)、列表数据(list)、直接数组
        const newNotices = result.data.records || result.data.list || result.data || []
        const processedNotices = newNotices.map(function(item) {
          return Object.assign({}, item, {
            dateText: self.formatDate(item.publishTime || item.publishedAt, 'YYYY-MM-DD HH:mm'),
            typeText: self.getNoticeTypeText(item.type),
            priorityText: self.getPriorityText(item.priority),
            summary: item.summary || self.extractSummary(item.content),
            // 兼容字段名称
            publisherName: item.publisherName || item.author || '系统管理员'
          })
        })
        
        const allNotices = reset ? processedNotices : self.data.notices.concat(processedNotices)
        
        self.setData({
          notices: allNotices,
          pageNum: self.data.pageNum + 1,
          noMore: newNotices.length < self.data.pageSize,
          loading: false
        })
      } else {
        throw new Error(result.message || '获取公告失败')
      }
    }).catch(error => {
      console.error('Load notices error:', error)
      
      // 如果是首次加载失败，使用模拟数据
      if (reset && self.data.notices.length === 0) {
        self.loadMockNotices()
      } else {
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
      
      self.setData({ loading: false })
    })
  },

  // 加载模拟数据
  loadMockNotices() {
    const mockNotices = [
      {
        id: 1,
        title: '春节放假通知',
        summary: '物业服务中心春节期间值班安排通知',
        content: '尊敬的业主朋友们：春节期间物业服务中心值班安排如下...',
        type: 'NOTICE',
        priority: 'HIGH',
        publishTime: '2024-01-20 10:00:00',
        publisherName: '物业管理处',
        viewCount: 156
      },
      {
        id: 2,
        title: '停水维护通知',
        summary: '因设备维护需要，明日上午将临时停水',
        content: '各位业主：因供水设备维护需要，明日上午9:00-12:00将临时停水...',
        type: 'MAINTENANCE',
        priority: 'HIGH',
        publishTime: '2024-01-19 15:30:00',
        publisherName: '工程部',
        viewCount: 89
      },
      {
        id: 3,
        title: '垃圾分类倡议书',
        summary: '共建绿色社区，从垃圾分类做起',
        content: '亲爱的业主朋友们：为响应城市垃圾分类号召，共建美好绿色社区...',
        type: 'NOTICE',
        priority: 'NORMAL',
        publishTime: '2024-01-18 09:00:00',
        publisherName: '环保小组',
        viewCount: 234
      },
      {
        id: 4,
        title: '社区羽毛球比赛',
        summary: '社区羽毛球友谊赛报名开始',
        content: '为丰富业主文化生活，社区将举办羽毛球友谊赛...',
        type: 'ACTIVITY',
        priority: 'NORMAL',
        publishTime: '2024-01-17 14:00:00',
        publisherName: '社区文体部',
        viewCount: 67
      },
      {
        id: 5,
        title: '电梯年检通知',
        summary: '电梯年度安全检查，部分时段暂停使用',
        content: '根据相关规定，小区电梯需进行年度安全检查...',
        type: 'MAINTENANCE',
        priority: 'NORMAL',
        publishTime: '2024-01-16 11:20:00',
        publisherName: '设备部',
        viewCount: 123
      }
    ]

    const processedNotices = mockNotices.map(function(item) {
      return Object.assign({}, item, {
        dateText: this.formatDate(item.publishTime, 'YYYY-MM-DD HH:mm'),
        typeText: this.getNoticeTypeText(item.type),
        priorityText: this.getPriorityText(item.priority)
      })
    }.bind(this))

    this.setData({
      notices: processedNotices,
      loading: false,
      noMore: true
    })
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ refreshing: true })
    
    setTimeout(() => {
      this.loadNotices(true)
      this.setData({ refreshing: false })
    }, 1000)
  },

  // 上拉加载更多
  loadMore() {
    if (!this.data.noMore && !this.data.loading) {
      this.loadNotices(false)
    }
  },

  // 查看公告详情
  viewNoticeDetail(e) {
    const notice = e.currentTarget.dataset.notice
    
    wx.navigateTo({
      url: `/pages/owner/notice-detail?id=${notice.id}`
    })
  },

  // 显示筛选弹窗
  showFilterModal() {
    this.setData({ 
      showFilterModal: true,
      filterType: this.data.activeType,
      filterPriority: this.data.filterPriority
    })
  },

  // 隐藏筛选弹窗
  hideFilterModal() {
    this.setData({ showFilterModal: false })
  },

  // 选择筛选类型
  selectFilterType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ filterType: type })
  },

  // 选择筛选优先级
  selectFilterPriority(e) {
    const priority = e.currentTarget.dataset.priority
    this.setData({ filterPriority: priority })
  },

  // 重置筛选
  resetFilter() {
    this.setData({
      filterType: 'ALL',
      filterPriority: 'ALL'
    })
  },

  // 应用筛选
  applyFilter() {
    this.setData({
      activeType: this.data.filterType,
      showFilterModal: false,
      notices: [],
      pageNum: 1,
      noMore: false
    })
    
    this.loadNotices(true)
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
      'NOTICE': '通知',
      'ACTIVITY': '活动',
      'MAINTENANCE': '维护',
      'EMERGENCY': '紧急'
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
  },

  extractSummary(content, maxLength = 60) {
    if (!content) return ''
    
    const cleanContent = content.replace(/\n+/g, ' ').replace(/\s+/g, ' ').trim()
    
    if (cleanContent.length <= maxLength) {
      return cleanContent
    }
    
    return cleanContent.substring(0, maxLength) + '...'
  }
})




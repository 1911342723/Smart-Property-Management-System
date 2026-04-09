// 历史巡检记录页面逻辑
Page({
  data: {
    // 筛选条件
    filters: {
      startDate: '',
      endDate: '',
      statusIndex: 0
    },
    
    // 状态选项
    statusOptions: [
      { value: 'all', label: '全部状态' },
      { value: 'normal', label: '仅正常' },
      { value: 'abnormal', label: '仅异常' }
    ],
    
    // 统计数据
    statistics: {
      totalCount: 0,
      normalCount: 0,
      abnormalCount: 0,
      completionRate: 0
    },
    
    // 巡检记录列表
    patrolRecords: [],
    
    // 分页参数
    pageNum: 1,
    pageSize: 10,
    loading: false,
    noMore: false,
    
    // 弹窗
    showDetailModal: false,
    selectedRecord: null
  },

  onLoad(options) {
    this.initFilters()
    this.loadPatrolRecords()
    this.loadStatistics()
  },

  onShow() {
    // 刷新数据
    this.refreshData()
  },

  onPullDownRefresh() {
    this.refreshData()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    this.loadMoreRecords()
  },

  // 初始化筛选条件
  initFilters() {
    const today = new Date()
    const oneWeekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
    
    this.setData({
      'filters.startDate': this.formatDate(oneWeekAgo),
      'filters.endDate': this.formatDate(today)
    })
  },

  // 格式化日期
  formatDate(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  // 开始日期选择
  onStartDateChange(e) {
    const startDate = e.detail.value
    this.setData({
      'filters.startDate': startDate
    })
    this.refreshData()
  },

  // 结束日期选择
  onEndDateChange(e) {
    const endDate = e.detail.value
    this.setData({
      'filters.endDate': endDate
    })
    this.refreshData()
  },

  // 状态筛选
  onStatusChange(e) {
    const statusIndex = parseInt(e.detail.value)
    this.setData({
      'filters.statusIndex': statusIndex
    })
    this.refreshData()
  },

  // 刷新数据
  refreshData() {
    this.setData({
      pageNum: 1,
      patrolRecords: [],
      noMore: false
    })
    this.loadPatrolRecords()
    this.loadStatistics()
  },

  // 加载巡检记录
  loadPatrolRecords() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    // 模拟API请求
    setTimeout(() => {
      const { filters, pageNum, pageSize } = this.data
      const mockRecords = this.generateMockRecords(pageNum, pageSize, filters)
      
      const newRecords = pageNum === 1 ? mockRecords : [...this.data.patrolRecords, ...mockRecords]
      
      this.setData({
        patrolRecords: newRecords,
        loading: false,
        noMore: mockRecords.length < pageSize,
        pageNum: pageNum + 1
      })
    }, 1000)
  },

  // 加载更多记录
  loadMoreRecords() {
    if (!this.data.noMore && !this.data.loading) {
      this.loadPatrolRecords()
    }
  },

  // 生成模拟数据
  generateMockRecords(pageNum, pageSize, filters) {
    const records = []
    const patrolPoints = [
      { name: '东门岗亭', location: '小区东门入口' },
      { name: '西门岗亭', location: '小区西门入口' },
      { name: '北门岗亭', location: '小区北门入口' },
      { name: '地下车库', location: 'B1层地下车库' },
      { name: '中心广场', location: '小区中心广场' },
      { name: '儿童游乐区', location: '小区儿童游乐场' },
      { name: '健身器材区', location: '室外健身区域' },
      { name: '垃圾分类站', location: '小区垃圾收集点' }
    ]
    
    const weatherOptions = ['晴', '多云', '阴', '小雨', '雨']
    const guards = ['张三', '李四', '王五', '赵六']
    
    for (let i = 0; i < pageSize; i++) {
      const index = (pageNum - 1) * pageSize + i
      if (index >= 50) break // 限制总数据量
      
      const date = new Date()
      date.setDate(date.getDate() - Math.floor(index / 3))
      
      const patrolPoint = patrolPoints[index % patrolPoints.length]
      const status = Math.random() > 0.3 ? 'normal' : 'abnormal'
      const hasPhotos = Math.random() > 0.7
      
      // 应用筛选条件
      const statusFilter = this.data.statusOptions[filters.statusIndex].value
      if (statusFilter !== 'all' && statusFilter !== status) {
        continue
      }
      
      const record = {
        id: `patrol_${index}`,
        patrolPoint: patrolPoint.name,
        location: patrolPoint.location,
        status: status,
        createDate: this.formatDate(date),
        createTime: `${String(Math.floor(Math.random() * 12) + 8).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
        guardName: guards[index % guards.length],
        weather: weatherOptions[index % weatherOptions.length],
        duration: Math.floor(Math.random() * 10) + 5,
        description: status === 'abnormal' ? `发现${patrolPoint.name}存在异常情况，需要进一步处理` : '',
        photos: hasPhotos ? ['/static/images/repair1.jpg', '/static/images/repair2.jpg'] : [],
        gpsInfo: {
          longitude: '116.' + (Math.floor(Math.random() * 1000000) + 300000),
          latitude: '39.' + (Math.floor(Math.random() * 1000000) + 900000),
          accuracy: Math.floor(Math.random() * 10) + 5
        }
      }
      
      records.push(record)
    }
    
    return records
  },

  // 加载统计数据
  loadStatistics() {
    // 模拟统计数据
    const totalCount = 156
    const normalCount = 142
    const abnormalCount = 14
    const completionRate = Math.floor((normalCount / totalCount) * 100)
    
    this.setData({
      statistics: {
        totalCount,
        normalCount,
        abnormalCount,
        completionRate
      }
    })
  },

  // 查看详情
  viewDetail(e) {
    const record = e.currentTarget.dataset.record
    this.setData({
      selectedRecord: record,
      showDetailModal: true
    })
  },

  // 关闭详情弹窗
  closeDetailModal() {
    this.setData({
      showDetailModal: false,
      selectedRecord: null
    })
  },

  // 预览图片
  previewImage(e) {
    const src = e.currentTarget.dataset.src
    const urls = e.currentTarget.dataset.urls || [src]
    
    wx.previewImage({
      current: src,
      urls: urls
    })
  }
})











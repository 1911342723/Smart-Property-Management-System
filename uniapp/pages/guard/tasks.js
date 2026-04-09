// 保安任务中心页面逻辑
Page({
  data: {
    // 统计数据
    todayPatrolCount: 0,
    completedPatrolCount: 0, 
    pendingPatrolCount: 0,
    
    // 位置信息
    currentLocation: '',
    
    // 巡检表单
    patrolPointIndex: 0,
    patrolPoints: [
      { id: 1, name: '东门岗亭', location: '小区东门入口' },
      { id: 2, name: '西门岗亭', location: '小区西门入口' },
      { id: 3, name: '北门岗亭', location: '小区北门入口' },
      { id: 4, name: '地下车库', location: 'B1层地下车库' },
      { id: 5, name: '中心广场', location: '小区中心广场' },
      { id: 6, name: '儿童游乐区', location: '小区儿童游乐场' },
      { id: 7, name: '健身器材区', location: '室外健身区域' },
      { id: 8, name: '垃圾分类站', location: '小区垃圾收集点' }
    ],
    patrolStatus: 'normal',
    abnormalDescription: '',
    patrolPhotos: [],
    submitting: false,
    
    // 今日巡检记录
    todayPatrolRecords: [],
    
    // 弹窗
    showRecordModal: false,
    selectedRecord: null
  },

  onLoad(options) {
    this.getCurrentLocation()
    this.loadTodayStats()
    this.loadTodayPatrolRecords()
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(0)
    }
    
    this.loadTodayStats()
    this.loadTodayPatrolRecords()
  },

  onPullDownRefresh() {
    this.loadTodayStats()
    this.loadTodayPatrolRecords()
    wx.stopPullDownRefresh()
  },

  // 获取当前位置
  getCurrentLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        // 模拟根据经纬度获取位置名称
        this.reverseGeocode(res.latitude, res.longitude)
      },
      fail: () => {
        this.setData({
          currentLocation: '定位失败，请检查定位权限'
        })
      }
    })
  },

  // 逆地理编码（模拟）
  reverseGeocode(lat, lng) {
    // 模拟位置解析
    const locations = [
      '小区东门附近',
      '小区西门附近', 
      '小区中心区域',
      '小区北侧',
      '小区南侧'
    ]
    const randomLocation = locations[Math.floor(Math.random() * locations.length)]
    
    this.setData({
      currentLocation: randomLocation
    })
  },

  // 加载今日统计
  loadTodayStats() {
    // 模拟数据
    const mockStats = {
      todayPatrolCount: 8,
      completedPatrolCount: 5,
      pendingPatrolCount: 3
    }
    
    this.setData(mockStats)
  },

  // 加载今日巡检记录
  loadTodayPatrolRecords() {
    // 模拟今日巡检记录
    const mockRecords = [
      {
        id: 1,
        patrolPoint: '东门岗亭',
        status: 'normal',
        location: '小区东门入口',
        createTime: '09:15',
        description: '',
        photos: ['/static/images/repair1.jpg']
      },
      {
        id: 2,
        patrolPoint: '地下车库',
        status: 'abnormal', 
        location: 'B1层地下车库',
        createTime: '10:30',
        description: '发现B1层车库照明灯故障，已联系维修',
        photos: ['/static/images/repair2.jpg']
      },
      {
        id: 3,
        patrolPoint: '中心广场',
        status: 'normal',
        location: '小区中心广场',
        createTime: '11:45',
        description: '',
        photos: []
      }
    ]
    
    this.setData({
      todayPatrolRecords: mockRecords
    })
  },

  // 选择巡检点
  onPatrolPointChange(e) {
    this.setData({
      patrolPointIndex: parseInt(e.detail.value)
    })
  },

  // 选择状态
  selectStatus(e) {
    const status = e.currentTarget.dataset.status
    this.setData({
      patrolStatus: status,
      abnormalDescription: status === 'normal' ? '' : this.data.abnormalDescription
    })
  },

  // 异常描述输入
  onAbnormalInput(e) {
    this.setData({
      abnormalDescription: e.detail.value
    })
  },

  // 选择图片
  chooseImage() {
    const maxCount = 6 - this.data.patrolPhotos.length
    
    wx.chooseMedia({
      count: maxCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFiles.map(file => file.tempFilePath)
        
        this.setData({
          patrolPhotos: [...this.data.patrolPhotos, ...tempFilePaths]
        })
      }
    })
  },

  // 删除图片
  deletePhoto(e) {
    const index = e.currentTarget.dataset.index
    const photos = [...this.data.patrolPhotos]
    photos.splice(index, 1)
    
    this.setData({
      patrolPhotos: photos
    })
  },

  // 预览图片
  previewImage(e) {
    const src = e.currentTarget.dataset.src
    const urls = this.data.selectedRecord ? 
      this.data.selectedRecord.photos : 
      this.data.patrolPhotos
    
    wx.previewImage({
      current: src,
      urls: urls
    })
  },

  // 提交巡检
  submitPatrol() {
    const { patrolPointIndex, patrolPoints, patrolStatus, abnormalDescription, patrolPhotos } = this.data
    
    // 表单验证
    if (patrolStatus === 'abnormal' && !abnormalDescription.trim()) {
      wx.showToast({
        title: '请填写异常描述',
        icon: 'none'
      })
      return
    }
    
    this.setData({ submitting: true })
    
    // 模拟提交
    setTimeout(() => {
      wx.showToast({
        title: '巡检提交成功',
        icon: 'success'
      })
      
      // 重置表单
      this.setData({
        patrolStatus: 'normal',
        abnormalDescription: '',
        patrolPhotos: [],
        submitting: false
      })
      
      // 刷新数据
      this.loadTodayStats()
      this.loadTodayPatrolRecords()
    }, 2000)
  },

  // 查看记录详情
  viewRecord(e) {
    const record = e.currentTarget.dataset.record
    this.setData({
      selectedRecord: record,
      showRecordModal: true
    })
  },

  // 关闭记录详情
  closeRecordModal() {
    this.setData({
      showRecordModal: false,
      selectedRecord: null
    })
  },

  // 跳转历史记录
  goToHistory() {
    wx.navigateTo({
      url: '/pages/guard/patrol-history'
    })
  }
})
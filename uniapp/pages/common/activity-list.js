// pages/common/activity-list.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    loading: false,  // 初始状态应该是false，在loadActivities中才设置为true
    error: null,
    activities: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    filters: {
      status: 'ALL', // ALL, REGISTRATION, UPCOMING, ONGOING
      type: 'ALL'    // ALL, SPORTS, ENTERTAINMENT, EDUCATION, COMMUNITY, HEALTH
    },
    statusFilterIndex: 0,
    typeFilterIndex: 0,
    currentStatusText: '全部状态',
    currentTypeText: '全部类型',
    statusOptions: [
      { value: 'ALL', text: '全部状态' },
      { value: 'REGISTRATION', text: '报名中' },
      { value: 'UPCOMING', text: '即将开始' },
      { value: 'ONGOING', text: '进行中' }
    ],
    typeOptions: [
      { value: 'ALL', text: '全部类型' },
      { value: 'SPORTS', text: '体育运动' },
      { value: 'ENTERTAINMENT', text: '文娱活动' },
      { value: 'EDUCATION', text: '教育讲座' },
      { value: 'COMMUNITY', text: '社区活动' },
      { value: 'HEALTH', text: '健康养生' }
    ]
  },

  onLoad() {
    console.log('=== 活动列表页面加载 ===')
    this.loadActivities()
  },

  // 加载活动列表
  loadActivities(refresh = false) {
    if (refresh) {
      this.setData({ 
        pageNum: 1,
        activities: [],
        hasMore: true
      })
    }

    if (!this.data.hasMore || this.data.loading) {
      return
    }

    this.setData({ loading: true, error: null })

    // 构建查询参数
    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }

    // 添加状态筛选
    if (this.data.filters.status !== 'ALL') {
      params.status = this.data.filters.status
    }

    // 添加类型筛选
    if (this.data.filters.type !== 'ALL') {
      params.type = this.data.filters.type
    }

    console.log('请求活动列表，参数：', params)
    
    // 禁用API的自动loading，使用页面自己的loading状态
    api.getActivityList(params, { loading: false }).then(result => {
      console.log('活动列表API返回结果:', result)
      
      if (result && result.code === 200) {
        let activityList = []
        
        // 处理返回的数据结构
        if (result.data && result.data.list) {
          activityList = result.data.list
        } else if (result.data && result.data.records) {
          activityList = result.data.records
        } else if (Array.isArray(result.data)) {
          activityList = result.data
        }

        console.log('解析后的活动数据:', activityList)

        // 处理活动数据
        const processedActivities = (activityList || []).map(item => {
          return {
            id: item.id,
            title: item.title,
            description: item.content || item.description,
            summary: item.summary || (item.description ? item.description.substring(0, 30) + '...' : ''),
            imageUrl: item.imageUrl || this.getDefaultActivityImage(item.type),
            timeText: this.formatActivityTime(item.startTime, item.endTime),
            location: item.location || '待定',
            status: item.status,
            statusText: this.getActivityStatusText(item.status),
            type: item.type,
            typeText: this.getActivityTypeText(item.type),
            participantCount: item.currentParticipants || 0,
            maxParticipants: item.maxParticipants,
            registrationStart: item.registrationStart,
            registrationEnd: item.registrationEnd,
            isRegistered: item.isRegistered || false,
            registrationStatus: item.registrationStatus
          }
        })

        console.log('处理后的活动数据:', processedActivities)

        // 更新数据
        this.setData({
          activities: [...this.data.activities, ...processedActivities],
          pageNum: this.data.pageNum + 1,
          hasMore: activityList.length === this.data.pageSize,
          loading: false
        })
        
        console.log('页面数据更新完成，活动数量:', this.data.activities.length)
      } else {
        console.warn('获取活动列表失败:', result)
        this.setData({
          error: (result && result.message) || '获取活动列表失败',
          loading: false
        })
      }
    }).catch(error => {
      console.error('Load activities error:', error)
      console.error('Error stack:', error.stack)
      this.setData({
        error: error.message || '网络错误，请稍后重试',
        loading: false
      })
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadActivities(true)
    wx.stopPullDownRefresh()
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadActivities()
    }
  },

  // 重试加载
  retryLoad() {
    this.setData({ error: null })
    this.loadActivities(true)
  },

  // 图片加载错误处理
  onImageError(e) {
    console.log('图片加载失败:', e.detail)
    // 可以在这里设置默认图片
  },

  // 切换状态筛选
  onStatusChange(e) {
    const index = e.detail.value
    const status = this.data.statusOptions[index].value
    const text = this.data.statusOptions[index].text
    
    this.setData({
      statusFilterIndex: index,
      currentStatusText: text,
      'filters.status': status
    }, () => {
      this.loadActivities(true)
    })
  },

  // 切换类型筛选
  onTypeChange(e) {
    const index = e.detail.value
    const type = this.data.typeOptions[index].value
    const text = this.data.typeOptions[index].text
    
    this.setData({
      typeFilterIndex: index,
      currentTypeText: text,
      'filters.type': type
    }, () => {
      this.loadActivities(true)
    })
  },

  // 查看活动详情
  viewActivityDetail(e) {
    const activity = e.currentTarget.dataset.activity
    wx.navigateTo({
      url: `/pages/common/activity-detail?id=${activity.id}`
    })
  },

  // 活动报名
  registerActivity(e) {
    const activity = e.currentTarget.dataset.activity
    
    // 检查活动状态
    if (activity.status !== 'REGISTRATION') {
      wx.showToast({
        title: '当前活动不在报名期间',
        icon: 'none'
      })
      return
    }
    
    // 检查是否已满员
    if (activity.participantCount >= activity.maxParticipants) {
      wx.showToast({
        title: '报名人数已满',
        icon: 'none'
      })
      return
    }
    
    // 检查报名时间
    const now = new Date().getTime()
    const registrationStart = new Date(activity.registrationStart).getTime()
    const registrationEnd = new Date(activity.registrationEnd).getTime()
    
    if (now < registrationStart) {
      wx.showToast({
        title: '报名未开始',
        icon: 'none'
      })
      return
    }
    
    if (now > registrationEnd) {
      wx.showToast({
        title: '报名已结束',
        icon: 'none'
      })
      return
    }
    
    // 显示报名确认框
    wx.showModal({
      title: '确认报名',
      content: `确定要报名参加"${activity.title}"吗？\n\n活动时间：${activity.timeText}\n活动地点：${activity.location}\n当前报名：${activity.participantCount}/${activity.maxParticipants}人`,
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '报名中...',
            mask: true
          })
          
          api.registerActivity(activity.id).then(result => {
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showModal({
                title: '报名成功',
                content: '您已成功报名参加活动，请按时参加。如有变动，我们会及时通知您。',
                showCancel: false,
                confirmText: '知道了',
                success: () => {
                  // 刷新活动列表
                  this.loadActivities(true)
                }
              })
            } else {
              wx.showModal({
                title: '报名失败',
                content: result.message || '报名失败，请稍后重试',
                showCancel: false,
                confirmText: '知道了'
              })
            }
          }).catch(error => {
            wx.hideLoading()
            console.error('Register activity error:', error)
            
            wx.showModal({
              title: '报名失败',
              content: '网络错误，请检查网络连接后重试',
              showCancel: false,
              confirmText: '知道了'
            })
          })
        }
      }
    })
  },

  // 取消报名
  cancelRegistration(e) {
    const activity = e.currentTarget.dataset.activity
    
    wx.showModal({
      title: '取消报名',
      content: `确定要取消"${activity.title}"的报名吗？`,
      editable: true,
      placeholderText: '请输入取消原因（选填）',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '取消中...',
            mask: true
          })
          
          const reason = res.content || ''
          
          api.cancelActivityRegistration(activity.id, reason).then(result => {
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '取消报名成功',
                icon: 'success'
              })
              // 刷新活动列表
              this.loadActivities(true)
            } else {
              wx.showModal({
                title: '取消失败',
                content: result.message || '取消报名失败，请稍后重试',
                showCancel: false,
                confirmText: '知道了'
              })
            }
          }).catch(error => {
            wx.hideLoading()
            console.error('Cancel registration error:', error)
            
            wx.showModal({
              title: '取消失败',
              content: '网络错误，请检查网络连接后重试',
              showCancel: false,
              confirmText: '知道了'
            })
          })
        }
      }
    })
  },

  // 格式化活动时间
  formatActivityTime(startTime, endTime) {
    if (!startTime) return '时间待定'
    
    const start = new Date(startTime)
    const end = new Date(endTime)
    
    const startMonth = String(start.getMonth() + 1).padStart(2, '0')
    const startDay = String(start.getDate()).padStart(2, '0')
    const startHour = String(start.getHours()).padStart(2, '0')
    const startMinute = String(start.getMinutes()).padStart(2, '0')
    
    let timeText = `${startMonth}月${startDay}日 ${startHour}:${startMinute}`
    
    if (endTime) {
      const endHour = String(end.getHours()).padStart(2, '0')
      const endMinute = String(end.getMinutes()).padStart(2, '0')
      timeText += ` - ${endHour}:${endMinute}`
    }
    
    return timeText
  },

  // 获取活动状态文本
  getActivityStatusText(status) {
    const statusMap = {
      'UPCOMING': '即将开始',
      'REGISTRATION': '报名中',
      'ONGOING': '进行中',
      'COMPLETED': '已结束',
      'CANCELLED': '已取消'
    }
    return statusMap[status] || '未知'
  },

  // 获取活动类型文本
  getActivityTypeText(type) {
    const typeMap = {
      'SPORTS': '体育运动',
      'ENTERTAINMENT': '文娱活动',
      'EDUCATION': '教育讲座',
      'COMMUNITY': '社区活动',
      'HEALTH': '健康养生'
    }
    return typeMap[type] || '其他'
  },

  // 获取默认活动图片
  getDefaultActivityImage(type) {
    const imageMap = {
      'SPORTS': '/static/images/activity-sports.jpg',
      'ENTERTAINMENT': '/static/images/activity-entertainment.jpg',
      'EDUCATION': '/static/images/activity-education.jpg',
      'COMMUNITY': '/static/images/activity-community.jpg',
      'HEALTH': '/static/images/activity-health.jpg'
    }
    return imageMap[type] || '/static/images/activity-default.jpg'
  }
})

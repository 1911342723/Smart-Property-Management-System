// pages/common/my-registrations.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    loading: true,
    error: null,
    registrations: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    statusFilter: 'ALL',
    statusFilterIndex: 0,
    currentStatusText: '全部状态',
    statusOptions: [
      { value: 'ALL', text: '全部状态' },
      { value: 'REGISTERED', text: '已报名' },
      { value: 'CONFIRMED', text: '已确认' },
      { value: 'CANCELLED', text: '已取消' }
    ]
  },

  onLoad() {
    console.log('=== 我的报名页面加载 ===')
    this.loadRegistrations()
  },

  // 加载报名列表
  loadRegistrations(refresh = false) {
    if (refresh) {
      this.setData({ 
        pageNum: 1,
        registrations: [],
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
    if (this.data.statusFilter !== 'ALL') {
      params.status = this.data.statusFilter
    }

    console.log('请求报名列表，参数：', params)
    
    api.getMyRegistrations(params).then(result => {
      console.log('报名列表API返回结果:', result)
      
      if (result.code === 200) {
        let registrationList = []
        
        // 处理返回的数据结构
        if (result.data && result.data.list) {
          registrationList = result.data.list
        } else if (result.data && result.data.records) {
          registrationList = result.data.records
        } else if (Array.isArray(result.data)) {
          registrationList = result.data
        }

        console.log('解析后的报名数据:', registrationList)

        // 处理报名数据
        const processedRegistrations = registrationList.map(item => {
          // 判断是否可以取消报名（只有报名中的活动且状态为已报名才能取消）
          const canCancel = item.activityStatus === 'REGISTRATION' && item.status === 'REGISTERED'
          
          return {
            id: item.id,
            activityId: item.activityId,
            activityTitle: item.activityTitle || '未知活动',
            activityImageUrl: item.activityImageUrl || '/static/images/activity-default.jpg',
            activityTimeText: this.formatActivityTime(item.startTime, item.endTime),
            location: item.location || '待定',
            participants: item.participants || 1,
            status: item.status,
            statusText: this.getRegistrationStatusText(item.status),
            registrationTimeText: this.formatDateTime(item.registrationTime),
            confirmTime: item.confirmTime,
            cancelTime: item.cancelTime,
            cancelReason: item.cancelReason,
            canCancel: canCancel,
            activityStatus: item.activityStatus
          }
        })

        console.log('处理后的报名数据:', processedRegistrations)

        // 更新数据
        this.setData({
          registrations: [...this.data.registrations, ...processedRegistrations],
          pageNum: this.data.pageNum + 1,
          hasMore: registrationList.length === this.data.pageSize,
          loading: false
        })
        
        console.log('页面数据更新完成，报名记录数量:', this.data.registrations.length)
      } else {
        console.warn('获取报名列表失败:', result)
        this.setData({
          error: result.message || '获取报名列表失败',
          loading: false
        })
      }
    }).catch(error => {
      console.error('Load registrations error:', error)
      this.setData({
        error: '网络错误，请稍后重试',
        loading: false
      })
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadRegistrations(true)
    wx.stopPullDownRefresh()
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadRegistrations()
    }
  },

  // 重试加载
  retryLoad() {
    this.setData({ error: null })
    this.loadRegistrations(true)
  },

  // 切换状态筛选
  onStatusChange(e) {
    const index = e.detail.value
    const status = this.data.statusOptions[index].value
    const text = this.data.statusOptions[index].text
    
    this.setData({
      statusFilterIndex: index,
      currentStatusText: text,
      statusFilter: status
    }, () => {
      this.loadRegistrations(true)
    })
  },

  // 查看活动详情
  viewActivityDetail(e) {
    const activityId = e.currentTarget.dataset.activityId
    wx.navigateTo({
      url: `/pages/common/activity-detail?id=${activityId}`
    })
  },

  // 取消报名
  cancelRegistration(e) {
    const activityId = e.currentTarget.dataset.activityId
    const registration = e.currentTarget.dataset.registration
    
    wx.showModal({
      title: '取消报名',
      content: `确定要取消"${registration.activityTitle}"的报名吗？`,
      editable: true,
      placeholderText: '请输入取消原因（选填）',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '取消中...',
            mask: true
          })
          
          const reason = res.content || ''
          
          api.cancelActivityRegistration(activityId, reason).then(result => {
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showToast({
                title: '取消报名成功',
                icon: 'success'
              })
              // 刷新报名列表
              this.loadRegistrations(true)
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

  // 获取报名状态文本
  getRegistrationStatusText(status) {
    const statusMap = {
      'REGISTERED': '已报名',
      'CONFIRMED': '已确认',
      'CANCELLED': '已取消'
    }
    return statusMap[status] || '未知'
  }
})






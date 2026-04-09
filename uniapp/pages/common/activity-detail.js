// pages/common/activity-detail.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    activity: {}
  },

  onLoad(options) {
    const activityId = options.id
    this.loadActivityDetail(activityId)
  },

  loadActivityDetail(id) {
    const self = this
    
    // 显示加载状态
    wx.showLoading({
      title: '加载中...',
      mask: true
    })
    
    // 从后端获取活动详情
    api.getActivityDetail(id).then(result => {
      wx.hideLoading()
      
      if (result.code === 200 && result.data) {
        const activity = result.data
        
        // 处理活动数据
        const processedActivity = {
          id: activity.id,
          title: activity.title,
          description: activity.content || activity.description,
          imageUrl: activity.imageUrl || this.getDefaultActivityImage(activity.type),
          timeText: this.formatActivityTime(activity.startTime, activity.endTime),
          location: activity.location || '待定',
          status: activity.status,
          statusText: this.getActivityStatusText(activity.status),
          currentParticipants: activity.currentParticipants || 0,
          maxParticipants: activity.maxParticipants,
          requirements: activity.requirements || '暂无特殊要求',
          contact: activity.contact || '物业服务中心：400-888-8888',
          // 新增字段
          registrationStart: activity.registrationStart,
          registrationEnd: activity.registrationEnd,
          organizer: activity.organizerName || '社区物业',
          isRegistered: activity.isRegistered || false,
          registrationStatus: activity.registrationStatus
        }
        
        this.setData({ activity: processedActivity })
      } else {
        wx.showToast({
          title: '获取活动详情失败',
          icon: 'none'
        })
      }
    }).catch(error => {
      wx.hideLoading()
      console.error('Load activity detail error:', error)
      
      wx.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      })
    })
  },

  joinActivity() {
    const activity = this.data.activity
    
    // 检查活动状态
    if (activity.status !== 'REGISTRATION') {
      wx.showToast({
        title: '当前活动不在报名期间',
        icon: 'none'
      })
      return
    }
    
    // 检查是否已满员
    if (activity.currentParticipants >= activity.maxParticipants) {
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
    
    wx.showModal({
      title: '确认报名',
      content: `确定要报名参加"${activity.title}"吗？\n\n活动时间：${activity.timeText}\n活动地点：${activity.location}\n当前报名：${activity.currentParticipants}/${activity.maxParticipants}人`,
      success: (res) => {
        if (res.confirm) {
          // 显示加载提示
          wx.showLoading({
            title: '报名中...',
            mask: true
          })
          
          // 调用报名接口
          api.registerActivity(activity.id).then(result => {
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showModal({
                title: '报名成功',
                content: '您已成功报名参加活动，请按时参加。如有变动，我们会及时通知您。',
                showCancel: false,
                confirmText: '知道了',
                success: () => {
                  // 重新加载活动详情
                  this.loadActivityDetail(activity.id)
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
  cancelRegistration() {
    const activity = this.data.activity
    
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
              // 重新加载活动详情
              this.loadActivityDetail(activity.id)
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






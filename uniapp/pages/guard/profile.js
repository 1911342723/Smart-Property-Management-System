// 保安个人中心页面逻辑
Page({
  data: {
    // 用户信息
    userInfo: {
      name: '张三',
      workNumber: 'G001',
      department: '物业管理部',
      avatar: '/static/images/default-avatar.png',
      workStatus: 'on-duty', // on-duty: 在岗, off-duty: 下班
      signature: '', // 个人签名
      phone: '', // 联系电话
      emergencyContact: '', // 紧急联系人
      emergencyPhone: '' // 紧急联系电话
    },
    
    // 个人业绩
    performance: {
      patrolRate: 95,      // 巡检完成率
      reportCount: 12,     // 上报事件数
      workDays: 22,        // 出勤天数
      rating: 4.8          // 综合评分
    },
    
    // 本周排班
    weekSchedule: [],
    
    // 未读消息数
    unreadMessages: 3
  },

  onLoad(options) {
    this.loadUserInfo()
    this.loadWeekSchedule()
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(2)
    }
    
    this.loadUserInfo()
    this.loadPerformance()
    this.checkMessages()
  },

  onPullDownRefresh() {
    this.loadUserInfo()
    this.loadPerformance()
    this.loadWeekSchedule()
    this.checkMessages()
    wx.stopPullDownRefresh()
  },

  // 加载用户信息
  loadUserInfo() {
    // 从缓存或接口获取用户信息
    const userInfo = wx.getStorageSync('userInfo') || this.data.userInfo
    
    this.setData({
      userInfo: userInfo
    })
  },

  // 加载业绩数据
  loadPerformance() {
    // 模拟业绩数据
    const mockPerformance = {
      patrolRate: Math.floor(Math.random() * 20) + 80,
      reportCount: Math.floor(Math.random() * 10) + 8,
      workDays: Math.floor(Math.random() * 5) + 20,
      rating: (Math.random() * 1 + 4).toFixed(1)
    }
    
    this.setData({
      performance: mockPerformance
    })
  },

  // 加载本周排班
  loadWeekSchedule() {
    const today = new Date()
    const weekSchedule = []
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(today)
      date.setDate(today.getDate() + i)
      
      const day = date.getDate()
      const weekDay = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()]
      const isToday = i === 0
      
      // 模拟排班数据
      const shifts = [
        { type: '早班', time: '08:00-16:00' },
        { type: '晚班', time: '16:00-24:00' },
        { type: '夜班', time: '00:00-08:00' },
        { type: '休息', time: '全天休息' }
      ]
      
      const randomShift = shifts[Math.floor(Math.random() * shifts.length)]
      const statuses = ['completed', 'pending', 'rest']
      const statusTexts = ['已完成', '待上班', '休息']
      
      const statusIndex = randomShift.type === '休息' ? 2 : (i === 0 ? 1 : 0)
      
      weekSchedule.push({
        date: date.toISOString().split('T')[0],
        day: day,
        dateText: weekDay,
        isToday: isToday,
        shiftType: randomShift.type,
        shiftTime: randomShift.time,
        status: statuses[statusIndex],
        statusText: statusTexts[statusIndex]
      })
    }
    
    this.setData({
      weekSchedule: weekSchedule
    })
  },

  // 检查未读消息
  checkMessages() {
    // 模拟未读消息数
    const unreadCount = Math.floor(Math.random() * 5)
    this.setData({
      unreadMessages: unreadCount
    })
  },

  // 查看排班详情
  viewSchedule() {
    wx.navigateTo({
      url: '/pages/guard/schedule-detail'
    })
  },

  // 跳转到消息页面
  goToMessages() {
    wx.navigateTo({
      url: '/pages/guard/messages'
    })
  },

  // 跳转到排班详情
  goToScheduleDetail() {
    wx.navigateTo({
      url: '/pages/guard/schedule-detail'
    })
  },

  // 跳转到业绩统计
  goToPerformance() {
    wx.navigateTo({
      url: '/pages/guard/performance-detail'
    })
  },

  // 跳转到设置
  goToSettings() {
    wx.navigateTo({
      url: '/pages/guard/settings'
    })
  },

  // 跳转到帮助页面
  goToHelp() {
    wx.navigateTo({
      url: '/pages/guard/help'
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除用户信息
          wx.removeStorageSync('userInfo')
          wx.removeStorageSync('token')
          
          // 返回登录页面
          wx.reLaunch({
            url: '/pages/login/login'
          })
        }
      }
    })
  }
})
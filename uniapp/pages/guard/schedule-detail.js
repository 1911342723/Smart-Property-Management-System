// 保安排班详情页面逻辑
Page({
  data: {
    currentDate: '',
    calendarDays: [],
    selectedDay: null,
    showDayDetail: false,
    monthOffset: 0,
    
    // 请假申请相关
    showLeaveModal: false,
    submittingLeave: false,
    leaveForm: {
      typeIndex: -1,
      startTime: '',
      endTime: '',
      startTimeIndex: [0, 0],
      endTimeIndex: [0, 0],
      reason: '',
      attachments: []
    },
    leaveTypes: [
      { name: '事假', value: 'personal', needProof: false },
      { name: '病假', value: 'sick', needProof: true },
      { name: '年假', value: 'annual', needProof: false },
      { name: '调休', value: 'compensatory', needProof: false },
      { name: '其他', value: 'other', needProof: false }
    ],
    dateTimeRange: [[], []],
    
    // 换班申请相关
    showSwapModal: false,
    submittingSwap: false,
    swapForm: {
      targetIndex: -1,
      shiftIndex: -1,
      reason: ''
    },
    swapTargets: [
      { id: 1, name: '张三', position: '保安员' },
      { id: 2, name: '李四', position: '保安员' },
      { id: 3, name: '王五', position: '保安员' }
    ],
    targetShifts: [],
    
    // 模拟数据
    scheduleData: {
      '2024-01': {
        1: { shift: { name: '早班', time: '06:00-14:00', type: 'morning' }, status: 'completed', location: 'A区大门' },
        2: { shift: { name: '晚班', time: '14:00-22:00', type: 'evening' }, status: 'completed', location: 'B区大门' },
        3: { shift: { name: '夜班', time: '22:00-06:00', type: 'night' }, status: 'absent', location: 'A区大门' },
        15: { shift: { name: '早班', time: '06:00-14:00', type: 'morning' }, status: 'leave', location: 'A区大门' },
        20: { shift: { name: '晚班', time: '14:00-22:00', type: 'evening' }, status: 'completed', location: 'C区大门' }
      }
    }
  },

  onLoad() {
    this.initCalendar()
    this.initDateTimeRange()
  },

  // 初始化日历
  initCalendar() {
    const now = new Date()
    this.setData({
      currentDate: this.formatDate(now)
    })
    this.generateCalendar(now.getFullYear(), now.getMonth())
  },

  // 初始化时间选择器数据
  initDateTimeRange() {
    const dates = []
    const times = []
    
    // 生成未来30天的日期
    for (let i = 0; i < 30; i++) {
      const date = new Date()
      date.setDate(date.getDate() + i)
      dates.push({
        name: this.formatDate(date),
        value: this.formatDate(date)
      })
    }
    
    // 生成24小时时间
    for (let h = 0; h < 24; h++) {
      for (let m = 0; m < 60; m += 30) {
        const timeStr = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`
        times.push({
          name: timeStr,
          value: timeStr
        })
      }
    }
    
    this.setData({
      dateTimeRange: [dates, times]
    })
  },

  // 生成日历
  generateCalendar(year, month) {
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    const startDate = new Date(firstDay)
    startDate.setDate(startDate.getDate() - firstDay.getDay())
    
    const days = []
    const scheduleKey = `${year}-${String(month + 1).padStart(2, '0')}`
    
    for (let i = 0; i < 42; i++) {
      const date = new Date(startDate)
      date.setDate(startDate.getDate() + i)
      
      const dayData = {
        date: date.getDate(),
        fullDate: this.formatDate(date),
        weekday: this.getWeekday(date),
        isCurrentMonth: date.getMonth() === month,
        isToday: this.isToday(date),
        shift: null,
        status: null,
        location: null,
        canModify: date >= new Date() // 只能修改今天及以后的排班
      }
      
      // 添加排班数据
      const daySchedule = this.data.scheduleData[scheduleKey]?.[date.getDate()]
      if (daySchedule) {
        Object.assign(dayData, daySchedule)
      }
      
      days.push(dayData)
    }
    
    this.setData({
      calendarDays: days,
      currentDate: `${year}年${month + 1}月`
    })
  },

  // 上一个月
  prevMonth() {
    const now = new Date()
    now.setMonth(now.getMonth() + this.data.monthOffset - 1)
    this.setData({
      monthOffset: this.data.monthOffset - 1
    })
    this.generateCalendar(now.getFullYear(), now.getMonth())
  },

  // 下一个月
  nextMonth() {
    const now = new Date()
    now.setMonth(now.getMonth() + this.data.monthOffset + 1)
    this.setData({
      monthOffset: this.data.monthOffset + 1
    })
    this.generateCalendar(now.getFullYear(), now.getMonth())
  },

  // 选择日期
  selectDay(e) {
    const day = e.currentTarget.dataset.day
    if (!day.isCurrentMonth) return
    
    this.setData({
      selectedDay: day,
      showDayDetail: true
    })
  },

  // 关闭日期详情
  closeDayDetail() {
    this.setData({
      showDayDetail: false,
      selectedDay: null
    })
  },

  // 申请请假
  requestDayOff() {
    this.setData({
      showLeaveModal: true,
      showDayDetail: false
    })
  },

  // 申请换班
  requestSwap() {
    this.setData({
      showSwapModal: true,
      showDayDetail: false
    })
    this.loadTargetShifts()
  },

  // 关闭请假弹窗
  closeLeaveModal() {
    this.setData({
      showLeaveModal: false,
      leaveForm: {
        typeIndex: -1,
        startTime: '',
        endTime: '',
        startTimeIndex: [0, 0],
        endTimeIndex: [0, 0],
        reason: '',
        attachments: []
      }
    })
  },

  // 关闭换班弹窗
  closeSwapModal() {
    this.setData({
      showSwapModal: false,
      swapForm: {
        targetIndex: -1,
        shiftIndex: -1,
        reason: ''
      },
      targetShifts: []
    })
  },

  // 请假类型选择
  onLeaveTypeChange(e) {
    this.setData({
      'leaveForm.typeIndex': parseInt(e.detail.value)
    })
  },

  // 开始时间选择
  onStartTimeChange(e) {
    const values = e.detail.value
    const dateTime = `${this.data.dateTimeRange[0][values[0]].value} ${this.data.dateTimeRange[1][values[1]].value}`
    this.setData({
      'leaveForm.startTimeIndex': values,
      'leaveForm.startTime': dateTime
    })
  },

  // 结束时间选择
  onEndTimeChange(e) {
    const values = e.detail.value
    const dateTime = `${this.data.dateTimeRange[0][values[0]].value} ${this.data.dateTimeRange[1][values[1]].value}`
    this.setData({
      'leaveForm.endTimeIndex': values,
      'leaveForm.endTime': dateTime
    })
  },

  // 请假原因输入
  onLeaveReasonInput(e) {
    this.setData({
      'leaveForm.reason': e.detail.value
    })
  },

  // 上传附件
  uploadAttachment() {
    wx.chooseImage({
      count: 3 - this.data.leaveForm.attachments.length,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const attachments = [...this.data.leaveForm.attachments, ...res.tempFilePaths]
        this.setData({
          'leaveForm.attachments': attachments
        })
      }
    })
  },

  // 删除附件
  deleteAttachment(e) {
    const index = e.currentTarget.dataset.index
    const attachments = this.data.leaveForm.attachments.filter((_, i) => i !== index)
    this.setData({
      'leaveForm.attachments': attachments
    })
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url
    wx.previewImage({
      current: url,
      urls: this.data.leaveForm.attachments
    })
  },

  // 提交请假申请
  submitLeaveRequest() {
    const form = this.data.leaveForm
    
    // 表单验证
    if (form.typeIndex < 0) {
      wx.showToast({ title: '请选择请假类型', icon: 'none' })
      return
    }
    if (!form.startTime) {
      wx.showToast({ title: '请选择开始时间', icon: 'none' })
      return
    }
    if (!form.endTime) {
      wx.showToast({ title: '请选择结束时间', icon: 'none' })
      return
    }
    if (!form.reason.trim()) {
      wx.showToast({ title: '请填写请假原因', icon: 'none' })
      return
    }
    
    this.setData({ submittingLeave: true })
    
    // 模拟提交
    setTimeout(() => {
      this.setData({ 
        submittingLeave: false,
        showLeaveModal: false
      })
      wx.showToast({
        title: '请假申请已提交',
        icon: 'success'
      })
      this.closeLeaveModal()
    }, 2000)
  },

  // 换班对象选择
  onSwapTargetChange(e) {
    this.setData({
      'swapForm.targetIndex': parseInt(e.detail.value)
    })
    this.loadTargetShifts()
  },

  // 对方班次选择
  onTargetShiftChange(e) {
    this.setData({
      'swapForm.shiftIndex': parseInt(e.detail.value)
    })
  },

  // 换班原因输入
  onSwapReasonInput(e) {
    this.setData({
      'swapForm.reason': e.detail.value
    })
  },

  // 加载对方可换班次
  loadTargetShifts() {
    // 模拟加载对方的班次数据
    const shifts = [
      { date: '2024-01-05', shift: '早班', time: '06:00-14:00', display: '1月5日 早班 (06:00-14:00)' },
      { date: '2024-01-06', shift: '晚班', time: '14:00-22:00', display: '1月6日 晚班 (14:00-22:00)' },
      { date: '2024-01-07', shift: '夜班', time: '22:00-06:00', display: '1月7日 夜班 (22:00-06:00)' }
    ]
    
    this.setData({
      targetShifts: shifts
    })
  },

  // 提交换班申请
  submitSwapRequest() {
    const form = this.data.swapForm
    
    // 表单验证
    if (form.targetIndex < 0) {
      wx.showToast({ title: '请选择换班对象', icon: 'none' })
      return
    }
    if (form.shiftIndex < 0) {
      wx.showToast({ title: '请选择对方班次', icon: 'none' })
      return
    }
    if (!form.reason.trim()) {
      wx.showToast({ title: '请填写换班原因', icon: 'none' })
      return
    }
    
    this.setData({ submittingSwap: true })
    
    // 模拟提交
    setTimeout(() => {
      this.setData({ 
        submittingSwap: false,
        showSwapModal: false
      })
      wx.showToast({
        title: '换班申请已提交',
        icon: 'success'
      })
      this.closeSwapModal()
    }, 2000)
  },

  // 工具方法
  formatDate(date) {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  },

  getWeekday(date) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return weekdays[date.getDay()]
  },

  isToday(date) {
    const today = new Date()
    return date.toDateString() === today.toDateString()
  },

  // 获取状态文本
  getStatusText(status) {
    const statusTexts = {
      completed: '已完成',
      absent: '缺勤',
      leave: '请假'
    }
    return statusTexts[status] || '未知'
  },

  // 申请请假
  requestLeave() {
    this.setData({ showLeaveModal: true })
  },

  // 申请换班
  swapShift() {
    this.setData({ showSwapModal: true })
    this.loadTargetShifts()
  },

  // 查看详情
  viewShiftDetail() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  }
})





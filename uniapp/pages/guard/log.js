// 保安值班日志页面逻辑
Page({
  data: {
    // 当前班次信息
    currentShift: {
      startTime: '08:00',
      endTime: '16:00',
      type: '早班',
      area: '小区东门',
      actualStartTime: '08:05'
    },
    
    // 交班记录表单
    logForm: {
      workSummary: '',
      abnormalEvents: '',
      pendingTasks: '',
      remarks: ''
    },
    
    // 设备检查列表
    equipmentList: [
      { id: 1, name: '东门门禁系统', location: '东门岗亭', status: 'normal' },
      { id: 2, name: '监控摄像头', location: '小区各出入口', status: 'normal' },
      { id: 3, name: '消防设备', location: '各楼层消防通道', status: 'normal' },
      { id: 4, name: '照明系统', location: '地下车库', status: 'abnormal' },
      { id: 5, name: '电梯设备', location: '各栋楼电梯', status: 'normal' },
      { id: 6, name: '对讲系统', location: '各单元门口', status: 'normal' }
    ],
    
    // 最近交班记录
    recentLogs: [],
    
    // 提交状态
    submitting: false,
    
    // 弹窗
    showLogModal: false,
    selectedLog: null
  },

  onLoad(options) {
    this.loadCurrentShift()
    this.loadRecentLogs()
  },

  onShow() {
    this.loadCurrentShift()
    this.loadRecentLogs()
  },

  onPullDownRefresh() {
    this.loadCurrentShift()
    this.loadRecentLogs()
    wx.stopPullDownRefresh()
  },

  // 加载当前班次信息
  loadCurrentShift() {
    // 模拟获取当前班次信息
    const now = new Date()
    const hour = now.getHours()
    
    let shiftInfo = {}
    if (hour >= 0 && hour < 8) {
      shiftInfo = {
        startTime: '00:00',
        endTime: '08:00',
        type: '夜班',
        area: '小区全区域',
        actualStartTime: '00:03'
      }
    } else if (hour >= 8 && hour < 16) {
      shiftInfo = {
        startTime: '08:00',
        endTime: '16:00',
        type: '早班',
        area: '小区东门',
        actualStartTime: '08:05'
      }
    } else {
      shiftInfo = {
        startTime: '16:00',
        endTime: '24:00',
        type: '晚班',
        area: '小区西门',
        actualStartTime: '16:02'
      }
    }
    
    this.setData({
      currentShift: shiftInfo
    })
  },

  // 加载最近交班记录
  loadRecentLogs() {
    // 模拟最近的交班记录
    const mockLogs = [
      {
        id: 1,
        date: '2024-01-15',
        shiftType: '夜班',
        shiftTime: '00:00-08:00',
        guardName: '李四',
        workSummary: '夜间巡逻正常，未发现异常情况。凌晨2点和5点各巡检一次，所有设备运行正常。',
        abnormalEvents: '',
        pendingTasks: '早班需关注地下车库照明灯泡更换',
        status: 'completed'
      },
      {
        id: 2,
        date: '2024-01-14',
        shiftType: '晚班',
        shiftTime: '16:00-24:00',
        guardName: '王五',
        workSummary: '晚班值勤正常，协助处理了一起访客登记。晚上9点后小区较为安静。',
        abnormalEvents: '21:30左右有业主投诉楼下噪音，已协调解决',
        pendingTasks: '',
        status: 'completed'
      },
      {
        id: 3,
        date: '2024-01-14',
        shiftType: '早班',
        shiftTime: '08:00-16:00',
        guardName: '张三',
        workSummary: '上午工作繁忙，处理了多起访客登记和车辆进出。下午进行了例行巡检。',
        abnormalEvents: '',
        pendingTasks: '晚班需注意B栋电梯维修人员进场',
        status: 'completed'
      }
    ]
    
    this.setData({
      recentLogs: mockLogs
    })
  },

  // 表单输入处理
  onFormInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    
    this.setData({
      [`logForm.${field}`]: value
    })
  },

  // 设置设备状态
  setEquipmentStatus(e) {
    const id = parseInt(e.currentTarget.dataset.id)
    const status = e.currentTarget.dataset.status
    
    const equipmentList = this.data.equipmentList.map(item => {
      if (item.id === id) {
        return { ...item, status }
      }
      return item
    })
    
    this.setData({ equipmentList })
  },

  // 提交交班记录
  submitLog() {
    const { logForm, equipmentList } = this.data
    
    // 表单验证
    if (!logForm.workSummary.trim()) {
      wx.showToast({
        title: '请填写工作概况',
        icon: 'none'
      })
      return
    }
    
    // 检查是否有异常设备未填写说明
    const abnormalEquipment = equipmentList.filter(item => item.status === 'abnormal')
    if (abnormalEquipment.length > 0 && !logForm.abnormalEvents.trim()) {
      wx.showModal({
        title: '提示',
        content: '检测到设备异常，请在异常情况中说明具体情况',
        showCancel: false
      })
      return
    }
    
    this.setData({ submitting: true })
    
    // 模拟提交
    setTimeout(() => {
      wx.showToast({
        title: '交班记录提交成功',
        icon: 'success'
      })
      
      // 重置表单
      this.setData({
        logForm: {
          workSummary: '',
          abnormalEvents: '',
          pendingTasks: '',
          remarks: ''
        },
        submitting: false
      })
      
      // 重置设备状态为正常
      const normalEquipment = this.data.equipmentList.map(item => ({
        ...item,
        status: 'normal'
      }))
      this.setData({ equipmentList: normalEquipment })
      
      // 刷新记录
      this.loadRecentLogs()
    }, 2000)
  },

  // 查看交班记录详情
  viewLog(e) {
    const log = e.currentTarget.dataset.log
    this.setData({
      selectedLog: log,
      showLogModal: true
    })
  },

  // 关闭记录详情
  closeLogModal() {
    this.setData({
      showLogModal: false,
      selectedLog: null
    })
  },

  // 跳转历史记录
  goToHistory() {
    wx.navigateTo({
      url: '/pages/guard/log-history'
    })
  },

  // 模板填充功能
  fillTemplate() {
    wx.showActionSheet({
      itemList: ['正常值班模板', '异常情况模板', '设备检修模板'],
      success: (res) => {
        let template = {}
        
        switch (res.tapIndex) {
          case 0:
            template = {
              workSummary: '本班次值勤正常，按时完成各项巡检任务。小区秩序良好，未发现异常情况。',
              abnormalEvents: '',
              pendingTasks: '',
              remarks: ''
            }
            break
          case 1:
            template = {
              workSummary: '本班次处理了突发情况，详见异常事件记录。',
              abnormalEvents: '请描述具体异常情况...',
              pendingTasks: '下班次需要继续关注...',
              remarks: ''
            }
            break
          case 2:
            template = {
              workSummary: '本班次协助设备维修，相关工作正常进行。',
              abnormalEvents: '',
              pendingTasks: '设备维修预计明日完成，需持续关注',
              remarks: '维修期间加强巡检频次'
            }
            break
        }
        
        this.setData({
          logForm: template
        })
      }
    })
  },

  // 语音输入（模拟）
  voiceInput() {
    wx.showToast({
      title: '语音输入功能开发中',
      icon: 'none'
    })
  }
})
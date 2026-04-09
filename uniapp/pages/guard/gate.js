// 保安门岗管理页面逻辑
Page({
  data: {
    // 标签页
    activeTab: 'visitor',
    
    // 访客相关
    scannedVisitor: null,
    todayVisitorRecords: [],
    
    // 车辆相关
    vehicleForm: {
      plateNumber: '',
      ownerName: '',
      phone: '',
      visitingOwner: '',
      purpose: '',
      stayDurationIndex: 0
    },
    stayDurations: [
      { label: '2小时内', value: 2 },
      { label: '半天(4小时)', value: 4 },
      { label: '全天(8小时)', value: 8 },
      { label: '过夜(24小时)', value: 24 },
      { label: '长期停放', value: 0 }
    ],
    submittingVehicle: false,
    todayVehicleRecords: [],
    
    // 弹窗
    showContactModal: false,
    contactOwner: null
  },

  onLoad(options) {
    this.loadTodayVisitorRecords()
    this.loadTodayVehicleRecords()
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(1)
    }
    
    this.loadTodayVisitorRecords()
    this.loadTodayVehicleRecords()
  },

  onPullDownRefresh() {
    this.loadTodayVisitorRecords()
    this.loadTodayVehicleRecords()
    wx.stopPullDownRefresh()
  },

  // 切换标签页
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab
    })
  },

  // 扫描二维码
  scanQRCode() {
    wx.scanCode({
      scanType: ['qrCode'],
      success: (res) => {
        this.processQRCode(res.result)
      },
      fail: () => {
        wx.showToast({
          title: '扫码失败，请重试',
          icon: 'none'
        })
      }
    })
  },

  // 处理二维码结果
  processQRCode(qrData) {
    try {
      // 模拟解析二维码数据
      const visitorData = this.parseVisitorQR(qrData)
      
      if (visitorData) {
        this.setData({
          scannedVisitor: visitorData
        })
      } else {
        wx.showToast({
          title: '无效的访客二维码',
          icon: 'none'
        })
      }
    } catch (error) {
      wx.showToast({
        title: '二维码解析失败',
        icon: 'none'
      })
    }
  },

  // 解析访客二维码（模拟）
  parseVisitorQR(qrData) {
    // 模拟二维码数据解析
    const mockVisitorData = {
      id: 'V' + Date.now(),
      visitorName: '张三',
      phone: '13800138000',
      ownerName: '李四',
      roomNumber: 'A座1201',
      startTime: '2024-01-15 09:00',
      endTime: '2024-01-15 18:00',
      purpose: '拜访朋友',
      status: Math.random() > 0.3 ? 'valid' : 'invalid' // 70%概率有效
    }
    
    return mockVisitorData
  },

  // 允许通行
  allowEntry() {
    if (!this.data.scannedVisitor) return
    
    const visitor = this.data.scannedVisitor
    
    // 记录访客入园
    const entryRecord = {
      id: Date.now(),
      visitorName: visitor.visitorName,
      phone: visitor.phone,
      ownerName: visitor.ownerName,
      roomNumber: visitor.roomNumber,
      purpose: visitor.purpose,
      entryTime: this.getCurrentTime(),
      status: 'entered'
    }
    
    // 更新今日记录
    const newRecords = [entryRecord, ...this.data.todayVisitorRecords]
    this.setData({
      todayVisitorRecords: newRecords,
      scannedVisitor: null
    })
    
    wx.showToast({
      title: '访客已放行',
      icon: 'success'
    })
  },

  // 拒绝通行
  denyEntry() {
    if (!this.data.scannedVisitor) return
    
    const visitor = this.data.scannedVisitor
    
    // 记录拒绝访问
    const denyRecord = {
      id: Date.now(),
      visitorName: visitor.visitorName,
      phone: visitor.phone,
      ownerName: visitor.ownerName,
      roomNumber: visitor.roomNumber,
      purpose: visitor.purpose,
      entryTime: this.getCurrentTime(),
      status: 'denied'
    }
    
    // 更新今日记录
    const newRecords = [denyRecord, ...this.data.todayVisitorRecords]
    this.setData({
      todayVisitorRecords: newRecords,
      scannedVisitor: null
    })
    
    wx.showToast({
      title: '已拒绝访客通行',
      icon: 'none'
    })
  },

  // 联系业主
  contactOwner() {
    if (!this.data.scannedVisitor) return
    
    const visitor = this.data.scannedVisitor
    
    // 模拟获取业主联系方式
    const ownerContact = {
      name: visitor.ownerName,
      roomNumber: visitor.roomNumber,
      phone: '13800138001' // 模拟业主电话
    }
    
    this.setData({
      contactOwner: ownerContact,
      showContactModal: true
    })
  },

  // 拨打电话
  callOwner() {
    if (!this.data.contactOwner) return
    
    wx.makePhoneCall({
      phoneNumber: this.data.contactOwner.phone,
      success: () => {
        this.closeContactModal()
      }
    })
  },

  // 关闭联系业主弹窗
  closeContactModal() {
    this.setData({
      showContactModal: false,
      contactOwner: null
    })
  },

  // 车辆表单输入
  onVehicleInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    
    this.setData({
      [`vehicleForm.${field}`]: value
    })
  },

  // 选择停留时间
  onStayDurationChange(e) {
    this.setData({
      'vehicleForm.stayDurationIndex': parseInt(e.detail.value)
    })
  },

  // 提交车辆登记
  submitVehicleForm() {
    const { vehicleForm } = this.data
    
    // 表单验证
    if (!vehicleForm.plateNumber.trim()) {
      wx.showToast({
        title: '请输入车牌号码',
        icon: 'none'
      })
      return
    }
    
    if (!vehicleForm.ownerName.trim()) {
      wx.showToast({
        title: '请输入车主姓名',
        icon: 'none'
      })
      return
    }
    
    if (!vehicleForm.phone.trim()) {
      wx.showToast({
        title: '请输入联系电话',
        icon: 'none'
      })
      return
    }
    
    this.setData({ submittingVehicle: true })
    
    // 模拟提交
    setTimeout(() => {
      const vehicleRecord = {
        id: Date.now(),
        plateNumber: vehicleForm.plateNumber,
        ownerName: vehicleForm.ownerName,
        phone: vehicleForm.phone,
        visitingOwner: vehicleForm.visitingOwner,
        purpose: vehicleForm.purpose,
        stayDuration: this.data.stayDurations[vehicleForm.stayDurationIndex].label,
        entryTime: this.getCurrentTime(),
        status: 'parked'
      }
      
      // 更新今日记录
      const newRecords = [vehicleRecord, ...this.data.todayVehicleRecords]
      this.setData({
        todayVehicleRecords: newRecords,
        submittingVehicle: false,
        vehicleForm: {
          plateNumber: '',
          ownerName: '',
          phone: '',
          visitingOwner: '',
          purpose: '',
          stayDurationIndex: 0
        }
      })
      
      wx.showToast({
        title: '车辆登记成功',
        icon: 'success'
      })
    }, 2000)
  },

  // 标记车辆离场
  markVehicleLeft(e) {
    const id = e.currentTarget.dataset.id
    const records = [...this.data.todayVehicleRecords]
    
    const index = records.findIndex(record => record.id === id)
    if (index !== -1) {
      records[index].status = 'left'
      records[index].exitTime = this.getCurrentTime()
      
      this.setData({
        todayVehicleRecords: records
      })
      
      wx.showToast({
        title: '已标记车辆离场',
        icon: 'success'
      })
    }
  },

  // 加载今日访客记录
  loadTodayVisitorRecords() {
    // 模拟今日访客记录
    const mockRecords = [
      {
        id: 1,
        visitorName: '王五',
        phone: '13800138888',
        ownerName: '赵六',
        roomNumber: 'B座806',
        purpose: '维修空调',
        entryTime: '14:30',
        status: 'entered'
      },
      {
        id: 2,
        visitorName: '陈七',
        phone: '13800139999',
        ownerName: '孙八',
        roomNumber: 'C座1505',
        purpose: '送快递',
        entryTime: '11:20',
        status: 'left'
      },
      {
        id: 3,
        visitorName: '刘九',
        phone: '13800137777',
        ownerName: '周十',
        roomNumber: 'A座302',
        purpose: '串门',
        entryTime: '09:45',
        status: 'denied'
      }
    ]
    
    this.setData({
      todayVisitorRecords: mockRecords
    })
  },

  // 加载今日车辆记录
  loadTodayVehicleRecords() {
    // 模拟今日车辆记录
    const mockRecords = [
      {
        id: 1,
        plateNumber: '京A12345',
        ownerName: '李明',
        phone: '13800138001',
        visitingOwner: '张三',
        purpose: '拜访朋友',
        stayDuration: '全天(8小时)',
        entryTime: '08:30',
        status: 'parked'
      },
      {
        id: 2,
        plateNumber: '京B67890',
        ownerName: '王红',
        phone: '13800138002',
        visitingOwner: '',
        purpose: '临时停车',
        stayDuration: '2小时内',
        entryTime: '12:15',
        exitTime: '13:45',
        status: 'left'
      }
    ]
    
    this.setData({
      todayVehicleRecords: mockRecords
    })
  },

  // 获取当前时间
  getCurrentTime() {
    const now = new Date()
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    return `${hours}:${minutes}`
  }
})
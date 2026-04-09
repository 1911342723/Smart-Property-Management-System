// 保安异常上报页面逻辑
Page({
  data: {
    // 选中的事件类型
    selectedType: '',
    
    // 事件类型名称映射
    typeNames: {
      security: '安全事故',
      fire: '火灾隐患', 
      facility: '设施损坏',
      dispute: '人员纠纷',
      environment: '环境问题',
      other: '其他事件'
    },
    
    // 上报表单
    reportForm: {
      location: '',
      description: '',
      urgency: 'medium',
      photos: [],
      voiceRecords: []
    },
    
    // 状态名称映射
    statusNames: {
      pending: '待处理',
      processing: '处理中',
      completed: '已完成'
    },
    
    // 紧急程度名称映射
    urgencyNames: {
      low: '一般',
      medium: '紧急', 
      high: '非常紧急'
    },
    
    // 提交状态
    submitting: false,
    
    // 语音录制状态
    recording: false,
    recorderManager: null,
    
    // 今日上报记录
    todayReports: [],
    
    // 弹窗
    showReportModal: false,
    selectedReport: null
  },

  onLoad(options) {
    this.initRecorderManager()
    this.loadTodayReports()
  },

  onShow() {
    this.loadTodayReports()
  },

  onPullDownRefresh() {
    this.loadTodayReports()
    wx.stopPullDownRefresh()
  },

  // 初始化录音管理器
  initRecorderManager() {
    const recorderManager = wx.getRecorderManager()
    
    recorderManager.onStart(() => {
      console.log('录音开始')
    })
    
    recorderManager.onStop((res) => {
      console.log('录音停止', res)
      
      // 模拟音频时长
      const duration = Math.floor(Math.random() * 30) + 5
      
      const voiceRecord = {
        tempFilePath: res.tempFilePath,
        duration: duration,
        playing: false
      }
      
      const newRecords = [...this.data.reportForm.voiceRecords, voiceRecord]
      this.setData({
        'reportForm.voiceRecords': newRecords,
        recording: false
      })
    })
    
    this.setData({ recorderManager })
  },

  // 选择事件类型
  selectType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({
      selectedType: type
    })
  },

  // 表单输入
  onFormInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    
    this.setData({
      [`reportForm.${field}`]: value
    })
  },

  // 选择紧急程度
  selectUrgency(e) {
    const urgency = e.currentTarget.dataset.urgency
    this.setData({
      'reportForm.urgency': urgency
    })
  },

  // 选择图片
  chooseImage() {
    const maxCount = 9 - this.data.reportForm.photos.length
    
    wx.chooseMedia({
      count: maxCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFiles.map(file => file.tempFilePath)
        
        const newPhotos = [...this.data.reportForm.photos, ...tempFilePaths]
        this.setData({
          'reportForm.photos': newPhotos
        })
      }
    })
  },

  // 删除图片
  deletePhoto(e) {
    const index = e.currentTarget.dataset.index
    const photos = [...this.data.reportForm.photos]
    photos.splice(index, 1)
    
    this.setData({
      'reportForm.photos': photos
    })
  },

  // 预览图片
  previewImage(e) {
    const src = e.currentTarget.dataset.src
    const urls = this.data.selectedReport ? 
      this.data.selectedReport.photos : 
      this.data.reportForm.photos
    
    wx.previewImage({
      current: src,
      urls: urls
    })
  },

  // 开始录音
  startRecord() {
    this.setData({ recording: true })
    
    this.data.recorderManager.start({
      duration: 60000, // 最长60秒
      sampleRate: 16000,
      numberOfChannels: 1,
      encodeBitRate: 96000,
      format: 'mp3'
    })
  },

  // 停止录音
  stopRecord() {
    if (this.data.recording) {
      this.data.recorderManager.stop()
    }
  },

  // 播放语音
  playVoice(e) {
    const index = e.currentTarget.dataset.index
    const voiceRecords = [...this.data.reportForm.voiceRecords]
    const voice = voiceRecords[index]
    
    if (voice.playing) {
      // 停止播放
      wx.stopVoice()
      voice.playing = false
    } else {
      // 停止其他语音播放
      voiceRecords.forEach(v => v.playing = false)
      
      // 播放当前语音
      wx.playVoice({
        filePath: voice.tempFilePath,
        success: () => {
          voice.playing = false
          this.setData({ 'reportForm.voiceRecords': voiceRecords })
        }
      })
      voice.playing = true
    }
    
    this.setData({
      'reportForm.voiceRecords': voiceRecords
    })
  },

  // 删除语音
  deleteVoice(e) {
    const index = e.currentTarget.dataset.index
    const voiceRecords = [...this.data.reportForm.voiceRecords]
    voiceRecords.splice(index, 1)
    
    this.setData({
      'reportForm.voiceRecords': voiceRecords
    })
  },

  // 提交上报
  submitReport() {
    const { selectedType, reportForm } = this.data
    
    // 表单验证
    if (!selectedType) {
      wx.showToast({
        title: '请选择事件类型',
        icon: 'none'
      })
      return
    }
    
    if (!reportForm.location.trim()) {
      wx.showToast({
        title: '请填写事件地点',
        icon: 'none'
      })
      return
    }
    
    if (!reportForm.description.trim()) {
      wx.showToast({
        title: '请填写事件描述',
        icon: 'none'
      })
      return
    }
    
    this.setData({ submitting: true })
    
    // 模拟提交
    setTimeout(() => {
      wx.showToast({
        title: '上报成功',
        icon: 'success'
      })
      
      // 重置表单
      this.setData({
        selectedType: '',
        reportForm: {
          location: '',
          description: '',
          urgency: 'medium',
          photos: [],
          voiceRecords: []
        },
        submitting: false
      })
      
      // 刷新记录
      this.loadTodayReports()
    }, 2000)
  },

  // 查看上报详情
  viewReport(e) {
    const report = e.currentTarget.dataset.report
    this.setData({
      selectedReport: report,
      showReportModal: true
    })
  },

  // 关闭上报详情
  closeReportModal() {
    this.setData({
      showReportModal: false,
      selectedReport: null
    })
  },

  // 跳转历史记录
  goToHistory() {
    wx.navigateTo({
      url: '/pages/guard/report-history'
    })
  },

  // 加载今日上报记录
  loadTodayReports() {
    // 模拟今日上报记录
    const mockReports = [
      {
        id: 1,
        type: 'facility',
        location: '小区东门',
        description: '东门门禁系统故障，无法正常刷卡进入',
        urgency: 'high',
        status: 'processing',
        createTime: '14:30',
        photos: ['/static/images/repair1.jpg'],
        handleResult: ''
      },
      {
        id: 2,
        type: 'environment', 
        location: 'B栋垃圾站',
        description: 'B栋垃圾站垃圾溢出，散发异味影响居民',
        urgency: 'medium',
        status: 'completed',
        createTime: '11:15',
        photos: [],
        handleResult: '已联系保洁员清理，垃圾站已恢复正常'
      },
      {
        id: 3,
        type: 'security',
        location: '地下车库',
        description: '发现可疑人员在地下车库徘徊，行为异常',
        urgency: 'high',
        status: 'completed',
        createTime: '09:45',
        photos: ['/static/images/repair2.jpg'],
        handleResult: '已核实身份，为新业主熟悉环境，无异常情况'
      }
    ]
    
    this.setData({
      todayReports: mockReports
    })
  }
})
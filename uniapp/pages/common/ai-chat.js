// pages/common/ai-chat.js
const api = require('../../utils/api.js')

Page({
  data: {
    rooms: [],
    roomNames: [],
    selectedRoomIndex: 0,
    selectedRoomName: '',
    selectedRoomId: null,
    
    uploadedImageUrl: '',
    analysisResult: null,
    
    analyzing: false,
    submitting: false,
    
    categoryMap: {
      'REPAIR': '维修',
      'COMPLAINT': '投诉',
      'SUGGESTION': '建议'
    },
    
    priorityMap: {
      'LOW': '低',
      'MEDIUM': '中',
      'HIGH': '高',
      'URGENT': '紧急'
    }
  },

  onLoad(options) {
    // 获取状态栏高度
    const systemInfo = wx.getSystemInfoSync()
    const statusBarHeight = systemInfo.statusBarHeight || 20
    // 设置CSS变量
    wx.nextTick(() => {
      const query = wx.createSelectorQuery()
      query.select('.ai-chat-container').boundingClientRect()
      query.exec((res) => {
        if (res[0]) {
          this.setData({
            statusBarHeight: statusBarHeight
          })
        }
      })
    })
    
    this.loadUserRooms()
  },

  /**
   * 加载用户房屋列表
   */
  async loadUserRooms() {
    try {
      const response = await api.getMyRooms()
      
      if (response && response.data) {
        const rooms = response.data
        const roomNames = rooms.map(room => {
          return `${room.buildingName || ''}${room.unitName || ''}${room.roomNo || ''}`
        })
        
        this.setData({
          rooms: rooms,
          roomNames: roomNames,
          selectedRoomName: roomNames.length > 0 ? roomNames[0] : '',
          selectedRoomId: rooms.length > 0 ? rooms[0].id : null
        })
      }
    } catch (error) {
      console.error('加载房屋列表失败:', error)
    }
  },

  /**
   * 选择房屋
   */
  onRoomChange(e) {
    const index = e.detail.value
    this.setData({
      selectedRoomIndex: index,
      selectedRoomName: this.data.roomNames[index],
      selectedRoomId: this.data.rooms[index].id
    })
  },

  /**
   * 选择图片
   */
  chooseImage() {
    const that = this
    
    wx.showActionSheet({
      itemList: ['拍照', '从相册选择'],
      success(res) {
        let sourceType = []
        if (res.tapIndex === 0) {
          sourceType = ['camera']
        } else {
          sourceType = ['album']
        }
        
        wx.chooseImage({
          count: 1,
          sizeType: ['compressed'],
          sourceType: sourceType,
          success(res) {
            const tempFilePath = res.tempFilePaths[0]
            that.analyzeImage(tempFilePath)
          }
        })
      }
    })
  },

  /**
   * AI分析图片
   */
  async analyzeImage(filePath) {
    if (!this.data.selectedRoomId) {
      wx.showToast({
        title: '请先选择房屋',
        icon: 'none'
      })
      return
    }

    this.setData({
      analyzing: true,
      analysisResult: null
    })

    try {
      const params = {
        roomId: this.data.selectedRoomId,
        category: 'REPAIR'
      }
      
      console.log('调用AI分析接口, roomId:', this.data.selectedRoomId)
      const response = await api.aiAnalyzeImage(filePath, params)
      console.log('AI分析响应:', response)
      
      if (response && response.data) {
        this.setData({
          uploadedImageUrl: response.data.imageUrl,
          analysisResult: response.data,
          analyzing: false
        })
        
        wx.showToast({
          title: 'AI分析完成',
          icon: 'success'
        })
      } else {
        throw new Error('分析失败')
      }
    } catch (error) {
      console.error('AI分析失败:', error)
      this.setData({ analyzing: false })
      
      wx.showModal({
        title: '分析失败',
        content: error.message || 'AI分析图片失败，请重试',
        showCancel: false
      })
    }
  },

  /**
   * 提交工单 - 使用AI生成的工单信息
   */
  async submitWorkOrder() {
    if (!this.data.analysisResult) {
      wx.showToast({
        title: '请先分析图片',
        icon: 'none'
      })
      return
    }

    this.setData({ submitting: true })

    try {
      // 使用AI接口创建工单，后端会自动设置submitterId
      const workOrderData = {
        title: this.data.analysisResult.title,
        content: this.data.analysisResult.description,
        category: this.data.analysisResult.suggestedCategory,
        priority: this.data.analysisResult.suggestedPriority,
        roomId: this.data.selectedRoomId,
        images: this.data.uploadedImageUrl
      }
      
      console.log('提交AI工单:', workOrderData)
      
      // 调用AI工单创建接口
      const response = await api.createWorkOrderFromAI(workOrderData)
      console.log('AI工单提交响应:', response)
      
      if (response && response.code === 200) {
        wx.showToast({
          title: '工单提交成功',
          icon: 'success',
          duration: 2000
        })
        
        this.setData({ submitting: false })
        
        // 延迟跳转到工单列表
        setTimeout(() => {
          wx.navigateBack()
        }, 2000)
      } else {
        throw new Error(response.message || '提交失败')
      }
    } catch (error) {
      console.error('提交工单失败:', error)
      this.setData({ submitting: false })
      
      wx.showModal({
        title: '提交失败',
        content: error.message || '工单提交失败，请重试',
        showCancel: false
      })
    }
  },

  /**
   * 返回上一页
   */
  goBack() {
    wx.navigateBack()
  }
})
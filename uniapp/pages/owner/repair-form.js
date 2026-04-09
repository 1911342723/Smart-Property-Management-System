// pages/owner/repair-form.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    categoryId: '',
    isQuick: false,
    formData: {
      title: '',
      content: '',
      category: 'REPAIR',
      priority: 'MEDIUM',
      images: [],
      phone: '',
      appointmentTime: ''
    },
    categoryInfo: {},
    priorities: [
      { value: 'LOW', label: '低', color: '#718096' },
      { value: 'MEDIUM', label: '中', color: '#d69e2e' },
      { value: 'HIGH', label: '高', color: '#e53e3e' },
      { value: 'URGENT', label: '紧急', color: '#c53030' }
    ],
    timeRange: [
      ['今天', '明天', '后天'],
      ['08:00-10:00', '10:00-12:00', '14:00-16:00', '16:00-18:00', '18:00-20:00']
    ],
    timeIndex: [0, 0],
    selectedTime: '',
    submitting: false,
    canSubmit: false
  },

  onLoad(options) {
    console.log('报修表单页面加载', options)
    
    const categoryId = options.category || '1'
    const isQuick = options.quick === 'true'
    
    this.setData({
      categoryId,
      isQuick
    })
    
    this.initCategoryInfo()
    this.loadUserInfo()
    
    if (isQuick) {
      this.initQuickRepair()
    }
    
    // 初始化预约时间显示
    this.updateSelectedTime()
    // 初始化提交状态检查
    this.checkCanSubmit()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = app.globalData.userInfo
    if (userInfo && userInfo.phone) {
      this.setData({
        'formData.phone': userInfo.phone
      })
    }
  },

  // 初始化类别信息
  initCategoryInfo() {
    const categories = {
      '1': { name: '水电维修', description: '水管漏水、电路故障等' },
      '2': { name: '家电维修', description: '冰箱、洗衣机、热水器等' },
      '3': { name: '门窗维修', description: '门锁损坏、窗户故障等' },
      '4': { name: '空调维修', description: '空调不制冷、异响等' },
      '5': { name: '网络维修', description: '网络故障、弱电维修等' },
      '6': { name: '其他维修', description: '其他各类维修需求' }
    }
    
    const categoryInfo = categories[this.data.categoryId] || categories['6']
    this.setData({ categoryInfo })
    
    wx.setNavigationBarTitle({
      title: categoryInfo.name
    })
  },

  // 初始化快速报修
  initQuickRepair() {
    const quickTemplates = {
      '1': {
        title: '水电设施故障',
        content: '请描述具体的水电问题，如：漏水位置、电路故障现象等'
      },
      '2': {
        title: '家电设备故障',
        content: '请描述家电故障现象，如：设备型号、故障表现等'
      },
      '3': {
        title: '门窗设施故障',
        content: '请描述门窗问题，如：门锁损坏、窗户关闭异常等'
      },
      '4': {
        title: '空调设备故障',
        content: '请描述空调问题，如：不制冷、异响、漏水等'
      },
      '5': {
        title: '网络设施故障',
        content: '请描述网络问题，如：网络中断、弱电故障等'
      },
      '6': {
        title: '其他维修需求',
        content: '请详细描述需要维修的问题'
      }
    }
    
    const template = quickTemplates[this.data.categoryId] || quickTemplates['6']
    this.setData({
      'formData.title': template.title,
      'formData.content': template.content
    }, () => {
      this.checkCanSubmit()
    })
  },

  // 标题输入
  onTitleInput(e) {
    this.setData({
      'formData.title': e.detail.value
    }, () => {
      this.checkCanSubmit()
    })
  },

  // 内容输入
  onContentInput(e) {
    this.setData({
      'formData.content': e.detail.value
    }, () => {
      this.checkCanSubmit()
    })
  },

  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    })
  },

  // 时间选择
  onTimeChange(e) {
    const timeIndex = e.detail.value
    this.setData({
      timeIndex
    }, () => {
      this.updateSelectedTime()
    })
  },

  // 更新选择的时间显示
  updateSelectedTime() {
    const { timeRange, timeIndex } = this.data
    const selectedTime = `${timeRange[0][timeIndex[0]]} ${timeRange[1][timeIndex[1]]}`
    this.setData({ selectedTime })
  },

  // 检查是否可以提交
  checkCanSubmit() {
    const { title, content } = this.data.formData
    const canSubmit = title.trim().length > 0 && content.trim().length > 0
    this.setData({ canSubmit })
  },

  // 选择优先级
  selectPriority(e) {
    const priority = e.currentTarget.dataset.priority
    this.setData({
      'formData.priority': priority
    })
  },

  // 选择图片
  chooseImage() {
    const maxCount = 3 - this.data.formData.images.length
    
    if (maxCount <= 0) {
      wx.showToast({
        title: '最多只能上传3张图片',
        icon: 'none'
      })
      return
    }

    wx.chooseImage({
      count: maxCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = res.tempFilePaths
        const currentImages = this.data.formData.images
        
        this.setData({
          'formData.images': [...currentImages, ...newImages]
        })
      },
      fail: (error) => {
        console.error('选择图片失败:', error)
        wx.showToast({
          title: '选择图片失败',
          icon: 'none'
        })
      }
    })
  },

  // 预览图片
  previewImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.formData.images
    
    wx.previewImage({
      current: images[index],
      urls: images
    })
  },

  // 删除图片
  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.formData.images
    images.splice(index, 1)
    
    this.setData({
      'formData.images': images
    })
  },

  // 提交报修
  async submitRepair() {
    if (this.data.submitting) return
    
    // 验证表单
    const { title, content } = this.data.formData
    
    if (!title.trim()) {
      wx.showToast({
        title: '请输入报修标题',
        icon: 'none'
      })
      return
    }
    
    if (!content.trim()) {
      wx.showToast({
        title: '请描述具体问题',
        icon: 'none'
      })
      return
    }
    
    this.setData({ submitting: true })
    
    try {
      const userInfo = app.globalData.userInfo
      
      if (!userInfo.id) {
        throw new Error('用户信息不完整，请重新登录')
      }

      // TODO: 如果有图片，先上传图片
      let imageUrls = []
      if (this.data.formData.images.length > 0) {
        imageUrls = await this.uploadImages(this.data.formData.images)
      }

      // 构建提交数据
      const submitData = {
        title: title.trim(),
        content: content.trim(),
        category: this.data.formData.category,
        priority: this.data.formData.priority,
        submitterId: userInfo.id,
        roomId: this.getUserRoomId(), // 获取用户房屋ID
        phone: this.data.formData.phone,
        appointmentTime: this.data.selectedTime,
        images: imageUrls.length > 0 ? JSON.stringify(imageUrls) : null
      }

      console.log('提交工单数据:', submitData)

      // 调用API提交工单
      const response = await api.submitWorkOrder(submitData)
      
      if (response.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        })
        
        // 延迟返回上一页
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        throw new Error(response.message || '提交失败')
      }
      
    } catch (error) {
      console.error('提交报修失败:', error)
      wx.showToast({
        title: error.message || '提交失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 上传图片
  async uploadImages(imagePaths) {
    const uploadPromises = imagePaths.map(async (path) => {
      try {
        const response = await api.uploadFile(path, {
          type: 'repair'
        })
        
        if (response.code === 200 && response.data) {
          return response.data.url || response.data.path
        } else {
          throw new Error('上传失败')
        }
      } catch (error) {
        console.error('上传图片失败:', error)
        throw error
      }
    })
    
    try {
      const urls = await Promise.all(uploadPromises)
      return urls
    } catch (error) {
      wx.showToast({
        title: '图片上传失败',
        icon: 'none'
      })
      throw error
    }
  },

  // 获取用户房屋ID（简化处理，实际应该从用户房产信息中获取）
  getUserRoomId() {
    // 这里简化处理，返回一个默认房屋ID
    // 在实际应用中，应该从用户的房产信息中获取
    const userInfo = app.globalData.userInfo
    
    // 根据用户信息映射房屋ID
    const roomMapping = {
      'owner001': 1,
      'owner002': 2,
      'owner003': 3
    }
    
    return roomMapping[userInfo.username] || 1
  },

  // 取消提交
  cancelSubmit() {
    wx.showModal({
      title: '确认取消',
      content: '是否取消本次报修？',
      success: (res) => {
        if (res.confirm) {
          wx.navigateBack()
        }
      }
    })
  }
})
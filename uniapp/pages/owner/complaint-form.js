// pages/owner/complaint-form.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    selectedType: '',
    selectedUrgency: 'NORMAL',
    submitting: false,
    formData: {
      title: '',
      content: '',
      phone: '',
      images: []
    },
    complaintTypes: [
      {
        key: 'NOISE',
        name: '噪音投诉',
        icon: '/static/icons/bell.svg',
        gradient: 'linear-gradient(135deg, #e53e3e 0%, #c53030 100%)'
      },
      {
        key: 'HYGIENE',
        name: '环境卫生',
        icon: '/static/icons/settings.svg',
        gradient: 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)'
      },
      {
        key: 'FACILITY',
        name: '设施维护',
        icon: '/static/icons/wrench.svg',
        gradient: 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)'
      },
      {
        key: 'SERVICE',
        name: '物业服务',
        icon: '/static/icons/feedback.svg',
        gradient: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)'
      },
      {
        key: 'SECURITY',
        name: '安全问题',
        icon: '/static/icons/alert-circle.svg',
        gradient: 'linear-gradient(135deg, #e53e3e 0%, #c53030 100%)'
      },
      {
        key: 'OTHER',
        name: '其他问题',
        icon: '/static/icons/info.svg',
        gradient: 'linear-gradient(135deg, #718096 0%, #4a5568 100%)'
      }
    ],
    urgencyLevels: [
      {
        key: 'LOW',
        name: '低',
        description: '不紧急，可以稍后处理',
        color: '#38a169'
      },
      {
        key: 'NORMAL',
        name: '普通',
        description: '一般问题，正常处理',
        color: '#3182ce'
      },
      {
        key: 'HIGH',
        name: '高',
        description: '比较紧急，需要优先处理',
        color: '#d69e2e'
      },
      {
        key: 'URGENT',
        name: '紧急',
        description: '非常紧急，需要立即处理',
        color: '#e53e3e'
      }
    ]
  },

  onLoad(options) {
    console.log('投诉表单页面加载')
    
    // 从参数中获取预设类型和标题
    if (options.category) {
      this.setData({
        selectedType: options.category.toUpperCase()
      })
    }
    
    if (options.title) {
      this.setData({
        'formData.title': decodeURIComponent(options.title)
      })
    }

    // 获取用户信息，预填联系方式
    this.loadUserInfo()
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

  // 选择投诉类型
  selectType(e) {
    const type = e.currentTarget.dataset.type
    console.log('选择投诉类型:', type)
    this.setData({
      selectedType: type
    })
  },

  // 选择紧急程度
  selectUrgency(e) {
    const urgency = e.currentTarget.dataset.urgency
    console.log('选择紧急程度:', urgency)
    this.setData({
      selectedUrgency: urgency
    })
  },

  // 标题输入
  onTitleInput(e) {
    this.setData({
      'formData.title': e.detail.value
    })
  },

  // 内容输入
  onContentInput(e) {
    this.setData({
      'formData.content': e.detail.value
    })
  },

  // 电话输入
  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    })
  },

  // 选择图片
  chooseImage() {
    const remainingCount = 3 - this.data.formData.images.length
    
    wx.chooseImage({
      count: remainingCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        console.log('选择图片成功:', res)
        
        const newImages = [...this.data.formData.images, ...res.tempFilePaths]
        this.setData({
          'formData.images': newImages
        })
      },
      fail: (err) => {
        console.error('选择图片失败:', err)
        wx.showToast({
          title: '选择图片失败',
          icon: 'none'
        })
      }
    })
  },

  // 删除图片
  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    console.log('删除图片，索引:', index)
    
    const images = [...this.data.formData.images]
    images.splice(index, 1)
    
    this.setData({
      'formData.images': images
    })
  },

  // 表单验证
  validateForm() {
    const { selectedType, formData } = this.data
    
    if (!selectedType) {
      wx.showToast({
        title: '请选择投诉类型',
        icon: 'none'
      })
      return false
    }
    
    if (!formData.title.trim()) {
      wx.showToast({
        title: '请输入投诉标题',
        icon: 'none'
      })
      return false
    }
    
    if (!formData.content.trim()) {
      wx.showToast({
        title: '请输入详细描述',
        icon: 'none'
      })
      return false
    }
    
    return true
  },

  // 提交表单
  async onSubmit() {
    console.log('提交投诉表单')
    
    if (!this.validateForm()) {
      return
    }
    
    if (this.data.submitting) {
      return
    }
    
    this.setData({ submitting: true })
    
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }

      // 准备提交数据
      const submitData = {
        complainantId: userInfo.id,
        roomId: userInfo.roomId || null,
        complaintType: this.data.selectedType,
        title: this.data.formData.title.trim(),
        content: this.data.formData.content.trim(),
        urgencyLevel: this.data.selectedUrgency,
        images: JSON.stringify(this.data.formData.images)
      }
      
      console.log('提交投诉数据:', submitData)
      
      const response = await api.submitComplaint(submitData)
      console.log('提交投诉响应:', response)
      
      if (response.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success',
          duration: 2000
        })
        
        // 延迟返回上一页
        setTimeout(() => {
          wx.navigateBack()
        }, 2000)
      } else {
        wx.showToast({
          title: response.message || '提交失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('提交投诉失败:', error)
      wx.showToast({
        title: '提交失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '智慧物业投诉建议',
      path: '/pages/owner/complaint-form'
    }
  }
})


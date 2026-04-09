// pages/owner/rate-order.js
const api = require('../../utils/api.js')

Page({
  data: {
    statusBarHeight: 0,
    orderId: null,
    orderInfo: {},
    ratings: {
      overall: 5,
      serviceAttitude: 5,
      workQuality: 5,
      responseSpeed: 5,
      professionalism: 5
    },
    ratingTexts: ['非常不满意', '不满意', '一般', '满意', '非常满意'],
    availableTags: [
      { id: 1, name: '专业', selected: false },
      { id: 2, name: '态度好', selected: false },
      { id: 3, name: '准时', selected: false },
      { id: 4, name: '细心', selected: false },
      { id: 5, name: '高效', selected: false },
      { id: 6, name: '耐心', selected: false },
      { id: 7, name: '负责', selected: false },
      { id: 8, name: '技术好', selected: false }
    ],
    content: '',
    images: [],
    isAnonymous: false,
    submitting: false
  },

  onLoad(options) {
    // 获取状态栏高度
    const systemInfo = wx.getSystemInfoSync()
    this.setData({
      statusBarHeight: systemInfo.statusBarHeight || 20,
      orderId: options.orderId
    })

    if (options.orderId) {
      this.loadOrderInfo(options.orderId)
    }
  },

  // 加载工单信息
  async loadOrderInfo(orderId) {
    try {
      // 先检查是否已经评价过
      const ratingResponse = await api.getRatingByOrderId(orderId)
      if (ratingResponse.code === 200 && ratingResponse.data) {
        wx.showModal({
          title: '提示',
          content: '该工单已评价，不能重复评价',
          showCancel: false,
          confirmText: '我知道了',
          success: () => {
            wx.navigateBack()
          }
        })
        return
      }

      // 加载工单信息
      const response = await api.getWorkOrderDetail(orderId)
      
      if (response.code === 200 && response.data) {
        this.setData({
          orderInfo: {
            title: response.data.title,
            orderNo: response.data.orderNo,
            completeTime: response.data.completeTime,
            workerName: response.data.assigneeName,
            workerAvatar: response.data.assigneeAvatar,
            workerId: response.data.assigneeId
          }
        })
      }
    } catch (error) {
      console.error('加载工单信息失败:', error)
    }
  },

  // 设置总体评分
  setOverallRating(e) {
    const rating = e.currentTarget.dataset.rating
    this.setData({
      'ratings.overall': rating
    })
  },

  // 设置详细评分
  setDetailRating(e) {
    const { type, rating } = e.currentTarget.dataset
    this.setData({
      [`ratings.${type}`]: rating
    })
  },

  // 切换标签
  toggleTag(e) {
    const tagId = e.currentTarget.dataset.id
    const tags = this.data.availableTags
    const index = tags.findIndex(t => t.id === tagId)
    
    if (index > -1) {
      tags[index].selected = !tags[index].selected
      this.setData({
        availableTags: tags
      })
    }
  },

  // 输入评价内容
  onContentInput(e) {
    this.setData({
      content: e.detail.value
    })
  },

  // 选择图片
  chooseImage() {
    const maxCount = 9 - this.data.images.length
    
    wx.chooseImage({
      count: maxCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = [...this.data.images, ...res.tempFilePaths]
        this.setData({
          images: newImages
        })
      }
    })
  },

  // 删除图片
  removeImage(e) {
    const index = e.currentTarget.dataset.index
    const images = this.data.images
    images.splice(index, 1)
    this.setData({
      images: images
    })
  },

  // 切换匿名
  toggleAnonymous() {
    this.setData({
      isAnonymous: !this.data.isAnonymous
    })
  },

  // 提交评价
  async submitRating() {
    if (this.data.submitting) return

    const { ratings, content, images, isAnonymous, orderInfo, orderId } = this.data

    // 验证
    if (!content.trim()) {
      wx.showToast({
        title: '请填写评价内容',
        icon: 'none'
      })
      return
    }

    try {
      this.setData({ submitting: true })

      // 上传图片（如果有）
      let imageUrls = []
      if (images.length > 0) {
        wx.showLoading({ title: '上传图片中...' })
        
        for (let i = 0; i < images.length; i++) {
          try {
            const uploadRes = await api.uploadFile(images[i], { type: 'rating' })
            if (uploadRes.code === 200 && uploadRes.data) {
              imageUrls.push(uploadRes.data.url || uploadRes.data.filePath || uploadRes.data)
            }
          } catch (uploadError) {
            console.error('图片上传失败:', uploadError)
          }
        }
        
        wx.hideLoading()
      }

      // 获取选中的标签
      const selectedTags = this.data.availableTags
        .filter(t => t.selected)
        .map(t => t.name)

      // 构建评价数据
      const ratingData = {
        orderId: parseInt(orderId),
        workerId: orderInfo.workerId,
        overallScore: ratings.overall,
        serviceAttitudeScore: ratings.serviceAttitude,
        workQualityScore: ratings.workQuality,
        responseSpeedScore: ratings.responseSpeed,
        professionalismScore: ratings.professionalism,
        content: content.trim(),
        images: JSON.stringify(imageUrls),
        tags: JSON.stringify(selectedTags),
        isAnonymous: isAnonymous
      }

      // 提交评价
      const response = await api.rateWorkOrderDetail(ratingData)

      if (response.code === 200) {
        wx.showToast({
          title: '评价成功',
          icon: 'success'
        })

        // 延迟返回
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        throw new Error(response.message || '评价失败')
      }

    } catch (error) {
      console.error('提交评价失败:', error)
      
      let errorMsg = '评价失败，请重试'
      if (error.message) {
        if (error.message.includes('已评价') || error.message.includes('重复评价')) {
          errorMsg = '该工单已评价，不能重复评价'
        } else if (error.message.includes('Duplicate entry')) {
          errorMsg = '该工单已评价，不能重复评价'
        } else {
          errorMsg = error.message
        }
      }
      
      wx.showToast({
        title: errorMsg,
        icon: 'none',
        duration: 2000
      })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 返回
  goBack() {
    wx.navigateBack()
  }
})

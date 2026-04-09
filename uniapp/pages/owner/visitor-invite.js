// pages/owner/visitor-invite.js
const app = getApp()
const api = require('../../utils/api.js')
const QR = require('../../utils/weapp-qrcode.js')

Page({
  data: {
    formData: {
      visitorName: '',
      visitorPhone: '',
      visitPurpose: '',
      arrivalDate: '',
      arrivalTime: '',
      departureDate: '',
      departureTime: ''
    },
    minDate: '',
    maxDate: '',
    submitting: false,
    showQRModal: false,
    qrCodeData: {},
    qrCodeUrl: ''
  },

  onLoad(options) {
    console.log('访客邀请页面加载')
    this.initDateRange()
  },

  // 初始化日期范围
  initDateRange() {
    const now = new Date()
    const maxDate = new Date()
    maxDate.setMonth(maxDate.getMonth() + 1) // 最多提前一个月预约
    
    this.setData({
      minDate: this.formatDate(now),
      maxDate: this.formatDate(maxDate)
    })
  },

  // 格式化日期时间（专门用于picker的start和end属性）
  formatDateTimeForPicker(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  },

  // 格式化日期时间（用于picker）
  formatDateTime(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  },

  // 格式化日期
  formatDate(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  // 访客姓名输入
  onNameInput(e) {
    this.setData({
      'formData.visitorName': e.detail.value
    })
  },

  // 访客电话输入
  onPhoneInput(e) {
    this.setData({
      'formData.visitorPhone': e.detail.value
    })
  },


  // 访问目的输入
  onPurposeInput(e) {
    this.setData({
      'formData.visitPurpose': e.detail.value
    })
  },

  // 到达日期选择
  onArrivalDateChange(e) {
    this.setData({
      'formData.arrivalDate': e.detail.value
    })
  },

  // 到达时间选择
  onArrivalTimeChange(e) {
    this.setData({
      'formData.arrivalTime': e.detail.value
    })
  },

  // 离开日期选择
  onDepartureDateChange(e) {
    this.setData({
      'formData.departureDate': e.detail.value
    })
  },

  // 离开时间选择
  onDepartureTimeChange(e) {
    this.setData({
      'formData.departureTime': e.detail.value
    })
  },


  // 表单验证
  validateForm() {
    const { formData } = this.data
    
    if (!formData.visitorName.trim()) {
      wx.showToast({
        title: '请输入访客姓名',
        icon: 'none'
      })
      return false
    }
    
    if (!formData.visitorPhone.trim()) {
      wx.showToast({
        title: '请输入访客电话',
        icon: 'none'
      })
      return false
    }
    
    // 手机号格式验证
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(formData.visitorPhone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return false
    }
    
    if (!formData.arrivalDate) {
      wx.showToast({
        title: '请选择到达日期',
        icon: 'none'
      })
      return false
    }
    
    if (!formData.arrivalTime) {
      wx.showToast({
        title: '请选择到达时间',
        icon: 'none'
      })
      return false
    }
    
    // 检查到达时间不能早于当前时间
    const arrivalDateTime = `${formData.arrivalDate} ${formData.arrivalTime}`
    const arrivalTime = new Date(arrivalDateTime.replace(/-/g, '/'))
    const now = new Date()
    if (arrivalTime <= now) {
      wx.showToast({
        title: '到达时间不能早于当前时间',
        icon: 'none'
      })
      return false
    }
    
    // 检查离开时间必须晚于到达时间
    if (formData.departureDate && formData.departureTime) {
      const departureDateTime = `${formData.departureDate} ${formData.departureTime}`
      const departureTime = new Date(departureDateTime.replace(/-/g, '/'))
      if (departureTime <= arrivalTime) {
        wx.showToast({
          title: '离开时间必须晚于到达时间',
          icon: 'none'
        })
        return false
      }
    }
    
    return true
  },

  // 提交表单
  async onSubmit() {
    console.log('提交访客邀请')
    
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
      const { formData } = this.data
      const plannedArrival = `${formData.arrivalDate} ${formData.arrivalTime}:00`
      const plannedDeparture = (formData.departureDate && formData.departureTime) 
        ? `${formData.departureDate} ${formData.departureTime}:00` 
        : null
      
      const submitData = {
        phone: formData.visitorPhone.trim(),
        visitorName: formData.visitorName.trim(),
        ownerId: userInfo.id,
        ownerPhone: userInfo.phone,
        visitPurpose: formData.visitPurpose.trim() || null,
        plannedArrival: plannedArrival,
        plannedDeparture: plannedDeparture
      }
      
      console.log('提交访客数据:', submitData)
      
      const response = await api.createVisitorQrCode(submitData)
      console.log('提交访客响应:', response)
      
      if (response.code === 200) {
        // 邀请成功，显示二维码
        this.showQRCodeModal(response.data)
      } else {
        wx.showToast({
          title: response.message || '邀请失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('提交访客邀请失败:', error)
      wx.showToast({
        title: '邀请失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 显示二维码弹窗
  showQRCodeModal(qrCodeData) {
    console.log('显示二维码:', qrCodeData)
    
    this.setData({
      showQRModal: true,
      qrCodeData: qrCodeData,
      qrCodeContent: qrCodeData.qrContent || qrCodeData.qrCode
    })
    
    // 生成二维码图片
    this.generateQRCode(qrCodeData.qrCode)
  },

  // 生成二维码
  generateQRCode(content) {
    console.log('开始生成二维码:', content)
    
    // 延迟执行，确保DOM已经渲染
    setTimeout(() => {
      const query = wx.createSelectorQuery().in(this)
      query.select('#qrCanvas')
        .fields({ node: true, size: true })
        .exec((res) => {
          console.log('Canvas查询结果:', res)
          
          if (res && res[0] && res[0].node) {
            const canvas = res[0].node
            
            try {
              // 使用正规的二维码库生成
              QR.drawQrcode({
                canvas: canvas,
                text: content,
                size: 200,
                margin: 10,
                backgroundColor: '#ffffff',
                foregroundColor: '#000000'
              })
              
              console.log('二维码绘制完成')
              
              // 转换为图片
              wx.canvasToTempFilePath({
                canvas: canvas,
                success: (result) => {
                  console.log('二维码生成成功:', result.tempFilePath)
                  this.setData({
                    qrCodeUrl: result.tempFilePath
                  })
                },
                fail: (err) => {
                  console.error('Canvas转图片失败:', err)
                  this.generateSimpleQR(content)
                }
              })
            } catch (error) {
              console.error('二维码生成异常:', error)
              this.generateSimpleQR(content)
            }
          } else {
            console.error('无法找到canvas节点')
            this.generateSimpleQR(content)
          }
        })
    }, 300)
  },

  // 生成简单的二维码（备用方案）
  generateSimpleQR(content) {
    console.log('使用备用方案生成二维码')
    
    // 创建一个简单的二维码图片URL（可以使用在线二维码服务）
    const qrText = encodeURIComponent(content)
    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${qrText}`
    
    // 下载二维码图片到本地
    wx.downloadFile({
      url: qrUrl,
      success: (res) => {
        if (res.statusCode === 200) {
          console.log('在线二维码下载成功:', res.tempFilePath)
          this.setData({
            qrCodeUrl: res.tempFilePath
          })
        } else {
          console.error('在线二维码下载失败:', res.statusCode)
          this.showQRCodeText(content)
        }
      },
      fail: (err) => {
        console.error('二维码下载失败:', err)
        this.showQRCodeText(content)
      }
    })
  },

  // 显示二维码文本（最后备用方案）
  showQRCodeText(content) {
    console.log('显示二维码文本内容')
    wx.showModal({
      title: '二维码内容',
      content: content,
      showCancel: false,
      confirmText: '复制',
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: content,
            success: () => {
              wx.showToast({
                title: '已复制到剪贴板',
                icon: 'success'
              })
            }
          })
        }
      }
    })
  },

  // 隐藏二维码弹窗
  hideQRCode() {
    this.setData({
      showQRModal: false,
      qrCodeData: {},
      qrCodeUrl: ''
    })
  },

  // 保存二维码到相册
  saveQRCode() {
    if (!this.data.qrCodeUrl) {
      wx.showToast({
        title: '二维码生成中...',
        icon: 'none'
      })
      return
    }

    wx.saveImageToPhotosAlbum({
      filePath: this.data.qrCodeUrl,
      success: () => {
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })
      },
      fail: (err) => {
        if (err.errMsg.includes('auth deny')) {
          wx.showModal({
            title: '提示',
            content: '需要授权访问相册才能保存二维码',
            success: (res) => {
              if (res.confirm) {
                wx.openSetting()
              }
            }
          })
        } else {
          wx.showToast({
            title: '保存失败',
            icon: 'none'
          })
        }
      }
    })
  },

  // 分享二维码
  shareQRCode() {
    const { qrCodeData } = this.data
    
    const shareContent = `访客通行码
访客：${qrCodeData.visitorName}
电话：${qrCodeData.visitorPhone}
访问时间：${this.formatDateTime(qrCodeData.plannedArrival)}
通行码：${qrCodeData.qrCode}

请保存此信息，到达时出示给门卫即可。`

    wx.setClipboardData({
      data: shareContent,
      success: () => {
        wx.showToast({
          title: '访客信息已复制',
          icon: 'success'
        })
      }
    })
  },

  // 格式化日期时间显示
  formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return ''
    
    const date = new Date(dateTimeStr)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    
    return `${month}-${day} ${hour}:${minute}`
  },

  // 返回访客列表
  goBackToVisitorList() {
    this.hideQRCode()
    wx.navigateBack()
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '访客邀请',
      path: '/pages/owner/visitor-invite'
    }
  }
})

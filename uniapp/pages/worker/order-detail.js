// pages/worker/order-detail.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    orderDetail: null,
    loading: true,
    workSteps: [
      { key: 'received', text: '已接单', desc: '工单已分配给您' },
      { key: 'processing', text: '处理中', desc: '正在现场处理问题' },
      { key: 'completed', text: '已完成', desc: '问题已解决完成' }
    ],
    workPhotos: [],
    workNotes: '',
    materialsUsed: []
  },

  onLoad(options) {
    console.log('维修工工单详情页面加载', options)
    
    if (options.id) {
      this.loadOrderDetail(options.id)
    }
  },

  // 加载工单详情
  async loadOrderDetail(orderId) {
    try {
      wx.showLoading({
        title: '加载中...'
      })

      const response = await api.getWorkOrderDetail(orderId)

      if (response.code === 200 && response.data) {
        const item = response.data
        
        // 解析图片数组
        let images = []
        try {
          images = item.images ? JSON.parse(item.images) : []
        } catch (e) {
          console.error('解析图片失败:', e)
        }

        const orderDetail = {
          id: item.id,
          title: item.title,
          description: item.content,
          category: item.category.toLowerCase(),
          categoryName: this.getCategoryName(item.category),
          status: item.status.toLowerCase(),
          statusText: this.getStatusText(item.status),
          urgentLevel: this.mapPriorityToUrgent(item.priority),
          urgentText: this.mapPriorityToUrgent(item.priority) === 'high' ? '紧急' : '普通',
          address: item.roomAddress || '未知地址',
          contactPhone: item.submitterPhone || '', // 使用正确的字段名
          ownerName: item.submitterName || '未知',
          ownerAvatar: '/static/images/default-avatar.png',
          images: images,
          createTime: new Date(item.submitTime || item.createTime),
          assignTime: item.assignTime ? new Date(item.assignTime) : null,
          startTime: item.startTime ? new Date(item.startTime) : null,
          completeTime: item.completeTime ? new Date(item.completeTime) : null,
          priority: item.priority,
          rating: item.rating,
          feedback: item.feedback,
          cost: item.cost,
          sourceData: item
        }

        this.setData({
          orderDetail: {
            ...orderDetail,
            createTimeText: this.formatDateTime(orderDetail.createTime),
            assignTimeText: orderDetail.assignTime ? this.formatDateTime(orderDetail.assignTime) : null,
            startTimeText: orderDetail.startTime ? this.formatDateTime(orderDetail.startTime) : null
          },
          loading: false
        })

        wx.setNavigationBarTitle({
          title: '工单详情'
        })
      } else {
        throw new Error(response.message || '获取工单详情失败')
      }
    } catch (error) {
      console.error('Load order detail error:', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } finally {
      wx.hideLoading()
    }
  },

  // 返回上一页
  goBack() {
    wx.navigateBack({
      delta: 1,
      fail: () => {
        // 如果返回失败，跳转到工单管理页面
        wx.switchTab({
          url: '/pages/worker/orders'
        })
      }
    })
  },

  // 联系业主
  contactOwner() {
    const { orderDetail } = this.data
    if (!orderDetail?.contactPhone) {
      wx.showToast({
        title: '暂无业主联系方式',
        icon: 'none'
      })
      return
    }

    wx.showActionSheet({
      itemList: [`拨打电话：${orderDetail.contactPhone}`, '发送消息'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: orderDetail.contactPhone
          })
        } else if (res.tapIndex === 1) {
          wx.showToast({
            title: '消息功能开发中',
            icon: 'none'
          })
        }
      }
    })
  },

  // 开始处理工单
  async startOrder() {
    wx.showModal({
      title: '开始处理',
      content: '确定要开始处理这个工单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({
              title: '处理中...'
            })
            
            const response = await api.startWorkOrder(this.data.orderDetail.id)
            
            if (response.code === 200) {
              wx.showToast({
                title: '已开始处理',
                icon: 'success'
              })
              
              // 重新加载工单详情
              this.loadOrderDetail(this.data.orderDetail.id)
            } else {
              throw new Error(response.message || '操作失败')
            }
          } catch (error) {
            console.error('Start order error:', error)
            wx.showToast({
              title: error.message || '开始处理失败',
              icon: 'none'
            })
          } finally {
            wx.hideLoading()
          }
        }
      }
    })
  },

  // 完成工单
  async completeOrder() {
    // 显示费用输入弹窗
    wx.showModal({
      title: '完成工单',
      content: '请输入维修费用（元），不填则为免费',
      editable: true,
      placeholderText: '请输入金额',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({
              title: '处理中...'
            })
            
            // 获取输入的费用
            const cost = res.content ? parseFloat(res.content) : 0
            
            // 验证费用格式
            if (res.content && (isNaN(cost) || cost < 0)) {
              wx.showToast({
                title: '费用格式不正确',
                icon: 'none'
              })
              return
            }
            
            // 调用完成工单API
            const response = await api.completeWorkOrder(this.data.orderDetail.id, cost > 0 ? cost : null)
            
            if (response.code === 200) {
              wx.showToast({
                title: '工单已完成',
                icon: 'success'
              })
              
              // 延迟返回上一页
              setTimeout(() => {
                wx.navigateBack()
              }, 1500)
            } else {
              throw new Error(response.message || '完成工单失败')
            }
          } catch (error) {
            console.error('Complete order error:', error)
            wx.showToast({
              title: error.message || '完成工单失败',
              icon: 'none'
            })
          } finally {
            wx.hideLoading()
          }
        }
      }
    })
  },

  // 申请协助
  requestHelp() {
    wx.showModal({
      title: '申请协助',
      content: '是否需要申请其他师傅协助处理？',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '协助申请已提交',
            icon: 'success'
          })
        }
      }
    })
  },

  // 上传工作照片
  uploadWorkPhoto() {
    const { workPhotos } = this.data
    const maxPhotos = 6
    const remainCount = maxPhotos - workPhotos.length
    
    if (remainCount <= 0) {
      wx.showToast({
        title: `最多上传${maxPhotos}张照片`,
        icon: 'none'
      })
      return
    }

    wx.chooseMedia({
      count: remainCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newPhotos = res.tempFiles.map(file => ({
          url: file.tempFilePath,
          size: file.size,
          uploadTime: new Date()
        }))
        
        this.setData({
          workPhotos: [...workPhotos, ...newPhotos]
        })
      }
    })
  },

  // 删除工作照片
  deleteWorkPhoto(e) {
    const index = e.currentTarget.dataset.index
    const workPhotos = this.data.workPhotos
    workPhotos.splice(index, 1)
    
    this.setData({
      workPhotos: workPhotos
    })
  },

  // 预览照片
  previewPhoto(e) {
    const index = e.currentTarget.dataset.index
    const type = e.currentTarget.dataset.type
    
    let urls = []
    let current = ''
    
    if (type === 'problem') {
      urls = this.data.orderDetail.images
      current = urls[index]
    } else if (type === 'work') {
      urls = this.data.workPhotos.map(photo => photo.url)
      current = urls[index]
    }
    
    wx.previewImage({
      current: current,
      urls: urls
    })
  },

  // 输入工作备注
  onWorkNotesInput(e) {
    this.setData({
      workNotes: e.detail.value
    })
  },

  // 添加使用材料
  addMaterial() {
    wx.showModal({
      title: '添加材料',
      content: '请输入使用的材料名称',
      editable: true,
      placeholderText: '如：水龙头阀芯',
      success: (res) => {
        if (res.confirm && res.content.trim()) {
          const materialsUsed = this.data.materialsUsed
          materialsUsed.push({
            name: res.content.trim(),
            quantity: 1,
            addTime: new Date()
          })
          
          this.setData({
            materialsUsed: materialsUsed
          })
        }
      }
    })
  },

  // 删除材料
  deleteMaterial(e) {
    const index = e.currentTarget.dataset.index
    const materialsUsed = this.data.materialsUsed
    materialsUsed.splice(index, 1)
    
    this.setData({
      materialsUsed: materialsUsed
    })
  },

  // 暂停工单
  pauseOrder() {
    wx.showActionSheet({
      itemList: ['暂停处理', '申请延期', '上报异常'],
      success: (res) => {
        const actions = ['暂停处理', '申请延期', '上报异常']
        wx.showToast({
          title: `${actions[res.tapIndex]}功能开发中`,
          icon: 'none'
        })
      }
    })
  },

  // 工具方法
  formatDateTime(date) {
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hour = String(d.getHours()).padStart(2, '0')
    const minute = String(d.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hour}:${minute}`
  },

  // 获取类别名称
  getCategoryName(category) {
    const categoryMap = {
      'REPAIR': '维修',
      'COMPLAINT': '投诉',
      'INQUIRY': '咨询',
      'SUGGESTION': '建议'
    }
    return categoryMap[category] || category
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待处理',
      'PROCESSING': '处理中',
      'COMPLETED': '已完成',
      'CANCELLED': '已取消',
      'CLOSED': '已关闭'
    }
    return statusMap[status] || status
  },

  // 优先级映射到紧急程度
  mapPriorityToUrgent(priority) {
    const priorityMap = {
      'LOW': 'low',
      'MEDIUM': 'normal',
      'HIGH': 'high',
      'URGENT': 'high'
    }
    return priorityMap[priority] || 'normal'
  }
})

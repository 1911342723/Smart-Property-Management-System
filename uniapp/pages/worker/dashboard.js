// pages/worker/dashboard.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: null,
    workStats: {
      todayOrders: 0,
      availableOrders: 0,  // 可接单数量
      pendingOrders: 0,
      processingOrders: 0,
      completedOrders: 0,
      monthlyScore: 0
    },
    quickActions: [
      {
        id: 'orders',
        name: '工单管理',
        icon: '/static/icons/repair.svg',
        color: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        path: '/pages/worker/orders'
      }
    ],
    recentOrders: [],
    notifications: [],
    loading: true
  },

  onLoad(options) {
    console.log('维修工dashboard页面加载')
    this.getUserInfo()
    this.loadDashboardData()
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(0)
    }
    
    // 刷新数据
    this.loadDashboardData()
  },

  // 获取用户信息
  getUserInfo() {
    const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({
        userInfo: {
          ...userInfo,
          name: userInfo.name || '维修师傅',
          workerId: userInfo.workerId || 'W001',
          department: userInfo.department || '维修部',
          avatar: userInfo.avatar || '/static/images/default-avatar.png'
        }
      })
    }
  },

  // 加载dashboard数据
  async loadDashboardData() {
    try {
      const userInfo = app.globalData.userInfo
      const userId = userInfo?.id
      
      if (!userId) {
        console.log('用户ID不存在，跳过加载数据')
        this.setData({ loading: false })
        return
      }

      // 获取工单统计数据
      const statsResponse = await api.getWorkOrderStats(userId)
      
      if (statsResponse.code === 200 && statsResponse.data) {
        const stats = statsResponse.data
        this.setData({
          workStats: {
            todayOrders: stats.today || 0,
            availableOrders: stats.available || 0, // 可接单数量
            pendingOrders: stats.pending || 0,
            processingOrders: stats.processing || 0,
            completedOrders: stats.completed || 0,
            monthlyScore: 4.8 // 评分需要另外计算
          }
        })
      }

      // 获取最近的工单列表
      const ordersResponse = await api.getWorkOrderList({
        pageNum: 1,
        pageSize: 5,
        assigneeId: userId,
        category: 'REPAIR'
      })

      if (ordersResponse.code === 200 && ordersResponse.data.list) {
        const recentOrders = ordersResponse.data.list.map(item => ({
          id: item.id,
          title: item.title,
          address: item.roomAddress || '未知地址',
          status: item.status ? item.status.toLowerCase() : 'pending', // 添加状态字段
          urgentLevel: this.mapPriorityToUrgent(item.priority),
          createTime: this.parseDate(item.submitTime || item.createTime),
          ownerName: item.submitterName || '未知',
          ownerPhone: item.submitterPhone || '' // 使用后端返回的提交人电话
        }))

        this.setData({
          recentOrders: recentOrders.slice(0, 3) // 只显示前3个
        })
      }

      // 通知消息（暂时使用模拟数据）
      this.setData({
        notifications: [
          {
            id: 1,
            title: '新工单提醒',
            content: `您有${statsResponse.data?.pending || 0}个待处理工单`,
            time: new Date(),
            timeText: this.formatTime(new Date()),
            type: 'order'
          }
        ],
        loading: false
      })

    } catch (error) {
      console.error('Load dashboard data error:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
    }
  },

  // 快捷操作点击
  onQuickActionTap(e) {
    const action = e.currentTarget.dataset.action
    console.log('快捷操作点击:', action)
    
    if (action.path) {
      if (action.path.includes('/pages/worker/orders')) {
        wx.switchTab({
          url: action.path,
          success: () => {
            console.log('跳转工单管理成功')
          },
          fail: (error) => {
            console.error('跳转工单管理失败:', error)
          }
        })
      } else {
        wx.navigateTo({
          url: action.path
        })
      }
    } else {
      wx.showToast({
        title: `${action.name}功能开发中`,
        icon: 'none'
      })
    }
  },

  // 查看工单详情
  viewOrderDetail(e) {
    const order = e.currentTarget.dataset.order
    console.log('查看工单详情:', order)
    
    if (!order || !order.id) {
      wx.showToast({
        title: '工单信息错误',
        icon: 'none'
      })
      return
    }
    
    wx.navigateTo({
      url: `/pages/worker/order-detail?id=${order.id}`,
      success: () => {
        console.log('跳转工单详情成功')
      },
      fail: (error) => {
        console.error('跳转工单详情失败:', error)
        wx.showToast({
          title: '跳转失败',
          icon: 'none'
        })
      }
    })
  },

  // 查看所有工单
  viewAllOrders() {
    wx.switchTab({
      url: '/pages/worker/orders'
    })
  },

  // 查看所有通知
  viewAllNotifications() {
    wx.navigateTo({
      url: '/pages/worker/notifications'
    })
  },

  // 联系业主
  contactOwner(e) {
    const order = e.currentTarget.dataset.order
    console.log('联系业主:', order)
    
    if (!order || !order.ownerPhone) {
      wx.showToast({
        title: '无法获取业主联系方式',
        icon: 'none'
      })
      return
    }
    
    wx.showActionSheet({
      itemList: [`拨打电话：${order.ownerPhone}`, '发送消息'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: order.ownerPhone
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
  async startOrder(e) {
    const order = e.currentTarget.dataset.order
    console.log('开始处理工单:', order)
    
    if (!order || !order.id) {
      wx.showToast({
        title: '工单信息不完整',
        icon: 'none'
      })
      return
    }
    
    wx.showModal({
      title: '开始处理',
      content: '确定要开始处理这个工单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({
              title: '处理中...'
            })
            
            // 调用开始处理API
            const response = await api.startWorkOrder(order.id)
            
            wx.hideLoading()
            
            if (response.code === 200) {
              wx.showToast({
                title: '已开始处理',
                icon: 'success'
              })
              
              // 刷新数据
              setTimeout(() => {
                this.loadDashboardData()
              }, 500)
            } else {
              wx.showToast({
                title: response.message || '开始处理失败',
                icon: 'none'
              })
            }
          } catch (error) {
            wx.hideLoading()
            console.error('Start order error:', error)
            wx.showToast({
              title: '网络错误，请重试',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadDashboardData()
    setTimeout(() => {
      wx.stopPullDownRefresh()
    }, 1000)
  },

  // 格式化时间
  formatTime(date) {
    const now = new Date()
    const target = new Date(date)
    const diff = now.getTime() - target.getTime()
    
    const minute = 60 * 1000
    const hour = 60 * minute
    
    if (diff < minute) {
      return '刚刚'
    } else if (diff < hour) {
      return `${Math.floor(diff / minute)}分钟前`
    } else {
      return `${Math.floor(diff / hour)}小时前`
    }
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
  },
  
  // 解析日期字符串（兼容iOS）
  parseDate(dateStr) {
    if (!dateStr) return null
    // 将 "yyyy-MM-dd HH:mm:ss" 转换为 "yyyy/MM/dd HH:mm:ss" 格式（iOS兼容）
    const formattedStr = dateStr.replace(/-/g, '/')
    return new Date(formattedStr)
  }
})
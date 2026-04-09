// pages/worker/orders.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    activeTab: 'pending',
    tabs: [
      { key: 'pending', name: '待处理', count: 0 },
      { key: 'processing', name: '处理中', count: 0 },
      { key: 'completed', name: '已完成', count: 0 },
      { key: 'closed', name: '已关闭', count: 0 }
    ],
    orders: [],
    filteredOrders: [],
    hasOrders: false,
    loading: false, // 修改为 false，避免首次加载被跳过
    refreshing: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 20
  },

  onLoad(options) {
    console.log('工单管理页面加载')
    this.loadOrders()
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
  },

  // 加载工单数据
  async loadOrders() {
    if (this.data.loading && !this.data.refreshing) {
      console.log('跳过加载：正在加载中')
      return
    }

    try {
      console.log('=== 开始加载工单 ===')
      console.log('当前Tab:', this.data.activeTab)
      
      if (!this.data.refreshing) {
        this.setData({ loading: true })
      }

      const userInfo = app.globalData.userInfo
      const userId = userInfo?.id
      
      console.log('用户ID:', userId)
      
      if (!userId) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        })
        this.setData({ loading: false, refreshing: false })
        return
      }

      // 调用API获取工单列表
      // 根据当前tab获取不同的工单：
      // - 待处理(pending): 显示未分配的工单（可以接单）
      // - 处理中/已完成/已关闭: 显示分配给自己的工单
      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        category: 'REPAIR'
      }
      
      if (this.data.activeTab === 'pending') {
        // 待处理：显示未分配的PENDING工单
        params.status = 'PENDING'
        // 不设置 assigneeId，这样可以显示所有未分配的工单
      } else {
        // 其他状态：只显示分配给自己的工单
        params.assigneeId = userId
        if (this.data.activeTab !== 'all') {
          params.status = this.data.activeTab.toUpperCase()
        }
      }
      
      console.log('请求参数:', params)
      const response = await api.getWorkOrderList(params)
      console.log('API响应:', response)

      if (response.code === 200) {
        const list = response.data?.list || []
        console.log('获取到工单数量:', list.length)
        
        const orders = list.map(item => ({
          id: item.id,
          title: item.title,
          description: item.content,
          address: item.roomAddress || '未知地址',
          status: item.status.toLowerCase(),
          urgentLevel: this.mapPriorityToUrgent(item.priority),
          createTime: this.parseDate(item.submitTime || item.createTime),
          startTime: item.startTime ? this.parseDate(item.startTime) : null,
          completedTime: item.completeTime ? this.parseDate(item.completeTime) : null,
          ownerName: item.submitterName || '未知',
          ownerPhone: item.submitterPhone || '', // 使用正确的字段名
          priority: item.priority,
          rating: item.rating,
          feedback: item.feedback,
          cost: item.cost,
          sourceData: item // 保存原始数据
        }))

        // 分页处理
        if (this.data.pageNum === 1) {
          await this.processOrders(orders)
        } else {
          const allOrders = [...this.data.orders, ...orders]
          await this.processOrders(allOrders)
        }

        // 更新分页状态
        this.setData({
          hasMore: orders.length >= this.data.pageSize,
          pageNum: this.data.pageNum + 1,
          loading: false,
          refreshing: false
        })
      } else {
        wx.showToast({
          title: response.message || '加载失败',
          icon: 'none'
        })
        this.setData({ 
          loading: false,
          refreshing: false
        })
      }
      
    } catch (error) {
      console.error('Load orders error:', error)
      wx.showToast({
        title: '网络错误，请重试',
        icon: 'none'
      })
      this.setData({ 
        loading: false,
        refreshing: false
      })
    }
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ 
      refreshing: true,
      pageNum: 1,
      hasMore: true
    })
    this.loadOrders()
  },

  // 加载更多
  loadMore() {
    console.log('loadMore 触发', { hasMore: this.data.hasMore, loading: this.data.loading })
    if (this.data.hasMore && !this.data.loading && !this.data.refreshing) {
      this.loadOrders()
    }
  },

  // 处理工单数据
  async processOrders(orders) {
    const statusMap = {
      'pending': { text: '待处理', color: '#ffc107' },
      'processing': { text: '处理中', color: '#2d3748' },
      'completed': { text: '已完成', color: '#38a169' },
      'closed': { text: '已关闭', color: '#6c757d' }
    }

    const processedOrders = orders.map(order => ({
      ...order,
      statusText: statusMap[order.status]?.text || '未知',
      statusColor: statusMap[order.status]?.color || '#6c757d',
      urgentText: order.urgentLevel === 'high' ? '紧急' : '普通',
      timeText: this.formatRelativeTime(order.createTime)
    }))

    this.setData({
      orders: processedOrders
    })

    // 初始化过滤后的工单列表
    this.updateFilteredOrders()
    
    // 更新tab统计数据（异步执行，不阻塞）
    this.updateTabCounts().catch(err => {
      console.error('Update tab counts failed:', err)
    })
  },
  
  // 更新tab统计数据
  async updateTabCounts() {
    try {
      const userInfo = app.globalData.userInfo
      const userId = userInfo?.id
      
      if (!userId) return
      
      // 获取统计数据
      const statsResponse = await api.getWorkOrderStats(userId)
      
      if (statsResponse.code === 200 && statsResponse.data) {
        const stats = statsResponse.data
        // pending tab显示可接单数量
        // processing/completed tab显示分配给自己的数量
        this.setData({
          'tabs[0].count': stats.available || 0,  // 可接单（未分配的）
          'tabs[1].count': stats.processing || 0, // 处理中（分配给自己的）
          'tabs[2].count': stats.completed || 0,  // 已完成（分配给自己的）
          'tabs[3].count': 0  // 已关闭（暂无）
        })
      }
    } catch (error) {
      console.error('Update tab counts error:', error)
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
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab,
      pageNum: 1,
      hasMore: true,
      orders: [],
      filteredOrders: []
    })
    // 切换tab时重新从后端加载数据
    this.loadOrders()
  },

  // 更新过滤后的工单列表
  updateFilteredOrders() {
    const { orders, activeTab } = this.data
    const filteredOrders = orders.filter(order => order.status === activeTab)
    this.setData({
      filteredOrders: filteredOrders,
      hasOrders: filteredOrders.length > 0
    })
  },

  // 获取当前标签的工单
  getFilteredOrders() {
    const { orders, activeTab } = this.data
    return orders.filter(order => order.status === activeTab)
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

  // 联系业主
  contactOwner(e) {
    const order = e.currentTarget.dataset.order
    
    wx.showActionSheet({
      itemList: [`拨打电话：${order.ownerPhone}`, '发送消息'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: order.ownerPhone
          })
        } else if (res.tapIndex === 1) {
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        }
      }
    })
  },

  // 接单
  async acceptOrder(e) {
    const order = e.currentTarget.dataset.order
    
    wx.showModal({
      title: '确认接单',
      content: `确定要接受工单"${order.title}"吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({
              title: '处理中...'
            })
            
            // 获取当前用户ID
            const userInfo = app.globalData.userInfo
            const userId = userInfo?.id
            
            if (!userId) {
              wx.hideLoading()
              wx.showToast({
                title: '请先登录',
                icon: 'none'
              })
              return
            }
            
            // 调用接单API（将工单分配给自己）
            const response = await api.acceptWorkOrder(order.id, userId)
            
            if (response.code === 200) {
              wx.showToast({
                title: '接单成功',
                icon: 'success'
              })
              
              // 重新加载数据
              this.loadOrders()
            } else {
              throw new Error(response.message || '接单失败')
            }
          } catch (error) {
            console.error('Accept order error:', error)
            wx.showToast({
              title: error.message || '接单失败',
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
  completeOrder(e) {
    const order = e.currentTarget.dataset.order
    
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
            const response = await api.completeWorkOrder(order.id, cost > 0 ? cost : null)
            
            if (response.code === 200) {
              wx.showToast({
                title: '工单已完成',
                icon: 'success'
              })
              
              // 重新加载数据
              this.loadOrders()
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

  // 格式化相对时间
  formatRelativeTime(date) {
    const now = new Date()
    const target = new Date(date)
    const diff = now.getTime() - target.getTime()
    
    const minute = 60 * 1000
    const hour = 60 * minute
    const day = 24 * hour
    
    if (diff < minute) {
      return '刚刚'
    } else if (diff < hour) {
      return `${Math.floor(diff / minute)}分钟前`
    } else if (diff < day) {
      return `${Math.floor(diff / hour)}小时前`
    } else {
      return `${Math.floor(diff / day)}天前`
    }
  }
})
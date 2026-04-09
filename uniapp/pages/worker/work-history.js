// pages/worker/work-history.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    orders: [],
    filteredOrders: [],
    loading: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 20,
    
    // 筛选条件
    filterStatus: 'all',
    filterMonth: 'all',
    sortBy: 'time', // time, rating
    
    // 统计数据
    stats: {
      total: 0,
      completed: 0,
      avgRating: 0,
      totalIncome: 0
    }
  },

  onLoad(options) {
    console.log('工作历史页面加载')
    this.loadOrders()
    this.loadStats()
  },

  // 加载工单列表
  async loadOrders() {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      const userId = userInfo?.id

      if (!userId) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }

      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        assigneeId: userId,
        category: 'REPAIR'
      }

      // 添加状态筛选
      if (this.data.filterStatus !== 'all') {
        params.status = this.data.filterStatus.toUpperCase()
      }

      const response = await api.getWorkOrderList(params)

      if (response.code === 200) {
        const list = response.data?.list || []
        
        let orders = list.map(item => ({
          id: item.id,
          title: item.title,
          description: item.content,
          address: item.roomAddress || '未知地址',
          status: item.status.toLowerCase(),
          statusText: this.getStatusText(item.status),
          statusColor: this.getStatusColor(item.status),
          priority: item.priority,
          createTime: this.formatDate(item.submitTime || item.createTime),
          completeTime: item.completeTime ? this.formatDate(item.completeTime) : null,
          rating: item.rating || 0,
          feedback: item.feedback || '',
          cost: item.cost || 0,
          ownerName: item.submitterName || '未知'
        }))

        // 应用筛选和排序
        orders = this.applyFiltersAndSort(orders)

        const allOrders = this.data.pageNum === 1 
          ? orders 
          : [...this.data.orders, ...orders]

        this.setData({
          orders: allOrders,
          filteredOrders: allOrders,
          hasMore: orders.length >= this.data.pageSize,
          loading: false
        })
      } else {
        wx.showToast({
          title: response.message || '加载失败',
          icon: 'none'
        })
        this.setData({ loading: false })
      }
    } catch (error) {
      console.error('加载工单失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
    }
  },

  // 加载统计数据
  async loadStats() {
    try {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      const userId = userInfo?.id

      if (!userId) return

      const response = await api.getWorkOrderStats(userId)
      
      if (response.code === 200 && response.data) {
        const stats = response.data
        this.setData({
          stats: {
            total: (stats.processing || 0) + (stats.completed || 0),
            completed: stats.completed || 0,
            avgRating: stats.averageRating || 0,
            totalIncome: stats.totalIncome || 0
          }
        })
      }
    } catch (error) {
      console.error('加载统计失败:', error)
    }
  },

  // 应用筛选和排序
  applyFiltersAndSort(orders) {
    let filtered = [...orders]

    // 按月份筛选
    if (this.data.filterMonth !== 'all') {
      const targetMonth = parseInt(this.data.filterMonth)
      filtered = filtered.filter(order => {
        const orderMonth = new Date(order.createTime).getMonth() + 1
        return orderMonth === targetMonth
      })
    }

    // 排序
    if (this.data.sortBy === 'rating') {
      filtered.sort((a, b) => b.rating - a.rating)
    } else {
      filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    }

    return filtered
  },

  // 显示筛选菜单
  showFilterMenu() {
    const statusOptions = ['全部', '已完成', '已关闭']
    
    wx.showActionSheet({
      itemList: statusOptions,
      success: (res) => {
        const statusMap = ['all', 'completed', 'closed']
        this.setData({
          filterStatus: statusMap[res.tapIndex],
          pageNum: 1,
          orders: []
        })
        this.loadOrders()
      }
    })
  },

  // 显示排序菜单
  showSortMenu() {
    wx.showActionSheet({
      itemList: ['按时间排序', '按评分排序'],
      success: (res) => {
        const sortMap = ['time', 'rating']
        this.setData({
          sortBy: sortMap[res.tapIndex]
        })
        
        const filtered = this.applyFiltersAndSort(this.data.orders)
        this.setData({
          filteredOrders: filtered
        })
      }
    })
  },

  // 查看工单详情
  viewOrderDetail(e) {
    const order = e.currentTarget.dataset.order
    wx.navigateTo({
      url: `/pages/worker/order-detail?id=${order.id}`
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({
      pageNum: 1,
      orders: []
    })
    Promise.all([
      this.loadOrders(),
      this.loadStats()
    ]).finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        pageNum: this.data.pageNum + 1
      })
      this.loadOrders()
    }
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待处理',
      'PROCESSING': '处理中',
      'COMPLETED': '已完成',
      'CLOSED': '已关闭'
    }
    return statusMap[status] || '未知'
  },

  // 获取状态颜色
  getStatusColor(status) {
    const colorMap = {
      'PENDING': '#ffc107',
      'PROCESSING': '#2d3748',
      'COMPLETED': '#38a169',
      'CLOSED': '#6c757d'
    }
    return colorMap[status] || '#6c757d'
  },

  // 格式化日期
  formatDate(dateStr) {
    if (!dateStr) return ''
    
    const date = new Date(dateStr.replace(/-/g, '/'))
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    
    return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
  }
})

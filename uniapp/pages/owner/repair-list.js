// pages/owner/repair-list.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    activeFilter: 'all',
    filterTabs: [
      { value: 'all', label: '全部', count: 0 },
      { value: 'pending', label: '待处理', count: 0 },
      { value: 'processing', label: '处理中', count: 0 },
      { value: 'completed', label: '已完成', count: 0 }
    ],
    repairList: [],
    loading: false,
    refreshing: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 20
  },

  onLoad() {
    console.log('报修列表页面加载')
    this.loadRepairList()
  },

  onShow() {
    // 刷新数据
    this.refreshData()
  },

  // 刷新数据
  async refreshData() {
    this.setData({
      pageNum: 1,
      hasMore: true,
      repairList: []
    })
    await this.loadRepairList()
  },

  // 加载报修列表
  async loadRepairList() {
    if (this.data.loading) return

    this.setData({ loading: true })

    try {
      const userInfo = app.globalData.userInfo
      const userId = userInfo?.id

      if (!userId) {
        console.log('用户ID不存在')
        this.setData({ loading: false })
        return
      }

      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize,
        submitterId: userId,
        category: 'REPAIR'
      }

      // 根据筛选条件添加状态参数
      if (this.data.activeFilter !== 'all') {
        params.status = this.data.activeFilter.toUpperCase()
      }

      const response = await api.getWorkOrderList(params)

      if (response.code === 200 && response.data.list) {
        const repairs = response.data.list.map(item => ({
          id: item.id,
          title: item.title,
          description: item.content,
          category: this.mapCategoryFromBackend(item.category),
          status: item.status.toLowerCase(),
          createTime: new Date(item.submitTime || item.createTime),
          workerName: item.assigneeName || '待分配',
          completedTime: item.completeTime ? new Date(item.completeTime) : null,
          priority: item.priority,
          rating: item.rating,
          feedback: item.feedback,
          sourceData: item // 保存原始数据
        }))

        // 处理显示数据
        const processedRepairs = repairs.map(item => ({
          ...item,
          statusColor: this.getStatusColor(item.status),
          statusText: this.getStatusText(item.status),
          timeText: this.formatRelativeTime(item.createTime)
        }))

        // 分页处理
        if (this.data.pageNum === 1) {
          this.setData({
            repairList: processedRepairs
          })
        } else {
          this.setData({
            repairList: [...this.data.repairList, ...processedRepairs]
          })
        }

        // 更新分页状态
        this.setData({
          hasMore: repairs.length >= this.data.pageSize,
          pageNum: this.data.pageNum + 1
        })

        // 更新筛选标签统计
        this.updateFilterCounts()

      } else {
        wx.showToast({
          title: response.message || '加载失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('加载报修列表失败:', error)
      wx.showToast({
        title: '网络错误，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ 
        loading: false,
        refreshing: false
      })
    }
  },

  // 更新筛选标签统计
  async updateFilterCounts() {
    try {
      const userInfo = app.globalData.userInfo
      const userId = userInfo?.id

      if (!userId) return

      // 获取各状态的统计数据
      const promises = [
        api.getWorkOrderList({ pageNum: 1, pageSize: 1, submitterId: userId, category: 'REPAIR' }),
        api.getWorkOrderList({ pageNum: 1, pageSize: 1, submitterId: userId, category: 'REPAIR', status: 'PENDING' }),
        api.getWorkOrderList({ pageNum: 1, pageSize: 1, submitterId: userId, category: 'REPAIR', status: 'PROCESSING' }),
        api.getWorkOrderList({ pageNum: 1, pageSize: 1, submitterId: userId, category: 'REPAIR', status: 'COMPLETED' })
      ]

      const results = await Promise.all(promises)
      
      const tabs = this.data.filterTabs.map((tab, index) => {
        if (results[index].code === 200) {
          tab.count = results[index].data.total || 0
        }
        return tab
      })

      this.setData({ filterTabs: tabs })
    } catch (error) {
      console.error('更新统计失败:', error)
    }
  },

  // 切换筛选条件
  switchFilter(e) {
    const filter = e.currentTarget.dataset.filter
    this.setData({
      activeFilter: filter,
      pageNum: 1,
      hasMore: true,
      repairList: []
    })
    this.loadRepairList()
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ refreshing: true })
    this.refreshData()
  },

  // 加载更多
  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadRepairList()
    }
  },

  // 查看报修详情
  viewRepairDetail(e) {
    const repair = e.currentTarget.dataset.repair
    wx.navigateTo({
      url: `/pages/owner/repair-detail?id=${repair.id}`
    })
  },

  // 评价报修
  rateRepair(e) {
    const repair = e.currentTarget.dataset.repair
    
    wx.navigateTo({
      url: `/pages/owner/repair-detail?id=${repair.id}&action=rate`
    })
  },

  // 工具方法
  getStatusColor(status) {
    const colorMap = {
      pending: '#f59e0b',     // 橙色 - 待处理
      processing: '#3b82f6',  // 蓝色 - 处理中
      completed: '#10b981',   // 绿色 - 已完成
      cancelled: '#6b7280',   // 灰色 - 已取消
      closed: '#374151'       // 深灰色 - 已关闭
    }
    return colorMap[status] || '#6b7280'
  },

  getStatusText(status) {
    const textMap = {
      pending: '待处理',
      processing: '处理中',
      completed: '已完成',
      cancelled: '已取消',
      closed: '已关闭'
    }
    return textMap[status] || '未知'
  },

  // 从后端类别映射到前端类别
  mapCategoryFromBackend(backendCategory) {
    const categoryMap = {
      'REPAIR': 'repair',
      'COMPLAINT': 'complaint',
      'SUGGESTION': 'suggestion'
    }
    return categoryMap[backendCategory] || 'other'
  },

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

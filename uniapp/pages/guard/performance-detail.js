// 业绩统计详情页面逻辑
Page({
  data: {
    // 当前时间范围
    activeTimeRange: 'month',
    
    // 核心指标数据
    metrics: {
      patrolRate: 95,
      patrolTrend: 'up',
      patrolChange: 5,
      responseTime: '3.5分钟',
      responseTrend: 'down',
      responseChange: 12,
      reportCount: 12,
      reportTrend: 'up',
      reportChange: 8,
      score: 4.8,
      scoreTrend: 'up',
      scoreChange: 0.2
    },
    
    // 排名信息
    currentRank: 3,
    totalGuards: 15,
    rankChange: 2,
    
    // 工作明细
    workDetails: {
      workDays: 22,
      overtimeHours: 16,
      leaveDays: 1,
      totalPatrols: 156,
      normalPatrols: 142,
      abnormalPatrols: 14,
      reportEvents: 12,
      resolvedEvents: 11,
      resolveRate: 92
    },
    
    // 历史数据（用于图表）
    trendData: []
  },

  onLoad(options) {
    this.loadMetricsData()
    this.loadTrendData()
  },

  onShow() {
    this.loadMetricsData()
  },

  onPullDownRefresh() {
    this.loadMetricsData()
    this.loadTrendData()
    wx.stopPullDownRefresh()
  },

  // 切换时间范围
  switchTimeRange(e) {
    const range = e.currentTarget.dataset.range
    this.setData({
      activeTimeRange: range
    })
    this.loadMetricsData()
  },

  // 加载指标数据
  loadMetricsData() {
    const { activeTimeRange } = this.data
    
    // 模拟不同时间范围的数据
    let mockMetrics = {}
    
    switch (activeTimeRange) {
      case 'week':
        mockMetrics = {
          patrolRate: 92,
          patrolTrend: 'down',
          patrolChange: 3,
          responseTime: '4.2分钟',
          responseTrend: 'up',
          responseChange: 8,
          reportCount: 3,
          reportTrend: 'down',
          reportChange: 25,
          score: 4.6,
          scoreTrend: 'up',
          scoreChange: 0.1
        }
        break
      case 'quarter':
        mockMetrics = {
          patrolRate: 96,
          patrolTrend: 'up',
          patrolChange: 7,
          responseTime: '3.2分钟',
          responseTrend: 'down',
          responseChange: 15,
          reportCount: 35,
          reportTrend: 'up',
          reportChange: 12,
          score: 4.9,
          scoreTrend: 'up',
          scoreChange: 0.3
        }
        break
      default: // month
        mockMetrics = {
          patrolRate: 95,
          patrolTrend: 'up',
          patrolChange: 5,
          responseTime: '3.5分钟',
          responseTrend: 'down',
          responseChange: 12,
          reportCount: 12,
          reportTrend: 'up',
          reportChange: 8,
          score: 4.8,
          scoreTrend: 'up',
          scoreChange: 0.2
        }
    }
    
    // 模拟排名变化
    const rankData = {
      week: { rank: 4, change: -1 },
      month: { rank: 3, change: 2 },
      quarter: { rank: 2, change: 3 }
    }
    
    const currentRankData = rankData[activeTimeRange]
    
    this.setData({
      metrics: mockMetrics,
      currentRank: currentRankData.rank,
      rankChange: currentRankData.change
    })
    
    // 同时更新工作明细
    this.updateWorkDetails()
  },

  // 更新工作明细
  updateWorkDetails() {
    const { activeTimeRange } = this.data
    let workDetails = {}
    
    switch (activeTimeRange) {
      case 'week':
        workDetails = {
          workDays: 5,
          overtimeHours: 4,
          leaveDays: 0,
          totalPatrols: 35,
          normalPatrols: 32,
          abnormalPatrols: 3,
          reportEvents: 3,
          resolvedEvents: 3,
          resolveRate: 100
        }
        break
      case 'quarter':
        workDetails = {
          workDays: 66,
          overtimeHours: 48,
          leaveDays: 3,
          totalPatrols: 468,
          normalPatrols: 426,
          abnormalPatrols: 42,
          reportEvents: 35,
          resolvedEvents: 32,
          resolveRate: 91
        }
        break
      default: // month
        workDetails = {
          workDays: 22,
          overtimeHours: 16,
          leaveDays: 1,
          totalPatrols: 156,
          normalPatrols: 142,
          abnormalPatrols: 14,
          reportEvents: 12,
          resolvedEvents: 11,
          resolveRate: 92
        }
    }
    
    this.setData({ workDetails })
  },

  // 加载趋势数据
  loadTrendData() {
    // 生成模拟的30天趋势数据
    const trendData = []
    const baseScore = 4.5
    
    for (let i = 0; i < 30; i++) {
      const date = new Date()
      date.setDate(date.getDate() - (29 - i))
      
      const score = baseScore + (Math.random() - 0.5) * 0.8
      trendData.push({
        date: this.formatDate(date),
        score: Number(score.toFixed(1)),
        patrolCount: Math.floor(Math.random() * 8) + 3,
        reportCount: Math.floor(Math.random() * 3)
      })
    }
    
    this.setData({ trendData })
  },

  // 格式化日期
  formatDate(date) {
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${month}-${day}`
  },

  // 查看详细排名
  viewRankingDetail() {
    wx.navigateTo({
      url: '/pages/guard/ranking-detail'
    })
  },

  // 查看趋势详情
  viewTrendDetail() {
    wx.showToast({
      title: '图表功能开发中',
      icon: 'none'
    })
  },

  // 导出数据
  exportData() {
    wx.showActionSheet({
      itemList: ['导出PDF报告', '导出Excel数据', '分享给管理员'],
      success: (res) => {
        switch (res.tapIndex) {
          case 0:
            this.exportPDF()
            break
          case 1:
            this.exportExcel()
            break
          case 2:
            this.shareToManager()
            break
        }
      }
    })
  },

  // 导出PDF
  exportPDF() {
    wx.showLoading({ title: '生成中...' })
    
    setTimeout(() => {
      wx.hideLoading()
      wx.showToast({
        title: 'PDF报告已生成',
        icon: 'success'
      })
    }, 2000)
  },

  // 导出Excel
  exportExcel() {
    wx.showLoading({ title: '导出中...' })
    
    setTimeout(() => {
      wx.hideLoading()
      wx.showToast({
        title: 'Excel数据已导出',
        icon: 'success'
      })
    }, 2000)
  },

  // 分享给管理员
  shareToManager() {
    wx.showToast({
      title: '已发送给管理员',
      icon: 'success'
    })
  },

  // 查看历史对比
  viewHistoryComparison() {
    wx.navigateTo({
      url: '/pages/guard/performance-history'
    })
  },

  // 设置目标
  setTarget() {
    wx.showModal({
      title: '设置月度目标',
      editable: true,
      placeholderText: '请输入巡检完成率目标(如:98)',
      success: (res) => {
        if (res.confirm && res.content) {
          wx.showToast({
            title: '目标设置成功',
            icon: 'success'
          })
        }
      }
    })
  }
})











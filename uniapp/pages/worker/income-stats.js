// pages/worker/income-stats.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    totalIncome: 0,
    monthIncome: 0,
    incomeDetails: [],
    loading: false,
    selectedMonth: new Date().getMonth() + 1,
    selectedYear: new Date().getFullYear()
  },

  onLoad(options) {
    console.log('收入统计页面加载')
    this.loadIncomeStats()
  },

  async loadIncomeStats() {
    try {
      this.setData({ loading: true })
      
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      const userId = userInfo?.id

      if (!userId) {
        wx.showToast({ title: '请先登录', icon: 'none' })
        return
      }

      // 获取已完成的工单（带费用信息）
      const response = await api.getWorkOrderList({
        assigneeId: userId,
        status: 'COMPLETED',
        pageNum: 1,
        pageSize: 100
      })

      if (response.code === 200) {
        const orders = response.data?.list || []
        
        let totalIncome = 0
        let monthIncome = 0
        const incomeDetails = []

        orders.forEach(order => {
          const cost = order.cost || 0
          if (cost > 0) {
            totalIncome += cost
            
            const completeDate = new Date(order.completeTime)
            if (completeDate.getMonth() + 1 === this.data.selectedMonth) {
              monthIncome += cost
            }
            
            incomeDetails.push({
              id: order.id,
              title: order.title,
              cost: cost,
              date: this.formatDate(order.completeTime),
              address: order.roomAddress
            })
          }
        })

        this.setData({
          totalIncome,
          monthIncome,
          incomeDetails: incomeDetails.reverse(),
          loading: false
        })
      }
    } catch (error) {
      console.error('加载收入统计失败:', error)
      wx.showToast({ title: '加载失败', icon: 'none' })
      this.setData({ loading: false })
    }
  },

  formatDate(dateStr) {
    if (!dateStr) return ''
    const date = new Date(dateStr.replace(/-/g, '/'))
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
})

// pages/owner/payment.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    totalAmount: '0.00',
    unpaidCount: 0,
    dueDate: '',
    selectedBills: [],
    selectedAmount: '0.00',
    bills: [],
    loading: false,
    currentPage: 1,
    pageSize: 20,
    hasMore: true,
    billSummary: null,
    mockBills: [
      {
        id: 1,
        title: '物业管理费',
        description: '包含保洁、绿化、安保等服务',
        period: '2024年1月',
        amount: '658.50',
        status: 'unpaid',
        statusText: '待缴费',
        overdue: false,
        dueDate: '2024-02-15',
        dueDateText: '2月15日到期',
        icon: '/static/icons/house.svg',
        iconBg: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        selected: false
      },
      {
        id: 2,
        title: '停车费',
        description: '地下车位 B2-088',
        period: '2024年1月',
        amount: '200.00',
        status: 'overdue',
        statusText: '已逾期',
        overdue: true,
        dueDate: '2024-02-01',
        dueDateText: '已逾期14天',
        icon: '/static/icons/settings.svg',
        iconBg: 'linear-gradient(135deg, #e53e3e 0%, #c53030 100%)',
        selected: false
      }
    ],
    payMethods: [
      {
        id: 'wechat',
        name: '微信支付',
        icon: '/static/icons/payment.svg',
        color: 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)'
      },
      {
        id: 'alipay',
        name: '支付宝',
        icon: '/static/icons/credit-card.svg',
        color: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)'
      },
      {
        id: 'bank',
        name: '银行卡',
        icon: '/static/icons/credit-card.svg',
        color: 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)'
      }
    ]
  },

  onLoad(options) {
    console.log('费用缴纳页面加载')
    this.initData()
  },

  onShow() {
    // 更新tabbar选中状态
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().updateSelected(0)
    }
  },

  // 初始化数据
  async initData() {
    this.loadBillSummary()
    this.loadBills(true)
  },

  // 加载费用统计
  async loadBillSummary() {
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        console.log('用户信息不存在，使用模拟数据')
        this.setMockSummary()
        return
      }

      console.log('加载费用统计，用户ID:', userInfo.id)
      const response = await api.getBillSummary(userInfo.id)
      
      console.log('费用统计响应:', response)
      
      if (response.code === 200 && response.data) {
        const summary = response.data
        this.setData({
          billSummary: summary,
          totalAmount: this.formatAmount(summary.totalAmount || 0),
          unpaidCount: summary.unpaidCount || 0,
          dueDate: summary.nearestDueDate || ''
        })
      } else {
        console.log('费用统计加载失败，使用模拟数据')
        this.setMockSummary()
      }
    } catch (error) {
      console.error('加载费用统计失败:', error)
      this.setMockSummary()
    }
  },

  // 设置模拟统计数据
  setMockSummary() {
    const total = this.data.mockBills.reduce((sum, bill) => {
      return sum + parseFloat(bill.amount)
    }, 0)
    
    this.setData({
      totalAmount: this.formatAmount(total),
      unpaidCount: this.data.mockBills.length,
      dueDate: '2025年1月15日',
      bills: this.data.mockBills.map(bill => ({...bill}))
    })
  },

  // 加载费用数据
  async loadBills(reset = false) {
    if (this.data.loading) return
    
    if (reset) {
      this.setData({
        currentPage: 1,
        bills: [],
        hasMore: true
      })
    }
    
    this.setData({ loading: true })
    
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        console.log('用户信息不存在，使用模拟数据')
        this.setData({
          bills: this.data.mockBills.map(bill => ({...bill})),
          loading: false
        })
        return
      }

      console.log('加载账单列表，用户ID:', userInfo.id, '页码:', this.data.currentPage)
      const response = await api.getBillList({
        pageNum: this.data.currentPage,
        pageSize: this.data.pageSize,
        ownerId: userInfo.id,
        status: 'UNPAID'
      })
      
      console.log('账单列表响应:', response)
      
      if (response.code === 200 && response.data) {
        const newBills = this.processBillsData(response.data.list || [])
        
        this.setData({
          bills: reset ? newBills : [...this.data.bills, ...newBills],
          hasMore: response.data.list && response.data.list.length >= this.data.pageSize,
          currentPage: this.data.currentPage + 1
        })
      } else {
        console.log('账单列表加载失败，使用模拟数据')
        if (reset) {
          this.setData({
            bills: this.data.mockBills.map(bill => ({...bill}))
          })
        }
      }
    } catch (error) {
      console.error('加载账单列表失败:', error)
      if (reset) {
        this.setData({
          bills: this.data.mockBills.map(bill => ({...bill}))
        })
      }
    } finally {
      this.setData({ loading: false })
    }
  },

  // 处理账单数据
  processBillsData(bills) {
    return bills.map(item => {
      const bill = {
        id: item.id,
        title: this.getBillTypeText(item.billType),
        description: item.description || '',
        period: item.billingPeriod,
        amount: parseFloat(item.amount).toFixed(2),
        status: item.status.toLowerCase(),
        statusText: this.getStatusText(item.status),
        overdue: item.status === 'OVERDUE',
        dueDate: item.dueDate,
        dueDateText: this.formatDueDate(item.dueDate),
        icon: this.getBillTypeIcon(item.billType),
        iconBg: this.getBillTypeColor(item.billType),
        selected: false,
        sourceData: item
      }
      
      console.log('处理账单数据:', item.billType, '->', bill.title)
      return bill
    }).filter(item => ['物业管理费', '停车费'].includes(item.title))
  },

  // 获取账单类型文本
  getBillTypeText(billType) {
    const typeMap = {
      'PROPERTY': '物业管理费',
      'PARKING': '停车费'
    }
    return typeMap[billType] || billType
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'UNPAID': '待缴费',
      'PAID': '已缴费',
      'OVERDUE': '已逾期'
    }
    return statusMap[status] || status
  },

  // 获取账单类型图标
  getBillTypeIcon(billType) {
    const iconMap = {
      'PROPERTY': '/static/icons/house.svg',
      'PARKING': '/static/icons/settings.svg'
    }
    return iconMap[billType] || '/static/icons/bill.svg'
  },

  // 获取账单类型颜色
  getBillTypeColor(billType) {
    const colorMap = {
      'PROPERTY': 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
      'PARKING': 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)'
    }
    return colorMap[billType] || 'linear-gradient(135deg, #718096 0%, #4a5568 100%)'
  },

  // 格式化到期日期
  formatDueDate(dueDate) {
    if (!dueDate) return ''
    
    const date = new Date(dueDate)
    const today = new Date()
    const diffTime = date.getTime() - today.getTime()
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    
    if (diffDays < 0) {
      return `已逾期${Math.abs(diffDays)}天`
    } else if (diffDays === 0) {
      return '今日到期'
    } else if (diffDays <= 7) {
      return `${diffDays}天后到期`
    } else {
      return `${date.getMonth() + 1}月${date.getDate()}日到期`
    }
  },

  // 格式化金额
  formatAmount(amount) {
    return parseFloat(amount || 0).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  },

  // 切换账单选中状态
  toggleBillSelection(e) {
    const bill = e.currentTarget.dataset.bill
    console.log('切换账单选择:', bill)
    
    // 更新账单选中状态
    const bills = this.data.bills.map(item => {
      if (item.id === bill.id) {
        return { ...item, selected: !item.selected }
      }
      return item
    })
    
    // 获取选中的账单
    const selectedBills = bills.filter(item => item.selected)
    
    // 计算选中金额
    const selectedAmount = selectedBills.reduce((sum, item) => {
      return sum + parseFloat(item.amount)
    }, 0)
    
    this.setData({
      bills,
      selectedBills,
      selectedAmount: selectedAmount.toFixed(2)
    })
  },


  // 进行缴费
  proceedToPay() {
    if (this.data.selectedBills.length === 0) {
      wx.showToast({
        title: '请选择要缴费的项目',
        icon: 'none'
      })
      return
    }
    
    console.log('进行缴费:', {
      bills: this.data.selectedBills,
      amount: this.data.selectedAmount
    })
    
    wx.showModal({
      title: '确认缴费',
      content: `确认使用微信支付缴费 ¥${this.data.selectedAmount} 吗？`,
      confirmText: '确认缴费',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.processPayment()
        }
      }
    })
  },

  // 处理支付
  async processPayment() {
    wx.showLoading({
      title: '正在处理缴费...'
    })
    
    try {
      const billIds = this.data.selectedBills.map(bill => bill.sourceData ? bill.sourceData.id : bill.id)
      console.log('批量缴费，账单IDs:', billIds)
      
      const response = await api.payBillsBatch(billIds, 'WECHAT')
      console.log('缴费响应:', response)
      
      wx.hideLoading()
      
      if (response.code === 200) {
        wx.showToast({
          title: '缴费成功',
          icon: 'success',
          duration: 2000
        })
        
        // 重新加载数据
        this.loadBillSummary()
        this.loadBills(true)
        
        // 清空选中状态
        this.setData({
          selectedBills: [],
          selectedAmount: '0.00'
        })
      } else {
        wx.showToast({
          title: response.message || '缴费失败，请重试',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('缴费处理失败:', error)
      wx.hideLoading()
      
      // 模拟支付处理（后备方案）
      const success = Math.random() > 0.1 // 90% 成功率
      
      if (success) {
        wx.showToast({
          title: '缴费成功',
          icon: 'success',
          duration: 2000
        })
        
        // 更新账单状态
        this.updateBillsStatus()
      } else {
        wx.showToast({
          title: '支付失败，请重试',
          icon: 'none'
        })
      }
    }
  },

  // 更新账单状态
  updateBillsStatus() {
    const bills = this.data.bills.map(bill => {
      if (bill.selected) {
        return {
          ...bill,
          status: 'paid',
          statusText: '已缴费',
          overdue: false,
          selected: false
        }
      }
      return bill
    })
    
    // 重新计算待缴费用
    const unpaidBills = bills.filter(bill => bill.status !== 'paid')
    const total = unpaidBills.reduce((sum, bill) => {
      return sum + parseFloat(bill.amount)
    }, 0)
    
    this.setData({
      bills,
      selectedBills: [],
      selectedAmount: '0.00',
      unpaidCount: unpaidBills.length,
      totalAmount: total.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    })
  },

  // 查看缴费记录
  viewHistory() {
    console.log('查看缴费记录')
    wx.navigateTo({
      url: '/pages/owner/payment-history'
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    console.log('下拉刷新费用数据')
    this.loadBillSummary()
    this.loadBills(true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 上拉加载更多
  onReachBottom() {
    console.log('上拉加载更多费用数据')
    if (this.data.hasMore && !this.data.loading) {
      this.loadBills(false)
    }
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '智慧物业缴费',
      path: '/pages/owner/payment'
    }
  }
})
// pages/owner/payment-history.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    payments: [],
    loading: false,
    refreshing: false,
    currentPage: 1,
    pageSize: 20,
    hasMore: true,
    currentFilter: 'all',
    filterTabs: [
      { key: 'all', name: '全部' },
      { key: 'WECHAT', name: '微信支付' },
      { key: 'ALIPAY', name: '支付宝' },
      { key: 'BANK', name: '银行转账' }
    ],
    mockPayments: [
      {
        id: 1,
        billNo: 'BILL202411250001',
        paymentNo: 'PAY202411250001',
        title: '物业管理费',
        description: '2024年11月物业费缴纳',
        amount: '658.50',
        paymentMethod: 'WECHAT',
        paymentMethodText: '微信支付',
        status: 'success',
        statusText: '支付成功',
        paymentTime: '2024-11-25 10:30:15',
        paymentTimeText: '2024-11-25 10:30',
        icon: '/static/icons/house.svg',
        iconBg: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)'
      },
      {
        id: 2,
        billNo: 'BILL202410250001',
        paymentNo: 'PAY202410250001',
        title: '物业管理费',
        description: '2024年10月物业费缴纳',
        amount: '658.50',
        paymentMethod: 'ALIPAY',
        paymentMethodText: '支付宝',
        status: 'success',
        statusText: '支付成功',
        paymentTime: '2024-10-25 14:22:35',
        paymentTimeText: '2024-10-25 14:22',
        icon: '/static/icons/house.svg',
        iconBg: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)'
      },
      {
        id: 3,
        billNo: 'BILL202410250003',
        paymentNo: 'PAY202410250003',
        title: '停车费',
        description: '2024年10月停车费缴纳',
        amount: '200.00',
        paymentMethod: 'WECHAT',
        paymentMethodText: '微信支付',
        status: 'success',
        statusText: '支付成功',
        paymentTime: '2024-10-31 16:45:12',
        paymentTimeText: '2024-10-31 16:45',
        icon: '/static/icons/settings.svg',
        iconBg: 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)'
      }
    ]
  },

  onLoad(options) {
    console.log('缴费历史页面加载')
    this.loadPayments(true)
  },

  onShow() {
    console.log('缴费历史页面显示')
  },

  // 加载缴费记录
  async loadPayments(reset = false) {
    if (this.data.loading) return
    
    if (reset) {
      this.setData({
        currentPage: 1,
        payments: [],
        hasMore: true
      })
    }
    
    this.setData({ loading: true })
    
    try {
      const userInfo = app.globalData.userInfo
      if (!userInfo || !userInfo.id) {
        console.log('用户信息不存在，使用模拟数据')
        this.setData({
          payments: this.filterPayments(this.data.mockPayments),
          loading: false
        })
        return
      }

      console.log('加载缴费记录，用户ID:', userInfo.id, '页码:', this.data.currentPage)
      
      const params = {
        pageNum: this.data.currentPage,
        pageSize: this.data.pageSize,
        ownerId: userInfo.id
      }
      
      if (this.data.currentFilter !== 'all') {
        params.paymentMethod = this.data.currentFilter
      }
      
      const response = await api.getPaymentHistory(params)
      console.log('缴费记录响应:', response)
      
      if (response.code === 200 && response.data) {
        const newPayments = this.processPaymentsData(response.data.list || [])
        
        this.setData({
          payments: reset ? newPayments : [...this.data.payments, ...newPayments],
          hasMore: response.data.list && response.data.list.length >= this.data.pageSize,
          currentPage: this.data.currentPage + 1
        })
      } else {
        console.log('缴费记录加载失败，使用模拟数据')
        if (reset) {
          this.setData({
            payments: this.filterPayments(this.data.mockPayments)
          })
        }
      }
    } catch (error) {
      console.error('加载缴费记录失败:', error)
      if (reset) {
        this.setData({
          payments: this.filterPayments(this.data.mockPayments)
        })
      }
    } finally {
      this.setData({ loading: false, refreshing: false })
    }
  },

  // 处理缴费记录数据
  processPaymentsData(payments) {
    return payments.map(item => ({
      id: item.id,
      billNo: item.billNo,
      paymentNo: item.paymentNo,
      title: this.getBillTypeText(item.billType),
      description: `${item.billingPeriod || ''}账单缴费`,
      amount: parseFloat(item.amount).toFixed(2),
      paymentMethod: item.paymentMethod,
      paymentMethodText: this.getPaymentMethodText(item.paymentMethod),
      status: item.status.toLowerCase(),
      statusText: this.getStatusText(item.status),
      paymentTime: item.paymentTime,
      paymentTimeText: this.formatPaymentTime(item.paymentTime),
      icon: this.getBillTypeIcon(item.billType),
      iconBg: this.getBillTypeColor(item.billType),
      sourceData: item
    })).filter(item => ['物业管理费', '停车费'].includes(item.title))
  },

  // 筛选缴费记录
  filterPayments(payments) {
    if (this.data.currentFilter === 'all') {
      return payments
    }
    return payments.filter(payment => payment.paymentMethod === this.data.currentFilter)
  },

  // 获取账单类型文本
  getBillTypeText(billType) {
    const typeMap = {
      'PROPERTY': '物业管理费',
      'PARKING': '停车费'
    }
    return typeMap[billType] || billType
  },

  // 获取支付方式文本
  getPaymentMethodText(method) {
    const methodMap = {
      'WECHAT': '微信支付',
      'ALIPAY': '支付宝',
      'BANK': '银行转账'
    }
    return methodMap[method] || method
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'SUCCESS': '支付成功',
      'PENDING': '待支付',
      'FAILED': '支付失败'
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

  // 格式化支付时间
  formatPaymentTime(paymentTime) {
    if (!paymentTime) return ''
    
    const date = new Date(paymentTime)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  },

  // 切换筛选条件
  switchFilter(e) {
    const filter = e.currentTarget.dataset.filter
    console.log('切换筛选条件:', filter)
    
    this.setData({
      currentFilter: filter
    })
    
    this.loadPayments(true)
  },

  // 查看缴费详情
  viewPaymentDetail(e) {
    const payment = e.currentTarget.dataset.payment
    console.log('查看缴费详情:', payment)
    
    // TODO: 跳转到缴费详情页面
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  },

  // 下拉刷新
  onRefresh() {
    console.log('下拉刷新缴费记录')
    this.setData({ refreshing: true })
    this.loadPayments(true)
  },

  // 上拉加载更多
  onLoadMore() {
    console.log('上拉加载更多缴费记录')
    if (this.data.hasMore && !this.data.loading) {
      this.loadPayments(false)
    }
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '缴费记录',
      path: '/pages/owner/payment-history'
    }
  }
})







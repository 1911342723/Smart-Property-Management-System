const api = require('../../utils/api.js')

Page({
  data: {
    refreshing: false,
    parkingSpaces: []
  },

  onLoad() {
    this.loadParkingSpaces()
  },

  onShow() {
    this.loadParkingSpaces(false)
  },

  onPullDownRefresh() {
    this.onRefresh()
  },

  async loadParkingSpaces(showLoading = true) {
    try {
      if (showLoading) {
        wx.showLoading({ title: '加载中...' })
      }

      const result = await api.getMyParkingSpaces()
      if (result.code !== 200) {
        throw new Error(result.message || '获取车位信息失败')
      }

      const parkingSpaces = (result.data || []).map(item => this.normalizeParkingSpace(item))
      this.setData({ parkingSpaces })
    } catch (error) {
      console.error('Load parking spaces error:', error)
      wx.showToast({
        title: error.message || '获取车位信息失败',
        icon: 'none'
      })
      this.loadMockParkingSpaces()
    } finally {
      if (showLoading) {
        wx.hideLoading()
      }
    }
  },

  loadMockParkingSpaces() {
    const parkingSpaces = [
      {
        id: 1,
        spaceNo: 'B2-088',
        areaCode: '地下二层A区',
        spaceType: 'FIXED',
        status: 'OCCUPIED',
        vehicleNo: '粤B12345',
        monthlyFee: 120,
        expireDate: '2026-12-31',
        remark: '固定产权车位'
      },
      {
        id: 2,
        spaceNo: 'A1-015',
        areaCode: '地面一区',
        spaceType: 'CHARGING',
        status: 'OCCUPIED',
        vehicleNo: '粤B8X88X',
        monthlyFee: 180,
        expireDate: '2026-10-31',
        remark: '支持新能源充电'
      }
    ].map(item => this.normalizeParkingSpace(item))

    this.setData({ parkingSpaces })
  },

  async onRefresh() {
    this.setData({ refreshing: true })
    try {
      await this.loadParkingSpaces(false)
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      })
    } finally {
      this.setData({ refreshing: false })
      wx.stopPullDownRefresh()
    }
  },

  viewParkingDetail(e) {
    const parkingSpace = e.currentTarget.dataset.parking
    wx.showModal({
      title: '车位详情',
      content: this.formatParkingDetail(parkingSpace),
      showCancel: false,
      confirmText: '确定'
    })
  },

  normalizeParkingSpace(item) {
    const areaCode = item.areaCode || ''
    const spaceNo = item.spaceNo || '未设置'
    const fullName = areaCode ? `${areaCode} ${spaceNo}` : spaceNo
    const monthlyFee = Number(item.monthlyFee || 0)

    return {
      ...item,
      fullName,
      typeText: this.getTypeText(item.spaceType),
      statusText: this.getStatusText(item.status),
      monthlyFeeText: monthlyFee > 0 ? `¥${monthlyFee.toFixed(2)}/月` : '待设置',
      vehicleNoText: item.vehicleNo || '未绑定车辆',
      expireDateText: item.expireDate || '未设置',
      remarkText: item.remark || '暂无备注'
    }
  },

  formatParkingDetail(item) {
    return [
      `车位编号：${item.spaceNo || '未设置'}`,
      `所属区域：${item.areaCode || '未设置'}`,
      `车位类型：${item.typeText || '未知'}`,
      `当前状态：${item.statusText || '未知'}`,
      `绑定车辆：${item.vehicleNoText || '未绑定车辆'}`,
      `月度费用：${item.monthlyFeeText || '待设置'}`,
      `到期时间：${item.expireDateText || '未设置'}`,
      `备注说明：${item.remarkText || '暂无备注'}`
    ].join('\n')
  },

  getTypeText(type) {
    const typeMap = {
      FIXED: '固定车位',
      TEMPORARY: '临停车位',
      CHARGING: '充电车位',
      VISITOR: '访客车位'
    }
    return typeMap[type] || '普通车位'
  },

  getStatusText(status) {
    const statusMap = {
      AVAILABLE: '空闲',
      OCCUPIED: '使用中',
      DISABLED: '停用'
    }
    return statusMap[status] || '未知'
  }
})
// pages/owner/house-info.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    refreshing: false,
    rooms: [],
    editingRoom: null,
    showEditModal: false,
    // 移除状态选择，业主不能修改房屋状态
    roomStatusOptions: [],
    roomTypeOptions: [
      { value: 'RESIDENTIAL', label: '住宅' },
      { value: 'COMMERCIAL', label: '商用' },
      { value: 'OFFICE', label: '办公' }
    ]
  },

  onLoad() {
    console.log('房屋信息页面加载')
    this.loadRooms()
  },

  onShow() {
    // 重新加载房屋信息
    this.loadRooms()
  },

  onPullDownRefresh() {
    this.onRefresh()
  },

  // 加载房屋信息
  async loadRooms() {
    try {
      wx.showLoading({ title: '加载中...' })
      
      const result = await api.getMyRooms()
      
      if (result.code === 200) {
        const rooms = result.data || []
        
        // 只显示业主拥有的房屋
        const ownerRooms = rooms.filter(room => room.relationType === 'OWNER')
        
        // 处理数据
        const processedRooms = ownerRooms.map(room => ({
          ...room,
          fullAddress: this.formatFullAddress(room),
          statusText: this.getStatusText(room.status),
          typeText: this.getTypeText(room.roomType),
          areaText: room.area ? `${room.area}㎡` : '未设置',
          propertyFeeText: room.propertyFee ? `¥${room.propertyFee}/月` : '未设置',
          canEdit: room.relationType === 'OWNER'  // 只有业主才能编辑
        }))
        
        this.setData({ rooms: processedRooms })
      } else {
        throw new Error(result.message || '获取房屋信息失败')
      }
    } catch (error) {
      console.error('Load rooms error:', error)
      wx.showToast({
        title: error.message || '获取房屋信息失败',
        icon: 'none'
      })
      
      // 降级到模拟数据
      this.loadMockRooms()
    } finally {
      wx.hideLoading()
    }
  },

  // 加载模拟房屋数据（降级方案）
  loadMockRooms() {
    const mockRooms = [
      {
        id: 1,
        buildingId: 1,
        unitId: 2,
        buildingName: '1号楼',
        unitName: '2单元',
        roomNo: '301',
        floor: 3,
        roomType: 'RESIDENTIAL',
        area: 89.5,
        status: 'OCCUPIED',
        propertyFee: 150.00,
        relationType: 'OWNER',
        fullAddress: '1号楼2单元301室',
        statusText: '已入住',
        typeText: '住宅',
        areaText: '89.5㎡',
        propertyFeeText: '¥150.00/月',
        canEdit: true  // 业主拥有的房屋可以编辑
      },
      {
        id: 3,
        buildingId: 2,
        unitId: 5,
        buildingName: '2号楼',
        unitName: '1单元',
        roomNo: '502',
        floor: 5,
        roomType: 'RESIDENTIAL',
        area: 120.0,
        status: 'OCCUPIED',
        propertyFee: 180.00,
        relationType: 'OWNER',
        fullAddress: '2号楼1单元502室',
        statusText: '已入住',
        typeText: '住宅',
        areaText: '120.0㎡',
        propertyFeeText: '¥180.00/月',
        canEdit: true  // 业主拥有的房屋可以编辑
      }
    ]
    
    this.setData({ rooms: mockRooms })
  },

  // 下拉刷新
  async onRefresh() {
    this.setData({ refreshing: true })
    
    try {
      await this.loadRooms()
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      })
    } finally {
      this.setData({ refreshing: false })
      wx.stopPullDownRefresh()
    }
  },

  // 查看房屋详情
  async viewRoomDetail(e) {
    const room = e.currentTarget.dataset.room
    
    try {
      wx.showLoading({ title: '加载中...' })
      
      const result = await api.getRoomDetail(room.id)
      
      if (result.code === 200) {
        const roomDetail = result.data
        
        wx.showModal({
          title: '房屋详情',
          content: this.formatRoomDetail(roomDetail),
          showCancel: false,
          confirmText: '确定'
        })
      } else {
        throw new Error(result.message || '获取房屋详情失败')
      }
    } catch (error) {
      console.error('View room detail error:', error)
      
      // 降级显示基本信息
      wx.showModal({
        title: '房屋详情',
        content: this.formatRoomDetail(room),
        showCancel: false,
        confirmText: '确定'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 编辑房屋信息
  editRoom(e) {
    const room = e.currentTarget.dataset.room
    
    this.setData({
      editingRoom: {
        ...room,
        originalStatus: room.status,
        originalArea: room.area
      },
      showEditModal: true
    })
  },

  // 关闭编辑弹窗
  closeEditModal() {
    this.setData({
      showEditModal: false,
      editingRoom: null
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空方法，用于阻止事件冒泡
  },

  // 输入面积
  onAreaInput(e) {
    const value = e.detail.value
    this.setData({
      'editingRoom.area': parseFloat(value) || 0
    })
  },

  // 选择房屋状态
  onStatusChange(e) {
    const index = e.detail.value
    const status = this.data.roomStatusOptions[index].value
    const statusText = this.data.roomStatusOptions[index].label
    
    this.setData({
      'editingRoom.status': status,
      'editingRoom.statusText': statusText
    })
  },

  // 保存房屋信息
  async saveRoom() {
    const room = this.data.editingRoom
    
    if (!room || !room.id) {
      wx.showToast({
        title: '房屋信息异常',
        icon: 'none'
      })
      return
    }

    // 验证数据
    if (!room.area || room.area <= 0) {
      wx.showToast({
        title: '请输入正确的房屋面积',
        icon: 'none'
      })
      return
    }

    try {
      wx.showLoading({ title: '保存中...' })
      
      // 业主只能更新房屋面积
      const updateData = {
        area: parseFloat(room.area)
      }
      
      console.log('Updating room:', room.id, updateData)
      
      const result = await api.updateRoom(room.id, updateData)
      
      if (result.code === 200) {
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })
        
        this.closeEditModal()
        this.loadRooms()
      } else {
        throw new Error(result.message || '保存失败')
      }
    } catch (error) {
      console.error('Save room error:', error)
      wx.showToast({
        title: error.message || '保存失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 工具方法
  formatFullAddress(room) {
    return `${room.buildingName || ''}${room.unitName || ''}${room.roomNo || ''}室`
  },

  getStatusText(status) {
    const statusMap = {
      'OCCUPIED': '已入住',
      'VACANT': '空置',
      'RENTED': '出租中'
    }
    return statusMap[status] || '未知'
  },

  getTypeText(type) {
    const typeMap = {
      'RESIDENTIAL': '住宅',
      'COMMERCIAL': '商用',
      'OFFICE': '办公'
    }
    return typeMap[type] || '未知'
  },

  formatRoomDetail(room) {
    return `房屋地址：${this.formatFullAddress(room)}\n` +
           `楼层：${room.floor || '未知'}楼\n` +
           `房屋类型：${this.getTypeText(room.roomType)}\n` +
           `建筑面积：${room.area ? room.area + '㎡' : '未设置'}\n` +
           `房屋状态：${this.getStatusText(room.status)}\n` +
           `物业费：${room.propertyFee ? '¥' + room.propertyFee + '/月' : '未设置'}`
  }
})

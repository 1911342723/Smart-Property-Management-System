// pages/owner/home.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    refreshing: false,
    unreadCount: 5,
    userInfo: {},
    greeting: '',
    houseInfo: null,
    // 公告相关
    notices: [],
    showNoticePopup: false,
    recentNotices: [], // 首页显示的最近公告
    quickServices: [
      {
        id: 1,
        name: '物业报修',
        icon: '/static/icons/repair.svg',
        gradient: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        route: '/pages/owner/repair',
        badge: null
      },
      {
        id: 2,
        name: '费用缴纳',
        icon: '/static/icons/payment.svg',
        gradient: 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)',
        route: '/pages/owner/payment',
        badge: null
      },
      {
        id: 3,
        name: '投诉建议',
        icon: '/static/icons/complaint.svg',
        gradient: 'linear-gradient(135deg, #718096 0%, #4a5568 100%)',
        route: '/pages/owner/complaint',
        badge: null
      },
      {
        id: 4,
        name: '访客通行',
        icon: '/static/icons/visitor.svg',
        gradient: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        route: '/pages/owner/visitor',
        badge: null
      },
      {
        id: 5,
        name: '我的车位',
        icon: '/static/icons/location.svg',
        gradient: 'linear-gradient(135deg, #0f766e 0%, #115e59 100%)',
        route: '/pages/owner/parking',
        badge: null
      }
    ],
    todoList: [],
    activities: [],
    activitiesError: null
  },

  onLoad() {
    console.log('业主首页加载')
    this.initData()
    
  },

  onShow() {
    this.setGreeting()
    this.loadUserInfo()
    
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(0)
    }
  },

  // 初始化数据
  initData() {
    this.setGreeting()
    this.loadUserInfo()
    this.loadHouseInfo()
    this.loadUnreadCount()
    this.loadBillSummary()
    this.loadTodoList()
    this.loadActivities()
    this.loadNotices()
  },

  // 设置问候语
  setGreeting() {
    const hour = new Date().getHours()
    let greeting = '上午好'
    if (hour >= 12 && hour < 18) {
      greeting = '下午好'
    } else if (hour >= 18) {
      greeting = '晚上好'
    }
    
    this.setData({ greeting })
  },

  // 加载用户信息
  loadUserInfo() {
    // 优先从全局数据获取
    let userInfo = app.globalData.userInfo || {}
    
    // 如果全局数据为空，从API获取最新用户信息
    if (!userInfo.id) {
      api.getUserInfo().then(response => {
        if (response.code === 200) {
          userInfo = response.data
          app.globalData.userInfo = userInfo
          wx.setStorageSync('userInfo', userInfo)
          
          this.setData({ 
            userInfo: {
              name: userInfo.realName || userInfo.name || '用户',
              avatar: userInfo.avatar || '/static/images/default-avatar.png'
            }
          })
        }
      }).catch(error => {
        console.error('Load user info error:', error)
        // 降级到本地存储的用户信息
        const localUserInfo = wx.getStorageSync('userInfo') || {}
        this.setData({ 
          userInfo: {
            name: localUserInfo.realName || localUserInfo.name || '用户',
            avatar: localUserInfo.avatar || '/static/images/default-avatar.png'
          }
        })
      })
    } else {
      this.setData({ 
        userInfo: {
          name: userInfo.realName || userInfo.name || '用户',
          avatar: userInfo.avatar || '/static/images/default-avatar.png'
        }
      })
    }
  },

  // 加载房屋信息
  loadHouseInfo() {
    const self = this
    console.log('=== 开始加载房屋信息 ===')
    
    api.getMyRooms().then(result => {
      console.log('API返回结果:', result)
      
      if (result.code === 200 && result.data && result.data.length > 0) {
        const primaryRoom = result.data[0] // 取第一个房屋作为主要房屋信息
        console.log('主房屋信息:', primaryRoom)
        
        const houseInfo = {
          building: primaryRoom.buildingName || '未知楼栋',
          unit: primaryRoom.unitName || '未知单元', 
          room: primaryRoom.roomNo || '未知房号',
          area: primaryRoom.area || 0,
          type: self.getRoomTypeText(primaryRoom.roomType),
          propertyFee: primaryRoom.propertyFee || 0,
          moveInDate: self.formatMoveInDate(primaryRoom.createTime),
          status: primaryRoom.status,
          statusText: self.getRoomStatusText(primaryRoom.status)
        }
        
        console.log('处理后的房屋信息:', houseInfo)
        self.setData({ houseInfo })
      } else {
        // 如果没有房屋信息，设置默认值
        self.setData({
          houseInfo: {
            building: '暂无',
            unit: '暂无',
            room: '暂无',
            area: 0,
            type: '暂无',
            propertyFee: 0,
            moveInDate: '暂无',
            status: 'VACANT',
            statusText: '暂无'
          }
        })
      }
    }).catch(error => {
      console.error('Load house info error:', error)
      // 降级到默认数据
      self.setData({
        houseInfo: {
          building: '1号楼',
          unit: '2单元',
          room: '301',
          area: 89.5,
          type: '住宅',
          propertyFee: 2.8,
          moveInDate: '2023-06-15',
          status: 'OCCUPIED',
          statusText: '已入住'
        }
      })
    })
  },

  // 加载未读消息统计
  loadUnreadCount() {
    const self = this
    
    api.getUnreadCount().then(result => {
      if (result.code === 200) {
        const unreadCount = result.data.all || 0
        self.setData({ unreadCount })
      }
    }).catch(error => {
      console.error('加载未读消息统计失败:', error)
      // 使用默认值
      self.setData({ unreadCount: 0 })
    })
  },

  // 加载账单统计
  loadBillSummary() {
    const self = this
    const userInfo = app.globalData.userInfo
    
    if (!userInfo || !userInfo.id) {
      console.log('用户信息不存在，跳过加载账单统计')
      return
    }

    api.getBillSummary(userInfo.id).then(result => {
      if (result.code === 200 && result.data) {
        const summary = result.data
        const unpaidCount = summary.unpaidCount || 0
        
        // 更新快捷服务中费用缴纳按钮的badge
        const quickServices = [...self.data.quickServices]
        const paymentServiceIndex = quickServices.findIndex(service => service.id === 2)
        
        if (paymentServiceIndex !== -1) {
          if (unpaidCount > 0) {
            quickServices[paymentServiceIndex].badge = unpaidCount > 99 ? '99+' : unpaidCount.toString()
          } else {
            quickServices[paymentServiceIndex].badge = null
          }
          
          self.setData({ quickServices })
        }
      }
    }).catch(error => {
      console.error('加载账单统计失败:', error)
      // 降级处理：不显示badge或使用默认值
      const quickServices = [...self.data.quickServices]
      const paymentServiceIndex = quickServices.findIndex(service => service.id === 2)
      
      if (paymentServiceIndex !== -1) {
        quickServices[paymentServiceIndex].badge = null
        self.setData({ quickServices })
      }
    })
  },

  // 加载待办事项
  loadTodoList() {
    const self = this
    const userInfo = app.globalData.userInfo
    const userId = userInfo.id
    
    if (!userId) {
      console.log('用户ID不存在，跳过加载待办事项')
      return
    }

    // 使用Promise.all同时加载工单和账单数据
    Promise.all([
      api.getWorkOrderList({
        pageNum: 1,
        pageSize: 5,
        submitterId: userId,
        status: 'PENDING,PROCESSING'
      }),
      api.getBillList({
        pageNum: 1,
        pageSize: 5,
        ownerId: userId,
        status: 'UNPAID'
      })
    ]).then(responses => {
      const [workOrdersResponse, billsResponse] = responses
      let todoList = []
      
      // 处理工单数据
      if (workOrdersResponse.code === 200 && workOrdersResponse.data.list) {
        const workOrders = workOrdersResponse.data.list.map(function(item) {
          return {
            id: 'wo_' + item.id,
            title: item.title,
            description: item.content,
            type: 'repair',
            status: item.status.toLowerCase(),
            createTime: item.submitTime || item.createTime,
            sourceData: item
          }
        })
        todoList = todoList.concat(workOrders)
      }

      // 处理账单数据
      if (billsResponse.code === 200 && billsResponse.data.list) {
        const bills = billsResponse.data.list.map(function(item) {
          return {
            id: 'bill_' + item.id,
            title: self.getBillTypeText(item.billType) + '缴费',
            description: item.billingPeriod + '期账单待缴纳',
            type: 'payment',
            status: 'pending',
            createTime: item.createTime,
            sourceData: item
          }
        })
        todoList = todoList.concat(bills)
      }

      // 按创建时间排序
      todoList.sort(function(a, b) {
        const dateA = self.parseDate(a.createTime)
        const dateB = self.parseDate(b.createTime)
        return dateB.getTime() - dateA.getTime()
      })
      
      // 只显示前3条
      todoList = todoList.slice(0, 3)

      // 处理显示数据
      const processedTodoList = todoList.map(function(item) {
        return Object.assign({}, item, {
          iconUrl: self.getTodoIcon(item.type),
          statusColor: self.getStatusColor(item.status),
          statusText: self.getStatusText(item.status, item.type),
          relativeTime: self.formatRelativeTime(item.createTime)
        })
      })

      self.setData({ todoList: processedTodoList })
      
    }).catch(error => {
      console.error('Load todo list error:', error)
      // 降级到假数据
      self.loadMockTodoList()
    })
  },

  // 加载模拟待办数据（降级方案）
  loadMockTodoList() {
    const todoList = [
      {
        id: 1,
        title: '物业费缴纳',
        description: '2024年1月物业费待缴纳',
        type: 'payment',
        status: 'pending',
        createTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)
      },
      {
        id: 2,
        title: '报修进度',
        description: '厨房水龙头维修进度更新',
        type: 'repair',
        status: 'processing',
        createTime: new Date(Date.now() - 6 * 60 * 60 * 1000)
      }
    ]

    const processedTodoList = todoList.map(function(item) {
      return Object.assign({}, item, {
        iconUrl: this.getTodoIcon(item.type),
        statusColor: this.getStatusColor(item.status),
        statusText: this.getStatusText(item.status, item.type),
        relativeTime: this.formatRelativeTime(item.createTime)
      })
    }.bind(this))

    this.setData({ todoList: processedTodoList })
  },


  // 加载社区活动
  loadActivities() {
    const self = this
    console.log('=== 开始加载社区活动 ===')
    
    // 重置错误状态
    this.setData({ activitiesError: null })
    
    // 获取最近的活动，限制数量为3个
    api.getActivityList({
      pageNum: 1,
      pageSize: 3,
      status: 'UPCOMING,REGISTRATION,ONGOING' // 只获取即将开始、报名中、进行中的活动
    }).then(result => {
      console.log('活动API返回结果:', result)
      
      if (result.code === 200) {
        // 打印详细的数据结构用于调试
        console.log('result.data详细结构:', result.data)
        console.log('result.data.list是否存在:', result.data && result.data.list)
        console.log('result.data.records是否存在:', result.data && result.data.records)
        
        // 处理成功情况 - 兼容不同的数据结构
        let activityList = null
        if (result.data && result.data.list) {
          activityList = result.data.list
        } else if (result.data && result.data.records) {
          // MyBatis Plus分页返回的是records字段
          activityList = result.data.records
        } else if (result.data && Array.isArray(result.data)) {
          // 直接返回数组
          activityList = result.data
        }
        
        if (activityList && activityList.length > 0) {
          const activities = activityList.map(function(item) {
            return Object.assign({}, item, {
              timeText: self.formatDate(item.startTime, 'MM月DD日'),
              statusText: self.getActivityStatusText(item.status),
              imageUrl: item.imageUrl || self.getDefaultActivityImage(item.type),
              summary: item.summary || (item.description ? item.description.substring(0, 30) + '...' : '')
            })
          })
          
          console.log('处理后的活动数据:', activities)
          self.setData({ activities, activitiesError: null })
        } else {
          // 数据为空的情况
          console.log('活动数据为空，result.data:', result.data)
          self.setData({ activities: [], activitiesError: null })
        }
      } else {
        // 业务错误
        throw new Error(result.message || '获取活动失败')
      }
    }).catch(error => {
      console.error('Load activities error:', error)
      
      // 显示错误状态，但不显示用户不友好的错误信息
      self.setData({ 
        activities: [],
        activitiesError: '暂无活动数据'
      })
      
      console.warn('活动加载失败:', error.message)
    })
  },

  // 重试加载活动
  retryLoadActivities() {
    this.setData({ activitiesError: null })
    this.loadActivities()
  },

  // 加载公告
  loadNotices() {
    const self = this
    
    // 初始化公告状态
    this.setData({
      notices: [],
      showNoticePopup: false,
      recentNotices: []
    })
    
    // 分别加载置顶公告和最近公告，避免一个失败影响另一个
    
    // 加载置顶公告（用于弹窗）
    api.getTopAnnouncements()
      .then(topResult => {
        console.log('置顶公告API返回:', topResult)
        
        if (topResult.code === 200) {
          let notices = []
          
          if (topResult.data && Array.isArray(topResult.data)) {
            notices = topResult.data.map(function(item) {
              return Object.assign({}, item, {
                dateText: self.formatDate(item.publishTime, 'MM-DD HH:mm'),
                typeText: self.getNoticeTypeText(item.type)
              })
            })
          }
          
          console.log('处理后的置顶公告:', notices)
          
          // 检查是否有新公告需要显示
          const hasUnreadNotices = notices.some(function(notice) {
            const lastReadTime = wx.getStorageSync('lastReadNoticeTime') || 0
            const noticeTime = self.parseDate(notice.publishTime).getTime()
            return noticeTime > lastReadTime
          })
          
          console.log('是否显示弹窗:', hasUnreadNotices && notices.length > 0)
          
          self.setData({ 
            notices: notices,
            showNoticePopup: hasUnreadNotices && notices.length > 0
          })
        } else {
          console.error('获取置顶公告失败:', topResult)
        }
      })
      .catch(error => {
        console.error('加载置顶公告失败:', error)
      })
    
    // 加载最近公告（用于首页展示）
    api.getAnnouncementList({ pageNum: 1, pageSize: 5 })
      .then(recentResult => {
        console.log('最近公告API返回:', recentResult)
        console.log('最近公告data完整内容:', JSON.stringify(recentResult.data))
        
        if (recentResult.code === 200) {
          // 兼容不同的数据结构
          let recentNotices = []
          if (recentResult.data) {
            if (recentResult.data.records) {
              // 分页格式：{ records: [], total: 0 }
              recentNotices = recentResult.data.records
              console.log('从data.records获取数据，长度:', recentNotices.length)
            } else if (Array.isArray(recentResult.data)) {
              // 数组格式：[]
              recentNotices = recentResult.data
              console.log('从data直接获取数据，长度:', recentNotices.length)
            } else {
              console.warn('未知的数据格式，data类型:', typeof recentResult.data, recentResult.data)
            }
          }
          
          console.log('原始最近公告数据:', recentNotices)
          
          const processedNotices = recentNotices.map(function(item) {
            return Object.assign({}, item, {
              dateText: self.formatDate(item.publishTime || item.publishedAt, 'MM-DD HH:mm'),
              typeText: self.getNoticeTypeText(item.type),
              summary: item.summary || (item.content ? item.content.substring(0, 50) + '...' : ''),
              publisherName: item.publisherName || item.author || '系统管理员'
            })
          })
          
          console.log('处理后的最近公告:', processedNotices)
          
          self.setData({ recentNotices: processedNotices.slice(0, 3) })
        } else {
          console.error('获取最近公告失败:', recentResult)
        }
      })
      .catch(error => {
        console.error('加载最近公告失败:', error)
      })
  },

  // 关闭公告弹窗
  closeNoticePopup() {
    this.setData({ showNoticePopup: false })
    
    // 记录最后阅读时间
    wx.setStorageSync('lastReadNoticeTime', new Date().getTime())
  },

  // 查看全部公告
  viewAllNotices() {
    console.log('点击查看全部公告')
    wx.navigateTo({
      url: '/pages/owner/notices'
    })
  },

  // 按类型查看公告
  viewNoticesByType(e) {
    const type = e.currentTarget.dataset.type
    wx.navigateTo({
      url: `/pages/owner/notices?type=${type}`
    })
  },

  // 查看公告详情
  viewNoticeDetail(e) {
    console.log('点击公告详情', e)
    const notice = e.currentTarget.dataset.notice
    console.log('公告数据:', notice)
    
    if (!notice || !notice.id) {
      console.error('公告信息无效')
      wx.showToast({
        title: '公告信息错误',
        icon: 'none'
      })
      return
    }

    console.log('跳转到详情页:', notice.id)
    wx.navigateTo({
      url: `/pages/owner/notice-detail?id=${notice.id}`
    })
  },

  // 重新加载公告
  retryLoadNotices() {
    this.loadNotices()
  },

  // 获取公告类型文本
  getNoticeTypeText(type) {
    const typeMap = {
      'SYSTEM': '系统公告',
      'MAINTENANCE': '维护公告', 
      'EVENT': '活动公告',
      'EMERGENCY': '紧急通知',
      'GENERAL': '一般通知'
    }
    return typeMap[type] || '通知'
  },

  // 下拉刷新
  onRefresh() {
    const self = this
    self.setData({ refreshing: true })
    
    // 刷新数据
    setTimeout(() => {
      self.initData()
      
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      })
      
      self.setData({ refreshing: false })
    }, 1000)
  },

  // 加载更多
  loadMore() {
    // 加载更多数据的逻辑
  },

  // 导航到服务页面
  navigateToService(e) {
    const service = e.currentTarget.dataset.service
    console.log('=== 点击服务 ===', service)
    
    if (!service || !service.route) {
      console.error('服务配置错误:', service)
      wx.showToast({
        title: '页面配置错误',
        icon: 'none'
      })
      return
    }
    
    // 对于报修页面，直接使用switchTab
    if (service.route === '/pages/owner/repair') {
      console.log('跳转到报修页面，使用switchTab')
      wx.switchTab({
        url: service.route,
        success: () => {
          console.log('switchTab跳转报修页面成功')
        },
        fail: (error) => {
          console.error('switchTab跳转报修页面失败:', error)
        }
      })
      return
    }
    
    // 其他页面使用navigateTo
    console.log('跳转到普通页面:', service.route)
    wx.navigateTo({
      url: service.route,
      success: () => {
        console.log('navigateTo跳转成功:', service.route)
      },
      fail: (error) => {
        console.error('navigateTo跳转失败:', error)
        wx.showToast({
          title: '页面跳转失败: ' + error.errMsg,
          icon: 'none'
        })
      }
    })
  },

  // 打开消息中心
  openMessages() {
    wx.switchTab({
      url: '/pages/owner/messages'
    })
  },

  // 打开AI客服
  openAiChat() {
    wx.navigateTo({
      url: '/pages/common/ai-chat'
    })
  },

  // 编辑房屋信息
  editHouseInfo() {
    wx.navigateTo({
      url: '/pages/owner/house-info'
    })
  },

  // 查看全部待办
  viewAllTodos() {
    wx.showToast({
      title: '查看全部待办',
      icon: 'none'
    })
  },

  // 处理待办点击
  handleTodoClick(e) {
    const todo = e.currentTarget.dataset.todo
    const routeMap = {
      payment: '/pages/owner/payment',
      repair: '/pages/owner/repair',
      complaint: '/pages/owner/complaint'
    }
    
    if (routeMap[todo.type]) {
      wx.navigateTo({
        url: routeMap[todo.type]
      })
    }
  },


  // 查看全部活动
  viewAllActivities() {
    wx.navigateTo({
      url: '/pages/common/activity-list',
      fail: (error) => {
        console.error('导航到活动列表页失败:', error)
        wx.showToast({
          title: '功能开发中',
          icon: 'none'
        })
      }
    })
  },

  // 查看活动详情
  viewActivityDetail(e) {
    const activity = e.currentTarget.dataset.activity
    wx.navigateTo({
      url: `/pages/common/activity-detail?id=${activity.id}`
    })
  },


  // 工具方法
  getTodoIcon(type) {
    const iconMap = {
      payment: '/static/icons/payment.svg',
      repair: '/static/icons/repair.svg',
      complaint: '/static/icons/complaint.svg',
      visitor: '/static/icons/visitor.svg'
    }
    return iconMap[type] || '/static/icons/edit.svg'
  },

  getStatusColor(status) {
    const colorMap = {
      pending: '#d69e2e',
      processing: '#38a169',
      completed: '#38a169',
      cancelled: '#a0aec0',
      urgent: '#e53e3e',
      resolved: '#38a169'
    }
    return colorMap[status] || '#718096'
  },

  getStatusText(status, type) {
    const statusTexts = {
      repair: {
        pending: '待接单',
        processing: '处理中',
        completed: '已完成',
        cancelled: '已取消'
      },
      payment: {
        pending: '待缴费',
        paid: '已缴费',
        overdue: '已逾期'
      },
      complaint: {
        pending: '待处理',
        processing: '处理中',
        resolved: '已解决',
        closed: '已关闭'
      }
    }
    return statusTexts[type]?.[status] || status
  },

  // 通用日期解析方法，处理iOS兼容性
  parseDate(date) {
    if (!date) {
      return new Date()
    }
    
    if (typeof date === 'string') {
      // 将 "YYYY-MM-DD HH:mm:ss" 格式转换为 iOS 兼容的格式
      const normalizedDate = date.replace(/\s/g, 'T').replace(/-/g, '/')
      let d = new Date(normalizedDate)
      
      // 如果转换失败，尝试其他方式
      if (isNaN(d.getTime())) {
        d = new Date(date.replace(/-/g, '/'))
      }
      
      // 验证日期是否有效
      if (isNaN(d.getTime())) {
        console.warn('Invalid date:', date)
        return new Date() // 使用当前时间作为备选
      }
      
      return d
    } else {
      return new Date(date)
    }
  },

  formatRelativeTime(date) {
    const now = new Date()
    const target = this.parseDate(date)
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
  },

  formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    const d = this.parseDate(date)
    
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')
    
    return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  },


  getActivityStatusText(status) {
    const statusMap = {
      'UPCOMING': '即将开始',
      'REGISTRATION': '报名中', 
      'ONGOING': '进行中',
      'COMPLETED': '已结束',
      'CANCELLED': '已取消',
      // 兼容旧版本
      'upcoming': '即将开始',
      'registration': '报名中',
      'ongoing': '进行中',
      'completed': '已结束'
    }
    return statusMap[status] || '未知'
  },

  // 获取默认活动图片
  getDefaultActivityImage(type) {
    const imageMap = {
      'SPORTS': '/static/images/activity-sports.jpg',
      'ENTERTAINMENT': '/static/images/activity-entertainment.jpg', 
      'EDUCATION': '/static/images/activity-education.jpg',
      'COMMUNITY': '/static/images/activity-community.jpg',
      'HEALTH': '/static/images/activity-health.jpg'
    }
    return imageMap[type] || '/static/images/activity-default.jpg'
  },

  // 活动报名
  registerForActivity(e) {
    const activity = e.currentTarget.dataset.activity
    const self = this
    
    // 检查报名状态
    if (activity.status !== 'REGISTRATION') {
      wx.showToast({
        title: '当前活动不在报名期间',
        icon: 'none'
      })
      return
    }
    
    // 检查是否已满员
    if (activity.currentParticipants >= activity.maxParticipants) {
      wx.showToast({
        title: '报名人数已满',
        icon: 'none'
      })
      return
    }
    
    wx.showModal({
      title: '确认报名',
      content: `确定要报名参加"${activity.title}"吗？\n\n活动时间：${activity.timeText}\n活动地点：${activity.location || '待定'}\n当前报名：${activity.currentParticipants}/${activity.maxParticipants}人`,
      confirmText: '确认报名',
      cancelText: '取消',
      success(res) {
        if (res.confirm) {
          // 显示加载提示
          wx.showLoading({
            title: '报名中...',
            mask: true
          })
          
          api.registerActivity(activity.id, {
            remarks: '',
            contactPhone: '', // 可以后续添加联系方式输入
            emergencyContact: ''
          }).then(result => {
            wx.hideLoading()
            
            if (result.code === 200) {
              wx.showModal({
                title: '报名成功',
                content: '您已成功报名参加活动，请按时参加。如有变动，我们会及时通知您。',
                showCancel: false,
                confirmText: '知道了',
                success() {
                  // 刷新活动数据
                  self.loadActivities()
                }
              })
            } else {
              wx.showModal({
                title: '报名失败',
                content: result.message || '报名失败，请稍后重试',
                showCancel: false,
                confirmText: '知道了'
              })
            }
          }).catch(error => {
            wx.hideLoading()
            console.error('Register activity error:', error)
            
            wx.showModal({
              title: '报名失败',
              content: '网络错误，请检查网络连接后重试',
              showCancel: false,
              confirmText: '知道了'
            })
          })
        }
      }
    })
  },

  getBillTypeText(type) {
    const typeMap = {
      'PROPERTY': '物业费',
      'PARKING': '停车费'
    }
    return typeMap[type] || '费用'
  },

  getRoomTypeText(type) {
    const typeMap = {
      'RESIDENTIAL': '住宅',
      'COMMERCIAL': '商用',
      'OFFICE': '办公'
    }
    return typeMap[type] || '住宅'
  },

  getRoomStatusText(status) {
    const statusMap = {
      'OCCUPIED': '已入住',
      'VACANT': '空置',
      'RENTED': '出租中'
    }
    return statusMap[status] || '未知'
  },

  formatMoveInDate(createTime) {
    if (!createTime) {
      return '暂无'
    }
    // 使用已经修复的formatDate方法处理日期兼容性
    return this.formatDate(createTime, 'YYYY-MM-DD')
  }
})
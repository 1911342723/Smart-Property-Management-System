// 消息通知页面逻辑
Page({
  data: {
    // 当前激活的标签
    activeTab: 'all',
    
    // 未读消息数量
    unreadCounts: {
      all: 0,
      system: 0,
      work: 0,
      notice: 0
    },
    
    // 所有消息
    allMessages: [],
    
    // 当前显示的消息
    currentMessages: [],
    
    // 分页参数
    pageNum: 1,
    pageSize: 20,
    loading: false,
    noMore: false,
    
    // 全部已读状态
    markingAllRead: false,
    
    // 弹窗
    showMessageModal: false,
    selectedMessage: null
  },

  onLoad(options) {
    this.loadMessages()
  },

  onShow() {
    this.loadMessages()
    this.updateUnreadCounts()
  },

  onPullDownRefresh() {
    this.refreshData()
    wx.stopPullDownRefresh()
  },

  onReachBottom() {
    this.loadMoreMessages()
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab
    })
    this.filterMessages()
  },

  // 刷新数据
  refreshData() {
    this.setData({
      pageNum: 1,
      allMessages: [],
      noMore: false
    })
    this.loadMessages()
  },

  // 加载消息
  loadMessages() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    // 模拟API请求
    setTimeout(() => {
      const newMessages = this.generateMockMessages(this.data.pageNum, this.data.pageSize)
      const allMessages = this.data.pageNum === 1 ? newMessages : [...this.data.allMessages, ...newMessages]
      
      this.setData({
        allMessages: allMessages,
        loading: false,
        noMore: newMessages.length < this.data.pageSize,
        pageNum: this.data.pageNum + 1
      })
      
      this.filterMessages()
      this.updateUnreadCounts()
    }, 1000)
  },

  // 加载更多消息
  loadMoreMessages() {
    if (!this.data.noMore && !this.data.loading) {
      this.loadMessages()
    }
  },

  // 生成模拟消息数据
  generateMockMessages(pageNum, pageSize) {
    const messages = []
    const types = ['system', 'work', 'notice']
    const typeNames = {
      system: '系统通知',
      work: '工作消息', 
      notice: '公告通知'
    }
    
    const mockData = [
      {
        type: 'system',
        title: '系统维护通知',
        content: '系统将于今晚23:00-01:00进行维护升级，期间可能影响部分功能使用，请提前做好工作安排。维护完成后将第一时间恢复服务。',
        priority: 'high',
        sender: '系统管理员'
      },
      {
        type: 'work',
        title: '值班安排调整',
        content: '由于本周末有特殊活动，需要调整保安值班安排。请查看最新的排班表，如有问题请及时联系管理员。',
        actionRequired: true,
        sender: '物业管理部'
      },
      {
        type: 'notice',
        title: '小区停车管理新规定',
        content: '为规范小区停车秩序，即日起实施新的停车管理规定。所有临时车辆需要在门岗登记，业主车辆需要办理停车证。',
        attachments: [
          { name: '停车管理规定.pdf', url: 'https://example.com/parking-rules.pdf' }
        ],
        sender: '物业管理部'
      },
      {
        type: 'work',
        title: '巡检路线更新',
        content: '根据小区安全需要，更新了部分巡检路线和时间安排。新增了地下车库B2层和新建的儿童游乐区域。',
        sender: '安保部'
      },
      {
        type: 'system',
        title: '密码安全提醒',
        content: '为保障账户安全，建议定期更换登录密码。密码应包含字母、数字和特殊字符，长度不少于8位。',
        sender: '系统管理员'
      }
    ]
    
    for (let i = 0; i < pageSize && i < mockData.length; i++) {
      const index = (pageNum - 1) * pageSize + i
      const data = mockData[i % mockData.length]
      
      const date = new Date()
      date.setHours(date.getHours() - index * 2)
      
      const message = {
        id: `msg_${index}`,
        type: data.type,
        title: data.title,
        content: data.content,
        createTime: this.formatTime(date),
        isRead: Math.random() > 0.4,
        readTime: Math.random() > 0.5 ? this.formatTime(new Date(date.getTime() + 3600000)) : null,
        priority: data.priority || 'normal',
        actionRequired: data.actionRequired || false,
        attachments: data.attachments || null,
        sender: data.sender
      }
      
      messages.push(message)
    }
    
    return messages
  },

  // 过滤消息
  filterMessages() {
    const { activeTab, allMessages } = this.data
    let filteredMessages = allMessages
    
    if (activeTab !== 'all') {
      filteredMessages = allMessages.filter(msg => msg.type === activeTab)
    }
    
    this.setData({
      currentMessages: filteredMessages
    })
  },

  // 更新未读数量
  updateUnreadCounts() {
    const { allMessages } = this.data
    const unreadCounts = {
      all: 0,
      system: 0,
      work: 0,
      notice: 0
    }
    
    allMessages.forEach(msg => {
      if (!msg.isRead) {
        unreadCounts.all++
        unreadCounts[msg.type]++
      }
    })
    
    this.setData({ unreadCounts })
  },

  // 标记全部已读
  markAllRead() {
    this.setData({ markingAllRead: true })
    
    // 模拟API请求
    setTimeout(() => {
      const allMessages = this.data.allMessages.map(msg => ({
        ...msg,
        isRead: true,
        readTime: msg.readTime || this.formatTime(new Date())
      }))
      
      this.setData({
        allMessages: allMessages,
        markingAllRead: false
      })
      
      this.filterMessages()
      this.updateUnreadCounts()
      
      wx.showToast({
        title: '已全部标记为已读',
        icon: 'success'
      })
    }, 1500)
  },

  // 查看消息详情
  viewMessage(e) {
    const message = e.currentTarget.dataset.message
    
    // 标记为已读
    if (!message.isRead) {
      this.markMessageAsRead(message.id)
    }
    
    this.setData({
      selectedMessage: message,
      showMessageModal: true
    })
  },

  // 标记单条消息已读
  markMessageAsRead(messageId) {
    const allMessages = this.data.allMessages.map(msg => {
      if (msg.id === messageId && !msg.isRead) {
        return {
          ...msg,
          isRead: true,
          readTime: this.formatTime(new Date())
        }
      }
      return msg
    })
    
    this.setData({ allMessages })
    this.filterMessages()
    this.updateUnreadCounts()
  },

  // 关闭消息详情
  closeMessageModal() {
    this.setData({
      showMessageModal: false,
      selectedMessage: null
    })
  },

  // 处理消息操作
  handleMessageAction(e) {
    const action = e.currentTarget.dataset.action
    const { selectedMessage } = this.data
    
    if (action === 'confirm') {
      wx.showToast({
        title: '已确认处理',
        icon: 'success'
      })
    } else if (action === 'later') {
      wx.showToast({
        title: '已标记稍后处理',
        icon: 'none'
      })
    }
    
    this.closeMessageModal()
  },

  // 下载附件
  downloadAttachment(e) {
    const url = e.currentTarget.dataset.url
    
    wx.showToast({
      title: '开始下载',
      icon: 'success'
    })
    
    // 这里可以实现真实的下载逻辑
    console.log('下载附件:', url)
  },

  // 获取类型图标
  getTypeIcon(type) {
    const icons = {
      system: '/static/icons/settings.svg',
      work: '/static/icons/wrench.svg',
      notice: '/static/icons/bell.svg'
    }
    return icons[type] || '/static/icons/message.svg'
  },

  // 获取类型名称
  getTypeName(type) {
    const names = {
      system: '系统通知',
      work: '工作消息',
      notice: '公告通知'
    }
    return names[type] || '消息'
  },

  // 获取标签名称
  getTabName(tab) {
    const names = {
      all: '全部',
      system: '系统通知',
      work: '工作消息',
      notice: '公告通知'
    }
    return names[tab] || ''
  },

  // 格式化时间
  formatTime(date) {
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    
    if (days === 0) {
      // 今天
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `今天 ${hours}:${minutes}`
    } else if (days === 1) {
      // 昨天
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `昨天 ${hours}:${minutes}`
    } else if (days < 7) {
      // 一周内
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${weekdays[date.getDay()]} ${hours}:${minutes}`
    } else {
      // 超过一周
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${month}-${day} ${hours}:${minutes}`
    }
  }
})











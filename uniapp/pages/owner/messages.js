// pages/owner/messages.js
const api = require('../../utils/api.js')
const app = getApp()

Page({
  data: {
    activeTab: 'all',
    messageTabs: [
      { value: 'all', label: '全部', count: 0 },
      { value: 'SYSTEM', label: '系统', count: 0 },
      { value: 'SERVICE', label: '服务', count: 0 },
      { value: 'NOTICE', label: '公告', count: 0 }
    ],
    allMessages: [],
    currentMessages: [],
    loading: false,
    pageNum: 1,
    pageSize: 20,
    hasMore: true
  },

  onLoad() {
    console.log('消息页面加载')
    this.loadMessages()
    this.loadUnreadCount()
  },

  onShow() {
    console.log('消息页面显示')
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(2)
    }
    
    // 刷新消息列表
    this.loadMessages(true)
    this.loadUnreadCount()
  },

  // 加载消息数据
  async loadMessages(refresh = true) {
    if (this.data.loading) return

    console.log('开始加载消息，refresh:', refresh, 'activeTab:', this.data.activeTab)
    this.setData({ loading: true })

    try {
      const messageType = this.data.activeTab === 'all' ? null : this.data.activeTab
      const params = {
        pageNum: refresh ? 1 : this.data.pageNum,
        pageSize: this.data.pageSize
      }
      
      // 只有不是全部时才传 messageType
      if (messageType) {
        params.messageType = messageType
      }

      console.log('请求参数:', params)
      const result = await api.getMessageList(params)
      console.log('消息列表响应:', result)
      
      if (result && result.code === 200) {
        // 兼容不同的返回格式
        const messages = result.data?.records || result.data?.list || result.data || []
        console.log('解析到的消息数量:', messages.length, messages)
        
        // 处理消息数据
        const processedMessages = messages.map(item => ({
          ...item,
          id: item.id,
          title: item.title,
          content: item.content,
          read: item.isRead === 1 || item.isRead === true,
          type: item.messageType,
          icon: item.icon || '/static/icons/message.svg',
          iconBg: item.iconBg || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          timeText: this.formatRelativeTime(item.createTime)
        }))

        console.log('处理后的消息:', processedMessages)

        if (refresh) {
          this.setData({
            currentMessages: processedMessages,
            pageNum: 1,
            hasMore: messages.length >= this.data.pageSize
          })
        } else {
          this.setData({
            currentMessages: [...this.data.currentMessages, ...processedMessages],
            hasMore: messages.length >= this.data.pageSize
          })
        }

        this.setData({ pageNum: params.pageNum + 1 })
        
        console.log('当前消息列表:', this.data.currentMessages)
      } else {
        console.error('加载消息失败，返回结果:', result)
        wx.showToast({
          title: result?.message || '加载失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('加载消息失败:', error)
      wx.showToast({
        title: '网络错误，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 加载未读消息统计
  async loadUnreadCount() {
    try {
      const result = await api.getUnreadCount()
      
      if (result.code === 200) {
        const counts = result.data
        const tabs = this.data.messageTabs.map(tab => {
          switch (tab.value) {
            case 'all':
              tab.count = counts.all || 0
              break
            case 'SYSTEM':
              tab.count = counts.system || 0
              break
            case 'SERVICE':
              tab.count = counts.service || 0
              break
            case 'NOTICE':
              tab.count = counts.notice || 0
              break
          }
          return tab
        })

        this.setData({ messageTabs: tabs })
      }
    } catch (error) {
      console.error('加载未读统计失败:', error)
    }
  },

  // 切换标签页
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab
    })
    this.loadMessages(true)
  },

  // 阅读消息
  async readMessage(e) {
    const message = e.currentTarget.dataset.message
    
    // 标记为已读
    if (!message.read) {
      try {
        const result = await api.markMessageAsRead(message.id)
        
        if (result.code === 200) {
          // 更新本地数据
          const messages = this.data.currentMessages.map(msg => {
            if (msg.id === message.id) {
              msg.read = true
              msg.isRead = 1
            }
            return msg
          })

          this.setData({
            currentMessages: messages
          })

          // 重新加载未读统计
          this.loadUnreadCount()
        }
      } catch (error) {
        console.error('标记已读失败:', error)
      }
    }

    // 显示消息详情（可以跳转到详情页面或弹窗显示）
    wx.showModal({
      title: message.title,
      content: message.content,
      showCancel: false,
      confirmText: '我知道了'
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadMessages(true)
    this.loadUnreadCount()
    wx.stopPullDownRefresh()
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMessages(false)
    }
  },

  // 格式化相对时间
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
// pages/worker/messages.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    messages: [],
    filteredMessages: [],
    activeTab: 'all',
    tabs: [
      { key: 'all', name: '全部' },
      { key: 'SYSTEM', name: '系统消息' },
      { key: 'ORDER', name: '工单消息' },
      { key: 'NOTICE', name: '通知公告' }
    ],
    unreadCount: 0,
    loading: false,
    hasMore: true,
    pageNum: 1,
    pageSize: 20
  },

  onLoad(options) {
    console.log('消息通知页面加载')
    this.loadMessages()
    this.loadUnreadCount()
  },

  onShow() {
    // 刷新未读数量
    this.loadUnreadCount()
  },

  // 加载消息列表
  async loadMessages() {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      }

      if (this.data.activeTab !== 'all') {
        params.messageType = this.data.activeTab
      }

      const response = await api.getMessageList(params)

      if (response.code === 200) {
        const list = response.data?.list || []
        const messages = list.map(item => ({
          id: item.id,
          title: item.title,
          content: item.content,
          type: item.messageType,
          isRead: item.isRead === 1,
          createTime: this.formatTime(item.createTime),
          relatedId: item.relatedId
        }))

        const allMessages = this.data.pageNum === 1 
          ? messages 
          : [...this.data.messages, ...messages]

        this.setData({
          messages: allMessages,
          filteredMessages: allMessages,
          hasMore: messages.length >= this.data.pageSize,
          loading: false
        })
      } else {
        wx.showToast({
          title: response.message || '加载失败',
          icon: 'none'
        })
        this.setData({ loading: false })
      }
    } catch (error) {
      console.error('加载消息失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
    }
  },

  // 加载未读消息数量
  async loadUnreadCount() {
    try {
      const response = await api.getUnreadCount()
      if (response.code === 200 && response.data) {
        this.setData({
          unreadCount: response.data
        })
      }
    } catch (error) {
      console.error('加载未读数量失败:', error)
    }
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({
      activeTab: tab,
      pageNum: 1,
      messages: [],
      filteredMessages: []
    })
    this.loadMessages()
  },

  // 查看消息详情
  async viewMessage(e) {
    const message = e.currentTarget.dataset.message

    // 如果消息未读，标记为已读
    if (!message.isRead) {
      try {
        await api.markMessageAsRead(message.id)
        // 更新本地状态
        const messages = this.data.messages.map(item => {
          if (item.id === message.id) {
            return { ...item, isRead: true }
          }
          return item
        })
        this.setData({
          messages: messages,
          filteredMessages: messages,
          unreadCount: Math.max(0, this.data.unreadCount - 1)
        })
      } catch (error) {
        console.error('标记已读失败:', error)
      }
    }

    // 显示消息内容
    wx.showModal({
      title: message.title,
      content: message.content,
      showCancel: false,
      confirmText: '知道了'
    })

    // 如果有关联工单，提供跳转选项
    if (message.type === 'ORDER' && message.relatedId) {
      setTimeout(() => {
        wx.showModal({
          title: '查看相关工单',
          content: '是否查看该工单详情？',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({
                url: `/pages/worker/order-detail?id=${message.relatedId}`
              })
            }
          }
        })
      }, 500)
    }
  },

  // 全部标记为已读
  async markAllAsRead() {
    wx.showModal({
      title: '标记已读',
      content: '确定将所有消息标记为已读吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' })
            
            const unreadMessages = this.data.messages
              .filter(msg => !msg.isRead)
              .map(msg => msg.id)
            
            if (unreadMessages.length > 0) {
              const response = await api.batchMarkMessagesAsRead(unreadMessages)
              
              if (response.code === 200) {
                const messages = this.data.messages.map(item => ({
                  ...item,
                  isRead: true
                }))
                
                this.setData({
                  messages: messages,
                  filteredMessages: messages,
                  unreadCount: 0
                })
                
                wx.showToast({
                  title: '已全部标记为已读',
                  icon: 'success'
                })
              }
            } else {
              wx.showToast({
                title: '没有未读消息',
                icon: 'none'
              })
            }
          } catch (error) {
            console.error('标记已读失败:', error)
            wx.showToast({
              title: '操作失败',
              icon: 'none'
            })
          } finally {
            wx.hideLoading()
          }
        }
      }
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({
      pageNum: 1,
      messages: [],
      filteredMessages: []
    })
    Promise.all([
      this.loadMessages(),
      this.loadUnreadCount()
    ]).finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        pageNum: this.data.pageNum + 1
      })
      this.loadMessages()
    }
  },

  // 格式化时间
  formatTime(timeStr) {
    if (!timeStr) return ''
    
    const date = new Date(timeStr.replace(/-/g, '/'))
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    
    const minute = 60 * 1000
    const hour = 60 * minute
    const day = 24 * hour
    
    if (diff < minute) {
      return '刚刚'
    } else if (diff < hour) {
      return `${Math.floor(diff / minute)}分钟前`
    } else if (diff < day) {
      return `${Math.floor(diff / hour)}小时前`
    } else if (diff < 3 * day) {
      return `${Math.floor(diff / day)}天前`
    } else {
      const month = date.getMonth() + 1
      const day = date.getDate()
      return `${month}月${day}日`
    }
  }
})

// components/notice-popup/notice-popup.js
Component({
  properties: {
    notices: {
      type: Array,
      value: []
    },
    visible: {
      type: Boolean,
      value: false
    }
  },

  data: {
    currentIndex: 0
  },

  methods: {
    closePopup() {
      this.triggerEvent('close')
    },

    preventTouchMove() {
      // 阻止滚动穿透
      return false
    },

    onSwiperChange(e) {
      this.setData({
        currentIndex: e.detail.current
      })
    },

    // 查看公告详情
    viewDetail(e) {
      const notice = e.currentTarget.dataset.notice
      if (!notice || !notice.id) {
        return
      }
      
      // 关闭弹窗
      this.triggerEvent('close')
      
      // 跳转到详情页
      wx.navigateTo({
        url: `/pages/owner/notice-detail?id=${notice.id}`
      })
    },

    // 查看全部公告
    viewAllNotices() {
      // 关闭弹窗
      this.triggerEvent('close')
      
      // 跳转到公告列表页
      wx.navigateTo({
        url: '/pages/owner/notices'
      })
    }
  }
})






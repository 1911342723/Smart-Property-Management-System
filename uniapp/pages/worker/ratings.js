// pages/worker/ratings.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    ratings: [],
    stats: {
      avgRating: 0,
      totalRatings: 0,
      fiveStars: 0,
      fourStars: 0,
      threeStars: 0,
      twoStars: 0,
      oneStars: 0
    },
    loading: false
  },

  onLoad(options) {
    console.log('评价管理页面加载')
    this.loadRatings()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadRatings()
  },

  async loadRatings() {
    try {
      this.setData({ loading: true })
      
      // 获取维修工的评价列表（使用新的评价API）
      const response = await api.getWorkerRatings(1, 100)

      if (response.code === 200) {
        const ratingsList = response.data?.records || response.data?.list || []
        
        const ratings = []
        let totalRating = 0
        let ratingCount = 0
        const starCounts = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }

        ratingsList.forEach(rating => {
          totalRating += rating.overallScore || 0
          ratingCount++
          starCounts[rating.overallScore] = (starCounts[rating.overallScore] || 0) + 1
          
          // 解析评价标签
          let tags = []
          try {
            if (rating.tags) {
              tags = JSON.parse(rating.tags)
            }
          } catch (e) {
            console.log('解析标签失败:', e)
          }
          
          ratings.push({
            id: rating.id,
            orderId: rating.orderId,
            title: rating.orderTitle || '工单',
            rating: rating.overallScore,
            serviceAttitudeScore: rating.serviceAttitudeScore,
            workQualityScore: rating.workQualityScore,
            responseSpeedScore: rating.responseSpeedScore,
            professionalismScore: rating.professionalismScore,
            content: rating.content || '暂无评价',
            tags: tags,
            images: rating.images,
            isAnonymous: rating.isAnonymous,
            date: this.formatDate(rating.createTime),
            ownerName: rating.isAnonymous ? '匿名用户' : (rating.raterName || '业主'),
            workerReply: rating.workerReply,
            replyTime: rating.replyTime,
            isHelpful: rating.isHelpful || 0
          })
        })

        this.setData({
          ratings: ratings,
          stats: {
            avgRating: ratingCount > 0 ? (totalRating / ratingCount).toFixed(1) : 0,
            totalRatings: ratingCount,
            fiveStars: starCounts[5] || 0,
            fourStars: starCounts[4] || 0,
            threeStars: starCounts[3] || 0,
            twoStars: starCounts[2] || 0,
            oneStars: starCounts[1] || 0
          },
          loading: false
        })
      }
    } catch (error) {
      console.error('加载评价失败:', error)
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

// pages/owner/repair.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    repairCategories: [
      {
        id: 1,
        name: '水电维修',
        icon: '/static/icons/droplet.svg',
        gradient: 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)',
        description: '水管漏水、电路故障等'
      },
      {
        id: 2,
        name: '家电维修',
        icon: '/static/icons/plug.svg',
        gradient: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        description: '冰箱、洗衣机、热水器等'
      },
      {
        id: 3,
        name: '门窗维修',
        icon: '/static/icons/door.svg',
        gradient: 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)',
        description: '门锁损坏、窗户故障等'
      },
      {
        id: 4,
        name: '空调维修',
        icon: '/static/icons/air-vent.svg',
        gradient: 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)',
        description: '空调不制冷、异响等'
      },
      {
        id: 5,
        name: '网络维修',
        icon: '/static/icons/wifi.svg',
        gradient: 'linear-gradient(135deg, #718096 0%, #4a5568 100%)',
        description: '网络故障、弱电维修等'
      },
      {
        id: 6,
        name: '其他维修',
        icon: '/static/icons/wrench.svg',
        gradient: 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)',
        description: '其他各类维修需求'
      }
    ],
    recentRepairs: [],
    repairStats: {
      total: 0,
      pending: 0,
      processing: 0,
      completed: 0
    }
  },

  onLoad() {
    console.log('=== 报修页面加载成功 ===')
    try {
      this.loadRepairData()
    } catch (error) {
      console.error('报修页面初始化失败:', error)
      wx.showToast({
        title: '页面初始化失败',
        icon: 'none'
      })
    }
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(1)
    }
  },

  // 加载报修数据
  async loadRepairData() {
    try {
      const userInfo = app.globalData.userInfo
      const userId = userInfo.id
      
      if (!userId) {
        console.log('用户ID不存在，跳过加载报修数据')
        this.loadMockRepairData()
        return
      }

      // 调用API获取工单列表
      const response = await api.getWorkOrderList({
        pageNum: 1,
        pageSize: 10,
        submitterId: userId,
        category: 'REPAIR'
      })

      console.log('API响应数据:', response)
      
      if (response.code === 200 && response.data && response.data.list) {
        console.log('原始数据列表:', response.data.list)
        
        const repairs = response.data.list.map(item => {
          console.log('处理单个工单:', item)
          return {
            id: item.id,
            title: item.title,
            description: item.content,
            category: this.mapCategoryFromBackend(item.category),
            status: item.status.toLowerCase(),
            createTime: new Date(item.submitTime || item.createTime),
            workerName: item.assigneeName || '待分配',
            workerPhone: '',
            completedTime: item.completeTime ? new Date(item.completeTime) : null,
            priority: item.priority,
            rating: item.rating,
            feedback: item.feedback,
            sourceData: item // 保存原始数据供模板使用
          }
        })

        console.log('映射后的repairs:', repairs)

        // 处理显示数据
        const processedRepairs = repairs.map(item => ({
          ...item,
          iconUrl: this.getCategoryIcon(item.category),
          statusColor: this.getStatusColor(item.status),
          statusText: this.getStatusText(item.status),
          timeText: this.formatRelativeTime(item.createTime)
        }))

        console.log('最终处理的repairs:', processedRepairs)

        // 计算统计数据
        const stats = {
          total: repairs.length,
          pending: repairs.filter(r => r.status === 'pending').length,
          processing: repairs.filter(r => r.status === 'processing').length,
          completed: repairs.filter(r => r.status === 'completed').length
        }

        this.setData({
          recentRepairs: processedRepairs,
          repairStats: stats
        })
        
        console.log('设置到页面的数据:', { processedRepairs, stats })
      } else {
        console.error('数据结构不匹配:', response)
        console.log('response.code:', response.code)
        console.log('response.data:', response.data)
        throw new Error('获取工单数据失败 - 数据结构不匹配')
      }
      
    } catch (error) {
      console.error('Load repair data error:', error)
      // 降级到模拟数据
      this.loadMockRepairData()
    }
  },

  // 加载模拟报修数据（降级方案）
  loadMockRepairData() {
    const repairs = [
      {
        id: 1,
        title: '厨房水龙头漏水',
        description: '厨房洗菜盆水龙头一直在滴水',
        category: 'water',
        status: 'processing',
        createTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
        workerName: '张师傅',
        workerPhone: '13800138002'
      },
      {
        id: 2,
        title: '客厅空调不制冷',
        description: '空调开启后不制冷，风扇正常运转',
        category: 'air',
        status: 'completed',
        createTime: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
        workerName: '李师傅',
        workerPhone: '13800138003',
        completedTime: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000)
      }
    ]

    const processedRepairs = repairs.map(item => ({
      ...item,
      iconUrl: this.getCategoryIcon(item.category),
      statusColor: this.getStatusColor(item.status),
      statusText: this.getStatusText(item.status),
      timeText: this.formatRelativeTime(item.createTime)
    }))

    const stats = {
      total: repairs.length,
      processing: repairs.filter(r => r.status === 'processing').length,
      completed: repairs.filter(r => r.status === 'completed').length
    }

    this.setData({
      recentRepairs: processedRepairs,
      repairStats: stats
    })
  },

  // 选择报修类别
  selectCategory(e) {
    const category = e.currentTarget.dataset.category
    
    // 显示选择快速保修或自定义保修的弹窗
    wx.showActionSheet({
      itemList: ['快速保修', '自定义保修'],
      success: (res) => {
        const isQuick = res.tapIndex === 0
        wx.navigateTo({
          url: `/pages/owner/repair-form?category=${category.id}&quick=${isQuick}`
        })
      }
    })
  },

  // 查看报修详情
  viewRepairDetail(e) {
    const repair = e.currentTarget.dataset.repair
    wx.navigateTo({
      url: `/pages/owner/repair-detail?id=${repair.id}`
    })
  },

  // 查看全部报修
  viewAllRepairs() {
    wx.navigateTo({
      url: '/pages/owner/repair-list'
    })
  },

  // 评价报修
  rateRepair(e) {
    const repair = e.currentTarget.dataset.repair
    
    wx.navigateTo({
      url: `/pages/owner/repair-detail?id=${repair.id}`
    })
  },

  // 工具方法
  getCategoryIcon(category) {
    const iconMap = {
      water: '/static/icons/droplet.svg',
      air: '/static/icons/air-vent.svg',
      door: '/static/icons/door.svg',
      appliance: '/static/icons/plug.svg',
      network: '/static/icons/wifi.svg',
      other: '/static/icons/wrench.svg'
    }
    return iconMap[category] || '/static/icons/wrench.svg'
  },

  getStatusColor(status) {
    const colorMap = {
      pending: '#f59e0b',     // 橙色 - 待处理
      processing: '#3b82f6',  // 蓝色 - 处理中
      completed: '#10b981',   // 绿色 - 已完成
      cancelled: '#6b7280',   // 灰色 - 已取消
      closed: '#374151'       // 深灰色 - 已关闭
    }
    return colorMap[status] || '#6b7280'
  },

  getStatusText(status) {
    const textMap = {
      pending: '待处理',
      processing: '处理中',
      completed: '已完成',
      cancelled: '已取消',
      closed: '已关闭'
    }
    return textMap[status] || '未知'
  },

  // 从后端类别映射到前端类别
  mapCategoryFromBackend(backendCategory) {
    const categoryMap = {
      'REPAIR': 'water'  // 默认映射到水电维修
    }
    return categoryMap[backendCategory] || 'other'
  },

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
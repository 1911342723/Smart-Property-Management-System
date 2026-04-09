// pages/worker/profile.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: null,
    workStats: {
      totalOrders: 0,
      completedOrders: 0,
      averageRating: 0,
      workDays: 0,
      processingOrders: 0,
      totalIncome: 0
    },
    availableSkillTags: [], // 从服务器获取的可用技能标签
    menuItems: [
      {
        id: 'work-record',
        name: '历史工单',
        icon: '/static/icons/history.svg',
        path: '/pages/worker/work-history',
        arrow: true
      },
      {
        id: 'income-stats',
        name: '收入统计',
        icon: '/static/icons/credit-card.svg',
        path: '/pages/worker/income-stats',
        arrow: true
      },
      {
        id: 'ratings',
        name: '评价管理',
        icon: '/static/icons/stats.svg',
        path: '/pages/worker/ratings',
        arrow: true
      },
      {
        id: 'messages',
        name: '消息通知',
        icon: '/static/icons/message.svg',
        path: '/pages/worker/messages',
        arrow: true
      },
      {
        id: 'settings',
        name: '设置',
        icon: '/static/icons/settings.svg',
        path: '/pages/worker/settings',
        arrow: true
      }
    ]
  },

  onLoad(options) {
    console.log('维修工个人中心页面加载')
    this.getUserInfo()
    this.loadWorkStats()
    this.loadAvailableSkillTags()
  },

  onShow() {
    // 初始化并更新tabbar
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo?.userType) {
        this.getTabBar().updateRoleTabBar(userInfo.userType)
      }
      this.getTabBar().updateSelected(2) // 维修工tabbar中"我的"是第3个（索引2）
    }
    
    // 刷新用户信息和技能标签
    this.getUserInfo()
    this.loadAvailableSkillTags()
  },

  // 获取用户信息
  async getUserInfo() {
    try {
      // 先从本地获取
      let userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      
      // 从服务器获取最新信息
      const response = await api.getUserInfo()
      if (response.code === 200 && response.data) {
        userInfo = {
          ...userInfo,
          ...response.data,
          // 映射后端字段到前端字段
          name: response.data.realName || response.data.name || '维修师傅',
          workerId: response.data.username || 'W001',
          phone: response.data.phone || '13800138002',
          avatar: response.data.avatar || '/static/icons/user.svg',
          skillTags: response.data.skillTags || userInfo.skillTags || [],
          workStatus: response.data.workStatus || userInfo.workStatus || 'AVAILABLE'
        }
        
        // 更新全局数据和本地存储
        app.globalData.userInfo = userInfo
        wx.setStorageSync('userInfo', userInfo)
      }
      
      // 加载维修工档案，获取工作状态
      try {
        const profileResponse = await api.getWorkerProfile()
        if (profileResponse.code === 200 && profileResponse.data) {
          userInfo.workStatus = profileResponse.data.workStatus || 'AVAILABLE'
          app.globalData.userInfo = userInfo
          wx.setStorageSync('userInfo', userInfo)
        }
      } catch (profileError) {
        console.log('获取维修工档案失败，使用默认状态:', profileError)
      }
      
      this.setData({
        userInfo: userInfo
      })
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 使用本地缓存
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo) {
        this.setData({
          userInfo: {
            ...userInfo,
            name: userInfo.realName || userInfo.name || '维修师傅',
            workerId: userInfo.username || userInfo.workerId || 'W001',
            phone: userInfo.phone || '13800138002',
            avatar: userInfo.avatar || '/static/icons/user.svg',
            skillTags: userInfo.skillTags || [],
            workStatus: userInfo.workStatus || 'AVAILABLE'
          }
        })
      }
    }
  },

  // 加载工作统计
  async loadWorkStats() {
    try {
      // 从后端获取维修工完整统计数据
      const statsResponse = await api.getWorkerStats()
      
      if (statsResponse.code === 200 && statsResponse.data) {
        const stats = statsResponse.data
        
        // 计算工作天数
        const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
        let workDays = 0
        
        if (userInfo?.createTime) {
          const registerDate = new Date(userInfo.createTime.replace(/-/g, '/'))
          const today = new Date()
          workDays = Math.ceil((today - registerDate) / (1000 * 60 * 60 * 24))
        }
        
        // 使用后端返回的统计数据
        this.setData({
          workStats: {
            totalOrders: stats.totalOrders || 0,
            completedOrders: stats.completedOrders || 0,
            processingOrders: stats.processingOrders || 0,
            averageRating: stats.averageRating || 0,
            workDays: workDays,
            totalIncome: stats.totalIncome || 0
          }
        })
        
        // 同时加载维修工档案获取更多信息
        this.loadWorkerProfile()
      } else {
        console.error('加载工作统计失败:', statsResponse.message)
        // 使用默认值
        this.setData({
          workStats: {
            totalOrders: 0,
            completedOrders: 0,
            processingOrders: 0,
            averageRating: 0,
            workDays: 0,
            totalIncome: 0
          }
        })
      }
    } catch (error) {
      console.error('加载工作统计出错:', error)
      wx.showToast({
        title: '统计数据加载失败',
        icon: 'none'
      })
    }
  },

  // 加载维修工档案
  async loadWorkerProfile() {
    try {
      const response = await api.getWorkerProfile()
      
      if (response.code === 200 && response.data) {
        const profile = response.data
        
        // 更新工作状态和详细统计
        this.setData({
          'userInfo.workStatus': profile.workStatus || 'available',
          'workStats.averageRating': profile.averageRating || 0,
          'workStats.totalIncome': profile.totalIncome || 0,
          'workStats.responseRate': profile.responseRate || 100,
          'workStats.completionRate': profile.completionRate || 100,
          'workStats.onTimeRate': profile.onTimeRate || 100
        })
      }
    } catch (error) {
      console.error('加载维修工档案失败:', error)
    }
  },

  // 加载可用的技能标签（从服务器获取管理员设置的标签）
  async loadAvailableSkillTags() {
    try {
      // 从服务器获取所有启用的技能标签
      const response = await api.getAllActiveSkills()
      
      if (response.code === 200 && response.data) {
        this.setData({
          availableSkillTags: response.data
        })
      }
      
      // 同时加载维修工已有的技能
      const skillsResponse = await api.getMySkills()
      if (skillsResponse.code === 200 && skillsResponse.data) {
        const skillTags = skillsResponse.data.map(skill => skill.skillName)
        
        // 更新用户信息中的技能标签
        this.setData({
          'userInfo.skillTags': skillTags
        })
      }
    } catch (error) {
      console.error('加载技能标签失败:', error)
    }
  },

  // 菜单项点击
  onMenuItemTap(e) {
    const item = e.currentTarget.dataset.item
    
    if (item.path) {
      if (item.path.startsWith('/pages/common/')) {
        wx.navigateTo({
          url: item.path
        })
      } else {
        wx.navigateTo({
          url: item.path
        })
      }
    } else {
      wx.showToast({
        title: `${item.name}功能开发中`,
        icon: 'none'
      })
    }
  },

  // 编辑个人信息
  editProfile() {
    const { userInfo } = this.data
    wx.showActionSheet({
      itemList: ['修改姓名', '修改电话'],
      success: (res) => {
        if (res.tapIndex === 0) {
          // 修改姓名
          wx.showModal({
            title: '修改姓名',
            editable: true,
            placeholderText: userInfo.name,
            success: (modalRes) => {
              if (modalRes.confirm && modalRes.content.trim()) {
                this.updateUserInfo({
                  name: modalRes.content.trim()
                })
              }
            }
          })
        } else if (res.tapIndex === 1) {
          // 修改电话
          wx.showModal({
            title: '修改电话',
            editable: true,
            placeholderText: userInfo.phone,
            success: (modalRes) => {
              if (modalRes.confirm && modalRes.content.trim()) {
                const phone = modalRes.content.trim()
                // 验证手机号格式
                if (!/^1[3-9]\d{9}$/.test(phone)) {
                  wx.showToast({
                    title: '手机号格式不正确',
                    icon: 'none'
                  })
                  return
                }
                this.updateUserInfo({
                  phone: phone
                })
              }
            }
          })
        }
      }
    })
  },

  // 更换头像
  changeAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFilePaths[0]
        
        wx.showLoading({
          title: '上传中...'
        })
        
        try {
          // 上传图片到服务器
          const uploadRes = await api.uploadFile(tempFilePath, {
            type: 'avatar'
          })
          
          if (uploadRes.code === 200 && uploadRes.data) {
            // 获取上传后的图片URL
            const avatarUrl = uploadRes.data.url || uploadRes.data.filePath || uploadRes.data
            
            // 更新用户信息
            await this.updateUserInfo({
              avatar: avatarUrl
            }, false) // false表示不显示重复的成功提示
            
            wx.hideLoading()
            wx.showToast({
              title: '头像更新成功',
              icon: 'success'
            })
          } else {
            throw new Error(uploadRes.message || '上传失败')
          }
        } catch (error) {
          console.error('上传头像失败:', error)
          wx.hideLoading()
          wx.showToast({
            title: error.message || '上传失败，请重试',
            icon: 'none'
          })
          
          // 如果上传失败，仍使用本地临时路径（仅在本地显示）
          this.setData({
            'userInfo.avatar': tempFilePath
          })
        }
      },
      fail: () => {
        wx.showToast({
          title: '取消选择',
          icon: 'none'
        })
      }
    })
  },

  // 编辑技能标签
  editSkillTags() {
    const { userInfo } = this.data
    const currentTags = userInfo.skillTags || []
    
    // 跳转到技能标签选择页面
    wx.navigateTo({
      url: `/pages/worker/skill-tags-selector?selected=${currentTags.join(',')}`
    })
  },

  // 显示技能标签选择器（使用服务器数据）
  showSkillTagsSelector() {
    const { availableSkillTags, userInfo } = this.data
    const currentTags = userInfo.skillTags || []
    
    if (availableSkillTags.length === 0) {
      wx.showToast({
        title: '暂无可选技能标签',
        icon: 'none'
      })
      return
    }

    // 过滤出未选择的技能标签
    const unselectedTags = availableSkillTags.filter(tag => 
      !currentTags.includes(tag.name)
    )
    
    if (unselectedTags.length === 0) {
      wx.showToast({
        title: '您已拥有所有可用技能',
        icon: 'none'
      })
      return
    }

    // 按分类分组显示
    const categories = {}
    unselectedTags.forEach(tag => {
      if (!categories[tag.category]) {
        categories[tag.category] = []
      }
      categories[tag.category].push(tag)
    })

    // 构建显示列表（分类 + 技能名称）
    const itemList = []
    const tagMap = [] // 用于映射选择项到实际标签
    
    Object.keys(categories).forEach(category => {
      categories[category].forEach(tag => {
        itemList.push(`【${tag.category}】${tag.name}`)
        tagMap.push(tag)
      })
    })
    
    wx.showActionSheet({
      itemList: itemList,
      success: (res) => {
        const selectedTag = tagMap[res.tapIndex]
        const newTags = [...currentTags, selectedTag.name]
        
        this.updateUserInfo({
          skillTags: newTags
        })
        
        // 同时保存标签ID（用于后端关联）
        this.saveUserSkillTagIds(selectedTag.id)
      }
    })
  },

  // 保存用户技能标签ID到服务器
  saveUserSkillTagIds(newTagId) {
    // 实际项目中调用API保存用户选择的技能标签ID
    console.log('保存技能标签ID到服务器:', newTagId)
    
    // 模拟API调用
    /*
    wx.request({
      url: app.globalData.baseUrl + '/api/worker/skill-tags',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        tagId: newTagId,
        action: 'add'
      },
      success: (res) => {
        if (res.data.code === 200) {
          console.log('技能标签保存成功')
        }
      }
    })
    */
  },

  // 删除技能标签
  removeSkillTag(e) {
    const { index, tag } = e.currentTarget.dataset
    const { availableSkillTags } = this.data
    
    wx.showModal({
      title: '删除技能标签',
      content: `确定要删除"${tag}"技能标签吗？\n\n删除后可以重新添加该技能。`,
      success: (res) => {
        if (res.confirm) {
          const currentTags = [...this.data.userInfo.skillTags]
          currentTags.splice(index, 1)
          
          this.updateUserInfo({
            skillTags: currentTags
          })

          // 找到对应的标签ID并从服务器删除
          const tagInfo = availableSkillTags.find(skillTag => skillTag.name === tag)
          if (tagInfo) {
            this.removeUserSkillTagFromServer(tagInfo.id)
          }
        }
      }
    })
  },

  // 从服务器删除用户技能标签
  removeUserSkillTagFromServer(tagId) {
    // 实际项目中调用API删除用户的技能标签
    console.log('从服务器删除技能标签ID:', tagId)
    
    // 模拟API调用
    /*
    wx.request({
      url: app.globalData.baseUrl + '/api/worker/skill-tags',
      method: 'DELETE',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        tagId: tagId
      },
      success: (res) => {
        if (res.data.code === 200) {
          console.log('技能标签删除成功')
        }
      }
    })
    */
  },

  // 刷新技能标签列表
  refreshSkillTags() {
    wx.showToast({
      title: '刷新中...',
      icon: 'loading'
    })
    
    // 重新加载可用的技能标签
    this.loadAvailableSkillTags()
  },

  // 更新用户信息
  async updateUserInfo(updates, showToast = true) {
    try {
      wx.showLoading({
        title: '更新中...'
      })
      
      // 调用API更新用户资料
      const response = await api.updateProfile(updates)
      
      if (response.code === 200) {
        // 更新成功后，重新获取用户信息
        const userInfoRes = await api.getUserInfo()
        
        let newUserInfo
        if (userInfoRes.code === 200 && userInfoRes.data) {
          newUserInfo = {
            ...this.data.userInfo,
            ...userInfoRes.data,
            skillTags: userInfoRes.data.skillTags || this.data.userInfo.skillTags
          }
        } else {
          // 如果获取失败，使用本地更新
          newUserInfo = {
            ...this.data.userInfo,
            ...updates
          }
        }
        
        this.setData({
          userInfo: newUserInfo
        })
        
        // 更新全局数据和本地存储
        app.globalData.userInfo = newUserInfo
        wx.setStorageSync('userInfo', newUserInfo)
        
        wx.hideLoading()
        
        if (showToast) {
          wx.showToast({
            title: '更新成功',
            icon: 'success'
          })
        }
      } else {
        throw new Error(response.message || '更新失败')
      }
    } catch (error) {
      console.error('更新用户信息失败:', error)
      wx.hideLoading()
      wx.showToast({
        title: error.message || '更新失败，请重试',
        icon: 'none'
      })
    }
  },

  // 查看工作统计详情
  viewStatsDetail() {
    wx.navigateTo({
      url: '/pages/worker/stats-detail'
    })
  },

  // 查看徽章详情
  viewBadges() {
    wx.showModal({
      title: '我的徽章',
      content: '您已获得2个徽章：金牌维修师、服务之星',
      showCancel: false
    })
  },

  // 联系客服
  contactService() {
    wx.showActionSheet({
      itemList: ['拨打客服电话', '在线客服'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: '400-123-4567'
          })
        } else if (res.tapIndex === 1) {
          wx.navigateTo({
            url: '/pages/common/ai-chat'
          })
        }
      }
    })
  },

  // 切换工作状态
  async toggleWorkStatus() {
    const currentStatus = this.data.userInfo.workStatus || 'AVAILABLE'
    const newStatus = currentStatus === 'AVAILABLE' ? 'BUSY' : 'AVAILABLE'
    const statusText = newStatus === 'AVAILABLE' ? '空闲' : '忙碌'
    
    wx.showModal({
      title: '切换工作状态',
      content: `确定要切换到${statusText}状态吗？\n\n${newStatus === 'BUSY' ? '忙碌状态下不会收到新工单通知' : '空闲状态可以接收新工单'}`,
      success: async (res) => {
        if (res.confirm) {
          try {
            // 调用专门的工作状态更新API
            const response = await api.updateWorkStatus(newStatus)
            
            if (response.code === 200) {
              // 更新本地状态
              this.setData({
                'userInfo.workStatus': newStatus
              })
              
              // 更新全局数据
              const userInfo = app.globalData.userInfo || {}
              userInfo.workStatus = newStatus
              app.globalData.userInfo = userInfo
              wx.setStorageSync('userInfo', userInfo)
              
              wx.showToast({
                title: `已切换到${statusText}状态`,
                icon: 'success'
              })
            } else {
              throw new Error(response.message || '切换失败')
            }
          } catch (error) {
            console.error('切换工作状态失败:', error)
            wx.showToast({
              title: error.message || '切换失败，请重试',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      confirmColor: '#e53e3e',
      success: (res) => {
        if (res.confirm) {
          app.logout()
        }
      }
    })
  },

  // 分享应用
  onShareAppMessage() {
    return {
      title: '智慧物业 - 让物业管理更智能',
      path: '/pages/login/login',
      imageUrl: '/static/images/share-bg.png'
    }
  }
})
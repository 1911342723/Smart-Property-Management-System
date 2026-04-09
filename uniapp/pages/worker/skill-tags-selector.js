// pages/worker/skill-tags-selector.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    availableTags: [], // 所有可用的技能标签
    selectedTags: [], // 已选择的标签
    categories: [] // 按分类组织的标签
  },

  onLoad(options) {
    console.log('技能标签选择器加载')
    
    // 从上一页传入的已选择标签（以逗号分隔的字符串）
    const selectedTagsStr = options.selected || ''
    const selectedTagNames = selectedTagsStr ? selectedTagsStr.split(',') : []
    
    // 加载所有可用的技能标签
    this.loadAvailableTags(selectedTagNames)
  },

  // 加载可用的技能标签
  async loadAvailableTags(selectedTagNames) {
    try {
      wx.showLoading({
        title: '加载中...'
      })

      // 从服务器获取技能标签
      const response = await api.getAllActiveSkills()
      
      if (response.code === 200 && response.data) {
        const activeTags = response.data
        
        // 按分类组织标签
        const categoriesMap = {}
        activeTags.forEach(tag => {
          if (!categoriesMap[tag.category]) {
            categoriesMap[tag.category] = {
              category: tag.category,
              tags: []
            }
          }
          categoriesMap[tag.category].tags.push(tag)
        })

        const categories = Object.values(categoriesMap)

        // 找出已选择的标签
        const selectedTags = activeTags.filter(tag => 
          selectedTagNames.includes(tag.name)
        )

        this.setData({
          availableTags: activeTags,
          categories: categories,
          selectedTags: selectedTags
        })
      } else {
        throw new Error(response.message || '加载技能标签失败')
      }

      wx.hideLoading()
    } catch (error) {
      console.error('加载技能标签失败:', error)
      wx.hideLoading()
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  // 切换标签选择状态
  toggleTag(e) {
    const tag = e.currentTarget.dataset.tag
    const { selectedTags } = this.data

    const index = selectedTags.findIndex(t => t.id === tag.id)
    
    if (index > -1) {
      // 已选择，移除
      selectedTags.splice(index, 1)
    } else {
      // 未选择，添加
      selectedTags.push(tag)
    }

    this.setData({
      selectedTags: [...selectedTags]
    })
  },

  // 移除标签
  removeTag(e) {
    const tagId = e.currentTarget.dataset.id
    const { selectedTags } = this.data

    const newSelectedTags = selectedTags.filter(t => t.id !== tagId)
    
    this.setData({
      selectedTags: newSelectedTags
    })
  },

  // 清空所有选择
  clearAll() {
    wx.showModal({
      title: '确认清空',
      content: '确定要清空所有已选择的技能标签吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({
            selectedTags: []
          })
        }
      }
    })
  },

  // 判断标签是否已选择
  isTagSelected(tagId) {
    return this.data.selectedTags.some(t => t.id === tagId)
  },

  // 保存技能标签
  async saveSkillTags() {
    const { selectedTags } = this.data

    if (selectedTags.length === 0) {
      wx.showToast({
        title: '请至少选择一个技能',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '保存中...'
    })

    try {
      const tagIds = selectedTags.map(t => t.id)

      // 批量添加技能（后端会先删除原有技能，再添加新技能）
      const response = await api.addSkillsBatch(tagIds)

      if (response.code === 200) {
        wx.hideLoading()
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })

        // 延迟返回，让用户看到成功提示
        setTimeout(() => {
          wx.navigateBack()
        }, 1000)
      } else {
        throw new Error(response.message || '保存失败')
      }
    } catch (error) {
      console.error('保存技能标签失败:', error)
      wx.hideLoading()
      wx.showToast({
        title: error.message || '保存失败，请重试',
        icon: 'none'
      })
    }
  },

  // 返回
  goBack() {
    wx.navigateBack()
  }
})

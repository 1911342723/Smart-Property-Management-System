// pages/test/repair-form-test.js
// 用于测试报修表单页面的加载和功能

Page({
  data: {
    testResult: '',
    isLoading: false
  },

  onLoad() {
    console.log('报修表单测试页面加载')
  },

  // 测试导航到报修表单
  testNavigateToForm() {
    this.setData({ isLoading: true, testResult: '正在测试...' })
    
    try {
      wx.navigateTo({
        url: '/pages/owner/repair-form?category=1&quick=true',
        success: () => {
          this.setData({ 
            testResult: '✅ 导航成功，页面应该正常加载',
            isLoading: false 
          })
        },
        fail: (error) => {
          this.setData({ 
            testResult: `❌ 导航失败: ${error.errMsg}`,
            isLoading: false 
          })
        }
      })
    } catch (error) {
      this.setData({ 
        testResult: `❌ 测试出错: ${error.message}`,
        isLoading: false 
      })
    }
  },

  // 测试普通报修
  testNormalForm() {
    this.setData({ isLoading: true, testResult: '正在测试...' })
    
    wx.navigateTo({
      url: '/pages/owner/repair-form?category=2&quick=false',
      success: () => {
        this.setData({ 
          testResult: '✅ 普通报修页面加载成功',
          isLoading: false 
        })
      },
      fail: (error) => {
        this.setData({ 
          testResult: `❌ 普通报修加载失败: ${error.errMsg}`,
          isLoading: false 
        })
      }
    })
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  }
})







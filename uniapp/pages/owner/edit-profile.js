// pages/owner/edit-profile.js - 修复版本
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: {},
    profileForm: {
      realName: '',
      phone: '',
      email: '',
      gender: '',
      birthday: '',
      signature: '',
      emergencyContact: '',
      emergencyPhone: ''
    },
    errors: {},
    showAvatarModal: false,
    savingProfile: false
  },

  onLoad() {
    console.log('编辑资料页面 onLoad')
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    try {
      // 优先从全局数据获取
      let userInfo = app.globalData.userInfo || {}
      
      // 如果全局数据为空，尝试从本地存储获取
      if (!userInfo.id) {
        userInfo = wx.getStorageSync('userInfo') || {}
      }
      
      console.log('编辑资料页面加载用户信息:', userInfo)
      
      // 如果仍然没有用户信息，提示用户重新登录
      if (!userInfo.id) {
        wx.showModal({
          title: '提示',
          content: '用户信息缺失，请重新登录',
          showCancel: false,
          success: () => {
            wx.reLaunch({
              url: '/pages/login/login'
            })
          }
        })
        return
      }
      
      this.setData({
        userInfo: userInfo,
        profileForm: {
          realName: userInfo.realName || '',
          phone: userInfo.phone || '',
          email: userInfo.email || '',
          gender: userInfo.gender || '',
          birthday: userInfo.birthday || '',
          signature: userInfo.signature || '',
          emergencyContact: userInfo.emergencyContact || '',
          emergencyPhone: userInfo.emergencyPhone || ''
        }
      })
    } catch (error) {
      console.error('加载用户信息失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 表单输入事件
  onRealNameInput: function(e) {
    this.setData({
      'profileForm.realName': e.detail.value
    })
    this.clearError('realName')
  },

  onPhoneInput: function(e) {
    this.setData({
      'profileForm.phone': e.detail.value
    })
    this.clearError('phone')
  },

  onEmailInput: function(e) {
    this.setData({
      'profileForm.email': e.detail.value
    })
    this.clearError('email')
  },

  onSignatureInput: function(e) {
    this.setData({
      'profileForm.signature': e.detail.value
    })
  },

  onEmergencyContactInput: function(e) {
    this.setData({
      'profileForm.emergencyContact': e.detail.value
    })
  },

  onEmergencyPhoneInput: function(e) {
    this.setData({
      'profileForm.emergencyPhone': e.detail.value
    })
    this.clearError('emergencyPhone')
  },

  // 选择性别
  selectGender: function(e) {
    const gender = e.currentTarget.dataset.gender
    this.setData({
      'profileForm.gender': gender
    })
  },

  // 选择生日
  onBirthdayChange: function(e) {
    this.setData({
      'profileForm.birthday': e.detail.value
    })
  },

  // 清除错误信息
  clearError: function(field) {
    const errors = Object.assign({}, this.data.errors)
    delete errors[field]
    this.setData({ errors: errors })
  },

  // 验证表单
  validateForm: function() {
    const form = this.data.profileForm
    const errors = {}

    // 真实姓名必填
    if (!form.realName.trim()) {
      errors.realName = '请输入真实姓名'
    } else if (form.realName.trim().length < 2) {
      errors.realName = '姓名至少需要2个字符'
    }

    // 手机号验证
    if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
      errors.phone = '请输入正确的手机号'
    }

    // 邮箱验证
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      errors.email = '请输入正确的邮箱地址'
    }

    // 紧急联系电话验证
    if (form.emergencyPhone && !/^1[3-9]\d{9}$/.test(form.emergencyPhone)) {
      errors.emergencyPhone = '请输入正确的紧急联系电话'
    }

    this.setData({ errors: errors })
    return Object.keys(errors).length === 0
  },

  // 保存个人资料
  saveProfile: function() {
    console.log('=== saveProfile 被调用 ===')
    
    // 表单验证
    if (!this.validateForm()) {
      console.log('表单验证失败')
      return
    }
    
    const form = this.data.profileForm
    const self = this
    console.log('准备保存的表单数据:', form)
    
    self.setData({ savingProfile: true })
    
    const requestData = {
      realName: form.realName,
      phone: form.phone,
      email: form.email,
      gender: form.gender,
      birthday: form.birthday,
      signature: form.signature,
      emergencyContact: form.emergencyContact,
      emergencyPhone: form.emergencyPhone
    }
    
    console.log('发送到后端的数据:', requestData)
    
    // 调用API更新用户信息
    api.updateProfile(requestData).then(function(response) {
      console.log('API updateProfile 响应:', response)
      
      if (response.code === 200) {
        console.log('保存成功，更新本地数据')
        // 更新本地数据
        const updatedUserInfo = Object.assign({}, self.data.userInfo, form)
        
        // 更新全局数据
        app.globalData.userInfo = updatedUserInfo
        wx.setStorageSync('userInfo', updatedUserInfo)
        
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })
        
        // 延迟返回上一页
        setTimeout(function() {
          wx.navigateBack()
        }, 1500)
      } else {
        throw new Error(response.message || '保存失败')
      }
    }).catch(function(error) {
      console.error('保存资料失败:', error)
      wx.showToast({
        title: '保存失败：' + (error.message || '网络错误'),
        icon: 'none'
      })
    }).finally(function() {
      console.log('保存操作完成，重置状态')
      self.setData({ savingProfile: false })
    })
  },

  // 编辑头像
  editAvatar: function() {
    this.setData({
      showAvatarModal: true
    })
  },

  // 关闭头像弹窗
  closeAvatarModal: function() {
    this.setData({
      showAvatarModal: false
    })
  }
})

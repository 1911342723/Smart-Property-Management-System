// 个人资料编辑页面逻辑
Page({
  data: {
    // 用户信息
    userInfo: {},
    
    // 表单数据
    profileForm: {
      name: '',
      signature: '',
      phone: '',
      emergencyContact: '',
      emergencyPhone: ''
    },
    
    // 头像选择弹窗
    showAvatarModal: false,
    savingProfile: false
  },

  onLoad() {
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {
      name: '张三',
      workNumber: 'G001',
      department: '物业管理部',
      avatar: '/static/images/default-avatar.png',
      signature: '',
      phone: '',
      emergencyContact: '',
      emergencyPhone: ''
    }
    
    this.setData({
      userInfo: userInfo,
      profileForm: {
        name: userInfo.name || '',
        signature: userInfo.signature || '',
        phone: userInfo.phone || '',
        emergencyContact: userInfo.emergencyContact || '',
        emergencyPhone: userInfo.emergencyPhone || ''
      }
    })
  },

  // 表单输入事件
  onNameInput(e) {
    this.setData({
      'profileForm.name': e.detail.value
    })
  },

  onSignatureInput(e) {
    this.setData({
      'profileForm.signature': e.detail.value
    })
  },

  onPhoneInput(e) {
    this.setData({
      'profileForm.phone': e.detail.value
    })
  },

  onEmergencyContactInput(e) {
    this.setData({
      'profileForm.emergencyContact': e.detail.value
    })
  },

  onEmergencyPhoneInput(e) {
    this.setData({
      'profileForm.emergencyPhone': e.detail.value
    })
  },

  // 保存个人资料
  saveProfile() {
    const form = this.data.profileForm
    
    // 表单验证
    if (!form.name.trim()) {
      wx.showToast({ title: '请输入姓名', icon: 'none' })
      return
    }
    
    if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    
    if (form.emergencyPhone && !/^1[3-9]\d{9}$/.test(form.emergencyPhone)) {
      wx.showToast({ title: '请输入正确的紧急联系电话', icon: 'none' })
      return
    }
    
    this.setData({ savingProfile: true })
    
    // 模拟保存
    setTimeout(() => {
      const updatedUserInfo = {
        ...this.data.userInfo,
        name: form.name,
        signature: form.signature,
        phone: form.phone,
        emergencyContact: form.emergencyContact,
        emergencyPhone: form.emergencyPhone
      }
      
      this.setData({
        userInfo: updatedUserInfo,
        savingProfile: false
      })
      
      // 保存到本地存储
      wx.setStorageSync('userInfo', updatedUserInfo)
      
      wx.showToast({
        title: '保存成功',
        icon: 'success'
      })
      
      // 延迟返回上一页
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }, 1500)
  },

  // 编辑头像
  editAvatar() {
    this.setData({
      showAvatarModal: true
    })
  },

  // 关闭头像弹窗
  closeAvatarModal() {
    this.setData({
      showAvatarModal: false
    })
  },

  // 从相册选择头像
  selectAvatarFromAlbum() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album'],
      success: (res) => {
        this.updateAvatar(res.tempFilePaths[0])
      },
      fail: (err) => {
        console.error('选择图片失败:', err)
        wx.showToast({
          title: '选择图片失败',
          icon: 'none'
        })
      }
    })
  },

  // 拍照
  takePhoto() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['camera'],
      success: (res) => {
        this.updateAvatar(res.tempFilePaths[0])
      },
      fail: (err) => {
        console.error('拍照失败:', err)
        wx.showToast({
          title: '拍照失败',
          icon: 'none'
        })
      }
    })
  },

  // 移除头像
  removeAvatar() {
    wx.showModal({
      title: '确认移除',
      content: '确定要移除当前头像吗？',
      success: (res) => {
        if (res.confirm) {
          this.updateAvatar('/static/images/default-avatar.png')
        }
      }
    })
  },

  // 更新头像
  updateAvatar(avatarPath) {
    const updatedUserInfo = {
      ...this.data.userInfo,
      avatar: avatarPath
    }
    
    this.setData({
      userInfo: updatedUserInfo,
      showAvatarModal: false
    })
    
    // 保存到本地存储
    wx.setStorageSync('userInfo', updatedUserInfo)
    
    wx.showToast({
      title: '头像更新成功',
      icon: 'success'
    })
  }
})






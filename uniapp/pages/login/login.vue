<template>
  <view class="login-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="decoration-circle circle-1"></view>
      <view class="decoration-circle circle-2"></view>
      <view class="decoration-circle circle-3"></view>
    </view>
    
    <!-- 顶部Logo区域 -->
    <view class="logo-section">
      <image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
      <text class="app-name">智慧物业</text>
      <text class="app-desc">让物业管理更智能</text>
    </view>
    
    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">📱</text>
          <input 
            class="input-field" 
            type="text" 
            placeholder="请输入手机号"
            v-model="loginForm.phone"
            maxlength="11"
          />
        </view>
      </view>
      
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">🔒</text>
          <input 
            class="input-field" 
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            v-model="loginForm.password"
          />
          <text class="password-toggle" @tap="togglePassword">
            {{ showPassword ? '👁️' : '👁️‍🗨️' }}
          </text>
        </view>
      </view>
      
      <!-- 角色选择 -->
      <view class="role-selector">
        <text class="selector-label">选择身份：</text>
        <view class="role-options">
          <view 
            class="role-option"
            :class="{ active: loginForm.role === role.value }"
            v-for="role in roleOptions"
            :key="role.value"
            @tap="selectRole(role.value)"
          >
            <text class="role-icon">{{ role.icon }}</text>
            <text class="role-text">{{ role.label }}</text>
          </view>
        </view>
      </view>
      
      <!-- 登录按钮 -->
      <button 
        class="login-btn"
        :class="{ disabled: !canLogin }"
        :disabled="!canLogin"
        @tap="handleLogin"
      >
        <text class="btn-text">{{ loading ? '登录中...' : '登录' }}</text>
      </button>
      
      <!-- 快捷操作 -->
      <view class="quick-actions">
        <text class="action-link" @tap="showDemoLogin">演示账号</text>
        <text class="action-divider">|</text>
        <text class="action-link" @tap="showRegister">注册账号</text>
        <text class="action-divider">|</text>
        <text class="action-link" @tap="showForgotPassword">忘记密码</text>
      </view>
    </view>
    
    <!-- 演示账号弹窗 -->
    <view class="demo-modal" v-if="showDemoModal" @tap="closeDemoModal">
      <view class="modal-content" @tap.stop>
        <view class="modal-header">
          <text class="modal-title">演示账号</text>
          <text class="modal-close" @tap="closeDemoModal">✕</text>
        </view>
        <view class="modal-body">
          <view 
            class="demo-account"
            v-for="account in demoAccounts"
            :key="account.role"
            @tap="useDemoAccount(account)"
          >
            <view class="account-info">
              <text class="account-role">{{ account.roleText }}</text>
              <text class="account-phone">{{ account.phone }}</text>
            </view>
            <text class="account-arrow">→</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { showToast, validatePhone } from '@/utils/common'

const userStore = useUserStore()

// 表单数据
const loginForm = ref({
  phone: '',
  password: ''
})

const showPassword = ref(false)
const loading = ref(false)
const showDemoModal = ref(false)

// 角色选项
const roleOptions = [
  { value: 'owner', label: '业主', icon: '🏠' },
  { value: 'worker', label: '维修工', icon: '🔧' },
  { value: 'guard', label: '保安', icon: '🛡️' }
]

// 演示账号
const demoAccounts = [
  { name: '张三(业主)', phone: '13800000101', password: '123456' },
  { name: '老张(维修工)', phone: '13800000301', password: '123456' },
  { name: '小刘(保安)', phone: '13800000201', password: '123456' }
]

// 计算属性
const canLogin = computed(() => {
  return loginForm.value.phone && 
         loginForm.value.password && 
         validatePhone(loginForm.value.phone)
})

// 方法
const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const selectRole = (role) => {
  loginForm.value.role = role
}

const handleLogin = async () => {
  if (!canLogin.value || loading.value) return
  
  if (!validatePhone(loginForm.value.phone)) {
    showToast('请输入正确的手机号')
    return
  }
  
  loading.value = true
  
  try {
    const result = await userStore.login({
      phone: loginForm.value.phone,
      password: loginForm.value.password,
      role: loginForm.value.role
    })
    
    if (result.success) {
      showToast('登录成功', 'success')
      
      // 根据角色跳转到对应页面
      const rolePages = {
        owner: '/pages/owner/home',
        worker: '/pages/worker/dashboard', 
        guard: '/pages/guard/tasks'
      }
      
      setTimeout(() => {
        uni.switchTab({
          url: rolePages[loginForm.value.role] || '/pages/owner/home'
        })
      }, 1000)
    } else {
      showToast(result.error || '登录失败')
    }
  } catch (error) {
    console.error('Login error:', error)
    showToast('登录失败，请重试')
  } finally {
    loading.value = false
  }
}

const showDemoLogin = () => {
  showDemoModal.value = true
}

const closeDemoModal = () => {
  showDemoModal.value = false
}

const useDemoAccount = (account) => {
  loginForm.value.phone = account.phone
  loginForm.value.password = account.password
  loginForm.value.role = account.role
  closeDemoModal()
  showToast('已填入演示账号')
}

const showRegister = () => {
  showToast('注册功能开发中')
}

const showForgotPassword = () => {
  showToast('找回密码功能开发中')
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background: $gradient-primary;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 30px 30px;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  
  .decoration-circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    
    &.circle-1 {
      width: 200px;
      height: 200px;
      top: -100px;
      right: -50px;
      animation: float 6s ease-in-out infinite;
    }
    
    &.circle-2 {
      width: 150px;
      height: 150px;
      bottom: 100px;
      left: -75px;
      animation: float 8s ease-in-out infinite reverse;
    }
    
    &.circle-3 {
      width: 100px;
      height: 100px;
      top: 200px;
      left: 50%;
      transform: translateX(-50%);
      animation: float 10s ease-in-out infinite;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
  
  .logo {
    width: 80px;
    height: 80px;
    margin-bottom: 20px;
  }
  
  .app-name {
    display: block;
    font-size: 32px;
    font-weight: 700;
    color: $text-white;
    margin-bottom: 8px;
  }
  
  .app-desc {
    display: block;
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-form {
  width: 100%;
  max-width: 320px;
  
  .form-item {
    margin-bottom: 24px;
  }
  
  .input-wrapper {
    position: relative;
    background: rgba(255, 255, 255, 0.1);
    border-radius: $radius-lg;
    border: 1px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    display: flex;
    align-items: center;
    padding: 0 16px;
    height: 50px;
    transition: all $transition-normal;
    
    &:focus-within {
      border-color: rgba(255, 255, 255, 0.5);
      background: rgba(255, 255, 255, 0.15);
    }
  }
  
  .input-icon {
    font-size: 18px;
    margin-right: 12px;
    opacity: 0.8;
  }
  
  .input-field {
    flex: 1;
    background: transparent;
    border: none;
    color: $text-white;
    font-size: 16px;
    
    &::placeholder {
      color: rgba(255, 255, 255, 0.6);
    }
  }
  
  .password-toggle {
    font-size: 18px;
    opacity: 0.8;
    cursor: pointer;
    padding: 4px;
  }
}

.role-selector {
  margin-bottom: 32px;
  
  .selector-label {
    display: block;
    color: rgba(255, 255, 255, 0.9);
    font-size: 16px;
    margin-bottom: 12px;
  }
  
  .role-options {
    display: flex;
    gap: 12px;
  }
  
  .role-option {
    flex: 1;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: $radius-lg;
    padding: 12px 8px;
    text-align: center;
    transition: all $transition-normal;
    cursor: pointer;
    
    &.active {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.5);
      transform: scale(1.05);
    }
    
    .role-icon {
      display: block;
      font-size: 20px;
      margin-bottom: 4px;
    }
    
    .role-text {
      display: block;
      color: $text-white;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

.login-btn {
  width: 100%;
  height: 50px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  transition: all $transition-normal;
  
  &:not(.disabled):active {
    transform: scale(0.98);
  }
  
  &.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  .btn-text {
    font-size: 16px;
    font-weight: 600;
    color: $primary-color;
  }
}

.quick-actions {
  text-align: center;
  
  .action-link {
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    cursor: pointer;
    
    &:active {
      opacity: 0.7;
    }
  }
  
  .action-divider {
    color: rgba(255, 255, 255, 0.5);
    margin: 0 16px;
  }
}

.demo-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  
  .modal-content {
    background: $bg-card;
    border-radius: $radius-xl;
    width: 100%;
    max-width: 300px;
    max-height: 80vh;
    overflow: hidden;
  }
  
  .modal-header {
    @include flex-between;
    padding: 20px;
    border-bottom: 1px solid $border-color;
    
    .modal-title {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
    }
    
    .modal-close {
      font-size: 20px;
      color: $text-secondary;
      cursor: pointer;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: $radius-sm;
      
      &:active {
        background: $bg-hover;
      }
    }
  }
  
  .modal-body {
    padding: 0;
  }
  
  .demo-account {
    @include flex-between;
    padding: 16px 20px;
    border-bottom: 1px solid $border-color;
    cursor: pointer;
    transition: background-color $transition-fast;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:active {
      background: $bg-hover;
    }
    
    .account-info {
      .account-role {
        display: block;
        color: $text-primary;
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 4px;
      }
      
      .account-phone {
        display: block;
        color: $text-secondary;
        font-size: 14px;
      }
    }
    
    .account-arrow {
      color: $text-muted;
      font-size: 18px;
    }
  }
}
</style>

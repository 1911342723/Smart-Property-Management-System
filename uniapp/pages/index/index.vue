<template>
  <view class="index-container">
    <!-- 启动页内容 -->
    <view class="splash-content">
      <!-- Logo动画 -->
      <view class="logo-animation">
        <image class="app-logo" src="/static/images/logo.png" mode="aspectFit"></image>
        <view class="logo-glow"></view>
      </view>
      
      <!-- 应用信息 -->
      <view class="app-info">
        <text class="app-name">智慧物业</text>
        <text class="app-slogan">让物业管理更智能</text>
        <text class="app-version">v1.0.0</text>
      </view>
      
      <!-- 加载动画 -->
      <view class="loading-section">
        <view class="loading-dots">
          <view class="dot dot1"></view>
          <view class="dot dot2"></view>
          <view class="dot dot3"></view>
        </view>
        <text class="loading-text">{{ loadingText }}</text>
      </view>
    </view>
    
    <!-- 背景装饰 -->
    <view class="bg-particles">
      <view class="particle" v-for="i in 20" :key="i" :style="getParticleStyle(i)"></view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loadingText = ref('正在加载...')

const loadingTexts = [
  '正在加载...',
  '初始化应用...',
  '检查更新...',
  '准备就绪...'
]

let textIndex = 0

onMounted(() => {
  // 启动页加载动画
  startLoadingAnimation()
  
  // 检查登录状态并跳转
  setTimeout(() => {
    checkAndNavigate()
  }, 3000)
})

const startLoadingAnimation = () => {
  const interval = setInterval(() => {
    textIndex = (textIndex + 1) % loadingTexts.length
    loadingText.value = loadingTexts[textIndex]
  }, 800)
  
  // 3秒后停止动画
  setTimeout(() => {
    clearInterval(interval)
  }, 3000)
}

const checkAndNavigate = () => {
  const token = uni.getStorageSync('token')
  const userInfo = uni.getStorageSync('userInfo')
  
  if (token && userInfo) {
    // 已登录，设置用户信息并跳转到对应首页
    userStore.setToken(token)
    userStore.setUserInfo(userInfo)
    
    const rolePages = {
      owner: '/pages/owner/home',
      worker: '/pages/worker/dashboard',
      guard: '/pages/guard/tasks'
    }
    
    const targetPage = rolePages[userInfo.role] || '/pages/owner/home'
    
    uni.reLaunch({
      url: targetPage
    })
  } else {
    // 未登录，跳转到登录页
    uni.reLaunch({
      url: '/pages/login/login'
    })
  }
}

const getParticleStyle = (index) => {
  const size = Math.random() * 4 + 2
  const left = Math.random() * 100
  const animationDelay = Math.random() * 3
  const animationDuration = Math.random() * 3 + 2
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${animationDelay}s`,
    animationDuration: `${animationDuration}s`
  }
}
</script>

<style lang="scss" scoped>
.index-container {
  width: 100vw;
  height: 100vh;
  background: $gradient-primary;
  position: relative;
  overflow: hidden;
  @include flex-center;
  flex-direction: column;
}

.splash-content {
  @include flex-center;
  flex-direction: column;
  z-index: 10;
}

.logo-animation {
  position: relative;
  margin-bottom: 60px;
  
  .app-logo {
    width: 120px;
    height: 120px;
    animation: logoFloat 3s ease-in-out infinite;
  }
  
  .logo-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 140px;
    height: 140px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    animation: glow 2s ease-in-out infinite alternate;
  }
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes glow {
  0% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  100% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

.app-info {
  text-align: center;
  margin-bottom: 80px;
  
  .app-name {
    display: block;
    color: $text-white;
    font-size: 36px;
    font-weight: 700;
    margin-bottom: 12px;
    letter-spacing: 2px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }
  
  .app-slogan {
    display: block;
    color: rgba(255, 255, 255, 0.9);
    font-size: 16px;
    font-weight: 300;
    margin-bottom: 20px;
    letter-spacing: 1px;
  }
  
  .app-version {
    display: block;
    color: rgba(255, 255, 255, 0.7);
    font-size: 12px;
  }
}

.loading-section {
  @include flex-center;
  flex-direction: column;
  
  .loading-dots {
    display: flex;
    gap: 8px;
    margin-bottom: 20px;
    
    .dot {
      width: 8px;
      height: 8px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 50%;
      animation: dotBounce 1.4s ease-in-out infinite both;
      
      &.dot1 {
        animation-delay: -0.32s;
      }
      
      &.dot2 {
        animation-delay: -0.16s;
      }
      
      &.dot3 {
        animation-delay: 0s;
      }
    }
  }
  
  .loading-text {
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    font-weight: 300;
    animation: textFade 1s ease-in-out infinite alternate;
  }
}

@keyframes dotBounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes textFade {
  0% {
    opacity: 0.6;
  }
  100% {
    opacity: 1;
  }
}

.bg-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  
  .particle {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    animation: particleFloat linear infinite;
  }
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-10vh) rotate(360deg);
    opacity: 0;
  }
}

/* 响应式适配 */
@media screen and (max-width: 375px) {
  .app-info {
    .app-name {
      font-size: 32px;
    }
    
    .app-slogan {
      font-size: 14px;
    }
  }
  
  .logo-animation {
    .app-logo {
      width: 100px;
      height: 100px;
    }
    
    .logo-glow {
      width: 120px;
      height: 120px;
    }
  }
}
</style>









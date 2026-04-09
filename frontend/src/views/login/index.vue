<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <div class="login-form-wrapper">
      <div class="login-form">
        <div class="login-header">
          <div class="logo">
            <div class="logo-icon">
              <el-icon><House /></el-icon>
            </div>
            <h1 class="logo-text">物业管理系统</h1>
          </div>
          <p class="login-subtitle">智慧物业，精细管理</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form-content"
          auto-complete="on"
          label-position="left"
        >
          <el-form-item prop="username">
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="用户名"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="密码"
              name="password"
              tabindex="2"
              auto-complete="on"
              size="large"
              prefix-icon="Lock"
              @keyup.enter="handleLogin"
            >
              <template #suffix>
                <el-icon class="show-pwd" @click="showPwd">
                  <View v-if="passwordType === 'password'" />
                  <Hide v-else />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>

          <el-button
            :loading="loading"
            type="primary"
            size="large"
            style="width: 100%; margin-bottom: 30px;"
            @click.prevent="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form>
        
        <div class="login-footer">
          <p class="demo-tips">演示账号：13800000001 / 123456</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, nextTick, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, User, Lock, View, Hide } from '@element-plus/icons-vue'

export default {
  name: 'Login',
  components: {
    House,
    User,
    Lock,
    View,
    Hide
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    
    const loginFormRef = ref(null)
    const username = ref(null)
    const password = ref(null)
    
    const loading = ref(false)
    const passwordType = ref('password')
    const redirect = ref(undefined)
    
    const loginForm = reactive({
      username: 'admin',
      password: '123456',
      rememberMe: false
    })
    
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else {
        callback()
      }
    }
    
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能少于6位'))
      } else {
        callback()
      }
    }
    
    const loginRules = {
      username: [{ required: true, trigger: 'blur', validator: validateUsername }],
      password: [{ required: true, trigger: 'blur', validator: validatePassword }]
    }
    
    const showPwd = () => {
      if (passwordType.value === 'password') {
        passwordType.value = ''
      } else {
        passwordType.value = 'password'
      }
      nextTick(() => {
        password.value.focus()
      })
    }
    
    const handleLogin = () => {
      loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            await store.dispatch('user/login', loginForm)
            ElMessage.success('登录成功')
            router.push({ path: redirect.value || '/' })
          } catch (error) {
            ElMessage.error('登录失败：' + (error.message || '网络错误'))
          } finally {
            loading.value = false
          }
        } else {
          ElMessage.error('请检查输入信息')
          return false
        }
      })
    }
    
    const getOtherQuery = (query) => {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    }
    
    onMounted(() => {
      if (loginForm.username === '') {
        username.value.focus()
      } else if (loginForm.password === '') {
        password.value.focus()
      }
    })
    
    // 监听路由变化
    const watchRoute = (route) => {
      const query = route.query
      if (query) {
        redirect.value = query.redirect
        loginForm.username = query.username || loginForm.username
        const otherQuery = getOtherQuery(query)
        if (Object.keys(otherQuery).length > 0) {
          redirect.value = redirect.value + '?' + new URLSearchParams(otherQuery).toString()
        }
      }
    }
    
    watchRoute(route)
    
    return {
      loginFormRef,
      username,
      password,
      loading,
      passwordType,
      loginForm,
      loginRules,
      showPwd,
      handleLogin
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.login-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .bg-shapes {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    
    .shape {
      position: absolute;
      border-radius: 50%;
      opacity: 0.1;
      animation: float 6s ease-in-out infinite;
      
      &.shape-1 {
        width: 300px;
        height: 300px;
        background: rgba(255, 255, 255, 0.1);
        top: 10%;
        left: 10%;
        animation-delay: 0s;
      }
      
      &.shape-2 {
        width: 200px;
        height: 200px;
        background: rgba(255, 255, 255, 0.05);
        top: 60%;
        right: 20%;
        animation-delay: 2s;
      }
      
      &.shape-3 {
        width: 150px;
        height: 150px;
        background: rgba(255, 255, 255, 0.08);
        bottom: 20%;
        left: 30%;
        animation-delay: 4s;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-form-wrapper {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 480px;
  padding: 0 20px;
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(220, 223, 230, 0.5);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 16px;
    
    .logo-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, $primary-color, $primary-active);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 24px;
    }
    
    .logo-text {
      font-size: 28px;
      font-weight: 700;
      color: $text-primary;
      margin: 0;
    }
  }
  
  .login-subtitle {
    color: $text-secondary;
    font-size: 16px;
    margin: 0;
  }
}

.login-form-content {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .el-input {
    .el-input__wrapper {
      background-color: #f5f7fa !important;
      border: 1px solid #EBEEF5 !important;
      border-radius: 12px !important;
      box-shadow: none !important;
      
      &:hover {
        border-color: $primary-color !important;
      }
      
      &.is-focus {
        border-color: $primary-color !important;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
      }
    }
    
    .el-input__inner {
      color: #303133 !important;
      
      &::placeholder {
        color: #C0C4CC !important;
      }
    }
    
    .el-input__prefix,
    .el-input__suffix {
      color: #909399 !important;
    }
  }
  
  .show-pwd {
    cursor: pointer;
    user-select: none;
    transition: color 0.2s;
    
    &:hover {
      color: $primary-color !important;
    }
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  
  .el-checkbox {
    .el-checkbox__label {
      color: #606266 !important;
    }
  }
  
  .forgot-password {
    color: $primary-color;
    text-decoration: none;
    font-size: 14px;
    transition: color 0.2s;
    
    &:hover {
      color: $primary-hover;
    }
  }
}

.el-button--primary {
  background: linear-gradient(135deg, $primary-color, $primary-active) !important;
  border: none !important;
  border-radius: 12px !important;
  height: 48px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  
  &:hover {
    background: linear-gradient(135deg, $primary-hover, $primary-color) !important;
    transform: translateY(-2px) !important;
    box-shadow: 0 8px 25px rgba(88, 101, 242, 0.3) !important;
  }
  
  &:active {
    transform: translateY(0) !important;
  }
}

.login-footer {
  text-align: center;
  
  .demo-tips {
    color: #606266;
    font-size: 14px;
    margin: 0;
    padding: 16px;
    background: #ecf5ff;
    border-radius: 8px;
    border: 1px solid #d9ecff;
  }
}

@media (max-width: 480px) {
  .login-form {
    padding: 32px 24px;
    margin: 0 16px;
  }
  
  .login-header .logo .logo-text {
    font-size: 24px;
  }
}
</style>

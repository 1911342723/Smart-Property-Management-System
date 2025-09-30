<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />

    <div class="right-menu">
      <div class="user-info">
        <span class="username">{{ username }}</span>
      </div>
      
      <el-button type="danger" plain @click="logout" class="logout-btn">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'

export default {
  name: 'Navbar',
  components: {
    Breadcrumb,
    Hamburger,
    SwitchButton
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const sidebar = computed(() => store.state.app.sidebar)
    const device = computed(() => store.state.app.device)
    const username = computed(() => store.state.user.userInfo.username || '管理员')
    
    const toggleSideBar = () => {
      store.dispatch('app/toggleSideBar')
    }
    
    const logout = async () => {
      await store.dispatch('user/logout')
      router.push('/login')
      ElMessage.success('退出登录成功')
    }
    
    return {
      sidebar,
      device,
      username,
      toggleSideBar,
      logout
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
@import '@/styles/responsive.scss';

.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: $bg-secondary;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  border-bottom: 1px solid $border-color;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
    margin-left: 16px;
  }

  .right-menu {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-info {
      display: flex;
      align-items: center;
      
      .username {
        font-size: 14px;
        color: $text-primary;
        font-weight: 500;
      }
    }

    .logout-btn {
      height: 36px;
      padding: 0 20px;
      
      .el-icon {
        margin-right: 4px;
      }
    }
  }
}

// 平板适配
@include tablet {
  .navbar {
    padding: 0 12px;
    
    .breadcrumb-container {
      margin-left: 12px;
    }
  }
}

// 手机适配
@include mobile {
  .navbar {
    padding: 0 12px;
    height: 50px;
    
    .hamburger-container {
      padding: 0 12px;
      line-height: 50px;
      
      .hamburger {
        display: inline-block;
        vertical-align: middle;
      }
    }
    
    .breadcrumb-container {
      display: none;
    }
    
    .right-menu {
      gap: 8px;
      
      .user-info {
        display: none;
      }
      
      .logout-btn {
        padding: 0 12px;
        height: 32px;
        font-size: 12px;
      }
    }
  }
}
</style>

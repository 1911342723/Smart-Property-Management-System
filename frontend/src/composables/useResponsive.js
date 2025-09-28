// 响应式 Composition API Hook

import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getDeviceType, createResponsiveListener, breakpoints } from '@/utils/responsive'

/**
 * 使用响应式设备检测
 * @returns {Object}
 */
export function useResponsive() {
  const deviceType = ref(getDeviceType())
  const screenWidth = ref(window.innerWidth)
  const screenHeight = ref(window.innerHeight)
  
  let removeListener = null
  
  const updateScreenSize = () => {
    screenWidth.value = window.innerWidth
    screenHeight.value = window.innerHeight
  }
  
  const handleResize = () => {
    updateScreenSize()
    deviceType.value = getDeviceType()
  }
  
  onMounted(() => {
    updateScreenSize()
    deviceType.value = getDeviceType()
    
    // 监听设备类型变化
    removeListener = createResponsiveListener((newDeviceType) => {
      deviceType.value = newDeviceType
    })
    
    // 监听屏幕尺寸变化
    window.addEventListener('resize', handleResize)
  })
  
  onUnmounted(() => {
    if (removeListener) {
      removeListener()
    }
    window.removeEventListener('resize', handleResize)
  })
  
  return {
    deviceType,
    screenWidth,
    screenHeight,
    isMobile: computed(() => deviceType.value === 'mobile'),
    isTablet: computed(() => deviceType.value === 'tablet'),
    isDesktop: computed(() => deviceType.value === 'desktop'),
    breakpoints
  }
}

/**
 * 使用响应式表格配置
 * @returns {Object}
 */
export function useResponsiveTable() {
  const { deviceType } = useResponsive()
  
  const tableConfig = computed(() => {
    const config = {
      size: 'default',
      pageSize: 20,
      showOverflowTooltip: true,
      stripe: true
    }
    
    switch (deviceType.value) {
      case 'mobile':
        config.size = 'small'
        config.pageSize = 10
        config.showOverflowTooltip = true
        break
      case 'tablet':
        config.size = 'default'
        config.pageSize = 15
        break
      default:
        config.size = 'default'
        config.pageSize = 20
    }
    
    return config
  })
  
  return {
    tableConfig,
    deviceType
  }
}

/**
 * 使用响应式布局配置
 * @returns {Object}
 */
export function useResponsiveLayout() {
  const { deviceType } = useResponsive()
  
  const layoutConfig = computed(() => {
    const config = {
      sidebarWidth: '210px',
      sidebarCollapsedWidth: '54px',
      showBreadcrumb: true,
      showHeaderSearch: true,
      showScreenfull: true,
      showSizeSelect: true,
      cardGutter: 20,
      containerPadding: '20px'
    }
    
    switch (deviceType.value) {
      case 'mobile':
        config.sidebarWidth = '210px'
        config.showBreadcrumb = false
        config.showHeaderSearch = false
        config.showScreenfull = false
        config.showSizeSelect = false
        config.cardGutter = 12
        config.containerPadding = '12px'
        break
      case 'tablet':
        config.sidebarWidth = '180px'
        config.sidebarCollapsedWidth = '54px'
        config.showBreadcrumb = true
        config.showHeaderSearch = false
        config.showScreenfull = false
        config.cardGutter = 16
        config.containerPadding = '16px'
        break
    }
    
    return config
  })
  
  return {
    layoutConfig,
    deviceType
  }
}

/**
 * 使用响应式栅格系统
 * @param {Object} options 栅格配置选项
 * @returns {Object}
 */
export function useResponsiveGrid(options = {}) {
  const { deviceType } = useResponsive()
  
  const defaultOptions = {
    xs: 24,  // 手机
    sm: 12,  // 平板
    md: 8,   // 小桌面
    lg: 6,   // 大桌面
    xl: 6    // 超大桌面
  }
  
  const gridOptions = { ...defaultOptions, ...options }
  
  const currentSpan = computed(() => {
    switch (deviceType.value) {
      case 'mobile':
        return gridOptions.xs
      case 'tablet':
        return gridOptions.sm
      default:
        return gridOptions.lg
    }
  })
  
  const gutter = computed(() => {
    switch (deviceType.value) {
      case 'mobile':
        return 12
      case 'tablet':
        return 16
      default:
        return 20
    }
  })
  
  return {
    currentSpan,
    gutter,
    deviceType
  }
}

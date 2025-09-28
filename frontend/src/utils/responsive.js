// 响应式工具函数

/**
 * 获取当前设备类型
 * @returns {string} 设备类型：mobile、tablet、desktop
 */
export function getDeviceType() {
  const width = window.innerWidth
  if (width < 768) {
    return 'mobile'
  } else if (width < 1024) {
    return 'tablet'
  } else {
    return 'desktop'
  }
}

/**
 * 判断是否为移动设备
 * @returns {boolean}
 */
export function isMobile() {
  return getDeviceType() === 'mobile'
}

/**
 * 判断是否为平板设备
 * @returns {boolean}
 */
export function isTablet() {
  return getDeviceType() === 'tablet'
}

/**
 * 判断是否为桌面设备
 * @returns {boolean}
 */
export function isDesktop() {
  return getDeviceType() === 'desktop'
}

/**
 * 响应式断点
 */
export const breakpoints = {
  xs: 480,   // 超小屏幕
  sm: 768,   // 小屏幕（手机）
  md: 1024,  // 中等屏幕（平板）
  lg: 1200,  // 大屏幕（桌面）
  xl: 1920   // 超大屏幕
}

/**
 * 创建响应式监听器
 * @param {Function} callback 回调函数
 * @returns {Function} 清除监听器的函数
 */
export function createResponsiveListener(callback) {
  let currentDeviceType = getDeviceType()
  
  const handleResize = () => {
    const newDeviceType = getDeviceType()
    if (newDeviceType !== currentDeviceType) {
      currentDeviceType = newDeviceType
      callback(newDeviceType)
    }
  }
  
  window.addEventListener('resize', handleResize)
  
  // 返回清除监听器的函数
  return () => {
    window.removeEventListener('resize', handleResize)
  }
}

/**
 * 根据设备类型获取表格分页大小
 * @returns {number}
 */
export function getTablePageSize() {
  const deviceType = getDeviceType()
  switch (deviceType) {
    case 'mobile':
      return 10
    case 'tablet':
      return 15
    default:
      return 20
  }
}

/**
 * 根据设备类型获取卡片列数
 * @returns {number}
 */
export function getCardColumns() {
  const deviceType = getDeviceType()
  switch (deviceType) {
    case 'mobile':
      return 1
    case 'tablet':
      return 2
    default:
      return 4
  }
}

/**
 * 根据设备类型调整Element Plus组件大小
 * @returns {string}
 */
export function getComponentSize() {
  const deviceType = getDeviceType()
  switch (deviceType) {
    case 'mobile':
      return 'small'
    case 'tablet':
      return 'default'
    default:
      return 'default'
  }
}

/**
 * 检查是否支持触摸
 * @returns {boolean}
 */
export function isTouchDevice() {
  return 'ontouchstart' in window || navigator.maxTouchPoints > 0
}

/**
 * 获取安全区域信息（用于适配刘海屏等）
 * @returns {Object}
 */
export function getSafeArea() {
  const computedStyle = getComputedStyle(document.documentElement)
  return {
    top: computedStyle.getPropertyValue('--safe-area-inset-top') || '0px',
    right: computedStyle.getPropertyValue('--safe-area-inset-right') || '0px',
    bottom: computedStyle.getPropertyValue('--safe-area-inset-bottom') || '0px',
    left: computedStyle.getPropertyValue('--safe-area-inset-left') || '0px'
  }
}

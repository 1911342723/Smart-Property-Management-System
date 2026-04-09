/**
 * 应用配置文件
 * 统一管理应用配置信息
 */

// 环境配置
const ENV_CONFIG = {
  // 开发环境
  development: {
    baseURL: 'http://localhost:8081/api',
    debug: true
  },
  // 测试环境
  test: {
    baseURL: 'https://test.your-domain.com/api', // 必须使用HTTPS
    debug: true
  },
  // 生产环境
  production: {
    baseURL: 'https://api.your-domain.com/api', // 必须使用HTTPS
    debug: false
  },
  // 临时开发环境（用于真机调试时）
  dev_remote: {
    baseURL: 'https://your-ngrok-domain.ngrok.io/api', // 使用ngrok或其他内网穿透工具
    debug: true
  }
}

// 当前环境（开发时修改此值）
// development: 本地开发（仅在开发者工具中且关闭域名检查时可用）
// dev_remote: 真机调试时使用（需要配置内网穿透）
// test: 测试环境
// production: 生产环境
const CURRENT_ENV = 'development'

// 获取当前环境配置
const CONFIG = ENV_CONFIG[CURRENT_ENV]

// 应用基础配置
const APP_CONFIG = {
  // 应用信息
  name: '智慧物业',
  version: '1.0.0',
  
  // API配置
  api: {
    baseURL: CONFIG.baseURL,
    timeout: 10000
  },
  
  // 调试配置
  debug: CONFIG.debug,
  
  // 文件上传配置
  upload: {
    maxSize: 10 * 1024 * 1024, // 10MB
    maxCount: 9, // 最大文件数量
    accept: ['image/jpg', 'image/jpeg', 'image/png', 'image/gif']
  },
  
  // 分页配置
  pagination: {
    defaultPageSize: 10,
    maxPageSize: 100
  },
  
  // 缓存配置
  cache: {
    userInfoKey: 'userInfo',
    tokenKey: 'token',
    settingsKey: 'settings'
  },
  
  // 角色配置
  roles: {
    ADMIN: '管理员',
    OWNER: '业主',
    GUARD: '保安',
    WORKER: '维修工'
  },
  
  // 工单状态配置
  workOrderStatus: {
    PENDING: '待处理',
    PROCESSING: '处理中',
    COMPLETED: '已完成',
    CLOSED: '已关闭',
    CANCELLED: '已取消'
  },
  
  // 工单优先级配置
  workOrderPriority: {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
    URGENT: '紧急'
  },
  
  // 账单状态配置
  billStatus: {
    UNPAID: '未缴费',
    PARTIAL: '部分缴费',
    PAID: '已缴费',
    OVERDUE: '逾期'
  },
  
  // 账单类型配置
  billType: {
    PROPERTY: '物业费',
    PARKING: '停车费'
  }
}

// 检查当前是否在微信开发者工具中
function isInDevTools() {
  try {
    return typeof __wxConfig !== 'undefined' && __wxConfig.debug
  } catch (e) {
    return false
  }
}

// 检查是否是合法的域名
function isValidDomain(url) {
  return url.startsWith('https://') || url.startsWith('wss://')
}

// 环境检查和警告
function checkEnvironment() {
  const currentConfig = ENV_CONFIG[CURRENT_ENV]
  const inDevTools = isInDevTools()
  
  if (!isValidDomain(currentConfig.baseURL)) {
    if (inDevTools) {
      console.warn('[CONFIG] 当前使用的是非HTTPS域名，仅在开发者工具中可用。请确保已关闭域名检查。')
      console.warn('[CONFIG] 如需真机调试，请切换到 dev_remote 环境并配置内网穿透工具。')
    } else {
      console.error('[CONFIG] 非法域名！小程序只允许HTTPS域名。当前环境:', CURRENT_ENV)
      console.error('[CONFIG] 请切换到合法环境或配置正确的HTTPS域名。')
      return false
    }
  }
  
  return true
}

// 调试日志
function log(...args) {
  if (APP_CONFIG.debug) {
    console.log('[CONFIG]', ...args)
  }
}

// 错误日志
function error(...args) {
  if (APP_CONFIG.debug) {
    console.error('[CONFIG ERROR]', ...args)
  }
}

// 初始化时检查环境
checkEnvironment()

module.exports = {
  ...APP_CONFIG,
  log,
  error,
  ENV_CONFIG,
  CURRENT_ENV,
  isInDevTools,
  isValidDomain,
  checkEnvironment
}


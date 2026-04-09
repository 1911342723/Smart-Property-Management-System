/**
 * API接口管理
 * 统一管理所有API接口
 */

const { get, post, put, del } = require('./request.js')

// ====================================
// 认证相关API
// ====================================

/**
 * 用户登录
 * @param {Object} data 登录数据
 * @param {string} data.username 用户名
 * @param {string} data.password 密码
 * @param {string} data.userType 用户类型
 */
function login(data) {
  return post('/auth/login', data)
}

/**
 * 用户注册
 * @param {Object} data 注册数据
 */
function register(data) {
  return post('/auth/register', data)
}

/**
 * 忘记密码
 * @param {Object} data 重置密码数据
 * @param {string} data.phone 手机号
 * @param {string} data.newPassword 新密码
 */
function forgotPassword(data) {
  return post('/auth/forgot-password', data)
}

/**
 * 获取当前用户信息
 */
function getUserInfo() {
  return get('/auth/userinfo')
}

/**
 * 获取当前用户统计数据
 */
function getUserStats() {
  return get('/auth/stats')
}

/**
 * 更新用户资料
 * @param {Object} data 用户资料数据
 */
function updateProfile(data) {
  return put('/auth/profile', data)
}

/**
 * 刷新token
 */
function refreshToken() {
  return post('/auth/refresh')
}

/**
 * 退出登录
 */
function logout() {
  return post('/auth/logout')
}

// ====================================
// 工单相关API
// ====================================

/**
 * 获取工单列表
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页大小
 * @param {string} params.category 工单类别
 * @param {string} params.status 工单状态
 * @param {number} params.submitterId 提交人ID
 * @param {number} params.assigneeId 分配人ID
 */
function getWorkOrderList(params) {
  return get('/workorder/list', params)
}

/**
 * 获取工单详情
 * @param {number} id 工单ID
 */
function getWorkOrderDetail(id) {
  return get(`/workorder/${id}`)
}

/**
 * 提交工单
 * @param {Object} data 工单数据
 * @param {string} data.title 工单标题
 * @param {string} data.content 工单内容
 * @param {string} data.category 工单类别
 * @param {string} data.priority 优先级
 * @param {number} data.submitterId 提交人ID
 * @param {number} data.roomId 房屋ID
 */
function submitWorkOrder(data) {
  return post('/workorder', data)
}

/**
 * 分配工单（管理员）
 * @param {number} id 工单ID
 * @param {number} assigneeId 分配人ID
 */
function assignWorkOrder(id, assigneeId) {
  return put(`/workorder/${id}/assign`, null, {
    url: `/workorder/${id}/assign?assigneeId=${assigneeId}`
  })
}

/**
 * 维修工接单
 * @param {number} id 工单ID
 */
function acceptWorkOrder(id, workerId) {
  const params = workerId ? { workerId } : {}
  const queryString = Object.keys(params)
    .map(key => `${key}=${params[key]}`)
    .join('&')
  const url = queryString ? `/workorder/${id}/accept?${queryString}` : `/workorder/${id}/accept`
  return put(url)
}

/**
 * 开始处理工单（维修工）
 * @param {number} id 工单ID
 */
function startWorkOrder(id) {
  return put(`/workorder/${id}/start`)
}

/**
 * 完成工单（维修工）
 * @param {number} id 工单ID
 * @param {number} cost 费用
 */
function completeWorkOrder(id, cost) {
  const url = cost ? `/workorder/${id}/complete?cost=${cost}` : `/workorder/${id}/complete`
  return put(url)
}

/**
 * 评价工单（业主）- 简单评价（兼容旧版）
 * @param {number} id 工单ID
 * @param {number} rating 评分
 * @param {string} feedback 反馈
 */
function rateWorkOrder(id, rating, feedback) {
  let url = `/workorder/${id}/rate?rating=${rating}`
  if (feedback) {
    url += `&feedback=${encodeURIComponent(feedback)}`
  }
  return put(url)
}

/**
 * 删除工单（管理员）
 * @param {number} id 工单ID
 */
function deleteWorkOrder(id) {
  return del(`/workorder/${id}`)
}

/**
 * 获取工单统计信息
 * @param {number} workerId 维修工ID（可选）
 */
function getWorkOrderStats(workerId) {
  const params = workerId ? { workerId } : {}
  return get('/workorder/stats', params)
}

// ====================================
// 用户管理API
// ====================================

/**
 * 获取用户列表
 * @param {Object} params 查询参数
 */
function getUserList(params) {
  return get('/admin/users', params)
}

/**
 * 创建用户
 * @param {Object} data 用户数据
 */
function createUser(data) {
  return post('/admin/users', data)
}

/**
 * 更新用户
 * @param {number} id 用户ID
 * @param {Object} data 用户数据
 */
function updateUser(id, data) {
  return put(`/admin/users/${id}`, data)
}

/**
 * 删除用户
 * @param {number} id 用户ID
 */
function deleteUser(id) {
  return del(`/admin/users/${id}`)
}

// ====================================
// 房产管理API
// ====================================

/**
 * 获取楼栋列表
 */
function getBuildingList() {
  return get('/admin/buildings')
}

/**
 * 获取房屋列表
 * @param {Object} params 查询参数
 */
function getRoomList(params) {
  return get('/admin/rooms', params)
}

/**
 * 获取用户房产信息
 * @param {number} userId 用户ID
 */
function getUserRooms(userId) {
  return get(`/owner/rooms/${userId}`)
}

/**
 * 获取当前用户房屋信息
 */
function getMyRooms() {
  return get('/owner/my-rooms')
}

/**
 * 获取房屋详情
 * @param {number} roomId 房屋ID
 */
function getRoomDetail(roomId) {
  return get(`/owner/rooms/detail/${roomId}`)
}

/**
 * 更新房屋信息
 * @param {number} roomId 房屋ID
 * @param {Object} data 房屋数据
 */
function updateRoom(roomId, data) {
  return put(`/owner/rooms/${roomId}`, data)
}

/**
 * 验证房屋所有权
 * @param {number} roomId 房屋ID
 */
function verifyRoomOwnership(roomId) {
  return get('/owner/rooms/' + roomId + '/verify')
}

// ==================== 公告管理 ====================

/**
 * 获取已发布的公告列表
 * @param {Object|number} params 查询参数或限制数量
 */
function getAnnouncementList(params = 10) {
  // 兼容旧版本，如果传入的是数字，转换为参数对象
  if (typeof params === 'number') {
    params = { limit: params }
  }
  
  // 如果是分页查询
  if (params.pageNum) {
    return get('/announcement/published/page', params)
  }
  
  // 简单列表查询
  return get('/announcement/published', params)
}

/**
 * 获取置顶公告
 */
function getTopAnnouncements() {
  return get('/announcement/top')
}

/**
 * 根据类型获取公告
 * @param {string} type 公告类型
 * @param {number} limit 限制数量
 */
function getAnnouncementsByType(type, limit = 10) {
  return get('/announcement/type/' + type, { limit })
}

/**
 * 获取公告详情
 * @param {number} id 公告ID
 */
function getAnnouncementDetail(id) {
  return get(`/announcement/${id}`)
}

/**
 * 增加公告浏览量
 * @param {number} id 公告ID
 */
function increaseAnnouncementView(id) {
  return post(`/announcement/${id}/view`)
}

/**
 * 收藏公告
 * @param {number} id 公告ID
 */
function collectAnnouncement(id) {
  return post(`/announcement/${id}/collect`)
}

/**
 * 取消收藏公告
 * @param {number} id 公告ID
 */
function uncollectAnnouncement(id) {
  return delete(`/announcement/${id}/collect`)
}

// ====================================
// 财务管理API
// ====================================

/**
 * 获取账单列表
 * @param {Object} params 查询参数
 */
function getBillList(params) {
  return get('/bill/list', params)
}

/**
 * 获取账单详情
 * @param {number} id 账单ID
 */
function getBillDetail(id) {
  return get(`/bill/${id}`)
}

/**
 * 缴费
 * @param {number} billId 账单ID
 * @param {Object} data 缴费数据
 */
function payBill(billId, data) {
  return post(`/bill/pay?billId=${billId}&paymentAmount=${data.amount}&paymentMethod=${data.paymentMethod}`)
}

/**
 * 获取缴费记录
 * @param {Object} params 查询参数
 */
function getPaymentHistory(params) {
  return get('/payment/list', params)
}

// ====================================
// 原有公告管理API（已移除重复定义）
// ====================================

// ====================================
// 活动管理API
// ====================================

/**
 * 获取活动列表
 * @param {Object} params 查询参数
 * @param {Object} options 请求选项
 */
function getActivityList(params, options) {
  return get('/community/activities', params, options)
}

/**
 * 获取活动详情
 * @param {number} id 活动ID
 */
function getActivityDetail(id) {
  return get(`/community/activities/${id}`)
}

/**
 * 报名活动
 * @param {number} activityId 活动ID
 * @param {Object} data 报名数据
 */
function registerActivity(activityId, data) {
  return post(`/community/activities/${activityId}/register`, data || {})
}

/**
 * 取消活动报名
 * @param {number} activityId 活动ID
 * @param {string} reason 取消原因
 */
function cancelActivityRegistration(activityId, reason) {
  return post(`/community/activities/${activityId}/cancel`, null, { reason: reason || '' })
}

/**
 * 获取我的报名列表
 * @param {Object} params 查询参数
 */
function getMyRegistrations(params) {
  return get('/community/activities/my-registrations', params)
}

/**
 * 获取活动报名列表
 * @param {number} activityId 活动ID
 * @param {Object} params 查询参数
 */
function getActivityRegistrations(activityId, params) {
  return get(`/community/activities/${activityId}/registrations`, params)
}

// ====================================
// 访客管理API
// ====================================

/**
 * 获取访客列表
 * @param {Object} params 查询参数
 */
function getVisitorList(params) {
  return get('/visitor/list', params)
}

/**
 * 创建访客预约
 * @param {Object} data 访客数据
 */
function createVisitor(data) {
  return post('/visitor', data)
}

/**
 * 获取访客详情
 * @param {number} id 访客ID
 */
function getVisitorDetail(id) {
  return get(`/visitor/${id}`)
}

/**
 * 更新访客状态
 * @param {number} visitorId 访客ID
 * @param {string} status 状态
 * @param {number} guardId 保安ID（可选）
 */
function updateVisitorStatus(visitorId, status, guardId) {
  const params = { visitorId, status }
  if (guardId) params.guardId = guardId
  return post('/visitor/status', null, params)
}

/**
 * 根据二维码获取访客信息
 * @param {string} qrCode 二维码
 */
function getVisitorByQrCode(qrCode) {
  return get(`/visitor/qrcode/${qrCode}`)
}

/**
 * 访客到达签到
 * @param {string} qrCode 二维码
 * @param {number} guardId 保安ID
 */
function visitorArrival(qrCode, guardId) {
  return post('/visitor/arrival', null, { qrCode, guardId })
}

/**
 * 访客离开签退
 * @param {number} visitorId 访客ID
 * @param {number} guardId 保安ID
 */
function visitorDeparture(visitorId, guardId) {
  return post('/visitor/departure', null, { visitorId, guardId })
}

/**
 * 获取访客统计信息
 * @param {number} ownerId 业主ID
 */
function getVisitorStats(ownerId) {
  return get('/visitor/stats', { ownerId })
}

/**
 * 获取今日访客列表
 * @param {number} ownerId 业主ID
 */
function getTodayVisitors(ownerId) {
  return get('/visitor/today', { ownerId })
}

/**
 * 扫码验证访客
 * @param {string} qrContent 二维码内容
 * @param {number} guardId 保安ID
 */
function scanVerifyVisitor(qrContent, guardId) {
  return post('/visitor/scan-verify', { qrContent, guardId })
}

/**
 * 访客签到
 * @param {number} visitorId 访客ID
 * @param {number} guardId 保安ID
 */
function checkInVisitor(visitorId, guardId) {
  return post('/visitor/check-in', null, { visitorId, guardId })
}

/**
 * 访客签退
 * @param {number} visitorId 访客ID
 * @param {number} guardId 保安ID
 */
function checkOutVisitor(visitorId, guardId) {
  return post('/visitor/check-out', null, { visitorId, guardId })
}


/**
 * 创建访客二维码
 * @param {Object} data 访客二维码数据
 */
function createVisitorQrCode(data) {
  return post('/visitor-qr/create', data)
}

/**
 * 验证访客二维码
 * @param {string} qrCode 二维码
 * @param {number} guardId 保安ID
 */
function verifyVisitorQrCode(qrCode, guardId) {
  return post('/visitor-qr/verify', { qrCode, guardId })
}

/**
 * 使用访客二维码（签到）
 * @param {string} qrCode 二维码
 * @param {number} guardId 保安ID
 */
function useVisitorQrCode(qrCode, guardId) {
  return post('/visitor-qr/use', { qrCode, guardId })
}

/**
 * 删除访客记录
 * @param {number} id 访客ID
 */
function deleteVisitor(id) {
  return del(`/visitor/${id}`)
}

// ====================================
// 消息管理API
// ====================================

/**
 * 获取消息列表
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页大小
 * @param {string} params.messageType 消息类型
 * @param {number} params.isRead 是否已读
 */
function getMessageList(params) {
  return get('/message/list', params)
}

/**
 * 标记消息为已读
 * @param {number} id 消息ID
 */
function markMessageAsRead(id) {
  return put(`/message/${id}/read`)
}

/**
 * 批量标记消息为已读
 * @param {Array} messageIds 消息ID数组
 */
function batchMarkMessagesAsRead(messageIds) {
  return put('/message/batch-read', messageIds)
}

/**
 * 获取未读消息统计
 */
function getUnreadCount() {
  return get('/message/unread-count')
}

/**
 * 发送消息
 * @param {Object} data 消息数据
 */
function sendMessage(data) {
  return post('/message', data)
}

/**
 * 发送系统消息
 * @param {string} title 标题
 * @param {string} content 内容
 * @param {number} receiverId 接收人ID
 */
function sendSystemMessage(title, content, receiverId) {
  return post('/message/system', null, {
    url: `/message/system?title=${encodeURIComponent(title)}&content=${encodeURIComponent(content)}&receiverId=${receiverId}`
  })
}

/**
 * 发送公告消息
 * @param {string} title 标题
 * @param {string} content 内容
 * @param {Array} receiverIds 接收人ID数组
 */
function sendNoticeMessage(title, content, receiverIds) {
  return post('/message/notice', receiverIds, {
    url: `/message/notice?title=${encodeURIComponent(title)}&content=${encodeURIComponent(content)}`
  })
}

// ====================================
// 系统API
// ====================================

/**
 * 系统健康检查
 */
function healthCheck() {
  return get('/test/health')
}

/**
 * 获取系统信息
 */
function getSystemInfo() {
  return get('/test/info')
}

/**
 * 获取费用统计
 * @param {number} ownerId 业主ID
 */
function getBillSummary(ownerId) {
  return get('/bill/summary', { ownerId })
}

/**
 * 批量缴费
 * @param {Array} billIds 账单ID列表
 * @param {string} paymentMethod 支付方式
 */
function payBillsBatch(billIds, paymentMethod) {
  return post(`/bill/pay/batch?paymentMethod=${paymentMethod}`, billIds)
}

/**
 * 创建账单
 * @param {Object} data 账单数据
 */
function createBill(data) {
  return post('/bill', data)
}

/**
 * 批量创建账单
 * @param {Array} data 账单列表
 */
function createBillsBatch(data) {
  return post('/bill/batch', data)
}

/**
 * 更新账单
 * @param {number} id 账单ID
 * @param {Object} data 账单数据
 */
function updateBill(id, data) {
  return put(`/bill/${id}`, data)
}

/**
 * 删除账单
 * @param {number} id 账单ID
 */
function deleteBill(id) {
  return del(`/bill/${id}`)
}

// ====================================
// 投诉建议相关API
// ====================================

/**
 * 获取投诉建议列表
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页大小
 * @param {string} params.complaintType 投诉类型
 * @param {string} params.status 状态
 * @param {number} params.complainantId 投诉人ID
 * @param {string} params.urgencyLevel 紧急程度
 */
function getComplaintList(params) {
  return get('/complaint/list', params)
}

/**
 * 获取投诉建议详情
 * @param {number} id 投诉ID
 */
function getComplaintDetail(id) {
  return get(`/complaint/${id}`)
}

/**
 * 提交投诉建议
 * @param {Object} data 投诉建议数据
 */
function submitComplaint(data) {
  return post('/complaint', data)
}

/**
 * 获取投诉统计信息
 * @param {number} complainantId 投诉人ID
 */
function getComplaintStats(complainantId) {
  return get('/complaint/stats', { complainantId })
}

/**
 * 评价投诉处理
 * @param {Object} params 评价参数
 * @param {number} params.complaintId 投诉ID
 * @param {number} params.complainantId 投诉人ID
 * @param {number} params.satisfactionRating 满意度评分
 * @param {string} params.feedback 评价反馈
 */
function rateComplaint(params) {
  return post('/complaint/rate', params)
}

/**
 * 文件上传
 * @param {string} filePath 文件路径
 * @param {Object} formData 表单数据
 */
function uploadFile(filePath, formData = {}) {
  const { upload } = require('./request.js')
  return upload('/file/upload', filePath, formData)
}

// ==================== AI助手相关 ====================

/**
 * AI图片识别生成工单信息
 * @param {String} filePath 图片文件路径
 * @param {Object} params 参数：roomId, category
 */
function aiAnalyzeImage(filePath, params = {}) {
  const { upload } = require('./request.js')
  return upload('/ai/analyze-image', filePath, params)
}

/**
 * 根据AI分析结果创建工单
 * @param {Object} workOrderData 工单数据
 */
function createWorkOrderFromAI(workOrderData) {
  return post('/ai/create-workorder', workOrderData)
}

/**
 * 一键上传图片并创建工单
 * @param {String} filePath 图片文件路径
 * @param {Object} params 参数：roomId, category
 */
function aiQuickSubmit(filePath, params = {}) {
  const { upload } = require('./request.js')
  return upload('/ai/quick-submit', filePath, params)
}

// ====================================
// 技能标签相关API
// ====================================

/**
 * 获取所有启用的技能标签
 */
function getAllActiveSkills() {
  return get('/skill-tags/active')
}

/**
 * 获取技能标签（按分类分组）
 */
function getSkillsGrouped() {
  return get('/skill-tags/grouped')
}

/**
 * 根据分类获取技能标签
 */
function getSkillsByCategory(category) {
  return get(`/skill-tags/category/${category}`)
}

// ====================================
// 维修工相关API
// ====================================

/**
 * 获取当前维修工的技能列表
 */
function getMySkills() {
  return get('/worker/skills')
}

/**
 * 批量添加技能
 */
function addSkillsBatch(skillIds) {
  return post('/worker/skills/batch', { skillIds })
}

/**
 * 删除技能
 */
function deleteWorkerSkill(skillId) {
  return del(`/worker/skills/${skillId}`)
}

/**
 * 更新技能熟练度
 */
function updateSkillProficiency(id, data) {
  return put(`/worker/skills/${id}`, data)
}

/**
 * 获取当前维修工的档案
 */
function getWorkerProfile() {
  return get('/worker/profile')
}

/**
 * 更新工作状态
 */
function updateWorkStatus(workStatus) {
  return put('/worker/work-status', { workStatus })
}

/**
 * 更新维修工档案
 */
function updateWorkerProfile(profile) {
  return put('/worker/profile', profile)
}

/**
 * 获取当前维修工的评价列表
 */
function getWorkerRatings(pageNum = 1, pageSize = 10) {
  return get('/worker/ratings', { pageNum, pageSize })
}

/**
 * 回复评价
 */
function replyRating(ratingId, reply) {
  return post(`/worker/ratings/${ratingId}/reply`, { reply })
}

/**
 * 获取维修工统计数据
 */
function getWorkerStats() {
  return get('/worker/stats')
}

// ====================================
// 评价相关API
// ====================================

/**
 * 创建评价
 */
function createRating(data) {
  return post('/ratings', data)
}

/**
 * 获取工单评价
 */
function getRatingByOrderId(orderId) {
  return get(`/ratings/order/${orderId}`)
}

/**
 * 点赞评价
 */
function likeRating(ratingId) {
  return post(`/ratings/${ratingId}/like`)
}

/**
 * 详细评价工单（使用新的评价体系）
 * @param {Object} data 评价数据
 * @param {number} data.orderId 工单ID
 * @param {number} data.workerId 维修工ID
 * @param {number} data.overallScore 总体评分（1-5）
 * @param {number} data.serviceAttitudeScore 服务态度评分
 * @param {number} data.workQualityScore 工作质量评分
 * @param {number} data.responseSpeedScore 响应速度评分
 * @param {number} data.professionalismScore 专业能力评分
 * @param {string} data.content 评价内容
 * @param {string} data.images 评价图片（JSON数组）
 * @param {string} data.tags 评价标签（JSON数组）
 * @param {boolean} data.isAnonymous 是否匿名
 */
function rateWorkOrderDetail(data) {
  return post('/ratings', data)
}

module.exports = {
  // 认证相关
  login,
  register,
  forgotPassword,
  getUserInfo,
  getUserStats,
  updateProfile,
  refreshToken,
  logout,
  
  // 工单相关
  getWorkOrderList,
  getWorkOrderDetail,
  submitWorkOrder,
  assignWorkOrder,
  acceptWorkOrder,
  startWorkOrder,
  completeWorkOrder,
  rateWorkOrder,
  deleteWorkOrder,
  getWorkOrderStats,
  
  // 用户管理
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  
  // 房产管理
  getBuildingList,
  getRoomList,
  getUserRooms,
  getMyRooms,
  getRoomDetail,
  updateRoom,
  verifyRoomOwnership,

  // 公告管理
  getAnnouncementList,
  getTopAnnouncements,
  getAnnouncementsByType,
  getAnnouncementDetail,
  increaseAnnouncementView,
  collectAnnouncement,
  uncollectAnnouncement,
  
  // 财务管理
  getBillList,
  getBillDetail,
  getBillSummary,
  payBill,
  payBillsBatch,
  getPaymentHistory,
  createBill,
  createBillsBatch,
  updateBill,
  deleteBill,
  
  // 活动管理
  getActivityList,
  getActivityDetail,
  registerActivity,
  cancelActivityRegistration,
  getMyRegistrations,
  getActivityRegistrations,
  
  // 访客管理
  getVisitorList,
  getVisitorDetail,
  createVisitor,
  updateVisitorStatus,
  getVisitorByQrCode,
  visitorArrival,
  visitorDeparture,
  getVisitorStats,
  getTodayVisitors,
  scanVerifyVisitor,
  checkInVisitor,
  checkOutVisitor,
  deleteVisitor,
  
  // 消息管理
  getMessageList,
  markMessageAsRead,
  batchMarkMessagesAsRead,
  getUnreadCount,
  sendMessage,
  sendSystemMessage,
  sendNoticeMessage,
  
  // 系统
  healthCheck,
  getSystemInfo,
  uploadFile,
  
  // 投诉建议相关
  getComplaintList,
  getComplaintDetail,
  submitComplaint,
  getComplaintStats,
  rateComplaint,
  
  // 访客二维码管理
  createVisitorQrCode,
  verifyVisitorQrCode,
  useVisitorQrCode,
  
  // AI助手
  aiAnalyzeImage,
  createWorkOrderFromAI,
  aiQuickSubmit,
  
  // 技能标签相关
  getAllActiveSkills,
  getSkillsGrouped,
  getSkillsByCategory,
  
  // 维修工相关
  getMySkills,
  addSkillsBatch,
  deleteWorkerSkill,
  updateSkillProficiency,
  getWorkerProfile,
  updateWorkStatus,
  updateWorkerProfile,
  getWorkerRatings,
  replyRating,
  getWorkerStats,
  
// 评价相关
createRating,
getRatingByOrderId,
likeRating,
rateWorkOrderDetail
}




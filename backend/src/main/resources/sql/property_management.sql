/*
 Navicat Premium Data Transfer

 Source Server         : 养金鱼
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : localhost:3306
 Source Schema         : property_management

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 29/09/2025 21:58:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动描述',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动详情',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动摘要',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动类型',
  `organizer_id` bigint NOT NULL COMMENT '组织者ID',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_participants` int NULL DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int NOT NULL DEFAULT 0 COMMENT '当前参与人数',
  `registration_start` datetime NULL DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` datetime NULL DEFAULT NULL COMMENT '报名结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PLANNED' COMMENT '状态：PLANNED-计划中，REGISTRATION-报名中，UPCOMING-即将开始，ONGOING-进行中，COMPLETED-已完成，CANCELLED-已取消',
  `is_free` tinyint NOT NULL DEFAULT 1 COMMENT '是否免费：0-收费，1-免费',
  `fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '活动费用',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动主图',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_activity_organizer`(`organizer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (1, '春节联欢晚会', '社区居民新春联欢活动，精彩节目等您参与', '一年一度的春节联欢晚会即将开始！我们为大家准备了精彩的文艺表演、有趣的互动游戏和丰富的奖品。欢迎全家老少一起参加，共度美好时光。活动现场还有免费的茶点和小食供应，让我们一起迎接新年的到来！', '社区居民新春联欢活动，精彩节目等您参与，欢迎大家踊跃参加', 'ENTERTAINMENT', 1001, '社区活动中心', '2025-02-10 19:00:00', '2025-02-10 22:00:00', 100, 23, '2025-01-15 09:00:00', '2025-12-08 18:00:00', 'UPCOMING', 1, NULL, '/static/images/activity1.jpg', NULL, '2025-09-29 20:03:58', '2025-09-29 20:37:46', 0);
INSERT INTO `activity` VALUES (2, '羽毛球比赛', '社区羽毛球友谊赛，现在开始报名', '社区羽毛球友谊赛正在火热报名中！无论您是羽毛球新手还是高手，都可以参与其中。比赛采用友谊第一、比赛第二的原则，重在参与和交流。我们将根据报名情况分组进行比赛，确保每位参与者都能享受运动的乐趣。', '社区羽毛球友谊赛，欢迎羽毛球爱好者参与', 'SPORTS', 1001, '社区体育馆', '2025-01-18 14:00:00', '2025-01-18 18:00:00', 32, 16, '2025-01-10 09:00:00', '2025-11-17 18:00:00', 'REGISTRATION', 1, NULL, '/static/images/activity2.jpg', NULL, '2025-09-29 20:03:58', '2025-09-29 20:38:37', 0);
INSERT INTO `activity` VALUES (3, '健康知识讲座', '邀请专业医师为社区居民讲解健康养生知识', '本次健康知识讲座将邀请三甲医院的专业医师为大家讲解冬季养生保健知识，包括饮食调理、运动保健、疾病预防等方面的内容。讲座结束后还有免费的健康咨询环节，欢迎有需要的居民朋友参加。', '邀请专业医师为社区居民讲解健康养生知识', 'EDUCATION', 1001, '社区会议室', '2025-01-20 15:00:00', '2025-01-20 17:00:00', 50, 8, '2025-01-12 09:00:00', '2025-01-19 18:00:00', 'REGISTRATION', 1, NULL, '/static/images/activity3.jpg', NULL, '2025-09-29 20:03:58', '2025-09-29 20:03:58', 0);

-- ----------------------------
-- Table structure for activity_registration
-- ----------------------------
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `participants` int NOT NULL DEFAULT 1 COMMENT '参与人数',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'REGISTERED' COMMENT '状态：REGISTERED-已报名，CONFIRMED-已确认，CANCELLED-已取消',
  `registration_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_user`(`activity_id` ASC, `user_id` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_registration_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_registration_time`(`registration_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_registration
-- ----------------------------
INSERT INTO `activity_registration` VALUES (1, 2, 1002, 1, '张三', '13800000101', NULL, NULL, 'REGISTERED', '2025-09-29 20:38:38', NULL, NULL, NULL, '2025-09-29 20:38:38', '2025-09-29 20:38:38', 0);

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公告摘要',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告类型：NOTICE-通知，ACTIVITY-活动，MAINTENANCE-维护，EMERGENCY-紧急',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：LOW-低，NORMAL-普通，HIGH-高',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下线，EXPIRED-已过期',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `read_count` int NOT NULL DEFAULT 0 COMMENT '阅读次数',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_publisher_id`(`publisher_id` ASC) USING BTREE COMMENT '发布人ID索引',
  INDEX `idx_type`(`type` ASC) USING BTREE COMMENT '公告类型索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `idx_priority`(`priority` ASC) USING BTREE COMMENT '优先级索引',
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE COMMENT '发布时间索引',
  INDEX `idx_is_top`(`is_top` ASC) USING BTREE COMMENT '置顶索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  CONSTRAINT `fk_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '小区物业费缴费通知', '各位业主您好：\n\n2024年1月份物业费已开始缴费，请及时缴纳。\n\n缴费方式：\n1. 微信小程序在线缴费\n2. 支付宝扫码缴费\n3. 银行转账\n4. 物业服务中心现场缴费\n\n缴费截止日期：2024年1月25日\n逾期将产生滞纳金，请各位业主务必按时缴费。\n\n如有疑问，请联系物业服务中心。\n\n物业服务中心\n2024年1月1日', '2024年1月份物业费已开始缴费，请及时缴纳。缴费方式：微信小程序、支付宝、银行转账。', 'NOTICE', 'HIGH', 'PUBLISHED', 1001, '2024-01-01 09:00:00', NULL, 1, 156, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (2, '电梯维护公告', '各位业主：\n\n因设备维护需要，1号楼电梯将于12月15日晚上22:00-次日6:00进行维护。\n\n维护期间电梯停止使用，请业主提前安排出行，给您带来的不便敬请谅解。\n\n如有紧急情况，请联系物业值班室：400-888-9999\n\n物业服务中心\n2023年12月10日', '因设备维护需要，1号楼电梯将于12月15日晚上22:00-次日6:00进行维护，请业主提前安排出行。', 'MAINTENANCE', 'HIGH', 'PUBLISHED', 1001, '2023-12-10 16:00:00', NULL, 0, 89, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (3, '元旦联欢活动邀请', '亲爱的业主朋友们：\n\n小区将于12月31日晚19:00举办元旦联欢活动，欢迎各位业主参加！\n\n活动时间：2023年12月31日 19:00-21:00\n活动地点：小区中央广场\n活动内容：\n- 文艺表演\n- 游戏互动\n- 抽奖活动\n- 茶点招待\n\n请有意参加的业主到物业服务中心报名，报名截止时间：12月28日。\n\n让我们一起迎接新年的到来！\n\n社区委员会\n2023年12月15日', '小区将于12月31日晚举办元旦联欢活动，欢迎各位业主参加，具体安排详见活动页面。', 'ACTIVITY', 'NORMAL', 'PUBLISHED', 1001, '2023-12-15 10:00:00', NULL, 0, 234, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (4, '停车位管理规定', '各位车主业主：\n\n为了规范小区停车秩序，现对停车位管理进行调整：\n\n1. 固定车位：按月收费，费用200元/月\n2. 临时停车：按小时收费，费用5元/小时\n3. 访客车位：免费停车2小时，超时按临时停车收费\n4. 禁止占用消防通道、绿化带等区域停车\n5. 违规停车将被拖移，费用自理\n\n新规定将于2024年1月1日起执行，请各位车主遵守相关规定。\n\n物业管理处\n2023年12月18日', '为了规范小区停车秩序，现对停车位管理进行调整，请各位车主遵守相关规定。', 'NOTICE', 'NORMAL', 'PUBLISHED', 1001, '2023-12-18 14:30:00', NULL, 0, 78, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (5, '春节期间服务安排', '各位业主：\n\n春节期间（2月10日-2月17日）物业服务安排如下：\n\n值班时间：\n- 除夕至初三（2月10日-13日）：8:00-18:00\n- 初四至初六（2月14日-16日）：9:00-17:00\n- 初七（2月17日）起恢复正常工作时间\n\n紧急联系电话：400-888-9999（24小时）\n\n值班服务：\n- 水电维修\n- 安全巡查\n- 清洁保洁\n- 门禁管理\n\n祝大家新春快乐，阖家幸福！\n\n物业服务中心\n2023年12月20日', '春节期间（2月10日-2月17日）物业服务安排通知，紧急联系电话：400-888-9999。', 'NOTICE', 'HIGH', 'PUBLISHED', 1001, '2023-12-20 11:00:00', NULL, 1, 312, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (6, '垃圾分类倡议书', '亲爱的业主朋友们：\n\n为响应城市垃圾分类号召，共建美好绿色社区，我们倡议：\n\n垃圾分类标准：\n🗂️ 厨余垃圾：剩菜剩饭、瓜皮果核、茶叶渣等\n♻️ 可回收物：纸类、塑料、金属、玻璃等\n⚠️ 有害垃圾：废电池、废灯泡、过期药品等\n🗑️ 其他垃圾：除上述外的生活废弃物\n\n投放要求：\n- 分类投放到对应颜色的垃圾桶\n- 厨余垃圾需沥干水分\n- 可回收物保持清洁干燥\n- 有害垃圾单独包装\n\n垃圾分类点已设置在各栋楼下，请大家积极参与，共建绿色家园！\n\n环保委员会\n2023-12-22', '为响应城市垃圾分类号召，共建美好绿色社区，请大家积极参与垃圾分类。', 'NOTICE', 'NORMAL', 'PUBLISHED', 1001, '2023-12-22 15:00:00', NULL, 0, 145, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (7, '紧急停水通知', '紧急通知：\n\n因供水管道突发故障，需要紧急抢修，今日下午15:00-19:00将全面停水。\n\n影响范围：全小区\n停水时间：今日15:00-19:00（预计）\n\n请各位业主：\n1. 提前储备生活用水\n2. 关闭家中水龙头，避免来水时溅水\n3. 如有紧急用水需求，可到物业服务中心\n\n抢修完成后将立即恢复供水，具体恢复时间另行通知。\n\n给您带来的不便深表歉意！\n\n物业服务中心\n2024年1月5日 12:00', '因供水管道突发故障，今日下午15:00-19:00将全面停水，请提前储备用水。', 'EMERGENCY', 'HIGH', 'PUBLISHED', 1001, '2024-01-05 12:00:00', NULL, 1, 89, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);
INSERT INTO `announcement` VALUES (8, '小区安全提醒', '各位业主：\n\n近期发生几起安全事件，为保障大家的人身财产安全，特别提醒：\n\n安全提醒：\n1. 外出时请锁好门窗，贵重物品妥善保管\n2. 不要给陌生人开门或提供门禁密码\n3. 发现可疑人员请及时联系物业或报警\n4. 车辆停放后请锁好车门，贵重物品不要留在车内\n5. 夜间出行请结伴而行，注意人身安全\n\n安保措施：\n- 已加强门禁管理和巡逻力度\n- 增加监控设备和照明设施\n- 24小时安保值班\n\n如发现异常情况，请立即联系：\n物业值班室：400-888-9999\n报警电话：110\n\n让我们共同维护小区安全！\n\n物业服务中心\n2024年1月8日', '近期发生几起安全事件，为保障大家的人身财产安全，请注意防范。', 'NOTICE', 'HIGH', 'PUBLISHED', 1001, '2024-01-08 10:00:00', NULL, 0, 203, NULL, '2025-09-29 19:01:26', '2025-09-29 19:01:26', 0);

-- ----------------------------
-- Table structure for bill
-- ----------------------------
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账单编号',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `bill_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账单类型：PROPERTY-物业费，UTILITY-水电费，PARKING-停车费，MAINTENANCE-维修费',
  `billing_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计费周期，如：2024年1月',
  `amount` decimal(10, 2) NOT NULL COMMENT '应缴金额',
  `paid_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '已缴金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'UNPAID' COMMENT '状态：UNPAID-未缴费，PAID-已缴费，OVERDUE-逾期',
  `due_date` date NULL DEFAULT NULL COMMENT '缴费截止日期',
  `paid_date` datetime NULL DEFAULT NULL COMMENT '缴费日期',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '费用说明',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bill_no`(`bill_no` ASC) USING BTREE,
  INDEX `idx_bill_type`(`bill_type` ASC) USING BTREE,
  INDEX `idx_billing_period`(`billing_period` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bill
-- ----------------------------

-- ----------------------------
-- Table structure for building
-- ----------------------------
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `building_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '楼栋名称',
  `building_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '楼栋编号',
  `total_floors` int NOT NULL COMMENT '总楼层数',
  `total_units` int NOT NULL COMMENT '总单元数',
  `total_rooms` int NOT NULL COMMENT '总房间数',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详细地址',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_building_no`(`building_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '楼栋表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of building
-- ----------------------------
INSERT INTO `building` VALUES (1, '1号楼', 'A001', 18, 4, 144, '智慧小区1号楼', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `building` VALUES (2, '2号楼', 'A002', 18, 4, 144, '智慧小区2号楼', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `building` VALUES (3, '3号楼', 'A003', 15, 3, 90, '智慧小区3号楼', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);

-- ----------------------------
-- Table structure for complaint
-- ----------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `complaint_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉编号',
  `complainant_id` bigint NOT NULL COMMENT '投诉人ID',
  `room_id` bigint NULL DEFAULT NULL COMMENT '房屋ID',
  `complaint_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉类型：NOISE-噪音投诉，HYGIENE-环境卫生，FACILITY-设施维护，SERVICE-物业服务，SECURITY-安全问题，OTHER-其他问题',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '相关图片（JSON数组）',
  `urgency_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL' COMMENT '紧急程度：LOW-低，NORMAL-普通，HIGH-高，URGENT-紧急',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，ASSIGNED-已分配，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理结果',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `satisfaction_rating` int NULL DEFAULT NULL COMMENT '满意度评分（1-5分）',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价反馈',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_complaint_no`(`complaint_no` ASC) USING BTREE,
  INDEX `idx_complainant_id`(`complainant_id` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_complaint_type`(`complaint_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_urgency_level`(`urgency_level` ASC) USING BTREE,
  INDEX `idx_handler_id`(`handler_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投诉建议表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of complaint
-- ----------------------------
INSERT INTO `complaint` VALUES (1, 'CP202509290001', 1002, NULL, 'NOISE', '噪音投诉', '111', '[]', 'NORMAL', 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, '', '2025-09-29 21:46:08', '', '2025-09-29 21:46:08', 0);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一标识ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息正文内容',
  `message_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型分类：SYSTEM=系统消息，SERVICE=服务消息，NOTICE=公告消息',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'INFO' COMMENT '消息重要程度：INFO=普通信息，WARNING=警告消息，ERROR=错误消息',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送人用户ID',
  `receiver_id` bigint NOT NULL COMMENT '接收人用户ID',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '阅读状态标记：0=未读消息，1=已读消息',
  `business_id` bigint NULL DEFAULT NULL COMMENT '关联的业务数据ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联的业务类型：WORK_ORDER=工单业务，BILL=账单业务，NOTICE=公告业务',
  `icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息图标路径',
  `icon_bg` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标背景色样式',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '消息最后更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '软删除标记：0=正常数据，1=已删除数据',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver_type`(`receiver_id` ASC, `message_type` ASC) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_business`(`business_id` ASC, `business_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息中心数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for owner_room
-- ----------------------------
DROP TABLE IF EXISTS `owner_room`;
CREATE TABLE `owner_room`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关系类型：OWNER-业主，TENANT-租户，FAMILY-家属',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `contract_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '合同编号（租户适用）',
  `monthly_rent` decimal(10, 2) NULL DEFAULT NULL COMMENT '月租金（租户适用）',
  `deposit` decimal(10, 2) NULL DEFAULT NULL COMMENT '押金（租户适用）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-无效，1-有效',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_owner_room_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_owner_room_room`(`room_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  CONSTRAINT `fk_owner_room_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_owner_room_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户房产关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of owner_room
-- ----------------------------
INSERT INTO `owner_room` VALUES (1, 1002, 1, 'OWNER', '2023-01-01', NULL, NULL, NULL, NULL, 1, '购买的第一套房产', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `owner_room` VALUES (2, 1002, 3, 'OWNER', '2023-06-01', NULL, NULL, NULL, NULL, 1, '投资房产', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `owner_room` VALUES (3, 1003, 4, 'OWNER', '2023-03-01', NULL, NULL, NULL, NULL, 1, '自住房产', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `owner_room` VALUES (4, 1004, 5, 'OWNER', '2023-01-01', NULL, NULL, NULL, NULL, 1, '商用房产', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `owner_room` VALUES (5, 1005, 7, 'OWNER', '2023-02-01', NULL, NULL, NULL, NULL, 1, '婚房', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `owner_room` VALUES (6, 1006, 8, 'OWNER', '2023-04-01', NULL, NULL, NULL, NULL, 1, '投资房产', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);

-- ----------------------------
-- Table structure for payment
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付编号',
  `bill_id` bigint NOT NULL COMMENT '账单ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式：WECHAT-微信支付，ALIPAY-支付宝，BANK-银行转账',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SUCCESS' COMMENT '支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败',
  `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方交易号',
  `payment_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_bill_id`(`bill_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_payment_method`(`payment_method` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_payment_time`(`payment_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment
-- ----------------------------

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房屋ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `unit_id` bigint NOT NULL COMMENT '单元ID',
  `room_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房屋编号',
  `floor` int NOT NULL COMMENT '楼层',
  `room_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房屋类型：RESIDENTIAL-住宅，COMMERCIAL-商用，OFFICE-办公',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '建筑面积（平方米）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VACANT' COMMENT '房屋状态：VACANT-空置，OCCUPIED-已入住，RENTED-出租',
  `property_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '物业费（元/月）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_no`(`building_id` ASC, `unit_id` ASC, `room_no` ASC) USING BTREE,
  INDEX `fk_room_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_room_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_room_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房屋表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, 2, '301', 3, 'RESIDENTIAL', 100.00, 'OCCUPIED', 150.00, '2025-09-29 18:34:09', '2025-09-29 18:46:24', 0);
INSERT INTO `room` VALUES (2, 1, 2, '302', 3, 'RESIDENTIAL', 92.30, 'VACANT', 155.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `room` VALUES (3, 2, 5, '502', 5, 'RESIDENTIAL', 120.00, 'OCCUPIED', 180.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `room` VALUES (4, 2, 6, '801', 8, 'RESIDENTIAL', 95.20, 'OCCUPIED', 160.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `room` VALUES (5, 3, 9, '101', 1, 'COMMERCIAL', 150.00, 'OCCUPIED', 300.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `room` VALUES (6, 1, 1, '1801', 18, 'RESIDENTIAL', 105.50, 'VACANT', 170.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `room` VALUES (7, 2, 7, '1205', 12, 'RESIDENTIAL', 88.80, 'OCCUPIED', 145.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `room` VALUES (8, 3, 10, '601', 6, 'RESIDENTIAL', 96.00, 'OCCUPIED', 165.00, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `sys_role` VALUES (2, '物业管理员', 'ADMIN', '物业管理员，负责日常管理工作', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `sys_role` VALUES (3, '业主', 'OWNER', '小区业主用户', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `sys_role` VALUES (4, '保安', 'GUARD', '小区保安人员', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `sys_role` VALUES (5, '维修工', 'WORKER', '维修工作人员', 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像地址',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户类型：ADMIN-管理员，OWNER-业主，GUARD-保安，WORKER-维修工',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别：MALE-男，FEMALE-女，OTHER-其他',
  `birthday` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生日',
  `signature` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人签名',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系电话',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1009 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1000, 'admin', '123456', '系统管理员', '13800000001', 'admin@property.com', NULL, 1, 'ADMIN', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1001, 'manager', '123456', '物业经理', '13800000002', 'manager@property.com', NULL, 1, 'ADMIN', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1002, 'owner001', '123456', 'Yan', '13800000101', 'zhangsan@example.com', NULL, 1, 'OWNER', '2025-09-29 21:30:04', '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, 'MALE', '', '111', '', '');
INSERT INTO `sys_user` VALUES (1003, 'owner002', '123456', '李四', '13800000102', 'lisi@example.com', NULL, 1, 'OWNER', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1004, 'owner003', '123456', '王五', '13800000103', 'wangwu@example.com', NULL, 1, 'OWNER', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1005, 'guard001', '123456', '保安小刘', '13800000201', 'guard001@property.com', NULL, 1, 'GUARD', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1006, 'guard002', '123456', '保安小赵', '13800000202', 'guard002@property.com', NULL, 1, 'GUARD', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1007, 'worker001', '123456', '维修师傅老张', '13800000301', 'worker001@property.com', NULL, 1, 'WORKER', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1008, 'worker002', '123456', '维修师傅老李', '13800000302', 'worker002@property.com', NULL, 1, 'WORKER', NULL, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `fk_user_role_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1000, 1, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (2, 1001, 2, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (3, 1002, 3, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (4, 1003, 3, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (5, 1004, 3, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (6, 1005, 4, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (7, 1006, 4, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (8, 1007, 5, '2025-09-29 18:34:09');
INSERT INTO `sys_user_role` VALUES (9, 1008, 5, '2025-09-29 18:34:09');

-- ----------------------------
-- Table structure for unit
-- ----------------------------
DROP TABLE IF EXISTS `unit`;
CREATE TABLE `unit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `unit_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单元名称',
  `unit_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单元编号',
  `total_floors` int NOT NULL COMMENT '总楼层数',
  `total_rooms` int NOT NULL COMMENT '总房间数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_unit_no`(`unit_no` ASC) USING BTREE,
  INDEX `fk_unit_building`(`building_id` ASC) USING BTREE,
  CONSTRAINT `fk_unit_building` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单元表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of unit
-- ----------------------------
INSERT INTO `unit` VALUES (1, 1, '1单元', '1-1', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (2, 1, '2单元', '1-2', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (3, 1, '3单元', '1-3', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (4, 1, '4单元', '1-4', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (5, 2, '1单元', '2-1', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (6, 2, '2单元', '2-2', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (7, 2, '3单元', '2-3', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (8, 2, '4单元', '2-4', 18, 36, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (9, 3, '1单元', '3-1', 15, 30, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (10, 3, '2单元', '3-2', 15, 30, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);
INSERT INTO `unit` VALUES (11, 3, '3单元', '3-3', 15, 30, 1, '2025-09-29 18:34:09', '2025-09-29 18:34:09', 0);

-- ----------------------------
-- Table structure for visitor
-- ----------------------------
DROP TABLE IF EXISTS `visitor`;
CREATE TABLE `visitor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访客ID',
  `visitor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访客手机号',
  `visitor_company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '访客公司',
  `visit_purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来访目的',
  `room_id` bigint NOT NULL COMMENT '访问房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `visit_date` date NOT NULL COMMENT '访问日期',
  `planned_arrival` datetime NULL DEFAULT NULL COMMENT '预计到达时间',
  `planned_departure` datetime NULL DEFAULT NULL COMMENT '预计离开时间',
  `actual_arrival` datetime NULL DEFAULT NULL COMMENT '实际到达时间',
  `actual_departure` datetime NULL DEFAULT NULL COMMENT '实际离开时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已批准，ARRIVED-已到达，DEPARTED-已离开，REJECTED-已拒绝，EXPIRED-已过期',
  `qr_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '二维码',
  `guard_id` bigint NULL DEFAULT NULL COMMENT '登记保安ID',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_visitor_phone`(`visitor_phone` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_visit_date`(`visit_date` ASC) USING BTREE,
  INDEX `idx_qr_code`(`qr_code` ASC) USING BTREE,
  CONSTRAINT `fk_visitor_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_visitor_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '访客表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of visitor
-- ----------------------------

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单编号',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工单内容',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单类别：REPAIR-维修，COMPLAINT-投诉，SUGGESTION-建议',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：LOW-低，MEDIUM-中，HIGH-高，URGENT-紧急',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-已完成，CLOSED-已关闭',
  `submitter_id` bigint NOT NULL COMMENT '提交人ID',
  `assignee_id` bigint NULL DEFAULT NULL COMMENT '分配人ID',
  `room_id` bigint NULL DEFAULT NULL COMMENT '关联房屋ID',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片地址（JSON数组）',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `assign_time` datetime NULL DEFAULT NULL COMMENT '分配时间',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始处理时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `rating` tinyint NULL DEFAULT NULL COMMENT '评分（1-5）',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '反馈意见',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '费用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `fk_order_submitter`(`submitter_id` ASC) USING BTREE,
  INDEX `fk_order_assignee`(`assignee_id` ASC) USING BTREE,
  INDEX `fk_order_room`(`room_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  CONSTRAINT `fk_order_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_submitter` FOREIGN KEY (`submitter_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_order
-- ----------------------------

-- ----------------------------
-- Table structure for work_order_log
-- ----------------------------
DROP TABLE IF EXISTS `work_order_log`;
CREATE TABLE `work_order_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint NOT NULL COMMENT '工单ID',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型：SUBMIT-提交，ASSIGN-分配，START-开始处理，UPDATE-更新，COMPLETE-完成，CLOSE-关闭',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_log_order`(`order_id` ASC) USING BTREE,
  INDEX `fk_log_operator`(`operator_id` ASC) USING BTREE,
  INDEX `idx_operation`(`operation` ASC) USING BTREE,
  CONSTRAINT `fk_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_log_order` FOREIGN KEY (`order_id`) REFERENCES `work_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单处理记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_order_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

-- 物业管理系统数据库初始化脚本
-- 包含建表语句和测试数据

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS property_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE property_management;

-- ====================================
-- 用户相关表
-- ====================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型：ADMIN-管理员，OWNER-业主，GUARD-保安，WORKER-维修工',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ====================================
-- 房产相关表
-- ====================================

-- 楼栋表
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `building_name` varchar(50) NOT NULL COMMENT '楼栋名称',
  `building_no` varchar(20) NOT NULL COMMENT '楼栋编号',
  `total_floors` int NOT NULL COMMENT '总楼层数',
  `total_units` int NOT NULL COMMENT '总单元数',
  `total_rooms` int NOT NULL COMMENT '总房间数',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_no` (`building_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋表';

-- 单元表
DROP TABLE IF EXISTS `unit`;
CREATE TABLE `unit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `unit_name` varchar(50) NOT NULL COMMENT '单元名称',
  `unit_no` varchar(20) NOT NULL COMMENT '单元编号',
  `total_floors` int NOT NULL COMMENT '总楼层数',
  `total_rooms` int NOT NULL COMMENT '总房间数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_unit_building` (`building_id`),
  KEY `idx_unit_no` (`unit_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单元表';

-- 房屋表
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房屋ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `unit_id` bigint NOT NULL COMMENT '单元ID',
  `room_no` varchar(20) NOT NULL COMMENT '房屋编号',
  `floor` int NOT NULL COMMENT '楼层',
  `room_type` varchar(20) NOT NULL COMMENT '房屋类型：RESIDENTIAL-住宅，COMMERCIAL-商用，OFFICE-办公',
  `area` decimal(10,2) DEFAULT NULL COMMENT '建筑面积（平方米）',
  `status` varchar(20) NOT NULL DEFAULT 'VACANT' COMMENT '房屋状态：VACANT-空置，OCCUPIED-已入住，RENTED-出租',
  `property_fee` decimal(10,2) DEFAULT NULL COMMENT '物业费（元/月）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_no` (`building_id`,`unit_id`,`room_no`),
  KEY `fk_room_unit` (`unit_id`),
  KEY `idx_room_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房屋表';

-- 业主房产关联表
DROP TABLE IF EXISTS `owner_room`;
CREATE TABLE `owner_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `relation_type` varchar(20) NOT NULL COMMENT '关系类型：OWNER-业主，TENANT-租户，FAMILY-家属',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业主房产关联表';

-- ====================================
-- 工单相关表
-- ====================================

-- 工单表
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `order_no` varchar(50) NOT NULL COMMENT '工单编号',
  `title` varchar(200) NOT NULL COMMENT '工单标题',
  `content` text COMMENT '工单内容',
  `category` varchar(50) NOT NULL COMMENT '工单类别：REPAIR-维修，COMPLAINT-投诉，SUGGESTION-建议',
  `priority` varchar(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：LOW-低，MEDIUM-中，HIGH-高，URGENT-紧急',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-已完成，CLOSED-已关闭',
  `submitter_id` bigint NOT NULL COMMENT '提交人ID',
  `assignee_id` bigint DEFAULT NULL COMMENT '分配人ID',
  `room_id` bigint DEFAULT NULL COMMENT '关联房屋ID',
  `images` text COMMENT '图片地址（JSON数组）',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `assign_time` datetime DEFAULT NULL COMMENT '分配时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始处理时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `rating` tinyint DEFAULT NULL COMMENT '评分（1-5）',
  `feedback` text COMMENT '反馈意见',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '费用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- 账单表
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  `bill_no` varchar(50) NOT NULL COMMENT '账单编号',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `bill_type` varchar(20) NOT NULL COMMENT '账单类型：PROPERTY-物业费，WATER-水费，ELECTRICITY-电费，GAS-燃气费，PARKING-停车费',
  `billing_period` varchar(20) NOT NULL COMMENT '计费周期（YYYY-MM）',
  `amount` decimal(10,2) NOT NULL COMMENT '应缴金额',
  `paid_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已缴金额',
  `status` varchar(20) NOT NULL DEFAULT 'UNPAID' COMMENT '状态：UNPAID-未缴费，PARTIAL-部分缴费，PAID-已缴费，OVERDUE-逾期',
  `due_date` date NOT NULL COMMENT '缴费截止日期',
  `paid_date` datetime DEFAULT NULL COMMENT '缴费日期',
  `description` varchar(500) DEFAULT NULL COMMENT '费用说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_bill_type` (`bill_type`),
  KEY `idx_billing_period` (`billing_period`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- ====================================
-- 插入测试数据
-- ====================================

-- 插入角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
(2, '物业管理员', 'ADMIN', '物业管理员，负责日常管理工作'),
(3, '业主', 'OWNER', '小区业主用户'),
(4, '保安', 'GUARD', '小区保安人员'),
(5, '维修工', 'WORKER', '维修工作人员');

-- 插入用户数据（密码为123456的BCrypt加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
(1000, 'admin', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '系统管理员', '13800000001', 'admin@property.com', 'ADMIN', 1),
(1001, 'manager', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '物业经理', '13800000002', 'manager@property.com', 'ADMIN', 1),
(1002, 'owner001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '张三', '13800000101', 'zhangsan@example.com', 'OWNER', 1),
(1003, 'owner002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '李四', '13800000102', 'lisi@example.com', 'OWNER', 1),
(1004, 'owner003', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '王五', '13800000103', 'wangwu@example.com', 'OWNER', 1),
(1005, 'guard001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '保安小刘', '13800000201', 'guard001@property.com', 'GUARD', 1),
(1006, 'guard002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '保安小赵', '13800000202', 'guard002@property.com', 'GUARD', 1),
(1007, 'worker001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '维修师傅老张', '13800000301', 'worker001@property.com', 'WORKER', 1),
(1008, 'worker002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '维修师傅老李', '13800000302', 'worker002@property.com', 'WORKER', 1);

-- 插入用户角色关联数据
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1000, 1), (1001, 2), (1002, 3), (1003, 3), (1004, 3), (1005, 4), (1006, 4), (1007, 5), (1008, 5);

-- 插入楼栋数据
INSERT INTO `building` (`id`, `building_name`, `building_no`, `total_floors`, `total_units`, `total_rooms`) VALUES
(1, '1号楼', 'A001', 18, 2, 72),
(2, '2号楼', 'A002', 18, 2, 72);

-- 插入单元数据
INSERT INTO `unit` (`id`, `building_id`, `unit_name`, `unit_no`, `total_floors`, `total_rooms`) VALUES
(1, 1, '1单元', '1', 18, 36),
(2, 1, '2单元', '2', 18, 36);

-- 插入房屋数据
INSERT INTO `room` (`id`, `building_id`, `unit_id`, `room_no`, `floor`, `room_type`, `area`, `status`, `property_fee`) VALUES
(1, 1, 1, '101', 1, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50),
(2, 1, 1, '102', 1, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50);

-- 插入业主房产关联数据
INSERT INTO `owner_room` (`owner_id`, `room_id`, `relation_type`, `start_date`) VALUES
(1002, 1, 'OWNER', '2023-01-01'),
(1003, 2, 'OWNER', '2023-02-01');

-- 插入工单数据
INSERT INTO `work_order` (`id`, `order_no`, `title`, `content`, `category`, `priority`, `status`, `submitter_id`, `assignee_id`, `room_id`, `submit_time`) VALUES
(1, 'WO202312010001', '厨房水龙头漏水', '厨房洗菜盆水龙头一直在滴水，请尽快维修', 'REPAIR', 'MEDIUM', 'PENDING', 1002, 1005, 1, '2023-12-01 09:30:00'),
(2, 'WO202312010002', '电梯噪音问题', '1号楼电梯运行时噪音很大，影响居住', 'COMPLAINT', 'HIGH', 'PROCESSING', 1003, 1005, 2, '2023-12-01 14:20:00');

-- 插入账单数据
INSERT INTO `bill` (`id`, `bill_no`, `room_id`, `owner_id`, `bill_type`, `billing_period`, `amount`, `status`, `due_date`) VALUES
(1, 'BILL202312001', 1, 1002, 'PROPERTY', '2023-12', 268.50, 'UNPAID', '2023-12-31'),
(2, 'BILL202312002', 2, 1003, 'PROPERTY', '2023-12', 268.50, 'UNPAID', '2023-12-31');

-- 完成初始化
SELECT '数据库初始化完成！' AS message;
SELECT '默认账号: admin/manager/owner001/guard001/worker001' AS account_info;
SELECT '默认密码: 123456' AS password_info;


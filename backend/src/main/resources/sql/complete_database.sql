-- ========================================
-- 智慧物业管理系统 - 完整数据库重建脚本
-- 版本：1.0.0
-- 创建时间：2024-12-29
-- ========================================

-- 删除数据库并重新创建
DROP DATABASE IF EXISTS property_management;
CREATE DATABASE property_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE property_management;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================
-- 用户和权限相关表
-- ====================================

-- 系统用户表
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

-- 系统角色表
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
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ====================================
-- 房产管理相关表
-- ====================================

-- 楼栋表
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
  KEY `idx_unit_no` (`unit_no`),
  CONSTRAINT `fk_unit_building` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单元表';

-- 房屋表
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
  KEY `idx_room_status` (`status`),
  CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`),
  CONSTRAINT `fk_room_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房屋表';

-- 用户房产关系表
CREATE TABLE `owner_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `relation_type` varchar(20) NOT NULL COMMENT '关系类型：OWNER-业主，TENANT-租户，FAMILY-家属',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `contract_no` varchar(50) DEFAULT NULL COMMENT '合同编号（租户适用）',
  `monthly_rent` decimal(10,2) DEFAULT NULL COMMENT '月租金（租户适用）',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '押金（租户适用）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_owner_room_user` (`user_id`),
  KEY `fk_owner_room_room` (`room_id`),
  KEY `idx_relation_type` (`relation_type`),
  CONSTRAINT `fk_owner_room_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `fk_owner_room_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户房产关系表';

-- ====================================
-- 工单管理相关表
-- ====================================

-- 工单表
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
  KEY `fk_order_submitter` (`submitter_id`),
  KEY `fk_order_assignee` (`assignee_id`),
  KEY `fk_order_room` (`room_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  CONSTRAINT `fk_order_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_order_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `fk_order_submitter` FOREIGN KEY (`submitter_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- 工单处理记录表
CREATE TABLE `work_order_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint NOT NULL COMMENT '工单ID',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operation` varchar(50) NOT NULL COMMENT '操作类型：SUBMIT-提交，ASSIGN-分配，START-开始处理，UPDATE-更新，COMPLETE-完成，CLOSE-关闭',
  `content` text COMMENT '操作内容',
  `images` text COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_log_order` (`order_id`),
  KEY `fk_log_operator` (`operator_id`),
  KEY `idx_operation` (`operation`),
  CONSTRAINT `fk_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_log_order` FOREIGN KEY (`order_id`) REFERENCES `work_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单处理记录表';

-- ====================================
-- 财务管理相关表
-- ====================================

-- 账单表
CREATE TABLE `bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_no` varchar(32) NOT NULL COMMENT '账单编号',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `bill_type` varchar(20) NOT NULL COMMENT '账单类型：PROPERTY-物业费，UTILITY-水电费，PARKING-停车费，MAINTENANCE-维修费',
  `billing_period` varchar(20) NOT NULL COMMENT '计费周期，如：2024年1月',
  `amount` decimal(10,2) NOT NULL COMMENT '应缴金额',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已缴金额',
  `status` varchar(20) DEFAULT 'UNPAID' COMMENT '状态：UNPAID-未缴费，PAID-已缴费，OVERDUE-逾期',
  `due_date` date COMMENT '缴费截止日期',
  `paid_date` datetime COMMENT '缴费日期',
  `description` text COMMENT '费用说明',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_bill_type` (`bill_type`),
  KEY `idx_status` (`status`),
  KEY `idx_billing_period` (`billing_period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 支付记录表
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_no` varchar(32) NOT NULL COMMENT '支付编号',
  `bill_id` bigint NOT NULL COMMENT '账单ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式：WECHAT-微信支付，ALIPAY-支付宝，BANK-银行转账',
  `status` varchar(20) DEFAULT 'SUCCESS' COMMENT '支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败',
  `transaction_id` varchar(64) COMMENT '第三方交易号',
  `payment_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  `remark` varchar(500) COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_status` (`status`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ====================================
-- 投诉建议相关表
-- ====================================

-- 投诉建议表
CREATE TABLE `complaint` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `complaint_no` varchar(32) NOT NULL COMMENT '投诉编号',
  `complainant_id` bigint NOT NULL COMMENT '投诉人ID',
  `room_id` bigint COMMENT '房屋ID',
  `complaint_type` varchar(20) NOT NULL COMMENT '投诉类型：NOISE-噪音投诉，HYGIENE-环境卫生，FACILITY-设施维护，SERVICE-物业服务，SECURITY-安全问题，OTHER-其他问题',
  `title` varchar(200) NOT NULL COMMENT '投诉标题',
  `content` text NOT NULL COMMENT '投诉内容',
  `images` text COMMENT '相关图片（JSON数组）',
  `urgency_level` varchar(20) DEFAULT 'NORMAL' COMMENT '紧急程度：LOW-低，NORMAL-普通，HIGH-高，URGENT-紧急',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，ASSIGNED-已分配，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭',
  `handler_id` bigint COMMENT '处理人ID',
  `handle_result` text COMMENT '处理结果',
  `handle_time` datetime COMMENT '处理时间',
  `satisfaction_rating` int COMMENT '满意度评分（1-5分）',
  `feedback` text COMMENT '评价反馈',
  `remark` varchar(500) COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_complaint_no` (`complaint_no`),
  KEY `idx_complainant_id` (`complainant_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_complaint_type` (`complaint_type`),
  KEY `idx_status` (`status`),
  KEY `idx_urgency_level` (`urgency_level`),
  KEY `idx_handler_id` (`handler_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议表';

-- ====================================
-- 访客管理相关表
-- ====================================

-- 访客表
CREATE TABLE `visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访客ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '访客手机号',
  `visitor_company` varchar(100) DEFAULT NULL COMMENT '访客公司',
  `visit_purpose` varchar(200) DEFAULT NULL COMMENT '来访目的',
  `room_id` bigint NOT NULL COMMENT '访问房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `visit_date` date NOT NULL COMMENT '访问日期',
  `planned_arrival` datetime DEFAULT NULL COMMENT '预计到达时间',
  `planned_departure` datetime DEFAULT NULL COMMENT '预计离开时间',
  `actual_arrival` datetime DEFAULT NULL COMMENT '实际到达时间',
  `actual_departure` datetime DEFAULT NULL COMMENT '实际离开时间',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已批准，ARRIVED-已到达，DEPARTED-已离开，REJECTED-已拒绝，EXPIRED-已过期',
  `qr_code` varchar(100) DEFAULT NULL COMMENT '二维码',
  `guard_id` bigint DEFAULT NULL COMMENT '登记保安ID',
  `remark` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_visitor_phone` (`visitor_phone`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_visit_date` (`visit_date`),
  KEY `idx_status` (`status`),
  KEY `idx_qr_code` (`qr_code`),
  CONSTRAINT `fk_visitor_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `fk_visitor_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客表';

-- ====================================
-- 消息通知相关表
-- ====================================

-- 消息表
CREATE TABLE `message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一标识ID',
    `title` varchar(200) NOT NULL COMMENT '消息标题',
    `content` text NOT NULL COMMENT '消息正文内容',
    `message_type` varchar(50) NOT NULL COMMENT '消息类型分类：SYSTEM=系统消息，SERVICE=服务消息，NOTICE=公告消息',
    `level` varchar(20) DEFAULT 'INFO' COMMENT '消息重要程度：INFO=普通信息，WARNING=警告消息，ERROR=错误消息',
    `sender_id` bigint DEFAULT NULL COMMENT '发送人用户ID',
    `receiver_id` bigint NOT NULL COMMENT '接收人用户ID',
    `is_read` tinyint DEFAULT 0 COMMENT '阅读状态标记：0=未读消息，1=已读消息',
    `business_id` bigint DEFAULT NULL COMMENT '关联的业务数据ID',
    `business_type` varchar(50) DEFAULT NULL COMMENT '关联的业务类型：WORK_ORDER=工单业务，BILL=账单业务，NOTICE=公告业务',
    `icon` varchar(200) DEFAULT NULL COMMENT '消息图标路径',
    `icon_bg` varchar(200) DEFAULT NULL COMMENT '图标背景色样式',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '消息最后更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '软删除标记：0=正常数据，1=已删除数据',
    PRIMARY KEY (`id`),
    KEY `idx_receiver_type` (`receiver_id`, `message_type`),
    KEY `idx_receiver_read` (`receiver_id`, `is_read`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_business` (`business_id`, `business_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息中心数据表';

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ====================================
-- 插入基础数据
-- ====================================

-- 插入角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
(2, '物业管理员', 'ADMIN', '物业管理员，负责日常管理工作'),
(3, '业主', 'OWNER', '小区业主用户'),
(4, '保安', 'GUARD', '小区保安人员'),
(5, '维修工', 'WORKER', '维修工作人员');

-- 插入用户数据（密码统一为123456）
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
(1000, 'admin', '123456', '系统管理员', '13800000001', 'admin@property.com', 'ADMIN', 1),
(1001, 'manager', '123456', '物业经理', '13800000002', 'manager@property.com', 'ADMIN', 1),
(1002, 'owner001', '123456', '张三', '13800000101', 'zhangsan@example.com', 'OWNER', 1),
(1003, 'owner002', '123456', '李四', '13800000102', 'lisi@example.com', 'OWNER', 1),
(1004, 'owner003', '123456', '王五', '13800000103', 'wangwu@example.com', 'OWNER', 1),
(1005, 'guard001', '123456', '保安小刘', '13800000201', 'guard001@property.com', 'GUARD', 1),
(1006, 'guard002', '123456', '保安小赵', '13800000202', 'guard002@property.com', 'GUARD', 1),
(1007, 'worker001', '123456', '维修师傅老张', '13800000301', 'worker001@property.com', 'WORKER', 1),
(1008, 'worker002', '123456', '维修师傅老李', '13800000302', 'worker002@property.com', 'WORKER', 1);

-- 插入用户角色关联数据
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1000, 1), -- admin - 超级管理员
(1001, 2), -- manager - 物业管理员
(1002, 3), -- owner001 - 业主
(1003, 3), -- owner002 - 业主
(1004, 3), -- owner003 - 业主
(1005, 4), -- guard001 - 保安
(1006, 4), -- guard002 - 保安
(1007, 5), -- worker001 - 维修工
(1008, 5); -- worker002 - 维修工

-- 插入楼栋数据
INSERT INTO `building` (`id`, `building_name`, `building_no`, `total_floors`, `total_units`, `total_rooms`, `address`) VALUES
(1, '1号楼', 'A001', 18, 4, 144, '智慧小区1号楼'),
(2, '2号楼', 'A002', 18, 4, 144, '智慧小区2号楼'),
(3, '3号楼', 'A003', 15, 3, 90, '智慧小区3号楼');

-- 插入单元数据
INSERT INTO `unit` (`id`, `building_id`, `unit_name`, `unit_no`, `total_floors`, `total_rooms`) VALUES
(1, 1, '1单元', '1-1', 18, 36),
(2, 1, '2单元', '1-2', 18, 36),
(3, 1, '3单元', '1-3', 18, 36),
(4, 1, '4单元', '1-4', 18, 36),
(5, 2, '1单元', '2-1', 18, 36),
(6, 2, '2单元', '2-2', 18, 36),
(7, 2, '3单元', '2-3', 18, 36),
(8, 2, '4单元', '2-4', 18, 36),
(9, 3, '1单元', '3-1', 15, 30),
(10, 3, '2单元', '3-2', 15, 30),
(11, 3, '3单元', '3-3', 15, 30);

-- 插入房屋数据
INSERT INTO `room` (`id`, `building_id`, `unit_id`, `room_no`, `floor`, `room_type`, `area`, `status`, `property_fee`) VALUES
(1, 1, 2, '301', 3, 'RESIDENTIAL', 89.50, 'OCCUPIED', 150.00),
(2, 1, 2, '302', 3, 'RESIDENTIAL', 92.30, 'VACANT', 155.00),
(3, 2, 5, '502', 5, 'RESIDENTIAL', 120.00, 'OCCUPIED', 180.00),
(4, 2, 6, '801', 8, 'RESIDENTIAL', 95.20, 'OCCUPIED', 160.00),
(5, 3, 9, '101', 1, 'COMMERCIAL', 150.00, 'OCCUPIED', 300.00),
(6, 1, 1, '1801', 18, 'RESIDENTIAL', 105.50, 'VACANT', 170.00),
(7, 2, 7, '1205', 12, 'RESIDENTIAL', 88.80, 'OCCUPIED', 145.00),
(8, 3, 10, '601', 6, 'RESIDENTIAL', 96.00, 'OCCUPIED', 165.00);

-- 插入用户房产关系数据
INSERT INTO `owner_room` (`user_id`, `room_id`, `relation_type`, `start_date`, `status`, `remark`) VALUES
(1002, 1, 'OWNER', '2023-01-01', 1, '购买的第一套房产'),
(1002, 3, 'OWNER', '2023-06-01', 1, '投资房产'),
(1003, 4, 'OWNER', '2023-03-01', 1, '自住房产'),
(1004, 5, 'OWNER', '2023-01-01', 1, '商用房产'),
(1005, 7, 'OWNER', '2023-02-01', 1, '婚房'),
(1006, 8, 'OWNER', '2023-04-01', 1, '投资房产');

-- 设置自增ID起始值
ALTER TABLE `building` AUTO_INCREMENT = 4;
ALTER TABLE `unit` AUTO_INCREMENT = 12;
ALTER TABLE `room` AUTO_INCREMENT = 9;
ALTER TABLE `owner_room` AUTO_INCREMENT = 7;

-- ====================================
-- 数据库创建完成
-- ====================================

-- 显示创建结果
SELECT '=== 数据库重建完成 ===' as info;
SELECT COUNT(*) as user_count FROM sys_user WHERE deleted = 0;
SELECT COUNT(*) as building_count FROM building WHERE deleted = 0;
SELECT COUNT(*) as room_count FROM room WHERE deleted = 0;
SELECT COUNT(*) as owner_room_count FROM owner_room WHERE deleted = 0;

-- 物业管理系统数据库表结构 - 第二部分

-- ====================================
-- 社区相关表
-- ====================================

-- 公告表
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `type` varchar(20) NOT NULL COMMENT '公告类型：NOTICE-通知，ACTIVITY-活动，MAINTENANCE-维护，EMERGENCY-紧急',
  `priority` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：LOW-低，NORMAL-普通，HIGH-高',
  `status` varchar(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，EXPIRED-已过期',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `read_count` int NOT NULL DEFAULT '0' COMMENT '阅读次数',
  `images` text COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_announcement_publisher` (`publisher_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  CONSTRAINT `fk_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 活动表
CREATE TABLE `activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(200) NOT NULL COMMENT '活动标题',
  `description` text COMMENT '活动描述',
  `content` text COMMENT '活动详情',
  `organizer_id` bigint NOT NULL COMMENT '组织者ID',
  `location` varchar(200) DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_participants` int DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int NOT NULL DEFAULT '0' COMMENT '当前参与人数',
  `registration_start` datetime DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` datetime DEFAULT NULL COMMENT '报名结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'PLANNED' COMMENT '状态：PLANNED-计划中，REGISTRATION-报名中，ONGOING-进行中，COMPLETED-已完成，CANCELLED-已取消',
  `is_free` tinyint NOT NULL DEFAULT '1' COMMENT '是否免费：0-收费，1-免费',
  `fee` decimal(10,2) DEFAULT NULL COMMENT '活动费用',
  `images` text COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_activity_organizer` (`organizer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  CONSTRAINT `fk_activity_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 活动报名表
CREATE TABLE `activity_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `participants` int NOT NULL DEFAULT '1' COMMENT '参与人数',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` varchar(20) NOT NULL DEFAULT 'REGISTERED' COMMENT '状态：REGISTERED-已报名，CONFIRMED-已确认，CANCELLED-已取消',
  `registration_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`,`user_id`),
  KEY `fk_registration_user` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_registration_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`),
  CONSTRAINT `fk_registration_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- ====================================
-- 访客管理表
-- ====================================

-- 访客预约表
CREATE TABLE `visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访客ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '访客电话',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `room_id` bigint DEFAULT NULL COMMENT '访问房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `owner_phone` varchar(20) DEFAULT NULL COMMENT '业主电话',
  `visit_purpose` varchar(200) DEFAULT NULL COMMENT '访问目的',
  `planned_arrival` datetime NOT NULL COMMENT '预计到达时间',
  `planned_departure` datetime DEFAULT NULL COMMENT '预计离开时间',
  `actual_arrival` datetime DEFAULT NULL COMMENT '实际到达时间',
  `actual_departure` datetime DEFAULT NULL COMMENT '实际离开时间',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已批准，REJECTED-已拒绝，ARRIVED-已到达，DEPARTED-已离开，EXPIRED-已过期',
  `qr_code` varchar(255) DEFAULT NULL COMMENT '二维码地址',
  `guard_id` bigint DEFAULT NULL COMMENT '接待保安ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_visitor_room` (`room_id`),
  KEY `fk_visitor_owner` (`owner_id`),
  KEY `fk_visitor_guard` (`guard_id`),
  KEY `idx_status` (`status`),
  KEY `idx_planned_arrival` (`planned_arrival`),
  CONSTRAINT `fk_visitor_guard` FOREIGN KEY (`guard_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_visitor_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_visitor_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客预约表';

-- ====================================
-- 系统管理表
-- ====================================

-- 系统配置表
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(20) NOT NULL DEFAULT 'STRING' COMMENT '配置类型：STRING-字符串，NUMBER-数字，BOOLEAN-布尔，JSON-JSON对象',
  `description` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `is_system` tinyint NOT NULL DEFAULT '0' COMMENT '是否系统配置：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) NOT NULL COMMENT '操作',
  `method` varchar(200) NOT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长（毫秒）',
  `ip` varchar(64) NOT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 文件上传记录表
CREATE TABLE `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) NOT NULL COMMENT '文件类型',
  `upload_user_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_file_user` (`upload_user_id`),
  KEY `idx_file_type` (`file_type`),
  CONSTRAINT `fk_file_user` FOREIGN KEY (`upload_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';





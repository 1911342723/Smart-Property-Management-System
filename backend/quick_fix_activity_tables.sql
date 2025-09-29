-- 快速修复：创建活动表
USE property_management;

-- 创建活动表
CREATE TABLE IF NOT EXISTS `activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(200) NOT NULL COMMENT '活动标题',
  `description` text COMMENT '活动描述',
  `content` text COMMENT '活动详情',
  `summary` varchar(500) DEFAULT NULL COMMENT '活动摘要',
  `type` varchar(50) DEFAULT NULL COMMENT '活动类型',
  `organizer_id` bigint NOT NULL COMMENT '组织者ID',
  `location` varchar(200) DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_participants` int DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int NOT NULL DEFAULT '0' COMMENT '当前参与人数',
  `registration_start` datetime DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` datetime DEFAULT NULL COMMENT '报名结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'PLANNED' COMMENT '状态',
  `is_free` tinyint NOT NULL DEFAULT '1' COMMENT '是否免费',
  `fee` decimal(10,2) DEFAULT NULL COMMENT '活动费用',
  `image_url` varchar(500) DEFAULT NULL COMMENT '活动主图',
  `images` text COMMENT '图片地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 创建活动报名表
CREATE TABLE IF NOT EXISTS `activity_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `participants` int NOT NULL DEFAULT '1' COMMENT '参与人数',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` varchar(20) NOT NULL DEFAULT 'REGISTERED' COMMENT '状态',
  `registration_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- 插入测试数据
INSERT IGNORE INTO `activity` VALUES
(1, '春节联欢晚会', '社区居民新春联欢活动，精彩节目等您参与', '一年一度的春节联欢晚会即将开始！', '社区居民新春联欢活动', 'ENTERTAINMENT', 1001, '社区活动中心', '2025-02-10 19:00:00', '2025-02-10 22:00:00', 100, 23, '2025-01-15 09:00:00', '2025-02-08 18:00:00', 'UPCOMING', 1, NULL, '/static/images/activity1.jpg', NULL, NOW(), NOW(), 0),
(2, '羽毛球比赛', '社区羽毛球友谊赛，现在开始报名', '社区羽毛球友谊赛正在火热报名中！', '社区羽毛球友谊赛', 'SPORTS', 1001, '社区体育馆', '2025-01-18 14:00:00', '2025-01-18 18:00:00', 32, 15, '2025-01-10 09:00:00', '2025-01-17 18:00:00', 'REGISTRATION', 1, NULL, '/static/images/activity2.jpg', NULL, NOW(), NOW(), 0);

SELECT 'Tables created and data inserted successfully!' as Result;




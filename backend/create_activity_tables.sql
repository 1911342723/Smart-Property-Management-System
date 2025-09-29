-- 创建活动相关表的脚本
-- 如果表已存在则跳过，避免数据丢失

USE property_management;

-- 检查并创建活动表
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
  `status` varchar(20) NOT NULL DEFAULT 'PLANNED' COMMENT '状态：PLANNED-计划中，REGISTRATION-报名中，UPCOMING-即将开始，ONGOING-进行中，COMPLETED-已完成，CANCELLED-已取消',
  `is_free` tinyint NOT NULL DEFAULT '1' COMMENT '是否免费：0-收费，1-免费',
  `fee` decimal(10,2) DEFAULT NULL COMMENT '活动费用',
  `image_url` varchar(500) DEFAULT NULL COMMENT '活动主图',
  `images` text COMMENT '图片地址（JSON数组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_activity_organizer` (`organizer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 检查并创建活动报名表
CREATE TABLE IF NOT EXISTS `activity_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `participants` int NOT NULL DEFAULT '1' COMMENT '参与人数',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `remarks` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` varchar(20) NOT NULL DEFAULT 'REGISTERED' COMMENT '状态：REGISTERED-已报名，CONFIRMED-已确认，CANCELLED-已取消',
  `registration_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`,`user_id`,`deleted`),
  KEY `fk_registration_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_registration_time` (`registration_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- 插入一些测试活动数据
INSERT IGNORE INTO `activity` (`id`, `title`, `description`, `content`, `summary`, `type`, `organizer_id`, `location`, `start_time`, `end_time`, `max_participants`, `current_participants`, `registration_start`, `registration_end`, `status`, `is_free`, `fee`, `image_url`) VALUES
(1, '春节联欢晚会', '社区居民新春联欢活动，精彩节目等您参与', '一年一度的春节联欢晚会即将开始！我们为大家准备了精彩的文艺表演、有趣的互动游戏和丰富的奖品。欢迎全家老少一起参加，共度美好时光。活动现场还有免费的茶点和小食供应，让我们一起迎接新年的到来！', '社区居民新春联欢活动，精彩节目等您参与，欢迎大家踊跃参加', 'ENTERTAINMENT', 1001, '社区活动中心', '2025-02-10 19:00:00', '2025-02-10 22:00:00', 100, 23, '2025-01-15 09:00:00', '2025-02-08 18:00:00', 'UPCOMING', 1, NULL, '/static/images/activity1.jpg'),
(2, '羽毛球比赛', '社区羽毛球友谊赛，现在开始报名', '社区羽毛球友谊赛正在火热报名中！无论您是羽毛球新手还是高手，都可以参与其中。比赛采用友谊第一、比赛第二的原则，重在参与和交流。我们将根据报名情况分组进行比赛，确保每位参与者都能享受运动的乐趣。', '社区羽毛球友谊赛，欢迎羽毛球爱好者参与', 'SPORTS', 1001, '社区体育馆', '2025-01-18 14:00:00', '2025-01-18 18:00:00', 32, 15, '2025-01-10 09:00:00', '2025-01-17 18:00:00', 'REGISTRATION', 1, NULL, '/static/images/activity2.jpg'),
(3, '健康知识讲座', '邀请专业医师为社区居民讲解健康养生知识', '本次健康知识讲座将邀请三甲医院的专业医师为大家讲解冬季养生保健知识，包括饮食调理、运动保健、疾病预防等方面的内容。讲座结束后还有免费的健康咨询环节，欢迎有需要的居民朋友参加。', '邀请专业医师为社区居民讲解健康养生知识', 'EDUCATION', 1001, '社区会议室', '2025-01-20 15:00:00', '2025-01-20 17:00:00', 50, 8, '2025-01-12 09:00:00', '2025-01-19 18:00:00', 'REGISTRATION', 1, NULL, '/static/images/activity3.jpg');

-- 显示创建结果
SELECT 'Activity tables created successfully!' as Result;




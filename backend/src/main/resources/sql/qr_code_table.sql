-- 二维码数据表
CREATE TABLE `visitor_qr_code` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) NOT NULL COMMENT '访客手机号',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `owner_phone` varchar(20) NOT NULL COMMENT '业主电话',
  `visit_purpose` varchar(200) DEFAULT NULL COMMENT '访问目的',
  `planned_arrival` datetime NOT NULL COMMENT '预计到达时间',
  `planned_departure` datetime DEFAULT NULL COMMENT '预计离开时间',
  `qr_code` varchar(255) NOT NULL COMMENT '二维码唯一标识',
  `qr_content` text NOT NULL COMMENT '二维码内容（JSON）',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-有效，USED-已使用，EXPIRED-已过期',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `guard_id` bigint DEFAULT NULL COMMENT '验证保安ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qr_code` (`qr_code`),
  KEY `idx_phone` (`phone`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`),
  KEY `idx_planned_arrival` (`planned_arrival`),
  CONSTRAINT `fk_qr_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_qr_guard` FOREIGN KEY (`guard_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客二维码表';





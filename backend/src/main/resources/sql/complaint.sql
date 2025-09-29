-- 投诉建议表
CREATE TABLE IF NOT EXISTS `complaint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `complaint_no` varchar(32) NOT NULL COMMENT '投诉编号',
  `complainant_id` bigint(20) NOT NULL COMMENT '投诉人ID',
  `room_id` bigint(20) COMMENT '房屋ID',
  `complaint_type` varchar(20) NOT NULL COMMENT '投诉类型：NOISE-噪音投诉，HYGIENE-环境卫生，FACILITY-设施维护，SERVICE-物业服务，SECURITY-安全问题，OTHER-其他问题',
  `title` varchar(200) NOT NULL COMMENT '投诉标题',
  `content` text NOT NULL COMMENT '投诉内容',
  `images` text COMMENT '相关图片（JSON数组）',
  `urgency_level` varchar(20) DEFAULT 'NORMAL' COMMENT '紧急程度：LOW-低，NORMAL-普通，HIGH-高，URGENT-紧急',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，ASSIGNED-已分配，PROCESSING-处理中，RESOLVED-已解决，CLOSED-已关闭',
  `handler_id` bigint(20) COMMENT '处理人ID',
  `handle_result` text COMMENT '处理结果',
  `handle_time` datetime COMMENT '处理时间',
  `satisfaction_rating` int(1) COMMENT '满意度评分（1-5分）',
  `feedback` text COMMENT '评价反馈',
  `remark` varchar(500) COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
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

-- 插入测试投诉建议数据
INSERT INTO `complaint` (`complaint_no`, `complainant_id`, `room_id`, `complaint_type`, `title`, `content`, `urgency_level`, `status`) VALUES
('CP202412290001', 1002, 1, 'NOISE', '楼上住户深夜噪音', '楼上住户经常在深夜制造噪音，包括拖拽家具、大声说话等，严重影响休息。希望物业能够协调处理。', 'HIGH', 'PENDING'),
('CP202412290002', 1002, 1, 'FACILITY', '电梯故障频繁', '6号楼电梯经常出现故障，按钮失灵、门关不严等问题，给业主出行带来很大不便，存在安全隐患。', 'HIGH', 'PROCESSING'),
('CP202412290003', 1002, 1, 'HYGIENE', '垃圾清理不及时', '小区垃圾桶经常满溢，垃圾清理不及时，特别是夏天容易产生异味，影响小区环境。', 'NORMAL', 'RESOLVED'),
('CP202412290004', 1002, 1, 'SERVICE', '门禁卡办理效率低', '门禁卡补办手续繁琐，等待时间过长，建议简化流程，提高办事效率。', 'NORMAL', 'CLOSED'),
('CP202412290005', 1002, 1, 'SECURITY', '小区监控设备故障', '小区部分监控摄像头出现故障，存在监控盲区，建议尽快维修更换。', 'HIGH', 'ASSIGNED');

-- 更新部分投诉记录的处理信息
UPDATE `complaint` SET 
  `handler_id` = 1001, 
  `status` = 'PROCESSING' 
WHERE `complaint_no` = 'CP202412290002';

UPDATE `complaint` SET 
  `handler_id` = 1001, 
  `handle_result` = '已安排保洁人员增加垃圾清理频次，每日清理两次，并加强监督管理。', 
  `handle_time` = '2024-12-27 16:30:00',
  `status` = 'RESOLVED'
WHERE `complaint_no` = 'CP202412290003';

UPDATE `complaint` SET 
  `handler_id` = 1001, 
  `handle_result` = '已优化门禁卡办理流程，现在可通过物业APP在线申请，1个工作日内完成制卡。', 
  `handle_time` = '2024-12-26 10:15:00',
  `satisfaction_rating` = 5,
  `feedback` = '处理很及时，新流程确实方便多了，满意！',
  `status` = 'CLOSED'
WHERE `complaint_no` = 'CP202412290004';

UPDATE `complaint` SET 
  `handler_id` = 1001, 
  `status` = 'ASSIGNED'
WHERE `complaint_no` = 'CP202412290005';







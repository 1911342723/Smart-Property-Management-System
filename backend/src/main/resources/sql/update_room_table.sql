-- 更新房屋关系设计，区分业主和租户

USE property_management;

-- 删除room表的owner_id字段（如果存在），使用关系表管理
-- ALTER TABLE `room` DROP COLUMN IF EXISTS `owner_id`;

-- 确保owner_room表存在且结构正确
CREATE TABLE IF NOT EXISTS `owner_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `relation_type` varchar(20) NOT NULL COMMENT '关系类型：OWNER-业主，TENANT-租户',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `fk_owner_room_user` (`user_id`),
  KEY `fk_owner_room_room` (`room_id`),
  KEY `idx_relation_type` (`relation_type`),
  UNIQUE KEY `uk_user_room_relation` (`user_id`, `room_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户房产关联表';

-- 插入测试楼栋数据（如果不存在）
INSERT IGNORE INTO `building` (`id`, `building_name`, `building_no`, `total_floors`, `total_units`, `total_rooms`, `address`, `status`) VALUES
(1, '1号楼', 'A001', 18, 4, 144, '智慧小区1号楼', 1),
(2, '2号楼', 'A002', 18, 4, 144, '智慧小区2号楼', 1),
(3, '3号楼', 'A003', 15, 3, 90, '智慧小区3号楼', 1);

-- 插入测试单元数据
INSERT IGNORE INTO `unit` (`id`, `building_id`, `unit_name`, `unit_no`, `total_floors`, `total_rooms`, `status`) VALUES
(1, 1, '1单元', '1-1', 18, 36, 1),
(2, 1, '2单元', '1-2', 18, 36, 1),
(3, 1, '3单元', '1-3', 18, 36, 1),
(4, 1, '4单元', '1-4', 18, 36, 1),
(5, 2, '1单元', '2-1', 18, 36, 1),
(6, 2, '2单元', '2-2', 18, 36, 1),
(7, 2, '3单元', '2-3', 18, 36, 1),
(8, 2, '4单元', '2-4', 18, 36, 1),
(9, 3, '1单元', '3-1', 15, 30, 1),
(10, 3, '2单元', '3-2', 15, 30, 1),
(11, 3, '3单元', '3-3', 15, 30, 1);

-- 插入测试房屋数据（不包含owner_id，使用关系表管理）
INSERT IGNORE INTO `room` (`id`, `building_id`, `unit_id`, `room_no`, `floor`, `room_type`, `area`, `status`, `property_fee`) VALUES
(1, 1, 2, '301', 3, 'RESIDENTIAL', 89.50, 'OCCUPIED', 150.00),
(2, 1, 2, '302', 3, 'RESIDENTIAL', 92.30, 'VACANT', 155.00),
(3, 2, 5, '502', 5, 'RESIDENTIAL', 120.00, 'OCCUPIED', 180.00),
(4, 2, 6, '801', 8, 'RESIDENTIAL', 95.20, 'OCCUPIED', 160.00),
(5, 3, 9, '101', 1, 'COMMERCIAL', 150.00, 'OCCUPIED', 300.00),
(6, 1, 1, '1801', 18, 'RESIDENTIAL', 105.50, 'VACANT', 170.00),
(7, 2, 7, '1205', 12, 'RESIDENTIAL', 88.80, 'OCCUPIED', 145.00),
(8, 3, 10, '601', 6, 'RESIDENTIAL', 96.00, 'OCCUPIED', 165.00);

-- 清空并重新插入用户房产关系数据
DELETE FROM `owner_room` WHERE id > 0;

-- 插入用户房产关系（1002是业主，拥有301和502）
INSERT INTO `owner_room` (`user_id`, `room_id`, `relation_type`, `start_date`, `status`) VALUES
(1002, 1, 'OWNER', '2023-01-01', 1),  -- 张三拥有1号楼2单元301
(1002, 3, 'OWNER', '2023-06-01', 1),  -- 张三拥有2号楼1单元502
(1003, 4, 'OWNER', '2023-03-01', 1),  -- 李四拥有2号楼2单元801
(1004, 5, 'OWNER', '2023-01-01', 1),  -- 王五拥有3号楼1单元101（商用）
(1005, 7, 'OWNER', '2023-02-01', 1),  -- 赵六拥有2号楼3单元1205
(1006, 8, 'OWNER', '2023-04-01', 1);  -- 钱七拥有3号楼2单元601

-- 如果有租户关系，可以这样添加（示例）
-- INSERT INTO `owner_room` (`user_id`, `room_id`, `relation_type`, `start_date`, `end_date`, `status`) VALUES
-- (1007, 3, 'TENANT', '2024-01-01', '2024-12-31', 1);  -- 某人租住502

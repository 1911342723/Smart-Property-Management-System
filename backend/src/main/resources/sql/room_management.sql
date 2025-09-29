-- 房屋管理相关表结构和测试数据

-- 如果表不存在，创建unit表
CREATE TABLE IF NOT EXISTS `unit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `building_id` bigint(20) NOT NULL COMMENT '楼栋ID',
  `unit_name` varchar(50) NOT NULL COMMENT '单元名称',
  `unit_no` varchar(20) NOT NULL COMMENT '单元编号',
  `total_floors` int(11) NOT NULL COMMENT '总楼层数',
  `rooms_per_floor` int(11) NOT NULL COMMENT '每层房间数',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_unit_no` (`unit_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单元表';

-- 更新room表结构，添加owner_id字段
ALTER TABLE `room` 
ADD COLUMN IF NOT EXISTS `owner_id` bigint(20) DEFAULT NULL COMMENT '业主ID' AFTER `property_fee`;

-- 创建索引
CREATE INDEX IF NOT EXISTS `idx_room_owner_id` ON `room` (`owner_id`);

-- 插入测试楼栋数据（如果不存在）
INSERT IGNORE INTO `building` (`id`, `building_name`, `building_no`, `total_floors`, `total_units`, `total_rooms`, `address`, `status`) VALUES
(1, '1号楼', 'A001', 18, 4, 144, '智慧小区1号楼', 1),
(2, '2号楼', 'A002', 18, 4, 144, '智慧小区2号楼', 1),
(3, '3号楼', 'A003', 15, 3, 90, '智慧小区3号楼', 1);

-- 插入测试单元数据
INSERT IGNORE INTO `unit` (`id`, `building_id`, `unit_name`, `unit_no`, `total_floors`, `rooms_per_floor`, `status`) VALUES
(1, 1, '1单元', '1-1', 18, 2, 1),
(2, 1, '2单元', '1-2', 18, 2, 1),
(3, 1, '3单元', '1-3', 18, 2, 1),
(4, 1, '4单元', '1-4', 18, 2, 1),
(5, 2, '1单元', '2-1', 18, 2, 1),
(6, 2, '2单元', '2-2', 18, 2, 1),
(7, 2, '3单元', '2-3', 18, 2, 1),
(8, 2, '4单元', '2-4', 18, 2, 1),
(9, 3, '1单元', '3-1', 15, 2, 1),
(10, 3, '2单元', '3-2', 15, 2, 1),
(11, 3, '3单元', '3-3', 15, 2, 1);

-- 插入测试房屋数据（如果不存在）
INSERT IGNORE INTO `room` (`id`, `building_id`, `unit_id`, `room_no`, `floor`, `room_type`, `area`, `status`, `property_fee`, `owner_id`) VALUES
(1, 1, 2, '301', 3, 'RESIDENTIAL', 89.50, 'OCCUPIED', 150.00, 1),
(2, 1, 2, '302', 3, 'RESIDENTIAL', 92.30, 'VACANT', 155.00, NULL),
(3, 2, 5, '502', 5, 'RESIDENTIAL', 120.00, 'RENTED', 180.00, 1),
(4, 2, 6, '801', 8, 'RESIDENTIAL', 95.20, 'OCCUPIED', 160.00, 2),
(5, 3, 9, '101', 1, 'COMMERCIAL', 150.00, 'OCCUPIED', 300.00, 3),
(6, 1, 1, '1801', 18, 'RESIDENTIAL', 105.50, 'VACANT', 170.00, NULL),
(7, 2, 7, '1205', 12, 'RESIDENTIAL', 88.80, 'OCCUPIED', 145.00, 4),
(8, 3, 10, '601', 6, 'RESIDENTIAL', 96.00, 'RENTED', 165.00, 5);

-- 更新已有用户的房屋关联（示例数据）
UPDATE `room` SET `owner_id` = 1 WHERE `id` IN (1, 3);
UPDATE `room` SET `owner_id` = 2 WHERE `id` = 4;
UPDATE `room` SET `owner_id` = 3 WHERE `id` = 5;
UPDATE `room` SET `owner_id` = 4 WHERE `id` = 7;
UPDATE `room` SET `owner_id` = 5 WHERE `id` = 8;

-- 创建房屋与业主关联的视图（用于查询优化）
CREATE OR REPLACE VIEW `v_room_owner` AS
SELECT 
    r.id as room_id,
    r.building_id,
    r.unit_id,
    r.room_no,
    r.floor,
    r.room_type,
    r.area,
    r.status as room_status,
    r.property_fee,
    r.owner_id,
    b.building_name,
    u.unit_name,
    s.username,
    s.real_name as owner_name,
    s.phone as owner_phone,
    s.email as owner_email
FROM room r
LEFT JOIN building b ON r.building_id = b.id
LEFT JOIN unit u ON r.unit_id = u.id
LEFT JOIN sys_user s ON r.owner_id = s.id
WHERE r.owner_id IS NOT NULL;





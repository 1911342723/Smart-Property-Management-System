-- 安全重建所有房屋管理相关表
-- 先删除外键约束，再删除表，最后重建

USE property_management;

-- 1. 禁用外键检查（临时）
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 删除所有相关表（现在可以安全删除）
DROP TABLE IF EXISTS `owner_room`;
DROP TABLE IF EXISTS `work_order_log`;
DROP TABLE IF EXISTS `work_order`;
DROP TABLE IF EXISTS `visitor`;
DROP TABLE IF EXISTS `room`;
DROP TABLE IF EXISTS `unit`;
DROP TABLE IF EXISTS `building`;

-- 3. 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 4. 重新创建楼栋表
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
  UNIQUE KEY `uk_building_no` (`building_no`),
  KEY `idx_building_name` (`building_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋表';

-- 5. 重新创建单元表
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
  KEY `idx_building_id` (`building_id`),
  KEY `idx_unit_no` (`unit_no`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_unit_building` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单元表';

-- 6. 重新创建房屋表
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
  KEY `idx_building_id` (`building_id`),
  KEY `idx_unit_id` (`unit_id`),
  KEY `idx_room_status` (`status`),
  KEY `idx_room_type` (`room_type`),
  CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `building` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_room_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房屋表';

-- 7. 创建用户房产关系表
CREATE TABLE `owner_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `room_id` bigint NOT NULL COMMENT '房屋ID',
  `relation_type` varchar(20) NOT NULL COMMENT '关系类型：OWNER-业主，TENANT-租户，FAMILY-家属',
  `start_date` date NOT NULL COMMENT '关系开始日期',
  `end_date` date DEFAULT NULL COMMENT '关系结束日期（租户适用）',
  `contract_no` varchar(50) DEFAULT NULL COMMENT '合同编号（租户适用）',
  `monthly_rent` decimal(10,2) DEFAULT NULL COMMENT '月租金（租户适用）',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '押金（租户适用）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_room_relation` (`user_id`, `room_id`, `relation_type`, `deleted`) COMMENT '用户房屋关系唯一索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_room_id` (`room_id`) COMMENT '房屋ID索引',
  KEY `idx_relation_type` (`relation_type`) COMMENT '关系类型索引',
  KEY `idx_status` (`status`) COMMENT '状态索引',
  CONSTRAINT `fk_owner_room_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_owner_room_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户房产关系表';

-- 8. 重新创建访客表（如果需要）
CREATE TABLE `visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访客ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '访客手机号',
  `visitor_company` varchar(100) DEFAULT NULL COMMENT '访客公司',
  `visit_purpose` varchar(200) DEFAULT NULL COMMENT '来访目的',
  `visit_room_id` bigint NOT NULL COMMENT '访问房屋ID',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `visit_date` date NOT NULL COMMENT '访问日期',
  `visit_time_start` time DEFAULT NULL COMMENT '预计到达时间',
  `visit_time_end` time DEFAULT NULL COMMENT '预计离开时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `actual_departure_time` datetime DEFAULT NULL COMMENT '实际离开时间',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已批准，ARRIVED-已到达，DEPARTED-已离开，REJECTED-已拒绝，EXPIRED-已过期',
  `qr_code` varchar(100) DEFAULT NULL COMMENT '二维码',
  `guard_id` bigint DEFAULT NULL COMMENT '登记保安ID',
  `remark` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_visitor_phone` (`visitor_phone`),
  KEY `idx_visit_room_id` (`visit_room_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_visit_date` (`visit_date`),
  KEY `idx_status` (`status`),
  KEY `idx_qr_code` (`qr_code`),
  CONSTRAINT `fk_visitor_room` FOREIGN KEY (`visit_room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_visitor_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客表';

-- 9. 插入基础数据
-- 插入楼栋数据
INSERT INTO `building` (`id`, `building_name`, `building_no`, `total_floors`, `total_units`, `total_rooms`, `address`, `status`) VALUES
(1, '1号楼', 'A001', 18, 4, 144, '智慧小区1号楼', 1),
(2, '2号楼', 'A002', 18, 4, 144, '智慧小区2号楼', 1),
(3, '3号楼', 'A003', 15, 3, 90, '智慧小区3号楼', 1);

-- 插入单元数据
INSERT INTO `unit` (`id`, `building_id`, `unit_name`, `unit_no`, `total_floors`, `total_rooms`, `status`) VALUES
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

-- 10. 确保测试用户存在
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `status`, `user_type`) VALUES
(1002, 'owner001', '123456', '张三', '13800000101', 'zhangsan@example.com', 1, 'OWNER'),
(1003, 'owner002', '123456', '李四', '13800000102', 'lisi@example.com', 1, 'OWNER'),
(1004, 'owner003', '123456', '王五', '13800000103', 'wangwu@example.com', 1, 'OWNER'),
(1005, 'owner004', '123456', '赵六', '13800000104', 'zhaoliu@example.com', 1, 'OWNER'),
(1006, 'owner005', '123456', '钱七', '13800000105', 'qianqi@example.com', 1, 'OWNER');

-- 11. 插入用户房产关系数据
INSERT INTO `owner_room` (`user_id`, `room_id`, `relation_type`, `start_date`, `status`, `remark`) VALUES
(1002, 1, 'OWNER', '2023-01-01', 1, '购买的第一套房产'),
(1002, 3, 'OWNER', '2023-06-01', 1, '投资房产'),
(1003, 4, 'OWNER', '2023-03-01', 1, '自住房产'),
(1004, 5, 'OWNER', '2023-01-01', 1, '商用房产'),
(1005, 7, 'OWNER', '2023-02-01', 1, '婚房'),
(1006, 8, 'OWNER', '2023-04-01', 1, '投资房产');

-- 12. 重新设置自增ID起始值
ALTER TABLE `building` AUTO_INCREMENT = 4;
ALTER TABLE `unit` AUTO_INCREMENT = 12;
ALTER TABLE `room` AUTO_INCREMENT = 9;
ALTER TABLE `owner_room` AUTO_INCREMENT = 7;
ALTER TABLE `visitor` AUTO_INCREMENT = 1;

-- 13. 验证创建结果
SELECT '=== 数据库重建完成，验证结果 ===' as info;

SELECT '=== 楼栋数据 ===' as info;
SELECT id, building_name, building_no, total_floors, total_units, total_rooms FROM building WHERE deleted = 0;

SELECT '=== 房屋数据 ===' as info;
SELECT r.id, CONCAT(b.building_name, u.unit_name, r.room_no, '室') as address, r.floor, r.room_type, r.area, r.status, r.property_fee
FROM room r
LEFT JOIN building b ON r.building_id = b.id
LEFT JOIN unit u ON r.unit_id = u.id
WHERE r.deleted = 0
ORDER BY r.building_id, r.unit_id, r.room_no;

SELECT '=== 用户房产关系 ===' as info;
SELECT 
    or.id,
    u.real_name as user_name,
    CONCAT(b.building_name, u2.unit_name, r.room_no, '室') as room_address,
    or.relation_type,
    or.start_date,
    or.remark
FROM owner_room or
LEFT JOIN sys_user u ON or.user_id = u.id
LEFT JOIN room r ON or.room_id = r.id
LEFT JOIN building b ON r.building_id = b.id
LEFT JOIN unit u2 ON r.unit_id = u2.id
WHERE or.deleted = 0
ORDER BY or.user_id, or.room_id;





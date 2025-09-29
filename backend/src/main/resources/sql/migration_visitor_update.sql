-- 访客表结构更新迁移脚本
-- 执行时间：2025-09-29

-- 1. 修改room_id字段为可选
ALTER TABLE `visitor` MODIFY COLUMN `room_id` bigint DEFAULT NULL COMMENT '访问房屋ID';

-- 2. 添加owner_phone字段
ALTER TABLE `visitor` ADD COLUMN `owner_phone` varchar(20) DEFAULT NULL COMMENT '业主电话' AFTER `owner_id`;

-- 3. 删除room_id的外键约束（如果存在）
ALTER TABLE `visitor` DROP FOREIGN KEY `fk_visitor_room`;

-- 4. 重新添加room_id的外键约束（允许NULL值）
ALTER TABLE `visitor` ADD CONSTRAINT `fk_visitor_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`);





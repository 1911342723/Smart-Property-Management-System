-- 插入未分配的测试工单（维修工可以接单）
-- 使用 NOW() 确保是今天的数据

-- 删除可能已存在的测试数据（如果order_no重复的话）
DELETE FROM work_order WHERE order_no IN ('WO202509300010', 'WO202509300011', 'WO202509300012');

-- 插入3个未分配的工单
INSERT INTO work_order 
(order_no, title, content, category, priority, status, submitter_id, assignee_id, room_id, images, submit_time, create_time, update_time, deleted) 
VALUES
('WO202509300010', '客厅灯不亮了', '客厅的主灯突然不亮了，请尽快维修', 'REPAIR', 'MEDIUM', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0),
('WO202509300011', '卫生间水龙头漏水', '水龙头一直滴水，浪费水资源', 'REPAIR', 'HIGH', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0),
('WO202509300012', '门锁有点松动', '入户门的锁有点松动，担心安全问题', 'REPAIR', 'LOW', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0);

-- 验证插入结果
SELECT 
  id, order_no, title, priority, status, assignee_id, 
  DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time
FROM work_order 
WHERE category = 'REPAIR' AND status = 'PENDING' AND assignee_id IS NULL AND deleted = 0
ORDER BY create_time DESC;
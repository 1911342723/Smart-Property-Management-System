-- 插入一些未分配的测试工单数据，供维修工接单使用

-- 插入未分配的待处理工单
INSERT INTO work_order (order_no, title, content, category, priority, status, submitter_id, assignee_id, room_id, images, submit_time, create_time, update_time, deleted) VALUES
('WO202509300010', '客厅灯不亮了', '客厅的主灯突然不亮了，可能是灯泡坏了或者开关有问题', 'REPAIR', 'MEDIUM', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0),
('WO202509300011', '卫生间水龙头漏水', '卫生间洗手盆的水龙头一直滴水，需要更换阀芯', 'REPAIR', 'HIGH', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0),
('WO202509300012', '厨房排气扇噪音大', '厨房排气扇运转时噪音很大，可能需要清洗或更换', 'REPAIR', 'LOW', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0),
('WO202509300013', '阳台窗户关不严', '阳台推拉窗关不严，有缝隙，雨天会漏水', 'REPAIR', 'URGENT', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0),
('WO202509300014', '空调不制冷', '客厅空调打开后不制冷，只吹风不降温', 'REPAIR', 'HIGH', 'PENDING', 1002, NULL, 1, '[]', NOW(), NOW(), NOW(), 0);

-- 查询验证
SELECT id, order_no, title, priority, status, assignee_id, submit_time 
FROM work_order 
WHERE category = 'REPAIR' AND status = 'PENDING' AND assignee_id IS NULL AND deleted = 0
ORDER BY submit_time DESC;

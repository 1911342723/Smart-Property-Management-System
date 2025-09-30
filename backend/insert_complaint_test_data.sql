-- ========================================
-- 投诉建议测试数据插入脚本
-- 为业主ID=1002添加各种类型的投诉
-- ========================================

-- 先清除旧的测试数据
DELETE FROM `complaint` WHERE `complainant_id` = 1002;

-- 插入测试投诉数据
INSERT INTO `complaint` (
    `complaint_no`,
    `complainant_id`,
    `room_id`,
    `complaint_type`,
    `title`,
    `content`,
    `images`,
    `urgency_level`,
    `status`,
    `handler_id`,
    `handle_result`,
    `handle_time`,
    `satisfaction_rating`,
    `feedback`,
    `remark`,
    `create_time`,
    `update_time`,
    `deleted`
) VALUES

-- 1. 待处理的噪音投诉
('CP202509300001', 
 1002,              -- 投诉人ID
 1,                 -- 房屋ID
 '噪音扰民',        -- 投诉类型（中文）
 '楼上住户深夜噪音严重',
 '楼上住户经常在晚上11点后制造噪音，包括拖拽家具、大声说话、儿童跑跳等，严重影响家人休息，已经持续一周了，希望物业能够协调处理。',
 '[]',
 'HIGH',            -- 高优先级
 'PENDING',         -- 待处理
 NULL,              -- 未分配处理人
 NULL,
 NULL,
 NULL,
 NULL,
 NULL,
 NOW() - INTERVAL 2 HOUR,    -- 2小时前提交
 NOW() - INTERVAL 2 HOUR,
 0),

-- 2. 处理中的环境卫生投诉
('CP202509290002', 
 1002,
 1,
 '环境卫生',
 '小区垃圾清运不及时',
 '小区2号楼旁边的垃圾桶经常溢出，已经好几天没有清理了，产生异味且有蚊虫滋生，影响居民生活环境。',
 '[]',
 'MEDIUM',          -- 中等优先级
 'PROCESSING',      -- 处理中
 1000,              -- 分配给管理员
 NULL,
 NULL,
 NULL,
 NULL,
 '已安排保洁人员增加清理频次',
 NOW() - INTERVAL 1 DAY,     -- 1天前提交
 NOW() - INTERVAL 1 DAY,
 0),

-- 3. 已解决的设施维护投诉
('CP202509280001', 
 1002,
 1,
 '设施维护',
 '电梯故障频繁',
 '1号楼的3号电梯最近经常出现故障，有时会突然停止，有时会开门不畅，存在安全隐患，请尽快检修。',
 '[]',
 'URGENT',          -- 紧急
 'RESOLVED',        -- 已解决
 1000,              -- 处理人
 '已联系电梯维保公司进行全面检修，更换了老化的控制模块，问题已解决。电梯现在运行正常，后续会加强日常巡检。',
 NOW() - INTERVAL 1 DAY + INTERVAL 20 HOUR,  -- 昨天解决
 5,                 -- 满意度评分5分
 '处理很及时，电梯现在运行很平稳，感谢物业的快速响应！',
 NULL,
 NOW() - INTERVAL 2 DAY,     -- 2天前提交
 NOW() - INTERVAL 1 DAY + INTERVAL 20 HOUR,
 0),

-- 4. 已关闭的停车问题投诉
('CP202509270001', 
 1002,
 1,
 '停车问题',
 '私家车占用消防通道',
 '地下车库B区经常有车辆占用消防通道停放，存在安全隐患，建议物业加强管理。',
 '[]',
 'HIGH',
 'CLOSED',          -- 已关闭
 1000,
 '已在地下车库增设禁停标识和警示牌，并安排安保人员定期巡查。对违规停车的业主进行了告知和教育，目前情况已明显改善。',
 NOW() - INTERVAL 2 DAY,
 4,                 -- 满意度评分4分
 '现在基本没有车辆乱停了，希望继续保持。',
 NULL,
 NOW() - INTERVAL 3 DAY,     -- 3天前提交
 NOW() - INTERVAL 2 DAY,
 0),

-- 5. 待处理的安全问题投诉
('CP202509290003', 
 1002,
 1,
 '安全问题',
 '小区门禁系统失灵',
 '小区东门的门禁系统经常失灵，有时刷卡无反应，导致业主进出不便，也存在安全隐患。',
 '[]',
 'HIGH',
 'PENDING',
 NULL,
 NULL,
 NULL,
 NULL,
 NULL,
 NULL,
 NOW() - INTERVAL 5 HOUR,    -- 5小时前提交
 NOW() - INTERVAL 5 HOUR,
 0),

-- 6. 已分配的服务态度投诉
('CP202509290004', 
 1002,
 1,
 '服务态度',
 '物业客服态度不佳',
 '昨天联系物业客服咨询问题时，客服人员态度冷淡，回答敷衍，希望物业能加强员工服务意识培训。',
 '[]',
 'LOW',
 'ASSIGNED',        -- 已分配
 1000,              -- 已分配处理人
 NULL,
 NULL,
 NULL,
 NULL,
 NULL,
 NOW() - INTERVAL 10 HOUR,   -- 10小时前提交
 NOW() - INTERVAL 9 HOUR,    -- 9小时前分配
 0),

-- 7. 其他类型投诉（处理中）
('CP202509280002', 
 1002,
 1,
 '其他',
 '小区快递柜数量不足',
 '小区快递柜经常爆满，很多快递只能放在地上，既不方便也不安全，建议增加快递柜数量。',
 '[]',
 'MEDIUM',
 'PROCESSING',
 1000,
 NULL,
 NULL,
 NULL,
 NULL,
 '正在联系快递柜公司评估增设方案',
 NOW() - INTERVAL 1 DAY + INTERVAL 8 HOUR,
 NOW() - INTERVAL 1 DAY + INTERVAL 8 HOUR,
 0);

-- 验证插入结果
SELECT 
    complaint_no AS '投诉编号',
    complaint_type AS '投诉类型',
    title AS '标题',
    urgency_level AS '紧急程度',
    status AS '状态',
    create_time AS '创建时间'
FROM complaint
WHERE complainant_id = 1002 AND deleted = 0
ORDER BY create_time DESC;

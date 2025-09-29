-- ========================================
-- 工单测试数据插入脚本
-- 专门为测试用户ID=1002的业主添加各种报修工单
-- ========================================

INSERT INTO `work_order` (
    `order_no`,        -- 工单编号
    `title`,           -- 工单标题
    `content`,         -- 工单详细内容
    `category`,        -- 工单类别
    `priority`,        -- 优先级
    `status`,          -- 工单状态
    `submitter_id`,    -- 提交人ID
    `assignee_id`,     -- 分配人ID（维修师傅）
    `room_id`,         -- 关联房屋ID
    `images`,          -- 相关图片（JSON格式）
    `submit_time`,     -- 提交时间
    `assign_time`,     -- 分配时间
    `start_time`,      -- 开始处理时间
    `complete_time`,   -- 完成时间
    `rating`,          -- 评分（1-5分）
    `feedback`,        -- 用户反馈
    `cost`             -- 维修费用
) VALUES

-- 1. 待处理的水电维修工单（刚提交）
('WO2025092900001', 
 '厨房水龙头漏水', 
 '厨房洗菜盆的水龙头一直在滴水，已经持续了两天，请尽快安排维修师傅处理。', 
 'REPAIR',          -- 报修类别
 'MEDIUM',          -- 中等优先级
 'PENDING',         -- 待处理状态
 1002,              -- 业主ID=1002提交
 NULL,              -- 尚未分配维修师傅
 1,                 -- 关联房屋ID=1
 '[]',              -- 暂无图片
 NOW() - INTERVAL 2 HOUR,  -- 2小时前提交
 NULL,              -- 尚未分配
 NULL,              -- 尚未开始处理
 NULL,              -- 尚未完成
 NULL,              -- 尚未评分
 NULL,              -- 尚无反馈
 NULL),             -- 尚无费用

-- 2. 处理中的空调维修工单（已分配师傅）
('WO2025092900002', 
 '客厅空调不制冷', 
 '客厅的空调开启后不制冷，风扇可以正常运转，但是出风不凉，怀疑是制冷剂不足。', 
 'REPAIR',          -- 报修类别
 'HIGH',            -- 高优先级（夏天空调很重要）
 'PROCESSING',      -- 处理中状态
 1002,              -- 业主ID=1002提交
 3,                 -- 分配给维修师傅ID=3
 1,                 -- 关联房屋ID=1
 '[]',              -- 暂无图片
 NOW() - INTERVAL 1 DAY,   -- 1天前提交
 NOW() - INTERVAL 20 HOUR, -- 20小时前分配
 NOW() - INTERVAL 18 HOUR, -- 18小时前开始处理
 NULL,              -- 尚未完成
 NULL,              -- 尚未评分
 NULL,              -- 尚无反馈
 NULL),             -- 费用待确定

-- 3. 已完成的门锁维修工单（需要评价）
('WO2025092800001', 
 '入户门锁损坏', 
 '入户门的智能锁突然无法识别指纹，输入密码也没有反应，只能用机械钥匙开门。', 
 'REPAIR',          -- 报修类别
 'URGENT',          -- 紧急优先级（安全问题）
 'COMPLETED',       -- 已完成状态
 1002,              -- 业主ID=1002提交
 4,                 -- 分配给维修师傅ID=4
 1,                 -- 关联房屋ID=1
 '[]',              -- 暂无图片
 NOW() - INTERVAL 2 DAY,   -- 2天前提交
 NOW() - INTERVAL 2 DAY + INTERVAL 2 HOUR,  -- 2天前分配
 NOW() - INTERVAL 2 DAY + INTERVAL 4 HOUR,  -- 2天前开始处理
 NOW() - INTERVAL 1 DAY,   -- 1天前完成
 NULL,              -- 等待用户评分
 NULL,              -- 等待用户反馈
 150.00),           -- 维修费用150元

-- 4. 已评价的网络维修工单（完整流程）
('WO2025092700001', 
 '网络信号不稳定', 
 '家里的WiFi信号经常断断续续，特别是在卧室，网速也很慢，影响正常使用。', 
 'REPAIR',          -- 报修类别
 'LOW',             -- 低优先级
 'COMPLETED',       -- 已完成状态
 1002,              -- 业主ID=1002提交
 5,                 -- 分配给维修师傅ID=5
 1,                 -- 关联房屋ID=1
 '[]',              -- 暂无图片
 NOW() - INTERVAL 3 DAY,   -- 3天前提交
 NOW() - INTERVAL 3 DAY + INTERVAL 1 HOUR,  -- 3天前分配
 NOW() - INTERVAL 2 DAY,   -- 2天前开始处理
 NOW() - INTERVAL 2 DAY + INTERVAL 3 HOUR,  -- 2天前完成
 5,                 -- 满分评价
 '师傅很专业，问题解决得很彻底，网络现在非常稳定！',  -- 用户反馈
 80.00),            -- 维修费用80元

-- 5. 投诉工单（噪音问题）
('WO2025092600001', 
 '楼上住户噪音扰民', 
 '楼上住户经常在深夜制造噪音，包括拖拽家具、大声说话等，严重影响休息。', 
 'COMPLAINT',       -- 投诉类别
 'MEDIUM',          -- 中等优先级
 'PROCESSING',      -- 处理中状态
 1002,              -- 业主ID=1002提交
 1,                 -- 分配给物业管理员ID=1
 1,                 -- 关联房屋ID=1
 '[]',              -- 暂无图片
 NOW() - INTERVAL 4 DAY,   -- 4天前提交
 NOW() - INTERVAL 4 DAY + INTERVAL 2 HOUR,  -- 4天前分配
 NOW() - INTERVAL 3 DAY,   -- 3天前开始处理
 NULL,              -- 尚未完成
 NULL,              -- 尚未评分
 NULL,              -- 尚无反馈
 NULL);             -- 投诉无费用

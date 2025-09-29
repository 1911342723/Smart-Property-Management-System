-- 检查活动数据
USE property_management;

-- 查看所有活动的详细信息
SELECT 
    id,
    title,
    status,
    start_time,
    end_time,
    registration_start,
    registration_end,
    current_participants,
    max_participants,
    deleted,
    NOW() as current_time,
    CASE 
        WHEN deleted = 1 THEN '已删除'
        WHEN status != 'REGISTRATION' THEN CONCAT('状态不对:', status)
        WHEN registration_start IS NOT NULL AND NOW() < registration_start THEN '报名未开始'
        WHEN registration_end IS NOT NULL AND NOW() > registration_end THEN '报名已结束'
        WHEN max_participants IS NOT NULL AND current_participants >= max_participants THEN '人数已满'
        ELSE '可以报名'
    END as can_register_reason
FROM activity 
ORDER BY id DESC;

-- 修复活动数据 - 确保有可报名的活动
UPDATE activity SET 
    status = 'REGISTRATION',
    registration_start = DATE_SUB(NOW(), INTERVAL 1 DAY),  -- 报名开始时间设为昨天
    registration_end = DATE_ADD(NOW(), INTERVAL 7 DAY),    -- 报名结束时间设为一周后
    start_time = DATE_ADD(NOW(), INTERVAL 10 DAY),         -- 活动开始时间设为10天后
    end_time = DATE_ADD(NOW(), INTERVAL 10 DAY) + INTERVAL 3 HOUR, -- 活动结束时间
    current_participants = COALESCE(current_participants, 0),
    max_participants = COALESCE(max_participants, 100),
    update_time = NOW()
WHERE deleted = 0;

-- 再次检查修复后的数据
SELECT 
    id,
    title,
    status,
    start_time,
    registration_start,
    registration_end,
    current_participants,
    max_participants,
    CASE 
        WHEN deleted = 1 THEN '已删除'
        WHEN status != 'REGISTRATION' THEN CONCAT('状态不对:', status)
        WHEN registration_start IS NOT NULL AND NOW() < registration_start THEN '报名未开始'
        WHEN registration_end IS NOT NULL AND NOW() > registration_end THEN '报名已结束'
        WHEN max_participants IS NOT NULL AND current_participants >= max_participants THEN '人数已满'
        ELSE '可以报名'
    END as can_register_status
FROM activity 
WHERE deleted = 0
ORDER BY id DESC;


-- 测试活动API数据脚本
USE property_management;

-- 检查活动表是否存在
SHOW TABLES LIKE 'activity';

-- 检查活动数据
SELECT COUNT(*) as activity_count FROM activity WHERE deleted = 0;

-- 查看现有活动数据
SELECT 
    id, 
    title, 
    status, 
    start_time, 
    end_time, 
    current_participants, 
    max_participants,
    create_time
FROM activity 
WHERE deleted = 0 
ORDER BY create_time DESC;

-- 如果没有数据，插入测试数据
INSERT IGNORE INTO activity (
    title, description, content, summary, type, organizer_id, 
    location, start_time, end_time, max_participants, current_participants,
    registration_start, registration_end, status, is_free, image_url
) VALUES 
(
    '社区新年联欢会', 
    '欢迎大家参加社区新年联欢会，共同庆祝新年的到来', 
    '社区新年联欢会将于2025年1月30日举行，届时将有精彩的文艺表演、互动游戏和丰富奖品。欢迎全体居民参加，让我们一起迎接新年！现场将提供免费茶点和小食。',
    '社区新年联欢会，精彩节目、互动游戏、丰富奖品等您来参加',
    'ENTERTAINMENT', 
    1001, 
    '社区活动中心', 
    '2025-01-30 19:00:00', 
    '2025-01-30 22:00:00', 
    100, 
    0,
    '2025-01-15 09:00:00',
    '2025-01-28 18:00:00',
    'REGISTRATION', 
    1, 
    '/static/images/activity1.jpg'
),
(
    '社区羽毛球比赛', 
    '社区羽毛球友谊赛，欢迎羽毛球爱好者报名参加', 
    '社区羽毛球友谊赛即将开始！无论您是新手还是高手，都欢迎参加。比赛将按照技能水平分组，确保每位参与者都能享受比赛乐趣。比赛用具由社区提供，您只需要穿运动服和运动鞋即可。',
    '社区羽毛球友谊赛，按技能分组，用具提供',
    'SPORTS', 
    1001, 
    '社区体育馆', 
    '2025-02-15 14:00:00', 
    '2025-02-15 18:00:00', 
    32, 
    0,
    '2025-01-20 09:00:00',
    '2025-02-13 18:00:00',
    'REGISTRATION', 
    1, 
    '/static/images/activity2.jpg'
),
(
    '健康知识讲座', 
    '邀请专业医师为社区居民讲解冬季养生保健知识', 
    '本次健康知识讲座将邀请三甲医院的专业医师，为大家详细讲解冬季养生保健知识，包括：1. 冬季饮食调理要点 2. 适合的运动方式 3. 常见疾病预防 4. 中老年人保健注意事项。讲座结束后设有免费咨询环节。',
    '专业医师讲解冬季养生，免费健康咨询',
    'HEALTH', 
    1001, 
    '社区会议室', 
    '2025-02-20 15:00:00', 
    '2025-02-20 17:00:00', 
    50, 
    0,
    '2025-01-25 09:00:00',
    '2025-02-18 18:00:00',
    'REGISTRATION', 
    1, 
    '/static/images/activity-health.jpg'
);

-- 再次检查插入后的数据
SELECT 
    id, 
    title, 
    status, 
    start_time, 
    current_participants, 
    max_participants
FROM activity 
WHERE deleted = 0 AND status = 'REGISTRATION'
ORDER BY start_time ASC;


-- 测试owner_room表的数据查询

USE property_management;

-- 查看所有用户
SELECT '=== 所有用户 ===' as info;
SELECT id, username, real_name, phone, user_type FROM sys_user WHERE deleted = 0;

-- 查看所有房屋
SELECT '=== 所有房屋 ===' as info;
SELECT r.id, r.building_id, r.unit_id, r.room_no, r.floor, r.area, r.status,
       b.building_name, u.unit_name
FROM room r
LEFT JOIN building b ON r.building_id = b.id
LEFT JOIN unit u ON r.unit_id = u.id
WHERE r.deleted = 0;

-- 查看所有房屋关系
SELECT '=== 所有房屋关系 ===' as info;
SELECT ow.id, ow.user_id, ow.room_id, ow.relation_type, ow.status,
       u.real_name as user_name,
       CONCAT(b.building_name, un.unit_name, r.room_no, '室') as room_address
FROM owner_room ow
LEFT JOIN sys_user u ON ow.user_id = u.id
LEFT JOIN room r ON ow.room_id = r.id
LEFT JOIN building b ON r.building_id = b.id
LEFT JOIN unit un ON r.unit_id = un.id
WHERE ow.deleted = 0;

-- 测试查询用户1002的房屋（模拟API查询）
SELECT '=== 用户1002的房屋 ===' as info;
SELECT r.*, b.building_name, u.unit_name, ow.relation_type
FROM room r
INNER JOIN owner_room ow ON r.id = ow.room_id
LEFT JOIN building b ON r.building_id = b.id
LEFT JOIN unit u ON r.unit_id = u.id
WHERE ow.user_id = 1002 AND ow.relation_type = 'OWNER'
AND ow.status = 1 AND ow.deleted = 0
ORDER BY b.building_name, u.unit_name, r.room_no;





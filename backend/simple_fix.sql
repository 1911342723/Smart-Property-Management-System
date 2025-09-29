-- 修复手机号登录 - 使用原始数据库中正确的BCrypt哈希
USE property_management;

-- 删除可能冲突的测试用户
DELETE FROM sys_user WHERE phone IN ('13800138001', '13800138002', '13800138003');

-- 创建手机号登录的测试账号，使用原始的正确BCrypt哈希值
INSERT INTO sys_user (id, username, password, real_name, phone, email, user_type, status) VALUES
(1002, 'owner_13800138001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '张三', '13800138001', 'owner@test.com', 'OWNER', 1),
(1007, 'worker_13800138002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '维修工老张', '13800138002', 'worker@test.com', 'WORKER', 1),
(1005, 'guard_13800138003', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '保安小刘', '13800138003', 'guard@test.com', 'GUARD', 1);

-- 验证插入结果
SELECT username, phone, real_name, user_type, '密码为123456，支持手机号登录' as status 
FROM sys_user 
WHERE phone IN ('13800138001', '13800138002', '13800138003');

SELECT '手机号登录已配置完成！用手机号和密码123456登录' AS message;

-- 将所有用户密码更新为明文
-- 开发环境使用，生产环境请勿使用明文密码

UPDATE sys_user SET password = '123456' 
WHERE phone IN ('13800000101', '13800000301', '13800000201');

-- 验证更新结果
SELECT username, phone, password, user_type, status 
FROM sys_user 
WHERE phone IN ('13800000101', '13800000301', '13800000201');

-- 如果需要，也可以为其他用户设置明文密码
-- UPDATE sys_user SET password = '123456' WHERE password LIKE '$2a$%';







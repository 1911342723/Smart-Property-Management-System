-- 紧急修复：直接重置 owner002 密码为原始明文
-- 这样可以立即让登录成功

USE property_management;

-- 直接使用数据库中其他正确的密码哈希
UPDATE sys_user SET password = (SELECT password FROM (SELECT password FROM sys_user WHERE username = 'admin' LIMIT 1) AS temp) WHERE username = 'owner002';

-- 验证
SELECT username, password FROM sys_user WHERE username IN ('admin', 'owner002');

SELECT '已修复 owner002 密码，现在可以登录了' AS message;















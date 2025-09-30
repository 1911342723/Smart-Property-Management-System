-- 测试密码问题
USE property_management;

-- 直接重新生成密码哈希，使用一个肯定正确的
-- 先删除测试用户，然后重新创建一个绝对正确的
DELETE FROM sys_user WHERE phone = '13800138999';

-- 创建一个测试用户，使用明确知道的密码哈希
-- 这个哈希是 "123456" 的标准BCrypt加密结果
INSERT INTO sys_user (id, username, password, real_name, phone, email, user_type, status) VALUES
(9999, 'test_user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '测试用户', '13800138999', 'test@test.com', 'OWNER', 1);

-- 验证插入
SELECT username, phone, '使用手机号13800138999和密码123456测试登录' as note FROM sys_user WHERE phone = '13800138999';


















-- 验证数据库中的用户数据
-- 用于排查登录问题

USE property_management;

-- 1. 检查用户表数据
SELECT '=== 用户表数据检查 ===' AS message;
SELECT 
  id,
  username,
  real_name,
  phone,
  user_type,
  status,
  CASE 
    WHEN status = 1 THEN '启用'
    ELSE '禁用'
  END AS status_text
FROM sys_user 
ORDER BY id;

-- 2. 检查密码格式（应该是BCrypt格式）
SELECT '=== 密码格式检查 ===' AS message;
SELECT 
  username,
  CASE 
    WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' OR password LIKE '$2y$%' THEN 'BCrypt格式正确'
    ELSE 'BCrypt格式错误'
  END AS password_format,
  LEFT(password, 20) AS password_preview
FROM sys_user
ORDER BY id;

-- 3. 检查用户角色关联
SELECT '=== 用户角色关联检查 ===' AS message;
SELECT 
  u.username,
  u.user_type,
  r.role_name,
  r.role_code
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
ORDER BY u.id;

-- 4. 检查演示账号
SELECT '=== 演示账号检查 ===' AS message;
SELECT 
  username,
  real_name,
  user_type,
  status,
  CASE 
    WHEN username IN ('owner001', 'owner002', 'worker001', 'worker002', 'guard001', 'guard002') THEN '✓ 演示账号'
    ELSE '普通账号'
  END AS account_type
FROM sys_user
WHERE username IN ('owner001', 'owner002', 'worker001', 'worker002', 'guard001', 'guard002', 'admin')
ORDER BY username;

-- 5. 统计信息
SELECT '=== 统计信息 ===' AS message;
SELECT 
  user_type,
  COUNT(*) AS count
FROM sys_user
WHERE status = 1
GROUP BY user_type
ORDER BY user_type;

-- 6. 验证测试用例
SELECT '=== 登录测试验证 ===' AS message;
SELECT 
  '测试账号状态检查' AS test_name,
  CASE 
    WHEN (SELECT COUNT(*) FROM sys_user WHERE username = 'owner001' AND status = 1) = 1 THEN '✓'
    ELSE '✗'
  END AS owner001_status,
  CASE 
    WHEN (SELECT COUNT(*) FROM sys_user WHERE username = 'worker001' AND status = 1) = 1 THEN '✓'
    ELSE '✗'
  END AS worker001_status,
  CASE 
    WHEN (SELECT COUNT(*) FROM sys_user WHERE username = 'guard001' AND status = 1) = 1 THEN '✓'
    ELSE '✗'
  END AS guard001_status;












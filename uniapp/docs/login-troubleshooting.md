# 登录问题诊断指南

## 问题现象
错误信息：`登录失败：Bad credentials`

## 原因分析

### 1. 可能的原因
- ✅ **用户名映射错误**：小程序演示账号与后端数据库用户名不匹配
- ✅ **密码错误**：密码不匹配或密码格式问题
- ✅ **用户状态异常**：用户被禁用或不存在
- ✅ **用户类型不匹配**：前端传递的userType与数据库不一致

### 2. 数据流程
```
小程序演示账号 → app.js映射 → API请求 → Spring Security验证 → 数据库查询
```

## 解决方案

### 步骤1: 检查数据库数据
```sql
-- 运行验证脚本
SOURCE verify_data.sql;
```

### 步骤2: 验证演示账号映射
在 `app.js` 中的映射逻辑：
```javascript
// 演示账号 → 数据库用户名
'13800138001' (业主) → 'owner001'
'13800138002' (维修工) → 'worker001'  
'13800138003' (保安) → 'guard001'
```

### 步骤3: 使用网络诊断工具
1. 打开网络测试页面：`pages/test/network-test`
2. 点击"开始诊断"进行全面检查
3. 点击"测试登录"单独测试账号

### 步骤4: 手动验证API
使用Postman或curl测试：
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "owner001",
    "password": "123456",
    "userType": "OWNER"
  }'
```

## 常见问题修复

### 问题1: 数据库未初始化
**症状**: 用户不存在
**解决**: 执行数据库初始化脚本
```sql
SOURCE src/main/resources/sql/init_database.sql;
```

### 问题2: 密码格式错误
**症状**: 密码验证失败
**解决**: 确认密码是BCrypt格式
```sql
-- 检查密码格式
SELECT username, LEFT(password, 10) FROM sys_user WHERE username = 'owner001';
-- 正确格式应该是: $2a$10$...
```

### 问题3: 用户被禁用
**症状**: 账号存在但无法登录
**解决**: 启用用户账号
```sql
UPDATE sys_user SET status = 1 WHERE username = 'owner001';
```

### 问题4: 演示账号映射错误
**症状**: 映射到不存在的用户名
**解决**: 修正 `app.js` 中的映射逻辑

## 快速修复脚本

### 重新创建测试账号
```sql
-- 删除现有测试数据（可选）
DELETE FROM sys_user WHERE username IN ('owner001', 'worker001', 'guard001');

-- 重新插入测试账号
INSERT INTO sys_user (id, username, password, real_name, phone, email, user_type, status) VALUES
(1002, 'owner001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '张三', '13800000101', 'zhangsan@example.com', 'OWNER', 1),
(1007, 'worker001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '维修师傅老张', '13800000301', 'worker001@property.com', 'WORKER', 1),
(1005, 'guard001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '保安小刘', '13800000201', 'guard001@property.com', 'GUARD', 1);
```

## 测试账号信息

| 演示手机号 | 映射用户名 | 密码 | 角色 | 状态 |
|-----------|-----------|------|------|------|
| 13800138001 | owner001 | 123456 | 业主 | 启用 |
| 13800138002 | worker001 | 123456 | 维修工 | 启用 |
| 13800138003 | guard001 | 123456 | 保安 | 启用 |

## 调试技巧

### 1. 查看控制台日志
- 小程序开发者工具控制台
- 后端Spring Boot日志

### 2. 使用网络诊断工具
- 域名检查
- 网络连接测试
- 登录API测试

### 3. 数据库直接查询
```sql
-- 查看具体用户信息
SELECT * FROM sys_user WHERE username = 'owner001';

-- 查看认证日志（如果有）
SELECT * FROM login_log WHERE username = 'owner001' ORDER BY login_time DESC LIMIT 5;
```

## 联系方式
如果问题仍然存在，请提供：
1. 详细错误信息
2. 网络诊断结果
3. 数据库验证结果
4. 控制台日志























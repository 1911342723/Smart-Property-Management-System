# API测试说明

## 快速测试指南

### 1. 启动服务
```bash
# 运行启动脚本
start.bat

# 或者直接使用Maven命令
mvn spring-boot:run
```

### 2. 初始化数据库
```sql
-- 在MySQL中执行初始化脚本
SOURCE src/main/resources/sql/init_database.sql;
```

### 3. 测试账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| manager | 123456 | 物业经理 |
| owner001 | 123456 | 业主 |
| guard001 | 123456 | 保安 |
| worker001 | 123456 | 维修工 |

### 4. API测试流程

#### 步骤1: 用户登录
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

响应示例：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1000,
    "username": "admin",
    "realName": "系统管理员",
    "userType": "ADMIN",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "roles": ["ROLE_ADMIN"]
  }
}
```

#### 步骤2: 获取用户信息
```http
GET http://localhost:8081/api/auth/userinfo
Authorization: Bearer {token}
```

#### 步骤3: 查询工单列表
```http
GET http://localhost:8081/api/workorder/list?pageNum=1&pageSize=10
Authorization: Bearer {token}
```

#### 步骤4: 提交工单（业主）
先用业主账号登录：
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "owner001",
  "password": "123456"
}
```

然后提交工单：
```http
POST http://localhost:8081/api/workorder
Authorization: Bearer {owner_token}
Content-Type: application/json

{
  "title": "门锁故障",
  "content": "房门锁具损坏，无法正常开锁",
  "category": "REPAIR",
  "priority": "HIGH",
  "submitterId": 1002,
  "roomId": 1
}
```

#### 步骤5: 分配工单（管理员）
```http
PUT http://localhost:8081/api/workorder/1/assign?assigneeId=1005
Authorization: Bearer {admin_token}
```

#### 步骤6: 开始处理工单（维修工）
先用维修工账号登录，然后：
```http
PUT http://localhost:8081/api/workorder/1/start
Authorization: Bearer {worker_token}
```

#### 步骤7: 完成工单
```http
PUT http://localhost:8081/api/workorder/1/complete?cost=50.00
Authorization: Bearer {worker_token}
```

#### 步骤8: 评价工单（业主）
```http
PUT http://localhost:8081/api/workorder/1/rate?rating=5&feedback=服务很好，问题解决了
Authorization: Bearer {owner_token}
```

### 5. 使用Swagger测试

访问：http://localhost:8081/api/swagger-ui/index.html

1. 点击右上角的"Authorize"按钮
2. 输入Bearer Token：`Bearer {你的token}`
3. 点击"Authorize"确认
4. 现在可以直接在Swagger界面测试所有API

### 6. 使用Postman测试

#### 环境变量设置
- `base_url`: http://localhost:8081/api
- `admin_token`: 管理员登录后获取的token
- `owner_token`: 业主登录后获取的token

#### Collection示例
```json
{
  "info": {
    "name": "物业管理系统API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8081/api"
    }
  ],
  "item": [
    {
      "name": "登录",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"admin\",\n  \"password\": \"123456\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/auth/login",
          "host": ["{{base_url}}"],
          "path": ["auth", "login"]
        }
      }
    }
  ]
}
```

### 7. 常见测试场景

#### 业主端测试场景
1. 业主登录
2. 查看个人工单
3. 提交维修申请
4. 查看工单进度
5. 评价完成的工单
6. 查看个人账单

#### 管理员测试场景
1. 管理员登录
2. 查看所有工单
3. 分配工单给维修工
4. 查看工单统计
5. 管理用户账号
6. 生成账单

#### 维修工测试场景
1. 维修工登录
2. 查看分配给自己的工单
3. 开始处理工单
4. 更新工单状态
5. 完成工单

### 8. 错误处理测试

#### 测试无效token
```http
GET http://localhost:8081/api/workorder/list
Authorization: Bearer invalid_token
```

期望响应：
```json
{
  "code": 401,
  "message": "未授权访问，请先登录"
}
```

#### 测试权限不足
用业主token访问管理员接口：
```http
PUT http://localhost:8081/api/workorder/1/assign?assigneeId=1005
Authorization: Bearer {owner_token}
```

期望响应：
```json
{
  "code": 403,
  "message": "权限不足，拒绝访问"
}
```

### 9. 数据库监控

访问Druid监控界面：http://localhost:8081/api/druid/index.html
- 用户名：admin
- 密码：admin

可以查看：
- SQL执行统计
- 数据库连接池状态
- 慢查询记录

### 10. 日志查看

查看应用日志：
```bash
# 查看实时日志
tail -f logs/property-management.log

# 查看错误日志
grep ERROR logs/property-management.log
```

### 11. 性能测试

使用Apache Bench进行简单性能测试：
```bash
# 测试登录接口
ab -n 100 -c 10 -p login.json -T "application/json" http://localhost:8081/api/auth/login
```

login.json内容：
```json
{"username":"admin","password":"123456"}
```

---

以上是API测试的完整指南，可以根据实际需要调整测试用例和参数。







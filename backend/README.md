# 物业管理系统后端服务

## 项目简介

这是物业管理系统的后端服务，基于Spring Boot + MyBatis Plus + MySQL + JWT技术栈开发，为微信小程序端和Web管理后台提供完整的API服务。

## 技术栈

- **Java**: JDK 8+
- **框架**: Spring Boot 2.7.10
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis Plus 3.5.3
- **安全**: Spring Security + JWT
- **连接池**: Druid
- **文档**: Swagger 3.0
- **工具**: Hutool

## 功能模块

### 核心模块
- 用户认证与授权（JWT）
- 用户管理（多角色支持）
- 房产管理（楼栋、单元、房屋）
- 工单管理（报修、投诉、建议）
- 财务管理（账单、缴费）
- 社区管理（公告、活动）
- 访客管理
- 系统管理

### 支持角色
- **管理员**: 系统管理、数据统计
- **业主**: 报修、缴费、投诉建议
- **保安**: 门禁管理、巡逻任务
- **维修工**: 工单处理、任务管理

## 环境要求

### 开发环境
- JDK 8 或以上
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+（可选）
- IDE：IntelliJ IDEA 或 Eclipse

### 数据库配置
创建数据库并执行初始化脚本：

```sql
-- 创建数据库
CREATE DATABASE property_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行表结构脚本
SOURCE src/main/resources/sql/schema.sql;
SOURCE src/main/resources/sql/schema_part2.sql;

-- 导入测试数据
SOURCE src/main/resources/sql/data.sql;
```

## 快速启动

### 1. 克隆项目
```bash
git clone [项目地址]
cd backend
```

### 2. 配置数据库
修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/property_management?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8
    username: root
    password: 你的密码
```

### 3. 安装依赖

如果遇到MySQL驱动依赖问题，请先运行修复脚本：
```bash
# Windows
fix-dependencies.bat

# 或手动执行
mvn clean install -s settings.xml
```

正常安装：
```bash
mvn clean install
```

### 4. 启动服务

**方式1：使用启动脚本（推荐）**
```bash
# 正常启动（包含Swagger）
start.bat

# 如果遇到Swagger兼容性问题，使用无Swagger模式
start-no-swagger.bat
```

**方式2：使用Maven命令**
```bash
# 正常启动
mvn spring-boot:run

# 无Swagger模式启动
mvn spring-boot:run -Dspring-boot.run.profiles=no-swagger
```

**方式3：IDE运行**
在IDE中运行 `PropertyManagementApplication` 主类。

### 5. 访问服务
- 应用地址：http://localhost:8081/api
- API文档：http://localhost:8081/api/swagger-ui/index.html
- Druid监控：http://localhost:8081/api/druid/index.html

## 测试账号

系统已预置以下测试账号（密码均为：123456）：

| 用户名 | 角色 | 说明 |
|-------|------|------|
| admin | 管理员 | 系统管理员，拥有所有权限 |
| manager | 管理员 | 物业经理 |
| owner001 | 业主 | 测试业主用户 |
| guard001 | 保安 | 测试保安用户 |
| worker001 | 维修工 | 测试维修工用户 |

## API接口说明

### 认证相关
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `GET /auth/userinfo` - 获取当前用户信息
- `POST /auth/refresh` - 刷新令牌
- `POST /auth/logout` - 用户登出

### 工单管理
- `GET /workorder/list` - 分页查询工单
- `GET /workorder/{id}` - 获取工单详情
- `POST /workorder` - 提交工单
- `PUT /workorder/{id}/assign` - 分配工单
- `PUT /workorder/{id}/start` - 开始处理
- `PUT /workorder/{id}/complete` - 完成工单
- `PUT /workorder/{id}/rate` - 评价工单

### 权限控制
API接口按角色进行权限控制：
- 管理员可访问所有接口
- 业主可提交工单、查看自己的工单和账单
- 保安可处理门禁相关功能
- 维修工可处理分配给自己的工单

## 配置说明

### JWT配置
```yaml
jwt:
  secret: property-management-secret-key-2023  # JWT密钥
  expiration: 604800  # 过期时间（秒）
  header: Authorization  # 请求头名称
  prefix: Bearer  # 令牌前缀
```

### 数据库配置
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 连接池配置
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
```

### MyBatis Plus配置
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## 开发指南

### 代码结构
```
src/main/java/com/property/
├── config/          # 配置类
├── controller/      # 控制器层
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── mapper/         # 数据访问层
├── service/        # 业务逻辑层
├── utils/          # 工具类
└── PropertyManagementApplication.java  # 启动类
```

### 添加新功能
1. 在 `entity` 包中创建实体类
2. 在 `mapper` 包中创建Mapper接口
3. 在 `service` 包中创建服务类
4. 在 `controller` 包中创建控制器
5. 添加相应的测试用例

### 数据库操作
使用MyBatis Plus简化数据库操作：
```java
// 继承BaseMapper获得基础CRUD功能
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
    // 自定义查询方法
}

// 服务层继承ServiceImpl
@Service
public class UserService extends ServiceImpl<UserMapper, SysUser> {
    // 业务逻辑
}
```

## 部署说明

### 打包应用
```bash
mvn clean package -Dmaven.test.skip=true
```

### 运行JAR包
```bash
java -jar target/property-management-1.0.0.jar
```

### Docker部署
```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/property-management-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 常见问题

### 1. Swagger兼容性问题
**错误信息**: `Failed to start bean 'documentationPluginsBootstrapper'`

**解决方案**:
```bash
# 方案1：使用修复后的配置（推荐）
start.bat

# 方案2：禁用Swagger启动
start-no-swagger.bat

# 方案3：手动指定配置文件
mvn spring-boot:run -Dspring-boot.run.profiles=no-swagger
```

### 2. MySQL驱动依赖问题
**错误信息**: `Could not find artifact mysql:mysql-connector-java:pom:unknown`

**解决方案**:
```bash
# 方案1：运行修复脚本
fix-dependencies.bat

# 方案2：手动清理并重新下载
mvn dependency:purge-local-repository
mvn clean install -s settings.xml

# 方案3：使用指定版本
# 已在pom.xml中固定MySQL驱动版本为8.0.33
```

### 3. 启动失败
- 检查数据库连接配置
- 确认MySQL服务已启动
- 检查端口是否被占用
- 运行数据库初始化脚本

### 4. 认证失败
- 检查JWT配置
- 确认用户名密码正确
- 检查用户状态是否启用

### 5. 权限不足
- 确认用户角色配置
- 检查接口权限注解
- 查看用户是否有相应权限

### 6. 网络问题
如果Maven下载依赖缓慢，项目已配置阿里云镜像源（settings.xml），自动使用国内镜像加速下载。

## 日志配置

日志文件位置：`logs/property-management.log`

可通过修改 `application.yml` 调整日志级别：
```yaml
logging:
  level:
    com.property: debug
    org.springframework.security: debug
```

## 联系方式

如有问题请联系开发团队或查看项目文档。

---

*本文档会随着项目发展持续更新*

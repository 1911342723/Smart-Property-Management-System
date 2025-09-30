# 智慧物业管理系统

## 📖 项目简介

智慧物业管理系统是一个集成了业主端、维修工端、门卫端和管理端的全方位物业管理解决方案。系统采用前后端分离架构，支持微信小程序和Web管理后台。

### 主要功能

#### 🏠 业主端（微信小程序）
- **报修服务**：在线提交报修工单，实时跟踪处理进度
- **AI助手**：智能识别问题，自动生成工单（支持图片识别）
- **缴费管理**：物业费、水电费在线缴纳，账单查询
- **投诉建议**：在线提交投诉和建议，查看处理结果
- **访客管理**：预约访客，生成访客二维码
- **社区活动**：参与社区活动报名
- **消息通知**：接收工单处理、缴费提醒等通知
- **评价系统**：完成服务后多维度评价维修工

#### 🔧 维修工端（微信小程序）
- **工单管理**：接单、处理工单、上传完工照片
- **技能标签**：展示个人技能，提升接单机会
- **工作历史**：查看历史工单，统计工作数据
- **评价管理**：查看业主评价，回复评价
- **收益统计**：查看工作收益、评分统计
- **消息通知**：接收新工单、评价等通知

#### 🛡️ 门卫端（微信小程序）
- **访客验证**：扫码验证访客身份
- **访客登记**：现场访客登记
- **巡逻打卡**：巡逻路线打卡记录
- **进出记录**：查看小区进出记录
- **异常上报**：上报异常情况

#### 💻 管理端（Web后台）
- **数据看板**：数据可视化，实时监控运营状态
- **用户管理**：业主、维修工、门卫信息管理
- **工单管理**：工单分配、监控、统计分析
- **财务管理**：账单管理、收费统计、财务报表
- **社区管理**：公告发布、活动管理
- **系统设置**：系统配置、权限管理、日志查看

---

## 🛠️ 技术栈

### 后端
- **框架**：Spring Boot 2.7.18
- **数据库**：MySQL 8.0
- **ORM**：MyBatis-Plus 3.5.3
- **安全认证**：JWT
- **API文档**：Swagger/Knife4j
- **AI集成**：火山引擎豆包大模型（图像识别）
- **工具库**：Hutool、Lombok

### 前端管理端
- **框架**：Vue 3
- **UI组件**：Element Plus
- **状态管理**：Vuex
- **路由**：Vue Router
- **图表**：ECharts
- **HTTP客户端**：Axios

### 小程序端
- **框架**：微信原生小程序
- **组件**：自定义组件、自定义TabBar
- **状态管理**：全局数据管理

---

## 📋 环境要求

### 开发环境
- **JDK**：1.8 或以上
- **Maven**：3.6 或以上
- **Node.js**：14.x 或以上
- **npm**：6.x 或以上
- **MySQL**：8.0 或以上
- **微信开发者工具**：最新版本

### 推荐配置
- CPU：4核以上
- 内存：8GB以上
- 硬盘：可用空间20GB以上

---

## 🚀 快速开始

### 第一步：数据库配置

1. **安装MySQL 8.0**
   - 下载地址：https://dev.mysql.com/downloads/mysql/
   - 安装后设置root密码

2. **创建数据库**
   ```sql
   CREATE DATABASE property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **导入数据**
   - 找到文件：`backend/src/main/resources/sql/property_management.sql`
   - 使用Navicat、MySQL Workbench或命令行导入：
   ```bash
   mysql -u root -p property_management < backend/src/main/resources/sql/property_management.sql
   ```

4. **修改数据库连接配置**
   - 打开文件：`backend/src/main/resources/application.yml`
   - 修改以下配置（大约在第9-12行）：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/property_management?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root        # 改成你的MySQL用户名
       password: 123456      # 改成你的MySQL密码
   ```

### 第二步：启动后端服务

#### 方法一：使用启动脚本（推荐，适合新手）

1. **双击运行启动脚本**
   - 找到项目根目录的 `start-backend.bat`
   - 双击运行，等待启动完成
   - 看到"Started PropertyManagementApplication"表示启动成功

2. **验证后端**
   - 打开浏览器访问：http://localhost:8081/doc.html
   - 看到Swagger API文档页面说明启动成功

#### 方法二：使用IDE（适合开发者）

1. **导入项目**
   - 使用IDEA或Eclipse打开 `backend` 目录
   - 等待Maven自动下载依赖（需要联网）

2. **运行主类**
   - 找到 `com.property.PropertyManagementApplication`
   - 右键选择 Run/Debug 运行

### 第三步：启动前端管理系统

#### 方法一：使用启动脚本（推荐，适合新手）

1. **双击运行启动脚本**
   - 找到项目根目录的 `start-frontend.bat`
   - 双击运行，等待启动完成
   - 自动打开浏览器访问 http://localhost:8080

2. **登录管理后台**
   - 默认管理员账号：`admin`
   - 默认密码：`admin123`

#### 方法二：手动启动（适合开发者）

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **启动开发服务器**
   ```bash
   npm run serve
   ```

3. **访问管理后台**
   - 浏览器打开：http://localhost:8080

### 第四步：启动微信小程序

1. **安装微信开发者工具**
   - 下载地址：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

2. **导入项目**
   - 打开微信开发者工具
   - 选择"导入项目"
   - 项目目录选择：`uniapp` 文件夹
   - AppID：使用测试号或自己的AppID

3. **配置后端地址**
   - 打开文件：`uniapp/config/index.js`
   - 修改API地址（第2行）：
   ```javascript
   const API_BASE_URL = 'http://localhost:8081/api'  // 改成你的后端地址
   ```

4. **编译运行**
   - 点击"编译"按钮
   - 在模拟器中查看效果

5. **登录测试**
   - 使用以下测试账号登录：
     - **业主账号**：`13800000101`，密码：`123456`
     - **维修工账号**：`13800000301`，密码：`123456`

---

## 📱 演示账号

### 管理端（Web后台）
| 账号类型 | 用户名 | 密码 | 说明 |
|---------|--------|------|------|
| 管理员 | admin | admin123 | 拥有所有权限 |

### 小程序端
| 账号类型 | 手机号 | 密码 | 说明 |
|---------|--------|------|------|
| 业主 | 13800000101 | 123456 | 业主端功能 |
| 维修工 | 13800000301 | 123456 | 维修工端功能 |
| web管理员 | 13800000001 | 123456 | web端功能 |

---

## 🔧 配置说明

### 后端配置

#### 文件上传路径
- 文件位置：`backend/src/main/resources/application.yml`（第46-49行）
```yaml
file:
  upload:
    path: D:/uploads/              # 上传文件保存路径，可改成你的路径
    url-prefix: http://localhost:8081/api/file/
```

#### AI功能配置（可选）
- 文件位置：`backend/src/main/resources/application.yml`（第38-45行）
```yaml
ai:
  openai:
    enabled: true                  # 是否启用AI功能
    api-key: your-api-key          # 豆包API密钥
    base-url: https://ark.cn-beijing.volces.com/api/v3/chat/completions
    model: doubao-1-5-thinking-vision-pro-250428
```

#### JWT密钥
- 文件位置：`backend/src/main/resources/application.yml`（第51-53行）
```yaml
jwt:
  secret: property-management-secret-key-2024  # JWT加密密钥
  expiration: 604800  # 7天有效期（秒）
```

### 前端配置

#### API地址
- 文件位置：`frontend/src/utils/request.js`（第5行）
```javascript
const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081/api'
```

或在 `frontend/.env.development` 中配置：
```env
VUE_APP_API_BASE_URL=http://localhost:8081/api
```

### 小程序配置

#### API地址
- 文件位置：`uniapp/config/index.js`
```javascript
const API_BASE_URL = 'http://localhost:8081/api'  // 开发环境
```

#### 服务器域名配置（正式发布时）
在微信公众平台配置服务器域名：
- request合法域名：你的后端域名
- uploadFile合法域名：你的后端域名
- downloadFile合法域名：你的后端域名

---

## 📂 项目结构

```
智慧物业管理系统/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/property/
│   │   │   │   ├── controller/     # 控制器层
│   │   │   │   ├── service/        # 业务逻辑层
│   │   │   │   ├── mapper/         # 数据访问层
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── config/         # 配置类
│   │   │   │   └── utils/          # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml # 主配置文件
│   │   │       └── sql/            # 数据库脚本
│   │   └── test/                   # 测试代码
│   ├── pom.xml                     # Maven配置
│   └── uploads/                    # 上传文件目录
│
├── frontend/                   # 前端管理系统
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── utils/              # 工具函数
│   │   └── styles/             # 样式文件
│   ├── package.json            # npm配置
│   └── vue.config.js           # Vue配置
│
├── uniapp/                     # 微信小程序
│   ├── pages/                  # 页面文件
│   │   ├── owner/              # 业主端页面
│   │   ├── worker/             # 维修工端页面
│   │   └── guard/              # 门卫端页面
│   ├── utils/                  # 工具函数
│   ├── components/             # 公共组件
│   ├── config/                 # 配置文件
│   ├── app.js                  # 小程序入口
│   └── app.json                # 小程序配置
│
├── start-backend.bat           # 后端启动脚本
├── start-frontend.bat          # 前端启动脚本
└── README.md                   # 本文档
```

---

## ❓ 常见问题

### 1. 后端启动失败

**问题**：提示数据库连接失败
```
Could not create connection to database server
```

**解决方案**：
- 检查MySQL是否已启动
- 确认数据库名称、用户名、密码是否正确
- 检查`application.yml`中的数据库配置
- 确保已创建数据库并导入SQL文件

---

**问题**：提示端口被占用
```
Port 8081 was already in use
```

**解决方案**：
- 方案1：关闭占用8081端口的程序
- 方案2：修改`application.yml`中的端口号：
  ```yaml
  server:
    port: 8082  # 改成其他端口
  ```

---

### 2. 前端启动失败

**问题**：npm install 失败
```
npm ERR! network request failed
```

**解决方案**：
- 切换npm镜像源：
  ```bash
  npm config set registry https://registry.npmmirror.com
  ```
- 重新执行 `npm install`

---

**问题**：前端无法访问后端接口
```
Network Error / CORS Error
```

**解决方案**：
- 确保后端服务已启动
- 检查前端配置的API地址是否正确
- 检查浏览器控制台的错误信息

---

### 3. 小程序问题

**问题**：小程序登录失败
```
请求失败 / 网络错误
```

**解决方案**：
- 确保后端服务已启动
- 检查`uniapp/config/index.js`中的API地址
- 微信开发者工具中开启"不校验合法域名"
  - 详情 → 本地设置 → 勾选"不校验合法域名..."

---

**问题**：小程序图片上传失败

**解决方案**：
- 检查后端文件上传路径是否有写入权限
- 检查`application.yml`中的文件上传配置
- 确保上传目录已创建

---

### 4. 数据库问题

**问题**：导入SQL文件失败

**解决方案**：
- 确保数据库字符集为`utf8mb4`
- 使用MySQL 8.0版本
- 检查SQL文件路径是否正确
- 使用可视化工具（Navicat）导入

---

## 🔐 安全建议

### 生产环境部署时请务必修改：

1. **数据库密码**
   - 使用强密码，包含大小写字母、数字、特殊字符
   - 定期更换密码

2. **JWT密钥**
   - 修改`application.yml`中的`jwt.secret`
   - 使用长度至少32位的随机字符串

3. **管理员账号**
   - 修改默认管理员密码
   - 使用强密码策略

4. **HTTPS**
   - 生产环境使用HTTPS协议
   - 配置SSL证书

5. **跨域配置**
   - 限制允许的跨域来源
   - 不要使用通配符`*`

---

## 📞 技术支持

如有问题，请通过以下方式联系：

- **问题反馈**：提交Issue
- **技术文档**：查看项目内各模块的详细文档
- **开发文档**：
  - 后端API文档：http://localhost:8081/doc.html
  - 数据库文档：查看SQL文件注释

---

## 🎉 功能亮点

### AI智能助手
- 图像识别：上传报修图片，AI自动识别问题类型
- 智能生成：自动生成工单标题和描述
- 优先级判断：根据问题严重程度自动评估优先级

### 多维度评价系统
- 总体评分：1-5星评分
- 细分维度：服务态度、工作质量、响应速度、专业能力
- 标签系统：快速选择评价标签
- 匿名评价：保护用户隐私
- 维修工回复：增强互动

### 技能标签系统
- 技能展示：维修工展示个人技能
- 分类管理：按类别管理技能标签
- 熟练度标识：初级、中级、高级、专家

### 数据可视化
- 实时数据大屏：工单统计、收益统计、趋势分析
- ECharts图表：折线图、柱状图、饼图、仪表盘
- 响应式设计：适配各种屏幕尺寸

---

## 🚀 部署到生产环境

### 后端部署

1. **打包**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **运行**
   ```bash
   java -jar target/property-management-1.0.0.jar
   ```

3. **后台运行（Linux）**
   ```bash
   nohup java -jar target/property-management-1.0.0.jar > app.log 2>&1 &
   ```

### 前端部署

1. **打包**
   ```bash
   cd frontend
   npm run build
   ```

2. **部署**
   - 将`dist`目录内容部署到Nginx或Apache
   - 配置示例（Nginx）：
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       location / {
           root /path/to/dist;
           index index.html;
           try_files $uri $uri/ /index.html;
       }
       
       location /api {
           proxy_pass http://localhost:8081;
       }
   }
   ```

### 小程序发布

1. **上传代码**
   - 在微信开发者工具中点击"上传"
   - 填写版本号和项目备注

2. **提交审核**
   - 登录微信公众平台
   - 进入版本管理
   - 提交审核

---



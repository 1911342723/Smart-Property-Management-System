# 物业管理系统

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Vue](https://img.shields.io/badge/vue-3.3.4-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/springboot-2.7.0-brightgreen.svg)
![UniApp](https://img.shields.io/badge/uniapp-3.0-brightgreen.svg)

一个现代化的物业管理系统，包含微信小程序端、Web管理后台和后端服务

[在线演示](https://demo.property-management.com) | [开发文档](./开发文档.md) | [API文档](https://api.property-management.com/doc.html)

</div>

## 📋 项目简介

本项目是一个完整的物业管理解决方案，旨在通过数字化手段提升物业管理效率，改善业主服务体验。系统采用微服务架构，支持多端访问，提供全方位的物业管理功能。

### ✨ 核心特性

- 🏠 **全面的业务覆盖**：涵盖业主服务、员工管理、财务管理等核心业务
- 📱 **多端适配**：微信小程序 + Web管理后台，满足不同角色需求
- 🎨 **现代化设计**：Discord风格UI，提供出色的用户体验
- ⚡ **高性能架构**：Spring Boot + Vue 3，保证系统稳定高效
- 🔐 **安全可靠**：完善的权限管理，保障数据安全
- 📊 **数据可视化**：丰富的图表和统计功能，助力管理决策

## 🏗️ 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   微信小程序端    │    │   Web管理后台    │    │     移动端APP    │
│   (UniApp)      │    │   (Vue 3)       │    │   (React Native) │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴───────────┐
                    │      API Gateway        │
                    │      (Spring Cloud)     │
                    └─────────────┬───────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
┌─────────┴───────┐    ┌─────────┴───────┐    ┌─────────┴───────┐
│   用户服务       │    │   业务服务       │    │   通知服务       │
│ (User Service)  │    │(Business Service)│    │(Notify Service) │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴───────────┐
                    │      数据存储层          │
                    │   MySQL + Redis + OSS   │
                    └─────────────────────────┘
```

## 🚀 快速开始

### 环境要求

- **Node.js** >= 14.0.0
- **Java** >= 8
- **MySQL** >= 8.0
- **Redis** >= 6.0 (可选)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-username/property-management-system.git
   cd property-management-system
   ```

2. **数据库初始化**
   ```sql
   CREATE DATABASE property_management CHARACTER SET utf8mb4;
   -- 导入sql/schema.sql文件
   ```

3. **启动后端服务**
   ```bash
   cd backend
   # 修改application.yml配置文件
   mvn clean install
   mvn spring-boot:run
   ```

4. **启动Web管理后台**
   ```bash
   cd frontend
   npm install
   npm run serve
   ```

5. **配置微信小程序**
   ```bash
   cd uniapp
   npm install
   # 使用微信开发者工具打开项目
   ```

6. **访问系统**
   - Web管理后台：http://localhost:8080
   - 后端API：http://localhost:8081
   - 默认账号：admin / 123456

## 📱 功能模块

### 微信小程序端

<details>
<summary><strong>🏠 业主端功能</strong></summary>

- **首页服务**
  - 快速服务入口
  - 公告通知查看
  - 社区活动信息
  
- **报修服务**
  - 在线报修申请
  - 上传图片和语音
  - 进度实时跟踪
  - 服务评价反馈
  
- **缴费管理**
  - 物业费查询缴纳
  - 水电费在线支付
  - 缴费历史记录
  - 欠费提醒通知
  
- **访客管理**
  - 访客信息预约
  - 二维码邀请码
  - 访问记录查询
  - 车辆进出管理
  
- **投诉建议**
  - 在线投诉提交
  - 处理进度跟踪
  - 满意度评价
  - 历史记录查看

</details>

<details>
<summary><strong>🛡️ 保安端功能</strong></summary>

- **门禁管理**
  - 访客身份验证
  - 车辆进出登记
  - 异常情况处理
  
- **巡逻管理**
  - 巡逻路线规划
  - 定点打卡签到
  - 异常情况上报
  
- **值班管理**
  - 值班安排查看
  - 交接班记录
  - 工作日志填写

</details>

<details>
<summary><strong>🔧 维修工端功能</strong></summary>

- **工单管理**
  - 工单接收处理
  - 进度状态更新
  - 完工照片上传
  
- **任务调度**
  - 今日任务查看
  - 工作路线规划
  - 时间安排管理
  
- **材料管理**
  - 材料使用记录
  - 库存查询申请
  - 成本统计分析

</details>

### Web管理后台

<details>
<summary><strong>📊 数据仪表盘</strong></summary>

- **KPI指标展示**
  - 入住率统计
  - 报修完成率
  - 当月收费率
  - 业主满意度
  
- **数据可视化**
  - 趋势分析图表
  - 多维度统计
  - 实时数据监控
  - 异常预警提醒

</details>

<details>
<summary><strong>👥 用户与房产管理</strong></summary>

- **业主管理**
  - 业主信息管理
  - 注册审核流程
  - 房产绑定关系
  - 批量导入导出
  
- **员工管理**
  - 账号权限管理
  - 角色分配（RBAC）
  - 部门组织架构
  - 绩效考核统计
  
- **房产管理**
  - 楼栋单元管理
  - 房屋信息维护
  - 业主绑定关系
  - 物业费率设置

</details>

<details>
<summary><strong>📋 服务工单管理</strong></summary>

- **工单处理**
  - 全量工单查看
  - 智能分配系统
  - 进度监控跟踪
  - 超时预警处理
  
- **质量管控**
  - 服务质量评估
  - 处理效率统计
  - 客户满意度分析
  - 改进建议收集

</details>

<details>
<summary><strong>💰 财务管理</strong></summary>

- **账单管理**
  - 批量账单生成
  - 多种费用类型
  - 缴费状态跟踪
  - 逾期催收提醒
  
- **财务分析**
  - 收入统计报表
  - 费用结构分析
  - 收缴率趋势图
  - 财务数据导出

</details>

<details>
<summary><strong>📢 社区管理</strong></summary>

- **公告管理**
  - 公告编辑发布
  - 定时发送功能
  - 阅读统计分析
  - 置顶推荐设置
  
- **活动管理**
  - 活动策划发布
  - 报名人数统计
  - 活动效果评估
  - 照片视频分享

</details>

## 🛠️ 技术栈

### 前端技术

| 技术 | 版本 | 描述 |
|-----|------|------|
| Vue.js | 3.3.4 | 渐进式JavaScript框架 |
| Element Plus | 2.3.8 | Vue 3组件库 |
| ECharts | 5.4.3 | 数据可视化图表库 |
| Vue Router | 4.2.4 | 官方路由管理器 |
| Vuex | 4.0.2 | 状态管理模式 |
| Axios | 1.4.0 | HTTP客户端库 |
| Sass | 1.64.1 | CSS预处理器 |

### 后端技术

| 技术 | 版本 | 描述 |
|-----|------|------|
| Spring Boot | 2.7.0 | Java应用开发框架 |
| MyBatis Plus | 3.5.2 | 持久层框架 |
| Spring Security | 5.7.2 | 安全框架 |
| JWT | 0.11.5 | JSON Web Token |
| Redis | 6.2 | 内存数据库 |
| MySQL | 8.0 | 关系型数据库 |
| Swagger | 3.0.0 | API文档工具 |

### 小程序技术

| 技术 | 版本 | 描述 |
|-----|------|------|
| UniApp | 3.0 | 跨平台开发框架 |
| Vue.js | 3.x | 渐进式框架 |
| uView UI | 2.0 | 全面兼容的UI框架 |

## 📸 系统截图

### Web管理后台

<details>
<summary>点击查看截图</summary>

#### 数据仪表盘
![Dashboard](./docs/images/dashboard.png)

#### 用户管理
![User Management](./docs/images/user-management.png)

#### 工单管理
![Work Order](./docs/images/work-order.png)

#### 财务管理
![Finance](./docs/images/finance.png)

</details>

### 微信小程序

<details>
<summary>点击查看截图</summary>

#### 业主端首页
![Owner Home](./docs/images/owner-home.png)

#### 报修服务
![Repair Service](./docs/images/repair.png)

#### 保安端
![Guard](./docs/images/guard.png)

#### 维修工端
![Worker](./docs/images/worker.png)

</details>

## 🗂️ 项目结构

```
物业管理系统/
├── 📁 uniapp/                    # 微信小程序端
│   ├── 📁 pages/                 # 页面文件
│   ├── 📁 components/            # 公共组件
│   ├── 📁 static/                # 静态资源
│   ├── 📁 utils/                 # 工具函数
│   └── 📄 manifest.json          # 应用配置
│
├── 📁 frontend/                   # Web管理后台
│   ├── 📁 public/                # 静态资源
│   ├── 📁 src/
│   │   ├── 📁 components/        # 公共组件
│   │   ├── 📁 views/             # 页面组件
│   │   ├── 📁 router/            # 路由配置
│   │   ├── 📁 store/             # 状态管理
│   │   └── 📁 utils/             # 工具函数
│   ├── 📄 package.json           # 依赖配置
│   └── 📄 vue.config.js          # 构建配置
│
├── 📁 backend/                    # 后端服务
│   ├── 📁 src/main/java/         # Java源码
│   │   └── 📁 com/property/      # 项目包
│   │       ├── 📁 controller/    # 控制器层
│   │       ├── 📁 service/       # 服务层
│   │       ├── 📁 mapper/        # 数据访问层
│   │       └── 📁 entity/        # 实体类
│   ├── 📁 src/main/resources/    # 资源文件
│   └── 📄 pom.xml                # Maven配置
│
├── 📁 docs/                       # 项目文档
├── 📁 sql/                        # 数据库脚本
├── 📄 .gitignore                  # Git忽略文件
├── 📄 README.md                   # 项目说明
└── 📄 开发文档.md                  # 开发文档
```

## 🔧 开发指南

### 本地开发

1. **环境准备**
   - 安装Node.js 14+
   - 安装Java 8+
   - 安装MySQL 8.0+
   - 安装微信开发者工具

2. **数据库配置**
   ```sql
   -- 创建数据库
   CREATE DATABASE property_management CHARACTER SET utf8mb4;
   
   -- 导入表结构
   source sql/schema.sql;
   
   -- 导入测试数据
   source sql/data.sql;
   ```

3. **启动服务**
   ```bash
   # 后端服务
   cd backend && mvn spring-boot:run
   
   # 前端服务
   cd frontend && npm run serve
   
   # 小程序：使用微信开发者工具打开uniapp目录
   ```

### 代码规范

- **命名规范**：遵循各语言的命名约定
- **注释规范**：重要函数必须添加注释
- **提交规范**：使用语义化的commit message
- **测试规范**：核心功能必须编写测试用例

详细规范请查看 [开发文档](./开发文档.md)

## 📋 更新日志

### v1.0.0 (2024-01-25)

#### ✨ 新功能
- 🎉 项目初始化，完成基础架构搭建
- 📱 完成微信小程序端核心功能开发
- 💻 完成Web管理后台主要功能开发
- 🔧 完成后端API服务开发
- 📊 集成ECharts数据可视化
- 🎨 实现Discord风格UI设计

#### 🐛 问题修复
- 修复登录状态管理问题
- 修复分页查询bug
- 修复文件上传功能异常

#### 🔨 技术优化
- 优化数据库查询性能
- 完善异常处理机制
- 增强系统安全性

## 🤝 贡献指南

我们欢迎所有形式的贡献，无论是新功能、bug修复还是文档改进。

### 贡献步骤

1. **Fork 项目**
2. **创建特性分支** (`git checkout -b feature/AmazingFeature`)
3. **提交更改** (`git commit -m 'Add some AmazingFeature'`)
4. **推送到分支** (`git push origin feature/AmazingFeature`)
5. **开启 Pull Request**

### 开发规范

- 遵循现有的代码风格
- 为新功能添加测试
- 更新相关文档
- 确保所有测试通过

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系我们

- **项目地址**：https://github.com/your-username/property-management-system
- **问题反馈**：https://github.com/your-username/property-management-system/issues
- **邮箱**：contact@property-management.com

## 🙏 致谢

感谢以下开源项目为本项目提供的支持：

- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [Spring Boot](https://spring.io/projects/spring-boot) - Java应用开发框架
- [Element Plus](https://element-plus.org/) - Vue 3组件库
- [ECharts](https://echarts.apache.org/) - 数据可视化库
- [UniApp](https://uniapp.dcloud.io/) - 跨平台开发框架

---

<div align="center">

**如果这个项目对您有帮助，请给我们一个 ⭐**

Made with ❤️ by Property Management Team

</div>

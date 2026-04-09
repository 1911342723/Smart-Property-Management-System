# 微信小程序域名配置说明

## 问题描述
错误信息：`http://localhost:8081 不在以下 request 合法域名列表中`

这是因为微信小程序对网络请求有严格的域名白名单限制，只允许访问配置在小程序管理后台的合法域名。

## 解决方案

### 1. 开发阶段（推荐）
在微信开发者工具中关闭域名检查：
1. 打开微信开发者工具
2. 点击右上角的"详情"按钮
3. 在"本地设置"中勾选"不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书"
4. 重新运行小程序

### 2. 真机调试
如果需要在真机上调试，可以使用内网穿透工具：

#### 使用 ngrok
```bash
# 安装 ngrok
npm install -g ngrok

# 将本地 8081 端口映射为 HTTPS 域名
ngrok http 8081
```

然后修改 `config/index.js` 中的环境配置：
```javascript
const CURRENT_ENV = 'dev_remote'

// 在 ENV_CONFIG.dev_remote 中设置 ngrok 提供的 HTTPS 域名
dev_remote: {
  baseURL: 'https://xxxxxxxx.ngrok.io/api',
  debug: true
}
```

### 3. 生产环境
1. 准备一个有效的 HTTPS 域名
2. 在小程序管理后台配置域名白名单：
   - 登录微信公众平台
   - 进入小程序管理后台
   - 开发 → 开发管理 → 开发设置 → 服务器域名
   - 添加你的 API 域名到 request 合法域名列表

3. 修改配置文件：
```javascript
// config/index.js
const CURRENT_ENV = 'production'

production: {
  baseURL: 'https://your-api-domain.com/api',
  debug: false
}
```

## 环境切换
项目已经配置了多环境支持，可以通过修改 `config/index.js` 中的 `CURRENT_ENV` 变量来切换：

- `development`: 本地开发环境（localhost）
- `dev_remote`: 远程开发环境（ngrok等）
- `test`: 测试环境
- `production`: 生产环境

## 网络诊断工具
可以使用项目中的网络诊断页面来检查当前配置状态：
- 页面路径：`pages/test/network-test`
- 功能：检查域名合法性、网络连接、API状态等

## 注意事项
1. **小程序只支持 HTTPS**: 所有外网域名必须使用 HTTPS 协议
2. **域名备案**: 生产环境的域名需要进行 ICP 备案
3. **证书有效性**: HTTPS 证书必须有效且未过期
4. **端口限制**: 小程序支持 443、80 端口，其他端口需要特殊配置

## 常见错误
- `request:fail url not in domain list`: 域名不在白名单中
- `request:fail ssl hand shake error`: SSL 证书问题
- `request:fail timeout`: 请求超时，检查网络和服务器状态























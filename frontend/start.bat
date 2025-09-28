@echo off
echo 启动物业管理系统前端服务...
echo.

REM 检查Node.js是否安装
node -v >nul 2>&1
if errorlevel 1 (
    echo 错误: 未安装Node.js，请先安装Node.js 14.0.0或更高版本
    pause
    exit /b 1
)

REM 检查npm是否可用
npm -v >nul 2>&1
if errorlevel 1 (
    echo 错误: npm不可用
    pause
    exit /b 1
)

REM 检查是否已安装依赖
if not exist node_modules (
    echo 正在安装依赖包...
    npm install
    if errorlevel 1 (
        echo 错误: 依赖安装失败
        pause
        exit /b 1
    )
)

echo 启动开发服务器...
echo 请稍等，服务器启动后会自动打开浏览器
echo 访问地址: http://localhost:8080
echo 默认账号: admin / 123456
echo.
echo 按 Ctrl+C 停止服务器
echo.

npm run serve

pause

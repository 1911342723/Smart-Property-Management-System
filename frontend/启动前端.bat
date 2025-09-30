@echo off
chcp 65001
echo ================================
echo   物业管理系统 - Web前端启动
echo ================================
echo.

cd /d "%~dp0"

echo [1/2] 检查依赖...
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
    if errorlevel 1 (
        echo 依赖安装失败，请检查网络或尝试手动运行: npm install
        pause
        exit /b 1
    )
) else (
    echo 依赖已存在
)

echo.
echo [2/2] 启动开发服务器...
echo 前端地址: http://localhost:8080
echo 按 Ctrl+C 停止服务
echo.

call npm run serve

pause



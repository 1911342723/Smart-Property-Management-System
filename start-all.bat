@echo off
chcp 65001 >nul
echo ========================================
echo   智慧物业管理系统 - 一键启动所有服务
echo ========================================
echo.

echo 此脚本将依次启动：
echo 1. 后端服务 (端口: 8081)
echo 2. 前端管理系统 (端口: 8080)
echo.
echo 提示：每个服务将在新窗口中启动
echo.

pause


REM 启动前端
echo.
echo [2/2] 正在启动前端服务...
start "智慧物业-前端服务" cmd /k "%~dp0start-frontend.bat"
echo [✓] 前端服务启动窗口已打开
echo.

echo ========================================
echo   所有服务启动命令已执行
echo ========================================
echo.
echo 服务地址：
echo - 前端管理系统: http://localhost:8080
echo - 后端API服务: http://localhost:8081
echo - API文档: http://localhost:8081/doc.html
echo.
echo 管理员账号：
echo - 用户名: admin
echo - 密码: admin123
echo.
echo 小程序测试账号：
echo - 业主: 13800000101 / 123456
echo - 维修工: 13800000301 / 123456
echo.
echo 提示：
echo - 服务启动需要一些时间，请耐心等待
echo - 关闭启动窗口会停止对应的服务
echo - 可以关闭本窗口，不影响服务运行
echo.
echo ========================================

pause

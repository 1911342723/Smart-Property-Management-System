@echo off
chcp 65001
echo ====================================
echo 物业管理系统依赖修复脚本
echo ====================================
echo.

echo 正在清理Maven本地仓库中的损坏依赖...
if exist "%USERPROFILE%\.m2\repository\mysql\mysql-connector-java" (
    rmdir /s /q "%USERPROFILE%\.m2\repository\mysql\mysql-connector-java"
    echo 已清理MySQL驱动依赖
)

if exist "%USERPROFILE%\.m2\repository\com\mysql" (
    rmdir /s /q "%USERPROFILE%\.m2\repository\com\mysql"
    echo 已清理MySQL相关依赖
)

echo.
echo 正在清理项目编译文件...
if exist "target" (
    rmdir /s /q "target"
    echo 已清理target目录
)

echo.
echo 正在重新下载依赖...
mvn clean
if %errorlevel% neq 0 (
    echo 错误：Maven clean失败
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
mvn compile
if %errorlevel% neq 0 (
    echo 错误：项目编译失败，请检查代码和网络连接
    echo.
    echo 如果网络有问题，请尝试以下操作：
    echo 1. 检查网络连接
    echo 2. 配置Maven镜像源（如阿里云）
    echo 3. 重新运行此脚本
    pause
    exit /b 1
)

echo.
echo ====================================
echo 依赖修复完成！
echo ====================================
echo.
echo 现在可以运行 start.bat 启动服务

pause







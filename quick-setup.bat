@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo   智慧物业管理系统 - 快速配置向导
echo ========================================
echo.
echo 本向导将帮助您快速配置系统
echo.

pause

REM ==================== 数据库配置 ====================
echo.
echo ========================================
echo   第一步：配置数据库连接
echo ========================================
echo.

set /p DB_HOST="请输入MySQL主机地址（默认localhost）: "
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT="请输入MySQL端口（默认3306）: "
if "%DB_PORT%"=="" set DB_PORT=3306

set /p DB_NAME="请输入数据库名称（默认property_management）: "
if "%DB_NAME%"=="" set DB_NAME=property_management

set /p DB_USER="请输入数据库用户名（默认root）: "
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASS="请输入数据库密码: "

echo.
echo 数据库配置信息：
echo - 主机: %DB_HOST%
echo - 端口: %DB_PORT%
echo - 数据库: %DB_NAME%
echo - 用户名: %DB_USER%
echo - 密码: ********
echo.

set /p CONFIRM="确认以上配置？(Y/N): "
if /i not "%CONFIRM%"=="Y" (
    echo 配置已取消
    pause
    exit /b
)

REM ==================== 文件上传路径配置 ====================
echo.
echo ========================================
echo   第二步：配置文件上传路径
echo ========================================
echo.
echo 当前路径: %~dp0
echo.
set DEFAULT_UPLOAD_PATH=%~dp0backend\uploads\
set /p UPLOAD_PATH="请输入文件上传路径（默认: %DEFAULT_UPLOAD_PATH%）: "
if "%UPLOAD_PATH%"=="" set UPLOAD_PATH=%DEFAULT_UPLOAD_PATH%

REM 确保路径以反斜杠结尾
if not "!UPLOAD_PATH:~-1!"=="\" set UPLOAD_PATH=!UPLOAD_PATH!\

REM 创建上传目录
if not exist "%UPLOAD_PATH%" (
    echo 创建上传目录: %UPLOAD_PATH%
    mkdir "%UPLOAD_PATH%"
    mkdir "%UPLOAD_PATH%avatar"
    mkdir "%UPLOAD_PATH%repair"
    mkdir "%UPLOAD_PATH%activity"
    mkdir "%UPLOAD_PATH%general"
    echo [✓] 目录创建成功
)

REM ==================== 修改配置文件 ====================
echo.
echo ========================================
echo   第三步：更新配置文件
echo ========================================
echo.

set CONFIG_FILE=backend\src\main\resources\application.yml

if not exist "%CONFIG_FILE%" (
    echo [✗] 配置文件不存在: %CONFIG_FILE%
    pause
    exit /b 1
)

echo 正在备份配置文件...
copy "%CONFIG_FILE%" "%CONFIG_FILE%.backup" >nul
echo [✓] 备份完成: %CONFIG_FILE%.backup

echo 正在更新配置文件...

REM 转换路径格式（Windows路径 -> Java路径）
set JAVA_UPLOAD_PATH=%UPLOAD_PATH:\=/%

REM 创建临时文件
set TEMP_FILE=%CONFIG_FILE%.tmp

REM 读取并修改配置
(
    for /f "delims=" %%i in ('type "%CONFIG_FILE%"') do (
        set "line=%%i"
        
        REM 修改数据库配置
        echo !line! | findstr "url: jdbc:mysql" >nul
        if not errorlevel 1 (
            echo     url: jdbc:mysql://%DB_HOST%:%DB_PORT%/%DB_NAME%?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai
        ) else (
            echo !line! | findstr "username:" >nul
            if not errorlevel 1 (
                echo !line! | findstr "redis" >nul
                if errorlevel 1 (
                    echo     username: %DB_USER%
                ) else (
                    echo !line!
                )
            ) else (
                echo !line! | findstr "password:" >nul
                if not errorlevel 1 (
                    echo !line! | findstr "redis" >nul
                    if errorlevel 1 (
                        echo     password: %DB_PASS%
                    ) else (
                        echo !line!
                    )
                ) else (
                    echo !line! | findstr "path: D:/uploads" >nul
                    if not errorlevel 1 (
                        echo     path: %JAVA_UPLOAD_PATH%
                    ) else (
                        echo !line!
                    )
                )
            )
        )
    )
) > "%TEMP_FILE%"

REM 替换原文件
move /y "%TEMP_FILE%" "%CONFIG_FILE%" >nul

echo [✓] 配置文件更新完成
echo.

REM ==================== 创建数据库 ====================
echo ========================================
echo   第四步：初始化数据库
echo ========================================
echo.
echo 提示：需要手动导入数据库脚本
echo.
echo SQL文件位置：
echo %~dp0backend\src\main\resources\sql\property_management.sql
echo.
echo 导入方式（选择其一）：
echo.
echo 方法1 - 使用MySQL命令行：
echo   mysql -u %DB_USER% -p -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo   mysql -u %DB_USER% -p %DB_NAME% ^< backend\src\main\resources\sql\property_management.sql
echo.
echo 方法2 - 使用Navicat等可视化工具：
echo   1. 连接到MySQL
echo   2. 创建数据库 %DB_NAME%（字符集：utf8mb4）
echo   3. 运行SQL脚本文件
echo.

set /p IMPORT_NOW="是否现在使用命令行导入数据库？(Y/N): "
if /i "%IMPORT_NOW%"=="Y" (
    echo.
    echo 正在创建数据库...
    mysql -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    if errorlevel 1 (
        echo [✗] 数据库创建失败，请检查MySQL连接
    ) else (
        echo [✓] 数据库创建成功
        echo.
        echo 正在导入数据...
        mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% < backend\src\main\resources\sql\property_management.sql
        
        if errorlevel 1 (
            echo [✗] 数据导入失败
        ) else (
            echo [✓] 数据导入成功
        )
    )
) else (
    echo 请稍后手动导入数据库
)

echo.

REM ==================== 完成 ====================
echo ========================================
echo   配置完成
echo ========================================
echo.

echo 配置摘要：
echo - 数据库主机: %DB_HOST%:%DB_PORT%
echo - 数据库名称: %DB_NAME%
echo - 上传路径: %UPLOAD_PATH%
echo - 配置备份: %CONFIG_FILE%.backup
echo.

echo 下一步操作：
echo 1. 确认数据库已正确导入
echo 2. 运行 check-environment.bat 检查环境
echo 3. 运行 start-all.bat 启动所有服务
echo.

echo 如需修改配置，请编辑：
echo %CONFIG_FILE%
echo.

echo ========================================
echo.

pause

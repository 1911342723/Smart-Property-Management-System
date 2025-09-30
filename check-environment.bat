@echo off
chcp 65001 >nul
echo ========================================
echo   智慧物业管理系统 - 环境检查工具
echo ========================================
echo.

set ERROR_COUNT=0
set WARNING_COUNT=0

REM ==================== Java检查 ====================
echo [1/6] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [✗] 未检测到Java环境
    echo     请安装JDK 1.8或以上版本
    echo     下载地址: https://www.oracle.com/java/technologies/downloads/
    set /a ERROR_COUNT+=1
) else (
    echo [✓] Java环境正常
    java -version 2>&1 | findstr /C:"version"
)
echo.

REM ==================== Maven检查 ====================
echo [2/6] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [!] 未检测到Maven（可使用Maven Wrapper替代）
    set /a WARNING_COUNT+=1
) else (
    echo [✓] Maven环境正常
    mvn -version 2>&1 | findstr /C:"Apache Maven"
)
echo.

REM ==================== Node.js检查 ====================
echo [3/6] 检查Node.js环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [✗] 未检测到Node.js环境
    echo     请安装Node.js 14.x或以上版本
    echo     下载地址: https://nodejs.org/
    set /a ERROR_COUNT+=1
) else (
    echo [✓] Node.js环境正常
    node -v
)
echo.

REM ==================== npm检查 ====================
echo [4/6] 检查npm...
npm -v >nul 2>&1
if errorlevel 1 (
    echo [✗] npm未正确安装
    set /a ERROR_COUNT+=1
) else (
    echo [✓] npm正常
    npm -v
    
    REM 检查npm镜像
    for /f "delims=" %%i in ('npm config get registry') do set NPM_REGISTRY=%%i
    echo     当前镜像: !NPM_REGISTRY!
    echo !NPM_REGISTRY! | findstr "npmmirror" >nul
    if errorlevel 1 (
        echo [!] 建议使用淘宝镜像以加速下载
        echo     设置命令: npm config set registry https://registry.npmmirror.com
        set /a WARNING_COUNT+=1
    )
)
echo.

REM ==================== MySQL检查 ====================
echo [5/6] 检查MySQL服务...
sc query MySQL >nul 2>&1
if errorlevel 1 (
    sc query MySQL80 >nul 2>&1
    if errorlevel 1 (
        echo [✗] 未检测到MySQL服务
        echo     请安装MySQL 8.0
        echo     下载地址: https://dev.mysql.com/downloads/mysql/
        set /a ERROR_COUNT+=1
    ) else (
        sc query MySQL80 | findstr "RUNNING" >nul
        if errorlevel 1 (
            echo [!] MySQL服务未运行
            echo     请启动MySQL服务
            set /a WARNING_COUNT+=1
        ) else (
            echo [✓] MySQL服务正在运行
        )
    )
) else (
    sc query MySQL | findstr "RUNNING" >nul
    if errorlevel 1 (
        echo [!] MySQL服务未运行
        echo     请启动MySQL服务
        set /a WARNING_COUNT+=1
    ) else (
        echo [✓] MySQL服务正在运行
    )
)
echo.

REM ==================== 端口检查 ====================
echo [6/6] 检查端口占用情况...
netstat -ano | findstr ":8080" >nul
if errorlevel 1 (
    echo [✓] 端口8080未被占用（前端端口）
) else (
    echo [!] 端口8080已被占用
    echo     这可能导致前端服务无法启动
    set /a WARNING_COUNT+=1
)

netstat -ano | findstr ":8081" >nul
if errorlevel 1 (
    echo [✓] 端口8081未被占用（后端端口）
) else (
    echo [!] 端口8081已被占用
    echo     这可能导致后端服务无法启动
    set /a WARNING_COUNT+=1
)
echo.

REM ==================== 配置文件检查 ====================
echo ========================================
echo   配置文件检查
echo ========================================
echo.

REM 检查后端配置
if exist "backend\src\main\resources\application.yml" (
    echo [✓] 后端配置文件存在
    echo     位置: backend\src\main\resources\application.yml
    echo.
    echo     请确认以下配置：
    echo     1. 数据库地址、用户名、密码
    echo     2. 文件上传路径
    echo     3. JWT密钥（生产环境需修改）
) else (
    echo [✗] 后端配置文件不存在
    set /a ERROR_COUNT+=1
)
echo.

REM 检查SQL文件
if exist "backend\src\main\resources\sql\property_management.sql" (
    echo [✓] 数据库脚本文件存在
    echo     位置: backend\src\main\resources\sql\property_management.sql
    echo     请确保已导入到MySQL数据库
) else (
    echo [✗] 数据库脚本文件不存在
    set /a ERROR_COUNT+=1
)
echo.

REM 检查前端配置
if exist "frontend\package.json" (
    echo [✓] 前端项目配置存在
) else (
    echo [✗] 前端项目配置不存在
    set /a ERROR_COUNT+=1
)
echo.

REM ==================== 总结 ====================
echo ========================================
echo   检查结果总结
echo ========================================
echo.

if %ERROR_COUNT% EQU 0 (
    if %WARNING_COUNT% EQU 0 (
        echo [✓] 环境检查通过！
        echo.
        echo 您可以开始部署了：
        echo 1. 双击运行 start-backend.bat 启动后端
        echo 2. 双击运行 start-frontend.bat 启动前端
        echo 或者直接运行 start-all.bat 一键启动所有服务
    ) else (
        echo [!] 环境基本正常，但有 %WARNING_COUNT% 个警告
        echo.
        echo 建议处理上述警告后再启动服务
    )
) else (
    echo [✗] 发现 %ERROR_COUNT% 个错误
    echo.
    echo 请先解决上述错误，然后重新运行此检查工具
)
echo.

echo ========================================
echo.

pause

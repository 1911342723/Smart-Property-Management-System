@echo off
chcp 65001 >nul
echo ========================================
echo    智慧物业管理系统 - 后端启动脚本
echo ========================================
echo.

REM 检查Java环境
echo [1/4] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装JDK 1.8或以上版本
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [✓] Java环境检查通过
echo.

REM 检查Maven环境
echo [2/4] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [警告] 未检测到Maven，将尝试使用Maven Wrapper
    set USE_WRAPPER=true
) else (
    echo [✓] Maven环境检查通过
    set USE_WRAPPER=false
)
echo.

REM 进入后端目录
cd /d "%~dp0backend"

REM 检查是否已编译
echo [3/4] 检查项目编译状态...
if exist "target\property-management-1.0.0.jar" (
    echo [✓] 发现已编译的jar包
    set /p recompile="是否重新编译？(Y/N，默认N): "
    if /i "%recompile%"=="Y" (
        goto :compile
    ) else (
        goto :run
    )
) else (
    echo [提示] 未找到编译文件，开始编译...
    goto :compile
)

:compile
echo.
echo [编译中] 正在编译项目，这可能需要几分钟...
echo 提示：首次编译需要下载依赖包，请保持网络畅通
echo.

if "%USE_WRAPPER%"=="true" (
    if exist "mvnw.cmd" (
        call mvnw.cmd clean package -DskipTests
    ) else (
        echo [错误] Maven Wrapper未找到，请安装Maven或联系管理员
        pause
        exit /b 1
    )
) else (
    call mvn clean package -DskipTests
)

if errorlevel 1 (
    echo.
    echo [错误] 项目编译失败，请检查：
    echo 1. 网络连接是否正常
    echo 2. Maven配置是否正确
    echo 3. 查看上方错误信息
    echo.
    pause
    exit /b 1
)

echo [✓] 项目编译成功
echo.

:run
echo [4/4] 启动后端服务...
echo.
echo ========================================
echo    服务启动中，请稍候...
echo ========================================
echo.
echo 后端服务地址: http://localhost:8081
echo API文档地址: http://localhost:8081/doc.html
echo.
echo 提示：
echo - 启动成功后会看到 "Started PropertyManagementApplication"
echo - 如需停止服务，请按 Ctrl+C
echo - 关闭此窗口也会停止服务
echo.
echo ========================================
echo.

REM 运行jar包
java -jar target\property-management-1.0.0.jar

REM 如果程序异常退出
if errorlevel 1 (
    echo.
    echo [错误] 服务启动失败，请检查：
    echo 1. MySQL数据库是否已启动
    echo 2. 数据库连接配置是否正确
    echo 3. 端口8081是否被占用
    echo 4. 查看上方错误信息
    echo.
    echo 配置文件位置：backend\src\main\resources\application.yml
    echo.
)

pause

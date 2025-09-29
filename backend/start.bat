@echo off
chcp 65001
echo ====================================
echo 物业管理系统后端服务启动脚本
echo ====================================
echo.

echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误：未检测到Java环境，请先安装JDK 8或以上版本
    pause
    exit /b 1
)

echo.
echo 正在检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误：未检测到Maven环境，请先安装Maven
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
mvn clean compile -s settings.xml
if %errorlevel% neq 0 (
    echo 错误：项目编译失败，请检查代码和网络连接
    echo.
    echo 如果遇到依赖下载问题，请：
    echo 1. 运行 fix-dependencies.bat 修复依赖
    echo 2. 检查网络连接
    echo 3. 确保MySQL驱动版本正确
    pause
    exit /b 1
)

echo.
echo 编译成功，正在启动服务...
echo.
echo ====================================
echo 服务地址：http://localhost:8081/api
echo API文档：http://localhost:8081/api/swagger-ui/index.html
echo Druid监控：http://localhost:8081/api/druid/index.html
echo ====================================
echo.
echo 测试账号：
echo - admin/123456 （管理员）
echo - owner001/123456 （业主）
echo - worker001/123456 （维修工）
echo ====================================
echo.

mvn spring-boot:run -s settings.xml

pause

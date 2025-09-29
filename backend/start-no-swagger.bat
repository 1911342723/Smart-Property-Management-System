@echo off
chcp 65001
echo ====================================
echo 物业管理系统后端服务启动脚本（无Swagger）
echo ====================================
echo.
echo 注意：此脚本禁用了Swagger文档功能
echo 如果Swagger有兼容性问题，推荐使用此方式启动
echo.

echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误：未检测到Java环境，请先安装JDK 8或以上版本
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
mvn clean compile -s settings.xml
if %errorlevel% neq 0 (
    echo 错误：项目编译失败，请检查代码和网络连接
    pause
    exit /b 1
)

echo.
echo 编译成功，正在启动服务（无Swagger模式）...
echo.
echo ====================================
echo 服务地址：http://localhost:8081/api
echo Druid监控：http://localhost:8081/api/druid/index.html
echo 注意：Swagger文档已禁用
echo ====================================
echo.
echo 测试账号：
echo - admin/123456 （管理员）
echo - owner001/123456 （业主）
echo - worker001/123456 （维修工）
echo ====================================
echo.

mvn spring-boot:run -s settings.xml -Dspring-boot.run.profiles=no-swagger

pause







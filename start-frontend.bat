@echo off
chcp 65001 >nul
echo ========================================
echo   智慧物业管理系统 - 前端启动脚本
echo ========================================
echo.

REM 检查Node.js环境
echo [1/4] 检查Node.js环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Node.js环境，请先安装Node.js
    echo 下载地址: https://nodejs.org/
    echo 推荐版本: 14.x 或以上
    pause
    exit /b 1
)
echo [✓] Node.js环境检查通过
node -v
echo.

REM 检查npm
echo [2/4] 检查npm...
npm -v >nul 2>&1
if errorlevel 1 (
    echo [错误] npm未正确安装
    pause
    exit /b 1
)
echo [✓] npm检查通过
npm -v
echo.

REM 进入前端目录
cd /d "%~dp0frontend"

REM 检查node_modules
echo [3/4] 检查依赖包...
if exist "node_modules\" (
    echo [✓] 发现已安装的依赖包
    set /p reinstall="是否重新安装依赖？(Y/N，默认N): "
    if /i "%reinstall%"=="Y" (
        goto :install
    ) else (
        goto :serve
    )
) else (
    echo [提示] 未找到依赖包，开始安装...
    goto :install
)

:install
echo.
echo [安装中] 正在安装依赖包，这可能需要几分钟...
echo 提示：如果速度较慢，建议使用淘宝镜像
echo.

REM 检查是否使用淘宝镜像
npm config get registry | findstr "npmmirror" >nul
if errorlevel 1 (
    echo [提示] 检测到未使用国内镜像
    set /p use_mirror="是否切换到淘宝镜像以加速下载？(Y/N，推荐Y): "
    if /i "%use_mirror%"=="Y" (
        echo 正在设置淘宝镜像...
        npm config set registry https://registry.npmmirror.com
        echo [✓] 镜像设置成功
    )
)
echo.

REM 安装依赖
echo 开始安装依赖包...
call npm install

if errorlevel 1 (
    echo.
    echo [错误] 依赖包安装失败，请检查：
    echo 1. 网络连接是否正常
    echo 2. npm配置是否正确
    echo 3. 磁盘空间是否充足
    echo.
    echo 如果多次失败，可以尝试：
    echo 1. 删除 node_modules 文件夹
    echo 2. 删除 package-lock.json 文件
    echo 3. 重新运行此脚本
    echo.
    pause
    exit /b 1
)

echo [✓] 依赖包安装成功
echo.

:serve
echo [4/4] 启动前端开发服务器...
echo.
echo ========================================
echo    服务启动中，请稍候...
echo ========================================
echo.
echo 前端访问地址: http://localhost:8080
echo.
echo 默认管理员账号:
echo 用户名: admin
echo 密码: admin123
echo.
echo 提示：
echo - 启动成功后会自动打开浏览器
echo - 如需停止服务，请按 Ctrl+C
echo - 关闭此窗口也会停止服务
echo - 修改代码后会自动刷新页面
echo.
echo 重要提示：
echo - 请确保后端服务已启动（运行 start-backend.bat）
echo - 后端地址: http://localhost:8081
echo.
echo ========================================
echo.

REM 启动开发服务器
call npm run serve

REM 如果程序异常退出
if errorlevel 1 (
    echo.
    echo [错误] 服务启动失败，请检查：
    echo 1. 端口8080是否被占用
    echo 2. 依赖包是否完整安装
    echo 3. 查看上方错误信息
    echo.
)

pause

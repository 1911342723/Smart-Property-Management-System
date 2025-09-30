@echo off
echo 正在清理编译缓存...
if exist target (
    rmdir /s /q target
    echo 已删除target目录
) else (
    echo target目录不存在
)

echo 正在重新编译...
mvn clean compile

echo 编译完成！
pause



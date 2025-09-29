@echo off
echo 测试文件上传功能...

echo 创建测试文件...
echo This is a test file > test.txt

echo 上传测试文件...
curl -X POST -H "Content-Type: multipart/form-data" -F "file=@test.txt" -F "type=repair" http://localhost:8081/api/file/upload

echo.
echo 检查uploads目录...
if exist "D:\uploads" (
    echo D:\uploads 目录存在
    dir "D:\uploads" /s
) else (
    echo D:\uploads 目录不存在
)

echo 清理测试文件...
del test.txt

echo 测试完成
pause







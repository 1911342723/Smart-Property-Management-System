#!/bin/bash

# 文件上传测试脚本

echo "测试文件上传功能..."

# 创建一个测试图片文件
echo "创建测试文件..."
echo "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI9" | base64 -d > test.png

# 测试文件上传
echo "上传测试文件..."
curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@test.png" \
  -F "type=repair" \
  http://localhost:8081/api/file/upload

echo -e "\n\n检查uploads目录..."
ls -la uploads/ 2>/dev/null || echo "uploads目录不存在"

# 清理测试文件
rm -f test.png

echo "测试完成"







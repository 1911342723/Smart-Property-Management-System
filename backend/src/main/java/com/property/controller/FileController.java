package com.property.controller;

import com.property.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8081/api/file/}")
    private String urlPrefix;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("文件类型") @RequestParam(value = "type", defaultValue = "general") String type) {
        
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 检查文件大小（10MB限制）
            long maxSize = 10 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过10MB");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (!isAllowedContentType(contentType)) {
                return Result.error("不支持的文件类型：" + contentType);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + extension;

            // 创建目录结构
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String typeDir = sanitizeType(type);
            String relativePath = typeDir + "/" + dateDir + "/" + fileName;
            
            // 获取绝对路径
            String absoluteUploadPath = new File(uploadPath).getAbsolutePath();
            if (!absoluteUploadPath.endsWith(File.separator)) {
                absoluteUploadPath += File.separator;
            }
            
            // 确保基础上传目录存在
            File baseUploadDir = new File(absoluteUploadPath);
            if (!baseUploadDir.exists()) {
                boolean created = baseUploadDir.mkdirs();
                if (!created) {
                    return Result.error("无法创建上传目录：" + absoluteUploadPath);
                }
            }
            
            // 创建具体的文件目录
            String fullDirPath = absoluteUploadPath + typeDir + File.separator + dateDir.replace("/", File.separator);
            File uploadDir = new File(fullDirPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    return Result.error("无法创建文件目录：" + uploadDir.getAbsolutePath());
                }
            }

            // 保存文件 - 直接使用Files.copy避免transferTo的路径问题
            File destFile = new File(uploadDir, fileName);
            System.out.println("准备保存文件到: " + destFile.getAbsolutePath());
            
            try {
                java.nio.file.Files.copy(file.getInputStream(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("文件保存成功: " + destFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("文件保存失败: " + e.getMessage());
                throw new IOException("文件保存失败", e);
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("originalName", originalFilename);
            result.put("path", relativePath);
            result.put("url", urlPrefix + relativePath);
            result.put("size", file.getSize());
            result.put("contentType", contentType);

            return Result.success("上传成功", result);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取文件")
    @GetMapping("/{type}/{year}/{month}/{day}/{fileName}")
    public void getFile(
            @PathVariable String type,
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String fileName,
            javax.servlet.http.HttpServletResponse response) {
        
        try {
            String filePath = uploadPath + type + "/" + year + "/" + month + "/" + day + "/" + fileName;
            File file = new File(filePath);
            
            if (!file.exists()) {
                response.setStatus(404);
                return;
            }

            // 设置响应头
            String contentType = getContentTypeByExtension(getFileExtension(fileName));
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "max-age=86400"); // 缓存1天

            // 输出文件
            java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();

        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedContentType(String contentType) {
        if (contentType == null) return false;
        
        return contentType.startsWith("image/") || 
               contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 清理类型名称
     */
    private String sanitizeType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "general";
        }
        return type.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentTypeByExtension(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }
}



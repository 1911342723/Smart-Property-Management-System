package com.property.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 文件上传配置
 * 启动时创建必要的目录
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Component
public class FileUploadConfig implements CommandLineRunner {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Override
    public void run(String... args) throws Exception {
        // 获取绝对路径
        File uploadDir = new File(uploadPath);
        String absolutePath = uploadDir.getAbsolutePath();
        
        System.out.println("配置的上传路径: " + uploadPath);
        System.out.println("解析的绝对路径: " + absolutePath);
        
        // 创建上传目录
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                System.out.println("创建上传目录成功: " + absolutePath);
            } else {
                System.err.println("创建上传目录失败: " + absolutePath);
            }
        } else {
            System.out.println("上传目录已存在: " + absolutePath);
        }

        // 创建常用的子目录
        String[] subDirs = {"repair", "avatar", "general", "document"};
        for (String subDir : subDirs) {
            File dir = new File(uploadDir, subDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    System.out.println("创建子目录: " + dir.getAbsolutePath());
                } else {
                    System.err.println("创建子目录失败: " + dir.getAbsolutePath());
                }
            }
        }
    }
}

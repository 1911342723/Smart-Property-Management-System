package com.property.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 * 用于生成BCrypt哈希密码
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String plainPassword = "123456";
        String hashedPassword = encoder.encode(plainPassword);
        
        System.out.println("原始密码: " + plainPassword);
        System.out.println("BCrypt哈希: " + hashedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(plainPassword, hashedPassword);
        System.out.println("密码验证: " + matches);
        
        // 生成多个哈希值供选择
        System.out.println("\n生成的BCrypt哈希值:");
        for (int i = 0; i < 5; i++) {
            String hash = encoder.encode(plainPassword);
            System.out.println((i+1) + ". " + hash);
        }
    }
}


















package com.property.controller;

import com.property.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证应用是否正常启动
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "系统测试")
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "物业管理系统运行正常");
        data.put("version", "1.0.0");
        
        return Result.success("系统运行正常", data);
    }

    @ApiOperation("系统信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("application", "property-management");
        data.put("description", "物业管理系统后端服务");
        data.put("version", "1.0.0");
        data.put("profiles", System.getProperty("spring.profiles.active", "default"));
        data.put("java_version", System.getProperty("java.version"));
        data.put("spring_boot_version", "2.7.18");
        
        return Result.success("系统信息", data);
    }
}







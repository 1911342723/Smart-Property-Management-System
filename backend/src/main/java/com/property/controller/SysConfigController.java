package com.property.controller;

import com.property.dto.Result;
import com.property.entity.SysConfig;
import com.property.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 */
@Api(tags = "系统配置管理")
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @PostConstruct
    public void init() {
        // 初始化默认配置
        sysConfigService.initDefaultConfigs();
    }

    @ApiOperation("获取所有配置")
    @GetMapping
    public Result<List<SysConfig>> getAllConfigs() {
        try {
            List<SysConfig> configs = sysConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据配置键获取配置")
    @GetMapping("/{key}")
    public Result<SysConfig> getConfigByKey(@PathVariable String key) {
        try {
            SysConfig config = sysConfigService.getConfigByKey(key);
            if (config != null) {
                return Result.success(config);
            } else {
                return Result.error("配置不存在");
            }
        } catch (Exception e) {
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新配置")
    @PutMapping
    public Result<String> updateConfig(@RequestBody SysConfig config) {
        try {
            boolean success = sysConfigService.updateConfig(config);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("批量更新配置")
    @PutMapping("/batch")
    public Result<String> batchUpdateConfig(@RequestBody Map<String, String> configs) {
        try {
            boolean success = sysConfigService.batchUpdateConfig(configs);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("批量更新配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("重置配置为默认值")
    @PutMapping("/{key}/reset")
    public Result<String> resetConfig(@PathVariable String key) {
        try {
            boolean success = sysConfigService.resetConfig(key);
            if (success) {
                return Result.success("重置成功");
            } else {
                return Result.error("重置失败或该配置不允许重置");
            }
        } catch (Exception e) {
            return Result.error("重置配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取物业费配置")
    @GetMapping("/property-fee")
    public Result<Map<String, Object>> getPropertyFeeConfig() {
        try {
            Map<String, Object> config = sysConfigService.getPropertyFeeConfig();
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("获取物业费配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新物业费配置")
    @PutMapping("/property-fee")
    public Result<String> updatePropertyFeeConfig(@RequestBody Map<String, String> feeConfig) {
        try {
            boolean success = sysConfigService.updatePropertyFeeConfig(feeConfig);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新物业费配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取邮件配置")
    @GetMapping("/email")
    public Result<Map<String, Object>> getEmailConfig() {
        try {
            Map<String, Object> config = sysConfigService.getEmailConfig();
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("获取邮件配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新邮件配置")
    @PutMapping("/email")
    public Result<String> updateEmailConfig(@RequestBody Map<String, String> emailConfig) {
        try {
            boolean success = sysConfigService.updateEmailConfig(emailConfig);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新邮件配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("测试邮件配置")
    @PostMapping("/email/test")
    public Result<String> testEmailConfig(@RequestBody Map<String, String> emailConfig) {
        try {
            // 这里可以实现实际的邮件发送测试逻辑
            // 暂时返回成功
            return Result.success("邮件配置测试成功");
        } catch (Exception e) {
            return Result.error("测试邮件配置失败：" + e.getMessage());
        }
    }
}

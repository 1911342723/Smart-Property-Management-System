package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.property.entity.SysConfig;
import com.property.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务
 */
@Service
public class SysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 获取所有配置
     */
    public List<SysConfig> getAllConfigs() {
        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("config_group", "sort");
        return sysConfigMapper.selectList(wrapper);
    }

    /**
     * 根据配置键获取配置
     */
    public SysConfig getConfigByKey(String key) {
        return sysConfigMapper.selectByKey(key);
    }

    /**
     * 根据配置键获取配置值
     */
    public String getConfigValue(String key) {
        SysConfig config = getConfigByKey(key);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 更新配置
     */
    public boolean updateConfig(SysConfig config) {
        if (config.getId() == null) {
            // 如果没有ID，尝试通过key查找
            SysConfig existConfig = getConfigByKey(config.getConfigKey());
            if (existConfig != null) {
                config.setId(existConfig.getId());
            }
        }
        
        config.setUpdateTime(LocalDateTime.now());
        return sysConfigMapper.updateById(config) > 0;
    }

    /**
     * 批量更新配置
     */
    public boolean batchUpdateConfig(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            SysConfig config = getConfigByKey(entry.getKey());
            if (config != null) {
                config.setConfigValue(entry.getValue());
                config.setUpdateTime(LocalDateTime.now());
                sysConfigMapper.updateById(config);
            }
        }
        return true;
    }

    /**
     * 重置配置为默认值
     */
    public boolean resetConfig(String key) {
        SysConfig config = getConfigByKey(key);
        if (config != null && config.getIsSystem() == 1) {
            // 获取默认值（这里简化处理，实际应该从配置文件或常量中获取）
            String defaultValue = getDefaultValue(key);
            config.setConfigValue(defaultValue);
            config.setUpdateTime(LocalDateTime.now());
            return sysConfigMapper.updateById(config) > 0;
        }
        return false;
    }

    /**
     * 获取物业费配置
     */
    public Map<String, Object> getPropertyFeeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("propertyFeePerSqm", getConfigValue("property.fee.per.sqm"));
        config.put("parkingFeeMonthly", getConfigValue("parking.fee.monthly"));
        return config;
    }

    /**
     * 更新物业费配置
     */
    public boolean updatePropertyFeeConfig(Map<String, String> feeConfig) {
        Map<String, String> configMap = new HashMap<>();
        if (feeConfig.containsKey("propertyFeePerSqm")) {
            configMap.put("property.fee.per.sqm", feeConfig.get("propertyFeePerSqm"));
        }
        if (feeConfig.containsKey("parkingFeeMonthly")) {
            configMap.put("parking.fee.monthly", feeConfig.get("parkingFeeMonthly"));
        }
        return batchUpdateConfig(configMap);
    }

    /**
     * 获取邮件配置
     */
    public Map<String, Object> getEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("host", getConfigValue("email.host"));
        config.put("port", getConfigValue("email.port"));
        config.put("username", getConfigValue("email.username"));
        config.put("password", getConfigValue("email.password"));
        config.put("from", getConfigValue("email.from"));
        config.put("ssl", getConfigValue("email.ssl"));
        return config;
    }

    /**
     * 更新邮件配置
     */
    public boolean updateEmailConfig(Map<String, String> emailConfig) {
        Map<String, String> configMap = new HashMap<>();
        if (emailConfig.containsKey("host")) {
            configMap.put("email.host", emailConfig.get("host"));
        }
        if (emailConfig.containsKey("port")) {
            configMap.put("email.port", emailConfig.get("port"));
        }
        if (emailConfig.containsKey("username")) {
            configMap.put("email.username", emailConfig.get("username"));
        }
        if (emailConfig.containsKey("password")) {
            configMap.put("email.password", emailConfig.get("password"));
        }
        if (emailConfig.containsKey("from")) {
            configMap.put("email.from", emailConfig.get("from"));
        }
        if (emailConfig.containsKey("ssl")) {
            configMap.put("email.ssl", emailConfig.get("ssl"));
        }
        return batchUpdateConfig(configMap);
    }

    /**
     * 获取默认配置值
     */
    private String getDefaultValue(String key) {
        // 这里简化处理，实际应该从配置文件或常量中获取
        Map<String, String> defaults = new HashMap<>();
        defaults.put("property.fee.per.sqm", "2.5");
        defaults.put("parking.fee.monthly", "200");
        defaults.put("email.port", "587");
        defaults.put("email.ssl", "true");
        return defaults.getOrDefault(key, "");
    }

    /**
     * 初始化默认配置（如果配置表为空）
     */
    public void initDefaultConfigs() {
        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        long count = sysConfigMapper.selectCount(wrapper);
        
        if (count == 0) {
            // 插入默认配置
            insertDefaultConfig("property.fee.per.sqm", "2.5", "物业费单价", "物业费每平米单价(元)", 
                              "DECIMAL", "PROPERTY_FEE", 1, 1);
            insertDefaultConfig("parking.fee.monthly", "200", "停车费", "停车费月租(元)", 
                              "DECIMAL", "PROPERTY_FEE", 1, 2);
            
            insertDefaultConfig("email.host", "smtp.example.com", "邮件服务器", "SMTP服务器地址", 
                              "STRING", "EMAIL", 1, 1);
            insertDefaultConfig("email.port", "587", "邮件端口", "SMTP服务器端口", 
                              "INTEGER", "EMAIL", 1, 2);
            insertDefaultConfig("email.username", "", "邮件用户名", "发件人邮箱账号", 
                              "STRING", "EMAIL", 0, 3);
            insertDefaultConfig("email.password", "", "邮件密码", "发件人邮箱密码", 
                              "PASSWORD", "EMAIL", 0, 4);
            insertDefaultConfig("email.from", "", "发件人", "发件人邮箱地址", 
                              "STRING", "EMAIL", 0, 5);
            insertDefaultConfig("email.ssl", "true", "启用SSL", "是否启用SSL加密", 
                              "BOOLEAN", "EMAIL", 1, 6);
        }
    }

    private void insertDefaultConfig(String key, String value, String name, String desc,
                                     String type, String group, int isSystem, int sort) {
        SysConfig config = new SysConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setConfigName(name);
        config.setDescription(desc);
        config.setConfigType(type);
        config.setConfigGroup(group);
        config.setIsSystem(isSystem);
        config.setStatus(1);
        config.setSort(sort);
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        sysConfigMapper.insert(config);
    }
}

package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统配置实体类
 */
@ApiModel(description = "系统配置")
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    @ApiModelProperty(value = "配置键")
    @TableField("config_key")
    private String configKey;

    @ApiModelProperty(value = "配置值")
    @TableField("config_value")
    private String configValue;

    @ApiModelProperty(value = "配置名称")
    @TableField("config_name")
    private String configName;

    @ApiModelProperty(value = "配置描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "配置类型")
    @TableField("config_type")
    private String configType;

    @ApiModelProperty(value = "配置分组")
    @TableField("config_group")
    private String configGroup;

    @ApiModelProperty(value = "是否系统内置(0=否 1=是)")
    @TableField("is_system")
    private Integer isSystem;

    @ApiModelProperty(value = "状态(0=停用 1=启用)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    // Getters and Setters
    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public Integer getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Integer isSystem) {
        this.isSystem = isSystem;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

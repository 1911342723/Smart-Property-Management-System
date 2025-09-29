package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 系统角色实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "系统角色")
@TableName("sys_role")
public class SysRole extends BaseEntity {

    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    @NotBlank(message = "角色编码不能为空")
    @TableField("role_code")
    private String roleCode;

    @ApiModelProperty(value = "角色描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}







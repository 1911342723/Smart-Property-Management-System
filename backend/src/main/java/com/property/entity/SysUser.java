package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "系统用户")
@TableName("sys_user")
public class SysUser extends BaseEntity {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "密码")
    @JsonIgnore
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    @TableField("real_name")
    private String realName;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "头像地址")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "性别：MALE-男，FEMALE-女，OTHER-其他")
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    private String birthday;

    @ApiModelProperty(value = "个人签名")
    @TableField("signature")
    private String signature;

    @ApiModelProperty(value = "紧急联系人")
    @TableField("emergency_contact")
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联系电话")
    @TableField("emergency_phone")
    private String emergencyPhone;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "用户类型：ADMIN-管理员，OWNER-业主，GUARD-保安，WORKER-维修工")
    @NotBlank(message = "用户类型不能为空")
    @TableField("user_type")
    private String userType;

    @ApiModelProperty(value = "最后登录时间")
    @TableField("last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}







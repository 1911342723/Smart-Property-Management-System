package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 活动报名实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "活动报名")
@TableName("activity_registration")
public class ActivityRegistration extends BaseEntity {

    @ApiModelProperty(value = "活动ID")
    @NotNull(message = "活动ID不能为空")
    @TableField("activity_id")
    private Long activityId;

    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "参与人数")
    @TableField("participants")
    private Integer participants;

    @ApiModelProperty(value = "联系人姓名")
    @NotBlank(message = "联系人姓名不能为空")
    @TableField("contact_name")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    @TableField("contact_phone")
    private String contactPhone;

    @ApiModelProperty(value = "紧急联系人")
    @TableField("emergency_contact")
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联系电话")
    @TableField("emergency_phone")
    private String emergencyPhone;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "状态：REGISTERED-已报名，CONFIRMED-已确认，CANCELLED-已取消，ATTENDED-已参加")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "报名时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("registration_time")
    private LocalDateTime registrationTime;

    @ApiModelProperty(value = "确认时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    @ApiModelProperty(value = "取消时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @ApiModelProperty(value = "取消原因")
    @TableField("cancel_reason")
    private String cancelReason;

    // 关联信息
    @ApiModelProperty(value = "活动信息")
    @TableField(exist = false)
    private Activity activity;

    @ApiModelProperty(value = "用户信息")
    @TableField(exist = false)
    private SysUser user;

    @ApiModelProperty(value = "用户姓名")
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty(value = "活动标题")
    @TableField(exist = false)
    private String activityTitle;

    // Getter and Setter methods
    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }
}




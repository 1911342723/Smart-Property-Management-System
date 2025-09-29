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
 * 访客二维码实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "访客二维码")
@TableName("visitor_qr_code")
public class VisitorQrCode extends BaseEntity {

    @ApiModelProperty(value = "访客手机号")
    @NotBlank(message = "访客手机号不能为空")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "访客姓名")
    @NotBlank(message = "访客姓名不能为空")
    @TableField("visitor_name")
    private String visitorName;

    @ApiModelProperty(value = "业主ID")
    @NotNull(message = "业主ID不能为空")
    @TableField("owner_id")
    private Long ownerId;

    @ApiModelProperty(value = "业主电话")
    @TableField("owner_phone")
    private String ownerPhone;

    @ApiModelProperty(value = "访问目的")
    @TableField("visit_purpose")
    private String visitPurpose;

    @ApiModelProperty(value = "预计到达时间")
    @NotNull(message = "预计到达时间不能为空")
    @TableField("planned_arrival")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedArrival;

    @ApiModelProperty(value = "预计离开时间")
    @TableField("planned_departure")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedDeparture;

    @ApiModelProperty(value = "二维码唯一标识")
    @NotBlank(message = "二维码标识不能为空")
    @TableField("qr_code")
    private String qrCode;

    @ApiModelProperty(value = "二维码内容（JSON）")
    @NotBlank(message = "二维码内容不能为空")
    @TableField("qr_content")
    private String qrContent;

    @ApiModelProperty(value = "状态")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "使用时间")
    @TableField("used_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedTime;

    @ApiModelProperty(value = "验证保安ID")
    @TableField("guard_id")
    private Long guardId;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    @ApiModelProperty(value = "保安姓名")
    @TableField(exist = false)
    private String guardName;

    // Getters and Setters
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getVisitPurpose() {
        return visitPurpose;
    }

    public void setVisitPurpose(String visitPurpose) {
        this.visitPurpose = visitPurpose;
    }

    public LocalDateTime getPlannedArrival() {
        return plannedArrival;
    }

    public void setPlannedArrival(LocalDateTime plannedArrival) {
        this.plannedArrival = plannedArrival;
    }

    public LocalDateTime getPlannedDeparture() {
        return plannedDeparture;
    }

    public void setPlannedDeparture(LocalDateTime plannedDeparture) {
        this.plannedDeparture = plannedDeparture;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(LocalDateTime usedTime) {
        this.usedTime = usedTime;
    }

    public Long getGuardId() {
        return guardId;
    }

    public void setGuardId(Long guardId) {
        this.guardId = guardId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getGuardName() {
        return guardName;
    }

    public void setGuardName(String guardName) {
        this.guardName = guardName;
    }
}





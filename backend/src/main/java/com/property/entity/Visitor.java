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
 * 访客实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "访客")
@TableName("visitor")
public class Visitor extends BaseEntity {

    @ApiModelProperty(value = "访客姓名")
    @NotBlank(message = "访客姓名不能为空")
    @TableField("visitor_name")
    private String visitorName;

    @ApiModelProperty(value = "访客电话")
    @NotBlank(message = "访客电话不能为空")
    @TableField("visitor_phone")
    private String visitorPhone;

    @ApiModelProperty(value = "身份证号")
    @TableField("id_card")
    private String idCard;

    @ApiModelProperty(value = "访问房屋ID")
    @TableField("room_id")
    private Long roomId;

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

    @ApiModelProperty(value = "实际到达时间")
    @TableField("actual_arrival")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualArrival;

    @ApiModelProperty(value = "实际离开时间")
    @TableField("actual_departure")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDeparture;

    @ApiModelProperty(value = "状态")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "二维码地址")
    @TableField("qr_code")
    private String qrCode;

    @ApiModelProperty(value = "接待保安ID")
    @TableField("guard_id")
    private Long guardId;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    @ApiModelProperty(value = "房屋地址")
    @TableField(exist = false)
    private String roomAddress;

    @ApiModelProperty(value = "保安姓名")
    @TableField(exist = false)
    private String guardName;

    // Getters and Setters
    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public LocalDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public LocalDateTime getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(LocalDateTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Long getGuardId() {
        return guardId;
    }

    public void setGuardId(Long guardId) {
        this.guardId = guardId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getGuardName() {
        return guardName;
    }

    public void setGuardName(String guardName) {
        this.guardName = guardName;
    }
}

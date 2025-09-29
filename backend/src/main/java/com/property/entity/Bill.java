package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "账单")
@TableName("bill")
public class Bill extends BaseEntity {

    @ApiModelProperty(value = "账单编号")
    @TableField("bill_no")
    private String billNo;

    @ApiModelProperty(value = "房屋ID")
    @NotNull(message = "房屋ID不能为空")
    @TableField("room_id")
    private Long roomId;

    @ApiModelProperty(value = "业主ID")
    @NotNull(message = "业主ID不能为空")
    @TableField("owner_id")
    private Long ownerId;

    @ApiModelProperty(value = "账单类型")
    @NotBlank(message = "账单类型不能为空")
    @TableField("bill_type")
    private String billType;

    @ApiModelProperty(value = "计费周期")
    @NotBlank(message = "计费周期不能为空")
    @TableField("billing_period")
    private String billingPeriod;

    @ApiModelProperty(value = "应缴金额")
    @NotNull(message = "应缴金额不能为空")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "已缴金额")
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "状态")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "缴费截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("due_date")
    private LocalDate dueDate;

    @ApiModelProperty(value = "缴费日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("paid_date")
    private LocalDateTime paidDate;

    @ApiModelProperty(value = "费用说明")
    @TableField("description")
    private String description;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "房屋地址")
    @TableField(exist = false)
    private String roomAddress;

    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
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

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}







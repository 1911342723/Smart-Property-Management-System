package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "支付记录")
@TableName("payment")
public class Payment extends BaseEntity {

    @ApiModelProperty(value = "支付编号")
    @TableField("payment_no")
    private String paymentNo;

    @ApiModelProperty(value = "账单ID")
    @NotNull(message = "账单ID不能为空")
    @TableField("bill_id")
    private Long billId;

    @ApiModelProperty(value = "业主ID")
    @NotNull(message = "业主ID不能为空")
    @TableField("owner_id")
    private Long ownerId;

    @ApiModelProperty(value = "支付金额")
    @NotNull(message = "支付金额不能为空")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付方式")
    @NotBlank(message = "支付方式不能为空")
    @TableField("payment_method")
    private String paymentMethod;

    @ApiModelProperty(value = "支付状态")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "第三方交易号")
    @TableField("transaction_id")
    private String transactionId;

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "账单编号")
    @TableField(exist = false)
    private String billNo;

    @ApiModelProperty(value = "账单类型")
    @TableField(exist = false)
    private String billType;

    @ApiModelProperty(value = "计费周期")
    @TableField(exist = false)
    private String billingPeriod;

    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    @ApiModelProperty(value = "房屋地址")
    @TableField(exist = false)
    private String roomAddress;

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
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

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }
}

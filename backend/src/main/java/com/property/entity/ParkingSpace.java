package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@ApiModel(description = "车位")
@TableName("parking_space")
public class ParkingSpace extends BaseEntity {

    @ApiModelProperty(value = "车位编号")
    @NotBlank(message = "车位编号不能为空")
    @TableField("space_no")
    private String spaceNo;

    @ApiModelProperty(value = "所属区域")
    @NotBlank(message = "所属区域不能为空")
    @TableField("area_code")
    private String areaCode;

    @ApiModelProperty(value = "车位类型：FIXED-固定车位，TEMPORARY-临停车位，CHARGING-充电车位，VISITOR-访客车位")
    @TableField("space_type")
    private String spaceType;

    @ApiModelProperty(value = "状态：AVAILABLE-空闲，OCCUPIED-已出租，RESERVED-预留，DISABLED-停用")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "绑定业主ID")
    @TableField("owner_id")
    private Long ownerId;

    @ApiModelProperty(value = "车牌号")
    @TableField("vehicle_no")
    private String vehicleNo;

    @ApiModelProperty(value = "月租费用")
    @TableField("monthly_fee")
    private BigDecimal monthlyFee;

    @ApiModelProperty(value = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("expire_date")
    private LocalDate expireDate;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    @ApiModelProperty(value = "业主手机号")
    @TableField(exist = false)
    private String ownerPhone;

    public String getSpaceNo() {
        return spaceNo;
    }

    public void setSpaceNo(String spaceNo) {
        this.spaceNo = spaceNo;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
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

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}
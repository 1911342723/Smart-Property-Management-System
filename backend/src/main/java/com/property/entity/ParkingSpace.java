package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 车位实体类
 */
@ApiModel(description = "车位")
@TableName("parking_space")
public class ParkingSpace extends BaseEntity {

    @ApiModelProperty(value = "车位编号")
    @NotBlank(message = "车位编号不能为空")
    @TableField("space_no")
    private String spaceNo;

    @ApiModelProperty(value = "车位位置")
    @TableField("location")
    private String location;

    @ApiModelProperty(value = "车位状态：IDLE-空闲，OCCUPIED-使用中，SOLD-已售")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "绑定的业主ID")
    @TableField("owner_id")
    private Long ownerId;

    @ApiModelProperty(value = "绑定的车牌号")
    @TableField("vehicle_no")
    private String vehicleNo;

    @ApiModelProperty(value = "停车管理费")
    @TableField("parking_fee")
    private BigDecimal parkingFee;

    // 非数据库字段
    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    @ApiModelProperty(value = "联系电话")
    @TableField(exist = false)
    private String ownerPhone;

    public String getSpaceNo() {
        return spaceNo;
    }

    public void setSpaceNo(String spaceNo) {
        this.spaceNo = spaceNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public BigDecimal getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(BigDecimal parkingFee) {
        this.parkingFee = parkingFee;
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

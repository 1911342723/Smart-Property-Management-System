package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 楼栋实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "楼栋")
@TableName("building")
public class Building extends BaseEntity {

    @ApiModelProperty(value = "楼栋名称")
    @NotBlank(message = "楼栋名称不能为空")
    @TableField("building_name")
    private String buildingName;

    @ApiModelProperty(value = "楼栋编号")
    @NotBlank(message = "楼栋编号不能为空")
    @TableField("building_no")
    private String buildingNo;

    @ApiModelProperty(value = "总楼层数")
    @NotNull(message = "总楼层数不能为空")
    @TableField("total_floors")
    private Integer totalFloors;

    @ApiModelProperty(value = "总单元数")
    @NotNull(message = "总单元数不能为空")
    @TableField("total_units")
    private Integer totalUnits;

    @ApiModelProperty(value = "总房间数")
    @NotNull(message = "总房间数不能为空")
    @TableField("total_rooms")
    private Integer totalRooms;

    @ApiModelProperty(value = "详细地址")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}







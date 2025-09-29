package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 单元实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "单元")
@TableName("unit")
public class Unit extends BaseEntity {

    @ApiModelProperty(value = "楼栋ID")
    @NotNull(message = "楼栋ID不能为空")
    @TableField("building_id")
    private Long buildingId;

    @ApiModelProperty(value = "单元名称")
    @NotBlank(message = "单元名称不能为空")
    @TableField("unit_name")
    private String unitName;

    @ApiModelProperty(value = "单元编号")
    @NotBlank(message = "单元编号不能为空")
    @TableField("unit_no")
    private String unitNo;

    @ApiModelProperty(value = "总楼层数")
    @NotNull(message = "总楼层数不能为空")
    @TableField("total_floors")
    private Integer totalFloors;

    @ApiModelProperty(value = "每层房间数")
    @NotNull(message = "每层房间数不能为空")
    @TableField("rooms_per_floor")
    private Integer roomsPerFloor;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "楼栋名称")
    @TableField(exist = false)
    private String buildingName;

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Integer getRoomsPerFloor() {
        return roomsPerFloor;
    }

    public void setRoomsPerFloor(Integer roomsPerFloor) {
        this.roomsPerFloor = roomsPerFloor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}





package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 房屋实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "房屋")
@TableName("room")
public class Room extends BaseEntity {

    @ApiModelProperty(value = "楼栋ID")
    @NotNull(message = "楼栋ID不能为空")
    @TableField("building_id")
    private Long buildingId;

    @ApiModelProperty(value = "单元ID")
    @NotNull(message = "单元ID不能为空")
    @TableField("unit_id")
    private Long unitId;

    @ApiModelProperty(value = "房屋编号")
    @NotBlank(message = "房屋编号不能为空")
    @TableField("room_no")
    private String roomNo;

    @ApiModelProperty(value = "楼层")
    @NotNull(message = "楼层不能为空")
    @TableField("floor")
    private Integer floor;

    @ApiModelProperty(value = "房屋类型：RESIDENTIAL-住宅，COMMERCIAL-商用，OFFICE-办公")
    @NotBlank(message = "房屋类型不能为空")
    @TableField("room_type")
    private String roomType;

    @ApiModelProperty(value = "建筑面积（平方米）")
    @TableField("area")
    private BigDecimal area;

    @ApiModelProperty(value = "房屋状态：VACANT-空置，OCCUPIED-已入住，RENTED-出租")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "物业费（元/月）")
    @TableField("property_fee")
    private BigDecimal propertyFee;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "楼栋名称")
    @TableField(exist = false)
    private String buildingName;

    @ApiModelProperty(value = "单元名称")
    @TableField(exist = false)
    private String unitName;

    @ApiModelProperty(value = "业主姓名")
    @TableField(exist = false)
    private String ownerName;

    @ApiModelProperty(value = "用户与房屋关系类型：OWNER-业主，TENANT-租户")
    @TableField(exist = false)
    private String relationType;

    @ApiModelProperty(value = "绑定的业主列表")
    @TableField(exist = false)
    private List<SysUser> owners;

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPropertyFee() {
        return propertyFee;
    }

    public void setPropertyFee(BigDecimal propertyFee) {
        this.propertyFee = propertyFee;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public List<SysUser> getOwners() {
        return owners;
    }

    public void setOwners(List<SysUser> owners) {
        this.owners = owners;
    }
}







package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 业主房屋关联实体
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "业主房屋关联")
@TableName("owner_room")
public class OwnerRoom extends BaseEntity {

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "房屋ID")
    @TableField("room_id")
    private Long roomId;

    @ApiModelProperty(value = "关系类型：OWNER-业主，TENANT-租户")
    @TableField("relation_type")
    private String relationType;

    @ApiModelProperty(value = "状态：1-正常，0-失效")
    @TableField("status")
    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

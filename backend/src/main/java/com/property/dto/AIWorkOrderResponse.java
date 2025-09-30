package com.property.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * AI工单响应DTO
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "AI工单响应")
public class AIWorkOrderResponse {

    @ApiModelProperty(value = "工单标题")
    private String title;

    @ApiModelProperty(value = "工单描述")
    private String description;

    @ApiModelProperty(value = "建议的工单类别")
    private String suggestedCategory;

    @ApiModelProperty(value = "建议的优先级")
    private String suggestedPriority;

    @ApiModelProperty(value = "图片URL")
    private String imageUrl;

    @ApiModelProperty(value = "提交人姓名")
    private String submitterName;

    @ApiModelProperty(value = "提交人电话")
    private String submitterPhone;

    @ApiModelProperty(value = "房屋地址")
    private String roomAddress;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestedCategory() {
        return suggestedCategory;
    }

    public void setSuggestedCategory(String suggestedCategory) {
        this.suggestedCategory = suggestedCategory;
    }

    public String getSuggestedPriority() {
        return suggestedPriority;
    }

    public void setSuggestedPriority(String suggestedPriority) {
        this.suggestedPriority = suggestedPriority;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getSubmitterPhone() {
        return submitterPhone;
    }

    public void setSubmitterPhone(String submitterPhone) {
        this.submitterPhone = submitterPhone;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }
}

package com.property.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI工单请求DTO
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "AI工单请求")
public class AIWorkOrderRequest {

    @ApiModelProperty(value = "图片文件", required = true)
    private MultipartFile image;

    @ApiModelProperty(value = "房屋ID")
    private Long roomId;

    @ApiModelProperty(value = "工单类别：REPAIR-维修，COMPLAINT-投诉，SUGGESTION-建议")
    private String category;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

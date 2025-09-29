package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 投诉建议实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "投诉建议")
@TableName("complaint")
public class Complaint extends BaseEntity {

    @ApiModelProperty(value = "投诉编号")
    @TableField("complaint_no")
    private String complaintNo;

    @ApiModelProperty(value = "投诉人ID")
    @NotNull(message = "投诉人ID不能为空")
    @TableField("complainant_id")
    private Long complainantId;

    @ApiModelProperty(value = "房屋ID")
    @TableField("room_id")
    private Long roomId;

    @ApiModelProperty(value = "投诉类型")
    @NotBlank(message = "投诉类型不能为空")
    @TableField("complaint_type")
    private String complaintType;

    @ApiModelProperty(value = "投诉标题")
    @NotBlank(message = "投诉标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "投诉内容")
    @NotBlank(message = "投诉内容不能为空")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "相关图片")
    @TableField("images")
    private String images;

    @ApiModelProperty(value = "紧急程度")
    @TableField("urgency_level")
    private String urgencyLevel;

    @ApiModelProperty(value = "状态")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "处理人ID")
    @TableField("handler_id")
    private Long handlerId;

    @ApiModelProperty(value = "处理结果")
    @TableField("handle_result")
    private String handleResult;

    @ApiModelProperty(value = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("handle_time")
    private LocalDateTime handleTime;

    @ApiModelProperty(value = "满意度评分")
    @TableField("satisfaction_rating")
    private Integer satisfactionRating;

    @ApiModelProperty(value = "评价反馈")
    @TableField("feedback")
    private String feedback;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "投诉人姓名")
    @TableField(exist = false)
    private String complainantName;

    @ApiModelProperty(value = "投诉人电话")
    @TableField(exist = false)
    private String complainantPhone;

    @ApiModelProperty(value = "房屋地址")
    @TableField(exist = false)
    private String roomAddress;

    @ApiModelProperty(value = "处理人姓名")
    @TableField(exist = false)
    private String handlerName;

    public String getComplaintNo() {
        return complaintNo;
    }

    public void setComplaintNo(String complaintNo) {
        this.complaintNo = complaintNo;
    }

    public Long getComplainantId() {
        return complainantId;
    }

    public void setComplainantId(Long complainantId) {
        this.complainantId = complainantId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }

    public Integer getSatisfactionRating() {
        return satisfactionRating;
    }

    public void setSatisfactionRating(Integer satisfactionRating) {
        this.satisfactionRating = satisfactionRating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public String getComplainantPhone() {
        return complainantPhone;
    }

    public void setComplainantPhone(String complainantPhone) {
        this.complainantPhone = complainantPhone;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }
}







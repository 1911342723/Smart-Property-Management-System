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
 * 工单实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "工单")
@TableName("work_order")
public class WorkOrder extends BaseEntity {

    @ApiModelProperty(value = "工单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "工单标题")
    @NotBlank(message = "工单标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "工单内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "工单类别：REPAIR-维修，COMPLAINT-投诉，SUGGESTION-建议")
    @NotBlank(message = "工单类别不能为空")
    @TableField("category")
    private String category;

    @ApiModelProperty(value = "优先级：LOW-低，MEDIUM-中，HIGH-高，URGENT-紧急")
    @TableField("priority")
    private String priority;

    @ApiModelProperty(value = "状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-已完成，CLOSED-已关闭")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "提交人ID")
    @NotNull(message = "提交人ID不能为空")
    @TableField("submitter_id")
    private Long submitterId;

    @ApiModelProperty(value = "分配人ID")
    @TableField("assignee_id")
    private Long assigneeId;

    @ApiModelProperty(value = "关联房屋ID")
    @TableField("room_id")
    private Long roomId;

    @ApiModelProperty(value = "图片地址（JSON数组）")
    @TableField("images")
    private String images;

    @ApiModelProperty(value = "提交时间")
    @TableField("submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "分配时间")
    @TableField("assign_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignTime;

    @ApiModelProperty(value = "开始处理时间")
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "完成时间")
    @TableField("complete_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;

    @ApiModelProperty(value = "评分（1-5）")
    @TableField("rating")
    private Integer rating;

    @ApiModelProperty(value = "反馈意见")
    @TableField("feedback")
    private String feedback;

    @ApiModelProperty(value = "费用")
    @TableField("cost")
    private BigDecimal cost;

    // 关联属性（非数据库字段）
    @ApiModelProperty(value = "提交人姓名")
    @TableField(exist = false)
    private String submitterName;
    
    @ApiModelProperty(value = "提交人电话")
    @TableField(exist = false)
    private String submitterPhone;

    @ApiModelProperty(value = "分配人姓名")
    @TableField(exist = false)
    private String assigneeName;

    @ApiModelProperty(value = "房屋地址")
    @TableField(exist = false)
    private String roomAddress;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Long submitterId) {
        this.submitterId = submitterId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public LocalDateTime getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(LocalDateTime assignTime) {
        this.assignTime = assignTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }
}







package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 公告实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "公告")
@TableName("announcement")
public class Announcement extends BaseEntity {

    @ApiModelProperty(value = "公告标题")
    @NotBlank(message = "公告标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "公告内容")
    @NotBlank(message = "公告内容不能为空")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "公告摘要")
    @TableField("summary")
    private String summary;

    @ApiModelProperty(value = "公告类型：NOTICE-通知，ACTIVITY-活动，MAINTENANCE-维护，EMERGENCY-紧急")
    @NotBlank(message = "公告类型不能为空")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "优先级：LOW-低，NORMAL-普通，HIGH-高")
    @TableField("priority")
    private String priority;

    @ApiModelProperty(value = "状态：DRAFT-草稿，PUBLISHED-已发布，EXPIRED-已过期")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "发布人ID")
    @TableField("publisher_id")
    private Long publisherId;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("publish_time")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "是否置顶：0-否，1-是")
    @TableField("is_top")
    private Integer isTop;

    @ApiModelProperty(value = "阅读次数")
    @TableField("read_count")
    private Integer readCount;

    @ApiModelProperty(value = "图片地址（JSON数组）")
    @TableField("images")
    private String images;

    // 非数据库字段
    @ApiModelProperty(value = "发布人姓名")
    @TableField(exist = false)
    private String publisherName;

    @ApiModelProperty(value = "类型文本")
    @TableField(exist = false)
    private String typeText;

    @ApiModelProperty(value = "状态文本")
    @TableField(exist = false)
    private String statusText;

    @ApiModelProperty(value = "优先级文本")
    @TableField(exist = false)
    private String priorityText;

    // Getters and Setters
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getPriorityText() {
        return priorityText;
    }

    public void setPriorityText(String priorityText) {
        this.priorityText = priorityText;
    }
}





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
 * 活动实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "社区活动")
@TableName("activity")
public class Activity extends BaseEntity {

    @ApiModelProperty(value = "活动标题")
    @NotBlank(message = "活动标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "活动描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "活动详情内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "活动摘要")
    @TableField("summary")
    private String summary;

    @ApiModelProperty(value = "活动类型：SPORTS-体育，ENTERTAINMENT-娱乐，EDUCATION-教育，COMMUNITY-社区，HEALTH-健康")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "组织者ID")
    @NotNull(message = "组织者ID不能为空")
    @TableField("organizer_id")
    private Long organizerId;

    @ApiModelProperty(value = "组织者姓名")
    @TableField(exist = false)
    private String organizerName;

    @ApiModelProperty(value = "活动地点")
    @TableField("location")
    private String location;

    @ApiModelProperty(value = "开始时间")
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "最大参与人数")
    @TableField("max_participants")
    private Integer maxParticipants;

    @ApiModelProperty(value = "当前参与人数")
    @TableField("current_participants")
    private Integer currentParticipants;

    @ApiModelProperty(value = "报名开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("registration_start")
    private LocalDateTime registrationStart;

    @ApiModelProperty(value = "报名结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("registration_end")
    private LocalDateTime registrationEnd;

    @ApiModelProperty(value = "状态：PLANNED-计划中，REGISTRATION-报名中，ONGOING-进行中，COMPLETED-已完成，CANCELLED-已取消")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "是否免费：0-收费，1-免费")
    @TableField("is_free")
    private Integer isFree;

    @ApiModelProperty(value = "活动费用")
    @TableField("fee")
    private BigDecimal fee;

    @ApiModelProperty(value = "活动图片URL")
    @TableField("image_url")
    private String imageUrl;

    @ApiModelProperty(value = "图片地址列表（JSON数组）")
    @TableField("images")
    private String images;

    @ApiModelProperty(value = "是否已报名")
    @TableField(exist = false)
    private Boolean isRegistered;

    @ApiModelProperty(value = "报名状态")
    @TableField(exist = false)
    private String registrationStatus;

    // Getter and Setter methods
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

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public LocalDateTime getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(LocalDateTime registrationStart) {
        this.registrationStart = registrationStart;
    }

    public LocalDateTime getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(LocalDateTime registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(Boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}




package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 消息实体类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@ApiModel(description = "消息")
@TableName("message")
public class Message extends BaseEntity {

    @ApiModelProperty(value = "消息标题")
    @NotBlank(message = "消息标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "消息类型：SYSTEM-系统消息，SERVICE-服务消息，NOTICE-公告消息")
    @NotBlank(message = "消息类型不能为空")
    @TableField("message_type")
    private String messageType;

    @ApiModelProperty(value = "消息级别：INFO-信息，WARNING-警告，ERROR-错误")
    @TableField("level")
    private String level;

    @ApiModelProperty(value = "发送人ID")
    @TableField("sender_id")
    private Long senderId;

    @ApiModelProperty(value = "接收人ID")
    @NotNull(message = "接收人ID不能为空")
    @TableField("receiver_id")
    private Long receiverId;

    @ApiModelProperty(value = "是否已读：0-未读，1-已读")
    @TableField("is_read")
    private Integer isRead;

    @ApiModelProperty(value = "关联业务ID")
    @TableField("business_id")
    private Long businessId;

    @ApiModelProperty(value = "关联业务类型：WORK_ORDER-工单，BILL-账单，NOTICE-公告")
    @TableField("business_type")
    private String businessType;

    @ApiModelProperty(value = "消息图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "图标背景色")
    @TableField("icon_bg")
    private String iconBg;

    // 非数据库字段
    @ApiModelProperty(value = "发送人姓名")
    @TableField(exist = false)
    private String senderName;

    @ApiModelProperty(value = "接收人姓名")
    @TableField(exist = false)
    private String receiverName;

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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconBg() {
        return iconBg;
    }

    public void setIconBg(String iconBg) {
        this.iconBg = iconBg;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}

package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单评价实体
 */
@Data
@TableName("work_order_rating")
public class WorkOrderRating {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 工单ID
     */
    private Long orderId;
    
    /**
     * 维修工ID
     */
    private Long workerId;
    
    /**
     * 评价人ID
     */
    private Long raterId;
    
    /**
     * 总体评分（1-5分）
     */
    private Integer overallScore;
    
    /**
     * 服务态度评分（1-5分）
     */
    private Integer serviceAttitudeScore;
    
    /**
     * 工作质量评分（1-5分）
     */
    private Integer workQualityScore;
    
    /**
     * 响应速度评分（1-5分）
     */
    private Integer responseSpeedScore;
    
    /**
     * 专业能力评分（1-5分）
     */
    private Integer professionalismScore;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片（JSON数组）
     */
    private String images;
    
    /**
     * 评价标签（JSON数组）
     */
    private String tags;
    
    /**
     * 是否匿名：0-否，1-是
     */
    private Boolean isAnonymous;
    
    /**
     * 维修工回复
     */
    private String workerReply;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
    /**
     * 有用数（点赞数）
     */
    private Integer isHelpful;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Boolean deleted;
    
    /**
     * 评价人姓名（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String raterName;
    
    /**
     * 维修工姓名（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String workerName;
    
    /**
     * 工单标题（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String orderTitle;
}

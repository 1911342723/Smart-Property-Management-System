package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工档案实体
 */
@Data
@TableName("worker_profile")
public class WorkerProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 维修工ID
     */
    private Long workerId;
    
    /**
     * 工作状态：AVAILABLE-空闲，BUSY-忙碌，OFFLINE-离线
     */
    private String workStatus;
    
    /**
     * 总工单数
     */
    private Integer totalOrders;
    
    /**
     * 已完成工单数
     */
    private Integer completedOrders;
    
    /**
     * 处理中工单数
     */
    private Integer processingOrders;
    
    /**
     * 平均评分
     */
    private BigDecimal averageRating;
    
    /**
     * 评价总数
     */
    private Integer totalRatings;
    
    /**
     * 5星评价数
     */
    private Integer fiveStarCount;
    
    /**
     * 4星评价数
     */
    private Integer fourStarCount;
    
    /**
     * 3星评价数
     */
    private Integer threeStarCount;
    
    /**
     * 2星评价数
     */
    private Integer twoStarCount;
    
    /**
     * 1星评价数
     */
    private Integer oneStarCount;
    
    /**
     * 总收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 服务区域（JSON数组）
     */
    private String serviceArea;
    
    /**
     * 工作时间
     */
    private String workTime;
    
    /**
     * 个人简介
     */
    private String introduction;
    
    /**
     * 响应率（%）
     */
    private BigDecimal responseRate;
    
    /**
     * 完工率（%）
     */
    private BigDecimal completionRate;
    
    /**
     * 准时率（%）
     */
    private BigDecimal onTimeRate;
    
    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;
    
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
}

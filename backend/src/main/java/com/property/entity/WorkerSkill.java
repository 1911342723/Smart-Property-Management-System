package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 维修工技能关联实体
 */
@Data
@TableName("worker_skill")
public class WorkerSkill {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 维修工ID
     */
    private Long workerId;
    
    /**
     * 技能标签ID
     */
    private Long skillId;
    
    /**
     * 熟练度：BEGINNER-初级，INTERMEDIATE-中级，ADVANCED-高级，EXPERT-专家
     */
    private String proficiencyLevel;
    
    /**
     * 从业年限
     */
    private Integer yearsOfExperience;
    
    /**
     * 相关证书
     */
    private String certification;
    
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
     * 技能标签名称（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String skillName;
    
    /**
     * 技能分类（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String skillCategory;
}

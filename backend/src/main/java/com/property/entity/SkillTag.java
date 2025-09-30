package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 技能标签实体
 */
@Data
@TableName("skill_tag")
public class SkillTag {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 技能名称
     */
    private String name;
    
    /**
     * 技能分类
     */
    private String category;
    
    /**
     * 技能描述
     */
    private String description;
    
    /**
     * 图标地址
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Boolean isActive;
    
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
}

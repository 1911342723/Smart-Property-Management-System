package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.entity.SkillTag;
import com.property.mapper.SkillTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 技能标签服务
 */
@Service
@RequiredArgsConstructor
public class SkillTagService {
    
    private final SkillTagMapper skillTagMapper;
    
    /**
     * 获取所有启用的技能标签
     */
    public List<SkillTag> getAllActiveSkills() {
        return skillTagMapper.selectList(
            new LambdaQueryWrapper<SkillTag>()
                .eq(SkillTag::getIsActive, true)
                .orderByAsc(SkillTag::getSortOrder)
        );
    }
    
    /**
     * 根据分类获取技能标签
     */
    public List<SkillTag> getSkillsByCategory(String category) {
        return skillTagMapper.selectList(
            new LambdaQueryWrapper<SkillTag>()
                .eq(SkillTag::getCategory, category)
                .eq(SkillTag::getIsActive, true)
                .orderByAsc(SkillTag::getSortOrder)
        );
    }
    
    /**
     * 根据ID获取技能标签
     */
    public SkillTag getById(Long id) {
        return skillTagMapper.selectById(id);
    }
    
    /**
     * 创建技能标签
     */
    public boolean createSkill(SkillTag skillTag) {
        return skillTagMapper.insert(skillTag) > 0;
    }
    
    /**
     * 更新技能标签
     */
    public boolean updateSkill(SkillTag skillTag) {
        return skillTagMapper.updateById(skillTag) > 0;
    }
    
    /**
     * 删除技能标签
     */
    public boolean deleteSkill(Long id) {
        return skillTagMapper.deleteById(id) > 0;
    }
}

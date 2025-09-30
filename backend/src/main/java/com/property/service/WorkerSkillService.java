package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.entity.WorkerSkill;
import com.property.mapper.WorkerSkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 维修工技能服务
 */
@Service
@RequiredArgsConstructor
public class WorkerSkillService {
    
    private final WorkerSkillMapper workerSkillMapper;
    
    /**
     * 获取维修工的技能列表
     */
    public List<WorkerSkill> getWorkerSkills(Long workerId) {
        return workerSkillMapper.selectWorkerSkillsWithDetails(workerId);
    }
    
    /**
     * 添加维修工技能
     */
    public boolean addWorkerSkill(WorkerSkill workerSkill) {
        return workerSkillMapper.insert(workerSkill) > 0;
    }
    
    /**
     * 批量添加维修工技能
     */
    @Transactional
    public boolean addWorkerSkillsBatch(Long workerId, List<Long> skillIds) {
        // 先删除原有技能
        workerSkillMapper.delete(
            new LambdaQueryWrapper<WorkerSkill>()
                .eq(WorkerSkill::getWorkerId, workerId)
        );
        
        // 添加新技能
        for (Long skillId : skillIds) {
            WorkerSkill workerSkill = new WorkerSkill();
            workerSkill.setWorkerId(workerId);
            workerSkill.setSkillId(skillId);
            workerSkill.setProficiencyLevel("INTERMEDIATE");
            workerSkill.setYearsOfExperience(0);
            workerSkillMapper.insert(workerSkill);
        }
        
        return true;
    }
    
    /**
     * 删除维修工技能
     */
    public boolean deleteWorkerSkill(Long workerId, Long skillId) {
        return workerSkillMapper.delete(
            new LambdaQueryWrapper<WorkerSkill>()
                .eq(WorkerSkill::getWorkerId, workerId)
                .eq(WorkerSkill::getSkillId, skillId)
        ) > 0;
    }
    
    /**
     * 更新维修工技能熟练度
     */
    public boolean updateSkillProficiency(Long id, String proficiencyLevel, Integer years, String certification) {
        WorkerSkill workerSkill = new WorkerSkill();
        workerSkill.setId(id);
        workerSkill.setProficiencyLevel(proficiencyLevel);
        workerSkill.setYearsOfExperience(years);
        workerSkill.setCertification(certification);
        return workerSkillMapper.updateById(workerSkill) > 0;
    }
}

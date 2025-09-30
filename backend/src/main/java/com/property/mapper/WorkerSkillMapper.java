package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.WorkerSkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 维修工技能Mapper
 */
@Mapper
public interface WorkerSkillMapper extends BaseMapper<WorkerSkill> {
    
    /**
     * 查询维修工的技能列表（包含技能详情）
     */
    @Select("SELECT ws.*, st.name as skill_name, st.category as skill_category " +
            "FROM worker_skill ws " +
            "LEFT JOIN skill_tag st ON ws.skill_id = st.id " +
            "WHERE ws.worker_id = #{workerId}")
    List<WorkerSkill> selectWorkerSkillsWithDetails(@Param("workerId") Long workerId);
}

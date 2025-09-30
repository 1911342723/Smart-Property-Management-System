package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.WorkerProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维修工档案Mapper
 */
@Mapper
public interface WorkerProfileMapper extends BaseMapper<WorkerProfile> {
}

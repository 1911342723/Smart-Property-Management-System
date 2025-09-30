package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.OwnerRoom;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业主房屋关联数据访问层
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface OwnerRoomMapper extends BaseMapper<OwnerRoom> {
}

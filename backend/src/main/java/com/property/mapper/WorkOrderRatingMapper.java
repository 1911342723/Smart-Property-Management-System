package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.WorkOrderRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工单评价Mapper
 */
@Mapper
public interface WorkOrderRatingMapper extends BaseMapper<WorkOrderRating> {
    
    /**
     * 查询维修工的评价列表（包含评价人和工单信息）
     */
    @Select("SELECT r.*, u.real_name as rater_name, wo.title as order_title " +
            "FROM work_order_rating r " +
            "LEFT JOIN sys_user u ON r.rater_id = u.id " +
            "LEFT JOIN work_order wo ON r.order_id = wo.id " +
            "WHERE r.worker_id = #{workerId} AND r.deleted = 0 " +
            "ORDER BY r.create_time DESC")
    List<WorkOrderRating> selectWorkerRatingsWithDetails(@Param("workerId") Long workerId);
    
    /**
     * 计算维修工的平均评分
     */
    @Select("SELECT AVG(overall_score) FROM work_order_rating WHERE worker_id = #{workerId} AND deleted = 0")
    BigDecimal selectAverageRatingByWorkerId(@Param("workerId") Long workerId);
    
    /**
     * 统计各星级评价数量
     */
    @Select("SELECT COUNT(*) FROM work_order_rating WHERE worker_id = #{workerId} AND overall_score = #{score} AND deleted = 0")
    Integer countByWorkerIdAndScore(@Param("workerId") Long workerId, @Param("score") Integer score);
}

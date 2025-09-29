package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动报名Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface ActivityRegistrationMapper extends BaseMapper<ActivityRegistration> {

    /**
     * 分页查询活动报名列表（包含活动和用户信息）
     */
    @Select({
        "<script>",
        "SELECT ar.*, a.title as activity_title, a.start_time, a.end_time, a.location,",
        "       u.real_name as user_name, u.phone as user_phone",
        "FROM activity_registration ar",
        "LEFT JOIN activity a ON ar.activity_id = a.id",
        "LEFT JOIN sys_user u ON ar.user_id = u.id",
        "WHERE ar.deleted = 0",
        "<if test='activityId != null'>",
        "  AND ar.activity_id = #{activityId}",
        "</if>",
        "<if test='userId != null'>",
        "  AND ar.user_id = #{userId}",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "  AND ar.status = #{status}",
        "</if>",
        "ORDER BY ar.registration_time DESC",
        "</script>"
    })
    IPage<ActivityRegistration> selectRegistrationPage(Page<ActivityRegistration> page,
                                                     @Param("activityId") Long activityId,
                                                     @Param("userId") Long userId,
                                                     @Param("status") String status);

    /**
     * 查询用户在指定活动的报名记录
     */
    @Select({
        "SELECT ar.*, a.title as activity_title, a.start_time, a.end_time, a.location ",
        "FROM activity_registration ar ",
        "LEFT JOIN activity a ON ar.activity_id = a.id ",
        "WHERE ar.activity_id = #{activityId} AND ar.user_id = #{userId} AND ar.deleted = 0"
    })
    ActivityRegistration selectByActivityIdAndUserId(@Param("activityId") Long activityId, 
                                                   @Param("userId") Long userId);

    /**
     * 统计活动报名人数
     */
    @Select({
        "SELECT COALESCE(SUM(participants), 0)",
        "FROM activity_registration",
        "WHERE activity_id = #{activityId} AND status IN ('REGISTERED', 'CONFIRMED') AND deleted = 0"
    })
    Integer countParticipantsByActivityId(@Param("activityId") Long activityId);

    /**
     * 查询用户的报名历史
     */
    @Select({
        "<script>",
        "SELECT ar.*, a.title as activity_title, a.start_time, a.end_time, a.location, a.status as activity_status",
        "FROM activity_registration ar",
        "LEFT JOIN activity a ON ar.activity_id = a.id",
        "WHERE ar.user_id = #{userId} AND ar.deleted = 0",
        "<if test='status != null and status != \"\"'>",
        "  AND ar.status = #{status}",
        "</if>",
        "ORDER BY ar.registration_time DESC",
        "</script>"
    })
    IPage<ActivityRegistration> selectUserRegistrationHistory(Page<ActivityRegistration> page,
                                                            @Param("userId") Long userId,
                                                            @Param("status") String status);
}

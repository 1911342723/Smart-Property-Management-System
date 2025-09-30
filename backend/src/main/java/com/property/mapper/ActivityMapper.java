package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 活动Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    /**
     * 分页查询活动列表（包含组织者信息）
     */
    @Select({
        "<script>",
        "SELECT a.*, u.real_name as organizer_name",
        "FROM activity a",
        "LEFT JOIN sys_user u ON a.organizer_id = u.id",
        "WHERE a.deleted = 0",
        "<if test='status != null and status != \"\"'>",
        "  AND a.status IN",
        "  <foreach item='item' index='index' collection='statusList' open='(' separator=',' close=')'>",
        "    #{item}",
        "  </foreach>",
        "</if>",
        "<if test='type != null and type != \"\"'>",
        "  AND a.type = #{type}",
        "</if>",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.description LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "ORDER BY a.start_time ASC, a.create_time DESC",
        "</script>"
    })
    IPage<Activity> selectActivityPage(Page<Activity> page, 
                                     @Param("status") String status,
                                     @Param("statusList") List<String> statusList,
                                     @Param("type") String type, 
                                     @Param("keyword") String keyword);

    /**
     * 根据用户ID查询活动列表（包含报名状态）
     */
    @Select({
        "<script>",
        "SELECT a.*, u.real_name as organizer_name,",
        "       ar.id as registration_id, ar.status as registration_status,",
        "       CASE WHEN ar.id IS NOT NULL THEN 1 ELSE 0 END as is_registered",
        "FROM activity a",
        "LEFT JOIN sys_user u ON a.organizer_id = u.id",
        "LEFT JOIN activity_registration ar ON a.id = ar.activity_id AND ar.user_id = #{userId} AND ar.deleted = 0",
        "WHERE a.deleted = 0",
        "<if test='status != null and status != \"\"'>",
        "  AND a.status IN",
        "  <foreach item='item' index='index' collection='statusList' open='(' separator=',' close=')'>",
        "    #{item}",
        "  </foreach>",
        "</if>",
        "<if test='type != null and type != \"\"'>",
        "  AND a.type = #{type}",
        "</if>",
        "ORDER BY a.start_time ASC, a.create_time DESC",
        "</script>"
    })
    IPage<Activity> selectActivityPageByUserId(Page<Activity> page,
                                             @Param("userId") Long userId,
                                             @Param("status") String status,
                                             @Param("statusList") List<String> statusList,
                                             @Param("type") String type);

    /**
     * 根据ID查询活动详情（包含组织者信息和用户报名状态）
     */
    @Select({
        "SELECT a.*, u.real_name as organizer_name,",
        "       ar.id as registration_id, ar.status as registration_status,",
        "       CASE WHEN ar.id IS NOT NULL THEN 1 ELSE 0 END as is_registered",
        "FROM activity a",
        "LEFT JOIN sys_user u ON a.organizer_id = u.id",
        "LEFT JOIN activity_registration ar ON a.id = ar.activity_id AND ar.user_id = #{userId} AND ar.deleted = 0",
        "WHERE a.id = #{id} AND a.deleted = 0"
    })
    Activity selectActivityDetailByUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 更新活动参与人数
     */
    @Update("UPDATE activity SET current_participants = current_participants + #{increment} WHERE id = #{activityId}")
    int updateParticipantsCount(@Param("activityId") Long activityId, @Param("increment") Integer increment);

    /**
     * 查询即将开始的活动（用于定时任务更新状态）
     */
    @Select({
        "SELECT * FROM activity",
        "WHERE deleted = 0 AND status = 'REGISTRATION'",
        "AND start_time <= DATE_ADD(NOW(), INTERVAL 1 HOUR)"
    })
    List<Activity> selectUpcomingActivities();

    /**
     * 查询已结束的活动（用于定时任务更新状态）
     */
    @Select({
        "SELECT * FROM activity",
        "WHERE deleted = 0 AND status IN ('REGISTRATION', 'ONGOING')",
        "AND end_time <= NOW()"
    })
    List<Activity> selectCompletedActivities();
}







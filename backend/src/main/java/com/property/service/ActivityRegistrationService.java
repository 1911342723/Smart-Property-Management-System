package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.ActivityRegistration;

/**
 * 活动报名服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface ActivityRegistrationService extends IService<ActivityRegistration> {

    /**
     * 用户报名参加活动
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @param registration 报名信息
     * @return 是否成功
     */
    boolean registerActivity(Long activityId, Long userId, ActivityRegistration registration);

    /**
     * 取消活动报名
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelRegistration(Long activityId, Long userId, String reason);

    /**
     * 确认活动报名
     * 
     * @param registrationId 报名ID
     * @return 是否成功
     */
    boolean confirmRegistration(Long registrationId);

    /**
     * 分页查询活动报名列表
     * 
     * @param page 分页参数
     * @param activityId 活动ID
     * @param userId 用户ID
     * @param status 报名状态
     * @return 报名列表
     */
    IPage<ActivityRegistration> getRegistrationPage(Page<ActivityRegistration> page, 
                                                  Long activityId, Long userId, String status);

    /**
     * 查询用户在指定活动的报名记录
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名记录
     */
    ActivityRegistration getUserRegistration(Long activityId, Long userId);

    /**
     * 查询用户的报名历史
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 报名状态
     * @return 报名历史
     */
    IPage<ActivityRegistration> getUserRegistrationHistory(Page<ActivityRegistration> page, 
                                                         Long userId, String status);

    /**
     * 统计活动报名人数
     * 
     * @param activityId 活动ID
     * @return 报名人数
     */
    Integer countParticipants(Long activityId);

    /**
     * 检查用户是否可以报名
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 检查结果
     */
    boolean canUserRegister(Long activityId, Long userId);
}




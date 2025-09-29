package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Activity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface ActivityService extends IService<Activity> {

    boolean canRegister(Long activityId);

    IPage<Activity> getActivityPage(Page<Activity> page, String status, String type, String keyword);

    IPage<Activity> getActivityPageByUserId(Page<Activity> page, Long userId, String status, String type);

    Activity getActivityDetail(Long id, Long userId);

    @Transactional(rollbackFor = Exception.class)
    boolean createActivity(Activity activity);

    @Transactional(rollbackFor = Exception.class)
    boolean updateActivity(Activity activity);

    @Transactional(rollbackFor = Exception.class)
    boolean deleteActivity(Long id);

    @Transactional(rollbackFor = Exception.class)
    boolean updateActivityStatus(Long id, String status);

    boolean updateParticipantsCount(Long activityId, Integer participants);

    boolean isUserRegistered(Long activityId, Long userId);

    @Transactional(rollbackFor = Exception.class)
    void updateActivityStatusScheduled();
}

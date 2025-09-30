package com.property.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Activity;
import com.property.mapper.ActivityMapper;
import com.property.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 活动服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public IPage<Activity> getActivityPage(Page<Activity> page, String status, String type, String keyword) {
        List<String> statusList = null;
        if (StringUtils.hasText(status)) {
            statusList = Arrays.asList(status.split(","));
        }
        return baseMapper.selectActivityPage(page, status, statusList, type, keyword);
    }

    @Override
    public IPage<Activity> getActivityPageByUserId(Page<Activity> page, Long userId, String status, String type) {
        List<String> statusList = null;
        if (StringUtils.hasText(status)) {
            statusList = Arrays.asList(status.split(","));
        }
        return baseMapper.selectActivityPageByUserId(page, userId, status, statusList, type);
    }

    @Override
    public Activity getActivityDetail(Long id, Long userId) {
        if (userId != null) {
            return baseMapper.selectActivityDetailByUserId(id, userId);
        } else {
            return baseMapper.selectById(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createActivity(Activity activity) {
        activity.setCurrentParticipants(0);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        
        // 根据报名时间和活动时间设置状态
        LocalDateTime now = LocalDateTime.now();
        if (activity.getRegistrationStart() != null && now.isBefore(activity.getRegistrationStart())) {
            activity.setStatus("PLANNED");
        } else if (activity.getRegistrationEnd() != null && now.isAfter(activity.getRegistrationEnd())) {
            activity.setStatus("ONGOING");
        } else {
            activity.setStatus("REGISTRATION");
        }
        
        return save(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateActivity(Activity activity) {
        activity.setUpdateTime(LocalDateTime.now());
        return updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteActivity(Long id) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setDeleted(1);
        activity.setUpdateTime(LocalDateTime.now());
        return updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateActivityStatus(Long id, String status) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activity.setUpdateTime(LocalDateTime.now());
        return updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateParticipantsCount(Long activityId, Integer increment) {
        return baseMapper.updateParticipantsCount(activityId, increment) > 0;
    }

    @Override
    public boolean canRegister(Long activityId) {
        Activity activity = getById(activityId);
        if (activity == null || activity.getDeleted() == 1) {
            return false;
        }
        
        // 检查活动状态
        if (!"REGISTRATION".equals(activity.getStatus())) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 检查报名时间
        if (activity.getRegistrationStart() != null && now.isBefore(activity.getRegistrationStart())) {
            return false;
        }
        
        if (activity.getRegistrationEnd() != null && now.isAfter(activity.getRegistrationEnd())) {
            return false;
        }
        
        // 检查人数限制
        if (activity.getMaxParticipants() != null && 
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean isUserRegistered(Long activityId, Long userId) {
        // 这里需要通过报名表来查询，实际应该调用ActivityRegistrationService
        // 为了简化，这里先返回false，实际使用时需要注入ActivityRegistrationService
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatusScheduled() {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新即将开始的活动状态
        List<Activity> upcomingActivities = baseMapper.selectUpcomingActivities();
        for (Activity activity : upcomingActivities) {
            if (now.isAfter(activity.getStartTime())) {
                updateActivityStatus(activity.getId(), "ONGOING");
            }
        }
        
        // 更新已结束的活动状态
        List<Activity> completedActivities = baseMapper.selectCompletedActivities();
        for (Activity activity : completedActivities) {
            updateActivityStatus(activity.getId(), "COMPLETED");
        }
    }
}

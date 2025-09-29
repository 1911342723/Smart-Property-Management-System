package com.property.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.ActivityRegistration;
import com.property.entity.SysUser;
import com.property.mapper.ActivityRegistrationMapper;
import com.property.mapper.SysUserMapper;
import com.property.service.ActivityRegistrationService;
import com.property.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 活动报名服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class ActivityRegistrationServiceImpl extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> 
        implements ActivityRegistrationService {

    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerActivity(Long activityId, Long userId, ActivityRegistration registration) {
        // 检查活动是否存在且可以报名
        if (!activityService.canRegister(activityId)) {
            throw new RuntimeException("活动不存在或不在报名期间");
        }
        
        // 检查用户是否已报名
        ActivityRegistration existingRegistration = getUserRegistration(activityId, userId);
        if (existingRegistration != null && !"CANCELLED".equals(existingRegistration.getStatus())) {
            throw new RuntimeException("您已报名该活动");
        }
        
        // 获取用户信息
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 设置报名信息
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setStatus("REGISTERED");
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setCreateTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        
        // 如果没有提供联系信息，使用用户信息
        if (registration.getContactName() == null) {
            registration.setContactName(user.getRealName());
        }
        if (registration.getContactPhone() == null) {
            registration.setContactPhone(user.getPhone());
        }
        if (registration.getParticipants() == null) {
            registration.setParticipants(1);
        }
        
        // 保存报名记录
        boolean success = save(registration);
        
        if (success) {
            // 更新活动参与人数
            activityService.updateParticipantsCount(activityId, registration.getParticipants());
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelRegistration(Long activityId, Long userId, String reason) {
        ActivityRegistration registration = getUserRegistration(activityId, userId);
        if (registration == null) {
            throw new RuntimeException("未找到报名记录");
        }
        
        if ("CANCELLED".equals(registration.getStatus())) {
            throw new RuntimeException("报名已取消");
        }
        
        // 更新报名状态
        registration.setStatus("CANCELLED");
        registration.setCancelTime(LocalDateTime.now());
        registration.setCancelReason(reason);
        registration.setUpdateTime(LocalDateTime.now());
        
        boolean success = updateById(registration);
        
        if (success) {
            // 减少活动参与人数
            activityService.updateParticipantsCount(activityId, -registration.getParticipants());
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmRegistration(Long registrationId) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        
        registration.setStatus("CONFIRMED");
        registration.setConfirmTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        
        return updateById(registration);
    }

    @Override
    public IPage<ActivityRegistration> getRegistrationPage(Page<ActivityRegistration> page, 
                                                         Long activityId, Long userId, String status) {
        return baseMapper.selectRegistrationPage(page, activityId, userId, status);
    }

    @Override
    public ActivityRegistration getUserRegistration(Long activityId, Long userId) {
        return baseMapper.selectByActivityIdAndUserId(activityId, userId);
    }

    @Override
    public IPage<ActivityRegistration> getUserRegistrationHistory(Page<ActivityRegistration> page, 
                                                                Long userId, String status) {
        return baseMapper.selectUserRegistrationHistory(page, userId, status);
    }

    @Override
    public Integer countParticipants(Long activityId) {
        return baseMapper.countParticipantsByActivityId(activityId);
    }

    @Override
    public boolean canUserRegister(Long activityId, Long userId) {
        // 检查活动是否可以报名
        if (!activityService.canRegister(activityId)) {
            return false;
        }
        
        // 检查用户是否已报名
        ActivityRegistration registration = getUserRegistration(activityId, userId);
        return registration == null || "CANCELLED".equals(registration.getStatus());
    }
}

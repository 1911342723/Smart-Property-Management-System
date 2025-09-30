package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.entity.WorkerProfile;
import com.property.mapper.WorkerProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 维修工档案服务
 */
@Service
@RequiredArgsConstructor
public class WorkerProfileService {
    
    private final WorkerProfileMapper profileMapper;
    
    /**
     * 获取维修工档案
     */
    public WorkerProfile getProfileByWorkerId(Long workerId) {
        WorkerProfile profile = profileMapper.selectOne(
            new LambdaQueryWrapper<WorkerProfile>()
                .eq(WorkerProfile::getWorkerId, workerId)
        );
        
        // 如果不存在，创建默认档案
        if (profile == null) {
            profile = createDefaultProfile(workerId);
        }
        
        return profile;
    }
    
    /**
     * 创建默认档案
     */
    private WorkerProfile createDefaultProfile(Long workerId) {
        WorkerProfile profile = new WorkerProfile();
        profile.setWorkerId(workerId);
        profile.setWorkStatus("AVAILABLE");
        profile.setTotalOrders(0);
        profile.setCompletedOrders(0);
        profile.setProcessingOrders(0);
        profileMapper.insert(profile);
        return profile;
    }
    
    /**
     * 更新工作状态
     */
    public boolean updateWorkStatus(Long workerId, String workStatus) {
        WorkerProfile profile = getProfileByWorkerId(workerId);
        profile.setWorkStatus(workStatus);
        profile.setLastOnlineTime(LocalDateTime.now());
        return profileMapper.updateById(profile) > 0;
    }
    
    /**
     * 更新档案信息
     */
    public boolean updateProfile(WorkerProfile profile) {
        return profileMapper.updateById(profile) > 0;
    }
    
    /**
     * 增加工单统计
     */
    public void incrementTotalOrders(Long workerId) {
        WorkerProfile profile = getProfileByWorkerId(workerId);
        profile.setTotalOrders(profile.getTotalOrders() + 1);
        profile.setProcessingOrders(profile.getProcessingOrders() + 1);
        profileMapper.updateById(profile);
    }
    
    /**
     * 完成工单统计
     */
    public void completeOrder(Long workerId) {
        WorkerProfile profile = getProfileByWorkerId(workerId);
        profile.setCompletedOrders(profile.getCompletedOrders() + 1);
        profile.setProcessingOrders(Math.max(0, profile.getProcessingOrders() - 1));
        profileMapper.updateById(profile);
    }
}

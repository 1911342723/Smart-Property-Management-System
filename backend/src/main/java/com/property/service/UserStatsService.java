package com.property.service;

import com.property.dto.UserStatsDTO;

/**
 * 用户统计服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface UserStatsService {

    /**
     * 获取用户统计数据
     * 
     * @param userId 用户ID
     * @return 统计数据
     */
    UserStatsDTO getUserStats(Long userId);

    /**
     * 获取当前用户统计数据
     * 
     * @return 统计数据
     */
    UserStatsDTO getCurrentUserStats();
}


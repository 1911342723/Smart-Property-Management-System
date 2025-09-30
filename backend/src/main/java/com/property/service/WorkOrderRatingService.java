package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.WorkOrderRating;
import com.property.entity.WorkerProfile;
import com.property.mapper.WorkOrderRatingMapper;
import com.property.mapper.WorkerProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单评价服务
 */
@Service
@RequiredArgsConstructor
public class WorkOrderRatingService {
    
    private final WorkOrderRatingMapper ratingMapper;
    private final WorkerProfileMapper profileMapper;
    
    /**
     * 创建评价
     */
    @Transactional
    public boolean createRating(WorkOrderRating rating) {
        // 检查该工单是否已经有评价
        WorkOrderRating existingRating = ratingMapper.selectOne(
            new LambdaQueryWrapper<WorkOrderRating>()
                .eq(WorkOrderRating::getOrderId, rating.getOrderId())
        );
        
        if (existingRating != null) {
            throw new RuntimeException("该工单已评价，不能重复评价");
        }
        
        // 插入评价
        int result = ratingMapper.insert(rating);
        
        if (result > 0) {
            // 更新维修工档案的评价统计
            updateWorkerRatingStats(rating.getWorkerId());
            return true;
        }
        
        return false;
    }
    
    /**
     * 更新维修工评价统计
     */
    private void updateWorkerRatingStats(Long workerId) {
        // 计算平均评分
        BigDecimal avgRating = ratingMapper.selectAverageRatingByWorkerId(workerId);
        if (avgRating == null) {
            avgRating = BigDecimal.ZERO;
        } else {
            avgRating = avgRating.setScale(2, RoundingMode.HALF_UP);
        }
        
        // 统计各星级数量
        int fiveStarCount = ratingMapper.countByWorkerIdAndScore(workerId, 5);
        int fourStarCount = ratingMapper.countByWorkerIdAndScore(workerId, 4);
        int threeStarCount = ratingMapper.countByWorkerIdAndScore(workerId, 3);
        int twoStarCount = ratingMapper.countByWorkerIdAndScore(workerId, 2);
        int oneStarCount = ratingMapper.countByWorkerIdAndScore(workerId, 1);
        int totalRatings = fiveStarCount + fourStarCount + threeStarCount + twoStarCount + oneStarCount;
        
        // 更新维修工档案
        WorkerProfile profile = profileMapper.selectOne(
            new LambdaQueryWrapper<WorkerProfile>()
                .eq(WorkerProfile::getWorkerId, workerId)
        );
        
        if (profile != null) {
            profile.setAverageRating(avgRating);
            profile.setTotalRatings(totalRatings);
            profile.setFiveStarCount(fiveStarCount);
            profile.setFourStarCount(fourStarCount);
            profile.setThreeStarCount(threeStarCount);
            profile.setTwoStarCount(twoStarCount);
            profile.setOneStarCount(oneStarCount);
            profileMapper.updateById(profile);
        }
    }
    
    /**
     * 维修工回复评价
     */
    public boolean replyRating(Long ratingId, String reply) {
        WorkOrderRating rating = new WorkOrderRating();
        rating.setId(ratingId);
        rating.setWorkerReply(reply);
        rating.setReplyTime(LocalDateTime.now());
        return ratingMapper.updateById(rating) > 0;
    }
    
    /**
     * 获取工单评价
     */
    public WorkOrderRating getRatingByOrderId(Long orderId) {
        return ratingMapper.selectOne(
            new LambdaQueryWrapper<WorkOrderRating>()
                .eq(WorkOrderRating::getOrderId, orderId)
        );
    }
    
    /**
     * 获取维修工的评价列表
     */
    public Page<WorkOrderRating> getWorkerRatings(Long workerId, int pageNum, int pageSize) {
        Page<WorkOrderRating> page = new Page<>(pageNum, pageSize);
        List<WorkOrderRating> ratings = ratingMapper.selectWorkerRatingsWithDetails(workerId);
        page.setRecords(ratings);
        page.setTotal(ratings.size());
        return page;
    }
    
    /**
     * 点赞评价
     */
    public boolean likeRating(Long ratingId) {
        WorkOrderRating rating = ratingMapper.selectById(ratingId);
        if (rating != null) {
            rating.setIsHelpful(rating.getIsHelpful() + 1);
            return ratingMapper.updateById(rating) > 0;
        }
        return false;
    }
}

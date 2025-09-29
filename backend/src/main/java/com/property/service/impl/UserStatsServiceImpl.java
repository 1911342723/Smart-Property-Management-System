package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.property.dto.UserStatsDTO;
import com.property.entity.ActivityRegistration;
import com.property.entity.Complaint;
import com.property.entity.Payment;
import com.property.entity.Visitor;
import com.property.entity.WorkOrder;
import com.property.mapper.ActivityRegistrationMapper;
import com.property.mapper.ComplaintMapper;
import com.property.mapper.PaymentMapper;
import com.property.mapper.VisitorMapper;
import com.property.mapper.WorkOrderMapper;
import com.property.service.UserStatsService;
import com.property.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户统计服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class UserStatsServiceImpl implements UserStatsService {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private ComplaintMapper complaintMapper;

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ActivityRegistrationMapper activityRegistrationMapper;

    private UserStatsDTO calculateUserStats(Long userId) {
        UserStatsDTO stats = new UserStatsDTO();
        try {
            // 统计报修次数
            QueryWrapper<WorkOrder> repairQuery = new QueryWrapper<>();
            repairQuery.eq("submitter_id", userId).eq("deleted", 0);
            Long repairCountLong = workOrderMapper.selectCount(repairQuery);
            stats.setRepairCount(repairCountLong != null ? repairCountLong.intValue() : 0);

            // 统计缴费次数
            QueryWrapper<Payment> paymentQuery = new QueryWrapper<>();
            paymentQuery.eq("owner_id", userId).eq("deleted", 0);
            Long paymentCountLong = paymentMapper.selectCount(paymentQuery);
            stats.setPaymentCount(paymentCountLong != null ? paymentCountLong.intValue() : 0);

            // 统计投诉建议数
            QueryWrapper<Complaint> complaintQuery = new QueryWrapper<>();
            complaintQuery.eq("complainant_id", userId).eq("deleted", 0);
            Long complaintCountLong = complaintMapper.selectCount(complaintQuery);
            stats.setComplaintCount(complaintCountLong != null ? complaintCountLong.intValue() : 0);

            // 统计访客预约数
            QueryWrapper<Visitor> visitorQuery = new QueryWrapper<>();
            visitorQuery.eq("owner_id", userId).eq("deleted", 0);
            Long visitorCountLong = visitorMapper.selectCount(visitorQuery);
            stats.setVisitorCount(visitorCountLong != null ? visitorCountLong.intValue() : 0);

            // 统计活动参与数
            QueryWrapper<ActivityRegistration> activityQuery = new QueryWrapper<>();
            activityQuery.eq("user_id", userId).eq("deleted", 0);
            Long activityCountLong = activityRegistrationMapper.selectCount(activityQuery);
            stats.setActivityCount(activityCountLong != null ? activityCountLong.intValue() : 0);
        } catch (Exception e) {
            // 如果查询出错，返回0值
            stats.setRepairCount(0);
            stats.setPaymentCount(0);
            stats.setComplaintCount(0);
            stats.setVisitorCount(0);
            stats.setActivityCount(0);
        }
        return stats;
    }

    @Override
    public UserStatsDTO getUserStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return calculateUserStats(userId);
    }

    @Override
    public UserStatsDTO getCurrentUserStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return calculateUserStats(userId);
    }
}
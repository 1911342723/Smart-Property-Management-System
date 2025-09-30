package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.WorkOrder;
import com.property.mapper.WorkOrderMapper;
import com.property.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 工单服务类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class WorkOrderService extends ServiceImpl<WorkOrderMapper, WorkOrder> {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 分页查询工单
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param category 类别
     * @param status 状态
     * @param submitterId 提交人ID
     * @param assigneeId 分配人ID
     * @return 分页结果
     */
    public PageResult<WorkOrder> getWorkOrderPage(int pageNum, int pageSize, 
                                                 String category, String status, 
                                                 Long submitterId, Long assigneeId) {
        Page<WorkOrder> page = new Page<>(pageNum, pageSize);
        IPage<WorkOrder> result = baseMapper.selectPageWithDetails(page, category, status, submitterId, assigneeId);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               result.getCurrent(), result.getSize());
    }

    /**
     * 提交工单
     * 
     * @param workOrder 工单信息
     * @return 是否成功
     */
    public boolean submitWorkOrder(WorkOrder workOrder) {
        // 生成工单编号
        workOrder.setOrderNo(generateOrderNo());
        
        // 设置默认值
        if (workOrder.getPriority() == null) {
            workOrder.setPriority("MEDIUM");
        }
        workOrder.setStatus("PENDING");
        workOrder.setSubmitTime(LocalDateTime.now());
        
        return this.save(workOrder);
    }

    /**
     * 生成工单编号
     * 
     * @return 工单编号
     */
    private String generateOrderNo() {
        // 格式：WO + 年月日 + 4位序号
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查询今日工单数量
        long count = this.lambdaQuery()
                .likeRight(WorkOrder::getOrderNo, "WO" + dateStr)
                .count();
        
        return String.format("WO%s%04d", dateStr, count + 1);
    }

    /**
     * 分配工单
     * 
     * @param orderId 工单ID
     * @param assigneeId 分配人ID
     * @return 是否成功
     */
    public boolean assignWorkOrder(Long orderId, Long assigneeId) {
        WorkOrder workOrder = this.getById(orderId);
        if (workOrder == null) {
            throw new RuntimeException("工单不存在");
        }
        
        if (!"PENDING".equals(workOrder.getStatus())) {
            throw new RuntimeException("只能分配待处理状态的工单");
        }
        
        workOrder.setAssigneeId(assigneeId);
        workOrder.setAssignTime(LocalDateTime.now());
        
        return this.updateById(workOrder);
    }

    /**
     * 维修工接单
     * 
     * @param orderId 工单ID
     * @param workerId 维修工ID（可选，如果不传则尝试从上下文获取）
     * @return 是否成功
     */
    public boolean acceptWorkOrder(Long orderId, Long workerId) {
        WorkOrder workOrder = this.getById(orderId);
        if (workOrder == null) {
            throw new RuntimeException("工单不存在");
        }
        
        if (!"PENDING".equals(workOrder.getStatus())) {
            throw new RuntimeException("只能接取待处理状态的工单");
        }
        
        if (workOrder.getAssigneeId() != null) {
            throw new RuntimeException("该工单已被分配");
        }
        
        // 获取维修工ID
        Long assigneeId = workerId;
        if (assigneeId == null) {
            assigneeId = getCurrentUserId();
        }
        
        if (assigneeId == null) {
            throw new RuntimeException("获取维修工信息失败");
        }
        
        // 分配给维修工
        workOrder.setAssigneeId(assigneeId);
        workOrder.setAssignTime(LocalDateTime.now());
        
        return this.updateById(workOrder);
    }

    /**
     * 开始处理工单
     * 
     * @param orderId 工单ID
     * @return 是否成功
     */
    public boolean startWorkOrder(Long orderId) {
        WorkOrder workOrder = this.getById(orderId);
        if (workOrder == null) {
            throw new RuntimeException("工单不存在");
        }
        
        if (!"PENDING".equals(workOrder.getStatus())) {
            throw new RuntimeException("只能开始处理待处理状态的工单");
        }
        
        // 如果还没有分配人，自动分配给当前用户
        if (workOrder.getAssigneeId() == null) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                workOrder.setAssigneeId(currentUserId);
                workOrder.setAssignTime(LocalDateTime.now());
            }
        }
        
        workOrder.setStatus("PROCESSING");
        workOrder.setStartTime(LocalDateTime.now());
        
        return this.updateById(workOrder);
    }
    
    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() && 
                !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    String phone = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                    // 通过手机号查询用户ID
                    com.property.entity.SysUser user = sysUserMapper.selectByPhone(phone);
                    return user != null ? user.getId() : null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 完成工单
     * 
     * @param orderId 工单ID
     * @param cost 费用
     * @return 是否成功
     */
    public boolean completeWorkOrder(Long orderId, java.math.BigDecimal cost) {
        WorkOrder workOrder = this.getById(orderId);
        if (workOrder == null) {
            throw new RuntimeException("工单不存在");
        }
        
        if (!"PROCESSING".equals(workOrder.getStatus())) {
            throw new RuntimeException("只能完成处理中状态的工单");
        }
        
        workOrder.setStatus("COMPLETED");
        workOrder.setCompleteTime(LocalDateTime.now());
        if (cost != null) {
            workOrder.setCost(cost);
        }
        
        return this.updateById(workOrder);
    }

    /**
     * 评价工单
     * 
     * @param orderId 工单ID
     * @param rating 评分
     * @param feedback 反馈
     * @return 是否成功
     */
    public boolean rateWorkOrder(Long orderId, Integer rating, String feedback) {
        WorkOrder workOrder = this.getById(orderId);
        if (workOrder == null) {
            throw new RuntimeException("工单不存在");
        }
        
        if (!"COMPLETED".equals(workOrder.getStatus())) {
            throw new RuntimeException("只能评价已完成的工单");
        }
        
        workOrder.setRating(rating);
        workOrder.setFeedback(feedback);
        
        return this.updateById(workOrder);
    }

    /**
     * 根据ID查询工单详情（带关联信息）
     * 
     * @param id 工单ID
     * @return 工单详情
     */
    public WorkOrder getWorkOrderDetail(Long id) {
        return baseMapper.selectByIdWithDetails(id);
    }

    /**
     * 获取工单统计信息
     * 
     * @param workerId 维修工ID（可选）
     * @return 统计信息
     */
    public java.util.Map<String, Object> getWorkOrderStats(Long workerId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkOrder> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        // 如果指定了维修工ID，则只统计该维修工的工单
        if (workerId != null) {
            queryWrapper.eq(WorkOrder::getAssigneeId, workerId);
        }
        
        // 统计各状态的工单数量
        long totalCount = this.count(queryWrapper);
        long pendingCount = this.count(queryWrapper.clone().eq(WorkOrder::getStatus, "PENDING"));
        long processingCount = this.count(queryWrapper.clone().eq(WorkOrder::getStatus, "PROCESSING"));
        long completedCount = this.count(queryWrapper.clone().eq(WorkOrder::getStatus, "COMPLETED"));
        long cancelledCount = this.count(queryWrapper.clone().eq(WorkOrder::getStatus, "CANCELLED"));
        
        stats.put("total", totalCount);
        stats.put("pending", pendingCount);
        stats.put("processing", processingCount);
        stats.put("completed", completedCount);
        stats.put("cancelled", cancelledCount);
        
        // 统计今日新增工单
        java.time.LocalDateTime todayStart = java.time.LocalDateTime.of(
            java.time.LocalDate.now(), java.time.LocalTime.MIN);
        long todayCount = this.count(queryWrapper.clone()
            .ge(WorkOrder::getCreateTime, todayStart));
        stats.put("today", todayCount);
        
        // 如果是维修工查询，额外统计"待接单"数量（未分配的PENDING工单）
        if (workerId != null) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkOrder> unassignedWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            unassignedWrapper.eq(WorkOrder::getStatus, "PENDING")
                .eq(WorkOrder::getCategory, "REPAIR")
                .isNull(WorkOrder::getAssigneeId);
            long availableCount = this.count(unassignedWrapper);
            stats.put("available", availableCount); // 可接单数量
        }
        
        return stats;
    }
}

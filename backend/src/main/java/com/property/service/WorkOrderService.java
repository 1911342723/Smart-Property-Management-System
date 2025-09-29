package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.WorkOrder;
import com.property.mapper.WorkOrderMapper;
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
        
        workOrder.setStatus("PROCESSING");
        workOrder.setStartTime(LocalDateTime.now());
        
        return this.updateById(workOrder);
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
}

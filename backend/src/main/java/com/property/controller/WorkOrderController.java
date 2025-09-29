package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.WorkOrder;
import com.property.service.WorkOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 工单控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "工单管理")
@RestController
@RequestMapping("/workorder")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @ApiOperation("分页查询工单")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'GUARD', 'WORKER')")
    public Result<PageResult<WorkOrder>> getWorkOrderList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("工单类别") @RequestParam(required = false) String category,
            @ApiParam("工单状态") @RequestParam(required = false) String status,
            @ApiParam("提交人ID") @RequestParam(required = false) Long submitterId,
            @ApiParam("分配人ID") @RequestParam(required = false) Long assigneeId) {
        
        PageResult<WorkOrder> result = workOrderService.getWorkOrderPage(
                pageNum, pageSize, category, status, submitterId, assigneeId);
        return Result.success(result);
    }

    @ApiOperation("根据ID查询工单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'GUARD', 'WORKER')")
    public Result<WorkOrder> getWorkOrderById(@PathVariable Long id) {
        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return Result.error("工单不存在");
        }
        return Result.success(workOrder);
    }

    @ApiOperation("提交工单")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Result<Void> submitWorkOrder(@Validated @RequestBody WorkOrder workOrder) {
        try {
            workOrderService.submitWorkOrder(workOrder);
            return Result.success();
        } catch (Exception e) {
            return Result.error("工单提交失败：" + e.getMessage());
        }
    }

    @ApiOperation("分配工单")
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assignWorkOrder(
            @PathVariable Long id,
            @ApiParam("分配人ID") @RequestParam Long assigneeId) {
        try {
            workOrderService.assignWorkOrder(id, assigneeId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("工单分配失败：" + e.getMessage());
        }
    }

    @ApiOperation("开始处理工单")
    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public Result<Void> startWorkOrder(@PathVariable Long id) {
        try {
            workOrderService.startWorkOrder(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("完成工单")
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public Result<Void> completeWorkOrder(
            @PathVariable Long id,
            @ApiParam("费用") @RequestParam(required = false) BigDecimal cost) {
        try {
            workOrderService.completeWorkOrder(id, cost);
            return Result.success();
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("评价工单")
    @PutMapping("/{id}/rate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Result<Void> rateWorkOrder(
            @PathVariable Long id,
            @ApiParam("评分（1-5）") @RequestParam Integer rating,
            @ApiParam("反馈意见") @RequestParam(required = false) String feedback) {
        try {
            if (rating < 1 || rating > 5) {
                return Result.error("评分必须在1-5之间");
            }
            workOrderService.rateWorkOrder(id, rating, feedback);
            return Result.success();
        } catch (Exception e) {
            return Result.error("评价失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除工单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteWorkOrder(@PathVariable Long id) {
        try {
            workOrderService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}



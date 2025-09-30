package com.property.controller;

import com.property.dto.Result;
import com.property.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据仪表盘控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "数据仪表盘")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @ApiOperation("获取仪表盘总览数据")
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getOverview() {
        try {
            Map<String, Object> overview = dashboardService.getOverview();
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取总览数据失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取工单统计数据")
    @GetMapping("/workorder-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getWorkOrderStats() {
        try {
            Map<String, Object> stats = dashboardService.getWorkOrderStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取工单统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取工单类型分布")
    @GetMapping("/workorder-distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getWorkOrderDistribution() {
        try {
            Map<String, Object> distribution = dashboardService.getWorkOrderDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error("获取工单分布失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取工单趋势数据")
    @GetMapping("/workorder-trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getWorkOrderTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        try {
            Map<String, Object> trend = dashboardService.getWorkOrderTrend(days);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("获取工单趋势失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取账单统计数据")
    @GetMapping("/bill-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getBillStats() {
        try {
            Map<String, Object> stats = dashboardService.getBillStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取账单统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取房产统计数据")
    @GetMapping("/property-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getPropertyStats() {
        try {
            Map<String, Object> stats = dashboardService.getPropertyStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取房产统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取入住率趋势")
    @GetMapping("/occupancy-trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getOccupancyTrend(
            @RequestParam(defaultValue = "6") Integer months) {
        try {
            Map<String, Object> trend = dashboardService.getOccupancyTrend(months);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("获取入住率趋势失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取月度收费统计")
    @GetMapping("/monthly-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Map<String, Object>> getMonthlyPayment(
            @RequestParam(defaultValue = "6") Integer months) {
        try {
            Map<String, Object> payment = dashboardService.getMonthlyPayment(months);
            return Result.success(payment);
        } catch (Exception e) {
            return Result.error("获取月度收费统计失败：" + e.getMessage());
        }
    }
}

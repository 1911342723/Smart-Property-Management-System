package com.property.controller;

import com.property.annotation.OperationLog;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Bill;
import com.property.service.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 账单控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "账单管理")
@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * 分页查询账单列表
     */
    @ApiOperation("分页查询账单列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @OperationLog(module = "账单管理", operationType = "SELECT", description = "查询账单列表")
    public Result<PageResult<Bill>> getBillList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("账单类型") @RequestParam(required = false) String billType,
            @ApiParam("账单状态") @RequestParam(required = false) String status,
            @ApiParam("业主ID") @RequestParam(required = false) Long ownerId,
            @ApiParam("计费周期") @RequestParam(required = false) String billingPeriod) {
        
        PageResult<Bill> result = billService.getBillPage(pageNum, pageSize, billType, status, ownerId, billingPeriod);
        return Result.success(result);
    }

    /**
     * 获取账单详情
     */
    @ApiOperation("获取账单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<Bill> getBillById(@ApiParam("账单ID") @PathVariable Long id) {
        Bill bill = billService.getById(id);
        if (bill == null) {
            return Result.error("账单不存在");
        }
        return Result.success(bill);
    }

    /**
     * 创建账单
     */
    @ApiOperation("创建账单")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "账单管理", operationType = "INSERT", description = "创建账单")
    public Result<String> createBill(@ApiParam("账单信息") @Valid @RequestBody Bill bill) {
        boolean success = billService.createBill(bill);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 批量创建账单
     */
    @ApiOperation("批量创建账单")
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "账单管理", operationType = "INSERT", description = "批量创建账单")
    public Result<String> createBillsBatch(@ApiParam("账单列表") @Valid @RequestBody List<Bill> bills) {
        boolean success = billService.createBillsBatch(bills);
        return success ? Result.success("批量创建成功") : Result.error("批量创建失败");
    }

    /**
     * 缴费
     */
    @ApiOperation("缴费")
    @PostMapping("/pay")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<String> payBill(
            @ApiParam("账单ID") @RequestParam Long billId,
            @ApiParam("缴费金额") @RequestParam BigDecimal paymentAmount,
            @ApiParam("支付方式") @RequestParam String paymentMethod) {
        
        try {
            boolean success = billService.payBill(billId, paymentAmount, paymentMethod);
            return success ? Result.success("缴费成功") : Result.error("缴费失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量缴费
     */
    @ApiOperation("批量缴费")
    @PostMapping("/pay/batch")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<String> payBillsBatch(
            @ApiParam("账单ID列表") @RequestBody List<Long> billIds,
            @ApiParam("支付方式") @RequestParam String paymentMethod) {
        
        try {
            boolean success = billService.payBillsBatch(billIds, paymentMethod);
            return success ? Result.success("批量缴费成功") : Result.error("批量缴费失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取账单统计信息
     */
    @ApiOperation("获取账单统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<BillService.BillStats> getBillStats(@ApiParam("业主ID") @RequestParam(required = false) Long ownerId) {
        BillService.BillStats stats = billService.getBillStats(ownerId);
        return Result.success(stats);
    }

    /**
     * 获取业主费用统计
     */
    @ApiOperation("获取业主费用统计")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<BillService.BillSummary> getBillSummary(@ApiParam("业主ID") @RequestParam Long ownerId) {
        BillService.BillSummary summary = billService.getBillSummary(ownerId);
        return Result.success(summary);
    }

    /**
     * 更新账单
     */
    @ApiOperation("更新账单")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "账单管理", operationType = "UPDATE", description = "更新账单")
    public Result<String> updateBill(
            @ApiParam("账单ID") @PathVariable Long id,
            @ApiParam("账单信息") @Valid @RequestBody Bill bill) {
        
        bill.setId(id);
        boolean success = billService.updateById(bill);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除账单
     */
    @ApiOperation("删除账单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "账单管理", operationType = "DELETE", description = "删除账单")
    public Result<String> deleteBill(@ApiParam("账单ID") @PathVariable Long id) {
        boolean success = billService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 导出账单列表
     */
    @ApiOperation("导出账单列表")
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "账单管理", operationType = "EXPORT", description = "导出账单列表")
    public void exportBills(
            @ApiParam("账单类型") @RequestParam(required = false) String billType,
            @ApiParam("账单状态") @RequestParam(required = false) String status,
            @ApiParam("业主ID") @RequestParam(required = false) Long ownerId,
            @ApiParam("计费周期") @RequestParam(required = false) String billingPeriod,
            HttpServletResponse response) throws IOException {
        try {
            billService.exportBillList(billType, status, ownerId, billingPeriod, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":500,\"message\":\"导出失败：" + e.getMessage() + "\"}");
        }
    }

    /**
     * 导出财务报表
     */
    @ApiOperation("导出财务报表")
    @GetMapping("/export-report")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "财务管理", operationType = "EXPORT", description = "导出财务报表")
    public void exportReport(
            @ApiParam("报表类型") @RequestParam(required = false) String reportType,
            @ApiParam("时间范围") @RequestParam(required = false) String timeRange,
            @ApiParam("楼栋") @RequestParam(required = false) String building,
            HttpServletResponse response) throws IOException {
        try {
            billService.exportFinanceReport(reportType, timeRange, building, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":500,\"message\":\"导出报表失败：" + e.getMessage() + "\"}");
        }
    }

    /**
     * 获取楼栋收费统计
     */
    @ApiOperation("获取楼栋收费统计")
    @GetMapping("/building-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<BillService.BuildingStats>> getBuildingStats(
            @ApiParam("计费周期") @RequestParam(required = false) String billingPeriod) {
        try {
            List<BillService.BuildingStats> stats = billService.getBuildingStats(billingPeriod);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取楼栋统计失败：" + e.getMessage());
        }
    }
}







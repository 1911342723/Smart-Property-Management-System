package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Payment;
import com.property.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 支付记录控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "支付记录管理")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 分页查询支付记录列表
     */
    @ApiOperation("分页查询支付记录列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<PageResult<Payment>> getPaymentList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("业主ID") @RequestParam(required = false) Long ownerId,
            @ApiParam("支付方式") @RequestParam(required = false) String paymentMethod,
            @ApiParam("支付状态") @RequestParam(required = false) String status) {
        
        PageResult<Payment> result = paymentService.getPaymentPage(pageNum, pageSize, ownerId, paymentMethod, status);
        return Result.success(result);
    }

    /**
     * 获取支付记录详情
     */
    @ApiOperation("获取支付记录详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<Payment> getPaymentById(@ApiParam("支付记录ID") @PathVariable Long id) {
        Payment payment = paymentService.getById(id);
        if (payment == null) {
            return Result.error("支付记录不存在");
        }
        return Result.success(payment);
    }

    /**
     * 创建支付记录
     */
    @ApiOperation("创建支付记录")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createPayment(@ApiParam("支付记录信息") @Valid @RequestBody Payment payment) {
        boolean success = paymentService.createPayment(payment);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 更新支付记录
     */
    @ApiOperation("更新支付记录")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updatePayment(
            @ApiParam("支付记录ID") @PathVariable Long id,
            @ApiParam("支付记录信息") @Valid @RequestBody Payment payment) {
        
        payment.setId(id);
        boolean success = paymentService.updateById(payment);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除支付记录
     */
    @ApiOperation("删除支付记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deletePayment(@ApiParam("支付记录ID") @PathVariable Long id) {
        boolean success = paymentService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 导出缴费记录
     */
    @ApiOperation("导出缴费记录")
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportPayments(
            @ApiParam("业主ID") @RequestParam(required = false) Long ownerId,
            @ApiParam("支付方式") @RequestParam(required = false) String paymentMethod,
            @ApiParam("支付状态") @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {
        try {
            paymentService.exportPaymentList(ownerId, paymentMethod, status, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":500,\"message\":\"导出失败：" + e.getMessage() + "\"}");
        }
    }
}







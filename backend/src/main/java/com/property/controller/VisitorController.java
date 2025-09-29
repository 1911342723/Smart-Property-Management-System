package com.property.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Visitor;
import com.property.service.VisitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 访客管理控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "访客管理")
@RestController
@RequestMapping("/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 分页查询访客列表
     */
    @ApiOperation("分页查询访客列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('OWNER') or hasRole('GUARD') or hasRole('ADMIN')")
    public Result<PageResult<Visitor>> getVisitorList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("业主ID") @RequestParam(required = false) Long ownerId,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        PageResult<Visitor> result = visitorService.getVisitorPage(pageNum, pageSize, ownerId, status, startDate, endDate);
        return Result.success(result);
    }

    /**
     * 获取访客详情
     */
    @ApiOperation("获取访客详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('GUARD') or hasRole('ADMIN')")
    public Result<Visitor> getVisitorById(@ApiParam("访客ID") @PathVariable Long id) {
        Visitor visitor = visitorService.getById(id);
        if (visitor == null) {
            return Result.error("访客记录不存在");
        }
        return Result.success(visitor);
    }

    /**
     * 创建访客预约
     */
    @ApiOperation("创建访客预约")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public Result<String> createVisitor(@ApiParam("访客信息") @Valid @RequestBody Visitor visitor) {
        try {
            boolean success = visitorService.createVisitor(visitor);
            return success ? Result.success("访客预约创建成功") : Result.error("访客预约创建失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新访客状态
     */
    @ApiOperation("更新访客状态")
    @PostMapping("/status")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<String> updateVisitorStatus(
            @ApiParam("访客ID") @RequestParam Long visitorId,
            @ApiParam("状态") @RequestParam String status,
            @ApiParam("保安ID") @RequestParam(required = false) Long guardId) {
        
        try {
            boolean success = visitorService.updateVisitorStatus(visitorId, status, guardId);
            return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据二维码获取访客信息
     */
    @ApiOperation("根据二维码获取访客信息")
    @GetMapping("/qrcode/{qrCode}")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<Visitor> getVisitorByQrCode(@ApiParam("二维码") @PathVariable String qrCode) {
        Visitor visitor = visitorService.getByQrCode(qrCode);
        if (visitor == null) {
            return Result.error("访客二维码无效");
        }
        return Result.success(visitor);
    }

    /**
     * 访客到达签到
     */
    @ApiOperation("访客到达签到")
    @PostMapping("/arrival")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<String> visitorArrival(
            @ApiParam("二维码") @RequestParam String qrCode,
            @ApiParam("保安ID") @RequestParam Long guardId) {
        
        try {
            boolean success = visitorService.visitorArrival(qrCode, guardId);
            return success ? Result.success("访客签到成功") : Result.error("访客签到失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 访客离开签退
     */
    @ApiOperation("访客离开签退")
    @PostMapping("/departure")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<String> visitorDeparture(
            @ApiParam("访客ID") @RequestParam Long visitorId,
            @ApiParam("保安ID") @RequestParam Long guardId) {
        
        try {
            boolean success = visitorService.visitorDeparture(visitorId, guardId);
            return success ? Result.success("访客签退成功") : Result.error("访客签退失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取访客统计信息
     */
    @ApiOperation("获取访客统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<VisitorService.VisitorStats> getVisitorStats(
            @ApiParam("业主ID") @RequestParam Long ownerId,
            HttpServletRequest request) {
        
        try {
            System.out.println("=== 访客统计接口调试信息 ===");
            System.out.println("请求参数 ownerId: " + ownerId);
            System.out.println("Authorization Header: " + request.getHeader("Authorization"));
            
            // 获取当前认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("当前认证信息: " + (auth != null ? auth.getName() : "null"));
            System.out.println("当前用户权限: " + (auth != null ? auth.getAuthorities() : "null"));
            
            VisitorService.VisitorStats stats = visitorService.getVisitorStats(ownerId);
            System.out.println("访客统计结果: " + stats);
            
            return Result.success(stats);
        } catch (Exception e) {
            System.err.println("获取访客统计失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取访客统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日访客列表
     */
    @ApiOperation("获取今日访客列表")
    @GetMapping("/today")
    @PreAuthorize("hasRole('OWNER') or hasRole('GUARD') or hasRole('ADMIN')")
    public Result<List<Visitor>> getTodayVisitors(@ApiParam("业主ID") @RequestParam Long ownerId) {
        List<Visitor> visitors = visitorService.getTodayVisitors(ownerId);
        return Result.success(visitors);
    }

    /**
     * 扫码验证访客
     */
    @ApiOperation("扫码验证访客")
    @PostMapping("/scan-verify")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<Visitor> scanVerifyVisitor(
            @ApiParam("二维码内容") @RequestBody ScanVerifyRequest request) {
        
        try {
            Visitor visitor = visitorService.scanVerifyVisitor(request.getQrContent(), request.getGuardId());
            return Result.success("验证成功", visitor);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 访客签到（扫码后确认）
     */
    @ApiOperation("访客签到")
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<String> checkInVisitor(
            @ApiParam("访客ID") @RequestParam Long visitorId,
            @ApiParam("保安ID") @RequestParam Long guardId) {
        
        try {
            boolean success = visitorService.checkInVisitor(visitorId, guardId);
            return success ? Result.success("签到成功") : Result.error("签到失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 访客签退
     */
    @ApiOperation("访客签退")
    @PostMapping("/check-out")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<String> checkOutVisitor(
            @ApiParam("访客ID") @RequestParam Long visitorId,
            @ApiParam("保安ID") @RequestParam Long guardId) {
        
        try {
            boolean success = visitorService.checkOutVisitor(visitorId, guardId);
            return success ? Result.success("签退成功") : Result.error("签退失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除访客记录
     */
    @ApiOperation("删除访客记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteVisitor(@ApiParam("访客ID") @PathVariable Long id) {
        boolean success = visitorService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 扫码验证请求
     */
    public static class ScanVerifyRequest {
        private String qrContent;
        private Long guardId;

        public String getQrContent() {
            return qrContent;
        }

        public void setQrContent(String qrContent) {
            this.qrContent = qrContent;
        }

        public Long getGuardId() {
            return guardId;
        }

        public void setGuardId(Long guardId) {
            this.guardId = guardId;
        }
    }
}

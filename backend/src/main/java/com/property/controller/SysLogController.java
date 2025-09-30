package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.SysLog;
import com.property.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统日志控制器
 */
@Api(tags = "系统日志管理")
@RestController
@RequestMapping("/system/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @ApiOperation("分页查询日志列表")
    @GetMapping("/list")
    public Result<PageResult<SysLog>> getLogList(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<SysLog> result = sysLogService.getLogList(pageNum, pageSize, 
                                                                 module, operationType, username, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询日志列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取日志详情")
    @GetMapping("/{id}")
    public Result<SysLog> getLogDetail(@PathVariable Long id) {
        try {
            SysLog log = sysLogService.getLogDetail(id);
            return Result.success(log);
        } catch (Exception e) {
            return Result.error("获取日志详情失败：" + e.getMessage());
        }
    }

    @ApiOperation("批量删除日志")
    @DeleteMapping("/batch-delete")
    public Result<String> batchDeleteLog(@RequestBody List<Long> ids) {
        try {
            boolean success = sysLogService.batchDeleteLog(ids);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除日志失败：" + e.getMessage());
        }
    }

    @ApiOperation("清空旧日志")
    @DeleteMapping("/clear")
    public Result<String> clearLogs(@RequestParam(defaultValue = "30") int days) {
        try {
            boolean success = sysLogService.clearOldLogs(days);
            if (success) {
                return Result.success("清空成功");
            } else {
                return Result.error("清空失败");
            }
        } catch (Exception e) {
            return Result.error("清空日志失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取日志统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getLogStats() {
        try {
            Map<String, Object> stats = sysLogService.getLogStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }
}

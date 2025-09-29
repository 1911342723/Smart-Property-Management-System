package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.Result;
import com.property.entity.Activity;
import com.property.entity.ActivityRegistration;
import com.property.service.ActivityRegistrationService;
import com.property.service.ActivityService;
import com.property.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 社区活动控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "社区活动管理")
@RestController
@RequestMapping("/community/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private ActivityRegistrationService registrationService;

    /**
     * 分页查询活动列表
     */
    @ApiOperation("分页查询活动列表")
    @GetMapping
    public Result<IPage<Activity>> getActivityList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("活动状态") @RequestParam(required = false) String status,
            @ApiParam("活动类型") @RequestParam(required = false) String type,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        
        Page<Activity> page = new Page<>(pageNum, pageSize);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        IPage<Activity> result;
        if (currentUserId != null) {
            // 业主用户查询，包含报名状态
            result = activityService.getActivityPageByUserId(page, currentUserId, status, type);
        } else {
            // 管理员或游客查询
            result = activityService.getActivityPage(page, status, type, keyword);
        }
        
        return Result.success(result);
    }

    /**
     * 查询活动详情
     */
    @ApiOperation("查询活动详情")
    @GetMapping("/{id}")
    public Result<Activity> getActivityDetail(@ApiParam("活动ID") @PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Activity activity = activityService.getActivityDetail(id, currentUserId);
        
        if (activity == null) {
            return Result.error("活动不存在");
        }
        
        return Result.success(activity);
    }

    /**
     * 创建活动
     */
    @ApiOperation("创建活动")
    @PostMapping
    public Result<Void> createActivity(@Valid @RequestBody Activity activity) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }
        
        activity.setOrganizerId(currentUserId);
        boolean success = activityService.createActivity(activity);
        
        return success ? Result.success() : Result.error("创建活动失败");
    }

    /**
     * 更新活动
     */
    @ApiOperation("更新活动")
    @PutMapping("/{id}")
    public Result<Void> updateActivity(@ApiParam("活动ID") @PathVariable Long id, 
                                     @Valid @RequestBody Activity activity) {
        activity.setId(id);
        boolean success = activityService.updateActivity(activity);
        
        return success ? Result.success() : Result.error("更新活动失败");
    }

    /**
     * 删除活动
     */
    @ApiOperation("删除活动")
    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@ApiParam("活动ID") @PathVariable Long id) {
        boolean success = activityService.deleteActivity(id);
        return success ? Result.success() : Result.error("删除活动失败");
    }

    /**
     * 报名参加活动
     */
    @ApiOperation("报名参加活动")
    @PostMapping("/{id}/register")
    public Result<Void> registerActivity(@ApiParam("活动ID") @PathVariable Long id,
                                       @RequestBody ActivityRegistration registration) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }
        
        try {
            boolean success = registrationService.registerActivity(id, currentUserId, registration);
            if (success) {
                return Result.success("报名成功", null);
            } else {
                return Result.error("报名失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消活动报名
     */
    @ApiOperation("取消活动报名")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelRegistration(@ApiParam("活动ID") @PathVariable Long id,
                                         @RequestParam(required = false) String reason) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }
        
        try {
            boolean success = registrationService.cancelRegistration(id, currentUserId, reason);
            if (success) {
                return Result.success("取消报名成功", null);
            } else {
                return Result.error("取消报名失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询活动报名列表
     */
    @ApiOperation("查询活动报名列表")
    @GetMapping("/{id}/registrations")
    public Result<IPage<ActivityRegistration>> getActivityRegistrations(
            @ApiParam("活动ID") @PathVariable Long id,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("报名状态") @RequestParam(required = false) String status) {
        
        Page<ActivityRegistration> page = new Page<>(pageNum, pageSize);
        IPage<ActivityRegistration> result = registrationService.getRegistrationPage(page, id, null, status);
        
        return Result.success(result);
    }

    /**
     * 查询用户报名历史
     */
    @ApiOperation("查询用户报名历史")
    @GetMapping("/my-registrations")
    public Result<IPage<ActivityRegistration>> getMyRegistrations(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("报名状态") @RequestParam(required = false) String status) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }
        
        Page<ActivityRegistration> page = new Page<>(pageNum, pageSize);
        IPage<ActivityRegistration> result = registrationService.getUserRegistrationHistory(page, currentUserId, status);
        
        return Result.success(result);
    }

    /**
     * 确认报名
     */
    @ApiOperation("确认报名")
    @PostMapping("/registrations/{registrationId}/confirm")
    public Result<Void> confirmRegistration(@ApiParam("报名ID") @PathVariable Long registrationId) {
        try {
            boolean success = registrationService.confirmRegistration(registrationId);
            if (success) {
                return Result.success("确认成功", null);
            } else {
                return Result.error("确认失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新活动状态
     */
    @ApiOperation("更新活动状态")
    @PostMapping("/{id}/status")
    public Result<Void> updateActivityStatus(@ApiParam("活动ID") @PathVariable Long id,
                                           @ApiParam("新状态") @RequestParam String status) {
        boolean success = activityService.updateActivityStatus(id, status);
        if (success) {
            return Result.success("状态更新成功", null);
        } else {
            return Result.error("状态更新失败");
        }
    }
}

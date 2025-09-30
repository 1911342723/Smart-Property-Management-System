package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.Result;
import com.property.entity.WorkOrderRating;
import com.property.entity.WorkerProfile;
import com.property.entity.WorkerSkill;
import com.property.service.WorkOrderRatingService;
import com.property.service.WorkerProfileService;
import com.property.service.WorkerSkillService;
import com.property.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维修工控制器
 */
@Api(tags = "维修工管理")
@RestController
@RequestMapping("/worker")
@RequiredArgsConstructor
public class WorkerController {
    
    private final WorkerSkillService workerSkillService;
    private final WorkerProfileService workerProfileService;
    private final WorkOrderRatingService ratingService;
    
    @ApiOperation("获取当前维修工的技能列表")
    @GetMapping("/skills")
    public Result<List<WorkerSkill>> getMySkills() {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        List<WorkerSkill> skills = workerSkillService.getWorkerSkills(workerId);
        return Result.success(skills);
    }
    
    @ApiOperation("批量添加技能")
    @PostMapping("/skills/batch")
    public Result<String> addSkillsBatch(@RequestBody AddSkillsRequest request) {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        boolean success = workerSkillService.addWorkerSkillsBatch(workerId, request.getSkillIds());
        return success ? Result.success("技能添加成功") : Result.error("技能添加失败");
    }
    
    @ApiOperation("删除技能")
    @DeleteMapping("/skills/{skillId}")
    public Result<String> deleteSkill(@PathVariable Long skillId) {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        boolean success = workerSkillService.deleteWorkerSkill(workerId, skillId);
        return success ? Result.success("技能删除成功") : Result.error("技能删除失败");
    }
    
    @ApiOperation("更新技能熟练度")
    @PutMapping("/skills/{id}")
    public Result<String> updateSkillProficiency(
            @PathVariable Long id,
            @RequestBody UpdateProficiencyRequest request) {
        boolean success = workerSkillService.updateSkillProficiency(
            id, 
            request.getProficiencyLevel(), 
            request.getYearsOfExperience(),
            request.getCertification()
        );
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @ApiOperation("获取当前维修工的档案")
    @GetMapping("/profile")
    public Result<WorkerProfile> getMyProfile() {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        WorkerProfile profile = workerProfileService.getProfileByWorkerId(workerId);
        return Result.success(profile);
    }
    
    @ApiOperation("更新工作状态")
    @PutMapping("/work-status")
    public Result<String> updateWorkStatus(@RequestBody UpdateWorkStatusRequest request) {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        boolean success = workerProfileService.updateWorkStatus(workerId, request.getWorkStatus());
        return success ? Result.success("工作状态更新成功") : Result.error("工作状态更新失败");
    }
    
    @ApiOperation("更新档案信息")
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody WorkerProfile profile) {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        profile.setWorkerId(workerId);
        boolean success = workerProfileService.updateProfile(profile);
        return success ? Result.success("档案更新成功") : Result.error("档案更新失败");
    }
    
    @ApiOperation("获取当前维修工的评价列表")
    @GetMapping("/ratings")
    public Result<Page<WorkOrderRating>> getMyRatings(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        Page<WorkOrderRating> ratings = ratingService.getWorkerRatings(workerId, pageNum, pageSize);
        return Result.success(ratings);
    }
    
    @ApiOperation("回复评价")
    @PostMapping("/ratings/{ratingId}/reply")
    public Result<String> replyRating(@PathVariable Long ratingId, @RequestBody ReplyRatingRequest request) {
        boolean success = ratingService.replyRating(ratingId, request.getReply());
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }
    
    @ApiOperation("获取维修工统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getWorkerStats() {
        Long workerId = SecurityUtils.getCurrentUser().getId();
        
        WorkerProfile profile = workerProfileService.getProfileByWorkerId(workerId);
        List<WorkerSkill> skills = workerSkillService.getWorkerSkills(workerId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", profile.getTotalOrders());
        stats.put("completedOrders", profile.getCompletedOrders());
        stats.put("processingOrders", profile.getProcessingOrders());
        stats.put("averageRating", profile.getAverageRating());
        stats.put("totalRatings", profile.getTotalRatings());
        stats.put("fiveStarCount", profile.getFiveStarCount());
        stats.put("fourStarCount", profile.getFourStarCount());
        stats.put("threeStarCount", profile.getThreeStarCount());
        stats.put("twoStarCount", profile.getTwoStarCount());
        stats.put("oneStarCount", profile.getOneStarCount());
        stats.put("totalIncome", profile.getTotalIncome());
        stats.put("responseRate", profile.getResponseRate());
        stats.put("completionRate", profile.getCompletionRate());
        stats.put("onTimeRate", profile.getOnTimeRate());
        stats.put("skillCount", skills.size());
        
        return Result.success(stats);
    }
    
    // 内部请求类
    @Data
    public static class AddSkillsRequest {
        private List<Long> skillIds;
    }
    
    @Data
    public static class UpdateProficiencyRequest {
        private String proficiencyLevel;
        private Integer yearsOfExperience;
        private String certification;
    }
    
    @Data
    public static class UpdateWorkStatusRequest {
        private String workStatus;
    }
    
    @Data
    public static class ReplyRatingRequest {
        private String reply;
    }
}

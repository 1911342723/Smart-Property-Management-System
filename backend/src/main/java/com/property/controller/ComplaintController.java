package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Complaint;
import com.property.service.ComplaintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 投诉建议控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "投诉建议管理")
@RestController
@RequestMapping("/complaint")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    /**
     * 分页查询投诉建议列表
     */
    @ApiOperation("分页查询投诉建议列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<PageResult<Complaint>> getComplaintList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("投诉类型") @RequestParam(required = false) String complaintType,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("投诉人ID") @RequestParam(required = false) Long complainantId,
            @ApiParam("紧急程度") @RequestParam(required = false) String urgencyLevel) {
        
        PageResult<Complaint> result = complaintService.getComplaintPage(pageNum, pageSize, complaintType, status, complainantId, urgencyLevel);
        return Result.success(result);
    }

    /**
     * 获取投诉建议详情
     */
    @ApiOperation("获取投诉建议详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<Complaint> getComplaintById(@ApiParam("投诉ID") @PathVariable Long id) {
        Complaint complaint = complaintService.getById(id);
        if (complaint == null) {
            return Result.error("投诉记录不存在");
        }
        return Result.success(complaint);
    }

    /**
     * 提交投诉建议
     */
    @ApiOperation("提交投诉建议")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public Result<String> submitComplaint(@ApiParam("投诉建议信息") @Valid @RequestBody Complaint complaint) {
        try {
            boolean success = complaintService.submitComplaint(complaint);
            return success ? Result.success("投诉提交成功") : Result.error("投诉提交失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分配处理人
     */
    @ApiOperation("分配处理人")
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> assignHandler(
            @ApiParam("投诉ID") @RequestParam Long complaintId,
            @ApiParam("处理人ID") @RequestParam Long handlerId) {
        
        try {
            boolean success = complaintService.assignHandler(complaintId, handlerId);
            return success ? Result.success("分配成功") : Result.error("分配失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 开始处理投诉
     */
    @ApiOperation("开始处理投诉")
    @PostMapping("/start")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> startHandling(
            @ApiParam("投诉ID") @RequestParam Long complaintId,
            @ApiParam("处理人ID") @RequestParam Long handlerId) {
        
        try {
            boolean success = complaintService.startHandling(complaintId, handlerId);
            return success ? Result.success("开始处理成功") : Result.error("开始处理失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 完成投诉处理
     */
    @ApiOperation("完成投诉处理")
    @PostMapping("/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> completeHandling(
            @ApiParam("投诉ID") @RequestParam Long complaintId,
            @ApiParam("处理人ID") @RequestParam Long handlerId,
            @ApiParam("处理结果") @RequestParam String handleResult) {
        
        try {
            boolean success = complaintService.completeHandling(complaintId, handlerId, handleResult);
            return success ? Result.success("处理完成") : Result.error("处理失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 评价投诉处理
     */
    @ApiOperation("评价投诉处理")
    @PostMapping("/rate")
    @PreAuthorize("hasRole('OWNER')")
    public Result<String> rateComplaint(@RequestBody RateComplaintRequest request) {
        
        try {
            boolean success = complaintService.rateComplaint(
                request.getComplaintId(), 
                request.getComplainantId(), 
                request.getSatisfactionRating(), 
                request.getFeedback()
            );
            return success ? Result.success("评价成功") : Result.error("评价失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 评价投诉请求参数
     */
    public static class RateComplaintRequest {
        private Long complaintId;
        private Long complainantId;
        private Integer satisfactionRating;
        private String feedback;

        public Long getComplaintId() {
            return complaintId;
        }

        public void setComplaintId(Long complaintId) {
            this.complaintId = complaintId;
        }

        public Long getComplainantId() {
            return complainantId;
        }

        public void setComplainantId(Long complainantId) {
            this.complainantId = complainantId;
        }

        public Integer getSatisfactionRating() {
            return satisfactionRating;
        }

        public void setSatisfactionRating(Integer satisfactionRating) {
            this.satisfactionRating = satisfactionRating;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }

    /**
     * 获取投诉统计信息
     */
    @ApiOperation("获取投诉统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public Result<ComplaintService.ComplaintStats> getComplaintStats(
            @ApiParam("投诉人ID") @RequestParam(required = false) Long complainantId) {
        
        ComplaintService.ComplaintStats stats = complaintService.getComplaintStats(complainantId);
        return Result.success(stats);
    }

    /**
     * 更新投诉建议
     */
    @ApiOperation("更新投诉建议")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateComplaint(
            @ApiParam("投诉ID") @PathVariable Long id,
            @ApiParam("投诉建议信息") @Valid @RequestBody Complaint complaint) {
        
        complaint.setId(id);
        boolean success = complaintService.updateById(complaint);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除投诉建议
     */
    @ApiOperation("删除投诉建议")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteComplaint(@ApiParam("投诉ID") @PathVariable Long id) {
        boolean success = complaintService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}


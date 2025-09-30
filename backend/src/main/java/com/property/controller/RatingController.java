package com.property.controller;

import com.property.dto.Result;
import com.property.entity.WorkOrderRating;
import com.property.service.WorkOrderRatingService;
import com.property.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 */
@Api(tags = "评价管理")
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {
    
    private final WorkOrderRatingService ratingService;
    
    @ApiOperation("创建评价")
    @PostMapping
    public Result<String> createRating(@RequestBody CreateRatingRequest request) {
        Long raterId = SecurityUtils.getCurrentUser().getId();
        
        WorkOrderRating rating = new WorkOrderRating();
        rating.setOrderId(request.getOrderId());
        rating.setWorkerId(request.getWorkerId());
        rating.setRaterId(raterId);
        rating.setOverallScore(request.getOverallScore());
        rating.setServiceAttitudeScore(request.getServiceAttitudeScore());
        rating.setWorkQualityScore(request.getWorkQualityScore());
        rating.setResponseSpeedScore(request.getResponseSpeedScore());
        rating.setProfessionalismScore(request.getProfessionalismScore());
        rating.setContent(request.getContent());
        rating.setImages(request.getImages());
        rating.setTags(request.getTags());
        rating.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);
        
        boolean success = ratingService.createRating(rating);
        return success ? Result.success("评价成功") : Result.error("评价失败");
    }
    
    @ApiOperation("获取工单评价")
    @GetMapping("/order/{orderId}")
    public Result<WorkOrderRating> getRatingByOrderId(@PathVariable Long orderId) {
        WorkOrderRating rating = ratingService.getRatingByOrderId(orderId);
        return Result.success(rating);
    }
    
    @ApiOperation("点赞评价")
    @PostMapping("/{ratingId}/like")
    public Result<String> likeRating(@PathVariable Long ratingId) {
        boolean success = ratingService.likeRating(ratingId);
        return success ? Result.success("点赞成功") : Result.error("点赞失败");
    }
    
    // 请求类
    @Data
    public static class CreateRatingRequest {
        private Long orderId;
        private Long workerId;
        private Integer overallScore;
        private Integer serviceAttitudeScore;
        private Integer workQualityScore;
        private Integer responseSpeedScore;
        private Integer professionalismScore;
        private String content;
        private String images;
        private String tags;
        private Boolean isAnonymous;
    }
}

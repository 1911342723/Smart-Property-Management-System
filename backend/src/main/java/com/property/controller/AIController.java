package com.property.controller;

import com.property.dto.AIWorkOrderResponse;
import com.property.dto.Result;
import com.property.entity.WorkOrder;
import com.property.service.AIService;
import com.property.service.WorkOrderService;
import com.property.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI助手控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "AI助手")
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private WorkOrderService workOrderService;

    @ApiOperation("AI图片识别生成工单信息")
    @PostMapping("/analyze-image")
    public Result<AIWorkOrderResponse> analyzeImage(
            @ApiParam("图片文件") @RequestParam("file") MultipartFile image,
            @ApiParam("房屋ID") @RequestParam(value = "roomId", required = false) Long roomId,
            @ApiParam("工单类别") @RequestParam(value = "category", defaultValue = "REPAIR") String category) {
        
        try {
            System.out.println("=== AI analyze-image API called ===");
            System.out.println("Image: " + (image != null ? image.getOriginalFilename() : "null"));
            System.out.println("RoomId: " + roomId);
            System.out.println("Category: " + category);
            
            // 验证图片
            if (image == null || image.isEmpty()) {
                System.out.println("Error: Image is null or empty");
                return Result.error("请上传图片");
            }

            // 验证文件类型
            String contentType = image.getContentType();
            System.out.println("Content-Type: " + contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只支持图片文件");
            }

            // 验证文件大小（10MB）
            if (image.getSize() > 10 * 1024 * 1024) {
                return Result.error("图片大小不能超过10MB");
            }

            // 调用AI服务分析图片
            System.out.println("Calling AI service...");
            AIWorkOrderResponse response = aiService.analyzeImageAndGenerateWorkOrder(image, roomId, category);
            System.out.println("AI analysis completed successfully");

            return Result.success("分析成功", response);

        } catch (Exception e) {
            System.err.println("=== AI analyze-image error ===");
            e.printStackTrace();
            return Result.error("分析失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据AI分析结果创建工单")
    @PostMapping("/create-workorder")
    public Result<WorkOrder> createWorkOrderFromAI(@RequestBody WorkOrder workOrder) {
        try {
            System.out.println("=== AI create-workorder API called ===");
            
            // 获取当前用户
            com.property.entity.SysUser currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                System.out.println("Error: Current user is null");
                return Result.error("用户未登录");
            }

            Long userId = currentUser.getId();
            System.out.println("Current user ID: " + userId);
            System.out.println("Current user name: " + currentUser.getRealName());

            // 设置提交人ID
            workOrder.setSubmitterId(userId);

            // 提交工单
            System.out.println("Submitting work order...");
            boolean success = workOrderService.submitWorkOrder(workOrder);

            if (success) {
                System.out.println("Work order created successfully");
                return Result.success("工单创建成功", workOrder);
            } else {
                System.out.println("Work order creation failed");
                return Result.error("工单创建失败");
            }

        } catch (Exception e) {
            System.err.println("=== AI create-workorder error ===");
            e.printStackTrace();
            return Result.error("工单创建失败：" + e.getMessage());
        }
    }

    @ApiOperation("一键上传图片并创建工单")
    @PostMapping("/quick-submit")
    public Result<WorkOrder> quickSubmit(
            @ApiParam("图片文件") @RequestParam("file") MultipartFile image,
            @ApiParam("房屋ID") @RequestParam(value = "roomId", required = false) Long roomId,
            @ApiParam("工单类别") @RequestParam(value = "category", defaultValue = "REPAIR") String category) {
        
        try {
            System.out.println("=== AI quick-submit API called ===");
            System.out.println("Image: " + (image != null ? image.getOriginalFilename() : "null"));
            System.out.println("RoomId: " + roomId);
            System.out.println("Category: " + category);
            
            // 1. AI分析图片
            System.out.println("Step 1: Analyzing image with AI...");
            AIWorkOrderResponse aiResponse = aiService.analyzeImageAndGenerateWorkOrder(image, roomId, category);
            System.out.println("AI analysis completed");
            System.out.println("Title: " + aiResponse.getTitle());
            System.out.println("Description: " + aiResponse.getDescription());

            // 2. 获取当前用户
            System.out.println("Step 2: Getting current user...");
            com.property.entity.SysUser currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                System.out.println("Error: Current user is null");
                return Result.error("用户未登录");
            }

            Long userId = currentUser.getId();
            System.out.println("Current user ID: " + userId);
            System.out.println("Current user name: " + currentUser.getRealName());

            // 3. 创建工单对象
            System.out.println("Step 3: Creating work order object...");
            WorkOrder workOrder = new WorkOrder();
            workOrder.setTitle(aiResponse.getTitle());
            workOrder.setContent(aiResponse.getDescription());
            workOrder.setCategory(aiResponse.getSuggestedCategory());
            workOrder.setPriority(aiResponse.getSuggestedPriority());
            workOrder.setSubmitterId(userId);
            workOrder.setRoomId(roomId);
            workOrder.setImages(aiResponse.getImageUrl());
            
            System.out.println("Work order object created");
            System.out.println("Submitter ID: " + workOrder.getSubmitterId());
            System.out.println("Room ID: " + workOrder.getRoomId());
            System.out.println("Category: " + workOrder.getCategory());

            // 4. 提交工单
            System.out.println("Step 4: Submitting work order...");
            boolean success = workOrderService.submitWorkOrder(workOrder);

            if (success) {
                System.out.println("Work order submitted successfully, ID: " + workOrder.getId());
                return Result.success("工单提交成功", workOrder);
            } else {
                System.out.println("Work order submission failed");
                return Result.error("工单提交失败");
            }

        } catch (Exception e) {
            System.err.println("=== AI quick-submit error ===");
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}

package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Message;
import com.property.service.MessageService;
import com.property.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "消息管理")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation("分页查询用户消息")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'GUARD', 'WORKER')")
    public Result<PageResult<Message>> getMessageList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("消息类型") @RequestParam(required = false) String messageType,
            @ApiParam("是否已读") @RequestParam(required = false) Integer isRead,
            HttpServletRequest request) {
        
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            PageResult<Message> result = messageService.getMessagePage(
                    pageNum, pageSize, userId, messageType, isRead);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询消息失败：" + e.getMessage());
        }
    }

    @ApiOperation("发送消息")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public Result<Void> sendMessage(@Validated @RequestBody Message message) {
        try {
            messageService.sendMessage(message);
            return Result.success();
        } catch (Exception e) {
            return Result.error("发送消息失败：" + e.getMessage());
        }
    }

    @ApiOperation("标记消息为已读")
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'GUARD', 'WORKER')")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            messageService.markAsRead(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("批量标记消息为已读")
    @PutMapping("/batch-read")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'GUARD', 'WORKER')")
    public Result<Void> batchMarkAsRead(
            @ApiParam("消息ID数组") @RequestBody Long[] messageIds,
            HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            messageService.batchMarkAsRead(messageIds, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取未读消息统计")
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'GUARD', 'WORKER')")
    public Result<Map<String, Object>> getUnreadCount(HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            Map<String, Object> result = new HashMap<>();
            result.put("all", messageService.countUnreadMessages(userId, null));
            result.put("system", messageService.countUnreadMessages(userId, "SYSTEM"));
            result.put("service", messageService.countUnreadMessages(userId, "SERVICE"));
            result.put("notice", messageService.countUnreadMessages(userId, "NOTICE"));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("发送系统消息")
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> sendSystemMessage(
            @ApiParam("标题") @RequestParam String title,
            @ApiParam("内容") @RequestParam String content,
            @ApiParam("接收人ID") @RequestParam Long receiverId) {
        try {
            messageService.sendSystemMessage(title, content, receiverId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("发送系统消息失败：" + e.getMessage());
        }
    }

    @ApiOperation("发送公告消息")
    @PostMapping("/notice")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> sendNoticeMessage(
            @ApiParam("标题") @RequestParam String title,
            @ApiParam("内容") @RequestParam String content,
            @ApiParam("接收人ID数组") @RequestBody Long[] receiverIds) {
        try {
            messageService.sendNoticeMessage(title, content, receiverIds);
            return Result.success();
        } catch (Exception e) {
            return Result.error("发送公告消息失败：" + e.getMessage());
        }
    }
}

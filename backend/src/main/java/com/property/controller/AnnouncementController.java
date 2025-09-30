package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.Result;
import com.property.entity.Announcement;
import com.property.mapper.AnnouncementMapper;
import com.property.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 公告控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "公告管理")
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    // ==================== 管理端接口 ====================

    @ApiOperation("分页查询公告列表")
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<IPage<Announcement>> getAnnouncementPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("公告类型") @RequestParam(required = false) String type,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        try {
            Page<Announcement> page = new Page<>(pageNum, pageSize);
            IPage<Announcement> result = announcementService.getAnnouncementPage(page, type, status, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询公告列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("创建公告")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> createAnnouncement(@Valid @RequestBody Announcement announcement) {
        try {
            boolean success = announcementService.createAnnouncement(announcement);
            return success ? Result.success("创建公告成功") : Result.error("创建公告失败");
        } catch (Exception e) {
            return Result.error("创建公告失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新公告")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> updateAnnouncement(
            @ApiParam("公告ID") @PathVariable Long id,
            @Valid @RequestBody Announcement announcement) {
        try {
            announcement.setId(id);
            boolean success = announcementService.updateAnnouncement(announcement);
            return success ? Result.success("更新公告成功") : Result.error("更新公告失败");
        } catch (Exception e) {
            return Result.error("更新公告失败：" + e.getMessage());
        }
    }

    @ApiOperation("发布公告")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> publishAnnouncement(@ApiParam("公告ID") @PathVariable Long id) {
        try {
            boolean success = announcementService.publishAnnouncement(id);
            return success ? Result.success("发布公告成功") : Result.error("发布公告失败");
        } catch (Exception e) {
            return Result.error("发布公告失败：" + e.getMessage());
        }
    }

    @ApiOperation("下线公告")
    @PostMapping("/{id}/offline")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> offlineAnnouncement(@ApiParam("公告ID") @PathVariable Long id) {
        try {
            boolean success = announcementService.offlineAnnouncement(id);
            return success ? Result.success("下线公告成功") : Result.error("下线公告失败");
        } catch (Exception e) {
            return Result.error("下线公告失败：" + e.getMessage());
        }
    }

    @ApiOperation("置顶/取消置顶公告")
    @PostMapping("/{id}/toggle-top")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> toggleTopAnnouncement(@ApiParam("公告ID") @PathVariable Long id) {
        try {
            boolean success = announcementService.toggleTopAnnouncement(id);
            return success ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> deleteAnnouncement(@ApiParam("公告ID") @PathVariable Long id) {
        try {
            boolean success = announcementService.deleteAnnouncement(id);
            return success ? Result.success("删除公告成功") : Result.error("删除公告失败");
        } catch (Exception e) {
            return Result.error("删除公告失败：" + e.getMessage());
        }
    }

    @ApiOperation("批量删除公告")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<String> batchDeleteAnnouncements(@RequestBody List<Long> ids) {
        try {
            boolean success = announcementService.batchDeleteAnnouncements(ids);
            return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取公告统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY')")
    public Result<AnnouncementMapper.AnnouncementStats> getAnnouncementStats() {
        try {
            AnnouncementMapper.AnnouncementStats stats = announcementService.getAnnouncementStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }

    // ==================== 小程序接口 ====================

    @ApiOperation("分页查询已发布的公告列表（小程序端）")
    @GetMapping("/published/page")
    public Result<IPage<Announcement>> getPublishedAnnouncementsPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("公告类型") @RequestParam(required = false) String type,
            @ApiParam("优先级") @RequestParam(required = false) String priority) {
        try {
            Page<Announcement> page = new Page<>(pageNum, pageSize);
            IPage<Announcement> result = announcementService.getPublishedAnnouncementsPage(page, type, priority);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取已发布的公告列表")
    @GetMapping("/published")
    public Result<List<Announcement>> getPublishedAnnouncements(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Announcement> announcements = announcementService.getPublishedAnnouncements(limit);
            return Result.success(announcements);
        } catch (Exception e) {
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取置顶公告")
    @GetMapping("/top")
    public Result<List<Announcement>> getTopAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getTopAnnouncements();
            return Result.success(announcements);
        } catch (Exception e) {
            return Result.error("获取置顶公告失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据类型获取公告")
    @GetMapping("/type/{type}")
    public Result<List<Announcement>> getAnnouncementsByType(
            @ApiParam("公告类型") @PathVariable String type,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Announcement> announcements = announcementService.getAnnouncementsByType(type, limit);
            return Result.success(announcements);
        } catch (Exception e) {
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@ApiParam("公告ID") @PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementDetail(id);
            if (announcement == null) {
                return Result.error("公告不存在");
            }
            return Result.success(announcement);
        } catch (Exception e) {
            return Result.error("获取公告详情失败：" + e.getMessage());
        }
    }
}

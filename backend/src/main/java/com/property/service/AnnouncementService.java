package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Announcement;
import com.property.mapper.AnnouncementMapper;

import java.util.List;

/**
 * 公告服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告列表
     */
    IPage<Announcement> getAnnouncementPage(Page<Announcement> page, String type, String status, String keyword);

    /**
     * 分页查询已发布的公告列表（小程序用）
     */
    IPage<Announcement> getPublishedAnnouncementsPage(Page<Announcement> page, String type, String priority);

    /**
     * 查询已发布的公告列表（小程序用）
     */
    List<Announcement> getPublishedAnnouncements(Integer limit);

    /**
     * 查询置顶公告
     */
    List<Announcement> getTopAnnouncements();

    /**
     * 根据类型查询公告
     */
    List<Announcement> getAnnouncementsByType(String type, Integer limit);

    /**
     * 创建公告
     */
    boolean createAnnouncement(Announcement announcement);

    /**
     * 更新公告
     */
    boolean updateAnnouncement(Announcement announcement);

    /**
     * 发布公告
     */
    boolean publishAnnouncement(Long id);

    /**
     * 下线公告
     */
    boolean offlineAnnouncement(Long id);

    /**
     * 置顶/取消置顶公告
     */
    boolean toggleTopAnnouncement(Long id);

    /**
     * 删除公告
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 批量删除公告
     */
    boolean batchDeleteAnnouncements(List<Long> ids);

    /**
     * 查看公告详情（增加阅读次数）
     */
    Announcement getAnnouncementDetail(Long id);

    /**
     * 查询公告统计信息
     */
    AnnouncementMapper.AnnouncementStats getAnnouncementStats();

    /**
     * 处理公告数据（设置文本字段）
     */
    void processAnnouncementData(Announcement announcement);

    /**
     * 处理公告列表数据
     */
    void processAnnouncementListData(List<Announcement> announcements);
}





package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Announcement;
import com.property.entity.SysUser;
import com.property.mapper.AnnouncementMapper;
import com.property.mapper.SysUserMapper;
import com.property.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<Announcement> getAnnouncementPage(Page<Announcement> page, String type, String status, String keyword) {
        IPage<Announcement> result = announcementMapper.selectAnnouncementPage(page, type, status, keyword);
        processAnnouncementListData(result.getRecords());
        return result;
    }

    @Override
    public IPage<Announcement> getPublishedAnnouncementsPage(Page<Announcement> page, String type, String priority) {
        IPage<Announcement> result = announcementMapper.selectPublishedAnnouncementsPage(page, type, priority);
        processAnnouncementListData(result.getRecords());
        return result;
    }

    @Override
    public List<Announcement> getPublishedAnnouncements(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        List<Announcement> announcements = announcementMapper.selectPublishedAnnouncements(limit);
        processAnnouncementListData(announcements);
        return announcements;
    }

    @Override
    public List<Announcement> getTopAnnouncements() {
        List<Announcement> announcements = announcementMapper.selectTopAnnouncements();
        processAnnouncementListData(announcements);
        return announcements;
    }

    @Override
    public List<Announcement> getAnnouncementsByType(String type, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        List<Announcement> announcements = announcementMapper.selectAnnouncementsByType(type, limit);
        processAnnouncementListData(announcements);
        return announcements;
    }

    @Override
    @Transactional
    public boolean createAnnouncement(Announcement announcement) {
        // 设置发布人ID
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            announcement.setPublisherId(currentUserId);
        }

        // 设置默认值
        if (announcement.getPriority() == null) {
            announcement.setPriority("NORMAL");
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus("DRAFT");
        }
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(0);
        }
        if (announcement.getReadCount() == null) {
            announcement.setReadCount(0);
        }

        // 如果是立即发布，设置发布时间
        if ("PUBLISHED".equals(announcement.getStatus())) {
            announcement.setPublishTime(LocalDateTime.now());
        }

        return save(announcement);
    }

    @Override
    @Transactional
    public boolean updateAnnouncement(Announcement announcement) {
        return updateById(announcement);
    }

    @Override
    @Transactional
    public boolean publishAnnouncement(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }

        announcement.setStatus("PUBLISHED");
        announcement.setPublishTime(LocalDateTime.now());
        
        return updateById(announcement);
    }

    @Override
    @Transactional
    public boolean offlineAnnouncement(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }

        announcement.setStatus("OFFLINE");
        
        return updateById(announcement);
    }

    @Override
    @Transactional
    public boolean toggleTopAnnouncement(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }

        announcement.setIsTop(announcement.getIsTop() == 1 ? 0 : 1);
        
        return updateById(announcement);
    }

    @Override
    @Transactional
    public boolean deleteAnnouncement(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean batchDeleteAnnouncements(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    @Transactional
    public Announcement getAnnouncementDetail(Long id) {
        Announcement announcement = getById(id);
        if (announcement != null) {
            // 增加阅读次数
            announcementMapper.incrementReadCount(id);
            announcement.setReadCount(announcement.getReadCount() + 1);
            
            // 处理数据
            processAnnouncementData(announcement);
        }
        return announcement;
    }

    @Override
    public AnnouncementMapper.AnnouncementStats getAnnouncementStats() {
        return announcementMapper.selectAnnouncementStats();
    }

    @Override
    public void processAnnouncementData(Announcement announcement) {
        if (announcement == null) {
            return;
        }

        // 设置类型文本
        announcement.setTypeText(getTypeText(announcement.getType()));
        
        // 设置状态文本
        announcement.setStatusText(getStatusText(announcement.getStatus()));
        
        // 设置优先级文本
        announcement.setPriorityText(getPriorityText(announcement.getPriority()));
    }

    @Override
    public void processAnnouncementListData(List<Announcement> announcements) {
        if (announcements == null || announcements.isEmpty()) {
            return;
        }

        for (Announcement announcement : announcements) {
            processAnnouncementData(announcement);
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                
                // 先尝试按用户名查找
                SysUser user = sysUserMapper.selectOne(
                    new QueryWrapper<SysUser>().eq("username", username).eq("deleted", 0)
                );
                
                // 如果按用户名找不到，尝试按手机号查找
                if (user == null) {
                    user = sysUserMapper.selectOne(
                        new QueryWrapper<SysUser>().eq("phone", username).eq("deleted", 0)
                    );
                }
                
                return user != null ? user.getId() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取类型文本
     */
    private String getTypeText(String type) {
        if (type == null) {
            return "未知";
        }
        
        switch (type) {
            case "NOTICE":
                return "通知";
            case "ACTIVITY":
                return "活动";
            case "MAINTENANCE":
                return "维护";
            case "EMERGENCY":
                return "紧急";
            default:
                return "其他";
        }
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        if (status == null) {
            return "未知";
        }
        
        switch (status) {
            case "DRAFT":
                return "草稿";
            case "PUBLISHED":
                return "已发布";
            case "EXPIRED":
                return "已过期";
            case "OFFLINE":
                return "已下线";
            default:
                return "未知";
        }
    }

    /**
     * 获取优先级文本
     */
    private String getPriorityText(String priority) {
        if (priority == null) {
            return "普通";
        }
        
        switch (priority) {
            case "LOW":
                return "低";
            case "NORMAL":
                return "普通";
            case "HIGH":
                return "高";
            default:
                return "普通";
        }
    }
}

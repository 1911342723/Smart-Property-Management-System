package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageResult;
import com.property.entity.Message;

/**
 * 消息服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface MessageService extends IService<Message> {

    /**
     * 分页查询用户消息
     */
    PageResult<Message> getMessagePage(int pageNum, int pageSize, 
                                       Long receiverId, String messageType, Integer isRead);

    /**
     * 发送消息
     */
    void sendMessage(Message message);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId, Long userId);

    /**
     * 批量标记消息为已读
     */
    void batchMarkAsRead(Long[] messageIds, Long userId);

    /**
     * 统计未读消息数量
     */
    int countUnreadMessages(Long userId, String messageType);

    /**
     * 发送系统消息
     */
    void sendSystemMessage(String title, String content, Long receiverId);

    /**
     * 发送服务消息（工单相关）
     */
    void sendServiceMessage(String title, String content, Long receiverId, 
                           Long businessId, String businessType);

    /**
     * 发送公告消息
     */
    void sendNoticeMessage(String title, String content, Long[] receiverIds);
}

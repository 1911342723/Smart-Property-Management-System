package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.Message;
import com.property.mapper.MessageMapper;
import com.property.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
@Transactional
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public PageResult<Message> getMessagePage(int pageNum, int pageSize, 
                                              Long receiverId, String messageType, Integer isRead) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        IPage<Message> result = baseMapper.selectMessagePageWithSender(page, receiverId, messageType, isRead);
        
        return new PageResult<Message>(
            result.getRecords(),
            result.getTotal(),
            Long.valueOf(pageNum),
            Long.valueOf(pageSize)
        );
    }

    @Override
    public void sendMessage(Message message) {
        // 设置默认值
        if (message.getIsRead() == null) {
            message.setIsRead(0);
        }
        if (message.getLevel() == null) {
            message.setLevel("INFO");
        }
        
        // 根据消息类型设置默认图标
        setDefaultIcon(message);
        
        save(message);
    }

    @Override
    public void markAsRead(Long messageId, Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("id", messageId)
               .eq("receiver_id", userId);
        
        Message message = new Message();
        message.setIsRead(1);
        
        update(message, wrapper);
    }

    @Override
    public void batchMarkAsRead(Long[] messageIds, Long userId) {
        baseMapper.markMessagesAsRead(userId, messageIds);
    }

    @Override
    public int countUnreadMessages(Long userId, String messageType) {
        return baseMapper.countUnreadMessages(userId, messageType);
    }

    @Override
    public void sendSystemMessage(String title, String content, Long receiverId) {
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType("SYSTEM");
        message.setReceiverId(receiverId);
        message.setLevel("INFO");
        
        sendMessage(message);
    }

    @Override
    public void sendServiceMessage(String title, String content, Long receiverId, 
                                  Long businessId, String businessType) {
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType("SERVICE");
        message.setReceiverId(receiverId);
        message.setBusinessId(businessId);
        message.setBusinessType(businessType);
        message.setLevel("INFO");
        
        sendMessage(message);
    }

    @Override
    public void sendNoticeMessage(String title, String content, Long[] receiverIds) {
        for (Long receiverId : receiverIds) {
            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setMessageType("NOTICE");
            message.setReceiverId(receiverId);
            message.setLevel("INFO");
            
            sendMessage(message);
        }
    }

    /**
     * 根据消息类型设置默认图标
     */
    private void setDefaultIcon(Message message) {
        if (message.getIcon() == null || message.getIconBg() == null) {
            switch (message.getMessageType()) {
                case "SYSTEM":
                    message.setIcon("/static/icons/settings.svg");
                    message.setIconBg("linear-gradient(135deg, #718096 0%, #4a5568 100%)");
                    break;
                case "SERVICE":
                    message.setIcon("/static/icons/wrench.svg");
                    message.setIconBg("linear-gradient(135deg, #38a169 0%, #2f855a 100%)");
                    break;
                case "NOTICE":
                    message.setIcon("/static/icons/info.svg");
                    message.setIconBg("linear-gradient(135deg, #2d3748 0%, #1a202c 100%)");
                    break;
                default:
                    message.setIcon("/static/icons/message.svg");
                    message.setIconBg("linear-gradient(135deg, #5865f2 0%, #3c45a5 100%)");
                    break;
            }
        }
    }
}

package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 分页查询用户消息（带发送人信息）
     */
    IPage<Message> selectMessagePageWithSender(Page<Message> page, 
                                               @Param("receiverId") Long receiverId,
                                               @Param("messageType") String messageType,
                                               @Param("isRead") Integer isRead);

    /**
     * 统计用户未读消息数量
     */
    int countUnreadMessages(@Param("receiverId") Long receiverId, 
                           @Param("messageType") String messageType);

    /**
     * 批量标记消息为已读
     */
    int markMessagesAsRead(@Param("receiverId") Long receiverId, 
                          @Param("messageIds") Long[] messageIds);
}

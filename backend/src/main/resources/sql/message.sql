-- ========================================
-- 消息中心数据表创建脚本
-- 用于存储小程序中的各种消息通知
-- ========================================

CREATE TABLE IF NOT EXISTS `message` (
    -- 基础字段
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一标识ID（主键，自增）',
    
    -- 消息内容字段
    `title` varchar(200) NOT NULL COMMENT '消息标题（显示在消息列表中的标题）',
    `content` text NOT NULL COMMENT '消息正文内容（详细的消息内容）',
    
    -- 消息分类字段
    `message_type` varchar(50) NOT NULL COMMENT '消息类型分类：SYSTEM=系统消息，SERVICE=服务消息，NOTICE=公告消息',
    `level` varchar(20) DEFAULT 'INFO' COMMENT '消息重要程度：INFO=普通信息，WARNING=警告消息，ERROR=错误消息',
    
    -- 用户关联字段
    `sender_id` bigint DEFAULT NULL COMMENT '发送人用户ID（谁发送的消息，可为空表示系统发送）',
    `receiver_id` bigint NOT NULL COMMENT '接收人用户ID（消息发给谁，必填）',
    
    -- 消息状态字段
    `is_read` tinyint DEFAULT 0 COMMENT '阅读状态标记：0=未读消息，1=已读消息',
    
    -- 业务关联字段（可选）
    `business_id` bigint DEFAULT NULL COMMENT '关联的业务数据ID（如工单ID、账单ID等）',
    `business_type` varchar(50) DEFAULT NULL COMMENT '关联的业务类型：WORK_ORDER=工单业务，BILL=账单业务，NOTICE=公告业务',
    
    -- 显示样式字段
    `icon` varchar(200) DEFAULT NULL COMMENT '消息图标路径（用于在前端显示不同类型消息的图标）',
    `icon_bg` varchar(200) DEFAULT NULL COMMENT '图标背景色样式（CSS渐变色代码）',
    
    -- 系统管理字段
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间（自动记录）',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '消息最后更新时间（自动更新）',
    `deleted` tinyint DEFAULT 0 COMMENT '软删除标记：0=正常数据，1=已删除数据',
    
    -- 主键定义
    PRIMARY KEY (`id`),
    
    -- 索引定义（提高查询性能）
    KEY `idx_receiver_type` (`receiver_id`, `message_type`) COMMENT '按接收人和消息类型查询的复合索引',
    KEY `idx_receiver_read` (`receiver_id`, `is_read`) COMMENT '按接收人和阅读状态查询的复合索引',
    KEY `idx_create_time` (`create_time`) COMMENT '按创建时间排序的索引',
    KEY `idx_business` (`business_id`, `business_type`) COMMENT '按业务关联查询的复合索引'
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息中心数据表-存储用户消息通知';

-- ========================================
-- 插入测试数据
-- 为消息表添加一些示例数据用于测试
-- ========================================

INSERT INTO `message` (
    `title`,           -- 消息标题
    `content`,         -- 消息内容 
    `message_type`,    -- 消息类型
    `level`,           -- 消息级别
    `sender_id`,       -- 发送人ID
    `receiver_id`,     -- 接收人ID
    `is_read`,         -- 是否已读
    `business_id`,     -- 关联业务ID
    `business_type`,   -- 业务类型
    `icon`,            -- 图标路径
    `icon_bg`          -- 图标背景色
) VALUES

-- 服务消息示例1：报修进度通知（未读）
('报修进度更新', 
 '您的水龙头维修申请已被维修工接单，预计今日下午完成', 
 'SERVICE',     -- 服务类消息
 'INFO',        -- 普通信息级别
 1,             -- 管理员发送
 2,             -- 发给业主ID=2
 0,             -- 未读状态
 1,             -- 关联工单ID=1
 'WORK_ORDER',  -- 工单业务类型
 '/static/icons/wrench.svg', 
 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)'),

-- 服务消息示例2：缴费提醒（未读）
('缴费提醒', 
 '您的2024年1月份物业费即将到期，请及时缴纳', 
 'SERVICE',     -- 服务类消息
 'WARNING',     -- 警告级别
 1,             -- 管理员发送
 2,             -- 发给业主ID=2
 0,             -- 未读状态
 1,             -- 关联账单ID=1
 'BILL',        -- 账单业务类型
 '/static/icons/payment.svg', 
 'linear-gradient(135deg, #d69e2e 0%, #b7791f 100%)'),

-- 系统消息示例1：维护通知（已读）
('系统维护通知', 
 '系统将于今晚23:00-01:00进行维护，期间部分功能可能无法使用', 
 'SYSTEM',      -- 系统类消息
 'INFO',        -- 普通信息级别
 1,             -- 管理员发送
 2,             -- 发给业主ID=2
 1,             -- 已读状态
 NULL,          -- 无关联业务
 NULL,          -- 无业务类型
 '/static/icons/settings.svg', 
 'linear-gradient(135deg, #718096 0%, #4a5568 100%)'),

-- 服务消息示例3：投诉处理结果（已读）
('投诉处理结果', 
 '您的噪音投诉已处理完成，感谢您的反馈', 
 'SERVICE',     -- 服务类消息
 'INFO',        -- 普通信息级别
 1,             -- 管理员发送
 2,             -- 发给业主ID=2
 1,             -- 已读状态
 2,             -- 关联工单ID=2
 'WORK_ORDER',  -- 工单业务类型
 '/static/icons/check-circle.svg', 
 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)'),

-- 系统消息示例2：版本更新（未读）
('版本更新', 
 '智慧物业小程序已更新至v1.1.0，新增AI客服功能', 
 'SYSTEM',      -- 系统类消息
 'INFO',        -- 普通信息级别
 1,             -- 管理员发送
 2,             -- 发给业主ID=2
 0,             -- 未读状态
 NULL,          -- 无关联业务
 NULL,          -- 无业务类型
 '/static/icons/info.svg', 
 'linear-gradient(135deg, #2d3748 0%, #1a202c 100%)');

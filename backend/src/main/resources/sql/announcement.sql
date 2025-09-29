-- ========================================
-- 公告管理系统数据表创建脚本
-- 用于存储小程序和web管理端的公告信息
-- ========================================

-- 删除已存在的表
DROP TABLE IF EXISTS `announcement`;

-- 创建公告表
CREATE TABLE `announcement` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` varchar(200) NOT NULL COMMENT '公告标题',
    `content` text NOT NULL COMMENT '公告内容',
    `summary` varchar(500) DEFAULT NULL COMMENT '公告摘要',
    `type` varchar(20) NOT NULL COMMENT '公告类型：NOTICE-通知，ACTIVITY-活动，MAINTENANCE-维护，EMERGENCY-紧急',
    `priority` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：LOW-低，NORMAL-普通，HIGH-高',
    `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下线，EXPIRED-已过期',
    `publisher_id` bigint NOT NULL COMMENT '发布人ID',
    `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
    `read_count` int NOT NULL DEFAULT '0' COMMENT '阅读次数',
    `images` text COMMENT '图片地址（JSON数组）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`) COMMENT '发布人ID索引',
    KEY `idx_type` (`type`) COMMENT '公告类型索引',
    KEY `idx_status` (`status`) COMMENT '状态索引',
    KEY `idx_priority` (`priority`) COMMENT '优先级索引',
    KEY `idx_publish_time` (`publish_time`) COMMENT '发布时间索引',
    KEY `idx_is_top` (`is_top`) COMMENT '置顶索引',
    KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
    CONSTRAINT `fk_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ========================================
-- 插入测试数据
-- ========================================

-- 插入公告测试数据
INSERT INTO `announcement` (`id`, `title`, `content`, `summary`, `type`, `priority`, `status`, `publisher_id`, `publish_time`, `is_top`, `read_count`) VALUES
(1, '小区物业费缴费通知', 
'各位业主您好：\n\n2024年1月份物业费已开始缴费，请及时缴纳。\n\n缴费方式：\n1. 微信小程序在线缴费\n2. 支付宝扫码缴费\n3. 银行转账\n4. 物业服务中心现场缴费\n\n缴费截止日期：2024年1月25日\n逾期将产生滞纳金，请各位业主务必按时缴费。\n\n如有疑问，请联系物业服务中心。\n\n物业服务中心\n2024年1月1日', 
'2024年1月份物业费已开始缴费，请及时缴纳。缴费方式：微信小程序、支付宝、银行转账。', 
'NOTICE', 'HIGH', 'PUBLISHED', 1001, '2024-01-01 09:00:00', 1, 156),

(2, '电梯维护公告', 
'各位业主：\n\n因设备维护需要，1号楼电梯将于12月15日晚上22:00-次日6:00进行维护。\n\n维护期间电梯停止使用，请业主提前安排出行，给您带来的不便敬请谅解。\n\n如有紧急情况，请联系物业值班室：400-888-9999\n\n物业服务中心\n2023年12月10日', 
'因设备维护需要，1号楼电梯将于12月15日晚上22:00-次日6:00进行维护，请业主提前安排出行。', 
'MAINTENANCE', 'HIGH', 'PUBLISHED', 1001, '2023-12-10 16:00:00', 0, 89),

(3, '元旦联欢活动邀请', 
'亲爱的业主朋友们：\n\n小区将于12月31日晚19:00举办元旦联欢活动，欢迎各位业主参加！\n\n活动时间：2023年12月31日 19:00-21:00\n活动地点：小区中央广场\n活动内容：\n- 文艺表演\n- 游戏互动\n- 抽奖活动\n- 茶点招待\n\n请有意参加的业主到物业服务中心报名，报名截止时间：12月28日。\n\n让我们一起迎接新年的到来！\n\n社区委员会\n2023年12月15日', 
'小区将于12月31日晚举办元旦联欢活动，欢迎各位业主参加，具体安排详见活动页面。', 
'ACTIVITY', 'NORMAL', 'PUBLISHED', 1001, '2023-12-15 10:00:00', 0, 234),

(4, '停车位管理规定', 
'各位车主业主：\n\n为了规范小区停车秩序，现对停车位管理进行调整：\n\n1. 固定车位：按月收费，费用200元/月\n2. 临时停车：按小时收费，费用5元/小时\n3. 访客车位：免费停车2小时，超时按临时停车收费\n4. 禁止占用消防通道、绿化带等区域停车\n5. 违规停车将被拖移，费用自理\n\n新规定将于2024年1月1日起执行，请各位车主遵守相关规定。\n\n物业管理处\n2023年12月18日', 
'为了规范小区停车秩序，现对停车位管理进行调整，请各位车主遵守相关规定。', 
'NOTICE', 'NORMAL', 'PUBLISHED', 1001, '2023-12-18 14:30:00', 0, 78),

(5, '春节期间服务安排', 
'各位业主：\n\n春节期间（2月10日-2月17日）物业服务安排如下：\n\n值班时间：\n- 除夕至初三（2月10日-13日）：8:00-18:00\n- 初四至初六（2月14日-16日）：9:00-17:00\n- 初七（2月17日）起恢复正常工作时间\n\n紧急联系电话：400-888-9999（24小时）\n\n值班服务：\n- 水电维修\n- 安全巡查\n- 清洁保洁\n- 门禁管理\n\n祝大家新春快乐，阖家幸福！\n\n物业服务中心\n2023年12月20日', 
'春节期间（2月10日-2月17日）物业服务安排通知，紧急联系电话：400-888-9999。', 
'NOTICE', 'HIGH', 'PUBLISHED', 1001, '2023-12-20 11:00:00', 1, 312),

(6, '垃圾分类倡议书', 
'亲爱的业主朋友们：\n\n为响应城市垃圾分类号召，共建美好绿色社区，我们倡议：\n\n垃圾分类标准：\n🗂️ 厨余垃圾：剩菜剩饭、瓜皮果核、茶叶渣等\n♻️ 可回收物：纸类、塑料、金属、玻璃等\n⚠️ 有害垃圾：废电池、废灯泡、过期药品等\n🗑️ 其他垃圾：除上述外的生活废弃物\n\n投放要求：\n- 分类投放到对应颜色的垃圾桶\n- 厨余垃圾需沥干水分\n- 可回收物保持清洁干燥\n- 有害垃圾单独包装\n\n垃圾分类点已设置在各栋楼下，请大家积极参与，共建绿色家园！\n\n环保委员会\n2023-12-22', 
'为响应城市垃圾分类号召，共建美好绿色社区，请大家积极参与垃圾分类。', 
'NOTICE', 'NORMAL', 'PUBLISHED', 1001, '2023-12-22 15:00:00', 0, 145),

(7, '紧急停水通知', 
'紧急通知：\n\n因供水管道突发故障，需要紧急抢修，今日下午15:00-19:00将全面停水。\n\n影响范围：全小区\n停水时间：今日15:00-19:00（预计）\n\n请各位业主：\n1. 提前储备生活用水\n2. 关闭家中水龙头，避免来水时溅水\n3. 如有紧急用水需求，可到物业服务中心\n\n抢修完成后将立即恢复供水，具体恢复时间另行通知。\n\n给您带来的不便深表歉意！\n\n物业服务中心\n2024年1月5日 12:00', 
'因供水管道突发故障，今日下午15:00-19:00将全面停水，请提前储备用水。', 
'EMERGENCY', 'HIGH', 'PUBLISHED', 1001, '2024-01-05 12:00:00', 1, 89),

(8, '小区安全提醒', 
'各位业主：\n\n近期发生几起安全事件，为保障大家的人身财产安全，特别提醒：\n\n安全提醒：\n1. 外出时请锁好门窗，贵重物品妥善保管\n2. 不要给陌生人开门或提供门禁密码\n3. 发现可疑人员请及时联系物业或报警\n4. 车辆停放后请锁好车门，贵重物品不要留在车内\n5. 夜间出行请结伴而行，注意人身安全\n\n安保措施：\n- 已加强门禁管理和巡逻力度\n- 增加监控设备和照明设施\n- 24小时安保值班\n\n如发现异常情况，请立即联系：\n物业值班室：400-888-9999\n报警电话：110\n\n让我们共同维护小区安全！\n\n物业服务中心\n2024年1月8日', 
'近期发生几起安全事件，为保障大家的人身财产安全，请注意防范。', 
'NOTICE', 'HIGH', 'PUBLISHED', 1001, '2024-01-08 10:00:00', 0, 203);

-- 查看插入结果
SELECT 
    a.id,
    a.title,
    a.type,
    a.priority,
    a.status,
    u.real_name as publisher_name,
    a.publish_time,
    a.is_top,
    a.read_count
FROM announcement a
LEFT JOIN sys_user u ON a.publisher_id = u.id
WHERE a.deleted = 0
ORDER BY a.is_top DESC, a.publish_time DESC;

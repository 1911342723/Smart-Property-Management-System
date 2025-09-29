-- 物业管理系统测试数据

USE property_management;

-- ====================================
-- 用户和角色数据
-- ====================================

-- 插入角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
(2, '物业管理员', 'ADMIN', '物业管理员，负责日常管理工作'),
(3, '业主', 'OWNER', '小区业主用户'),
(4, '保安', 'GUARD', '小区保安人员'),
(5, '维修工', 'WORKER', '维修工作人员');

-- 插入用户数据（密码为123456的MD5加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
(1000, 'admin', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '系统管理员', '13800000001', 'admin@property.com', 'ADMIN', 1),
(1001, 'manager', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '物业经理', '13800000002', 'manager@property.com', 'ADMIN', 1),
(1002, 'owner001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '张三', '13800000101', 'zhangsan@example.com', 'OWNER', 1),
(1003, 'owner002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '李四', '13800000102', 'lisi@example.com', 'OWNER', 1),
(1004, 'owner003', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '王五', '13800000103', 'wangwu@example.com', 'OWNER', 1),
(1005, 'guard001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '保安小刘', '13800000201', 'guard001@property.com', 'GUARD', 1),
(1006, 'guard002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '保安小赵', '13800000202', 'guard002@property.com', 'GUARD', 1),
(1007, 'worker001', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '维修师傅老张', '13800000301', 'worker001@property.com', 'WORKER', 1),
(1008, 'worker002', '$2a$10$7JB720yubVSOfvVWbgFTOOWYUh5D1JPJN1xKoZxPyMxzYcElKrL8K', '维修师傅老李', '13800000302', 'worker002@property.com', 'WORKER', 1);

-- 插入用户角色关联数据
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1000, 1), -- admin - 超级管理员
(1001, 2), -- manager - 物业管理员
(1002, 3), -- owner001 - 业主
(1003, 3), -- owner002 - 业主
(1004, 3), -- owner003 - 业主
(1005, 4), -- guard001 - 保安
(1006, 4), -- guard002 - 保安
(1007, 5), -- worker001 - 维修工
(1008, 5); -- worker002 - 维修工

-- ====================================
-- 房产数据
-- ====================================

-- 插入楼栋数据
INSERT INTO `building` (`id`, `building_name`, `building_no`, `total_floors`, `total_units`, `total_rooms`) VALUES
(1, '1号楼', 'A001', 18, 2, 72),
(2, '2号楼', 'A002', 18, 2, 72),
(3, '3号楼', 'A003', 25, 3, 150),
(4, '商业楼', 'B001', 5, 1, 20);

-- 插入单元数据
INSERT INTO `unit` (`id`, `building_id`, `unit_name`, `unit_no`, `total_floors`, `total_rooms`) VALUES
(1, 1, '1单元', '1', 18, 36),
(2, 1, '2单元', '2', 18, 36),
(3, 2, '1单元', '1', 18, 36),
(4, 2, '2单元', '2', 18, 36),
(5, 3, '1单元', '1', 25, 50),
(6, 3, '2单元', '2', 25, 50),
(7, 3, '3单元', '3', 25, 50),
(8, 4, '1单元', '1', 5, 20);

-- 插入房屋数据（示例部分房屋）
INSERT INTO `room` (`id`, `building_id`, `unit_id`, `room_no`, `floor`, `room_type`, `area`, `status`, `property_fee`) VALUES
(1, 1, 1, '101', 1, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50),
(2, 1, 1, '102', 1, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50),
(3, 1, 1, '201', 2, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50),
(4, 1, 1, '301', 3, 'RESIDENTIAL', 89.50, 'VACANT', 268.50),
(5, 1, 1, '401', 4, 'RESIDENTIAL', 89.50, 'VACANT', 268.50),
(6, 1, 2, '101', 1, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50),
(7, 1, 2, '201', 2, 'RESIDENTIAL', 89.50, 'OCCUPIED', 268.50),
(8, 2, 3, '101', 1, 'RESIDENTIAL', 95.80, 'OCCUPIED', 287.40),
(9, 2, 3, '201', 2, 'RESIDENTIAL', 95.80, 'VACANT', 287.40),
(10, 3, 5, '1501', 15, 'RESIDENTIAL', 120.00, 'OCCUPIED', 360.00),
(11, 4, 8, '101', 1, 'COMMERCIAL', 50.00, 'RENTED', 200.00);

-- 插入业主房产关联数据
INSERT INTO `owner_room` (`owner_id`, `room_id`, `relation_type`, `start_date`) VALUES
(1002, 1, 'OWNER', '2023-01-01'),    -- 张三 - 1号楼1单元101
(1003, 2, 'OWNER', '2023-02-01'),    -- 李四 - 1号楼1单元102
(1004, 3, 'OWNER', '2023-03-01'),    -- 王五 - 1号楼1单元201
(1002, 6, 'OWNER', '2023-01-01'),    -- 张三 - 1号楼2单元101（一个业主多套房）
(1003, 8, 'OWNER', '2023-04-01'),    -- 李四 - 2号楼1单元101
(1004, 10, 'OWNER', '2023-05-01');   -- 王五 - 3号楼1单元1501

-- ====================================
-- 工单数据
-- ====================================

-- 插入工单数据
INSERT INTO `work_order` (`id`, `order_no`, `title`, `content`, `category`, `priority`, `status`, `submitter_id`, `assignee_id`, `room_id`, `submit_time`) VALUES
(1, 'WO202312010001', '厨房水龙头漏水', '厨房洗菜盆水龙头一直在滴水，请尽快维修', 'REPAIR', 'MEDIUM', 'COMPLETED', 1002, 1007, 1, '2023-12-01 09:30:00'),
(2, 'WO202312010002', '电梯噪音问题', '1号楼电梯运行时噪音很大，影响居住', 'COMPLAINT', 'HIGH', 'PROCESSING', 1003, 1008, 2, '2023-12-01 14:20:00'),
(3, 'WO202312010003', '门锁故障', '房门锁具损坏，无法正常开锁', 'REPAIR', 'HIGH', 'PENDING', 1004, NULL, 3, '2023-12-01 18:45:00'),
(4, 'WO202312020001', '小区绿化建议', '建议在小区增加一些健身器材', 'SUGGESTION', 'LOW', 'PENDING', 1002, NULL, NULL, '2023-12-02 10:15:00'),
(5, 'WO202312020002', '阳台护栏松动', '阳台护栏有松动，存在安全隐患', 'REPAIR', 'URGENT', 'PROCESSING', 1003, 1007, 8, '2023-12-02 16:30:00');

-- 插入工单处理记录
INSERT INTO `work_order_log` (`order_id`, `operator_id`, `operation`, `content`) VALUES
(1, 1002, 'SUBMIT', '提交维修申请：厨房水龙头漏水'),
(1, 1001, 'ASSIGN', '分配给维修师傅老张处理'),
(1, 1007, 'START', '开始处理工单，已到现场查看'),
(1, 1007, 'COMPLETE', '维修完成，更换了新的水龙头'),
(2, 1003, 'SUBMIT', '提交投诉：电梯噪音问题'),
(2, 1001, 'ASSIGN', '分配给维修师傅老李处理'),
(2, 1008, 'START', '开始处理，正在检查电梯设备'),
(3, 1004, 'SUBMIT', '提交维修申请：门锁故障'),
(4, 1002, 'SUBMIT', '提交建议：小区绿化建议'),
(5, 1003, 'SUBMIT', '提交维修申请：阳台护栏松动'),
(5, 1001, 'ASSIGN', '分配给维修师傅老张处理'),
(5, 1007, 'START', '开始处理，正在采购配件');

-- ====================================
-- 财务数据
-- ====================================

-- 插入账单数据
INSERT INTO `bill` (`id`, `bill_no`, `room_id`, `owner_id`, `bill_type`, `billing_period`, `amount`, `status`, `due_date`) VALUES
(1, 'BILL202312001', 1, 1002, 'PROPERTY', '2023-12', 268.50, 'PAID', '2023-12-31'),
(2, 'BILL202312002', 2, 1003, 'PROPERTY', '2023-12', 268.50, 'PAID', '2023-12-31'),
(3, 'BILL202312003', 3, 1004, 'PROPERTY', '2023-12', 268.50, 'UNPAID', '2023-12-31'),
(4, 'BILL202312004', 6, 1002, 'PROPERTY', '2023-12', 268.50, 'PAID', '2023-12-31'),
(5, 'BILL202312005', 8, 1003, 'PROPERTY', '2023-12', 287.40, 'UNPAID', '2023-12-31'),
(6, 'BILL202312006', 10, 1004, 'PROPERTY', '2023-12', 360.00, 'PAID', '2023-12-31'),
(7, 'BILL202312007', 1, 1002, 'WATER', '2023-12', 45.60, 'PAID', '2023-12-31'),
(8, 'BILL202312008', 2, 1003, 'ELECTRICITY', '2023-12', 125.80, 'UNPAID', '2023-12-31'),
(9, 'BILL202401001', 1, 1002, 'PROPERTY', '2024-01', 268.50, 'UNPAID', '2024-01-31'),
(10, 'BILL202401002', 2, 1003, 'PROPERTY', '2024-01', 268.50, 'UNPAID', '2024-01-31');

-- 插入缴费记录数据
INSERT INTO `payment` (`id`, `payment_no`, `bill_id`, `payer_id`, `amount`, `payment_method`, `payment_time`, `status`) VALUES
(1, 'PAY202312010001', 1, 1002, 268.50, 'WECHAT', '2023-12-05 10:30:00', 'SUCCESS'),
(2, 'PAY202312010002', 2, 1003, 268.50, 'ALIPAY', '2023-12-08 14:20:00', 'SUCCESS'),
(3, 'PAY202312010003', 4, 1002, 268.50, 'WECHAT', '2023-12-05 10:35:00', 'SUCCESS'),
(4, 'PAY202312010004', 6, 1004, 360.00, 'BANK', '2023-12-10 09:15:00', 'SUCCESS'),
(5, 'PAY202312010005', 7, 1002, 45.60, 'WECHAT', '2023-12-12 16:45:00', 'SUCCESS');

-- ====================================
-- 社区数据
-- ====================================

-- 插入公告数据
INSERT INTO `announcement` (`id`, `title`, `content`, `type`, `priority`, `publisher_id`, `publish_time`, `is_top`, `read_count`) VALUES
(1, '小区物业费缴费通知', '各位业主您好，2024年1月份物业费已开始缴费，请及时缴纳。缴费方式：微信小程序、支付宝、银行转账。', 'NOTICE', 'HIGH', 1001, '2023-12-01 09:00:00', 1, 156),
(2, '电梯维护公告', '因设备维护需要，1号楼电梯将于12月15日晚上22:00-次日6:00进行维护，请业主提前安排出行。', 'MAINTENANCE', 'HIGH', 1001, '2023-12-10 16:00:00', 0, 89),
(3, '元旦联欢活动邀请', '小区将于12月31日晚举办元旦联欢活动，欢迎各位业主参加，具体安排详见活动页面。', 'ACTIVITY', 'NORMAL', 1001, '2023-12-15 10:00:00', 0, 234),
(4, '停车位管理规定', '为了规范小区停车秩序，现对停车位管理进行调整，请各位车主遵守相关规定。', 'NOTICE', 'NORMAL', 1001, '2023-12-18 14:30:00', 0, 78),
(5, '春节期间服务安排', '春节期间（2月10日-2月17日）物业服务安排通知，紧急联系电话：400-888-9999。', 'NOTICE', 'HIGH', 1001, '2023-12-20 11:00:00', 1, 312);

-- 插入活动数据
INSERT INTO `activity` (`id`, `title`, `description`, `organizer_id`, `location`, `start_time`, `end_time`, `max_participants`, `registration_start`, `registration_end`, `status`, `is_free`) VALUES
(1, '元旦联欢会', '小区业主元旦联欢活动，有精彩表演和抽奖环节', 1001, '小区活动中心', '2023-12-31 19:00:00', '2023-12-31 22:00:00', 200, '2023-12-15 10:00:00', '2023-12-30 18:00:00', 'REGISTRATION', 1),
(2, '亲子运动会', '小区儿童亲子运动会，增进家庭感情', 1001, '小区中心广场', '2024-01-06 14:00:00', '2024-01-06 17:00:00', 100, '2023-12-20 09:00:00', '2024-01-05 18:00:00', 'PLANNED', 1),
(3, '健康知识讲座', '邀请专业医生讲解冬季健康保健知识', 1001, '小区会议室', '2024-01-13 15:00:00', '2024-01-13 16:30:00', 50, '2024-01-01 09:00:00', '2024-01-12 18:00:00', 'PLANNED', 1);

-- 插入活动报名数据
INSERT INTO `activity_registration` (`activity_id`, `user_id`, `participants`, `contact_name`, `contact_phone`, `status`) VALUES
(1, 1002, 2, '张三', '13800000101', 'REGISTERED'),
(1, 1003, 1, '李四', '13800000102', 'REGISTERED'),
(1, 1004, 3, '王五', '13800000103', 'REGISTERED');

-- ====================================
-- 访客数据
-- ====================================

-- 插入访客预约数据
INSERT INTO `visitor` (`id`, `visitor_name`, `visitor_phone`, `room_id`, `owner_id`, `visit_purpose`, `planned_arrival`, `planned_departure`, `status`) VALUES
(1, '赵六', '13900000001', 1, 1002, '探访朋友', '2023-12-25 14:00:00', '2023-12-25 18:00:00', 'APPROVED'),
(2, '钱七', '13900000002', 2, 1003, '送货', '2023-12-26 10:00:00', '2023-12-26 11:00:00', 'APPROVED'),
(3, '孙八', '13900000003', 3, 1004, '家庭聚会', '2023-12-30 16:00:00', '2023-12-30 22:00:00', 'PENDING');

-- ====================================
-- 系统配置数据
-- ====================================

-- 插入系统配置数据
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`, `is_system`) VALUES
('system.name', '物业管理系统', 'STRING', '系统名称', 1),
('system.version', '1.0.0', 'STRING', '系统版本', 1),
('property.fee.rate', '3.0', 'NUMBER', '物业费标准（元/平方米/月）', 0),
('visitor.max.days', '7', 'NUMBER', '访客预约最大天数', 0),
('file.upload.path', '/uploads/', 'STRING', '文件上传路径', 1),
('file.max.size', '10485760', 'NUMBER', '文件最大大小（字节）', 1),
('notification.enabled', 'true', 'BOOLEAN', '是否启用消息通知', 0),
('maintenance.phone', '400-888-9999', 'STRING', '维修服务电话', 0);







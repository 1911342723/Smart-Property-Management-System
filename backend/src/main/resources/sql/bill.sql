-- 账单表
CREATE TABLE IF NOT EXISTS `bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_no` varchar(32) NOT NULL COMMENT '账单编号',
  `room_id` bigint(20) NOT NULL COMMENT '房屋ID',
  `owner_id` bigint(20) NOT NULL COMMENT '业主ID',
  `bill_type` varchar(20) NOT NULL COMMENT '账单类型：PROPERTY-物业费，UTILITY-水电费，PARKING-停车费，MAINTENANCE-维修费',
  `billing_period` varchar(20) NOT NULL COMMENT '计费周期，如：2024年1月',
  `amount` decimal(10,2) NOT NULL COMMENT '应缴金额',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已缴金额',
  `status` varchar(20) DEFAULT 'UNPAID' COMMENT '状态：UNPAID-未缴费，PAID-已缴费，OVERDUE-逾期',
  `due_date` date COMMENT '缴费截止日期',
  `paid_date` datetime COMMENT '缴费日期',
  `description` text COMMENT '费用说明',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_bill_type` (`bill_type`),
  KEY `idx_status` (`status`),
  KEY `idx_billing_period` (`billing_period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_no` varchar(32) NOT NULL COMMENT '支付编号',
  `bill_id` bigint(20) NOT NULL COMMENT '账单ID',
  `owner_id` bigint(20) NOT NULL COMMENT '业主ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式：WECHAT-微信支付，ALIPAY-支付宝，BANK-银行转账',
  `status` varchar(20) DEFAULT 'SUCCESS' COMMENT '支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败',
  `transaction_id` varchar(64) COMMENT '第三方交易号',
  `payment_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  `remark` varchar(500) COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_status` (`status`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 插入测试账单数据
INSERT INTO `bill` (`bill_no`, `room_id`, `owner_id`, `bill_type`, `billing_period`, `amount`, `paid_amount`, `status`, `due_date`, `description`) VALUES
('BILL202412250001', 1, 1002, 'PROPERTY', '2024年12月', 658.50, 0.00, 'UNPAID', '2025-01-15', '包含保洁、绿化、安保等服务'),
('BILL202412250002', 1, 1002, 'UTILITY', '2024年12月', 398.00, 0.00, 'UNPAID', '2025-01-20', '用水量：15.2吨，用电量：268度'),
('BILL202412250003', 1, 1002, 'PARKING', '2024年12月', 200.00, 0.00, 'OVERDUE', '2024-12-31', '地下车位 B2-088'),
('BILL202411250001', 1, 1002, 'PROPERTY', '2024年11月', 658.50, 658.50, 'PAID', '2024-12-15', '包含保洁、绿化、安保等服务'),
('BILL202411250002', 1, 1002, 'UTILITY', '2024年11月', 356.80, 356.80, 'PAID', '2024-12-20', '用水量：13.8吨，用电量：245度'),
('BILL202410250001', 1, 1002, 'PROPERTY', '2024年10月', 658.50, 658.50, 'PAID', '2024-11-15', '包含保洁、绿化、安保等服务'),
('BILL202410250002', 1, 1002, 'UTILITY', '2024年10月', 412.30, 412.30, 'PAID', '2024-11-20', '用水量：16.5吨，用电量：289度'),
('BILL202410250003', 1, 1002, 'PARKING', '2024年10月', 200.00, 200.00, 'PAID', '2024-10-31', '地下车位 B2-088');

-- 插入测试支付记录数据
INSERT INTO `payment` (`payment_no`, `bill_id`, `owner_id`, `amount`, `payment_method`, `status`, `transaction_id`, `payment_time`, `remark`) VALUES
('PAY202411250001', 4, 1002, 658.50, 'WECHAT', 'SUCCESS', 'wx_20241125001234567890', '2024-11-25 10:30:15', '物业费缴纳'),
('PAY202411250002', 5, 1002, 356.80, 'WECHAT', 'SUCCESS', 'wx_20241125001234567891', '2024-11-25 10:31:20', '水电费缴纳'),
('PAY202410250001', 6, 1002, 658.50, 'ALIPAY', 'SUCCESS', 'alipay_20241025123456789', '2024-10-25 14:22:35', '物业费缴纳'),
('PAY202410250002', 7, 1002, 412.30, 'ALIPAY', 'SUCCESS', 'alipay_20241025123456790', '2024-10-25 14:23:40', '水电费缴纳'),
('PAY202410250003', 8, 1002, 200.00, 'WECHAT', 'SUCCESS', 'wx_20241025001234567892', '2024-10-31 16:45:12', '停车费缴纳');







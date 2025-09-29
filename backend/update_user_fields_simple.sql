-- 简单的用户表字段更新脚本
-- 直接添加字段，如果字段已存在会报错但不影响

ALTER TABLE sys_user ADD COLUMN gender VARCHAR(10) COMMENT '性别：MALE-男，FEMALE-女，OTHER-其他';
ALTER TABLE sys_user ADD COLUMN birthday VARCHAR(20) COMMENT '生日';  
ALTER TABLE sys_user ADD COLUMN signature VARCHAR(500) COMMENT '个人签名';
ALTER TABLE sys_user ADD COLUMN emergency_contact VARCHAR(50) COMMENT '紧急联系人';
ALTER TABLE sys_user ADD COLUMN emergency_phone VARCHAR(20) COMMENT '紧急联系电话';

-- 显示表结构
DESCRIBE sys_user;


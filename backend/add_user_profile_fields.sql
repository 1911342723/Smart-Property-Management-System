-- 为sys_user表添加新的用户资料字段
-- 添加时间: 2024年

-- 检查字段是否已存在，如果不存在则添加
SET @sql = '';

-- 添加gender字段
SELECT COUNT(*) INTO @count FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'gender';
SET @sql = IF(@count = 0, 'ALTER TABLE sys_user ADD COLUMN gender VARCHAR(10) COMMENT "性别：MALE-男，FEMALE-女，OTHER-其他";', '');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加birthday字段
SELECT COUNT(*) INTO @count FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'birthday';
SET @sql = IF(@count = 0, 'ALTER TABLE sys_user ADD COLUMN birthday DATE COMMENT "生日";', '');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加signature字段
SELECT COUNT(*) INTO @count FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'signature';
SET @sql = IF(@count = 0, 'ALTER TABLE sys_user ADD COLUMN signature VARCHAR(500) COMMENT "个人签名";', '');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加emergency_contact字段
SELECT COUNT(*) INTO @count FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'emergency_contact';
SET @sql = IF(@count = 0, 'ALTER TABLE sys_user ADD COLUMN emergency_contact VARCHAR(50) COMMENT "紧急联系人";', '');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加emergency_phone字段
SELECT COUNT(*) INTO @count FROM information_schema.columns 
WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'emergency_phone';
SET @sql = IF(@count = 0, 'ALTER TABLE sys_user ADD COLUMN emergency_phone VARCHAR(20) COMMENT "紧急联系电话";', '');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 显示表结构
SELECT 'sys_user表结构更新完成' as message;
DESCRIBE sys_user;





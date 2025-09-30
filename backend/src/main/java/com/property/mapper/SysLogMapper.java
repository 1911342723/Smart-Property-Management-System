package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.SysLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 系统日志Mapper
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

    /**
     * 获取日志统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total, " +
            "IFNULL(SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END), 0) as successCount, " +
            "IFNULL(SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END), 0) as failureCount, " +
            "IFNULL(AVG(execution_time), 0) as avgExecutionTime " +
            "FROM sys_log WHERE deleted = 0")
    Map<String, Object> getLogStats();

    /**
     * 删除指定天数之前的日志（物理删除）
     */
    @Delete("DELETE FROM sys_log WHERE DATE(create_time) < DATE_SUB(CURDATE(), INTERVAL #{days} DAY)")
    int deleteOldLogs(@Param("days") int days);
}

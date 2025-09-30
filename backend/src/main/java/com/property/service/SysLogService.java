package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.PageResult;
import com.property.entity.SysLog;
import com.property.mapper.SysLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 系统日志服务
 */
@Service
public class SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    /**
     * 分页查询日志列表
     */
    public PageResult<SysLog> getLogList(Long pageNum, Long pageSize, String module, 
                                         String operationType, String username, Integer status) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(module)) {
            wrapper.like("module", module);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq("operation_type", operationType);
        }
        if (StringUtils.hasText(username)) {
            wrapper.like("username", username);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        
        IPage<SysLog> result = sysLogMapper.selectPage(page, wrapper);
        
        PageResult<SysLog> pageResult = new PageResult<>();
        pageResult.setList(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        
        return pageResult;
    }

    /**
     * 获取日志详情
     */
    public SysLog getLogDetail(Long id) {
        return sysLogMapper.selectById(id);
    }

    /**
     * 批量删除日志（物理删除）
     */
    public boolean batchDeleteLog(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        // 直接从数据库删除
        return sysLogMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 清空旧日志（物理删除）
     */
    public boolean clearOldLogs(int days) {
        return sysLogMapper.deleteOldLogs(days) >= 0;
    }

    /**
     * 获取日志统计
     */
    public Map<String, Object> getLogStats() {
        return sysLogMapper.getLogStats();
    }

    /**
     * 保存日志
     */
    public boolean saveLog(SysLog log) {
        return sysLogMapper.insert(log) > 0;
    }
}

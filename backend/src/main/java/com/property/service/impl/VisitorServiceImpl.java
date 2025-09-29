package com.property.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.property.dto.PageResult;
import com.property.entity.Visitor;
import com.property.mapper.VisitorMapper;
import com.property.service.VisitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访客服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Override
    public PageResult<Visitor> getVisitorPage(int pageNum, int pageSize, 
                                            Long ownerId, String status, 
                                            LocalDateTime startDate, LocalDateTime endDate) {
        Page<Visitor> page = new Page<>(pageNum, pageSize);
        IPage<Visitor> result = baseMapper.selectPageWithDetails(page, ownerId, status, startDate, endDate);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    @Override
    @Transactional
    public boolean createVisitor(Visitor visitor) {
        // 生成二维码
        if (visitor.getQrCode() == null || visitor.getQrCode().isEmpty()) {
            visitor.setQrCode(generateQrCode());
        }
        
        // 设置默认状态
        if (visitor.getStatus() == null) {
            visitor.setStatus("PENDING");
        }
        
        return this.save(visitor);
    }

    @Override
    @Transactional
    public boolean updateVisitorStatus(Long visitorId, String status, Long guardId) {
        Visitor visitor = this.getById(visitorId);
        if (visitor == null) {
            throw new RuntimeException("访客记录不存在");
        }
        
        visitor.setStatus(status);
        if (guardId != null) {
            visitor.setGuardId(guardId);
        }
        
        // 根据状态设置时间
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "ARRIVED":
                visitor.setActualArrival(now);
                break;
            case "DEPARTED":
                visitor.setActualDeparture(now);
                break;
        }
        
        return this.updateById(visitor);
    }

    @Override
    public Visitor getByQrCode(String qrCode) {
        return baseMapper.selectByQrCode(qrCode);
    }

    @Override
    @Transactional
    public boolean visitorArrival(String qrCode, Long guardId) {
        Visitor visitor = this.getByQrCode(qrCode);
        if (visitor == null) {
            throw new RuntimeException("访客二维码无效");
        }
        
        if (!"APPROVED".equals(visitor.getStatus()) && !"PENDING".equals(visitor.getStatus())) {
            throw new RuntimeException("访客状态不正确，无法签到");
        }
        
        // 检查是否在有效时间内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(visitor.getPlannedArrival().minusHours(2))) {
            throw new RuntimeException("访客到达时间过早");
        }
        
        if (visitor.getPlannedDeparture() != null && now.isAfter(visitor.getPlannedDeparture())) {
            throw new RuntimeException("访客预约已过期");
        }
        
        visitor.setStatus("ARRIVED");
        visitor.setActualArrival(now);
        visitor.setGuardId(guardId);
        
        return this.updateById(visitor);
    }

    @Override
    @Transactional
    public boolean visitorDeparture(Long visitorId, Long guardId) {
        Visitor visitor = this.getById(visitorId);
        if (visitor == null) {
            throw new RuntimeException("访客记录不存在");
        }
        
        if (!"ARRIVED".equals(visitor.getStatus())) {
            throw new RuntimeException("访客状态不正确，无法签退");
        }
        
        visitor.setStatus("DEPARTED");
        visitor.setActualDeparture(LocalDateTime.now());
        visitor.setGuardId(guardId);
        
        return this.updateById(visitor);
    }

    @Override
    public VisitorStats getVisitorStats(Long ownerId) {
        Map<String, Object> statsMap = baseMapper.getVisitorStats(ownerId);
        
        VisitorStats stats = new VisitorStats();
        stats.setTotal(((Number) statsMap.getOrDefault("total", 0)).intValue());
        stats.setToday(((Number) statsMap.getOrDefault("today", 0)).intValue());
        stats.setThisWeek(((Number) statsMap.getOrDefault("thisWeek", 0)).intValue());
        stats.setThisMonth(((Number) statsMap.getOrDefault("thisMonth", 0)).intValue());
        
        return stats;
    }

    @Override
    public List<Visitor> getTodayVisitors(Long ownerId) {
        return baseMapper.getTodayVisitors(ownerId);
    }

    @Override
    public Visitor scanVerifyVisitor(String qrContent, Long guardId) throws JsonProcessingException {
        try {
            // 解析二维码内容（JSON格式）
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode qrData = objectMapper.readTree(qrContent);
            
            String qrCode = qrData.get("qrCode").asText();
            Long visitorId = qrData.get("visitorId").asLong();
            
            // 根据二维码获取访客信息
            Visitor visitor = this.getByQrCode(qrCode);
            if (visitor == null) {
                throw new RuntimeException("无效的访客二维码");
            }
            
            // 验证访客ID是否匹配
            if (!visitor.getId().equals(visitorId)) {
                throw new RuntimeException("访客信息不匹配");
            }
            
            // 检查访客状态
            if ("EXPIRED".equals(visitor.getStatus()) || "REJECTED".equals(visitor.getStatus())) {
                throw new RuntimeException("访客通行码已失效");
            }
            
            if ("DEPARTED".equals(visitor.getStatus())) {
                throw new RuntimeException("访客已离开");
            }
            
            // 检查访问时间
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(visitor.getPlannedArrival().minusHours(2))) {
                throw new RuntimeException("访客到达时间过早");
            }
            
            if (visitor.getPlannedDeparture() != null && now.isAfter(visitor.getPlannedDeparture())) {
                // 自动设置为过期状态
                visitor.setStatus("EXPIRED");
                this.updateById(visitor);
                throw new RuntimeException("访客通行码已过期");
            }
            
            return visitor;
            
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            }
            throw new RuntimeException("二维码格式错误");
        }
    }

    @Override
    @Transactional
    public boolean checkInVisitor(Long visitorId, Long guardId) {
        Visitor visitor = this.getById(visitorId);
        if (visitor == null) {
            throw new RuntimeException("访客记录不存在");
        }
        
        if (!"PENDING".equals(visitor.getStatus()) && !"APPROVED".equals(visitor.getStatus())) {
            throw new RuntimeException("访客状态不正确，无法签到");
        }
        
        visitor.setStatus("ARRIVED");
        visitor.setActualArrival(LocalDateTime.now());
        visitor.setGuardId(guardId);
        
        return this.updateById(visitor);
    }

    @Override
    @Transactional
    public boolean checkOutVisitor(Long visitorId, Long guardId) {
        Visitor visitor = this.getById(visitorId);
        if (visitor == null) {
            throw new RuntimeException("访客记录不存在");
        }
        
        if (!"ARRIVED".equals(visitor.getStatus())) {
            throw new RuntimeException("访客未签到，无法签退");
        }
        
        visitor.setStatus("DEPARTED");
        visitor.setActualDeparture(LocalDateTime.now());
        visitor.setGuardId(guardId);
        
        return this.updateById(visitor);
    }

    /**
     * 生成访客二维码
     * 
     * @return 二维码字符串
     */
    private String generateQrCode() {
        String qrCode = baseMapper.generateQrCode();
        return qrCode != null ? qrCode : "VIS" + System.currentTimeMillis();
    }
}

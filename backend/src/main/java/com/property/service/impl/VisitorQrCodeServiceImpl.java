package com.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.VisitorQrCode;
import com.property.mapper.VisitorQrCodeMapper;
import com.property.service.VisitorQrCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 访客二维码服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class VisitorQrCodeServiceImpl extends ServiceImpl<VisitorQrCodeMapper, VisitorQrCode> implements VisitorQrCodeService {

    @Override
    @Transactional
    public VisitorQrCode createOrUpdateQrCode(VisitorQrCode qrCode) {
        // 检查该手机号是否已有有效的二维码
        VisitorQrCode existingQrCode = baseMapper.findActiveByPhone(qrCode.getPhone());
        
        if (existingQrCode != null) {
            // 更新现有二维码
            existingQrCode.setVisitorName(qrCode.getVisitorName());
            existingQrCode.setVisitPurpose(qrCode.getVisitPurpose());
            existingQrCode.setPlannedArrival(qrCode.getPlannedArrival());
            existingQrCode.setPlannedDeparture(qrCode.getPlannedDeparture());
            
            // 重新生成二维码内容
            String newQrCode = baseMapper.generateQrCode();
            existingQrCode.setQrCode(newQrCode);
            existingQrCode.setQrContent(generateQrContent(existingQrCode));
            
            this.updateById(existingQrCode);
            return existingQrCode;
        } else {
            // 创建新的二维码
            String newQrCode = baseMapper.generateQrCode();
            qrCode.setQrCode(newQrCode);
            qrCode.setQrContent(generateQrContent(qrCode));
            qrCode.setStatus("ACTIVE");
            
            this.save(qrCode);
            return qrCode;
        }
    }

    @Override
    public VisitorQrCode verifyQrCode(String qrCodeStr, Long guardId) {
        VisitorQrCode qrCode = baseMapper.findByQrCode(qrCodeStr);
        
        if (qrCode == null) {
            throw new RuntimeException("无效的二维码");
        }
        
        if (!"ACTIVE".equals(qrCode.getStatus())) {
            throw new RuntimeException("二维码已失效");
        }
        
        // 检查时间是否有效
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(qrCode.getPlannedArrival().minusHours(2))) {
            throw new RuntimeException("访客到达时间过早");
        }
        
        if (qrCode.getPlannedDeparture() != null && now.isAfter(qrCode.getPlannedDeparture())) {
            // 自动设置为过期
            qrCode.setStatus("EXPIRED");
            this.updateById(qrCode);
            throw new RuntimeException("二维码已过期");
        }
        
        return qrCode;
    }

    @Override
    @Transactional
    public boolean useQrCode(String qrCodeStr, Long guardId) {
        VisitorQrCode qrCode = baseMapper.findByQrCode(qrCodeStr);
        
        if (qrCode == null) {
            throw new RuntimeException("无效的二维码");
        }
        
        // 标记为已使用
        qrCode.setStatus("USED");
        qrCode.setUsedTime(LocalDateTime.now());
        qrCode.setGuardId(guardId);
        
        this.updateById(qrCode);
        
        // 删除已使用的二维码
        this.removeById(qrCode.getId());
        
        return true;
    }

    /**
     * 生成二维码内容（JSON格式）
     * 
     * @param qrCode 二维码信息
     * @return JSON字符串
     */
    private String generateQrContent(VisitorQrCode qrCode) {
        return String.format(
            "{\"qrCode\":\"%s\",\"visitorName\":\"%s\",\"phone\":\"%s\",\"visitPurpose\":\"%s\",\"plannedArrival\":\"%s\",\"plannedDeparture\":\"%s\",\"ownerId\":%d,\"ownerPhone\":\"%s\"}",
            qrCode.getQrCode(),
            qrCode.getVisitorName(),
            qrCode.getPhone(),
            qrCode.getVisitPurpose() != null ? qrCode.getVisitPurpose() : "",
            qrCode.getPlannedArrival().toString(),
            qrCode.getPlannedDeparture() != null ? qrCode.getPlannedDeparture().toString() : "",
            qrCode.getOwnerId(),
            qrCode.getOwnerPhone() != null ? qrCode.getOwnerPhone() : ""
        );
    }
}





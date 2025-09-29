package com.property.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.Complaint;
import com.property.mapper.ComplaintMapper;
import com.property.service.ComplaintService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 投诉建议服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    @Override
    public PageResult<Complaint> getComplaintPage(int pageNum, int pageSize, 
                                                 String complaintType, String status, 
                                                 Long complainantId, String urgencyLevel) {
        Page<Complaint> page = new Page<>(pageNum, pageSize);
        IPage<Complaint> result = baseMapper.selectPageWithDetails(page, complaintType, status, complainantId, urgencyLevel);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    @Override
    @Transactional
    public boolean submitComplaint(Complaint complaint) {
        // 生成投诉编号
        if (complaint.getComplaintNo() == null || complaint.getComplaintNo().isEmpty()) {
            complaint.setComplaintNo(generateComplaintNo());
        }
        
        // 设置默认值
        if (complaint.getStatus() == null) {
            complaint.setStatus("PENDING");
        }
        if (complaint.getUrgencyLevel() == null) {
            complaint.setUrgencyLevel("NORMAL");
        }
        
        return this.save(complaint);
    }

    @Override
    @Transactional
    public boolean assignHandler(Long complaintId, Long handlerId) {
        Complaint complaint = this.getById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        
        if (!"PENDING".equals(complaint.getStatus())) {
            throw new RuntimeException("投诉状态不正确，无法分配处理人");
        }
        
        complaint.setHandlerId(handlerId);
        complaint.setStatus("ASSIGNED");
        
        return this.updateById(complaint);
    }

    @Override
    @Transactional
    public boolean startHandling(Long complaintId, Long handlerId) {
        Complaint complaint = this.getById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        
        if (!"ASSIGNED".equals(complaint.getStatus()) && !"PENDING".equals(complaint.getStatus())) {
            throw new RuntimeException("投诉状态不正确，无法开始处理");
        }
        
        complaint.setHandlerId(handlerId);
        complaint.setStatus("PROCESSING");
        
        return this.updateById(complaint);
    }

    @Override
    @Transactional
    public boolean completeHandling(Long complaintId, Long handlerId, String handleResult) {
        Complaint complaint = this.getById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        
        if (!"PROCESSING".equals(complaint.getStatus())) {
            throw new RuntimeException("投诉状态不正确，无法完成处理");
        }
        
        complaint.setHandleResult(handleResult);
        complaint.setHandleTime(LocalDateTime.now());
        complaint.setStatus("RESOLVED");
        
        return this.updateById(complaint);
    }

    @Override
    @Transactional
    public boolean rateComplaint(Long complaintId, Long complainantId, Integer satisfactionRating, String feedback) {
        Complaint complaint = this.getById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        
        if (!complaint.getComplainantId().equals(complainantId)) {
            throw new RuntimeException("只能评价自己的投诉");
        }
        
        if (!"RESOLVED".equals(complaint.getStatus())) {
            throw new RuntimeException("投诉状态不正确，无法评价");
        }
        
        complaint.setSatisfactionRating(satisfactionRating);
        complaint.setFeedback(feedback);
        complaint.setStatus("CLOSED");
        
        return this.updateById(complaint);
    }

    @Override
    public ComplaintStats getComplaintStats(Long complainantId) {
        List<Map<String, Object>> statsList;
        
        // 根据是否传入complainantId决定调用哪个方法
        if (complainantId != null) {
            statsList = baseMapper.getComplaintStatsByComplainant(complainantId);
        } else {
            statsList = baseMapper.getAllComplaintStatsByStatus();
        }
        
        ComplaintStats stats = new ComplaintStats();
        stats.setTotal(0);
        stats.setPending(0);
        stats.setProcessing(0);
        stats.setResolved(0);
        stats.setClosed(0);
        
        for (Map<String, Object> statusStat : statsList) {
            String status = (String) statusStat.get("status");
            Long count = (Long) statusStat.get("count");
            
            stats.setTotal(stats.getTotal() + count.intValue());
            
            switch (status) {
                case "PENDING":
                case "ASSIGNED":
                    stats.setPending(stats.getPending() + count.intValue());
                    break;
                case "PROCESSING":
                    stats.setProcessing(stats.getProcessing() + count.intValue());
                    break;
                case "RESOLVED":
                    stats.setResolved(stats.getResolved() + count.intValue());
                    break;
                case "CLOSED":
                    stats.setClosed(stats.getClosed() + count.intValue());
                    break;
            }
        }
        
        return stats;
    }

    /**
     * 生成投诉编号
     * 
     * @return 投诉编号
     */
    private String generateComplaintNo() {
        String complaintNo = baseMapper.generateComplaintNo();
        return complaintNo != null ? complaintNo : "CP" + System.currentTimeMillis();
    }
}







package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageResult;
import com.property.entity.Complaint;

import java.util.Map;

/**
 * 投诉建议服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface ComplaintService extends IService<Complaint> {

    /**
     * 分页查询投诉建议
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param complaintType 投诉类型
     * @param status 状态
     * @param complainantId 投诉人ID
     * @param urgencyLevel 紧急程度
     * @return 分页结果
     */
    PageResult<Complaint> getComplaintPage(int pageNum, int pageSize, 
                                          String complaintType, String status, 
                                          Long complainantId, String urgencyLevel);

    /**
     * 提交投诉建议
     * 
     * @param complaint 投诉建议信息
     * @return 是否成功
     */
    boolean submitComplaint(Complaint complaint);

    /**
     * 分配处理人
     * 
     * @param complaintId 投诉ID
     * @param handlerId 处理人ID
     * @return 是否成功
     */
    boolean assignHandler(Long complaintId, Long handlerId);

    /**
     * 开始处理投诉
     * 
     * @param complaintId 投诉ID
     * @param handlerId 处理人ID
     * @return 是否成功
     */
    boolean startHandling(Long complaintId, Long handlerId);

    /**
     * 完成投诉处理
     * 
     * @param complaintId 投诉ID
     * @param handlerId 处理人ID
     * @param handleResult 处理结果
     * @return 是否成功
     */
    boolean completeHandling(Long complaintId, Long handlerId, String handleResult);

    /**
     * 评价投诉处理
     * 
     * @param complaintId 投诉ID
     * @param complainantId 投诉人ID
     * @param satisfactionRating 满意度评分
     * @param feedback 评价反馈
     * @return 是否成功
     */
    boolean rateComplaint(Long complaintId, Long complainantId, Integer satisfactionRating, String feedback);

    /**
     * 获取投诉统计信息
     * 
     * @param complainantId 投诉人ID（可选）
     * @return 统计信息
     */
    ComplaintStats getComplaintStats(Long complainantId);

    /**
     * 投诉统计信息
     */
    class ComplaintStats {
        private Integer total;         // 总数
        private Integer pending;       // 待处理
        private Integer processing;    // 处理中
        private Integer resolved;      // 已解决
        private Integer closed;        // 已关闭

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getPending() {
            return pending;
        }

        public void setPending(Integer pending) {
            this.pending = pending;
        }

        public Integer getProcessing() {
            return processing;
        }

        public void setProcessing(Integer processing) {
            this.processing = processing;
        }

        public Integer getResolved() {
            return resolved;
        }

        public void setResolved(Integer resolved) {
            this.resolved = resolved;
        }

        public Integer getClosed() {
            return closed;
        }

        public void setClosed(Integer closed) {
            this.closed = closed;
        }
    }
}







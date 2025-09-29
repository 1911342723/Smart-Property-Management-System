package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.property.dto.PageResult;
import com.property.entity.Visitor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 访客服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface VisitorService extends IService<Visitor> {

    /**
     * 分页查询访客信息
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param ownerId 业主ID
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    PageResult<Visitor> getVisitorPage(int pageNum, int pageSize, 
                                     Long ownerId, String status, 
                                     LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 创建访客预约
     * 
     * @param visitor 访客信息
     * @return 是否成功
     */
    boolean createVisitor(Visitor visitor);

    /**
     * 更新访客状态
     * 
     * @param visitorId 访客ID
     * @param status 状态
     * @param guardId 保安ID（可选）
     * @return 是否成功
     */
    boolean updateVisitorStatus(Long visitorId, String status, Long guardId);

    /**
     * 根据二维码获取访客信息
     * 
     * @param qrCode 二维码
     * @return 访客信息
     */
    Visitor getByQrCode(String qrCode);

    /**
     * 访客到达
     * 
     * @param qrCode 二维码
     * @param guardId 保安ID
     * @return 是否成功
     */
    boolean visitorArrival(String qrCode, Long guardId);

    /**
     * 访客离开
     * 
     * @param visitorId 访客ID
     * @param guardId 保安ID
     * @return 是否成功
     */
    boolean visitorDeparture(Long visitorId, Long guardId);

    /**
     * 获取访客统计信息
     * 
     * @param ownerId 业主ID
     * @return 统计信息
     */
    VisitorStats getVisitorStats(Long ownerId);

    /**
     * 获取今日访客列表
     * 
     * @param ownerId 业主ID
     * @return 今日访客列表
     */
    List<Visitor> getTodayVisitors(Long ownerId);

    /**
     * 扫码验证访客
     * 
     * @param qrContent 二维码内容
     * @param guardId 保安ID
     * @return 访客信息
     */
    Visitor scanVerifyVisitor(String qrContent, Long guardId) throws JsonProcessingException;

    /**
     * 访客签到
     * 
     * @param visitorId 访客ID
     * @param guardId 保安ID
     * @return 是否成功
     */
    boolean checkInVisitor(Long visitorId, Long guardId);

    /**
     * 访客签退
     * 
     * @param visitorId 访客ID
     * @param guardId 保安ID
     * @return 是否成功
     */
    boolean checkOutVisitor(Long visitorId, Long guardId);

    /**
     * 访客统计信息
     */
    class VisitorStats {
        private Integer total;      // 总数
        private Integer today;      // 今日
        private Integer thisWeek;   // 本周
        private Integer thisMonth;  // 本月

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getToday() {
            return today;
        }

        public void setToday(Integer today) {
            this.today = today;
        }

        public Integer getThisWeek() {
            return thisWeek;
        }

        public void setThisWeek(Integer thisWeek) {
            this.thisWeek = thisWeek;
        }

        public Integer getThisMonth() {
            return thisMonth;
        }

        public void setThisMonth(Integer thisMonth) {
            this.thisMonth = thisMonth;
        }
    }
}

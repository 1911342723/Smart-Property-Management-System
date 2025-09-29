package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageResult;
import com.property.entity.Bill;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账单服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface BillService extends IService<Bill> {

    /**
     * 分页查询账单
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param billType 账单类型
     * @param status 账单状态
     * @param ownerId 业主ID
     * @param billingPeriod 计费周期
     * @return 分页结果
     */
    PageResult<Bill> getBillPage(int pageNum, int pageSize, 
                                String billType, String status, 
                                Long ownerId, String billingPeriod);

    /**
     * 创建账单
     * 
     * @param bill 账单信息
     * @return 是否成功
     */
    boolean createBill(Bill bill);

    /**
     * 批量创建账单
     * 
     * @param bills 账单列表
     * @return 是否成功
     */
    boolean createBillsBatch(List<Bill> bills);

    /**
     * 缴费
     * 
     * @param billId 账单ID
     * @param paymentAmount 缴费金额
     * @param paymentMethod 支付方式
     * @return 是否成功
     */
    boolean payBill(Long billId, BigDecimal paymentAmount, String paymentMethod);

    /**
     * 批量缴费
     * 
     * @param billIds 账单ID列表
     * @param paymentMethod 支付方式
     * @return 是否成功
     */
    boolean payBillsBatch(List<Long> billIds, String paymentMethod);

    /**
     * 获取业主待缴费用统计
     * 
     * @param ownerId 业主ID
     * @return 统计信息
     */
    BillSummary getBillSummary(Long ownerId);

    /**
     * 账单统计信息
     */
    class BillSummary {
        private BigDecimal totalAmount;      // 总待缴金额
        private Integer unpaidCount;         // 待缴费用数量
        private Integer overdueCount;        // 逾期费用数量
        private String nearestDueDate;       // 最近到期日期

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getUnpaidCount() {
            return unpaidCount;
        }

        public void setUnpaidCount(Integer unpaidCount) {
            this.unpaidCount = unpaidCount;
        }

        public Integer getOverdueCount() {
            return overdueCount;
        }

        public void setOverdueCount(Integer overdueCount) {
            this.overdueCount = overdueCount;
        }

        public String getNearestDueDate() {
            return nearestDueDate;
        }

        public void setNearestDueDate(String nearestDueDate) {
            this.nearestDueDate = nearestDueDate;
        }
    }
}







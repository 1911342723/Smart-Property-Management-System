package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageResult;
import com.property.entity.Bill;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * 获取账单统计（用于仪表盘）
     * 
     * @param ownerId 业主ID（可选）
     * @return 统计信息
     */
    BillStats getBillStats(Long ownerId);

    /**
     * 获取业主待缴费用统计
     * 
     * @param ownerId 业主ID
     * @return 统计信息
     */
    BillSummary getBillSummary(Long ownerId);

    /**
     * 导出账单列表
     * 
     * @param billType 账单类型
     * @param status 账单状态
     * @param ownerId 业主ID
     * @param billingPeriod 计费周期
     * @param response HTTP响应
     */
    void exportBillList(String billType, String status, Long ownerId, String billingPeriod, HttpServletResponse response) throws IOException;

    /**
     * 导出财务报表
     * 
     * @param reportType 报表类型
     * @param timeRange 时间范围
     * @param building 楼栋
     * @param response HTTP响应
     */
    void exportFinanceReport(String reportType, String timeRange, String building, HttpServletResponse response) throws IOException;

    /**
     * 获取楼栋收费统计
     * 
     * @param billingPeriod 计费周期
     * @return 楼栋统计列表
     */
    List<BuildingStats> getBuildingStats(String billingPeriod);

    /**
     * 账单统计信息（用于仪表盘）
     */
    class BillStats {
        private double paymentRate;       // 缴费率
        private double paymentTrend;      // 缴费率趋势
        private int totalBills;           // 总账单数
        private int paidBills;            // 已缴费账单数
        private BigDecimal totalAmount;   // 总金额
        private BigDecimal paidAmount;    // 已缴金额
        
        // Getters and Setters
        public double getPaymentRate() { return paymentRate; }
        public void setPaymentRate(double paymentRate) { this.paymentRate = paymentRate; }
        
        public double getPaymentTrend() { return paymentTrend; }
        public void setPaymentTrend(double paymentTrend) { this.paymentTrend = paymentTrend; }
        
        public int getTotalBills() { return totalBills; }
        public void setTotalBills(int totalBills) { this.totalBills = totalBills; }
        
        public int getPaidBills() { return paidBills; }
        public void setPaidBills(int paidBills) { this.paidBills = paidBills; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    }

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

    /**
     * 楼栋收费统计
     */
    class BuildingStats {
        private String building;              // 楼栋名称
        private Long buildingId;              // 楼栋ID
        private Integer totalHouses;          // 总房屋数
        private Integer paidHouses;           // 已缴费房屋数
        private Integer unpaidHouses;         // 未缴费房屋数
        private Double paymentRate;           // 缴费率
        private BigDecimal totalAmount;       // 应收总额
        private BigDecimal paidAmount;        // 已收金额
        private BigDecimal unpaidAmount;      // 未收金额

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public Long getBuildingId() {
            return buildingId;
        }

        public void setBuildingId(Long buildingId) {
            this.buildingId = buildingId;
        }

        public Integer getTotalHouses() {
            return totalHouses;
        }

        public void setTotalHouses(Integer totalHouses) {
            this.totalHouses = totalHouses;
        }

        public Integer getPaidHouses() {
            return paidHouses;
        }

        public void setPaidHouses(Integer paidHouses) {
            this.paidHouses = paidHouses;
        }

        public Integer getUnpaidHouses() {
            return unpaidHouses;
        }

        public void setUnpaidHouses(Integer unpaidHouses) {
            this.unpaidHouses = unpaidHouses;
        }

        public Double getPaymentRate() {
            return paymentRate;
        }

        public void setPaymentRate(Double paymentRate) {
            this.paymentRate = paymentRate;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
        }

        public BigDecimal getUnpaidAmount() {
            return unpaidAmount;
        }

        public void setUnpaidAmount(BigDecimal unpaidAmount) {
            this.unpaidAmount = unpaidAmount;
        }
    }
}







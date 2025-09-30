package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.Bill;
import com.property.entity.Payment;
import com.property.mapper.BillMapper;
import com.property.service.BillService;
import com.property.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 账单服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Autowired
    private PaymentService paymentService;

    @Override
    public PageResult<Bill> getBillPage(int pageNum, int pageSize, 
                                       String billType, String status, 
                                       Long ownerId, String billingPeriod) {
        Page<Bill> page = new Page<>(pageNum, pageSize);
        IPage<Bill> result = baseMapper.selectPageWithDetails(page, billType, status, ownerId, billingPeriod);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    @Override
    @Transactional
    public boolean createBill(Bill bill) {
        // 生成账单编号
        if (bill.getBillNo() == null || bill.getBillNo().isEmpty()) {
            bill.setBillNo(generateBillNo());
        }
        
        // 设置默认值
        if (bill.getStatus() == null) {
            bill.setStatus("UNPAID");
        }
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        
        return this.save(bill);
    }

    @Override
    @Transactional
    public boolean createBillsBatch(List<Bill> bills) {
        for (Bill bill : bills) {
            // 生成账单编号
            if (bill.getBillNo() == null || bill.getBillNo().isEmpty()) {
                bill.setBillNo(generateBillNo());
            }
            
            // 设置默认值
            if (bill.getStatus() == null) {
                bill.setStatus("UNPAID");
            }
            if (bill.getPaidAmount() == null) {
                bill.setPaidAmount(BigDecimal.ZERO);
            }
        }
        
        return this.saveBatch(bills);
    }

    @Override
    @Transactional
    public boolean payBill(Long billId, BigDecimal paymentAmount, String paymentMethod) {
        Bill bill = this.getById(billId);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        
        if (!"UNPAID".equals(bill.getStatus()) && !"OVERDUE".equals(bill.getStatus())) {
            throw new RuntimeException("账单状态不正确，无法缴费");
        }
        
        // 更新账单状态
        bill.setPaidAmount(paymentAmount);
        bill.setStatus("PAID");
        bill.setPaidDate(LocalDateTime.now());
        
        boolean billUpdated = this.updateById(bill);
        
        if (billUpdated) {
            // 创建支付记录
            Payment payment = new Payment();
            payment.setBillId(billId);
            payment.setOwnerId(bill.getOwnerId());
            payment.setAmount(paymentAmount);
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus("SUCCESS");
            payment.setPaymentTime(LocalDateTime.now());
            payment.setRemark("账单缴费");
            
            paymentService.createPayment(payment);
        }
        
        return billUpdated;
    }

    @Override
    @Transactional
    public boolean payBillsBatch(List<Long> billIds, String paymentMethod) {
        for (Long billId : billIds) {
            Bill bill = this.getById(billId);
            if (bill == null) {
                continue;
            }
            
            if (!"UNPAID".equals(bill.getStatus()) && !"OVERDUE".equals(bill.getStatus())) {
                continue;
            }
            
            // 更新账单状态
            bill.setPaidAmount(bill.getAmount());
            bill.setStatus("PAID");
            bill.setPaidDate(LocalDateTime.now());
            
            boolean billUpdated = this.updateById(bill);
            
            if (billUpdated) {
                // 创建支付记录
                Payment payment = new Payment();
                payment.setBillId(billId);
                payment.setOwnerId(bill.getOwnerId());
                payment.setAmount(bill.getAmount());
                payment.setPaymentMethod(paymentMethod);
                payment.setStatus("SUCCESS");
                payment.setPaymentTime(LocalDateTime.now());
                payment.setRemark("批量账单缴费");
                
                paymentService.createPayment(payment);
            }
        }
        
        return true;
    }

    @Override
    public BillStats getBillStats(Long ownerId) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        if (ownerId != null) {
            queryWrapper.eq("owner_id", ownerId);
        }
        queryWrapper.eq("deleted", 0);
        
        List<Bill> allBills = this.list(queryWrapper);
        
        BillStats stats = new BillStats();
        
        if (allBills.isEmpty()) {
            stats.setPaymentRate(0);
            stats.setPaymentTrend(0);
            stats.setTotalBills(0);
            stats.setPaidBills(0);
            stats.setTotalAmount(BigDecimal.ZERO);
            stats.setPaidAmount(BigDecimal.ZERO);
            return stats;
        }
        
        // 统计总账单数
        stats.setTotalBills(allBills.size());
        
        // 统计已缴费账单数
        int paidCount = (int) allBills.stream()
                .filter(bill -> "PAID".equals(bill.getStatus()))
                .count();
        stats.setPaidBills(paidCount);
        
        // 计算缴费率
        double paymentRate = (stats.getTotalBills() > 0) ? 
                (paidCount * 100.0 / stats.getTotalBills()) : 0;
        stats.setPaymentRate(Math.round(paymentRate * 10) / 10.0);
        
        // 模拟趋势（实际应该对比上个月数据）
        stats.setPaymentTrend(0);
        
        // 计算总金额
        BigDecimal totalAmount = allBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount);
        
        // 计算已缴金额
        BigDecimal paidAmount = allBills.stream()
                .filter(bill -> "PAID".equals(bill.getStatus()))
                .map(Bill::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setPaidAmount(paidAmount);
        
        return stats;
    }

    @Override
    public BillSummary getBillSummary(Long ownerId) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", ownerId)
                   .eq("status", "UNPAID")
                   .eq("deleted", 0);
        
        List<Bill> unpaidBills = this.list(queryWrapper);
        
        BillSummary summary = new BillSummary();
        
        if (unpaidBills.isEmpty()) {
            summary.setTotalAmount(BigDecimal.ZERO);
            summary.setUnpaidCount(0);
            summary.setOverdueCount(0);
            summary.setNearestDueDate(null);
            return summary;
        }
        
        // 计算总金额
        BigDecimal totalAmount = unpaidBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalAmount(totalAmount);
        
        // 计算待缴费用数量
        summary.setUnpaidCount(unpaidBills.size());
        
        // 计算逾期费用数量
        LocalDate today = LocalDate.now();
        int overdueCount = (int) unpaidBills.stream()
                .filter(bill -> bill.getDueDate() != null && bill.getDueDate().isBefore(today))
                .count();
        summary.setOverdueCount(overdueCount);
        
        // 获取最近到期日期
        unpaidBills.stream()
                .filter(bill -> bill.getDueDate() != null)
                .min((b1, b2) -> b1.getDueDate().compareTo(b2.getDueDate()))
                .ifPresent(bill -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
                    summary.setNearestDueDate(bill.getDueDate().format(formatter));
                });
        
        return summary;
    }

    /**
     * 生成账单编号
     * 
     * @return 账单编号
     */
    private String generateBillNo() {
        String billNo = baseMapper.generateBillNo();
        return billNo != null ? billNo : "BILL" + System.currentTimeMillis();
    }
}

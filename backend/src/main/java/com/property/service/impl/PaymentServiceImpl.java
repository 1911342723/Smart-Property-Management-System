package com.property.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.Payment;
import com.property.mapper.PaymentMapper;
import com.property.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 支付记录服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Override
    public PageResult<Payment> getPaymentPage(int pageNum, int pageSize, 
                                             Long ownerId, String paymentMethod, String status) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        IPage<Payment> result = baseMapper.selectPageWithDetails(page, ownerId, paymentMethod, status);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    @Override
    @Transactional
    public boolean createPayment(Payment payment) {
        // 生成支付编号
        if (payment.getPaymentNo() == null || payment.getPaymentNo().isEmpty()) {
            payment.setPaymentNo(generatePaymentNo());
        }
        
        // 设置默认值
        if (payment.getStatus() == null) {
            payment.setStatus("SUCCESS");
        }
        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        
        return this.save(payment);
    }

    /**
     * 生成支付编号
     * 
     * @return 支付编号
     */
    private String generatePaymentNo() {
        String paymentNo = baseMapper.generatePaymentNo();
        return paymentNo != null ? paymentNo : "PAY" + System.currentTimeMillis();
    }
}







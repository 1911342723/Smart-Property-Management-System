package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageResult;
import com.property.entity.Payment;

/**
 * 支付记录服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 分页查询支付记录
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param ownerId 业主ID
     * @param paymentMethod 支付方式
     * @param status 支付状态
     * @return 分页结果
     */
    PageResult<Payment> getPaymentPage(int pageNum, int pageSize, 
                                      Long ownerId, String paymentMethod, String status);

    /**
     * 创建支付记录
     * 
     * @param payment 支付记录信息
     * @return 是否成功
     */
    boolean createPayment(Payment payment);
}







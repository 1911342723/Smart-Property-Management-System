package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付记录Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    /**
     * 分页查询支付记录（带关联信息）
     * 
     * @param page 分页对象
     * @param ownerId 业主ID
     * @param paymentMethod 支付方式
     * @param status 支付状态
     * @return 支付记录列表
     */
    @Select({
        "<script>",
        "SELECT p.*, ",
        "b.bill_no, b.bill_type, b.billing_period, ",
        "u.real_name as owner_name, ",
        "CONCAT(bd.building_name, '-', un.unit_name, '-', r.room_no) as room_address ",
        "FROM payment p ",
        "LEFT JOIN bill b ON p.bill_id = b.id ",
        "LEFT JOIN sys_user u ON p.owner_id = u.id ",
        "LEFT JOIN room r ON b.room_id = r.id ",
        "LEFT JOIN unit un ON r.unit_id = un.id ",
        "LEFT JOIN building bd ON r.building_id = bd.id ",
        "WHERE p.deleted = 0 ",
        "<if test='ownerId != null'>",
        "AND p.owner_id = #{ownerId} ",
        "</if>",
        "<if test='paymentMethod != null and paymentMethod != \"\"'>",
        "AND p.payment_method = #{paymentMethod} ",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND p.status = #{status} ",
        "</if>",
        "ORDER BY p.payment_time DESC",
        "</script>"
    })
    IPage<Payment> selectPageWithDetails(Page<Payment> page,
                                        @Param("ownerId") Long ownerId,
                                        @Param("paymentMethod") String paymentMethod,
                                        @Param("status") String status);

    /**
     * 生成支付编号
     * 
     * @return 支付编号
     */
    @Select("SELECT CONCAT('PAY', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(IFNULL(MAX(SUBSTRING(payment_no, 12)), 0) + 1, 4, '0')) " +
            "FROM payment WHERE payment_no LIKE CONCAT('PAY', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generatePaymentNo();
}

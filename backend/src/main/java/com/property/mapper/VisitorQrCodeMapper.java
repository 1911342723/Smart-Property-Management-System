package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.VisitorQrCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 访客二维码Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface VisitorQrCodeMapper extends BaseMapper<VisitorQrCode> {

    /**
     * 根据手机号查找有效的二维码
     * 
     * @param phone 手机号
     * @return 二维码信息
     */
    @Select("SELECT * FROM visitor_qr_code WHERE phone = #{phone} AND status = 'ACTIVE' ORDER BY create_time DESC LIMIT 1")
    VisitorQrCode findActiveByPhone(@Param("phone") String phone);

    /**
     * 根据二维码查找
     * 
     * @param qrCode 二维码
     * @return 二维码信息
     */
    @Select("SELECT * FROM visitor_qr_code WHERE qr_code = #{qrCode} AND status = 'ACTIVE'")
    VisitorQrCode findByQrCode(@Param("qrCode") String qrCode);

    /**
     * 生成二维码唯一标识
     * 
     * @return 二维码字符串
     */
    @Select("SELECT CONCAT('QR', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(FLOOR(RAND() * 1000), 3, '0'))")
    String generateQrCode();
}





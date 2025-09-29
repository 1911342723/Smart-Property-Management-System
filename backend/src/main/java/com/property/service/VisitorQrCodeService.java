package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.VisitorQrCode;

/**
 * 访客二维码服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface VisitorQrCodeService extends IService<VisitorQrCode> {

    /**
     * 创建或更新访客二维码
     * 
     * @param qrCode 二维码信息
     * @return 二维码信息
     */
    VisitorQrCode createOrUpdateQrCode(VisitorQrCode qrCode);

    /**
     * 验证二维码
     * 
     * @param qrCodeStr 二维码字符串
     * @param guardId 保安ID
     * @return 二维码信息
     */
    VisitorQrCode verifyQrCode(String qrCodeStr, Long guardId);

    /**
     * 使用二维码（标记为已使用并删除）
     * 
     * @param qrCodeStr 二维码字符串
     * @param guardId 保安ID
     * @return 是否成功
     */
    boolean useQrCode(String qrCodeStr, Long guardId);
}





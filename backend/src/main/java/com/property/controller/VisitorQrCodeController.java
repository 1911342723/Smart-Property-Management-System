package com.property.controller;

import com.property.dto.Result;
import com.property.entity.VisitorQrCode;
import com.property.service.VisitorQrCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 访客二维码控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "访客二维码管理")
@RestController
@RequestMapping("/visitor-qr")
public class VisitorQrCodeController {

    @Autowired
    private VisitorQrCodeService visitorQrCodeService;

    /**
     * 创建访客二维码
     */
    @ApiOperation("创建访客二维码")
    @PostMapping("/create")
    @PreAuthorize("hasRole('OWNER')")
    public Result<VisitorQrCode> createVisitorQrCode(@ApiParam("访客信息") @Valid @RequestBody VisitorQrCode qrCode) {
        try {
            VisitorQrCode result = visitorQrCodeService.createOrUpdateQrCode(qrCode);
            return Result.success("二维码创建成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证二维码
     */
    @ApiOperation("验证二维码")
    @PostMapping("/verify")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<VisitorQrCode> verifyQrCode(@ApiParam("验证请求") @RequestBody VerifyRequest request) {
        try {
            VisitorQrCode qrCode = visitorQrCodeService.verifyQrCode(request.getQrCode(), request.getGuardId());
            return Result.success("验证成功", qrCode);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 使用二维码（签到）
     */
    @ApiOperation("使用二维码")
    @PostMapping("/use")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public Result<String> useQrCode(@ApiParam("使用请求") @RequestBody VerifyRequest request) {
        try {
            boolean success = visitorQrCodeService.useQrCode(request.getQrCode(), request.getGuardId());
            return success ? Result.success("签到成功") : Result.error("签到失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证请求
     */
    public static class VerifyRequest {
        private String qrCode;
        private Long guardId;

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public Long getGuardId() {
            return guardId;
        }

        public void setGuardId(Long guardId) {
            this.guardId = guardId;
        }
    }
}





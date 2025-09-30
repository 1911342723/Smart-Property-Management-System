package com.property.controller;

import com.property.dto.LoginDTO;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.dto.UserInfoDTO;
import com.property.entity.SysUser;
import com.property.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "用户认证")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserInfoDTO> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            UserInfoDTO userInfo = authService.login(loginDTO);
            return Result.success("登录成功", userInfo);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody SysUser user) {
        try {
            authService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<UserInfoDTO> getCurrentUser() {
        try {
            UserInfoDTO userInfo = authService.getCurrentUser();
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    @ApiOperation("刷新令牌")
    @PostMapping("/refresh")
    public Result<String> refreshToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("无效的令牌格式");
            }
            
            String token = authHeader.substring(7);
            String newToken = authService.refreshToken(token);
            return Result.success("令牌刷新成功", newToken);
        } catch (Exception e) {
            return Result.error("令牌刷新失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，客户端删除token即可
        return Result.success("登出成功");
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<SysUser>> getUserList(
            @ApiParam("角色类型") @RequestParam(required = false) String role,
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        try {
            PageResult<SysUser> result = authService.getUserList(role, pageNum, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("添加用户")
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createUser(@Validated @RequestBody SysUser user) {
        try {
            authService.register(user);
            return Result.success("创建用户成功");
        } catch (Exception e) {
            return Result.error("创建用户失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新用户")
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateUser(
            @ApiParam("用户ID") @PathVariable Long id,
            @Validated @RequestBody SysUser user) {
        try {
            user.setId(id);
            boolean success = authService.updateUser(user);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新用户失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteUser(@ApiParam("用户ID") @PathVariable Long id) {
        try {
            boolean success = authService.deleteUser(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    @ApiOperation("重置用户密码")
    @PostMapping("/users/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> resetPassword(
            @ApiParam("用户ID") @PathVariable Long id,
            @RequestBody ResetPasswordRequest request) {
        try {
            boolean success = authService.resetPassword(id, request.getNewPassword());
            return success ? Result.success("密码重置成功") : Result.error("密码重置失败");
        } catch (Exception e) {
            return Result.error("密码重置失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新当前用户资料")
    @PutMapping("/profile")
    public Result<UserInfoDTO> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            UserInfoDTO userInfo = authService.updateCurrentUserProfile(request);
            return Result.success("更新成功", userInfo);
        } catch (Exception e) {
            return Result.error("更新资料失败：" + e.getMessage());
        }
    }

    /**
     * 密码重置请求
     */
    public static class ResetPasswordRequest {
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * 更新资料请求
     */
    public static class UpdateProfileRequest {
        private String realName;
        private String phone;
        private String email;
        private String avatar;
        private String gender;
        private String birthday;
        private String signature;
        private String emergencyContact;
        private String emergencyPhone;
        private String workStatus;

        // Getters and Setters
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getBirthday() { return birthday; }
        public void setBirthday(String birthday) { this.birthday = birthday; }
        
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
        
        public String getEmergencyPhone() { return emergencyPhone; }
        public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
        
        public String getWorkStatus() { return workStatus; }
        public void setWorkStatus(String workStatus) { this.workStatus = workStatus; }
    }
}




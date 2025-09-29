package com.property.controller;

import com.property.dto.LoginDTO;
import com.property.dto.Result;
import com.property.dto.UserInfoDTO;
import com.property.dto.UserStatsDTO;
import com.property.entity.SysUser;
import com.property.service.AuthService;
import com.property.service.UserStatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserStatsService userStatsService;

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

    @ApiOperation("获取当前用户统计数据")
    @GetMapping("/stats")
    public Result<UserStatsDTO> getCurrentUserStats() {
        try {
            UserStatsDTO stats = userStatsService.getCurrentUserStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody SysUser updateData) {
        try {
            // 获取当前用户信息
            UserInfoDTO currentUser = authService.getCurrentUser();
            boolean success = authService.updateUserProfile(currentUser.getId(), updateData);
            
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}




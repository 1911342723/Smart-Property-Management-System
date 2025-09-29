package com.property.service;

import com.property.dto.LoginDTO;
import com.property.dto.UserInfoDTO;
import com.property.entity.SysUser;
import com.property.mapper.SysUserMapper;
import com.property.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 认证服务类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // @Autowired
    // private PasswordEncoder passwordEncoder; // 不再需要，使用明文密码

    /**
     * 用户登录
     * 
     * @param loginDTO 登录信息
     * @return 用户信息和令牌
     */
    public UserInfoDTO login(LoginDTO loginDTO) {
        System.out.println("=== 登录调试信息 ===");
        System.out.println("输入的手机号: " + loginDTO.getUsername());
        System.out.println("输入的密码: " + loginDTO.getPassword());
        
        // 通过手机号获取用户信息
        SysUser user = userMapper.selectByPhone(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        System.out.println("数据库中的用户名: " + user.getUsername());
        System.out.println("数据库中的密码: " + user.getPassword());
        
        // 直接比较明文密码
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码不正确");
        }
        
        // 直接使用手机号登录，验证用户名和密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌 - 使用手机号作为subject，这样JWT过滤器可以正确找到用户
        String token = jwtUtils.generateToken(user.getId(), user.getPhone(), user.getUserType());

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 构建返回信息
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserType(user.getUserType());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        userInfo.setToken(token);
        
        // 设置角色权限（简化处理）
        userInfo.setRoles(Arrays.asList("ROLE_" + user.getUserType()));
        
        return userInfo;
    }

    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 注册结果
     */
    public boolean register(SysUser user) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (userMapper.selectByPhone(user.getPhone()) != null) {
            throw new RuntimeException("手机号已存在");
        }

        // 直接使用明文密码（开发环境）
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }
        if (user.getUserType() == null) {
            user.setUserType("OWNER"); // 默认为业主
        }

        return userMapper.insert(user) > 0;
    }

    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    public UserInfoDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        String username = authentication.getName();
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserType(user.getUserType());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        userInfo.setRoles(Arrays.asList("ROLE_" + user.getUserType()));

        return userInfo;
    }

    /**
     * 刷新令牌
     * 
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        if (jwtUtils.isTokenExpired(token)) {
            throw new RuntimeException("令牌已过期");
        }
        return jwtUtils.refreshToken(token);
    }

    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param updateData 更新数据
     * @return 更新结果
     */
    public boolean updateUserProfile(Long userId, SysUser updateData) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 只更新允许的字段
        if (updateData.getRealName() != null) {
            user.setRealName(updateData.getRealName());
        }
        if (updateData.getPhone() != null) {
            user.setPhone(updateData.getPhone());
        }
        if (updateData.getEmail() != null) {
            user.setEmail(updateData.getEmail());
        }
        if (updateData.getAvatar() != null) {
            user.setAvatar(updateData.getAvatar());
        }

        return userMapper.updateById(user) > 0;
    }
}


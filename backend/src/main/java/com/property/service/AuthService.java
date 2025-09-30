package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.LoginDTO;
import com.property.dto.PageResult;
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
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456"); // 设置默认密码
        }
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
        
        // 先尝试用username查询
        SysUser user = userMapper.selectByUsername(username);
        
        // 如果找不到，尝试用phone查询（兼容手机号登录）
        if (user == null) {
            user = userMapper.selectByPhone(username);
        }
        
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
     * 获取用户列表
     * 
     * @param role 角色类型
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @return 用户列表
     */
    public PageResult<SysUser> getUserList(String role, int pageNum, int pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        
        // 排除已删除的用户
        queryWrapper.eq("deleted", 0);
        
        // 角色筛选
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq("user_type", role);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("username", keyword)
                .or().like("real_name", keyword)
                .or().like("phone", keyword)
                .or().like("email", keyword)
            );
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        IPage<SysUser> result = userMapper.selectPage(page, queryWrapper);
        
        return new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            Long.valueOf(pageNum),
            Long.valueOf(pageSize)
        );
    }

    /**
     * 更新用户
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    public boolean updateUser(SysUser user) {
        SysUser existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        return userMapper.updateById(user) > 0;
    }

    /**
     * 删除用户（逻辑删除）
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean deleteUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 使用MyBatis-Plus的逻辑删除，会自动将deleted设置为1
        return userMapper.deleteById(userId) > 0;
    }

    /**
     * 重置密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 设置新密码（明文）
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());
        
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 通过手机号重置密码（忘记密码功能）
     * 
     * @param phone 手机号
     * @param newPassword 新密码
     * @return 是否成功
     */
    public boolean resetPasswordByPhone(String phone, String newPassword) {
        // 验证手机号格式
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        
        // 验证密码
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }
        
        // 查找用户
        SysUser user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("该手机号未注册");
        }
        
        // 设置新密码（明文）
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());
        
        return userMapper.updateById(user) > 0;
    }

    /**
     * 更新当前用户资料
     * 
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    public UserInfoDTO updateCurrentUserProfile(com.property.controller.AuthController.UpdateProfileRequest request) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
        String phone = authentication.getName();
        SysUser user = userMapper.selectByPhone(phone);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        if (request.getRealName() != null && !request.getRealName().isEmpty()) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            // 检查手机号是否已被其他用户使用
            SysUser existingUser = userMapper.selectByPhone(request.getPhone());
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                throw new RuntimeException("手机号已被使用");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getSignature() != null) {
            user.setSignature(request.getSignature());
        }
        if (request.getEmergencyContact() != null) {
            user.setEmergencyContact(request.getEmergencyContact());
        }
        if (request.getEmergencyPhone() != null) {
            user.setEmergencyPhone(request.getEmergencyPhone());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        int result = userMapper.updateById(user);
        if (result == 0) {
            throw new RuntimeException("更新失败");
        }
        
        // 返回更新后的用户信息
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserType(user.getUserType());
        userInfo.setGender(user.getGender());
        userInfo.setBirthday(user.getBirthday());
        userInfo.setSignature(user.getSignature());
        userInfo.setEmergencyContact(user.getEmergencyContact());
        userInfo.setEmergencyPhone(user.getEmergencyPhone());
        
        return userInfo;
    }
}


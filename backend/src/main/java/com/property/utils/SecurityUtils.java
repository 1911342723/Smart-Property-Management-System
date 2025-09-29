package com.property.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.property.entity.SysUser;
import com.property.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Component
public class SecurityUtils {

    private static SysUserMapper sysUserMapper;

    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        SecurityUtils.sysUserMapper = sysUserMapper;
    }

    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                
                // 先尝试按用户名查找
                SysUser user = sysUserMapper.selectOne(
                    new QueryWrapper<SysUser>().eq("username", username).eq("deleted", 0)
                );
                
                // 如果按用户名找不到，尝试按手机号查找（兼容手机号登录）
                if (user == null) {
                    user = sysUserMapper.selectOne(
                        new QueryWrapper<SysUser>().eq("phone", username).eq("deleted", 0)
                    );
                }
                
                return user != null ? user.getId() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息，未登录返回null
     */
    public static SysUser getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                
                // 先尝试按用户名查找
                SysUser user = sysUserMapper.selectOne(
                    new QueryWrapper<SysUser>().eq("username", username).eq("deleted", 0)
                );
                
                // 如果按用户名找不到，尝试按手机号查找
                if (user == null) {
                    user = sysUserMapper.selectOne(
                        new QueryWrapper<SysUser>().eq("phone", username).eq("deleted", 0)
                    );
                }
                
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     * 
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 检查当前用户是否已登录
     * 
     * @return true-已登录，false-未登录
     */
    public static boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated() && 
                   !(authentication.getPrincipal() instanceof String);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查当前用户是否为指定类型
     * 
     * @param userType 用户类型
     * @return true-是指定类型，false-不是
     */
    public static boolean isUserType(String userType) {
        SysUser currentUser = getCurrentUser();
        return currentUser != null && userType.equals(currentUser.getUserType());
    }

    /**
     * 检查当前用户是否为业主
     * 
     * @return true-是业主，false-不是
     */
    public static boolean isOwner() {
        return isUserType("OWNER");
    }

    /**
     * 检查当前用户是否为物业工作人员
     * 
     * @return true-是工作人员，false-不是
     */
    public static boolean isWorker() {
        return isUserType("WORKER");
    }

    /**
     * 检查当前用户是否为管理员
     * 
     * @return true-是管理员，false-不是
     */
    public static boolean isAdmin() {
        return isUserType("ADMIN");
    }

    /**
     * 检查当前用户是否为保安
     * 
     * @return true-是保安，false-不是
     */
    public static boolean isGuard() {
        return isUserType("GUARD");
    }
}

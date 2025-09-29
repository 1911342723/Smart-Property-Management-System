package com.property.service;

import com.property.entity.SysUser;
import com.property.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户详情服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 直接使用手机号登录
        SysUser user = userMapper.selectByPhone(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        return User.builder()
                .username(username)  // 使用手机号作为用户名
                .password(user.getPassword())  // 直接使用明文密码
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getStatus() == 0)
                .build();
    }
    
    /**
     * 获取用户权限
     */
    private Collection<? extends GrantedAuthority> getAuthorities(SysUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 根据用户类型添加角色权限
        switch (user.getUserType()) {
            case "ADMIN":
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            case "OWNER":
                authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
                break;
            case "GUARD":
                authorities.add(new SimpleGrantedAuthority("ROLE_GUARD"));
                break;
            case "WORKER":
                authorities.add(new SimpleGrantedAuthority("ROLE_WORKER"));
                break;
            default:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
        }
        
        return authorities;
    }
}


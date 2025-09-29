package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.property.entity.Room;
import com.property.entity.SysUser;
import com.property.mapper.RoomMapper;
import com.property.mapper.SysUserMapper;
import com.property.service.OwnerService;
import com.property.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业主服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<Room> getUserRooms(Long userId) {
        // 只返回用户作为业主拥有的房屋
        return roomMapper.selectRoomsByUserId(userId, "OWNER");
    }

    @Override
    public List<Room> getCurrentUserRooms() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        System.out.println("=== DEBUG: getCurrentUserRooms ===");
        System.out.println("Current userId: " + userId);
        
        // 只返回当前用户作为业主拥有的房屋
        List<Room> rooms = roomMapper.selectRoomsByUserId(userId, "OWNER");
        
        System.out.println("Found rooms count: " + (rooms != null ? rooms.size() : 0));
        
        return rooms;
    }

    @Override
    public Room getRoomDetail(Long roomId) {
        Room room = roomMapper.selectRoomWithDetails(roomId);
        if (room == null) {
            throw new RuntimeException("房屋不存在");
        }
        
        // 验证房屋所有权
        if (!verifyRoomOwnership(roomId)) {
            throw new RuntimeException("无权限访问该房屋信息");
        }
        
        return room;
    }

    @Override
    public void updateRoom(Room room) {
        if (room.getId() == null) {
            throw new RuntimeException("房屋ID不能为空");
        }
        
        // 验证房屋所有权
        if (!verifyRoomOwnership(room.getId())) {
            throw new RuntimeException("无权限修改该房屋信息");
        }
        
        // 获取原房屋信息
        Room existingRoom = roomMapper.selectById(room.getId());
        if (existingRoom == null) {
            throw new RuntimeException("房屋不存在");
        }
        
        // 业主只能更新面积信息
        // 不能更改房屋状态（由物业管理）
        
        // 验证面积数据
        if (room.getArea() == null || room.getArea().doubleValue() <= 0) {
            throw new RuntimeException("请输入正确的房屋面积");
        }
        
        // 创建更新对象，只设置允许更新的字段
        Room updateRoom = new Room();
        updateRoom.setId(room.getId());
        updateRoom.setArea(room.getArea());
        
        int result = roomMapper.updateById(updateRoom);
        if (result <= 0) {
            throw new RuntimeException("更新房屋信息失败");
        }
        
        System.out.println("=== DEBUG: Room updated successfully ===");
        System.out.println("Room ID: " + room.getId());
        System.out.println("New area: " + room.getArea());
    }

    @Override
    public boolean verifyRoomOwnership(Long roomId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return false;
        }
        
        // 查询用户是否为该房屋的业主
        int count = roomMapper.countUserRoomRelation(userId, roomId, "OWNER");
        return count > 0;
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
}

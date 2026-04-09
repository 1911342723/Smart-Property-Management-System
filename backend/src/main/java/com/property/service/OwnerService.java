package com.property.service;

import com.property.entity.ParkingSpace;
import com.property.entity.Room;

import java.util.List;

/**
 * 业主服务接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
public interface OwnerService {

    /**
     * 获取指定用户的房屋信息
     * 
     * @param userId 用户ID
     * @return 房屋信息列表
     */
    List<Room> getUserRooms(Long userId);

    /**
     * 获取当前用户的房屋信息
     * 
     * @return 房屋信息列表
     */
    List<Room> getCurrentUserRooms();

    /**
     * 获取当前用户的车位信息
     *
     * @return 车位信息列表
     */
    List<ParkingSpace> getCurrentUserParkingSpaces();

    /**
     * 获取房屋详情
     * 
     * @param roomId 房屋ID
     * @return 房屋详情
     */
    Room getRoomDetail(Long roomId);

    /**
     * 更新房屋信息
     * 
     * @param room 房屋信息
     */
    void updateRoom(Room room);

    /**
     * 验证当前用户对房屋的所有权
     * 
     * @param roomId 房屋ID
     * @return 是否拥有该房屋
     */
    boolean verifyRoomOwnership(Long roomId);
}





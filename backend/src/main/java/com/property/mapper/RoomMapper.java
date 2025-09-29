package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋数据访问层
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface RoomMapper extends BaseMapper<Room> {

    /**
     * 根据用户ID查询房屋列表（通过关系表）
     * 
     * @param userId 用户ID
     * @param relationType 关系类型：OWNER-业主，TENANT-租户
     * @return 房屋列表
     */
    @Select("SELECT r.*, b.building_name, u.unit_name, ow.relation_type " +
            "FROM room r " +
            "INNER JOIN owner_room ow ON r.id = ow.room_id " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "LEFT JOIN unit u ON r.unit_id = u.id " +
            "WHERE ow.user_id = #{userId} AND ow.relation_type = #{relationType} " +
            "AND ow.status = 1 AND ow.deleted = 0 " +
            "ORDER BY b.building_name, u.unit_name, r.room_no")
    List<Room> selectRoomsByUserId(@Param("userId") Long userId, @Param("relationType") String relationType);

    /**
     * 查询房屋详情（包含所有关联信息）
     * 
     * @param roomId 房屋ID
     * @return 房屋详情
     */
    @Select("SELECT r.*, b.building_name, u.unit_name " +
            "FROM room r " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "LEFT JOIN unit u ON r.unit_id = u.id " +
            "WHERE r.id = #{roomId}")
    Room selectRoomWithDetails(@Param("roomId") Long roomId);

    /**
     * 验证用户对房屋的权限
     * 
     * @param userId 用户ID
     * @param roomId 房屋ID
     * @param relationType 关系类型
     * @return 关系记录数量
     */
    @Select("SELECT COUNT(*) FROM owner_room " +
            "WHERE user_id = #{userId} AND room_id = #{roomId} " +
            "AND relation_type = #{relationType} AND status = 1 AND deleted = 0")
    int countUserRoomRelation(@Param("userId") Long userId, @Param("roomId") Long roomId, @Param("relationType") String relationType);

    /**
     * 根据楼栋ID查询房屋列表
     * 
     * @param buildingId 楼栋ID
     * @return 房屋列表
     */
    @Select("SELECT r.*, b.building_name " +
            "FROM room r " +
            "LEFT JOIN building b ON r.building_id = b.id " +
            "WHERE r.building_id = #{buildingId} " +
            "ORDER BY r.floor, r.room_no")
    List<Room> selectRoomsByBuildingId(@Param("buildingId") Long buildingId);
}

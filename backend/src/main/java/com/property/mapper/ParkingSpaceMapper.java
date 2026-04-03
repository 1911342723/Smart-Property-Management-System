package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.ParkingSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {
    
    @Select("SELECT p.*, u.real_name as owner_name, u.phone as owner_phone " +
            "FROM parking_space p " +
            "LEFT JOIN sys_user u ON p.owner_id = u.id " +
            "WHERE p.deleted = 0 " +
            "ORDER BY p.space_no ASC " +
            "LIMIT #{offset}, #{limit}")
    List<ParkingSpace> selectParkingSpacePage(int offset, int limit);
    
    @Select("SELECT COUNT(*) FROM parking_space WHERE deleted = 0")
    long countParkingSpace();

    @Select("SELECT p.*, u.real_name as owner_name, u.phone as owner_phone " +
            "FROM parking_space p " +
            "LEFT JOIN sys_user u ON p.owner_id = u.id " +
            "WHERE p.deleted = 0 AND (p.space_no LIKE CONCAT('%', #{keyword}, '%') OR u.real_name LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY p.space_no ASC " +
            "LIMIT #{offset}, #{limit}")
    List<ParkingSpace> selectParkingSpacePageByKeyword(String keyword, int offset, int limit);

    @Select("SELECT COUNT(*) FROM parking_space p " +
            "LEFT JOIN sys_user u ON p.owner_id = u.id " +
            "WHERE p.deleted = 0 AND (p.space_no LIKE CONCAT('%', #{keyword}, '%') OR u.real_name LIKE CONCAT('%', #{keyword}, '%'))")
    long countParkingSpaceByKeyword(String keyword);
}

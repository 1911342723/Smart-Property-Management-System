package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.PageResult;
import com.property.entity.Room;
import com.property.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 房产服务类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class RoomService {

    @Autowired
    private RoomMapper roomMapper;

    /**
     * 获取房产列表（分页）
     */
    public PageResult<Room> getPropertyList(Integer pageNum, Integer pageSize,
                                           String building, String unit, String room,
                                           String type, String bindStatus, String keyword) {
        Page<Room> page = new Page<>(pageNum, pageSize);
        
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        
        // 搜索条件
        if (StringUtils.hasText(building)) {
            queryWrapper.like("building_name", building);
        }
        if (StringUtils.hasText(unit)) {
            queryWrapper.like("unit_name", unit);
        }
        if (StringUtils.hasText(room)) {
            queryWrapper.like("room_no", room);
        }
        if (StringUtils.hasText(type)) {
            queryWrapper.eq("room_type", type);
        }
        if (StringUtils.hasText(bindStatus)) {
            if ("bound".equals(bindStatus)) {
                queryWrapper.isNotNull("owner_name");
            } else if ("unbound".equals(bindStatus)) {
                queryWrapper.isNull("owner_name");
            }
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("room_no", keyword)
                .or().like("building_name", keyword)
                .or().like("owner_name", keyword)
            );
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<Room> resultPage = roomMapper.selectPage(page, queryWrapper);
        
        PageResult<Room> pageResult = new PageResult<>();
        pageResult.setList(resultPage.getRecords());
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setPageNum(Long.valueOf(pageNum));
        pageResult.setPageSize(Long.valueOf(pageSize));
        
        return pageResult;
    }

    /**
     * 获取房产详情
     */
    public Room getPropertyDetail(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new RuntimeException("房产不存在");
        }
        return room;
    }

    /**
     * 添加房产
     */
    @Transactional(rollbackFor = Exception.class)
    public void createProperty(Room room) {
        // 检查房号是否重复
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("building_id", room.getBuildingId())
                   .eq("unit_id", room.getUnitId())
                   .eq("room_no", room.getRoomNo())
                   .eq("deleted", 0);
        
        Long count = roomMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("该房号已存在");
        }
        
        // 默认状态为空置
        if (!StringUtils.hasText(room.getStatus())) {
            room.setStatus("VACANT");
        }
        
        roomMapper.insert(room);
    }

    /**
     * 更新房产信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProperty(Room room) {
        Room existingRoom = roomMapper.selectById(room.getId());
        if (existingRoom == null) {
            throw new RuntimeException("房产不存在");
        }
        
        roomMapper.updateById(room);
    }

    /**
     * 删除房产
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProperty(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new RuntimeException("房产不存在");
        }
        
        // 逻辑删除
        roomMapper.deleteById(id);
    }

    /**
     * 批量删除房产
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteProperty(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的房产");
        }
        
        roomMapper.deleteBatchIds(ids);
    }

    /**
     * 绑定业主
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindOwner(Long propertyId, Long ownerId, String relationType) {
        Room room = roomMapper.selectById(propertyId);
        if (room == null) {
            throw new RuntimeException("房产不存在");
        }
        
        // 更新房产状态
        room.setStatus("OCCUPIED");
        // 这里简化处理，实际应该关联用户表
        roomMapper.updateById(room);
        
        // TODO: 在实际项目中，应该在user_room_relation表中插入关联记录
    }

    /**
     * 解绑业主
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindOwner(Long propertyId, Long ownerId) {
        Room room = roomMapper.selectById(propertyId);
        if (room == null) {
            throw new RuntimeException("房产不存在");
        }
        
        // 更新房产状态
        room.setStatus("VACANT");
        roomMapper.updateById(room);
        
        // TODO: 在实际项目中，应该删除user_room_relation表中的关联记录
    }
}

package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.PageResult;
import com.property.entity.ParkingSpace;
import com.property.entity.SysUser;
import com.property.mapper.ParkingSpaceMapper;
import com.property.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ParkingSpaceService {

    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    public PageResult<ParkingSpace> getParkingSpaceList(Integer pageNum, Integer pageSize,
                                                        String areaCode, String spaceType,
                                                        String status, Long ownerId,
                                                        String keyword) {
        Page<ParkingSpace> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StringUtils.hasText(areaCode)) {
            queryWrapper.like("area_code", areaCode);
        }
        if (StringUtils.hasText(spaceType)) {
            queryWrapper.eq("space_type", spaceType);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        if (ownerId != null) {
            queryWrapper.eq("owner_id", ownerId);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like("space_no", keyword)
                .or().like("area_code", keyword)
                .or().like("vehicle_no", keyword)
                .or().like("remark", keyword));
        }

        queryWrapper.orderByAsc("area_code").orderByAsc("space_no");

        Page<ParkingSpace> resultPage = parkingSpaceMapper.selectPage(page, queryWrapper);
        fillOwnerInfo(resultPage.getRecords());
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal(), Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    public ParkingSpace getParkingSpaceDetail(Long id) {
        ParkingSpace parkingSpace = parkingSpaceMapper.selectById(id);
        if (parkingSpace == null || Integer.valueOf(1).equals(parkingSpace.getDeleted())) {
            throw new RuntimeException("车位不存在");
        }
        fillOwnerInfo(Collections.singletonList(parkingSpace));
        return parkingSpace;
    }

    public List<ParkingSpace> getOwnerParkingSpaces(Long ownerId) {
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
            .eq("owner_id", ownerId)
            .orderByAsc("area_code")
            .orderByAsc("space_no");

        List<ParkingSpace> parkingSpaces = parkingSpaceMapper.selectList(queryWrapper);
        fillOwnerInfo(parkingSpaces);
        return parkingSpaces;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createParkingSpace(ParkingSpace parkingSpace) {
        validateUniqueSpaceNo(null, parkingSpace.getSpaceNo());
        normalizeParkingSpace(parkingSpace);
        parkingSpaceMapper.insert(parkingSpace);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateParkingSpace(ParkingSpace parkingSpace) {
        ParkingSpace existing = parkingSpaceMapper.selectById(parkingSpace.getId());
        if (existing == null || Integer.valueOf(1).equals(existing.getDeleted())) {
            throw new RuntimeException("车位不存在");
        }
        validateUniqueSpaceNo(parkingSpace.getId(), parkingSpace.getSpaceNo());
        normalizeParkingSpace(parkingSpace);
        parkingSpaceMapper.updateById(parkingSpace);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteParkingSpace(Long id) {
        ParkingSpace existing = parkingSpaceMapper.selectById(id);
        if (existing == null || Integer.valueOf(1).equals(existing.getDeleted())) {
            throw new RuntimeException("车位不存在");
        }
        parkingSpaceMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteParkingSpace(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的车位");
        }
        parkingSpaceMapper.deleteBatchIds(ids);
    }

    private void validateUniqueSpaceNo(Long id, String spaceNo) {
        QueryWrapper<ParkingSpace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("space_no", spaceNo).eq("deleted", 0);
        if (id != null) {
            queryWrapper.ne("id", id);
        }
        Long count = parkingSpaceMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("车位编号已存在");
        }
    }

    private void normalizeParkingSpace(ParkingSpace parkingSpace) {
        if (!StringUtils.hasText(parkingSpace.getSpaceType())) {
            parkingSpace.setSpaceType("FIXED");
        }
        if (!StringUtils.hasText(parkingSpace.getStatus())) {
            parkingSpace.setStatus(parkingSpace.getOwnerId() != null ? "OCCUPIED" : "AVAILABLE");
        }
        if (parkingSpace.getMonthlyFee() == null) {
            parkingSpace.setMonthlyFee(BigDecimal.ZERO);
        }
        if (parkingSpace.getOwnerId() == null) {
            parkingSpace.setVehicleNo(null);
            if ("OCCUPIED".equals(parkingSpace.getStatus())) {
                parkingSpace.setStatus("AVAILABLE");
            }
            return;
        }

        SysUser owner = sysUserMapper.selectById(parkingSpace.getOwnerId());
        if (owner == null || Integer.valueOf(1).equals(owner.getDeleted())) {
            throw new RuntimeException("绑定业主不存在");
        }
        if (!Objects.equals("OWNER", owner.getUserType())) {
            throw new RuntimeException("仅支持绑定业主账号");
        }
        if (!StringUtils.hasText(parkingSpace.getStatus()) || "AVAILABLE".equals(parkingSpace.getStatus())) {
            parkingSpace.setStatus("OCCUPIED");
        }
    }

    private void fillOwnerInfo(List<ParkingSpace> parkingSpaces) {
        if (parkingSpaces == null || parkingSpaces.isEmpty()) {
            return;
        }
        List<Long> ownerIds = parkingSpaces.stream()
            .map(ParkingSpace::getOwnerId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (ownerIds.isEmpty()) {
            return;
        }

        Map<Long, SysUser> ownerMap = sysUserMapper.selectBatchIds(ownerIds).stream()
            .collect(Collectors.toMap(SysUser::getId, user -> user));
        for (ParkingSpace parkingSpace : parkingSpaces) {
            if (parkingSpace.getOwnerId() == null) {
                continue;
            }
            SysUser owner = ownerMap.get(parkingSpace.getOwnerId());
            if (owner != null) {
                parkingSpace.setOwnerName(owner.getRealName());
                parkingSpace.setOwnerPhone(owner.getPhone());
            }
        }
    }
}
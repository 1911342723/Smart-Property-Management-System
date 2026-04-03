package com.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.ParkingSpace;
import com.property.mapper.ParkingSpaceMapper;
import com.property.service.ParkingSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements ParkingSpaceService {

    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Override
    public Map<String, Object> getParkingSpacePage(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        List<ParkingSpace> list;
        long total;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = parkingSpaceMapper.selectParkingSpacePageByKeyword(keyword, offset, size);
            total = parkingSpaceMapper.countParkingSpaceByKeyword(keyword);
        } else {
            list = parkingSpaceMapper.selectParkingSpacePage(offset, size);
            total = parkingSpaceMapper.countParkingSpace();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", page);
        result.put("pageSize", size);
        return result;
    }
}

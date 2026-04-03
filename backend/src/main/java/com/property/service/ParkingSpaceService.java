package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.ParkingSpace;
import java.util.Map;

public interface ParkingSpaceService extends IService<ParkingSpace> {
    Map<String, Object> getParkingSpacePage(int page, int size, String keyword);
}

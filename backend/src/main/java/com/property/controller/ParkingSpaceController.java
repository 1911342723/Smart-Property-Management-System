package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.ParkingSpace;
import com.property.service.ParkingSpaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "车位管理")
@RestController
@RequestMapping("/parking-space")
public class ParkingSpaceController {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @ApiOperation("获取车位列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<ParkingSpace>> getParkingSpaceList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String spaceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String keyword) {
        try {
            return Result.success(parkingSpaceService.getParkingSpaceList(pageNum, pageSize, areaCode, spaceType, status, ownerId, keyword));
        } catch (Exception e) {
            return Result.error("获取车位列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取车位详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ParkingSpace> getParkingSpaceDetail(@ApiParam("车位ID") @PathVariable Long id) {
        try {
            return Result.success(parkingSpaceService.getParkingSpaceDetail(id));
        } catch (Exception e) {
            return Result.error("获取车位详情失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增车位")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createParkingSpace(@RequestBody ParkingSpace parkingSpace) {
        try {
            parkingSpaceService.createParkingSpace(parkingSpace);
            return Result.success("新增车位成功");
        } catch (Exception e) {
            return Result.error("新增车位失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新车位")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateParkingSpace(@PathVariable Long id, @RequestBody ParkingSpace parkingSpace) {
        try {
            parkingSpace.setId(id);
            parkingSpaceService.updateParkingSpace(parkingSpace);
            return Result.success("更新车位成功");
        } catch (Exception e) {
            return Result.error("更新车位失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除车位")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteParkingSpace(@PathVariable Long id) {
        try {
            parkingSpaceService.deleteParkingSpace(id);
            return Result.success("删除车位成功");
        } catch (Exception e) {
            return Result.error("删除车位失败：" + e.getMessage());
        }
    }

    @ApiOperation("批量删除车位")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchDeleteParkingSpace(@RequestBody List<Long> ids) {
        try {
            parkingSpaceService.batchDeleteParkingSpace(ids);
            return Result.success("批量删除车位成功");
        } catch (Exception e) {
            return Result.error("批量删除车位失败：" + e.getMessage());
        }
    }
}
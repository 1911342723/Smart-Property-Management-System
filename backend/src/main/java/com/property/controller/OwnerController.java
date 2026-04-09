package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.ParkingSpace;
import com.property.entity.Room;
import com.property.service.OwnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业主相关控制器
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "业主管理")
@RestController
@RequestMapping("/owner")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @ApiOperation("获取业主房屋信息列表")
    @GetMapping("/rooms/{userId}")
    public Result<List<Room>> getUserRooms(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            List<Room> rooms = ownerService.getUserRooms(userId);
            return Result.success(rooms);
        } catch (Exception e) {
            return Result.error("获取房屋信息失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取当前用户房屋信息")
    @GetMapping("/my-rooms")
    public Result<List<Room>> getMyRooms() {
        try {
            List<Room> rooms = ownerService.getCurrentUserRooms();
            return Result.success(rooms);
        } catch (Exception e) {
            return Result.error("获取房屋信息失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取当前用户车位信息")
    @GetMapping("/my-parking-spaces")
    public Result<List<ParkingSpace>> getMyParkingSpaces() {
        try {
            List<ParkingSpace> parkingSpaces = ownerService.getCurrentUserParkingSpaces();
            return Result.success(parkingSpaces);
        } catch (Exception e) {
            return Result.error("获取车位信息失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取房屋详情")
    @GetMapping("/rooms/detail/{roomId}")
    public Result<Room> getRoomDetail(
            @ApiParam(value = "房屋ID", required = true) @PathVariable Long roomId) {
        try {
            Room room = ownerService.getRoomDetail(roomId);
            return Result.success(room);
        } catch (Exception e) {
            return Result.error("获取房屋详情失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新房屋信息")
    @PutMapping("/rooms/{roomId}")
    public Result<String> updateRoom(
            @ApiParam(value = "房屋ID", required = true) @PathVariable Long roomId,
            @RequestBody Room room) {
        try {
            room.setId(roomId);
            ownerService.updateRoom(room);
            return Result.success("房屋信息更新成功");
        } catch (Exception e) {
            return Result.error("更新房屋信息失败：" + e.getMessage());
        }
    }

    @ApiOperation("验证房屋所有权")
    @GetMapping("/rooms/{roomId}/verify")
    public Result<Boolean> verifyRoomOwnership(
            @ApiParam(value = "房屋ID", required = true) @PathVariable Long roomId) {
        try {
            boolean isOwner = ownerService.verifyRoomOwnership(roomId);
            return Result.success(isOwner);
        } catch (Exception e) {
            return Result.error("验证房屋所有权失败：" + e.getMessage());
        }
    }
}

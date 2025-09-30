package com.property.controller;

import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Room;
import com.property.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房产管理控制器（管理员）
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Api(tags = "房产管理")
@RestController
@RequestMapping("/property")
public class PropertyController {

    @Autowired
    private RoomService roomService;

    @ApiOperation("获取房产列表（分页）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<PageResult<Room>> getPropertyList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String room,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String bindStatus,
            @RequestParam(required = false) String keyword) {
        try {
            PageResult<Room> result = roomService.getPropertyList(
                pageNum, pageSize, building, unit, room, type, bindStatus, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取房产列表失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取房产详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Room> getPropertyDetail(
            @ApiParam(value = "房产ID", required = true) @PathVariable Long id) {
        try {
            Room room = roomService.getPropertyDetail(id);
            return Result.success(room);
        } catch (Exception e) {
            return Result.error("获取房产详情失败：" + e.getMessage());
        }
    }

    @ApiOperation("添加房产")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<String> createProperty(@RequestBody Room room) {
        try {
            roomService.createProperty(room);
            return Result.success("添加房产成功");
        } catch (Exception e) {
            return Result.error("添加房产失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新房产信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<String> updateProperty(
            @ApiParam(value = "房产ID", required = true) @PathVariable Long id,
            @RequestBody Room room) {
        try {
            room.setId(id);
            roomService.updateProperty(room);
            return Result.success("更新房产信息成功");
        } catch (Exception e) {
            return Result.error("更新房产信息失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除房产")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<String> deleteProperty(
            @ApiParam(value = "房产ID", required = true) @PathVariable Long id) {
        try {
            roomService.deleteProperty(id);
            return Result.success("删除房产成功");
        } catch (Exception e) {
            return Result.error("删除房产失败：" + e.getMessage());
        }
    }

    @ApiOperation("批量删除房产")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<String> batchDeleteProperty(@RequestBody List<Long> ids) {
        try {
            roomService.batchDeleteProperty(ids);
            return Result.success("批量删除房产成功");
        } catch (Exception e) {
            return Result.error("批量删除房产失败：" + e.getMessage());
        }
    }

    @ApiOperation("绑定业主")
    @PostMapping("/bind")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<String> bindOwner(
            @RequestParam Long propertyId,
            @RequestParam Long ownerId,
            @RequestParam(defaultValue = "OWNER") String relationType) {
        try {
            roomService.bindOwner(propertyId, ownerId, relationType);
            return Result.success("绑定业主成功");
        } catch (Exception e) {
            return Result.error("绑定业主失败：" + e.getMessage());
        }
    }

    @ApiOperation("解绑业主")
    @PostMapping("/unbind")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<String> unbindOwner(
            @RequestParam Long propertyId,
            @RequestParam Long ownerId) {
        try {
            roomService.unbindOwner(propertyId, ownerId);
            return Result.success("解绑业主成功");
        } catch (Exception e) {
            return Result.error("解绑业主失败：" + e.getMessage());
        }
    }
}

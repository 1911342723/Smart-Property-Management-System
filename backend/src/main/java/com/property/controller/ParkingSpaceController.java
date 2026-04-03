package com.property.controller;

import com.property.common.Result;
import com.property.entity.ParkingSpace;
import com.property.service.ParkingSpaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;

@Api(tags = "车位管理接口")
@RestController
@RequestMapping("/api/parking")
public class ParkingSpaceController {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @ApiOperation("分页获取车位列表")
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> pageData = parkingSpaceService.getParkingSpacePage(pageNum, pageSize, keyword);
        return Result.success(pageData);
    }

    @ApiOperation("获取车位详情")
    @GetMapping("/{id}")
    public Result<?> getInfo(@PathVariable Long id) {
        return Result.success(parkingSpaceService.getById(id));
    }

    @ApiOperation("新增车位")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody ParkingSpace parkingSpace) {
        return Result.success(parkingSpaceService.save(parkingSpace));
    }

    @ApiOperation("修改车位")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody ParkingSpace parkingSpace) {
        return Result.success(parkingSpaceService.updateById(parkingSpace));
    }

    @ApiOperation("删除车位")
    @DeleteMapping("/{ids}")
    public Result<?> remove(@PathVariable Long[] ids) {
        return Result.success(parkingSpaceService.removeByIds(Arrays.asList(ids)));
    }
}

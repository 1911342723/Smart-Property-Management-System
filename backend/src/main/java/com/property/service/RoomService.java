package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.PageResult;
import com.property.entity.OwnerRoom;
import com.property.entity.Room;
import com.property.entity.SysUser;
import com.property.mapper.OwnerRoomMapper;
import com.property.mapper.RoomMapper;
import com.property.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    
    @Autowired
    private OwnerRoomMapper ownerRoomMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;

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
        
        // 加载每个房产的业主信息
        List<Room> rooms = resultPage.getRecords();
        if (rooms != null && !rooms.isEmpty()) {
            // 收集所有房产ID
            List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .collect(Collectors.toList());
            
            // 查询这些房产的业主关联
            QueryWrapper<OwnerRoom> orWrapper = new QueryWrapper<>();
            orWrapper.in("room_id", roomIds)
                    .eq("status", 1)
                    .eq("deleted", 0);
            List<OwnerRoom> ownerRooms = ownerRoomMapper.selectList(orWrapper);
            
            // 按房产ID分组
            Map<Long, List<Long>> roomOwnerMap = ownerRooms.stream()
                .collect(Collectors.groupingBy(
                    OwnerRoom::getRoomId,
                    Collectors.mapping(OwnerRoom::getUserId, Collectors.toList())
                ));
            
            // 查询所有业主用户信息
            if (!ownerRooms.isEmpty()) {
                List<Long> userIds = ownerRooms.stream()
                    .map(OwnerRoom::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
                
                List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
                Map<Long, SysUser> userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
                
                // 为每个房产设置业主列表
                for (Room r : rooms) {
                    List<Long> ownerIds = roomOwnerMap.get(r.getId());
                    if (ownerIds != null && !ownerIds.isEmpty()) {
                        List<SysUser> roomOwners = ownerIds.stream()
                            .map(userMap::get)
                            .filter(u -> u != null)
                            .collect(Collectors.toList());
                        r.setOwners(roomOwners);
                    } else {
                        r.setOwners(new ArrayList<>());
                    }
                }
            } else {
                // 没有业主，设置空列表
                rooms.forEach(r -> r.setOwners(new ArrayList<>()));
            }
        }
        
        PageResult<Room> pageResult = new PageResult<>();
        pageResult.setList(rooms);
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
        
        // 加载业主信息
        QueryWrapper<OwnerRoom> orWrapper = new QueryWrapper<>();
        orWrapper.eq("room_id", id)
                .eq("status", 1)
                .eq("deleted", 0);
        List<OwnerRoom> ownerRooms = ownerRoomMapper.selectList(orWrapper);
        
        if (ownerRooms != null && !ownerRooms.isEmpty()) {
            List<Long> userIds = ownerRooms.stream()
                .map(OwnerRoom::getUserId)
                .collect(Collectors.toList());
            
            List<SysUser> owners = sysUserMapper.selectBatchIds(userIds);
            room.setOwners(owners);
        } else {
            room.setOwners(new ArrayList<>());
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
        
        // 检查是否已经绑定
        QueryWrapper<OwnerRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", ownerId)
                   .eq("room_id", propertyId)
                   .eq("relation_type", relationType)
                   .eq("deleted", 0);
        OwnerRoom existing = ownerRoomMapper.selectOne(queryWrapper);
        
        if (existing != null) {
            // 已存在，更新状态为正常
            existing.setStatus(1);
            existing.setUpdateTime(LocalDateTime.now());
            ownerRoomMapper.updateById(existing);
        } else {
            // 创建新的关联记录
            OwnerRoom ownerRoom = new OwnerRoom();
            ownerRoom.setUserId(ownerId);
            ownerRoom.setRoomId(propertyId);
            ownerRoom.setRelationType(relationType);
            ownerRoom.setStatus(1);
            ownerRoom.setCreateTime(LocalDateTime.now());
            ownerRoom.setUpdateTime(LocalDateTime.now());
            ownerRoom.setDeleted(0);
            ownerRoomMapper.insert(ownerRoom);
        }
        
        // 更新房产状态
        room.setStatus("OCCUPIED");
        room.setUpdateTime(LocalDateTime.now());
        roomMapper.updateById(room);
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
        
        // 删除关联记录（逻辑删除）
        QueryWrapper<OwnerRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", ownerId)
                   .eq("room_id", propertyId)
                   .eq("deleted", 0);
        OwnerRoom ownerRoom = ownerRoomMapper.selectOne(queryWrapper);
        
        if (ownerRoom != null) {
            ownerRoomMapper.deleteById(ownerRoom.getId());
        }
        
        // 检查是否还有其他业主绑定此房产
        QueryWrapper<OwnerRoom> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("room_id", propertyId)
                   .eq("status", 1)
                   .eq("deleted", 0);
        long count = ownerRoomMapper.selectCount(checkWrapper);
        
        // 如果没有其他业主，更新房产状态为空置
        if (count == 0) {
            room.setStatus("VACANT");
            room.setUpdateTime(LocalDateTime.now());
            roomMapper.updateById(room);
        }
    }

    /**
     * 导出房产列表
     */
    public void exportPropertyList(String building, String unit, String room,
                                   String type, String bindStatus, String keyword,
                                   HttpServletResponse response) throws IOException {
        // 查询所有符合条件的房产（不分页）
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        
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
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("room_no", keyword)
                .or().like("building_name", keyword)
                .or().like("owner_name", keyword)
            );
        }
        
        queryWrapper.orderByDesc("create_time");
        List<Room> rooms = roomMapper.selectList(queryWrapper);
        
        // 加载业主信息
        if (rooms != null && !rooms.isEmpty()) {
            List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .collect(Collectors.toList());
            
            QueryWrapper<OwnerRoom> orWrapper = new QueryWrapper<>();
            orWrapper.in("room_id", roomIds)
                    .eq("status", 1)
                    .eq("deleted", 0);
            List<OwnerRoom> ownerRooms = ownerRoomMapper.selectList(orWrapper);
            
            Map<Long, List<Long>> roomOwnerMap = ownerRooms.stream()
                .collect(Collectors.groupingBy(
                    OwnerRoom::getRoomId,
                    Collectors.mapping(OwnerRoom::getUserId, Collectors.toList())
                ));
            
            if (!ownerRooms.isEmpty()) {
                List<Long> userIds = ownerRooms.stream()
                    .map(OwnerRoom::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
                
                List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
                Map<Long, SysUser> userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
                
                for (Room r : rooms) {
                    List<Long> ownerIds = roomOwnerMap.get(r.getId());
                    if (ownerIds != null && !ownerIds.isEmpty()) {
                        List<SysUser> roomOwners = ownerIds.stream()
                            .map(userMap::get)
                            .filter(u -> u != null)
                            .collect(Collectors.toList());
                        r.setOwners(roomOwners);
                    } else {
                        r.setOwners(new ArrayList<>());
                    }
                }
            } else {
                rooms.forEach(r -> r.setOwners(new ArrayList<>()));
            }
        }
        
        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("房产列表");
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"房产ID", "楼栋ID", "单元ID", "房间号", "楼层", "房屋类型", "面积(㎡)", "状态", "物业费(元/月)", "业主姓名", "业主电话", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }
        
        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (Room r : rooms) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getBuildingId() != null ? r.getBuildingId() : 0);
            row.createCell(2).setCellValue(r.getUnitId() != null ? r.getUnitId() : 0);
            row.createCell(3).setCellValue(r.getRoomNo() != null ? r.getRoomNo() : "");
            row.createCell(4).setCellValue(r.getFloor() != null ? r.getFloor() : 0);
            row.createCell(5).setCellValue(r.getRoomType() != null ? r.getRoomType() : "");
            row.createCell(6).setCellValue(r.getArea() != null ? r.getArea().doubleValue() : 0);
            row.createCell(7).setCellValue(r.getStatus() != null ? r.getStatus() : "");
            row.createCell(8).setCellValue(r.getPropertyFee() != null ? r.getPropertyFee().doubleValue() : 0);
            
            // 业主信息（多个业主用逗号分隔）
            if (r.getOwners() != null && !r.getOwners().isEmpty()) {
                String ownerNames = r.getOwners().stream()
                    .map(o -> o.getRealName() != null ? o.getRealName() : o.getUsername())
                    .collect(Collectors.joining(", "));
                String ownerPhones = r.getOwners().stream()
                    .map(SysUser::getPhone)
                    .collect(Collectors.joining(", "));
                row.createCell(9).setCellValue(ownerNames);
                row.createCell(10).setCellValue(ownerPhones);
            } else {
                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue("");
            }
            
            row.createCell(11).setCellValue(r.getCreateTime() != null ? r.getCreateTime().format(formatter) : "");
        }
        
        // 设置响应头
        String fileName = "房产列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    /**
     * 下载房产导入模板
     */
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("房产导入模板");
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"楼栋ID*", "单元ID*", "房间号*", "楼层*", "房屋类型*", "面积(㎡)*", "状态", "物业费(元/月)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }
        
        // 添加示例数据行
        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue(1);
        exampleRow.createCell(1).setCellValue(1);
        exampleRow.createCell(2).setCellValue("101");
        exampleRow.createCell(3).setCellValue(1);
        exampleRow.createCell(4).setCellValue("RESIDENTIAL");
        exampleRow.createCell(5).setCellValue(88.5);
        exampleRow.createCell(6).setCellValue("VACANT");
        exampleRow.createCell(7).setCellValue(150);
        
        // 添加说明行
        Row noteRow = sheet.createRow(3);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("说明：标*为必填项，房屋类型：RESIDENTIAL-住宅，COMMERCIAL-商用，OFFICE-办公；状态：VACANT-空置，OCCUPIED-已入住，RENTED-出租");
        
        // 设置响应头
        String fileName = "房产导入模板.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }
}

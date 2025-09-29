package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访客Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {

    /**
     * 分页查询访客信息（包含关联信息）
     * 
     * @param page 分页参数
     * @param ownerId 业主ID
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 访客信息列表
     */
    @Select({
        "<script>",
        "SELECT v.*, u.real_name as owner_name, ",
        "CONCAT(b.building_name, '-', un.unit_name, '-', r.room_no) as room_address, ",
        "g.real_name as guard_name ",
        "FROM visitor v ",
        "LEFT JOIN sys_user u ON v.owner_id = u.id ",
        "LEFT JOIN room r ON v.room_id = r.id ",
        "LEFT JOIN building b ON r.building_id = b.id ",
        "LEFT JOIN unit un ON r.unit_id = un.id ",
        "LEFT JOIN sys_user g ON v.guard_id = g.id ",
        "WHERE v.deleted = 0 ",
        "<if test='ownerId != null'>",
        "AND v.owner_id = #{ownerId} ",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND v.status = #{status} ",
        "</if>",
        "<if test='startDate != null'>",
        "AND v.planned_arrival >= #{startDate} ",
        "</if>",
        "<if test='endDate != null'>",
        "AND v.planned_arrival &lt;= #{endDate} ",
        "</if>",
        "ORDER BY v.planned_arrival DESC",
        "</script>"
    })
    IPage<Visitor> selectPageWithDetails(Page<Visitor> page, 
                                       @Param("ownerId") Long ownerId,
                                       @Param("status") String status,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 根据二维码获取访客信息
     * 
     * @param qrCode 二维码
     * @return 访客信息
     */
    @Select({
        "SELECT v.*, u.real_name as owner_name, ",
        "CONCAT(b.building_name, '-', un.unit_name, '-', r.room_no) as room_address ",
        "FROM visitor v ",
        "LEFT JOIN sys_user u ON v.owner_id = u.id ",
        "LEFT JOIN room r ON v.room_id = r.id ",
        "LEFT JOIN building b ON r.building_id = b.id ",
        "LEFT JOIN unit un ON r.unit_id = un.id ",
        "WHERE v.qr_code = #{qrCode} AND v.deleted = 0"
    })
    Visitor selectByQrCode(@Param("qrCode") String qrCode);

    /**
     * 获取访客统计信息
     * 
     * @param ownerId 业主ID
     * @return 统计信息
     */
    @Select({
        "SELECT ",
        "COUNT(*) as total, ",
        "SUM(CASE WHEN DATE(planned_arrival) = CURDATE() THEN 1 ELSE 0 END) as today, ",
        "SUM(CASE WHEN YEARWEEK(planned_arrival, 1) = YEARWEEK(CURDATE(), 1) THEN 1 ELSE 0 END) as thisWeek, ",
        "SUM(CASE WHEN YEAR(planned_arrival) = YEAR(CURDATE()) AND MONTH(planned_arrival) = MONTH(CURDATE()) THEN 1 ELSE 0 END) as thisMonth ",
        "FROM visitor ",
        "WHERE owner_id = #{ownerId} AND deleted = 0"
    })
    Map<String, Object> getVisitorStats(@Param("ownerId") Long ownerId);

    /**
     * 生成访客二维码
     * 
     * @return 二维码字符串
     */
    @Select("SELECT CONCAT('VIS', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(IFNULL(MAX(SUBSTRING(qr_code, 12)), 0) + 1, 6, '0')) " +
            "FROM visitor WHERE qr_code LIKE CONCAT('VIS', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generateQrCode();

    /**
     * 获取今日访客列表
     * 
     * @param ownerId 业主ID
     * @return 今日访客列表
     */
    @Select({
        "SELECT v.*, u.real_name as owner_name, ",
        "CONCAT(b.building_name, '-', un.unit_name, '-', r.room_no) as room_address ",
        "FROM visitor v ",
        "LEFT JOIN sys_user u ON v.owner_id = u.id ",
        "LEFT JOIN room r ON v.room_id = r.id ",
        "LEFT JOIN building b ON r.building_id = b.id ",
        "LEFT JOIN unit un ON r.unit_id = un.id ",
        "WHERE v.owner_id = #{ownerId} AND DATE(v.planned_arrival) = CURDATE() AND v.deleted = 0 ",
        "ORDER BY v.planned_arrival ASC"
    })
    List<Visitor> getTodayVisitors(@Param("ownerId") Long ownerId);
}





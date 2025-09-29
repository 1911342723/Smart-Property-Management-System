package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 投诉建议Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

    /**
     * 分页查询投诉建议（带关联信息）
     * 
     * @param page 分页对象
     * @param complaintType 投诉类型
     * @param status 状态
     * @param complainantId 投诉人ID
     * @param urgencyLevel 紧急程度
     * @return 投诉建议列表
     */
    @Select({
        "<script>",
        "SELECT c.*, ",
        "u1.real_name as complainant_name, u1.phone as complainant_phone, ",
        "u2.real_name as handler_name, ",
        "CONCAT(bd.building_name, '-', un.unit_name, '-', r.room_no) as room_address ",
        "FROM complaint c ",
        "LEFT JOIN sys_user u1 ON c.complainant_id = u1.id ",
        "LEFT JOIN sys_user u2 ON c.handler_id = u2.id ",
        "LEFT JOIN room r ON c.room_id = r.id ",
        "LEFT JOIN unit un ON r.unit_id = un.id ",
        "LEFT JOIN building bd ON r.building_id = bd.id ",
        "WHERE c.deleted = 0 ",
        "<if test='complaintType != null and complaintType != \"\"'>",
        "AND c.complaint_type = #{complaintType} ",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND c.status IN ",
        "<foreach item='item' index='index' collection='status.split(\",\")' open='(' separator=',' close=')'>",
        "#{item}",
        "</foreach>",
        "</if>",
        "<if test='complainantId != null'>",
        "AND c.complainant_id = #{complainantId} ",
        "</if>",
        "<if test='urgencyLevel != null and urgencyLevel != \"\"'>",
        "AND c.urgency_level = #{urgencyLevel} ",
        "</if>",
        "ORDER BY c.create_time DESC",
        "</script>"
    })
    IPage<Complaint> selectPageWithDetails(Page<Complaint> page,
                                          @Param("complaintType") String complaintType,
                                          @Param("status") String status,
                                          @Param("complainantId") Long complainantId,
                                          @Param("urgencyLevel") String urgencyLevel);

    /**
     * 生成投诉编号
     * 
     * @return 投诉编号
     */
    @Select("SELECT CONCAT('CP', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(IFNULL(MAX(SUBSTRING(complaint_no, 11)), 0) + 1, 4, '0')) " +
            "FROM complaint WHERE complaint_no LIKE CONCAT('CP', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generateComplaintNo();

    /**
     * 统计投诉数量按状态分组
     * 
     * @param complainantId 投诉人ID
     * @return 统计结果
     */
    @Select({
        "SELECT status, COUNT(*) as count ",
        "FROM complaint ",
        "WHERE deleted = 0 ",
        "<if test='complainantId != null'>",
        "AND complainant_id = #{complainantId} ",
        "</if>",
        "GROUP BY status"
    })
    java.util.List<java.util.Map<String, Object>> getComplaintStatsByStatus(@Param("complainantId") Long complainantId);
}







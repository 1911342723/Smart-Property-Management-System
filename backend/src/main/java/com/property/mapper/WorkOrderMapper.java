package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工单Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 分页查询工单（带关联信息）
     * 
     * @param page 分页对象
     * @param category 工单类别
     * @param status 工单状态
     * @param submitterId 提交人ID
     * @param assigneeId 分配人ID
     * @return 工单列表
     */
    @Select({
        "<script>",
        "SELECT wo.*, ",
        "su.real_name as submitter_name, ",
        "su.phone as submitter_phone, ",
        "au.real_name as assignee_name, ",
        "CONCAT(b.building_name, '-', u.unit_name, '-', r.room_no) as room_address ",
        "FROM work_order wo ",
        "LEFT JOIN sys_user su ON wo.submitter_id = su.id ",
        "LEFT JOIN sys_user au ON wo.assignee_id = au.id ",
        "LEFT JOIN room r ON wo.room_id = r.id ",
        "LEFT JOIN unit u ON r.unit_id = u.id ",
        "LEFT JOIN building b ON r.building_id = b.id ",
        "WHERE wo.deleted = 0 ",
        "<if test='category != null and category != \"\"'>",
        "AND wo.category = #{category} ",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND wo.status = #{status} ",
        "</if>",
        "<if test='submitterId != null'>",
        "AND wo.submitter_id = #{submitterId} ",
        "</if>",
        "<if test='assigneeId != null'>",
        "AND wo.assignee_id = #{assigneeId} ",
        "</if>",
        "ORDER BY wo.create_time DESC",
        "</script>"
    })
    IPage<WorkOrder> selectPageWithDetails(Page<WorkOrder> page, 
                                          @Param("category") String category,
                                          @Param("status") String status,
                                          @Param("submitterId") Long submitterId,
                                          @Param("assigneeId") Long assigneeId);

    /**
     * 生成工单编号
     * 
     * @return 工单编号
     */
    @Select("SELECT CONCAT('WO', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(IFNULL(MAX(SUBSTRING(order_no, 11)), 0) + 1, 4, '0')) " +
            "FROM work_order WHERE order_no LIKE CONCAT('WO', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generateOrderNo();

    /**
     * 根据分配人查询待处理工单数量
     * 
     * @param assigneeId 分配人ID
     * @return 工单数量
     */
    @Select("SELECT COUNT(*) FROM work_order WHERE assignee_id = #{assigneeId} AND status IN ('PENDING', 'PROCESSING') AND deleted = 0")
    int countPendingByAssignee(@Param("assigneeId") Long assigneeId);

    /**
     * 根据提交人查询工单统计
     * 
     * @param submitterId 提交人ID
     * @return 工单统计
     */
    @Select("SELECT status, COUNT(*) as count FROM work_order WHERE submitter_id = #{submitterId} AND deleted = 0 GROUP BY status")
    List<Object> countBySubmitter(@Param("submitterId") Long submitterId);

    /**
     * 根据ID查询工单详情（带关联信息）
     * 
     * @param id 工单ID
     * @return 工单详情
     */
    @Select({
        "SELECT wo.*, ",
        "su.real_name as submitter_name, ",
        "su.phone as submitter_phone, ",
        "au.real_name as assignee_name, ",
        "CONCAT(b.building_name, '-', u.unit_name, '-', r.room_no) as room_address ",
        "FROM work_order wo ",
        "LEFT JOIN sys_user su ON wo.submitter_id = su.id ",
        "LEFT JOIN sys_user au ON wo.assignee_id = au.id ",
        "LEFT JOIN room r ON wo.room_id = r.id ",
        "LEFT JOIN unit u ON r.unit_id = u.id ",
        "LEFT JOIN building b ON r.building_id = b.id ",
        "WHERE wo.id = #{id} AND wo.deleted = 0"
    })
    WorkOrder selectByIdWithDetails(@Param("id") Long id);
}







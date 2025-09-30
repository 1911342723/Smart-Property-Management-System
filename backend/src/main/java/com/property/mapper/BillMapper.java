package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账单Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 分页查询账单（带关联信息）
     * 
     * @param page 分页对象
     * @param billType 账单类型
     * @param status 账单状态
     * @param ownerId 业主ID
     * @param billingPeriod 计费周期
     * @return 账单列表
     */
    @Select({
        "<script>",
        "SELECT b.*, ",
        "u.real_name as owner_name, ",
        "CONCAT(bd.building_name, '-', un.unit_name, '-', r.room_no) as room_address ",
        "FROM bill b ",
        "LEFT JOIN sys_user u ON b.owner_id = u.id ",
        "LEFT JOIN room r ON b.room_id = r.id ",
        "LEFT JOIN unit un ON r.unit_id = un.id ",
        "LEFT JOIN building bd ON r.building_id = bd.id ",
        "WHERE b.deleted = 0 ",
        "<if test='billType != null and billType != \"\"'>",
        "AND b.bill_type = #{billType} ",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "AND b.status IN ",
        "<foreach item='item' index='index' collection='status.split(\",\")' open='(' separator=',' close=')'>",
        "#{item}",
        "</foreach>",
        "</if>",
        "<if test='ownerId != null'>",
        "AND b.owner_id = #{ownerId} ",
        "</if>",
        "<if test='billingPeriod != null and billingPeriod != \"\"'>",
        "AND b.billing_period = #{billingPeriod} ",
        "</if>",
        "ORDER BY b.create_time DESC",
        "</script>"
    })
    IPage<Bill> selectPageWithDetails(Page<Bill> page,
                                     @Param("billType") String billType,
                                     @Param("status") String status,
                                     @Param("ownerId") Long ownerId,
                                     @Param("billingPeriod") String billingPeriod);

    /**
     * 生成账单编号
     * 
     * @return 账单编号
     */
    @Select("SELECT CONCAT('BILL', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(IFNULL(MAX(SUBSTRING(bill_no, 13)), 0) + 1, 3, '0')) " +
            "FROM bill WHERE bill_no LIKE CONCAT('BILL', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generateBillNo();

    /**
     * 查询不同的楼栋ID
     * 
     * @return 楼栋ID列表
     */
    @Select("SELECT DISTINCT r.building_id FROM room r WHERE r.deleted = 0 ORDER BY r.building_id")
    List<Long> selectDistinctBuildingIds();
}




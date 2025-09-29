package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 公告Mapper接口
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 分页查询公告列表（带发布人信息）
     */
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.deleted = 0 " +
            "AND (#{type} IS NULL OR a.type = #{type}) " +
            "AND (#{status} IS NULL OR a.status = #{status}) " +
            "AND (#{keyword} IS NULL OR a.title LIKE CONCAT('%', #{keyword}, '%') OR a.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY a.is_top DESC, a.publish_time DESC")
    IPage<Announcement> selectAnnouncementPage(
            Page<Announcement> page,
            @Param("type") String type,
            @Param("status") String status,
            @Param("keyword") String keyword
    );

    /**
     * 查询已发布的公告列表（小程序用）
     */
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.deleted = 0 " +
            "AND a.status = 'PUBLISHED' " +
            "AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{limit}")
    List<Announcement> selectPublishedAnnouncements(@Param("limit") Integer limit);

    /**
     * 查询置顶公告
     */
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.deleted = 0 " +
            "AND a.status = 'PUBLISHED' " +
            "AND a.is_top = 1 " +
            "AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "ORDER BY a.publish_time DESC")
    List<Announcement> selectTopAnnouncements();

    /**
     * 根据类型查询公告
     */
    @Select("SELECT a.*, u.real_name as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.deleted = 0 " +
            "AND a.status = 'PUBLISHED' " +
            "AND a.type = #{type} " +
            "AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "ORDER BY a.is_top DESC, a.publish_time DESC " +
            "LIMIT #{limit}")
    List<Announcement> selectAnnouncementsByType(
            @Param("type") String type,
            @Param("limit") Integer limit
    );

    /**
     * 增加阅读次数
     */
    @Update("UPDATE announcement SET read_count = read_count + 1 WHERE id = #{id}")
    int incrementReadCount(@Param("id") Long id);

    /**
     * 批量更新公告状态
     */
    @Update("UPDATE announcement SET status = #{status} WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 查询公告统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN status = 'PUBLISHED' THEN 1 ELSE 0 END) as published, " +
            "SUM(CASE WHEN is_top = 1 THEN 1 ELSE 0 END) as pinned, " +
            "SUM(read_count) as totalViews " +
            "FROM announcement " +
            "WHERE deleted = 0")
    AnnouncementStats selectAnnouncementStats();

    /**
     * 统计信息内部类
     */
    class AnnouncementStats {
        private Integer total;
        private Integer published;
        private Integer pinned;
        private Long totalViews;

        // Getters and Setters
        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getPublished() {
            return published;
        }

        public void setPublished(Integer published) {
            this.published = published;
        }

        public Integer getPinned() {
            return pinned;
        }

        public void setPinned(Integer pinned) {
            this.pinned = pinned;
        }

        public Long getTotalViews() {
            return totalViews;
        }

        public void setTotalViews(Long totalViews) {
            this.totalViews = totalViews;
        }
    }
}





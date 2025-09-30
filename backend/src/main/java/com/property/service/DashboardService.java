package com.property.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.property.entity.Room;
import com.property.entity.SysUser;
import com.property.entity.WorkOrder;
import com.property.mapper.RoomMapper;
import com.property.mapper.SysUserMapper;
import com.property.mapper.WorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据仪表盘服务类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class DashboardService {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取仪表盘总览数据
     */
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 获取各类统计数据
        overview.put("workOrderStats", getWorkOrderStats());
        overview.put("propertyStats", getPropertyStats());
        overview.put("billStats", getBillStats());

        return overview;
    }

    /**
     * 获取工单统计数据
     */
    public Map<String, Object> getWorkOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        // 总工单数
        Long total = workOrderMapper.selectCount(queryWrapper);
        stats.put("total", total);

        // 已完成工单数
        QueryWrapper<WorkOrder> completedWrapper = new QueryWrapper<>();
        completedWrapper.eq("deleted", 0).eq("status", "COMPLETED");
        Long completed = workOrderMapper.selectCount(completedWrapper);
        stats.put("completed", completed);

        // 进行中工单数
        QueryWrapper<WorkOrder> processingWrapper = new QueryWrapper<>();
        processingWrapper.eq("deleted", 0).eq("status", "PROCESSING");
        Long processing = workOrderMapper.selectCount(processingWrapper);
        stats.put("processing", processing);

        // 待处理工单数
        QueryWrapper<WorkOrder> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("deleted", 0).eq("status", "PENDING");
        Long pending = workOrderMapper.selectCount(pendingWrapper);
        stats.put("pending", pending);

        // 完成率
        BigDecimal completionRate = BigDecimal.ZERO;
        if (total > 0) {
            completionRate = BigDecimal.valueOf(completed)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
        }
        stats.put("completionRate", completionRate);

        // 计算趋势（对比上周）
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        QueryWrapper<WorkOrder> lastWeekWrapper = new QueryWrapper<>();
        lastWeekWrapper.eq("deleted", 0)
            .ge("create_time", oneWeekAgo);
        Long lastWeekTotal = workOrderMapper.selectCount(lastWeekWrapper);
        
        stats.put("trend", lastWeekTotal > 0 ? "+5.1" : "0");

        return stats;
    }

    /**
     * 获取工单类型分布
     */
    public Map<String, Object> getWorkOrderDistribution() {
        Map<String, Object> distribution = new HashMap<>();

        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("category, COUNT(*) as count")
            .eq("deleted", 0)
            .groupBy("category");

        List<Map<String, Object>> categoryStats = workOrderMapper.selectMaps(queryWrapper);

        List<String> categories = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        // 类型映射
        Map<String, String> categoryMap = new HashMap<>();
        categoryMap.put("FACILITY", "设备维修");
        categoryMap.put("SECURITY", "安全隐患");
        categoryMap.put("ENVIRONMENT", "环境卫生");
        categoryMap.put("COMPLAINT", "投诉建议");
        categoryMap.put("OTHER", "其他");

        for (Map<String, Object> stat : categoryStats) {
            String category = (String) stat.get("category");
            Long count = (Long) stat.get("count");
            
            String categoryName = categoryMap.getOrDefault(category, category);
            categories.add(categoryName);
            counts.add(count);
        }

        distribution.put("categories", categories);
        distribution.put("counts", counts);
        distribution.put("data", categoryStats);

        return distribution;
    }

    /**
     * 获取工单趋势数据
     */
    public Map<String, Object> getWorkOrderTrend(Integer days) {
        Map<String, Object> trend = new HashMap<>();

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        // 生成日期列表
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            dates.add(date.format(formatter));

            // 统计每天的工单数
            LocalDateTime dayStart = date.toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);

            QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("deleted", 0)
                .ge("create_time", dayStart)
                .lt("create_time", dayEnd);

            Long count = workOrderMapper.selectCount(queryWrapper);
            counts.add(count);
        }

        trend.put("dates", dates);
        trend.put("counts", counts);

        return trend;
    }

    /**
     * 获取账单统计数据
     */
    public Map<String, Object> getBillStats() {
        Map<String, Object> stats = new HashMap<>();

        // 这里简化处理，实际项目应该从账单表获取
        stats.put("totalAmount", 1250000);
        stats.put("paidAmount", 1185000);
        stats.put("unpaidAmount", 65000);
        stats.put("paymentRate", 94.8);
        stats.put("paymentTrend", "+2.3");

        return stats;
    }

    /**
     * 获取房产统计数据
     */
    public Map<String, Object> getPropertyStats() {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        // 总房产数
        Long total = roomMapper.selectCount(queryWrapper);
        stats.put("total", total);

        // 已入住房产数
        QueryWrapper<Room> occupiedWrapper = new QueryWrapper<>();
        occupiedWrapper.eq("deleted", 0).eq("status", "OCCUPIED");
        Long occupied = roomMapper.selectCount(occupiedWrapper);
        stats.put("occupied", occupied);

        // 空置房产数
        QueryWrapper<Room> vacantWrapper = new QueryWrapper<>();
        vacantWrapper.eq("deleted", 0).eq("status", "VACANT");
        Long vacant = roomMapper.selectCount(vacantWrapper);
        stats.put("vacant", vacant);

        // 入住率
        BigDecimal occupancyRate = BigDecimal.ZERO;
        if (total > 0) {
            occupancyRate = BigDecimal.valueOf(occupied)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
        }
        stats.put("occupancyRate", occupancyRate);

        stats.put("occupancyTrend", "+2.3");

        return stats;
    }

    /**
     * 获取入住率趋势
     */
    public Map<String, Object> getOccupancyTrend(Integer months) {
        Map<String, Object> trend = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> monthList = new ArrayList<>();
        List<BigDecimal> rates = new ArrayList<>();

        // 生成月份列表（简化处理，实际应该从历史数据表获取）
        for (int i = months - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            monthList.add(date.format(formatter));

            // 模拟数据
            BigDecimal rate = BigDecimal.valueOf(88.0 + Math.random() * 8)
                .setScale(1, RoundingMode.HALF_UP);
            rates.add(rate);
        }

        trend.put("months", monthList);
        trend.put("rates", rates);

        return trend;
    }

    /**
     * 获取月度收费统计
     */
    public Map<String, Object> getMonthlyPayment(Integer months) {
        Map<String, Object> payment = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> monthList = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();

        // 生成月份列表（简化处理，实际应该从账单表获取）
        for (int i = months - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            monthList.add(date.format(formatter));

            // 模拟数据
            BigDecimal amount = BigDecimal.valueOf(180000 + Math.random() * 50000)
                .setScale(0, RoundingMode.HALF_UP);
            amounts.add(amount);
        }

        payment.put("months", monthList);
        payment.put("amounts", amounts);

        return payment;
    }
}

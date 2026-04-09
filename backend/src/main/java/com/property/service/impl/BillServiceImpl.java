package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.Bill;
import com.property.entity.Payment;
import com.property.mapper.BillMapper;
import com.property.mapper.RoomMapper;
import com.property.service.BillService;
import com.property.service.PaymentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 账单服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RoomMapper roomMapper;

    private List<String> getManagedBillTypes() {
        return Arrays.asList("PROPERTY", "PARKING", "PROPERTY_FEE", "PARKING_FEE");
    }

    @Override
    public PageResult<Bill> getBillPage(int pageNum, int pageSize, 
                                       String billType, String status, 
                                       Long ownerId, String billingPeriod) {
        Page<Bill> page = new Page<>(pageNum, pageSize);
        IPage<Bill> result = baseMapper.selectPageWithDetails(page, billType, status, ownerId, billingPeriod);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    @Override
    @Transactional
    public boolean createBill(Bill bill) {
        // 生成账单编号
        if (bill.getBillNo() == null || bill.getBillNo().isEmpty()) {
            bill.setBillNo(generateBillNo());
        }
        
        // 设置默认值
        if (bill.getStatus() == null) {
            bill.setStatus("UNPAID");
        }
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        
        return this.save(bill);
    }

    @Override
    @Transactional
    public boolean createBillsBatch(List<Bill> bills) {
        for (Bill bill : bills) {
            // 生成账单编号
            if (bill.getBillNo() == null || bill.getBillNo().isEmpty()) {
                bill.setBillNo(generateBillNo());
            }
            
            // 设置默认值
            if (bill.getStatus() == null) {
                bill.setStatus("UNPAID");
            }
            if (bill.getPaidAmount() == null) {
                bill.setPaidAmount(BigDecimal.ZERO);
            }
        }
        
        return this.saveBatch(bills);
    }

    @Override
    @Transactional
    public boolean payBill(Long billId, BigDecimal paymentAmount, String paymentMethod) {
        Bill bill = this.getById(billId);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        
        if (!"UNPAID".equals(bill.getStatus()) && !"OVERDUE".equals(bill.getStatus())) {
            throw new RuntimeException("账单状态不正确，无法缴费");
        }
        
        // 更新账单状态
        bill.setPaidAmount(paymentAmount);
        bill.setStatus("PAID");
        bill.setPaidDate(LocalDateTime.now());
        
        boolean billUpdated = this.updateById(bill);
        
        if (billUpdated) {
            // 创建支付记录
            Payment payment = new Payment();
            payment.setBillId(billId);
            payment.setOwnerId(bill.getOwnerId());
            payment.setAmount(paymentAmount);
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus("SUCCESS");
            payment.setPaymentTime(LocalDateTime.now());
            payment.setRemark("账单缴费");
            
            paymentService.createPayment(payment);
        }
        
        return billUpdated;
    }

    @Override
    @Transactional
    public boolean payBillsBatch(List<Long> billIds, String paymentMethod) {
        for (Long billId : billIds) {
            Bill bill = this.getById(billId);
            if (bill == null) {
                continue;
            }
            
            if (!"UNPAID".equals(bill.getStatus()) && !"OVERDUE".equals(bill.getStatus())) {
                continue;
            }
            
            // 更新账单状态
            bill.setPaidAmount(bill.getAmount());
            bill.setStatus("PAID");
            bill.setPaidDate(LocalDateTime.now());
            
            boolean billUpdated = this.updateById(bill);
            
            if (billUpdated) {
                // 创建支付记录
                Payment payment = new Payment();
                payment.setBillId(billId);
                payment.setOwnerId(bill.getOwnerId());
                payment.setAmount(bill.getAmount());
                payment.setPaymentMethod(paymentMethod);
                payment.setStatus("SUCCESS");
                payment.setPaymentTime(LocalDateTime.now());
                payment.setRemark("批量账单缴费");
                
                paymentService.createPayment(payment);
            }
        }
        
        return true;
    }

    @Override
    public BillStats getBillStats(Long ownerId) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("bill_type", getManagedBillTypes());
        if (ownerId != null) {
            queryWrapper.eq("owner_id", ownerId);
        }
        queryWrapper.eq("deleted", 0);
        
        List<Bill> allBills = this.list(queryWrapper);
        
        BillStats stats = new BillStats();
        
        if (allBills.isEmpty()) {
            stats.setPaymentRate(0);
            stats.setPaymentTrend(0);
            stats.setTotalBills(0);
            stats.setPaidBills(0);
            stats.setTotalAmount(BigDecimal.ZERO);
            stats.setPaidAmount(BigDecimal.ZERO);
            return stats;
        }
        
        // 统计总账单数
        stats.setTotalBills(allBills.size());
        
        // 统计已缴费账单数
        int paidCount = (int) allBills.stream()
                .filter(bill -> "PAID".equals(bill.getStatus()))
                .count();
        stats.setPaidBills(paidCount);
        
        // 计算缴费率
        double paymentRate = (stats.getTotalBills() > 0) ? 
                (paidCount * 100.0 / stats.getTotalBills()) : 0;
        stats.setPaymentRate(Math.round(paymentRate * 10) / 10.0);
        
        // 模拟趋势（实际应该对比上个月数据）
        stats.setPaymentTrend(0);
        
        // 计算总金额
        BigDecimal totalAmount = allBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount);
        
        // 计算已缴金额
        BigDecimal paidAmount = allBills.stream()
                .filter(bill -> "PAID".equals(bill.getStatus()))
                .map(Bill::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setPaidAmount(paidAmount);
        
        return stats;
    }

    @Override
    public BillSummary getBillSummary(Long ownerId) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", ownerId)
                   .in("bill_type", getManagedBillTypes())
                   .eq("status", "UNPAID")
                   .eq("deleted", 0);
        
        List<Bill> unpaidBills = this.list(queryWrapper);
        
        BillSummary summary = new BillSummary();
        
        if (unpaidBills.isEmpty()) {
            summary.setTotalAmount(BigDecimal.ZERO);
            summary.setUnpaidCount(0);
            summary.setOverdueCount(0);
            summary.setNearestDueDate(null);
            return summary;
        }
        
        // 计算总金额
        BigDecimal totalAmount = unpaidBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalAmount(totalAmount);
        
        // 计算待缴费用数量
        summary.setUnpaidCount(unpaidBills.size());
        
        // 计算逾期费用数量
        LocalDate today = LocalDate.now();
        int overdueCount = (int) unpaidBills.stream()
                .filter(bill -> bill.getDueDate() != null && bill.getDueDate().isBefore(today))
                .count();
        summary.setOverdueCount(overdueCount);
        
        // 获取最近到期日期
        unpaidBills.stream()
                .filter(bill -> bill.getDueDate() != null)
                .min((b1, b2) -> b1.getDueDate().compareTo(b2.getDueDate()))
                .ifPresent(bill -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
                    summary.setNearestDueDate(bill.getDueDate().format(formatter));
                });
        
        return summary;
    }

    /**
     * 生成账单编号
     * 
     * @return 账单编号
     */
    private String generateBillNo() {
        String billNo = baseMapper.generateBillNo();
        return billNo != null ? billNo : "BILL" + System.currentTimeMillis();
    }

    @Override
    public void exportBillList(String billType, String status, Long ownerId, String billingPeriod, HttpServletResponse response) throws IOException {
        // 查询符合条件的所有账单（不分页）
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.in("bill_type", getManagedBillTypes());
        
        if (StringUtils.hasText(billType)) {
            queryWrapper.eq("bill_type", billType);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        if (ownerId != null) {
            queryWrapper.eq("owner_id", ownerId);
        }
        if (StringUtils.hasText(billingPeriod)) {
            queryWrapper.eq("billing_period", billingPeriod);
        }
        
        queryWrapper.orderByDesc("create_time");
        List<Bill> bills = baseMapper.selectList(queryWrapper);
        
        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("账单列表");
        
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
        String[] headers = {"账单编号", "业主ID", "账单类型", "计费周期", "应缴金额", "实缴金额", "状态", "账单日期", "到期日期", "缴费日期", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4500);
        }
        
        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int rowNum = 1;
        for (Bill bill : bills) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(bill.getBillNo() != null ? bill.getBillNo() : "");
            row.createCell(1).setCellValue(bill.getOwnerId() != null ? bill.getOwnerId() : 0);
            row.createCell(2).setCellValue(bill.getBillType() != null ? bill.getBillType() : "");
            row.createCell(3).setCellValue(bill.getBillingPeriod() != null ? bill.getBillingPeriod() : "");
            row.createCell(4).setCellValue(bill.getAmount() != null ? bill.getAmount().doubleValue() : 0);
            row.createCell(5).setCellValue(bill.getPaidAmount() != null ? bill.getPaidAmount().doubleValue() : 0);
            row.createCell(6).setCellValue(bill.getStatus() != null ? bill.getStatus() : "");
            row.createCell(7).setCellValue(bill.getCreateTime() != null ? bill.getCreateTime().format(formatter) : "");
            row.createCell(8).setCellValue(bill.getDueDate() != null ? bill.getDueDate().format(dateFormatter) : "");
            row.createCell(9).setCellValue(bill.getPaidDate() != null ? bill.getPaidDate().format(formatter) : "");
            row.createCell(10).setCellValue(bill.getDescription() != null ? bill.getDescription() : "");
        }
        
        // 设置响应头
        String fileName = "账单列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    @Override
    public void exportFinanceReport(String reportType, String timeRange, String building, HttpServletResponse response) throws IOException {
        // 查询账单数据用于统计
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.in("bill_type", getManagedBillTypes());
        
        // 根据时间范围过滤（简化处理，实际应该根据timeRange计算日期范围）
        if (StringUtils.hasText(building)) {
            queryWrapper.eq("building", building);
        }
        
        queryWrapper.orderByDesc("create_time");
        List<Bill> bills = baseMapper.selectList(queryWrapper);
        
        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("财务报表");
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        // 根据报表类型生成不同内容
        if ("income".equals(reportType)) {
            // 收入报表
            Row headerRow = sheet.createRow(0);
            String[] headers = {"账单类型", "账单数量", "应收金额", "实收金额", "收缴率(%)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }
            
            // 按类型统计
            java.util.Map<String, java.util.List<Bill>> billsByType = bills.stream()
                .collect(java.util.stream.Collectors.groupingBy(Bill::getBillType));
            
            int rowNum = 1;
            BigDecimal totalReceivable = BigDecimal.ZERO;
            BigDecimal totalReceived = BigDecimal.ZERO;
            
            for (java.util.Map.Entry<String, java.util.List<Bill>> entry : billsByType.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                List<Bill> typeBills = entry.getValue();
                
                BigDecimal receivable = typeBills.stream()
                    .map(Bill::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal received = typeBills.stream()
                    .map(bill -> bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                double rate = receivable.doubleValue() > 0 ? 
                    (received.doubleValue() / receivable.doubleValue() * 100) : 0;
                
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(typeBills.size());
                row.createCell(2).setCellValue(receivable.doubleValue());
                row.createCell(3).setCellValue(received.doubleValue());
                row.createCell(4).setCellValue(rate);
                
                totalReceivable = totalReceivable.add(receivable);
                totalReceived = totalReceived.add(received);
            }
            
            // 添加总计行
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("总计");
            totalRow.createCell(1).setCellValue(bills.size());
            totalRow.createCell(2).setCellValue(totalReceivable.doubleValue());
            totalRow.createCell(3).setCellValue(totalReceived.doubleValue());
            double totalRate = totalReceivable.doubleValue() > 0 ? 
                (totalReceived.doubleValue() / totalReceivable.doubleValue() * 100) : 0;
            totalRow.createCell(4).setCellValue(totalRate);
            
        } else {
            // 其他类型报表，使用通用格式
            Row headerRow = sheet.createRow(0);
            String[] headers = {"账单编号", "账单类型", "应缴金额", "实缴金额", "状态", "账单日期", "到期日期"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4500);
            }
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            int rowNum = 1;
            for (Bill bill : bills) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(bill.getBillNo() != null ? bill.getBillNo() : "");
                row.createCell(1).setCellValue(bill.getBillType() != null ? bill.getBillType() : "");
                row.createCell(2).setCellValue(bill.getAmount() != null ? bill.getAmount().doubleValue() : 0);
                row.createCell(3).setCellValue(bill.getPaidAmount() != null ? bill.getPaidAmount().doubleValue() : 0);
                row.createCell(4).setCellValue(bill.getStatus() != null ? bill.getStatus() : "");
                row.createCell(5).setCellValue(bill.getCreateTime() != null ? bill.getCreateTime().toLocalDate().format(dateFormatter) : "");
                row.createCell(6).setCellValue(bill.getDueDate() != null ? bill.getDueDate().format(dateFormatter) : "");
            }
        }
        
        // 设置响应头
        String reportTypeName = reportType != null ? reportType : "综合";
        String fileName = "财务报表_" + reportTypeName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    @Override
    public List<BillService.BuildingStats> getBuildingStats(String billingPeriod) {
        // 查询所有楼栋（通过room表的building_id去重）
        List<Long> buildingIds = baseMapper.selectDistinctBuildingIds();
        
        List<BillService.BuildingStats> statsList = new java.util.ArrayList<>();
        
        for (Long buildingId : buildingIds) {
            BillService.BuildingStats stats = new BillService.BuildingStats();
            
            // 查询该楼栋的房屋数
            QueryWrapper<com.property.entity.Room> roomWrapper = new QueryWrapper<>();
            roomWrapper.eq("building_id", buildingId)
                      .eq("deleted", 0);
            long totalHouses = roomMapper.selectCount(roomWrapper);
            
            // 查询该楼栋的账单数据
            QueryWrapper<Bill> billWrapper = new QueryWrapper<>();
            billWrapper.eq("deleted", 0);
            billWrapper.in("bill_type", getManagedBillTypes());
            
            // 通过子查询关联room表筛选楼栋
            billWrapper.inSql("room_id", 
                "SELECT id FROM room WHERE building_id = " + buildingId + " AND deleted = 0");
            
            if (StringUtils.hasText(billingPeriod)) {
                billWrapper.eq("billing_period", billingPeriod);
            }
            
            List<Bill> bills = baseMapper.selectList(billWrapper);
            
            // 统计数据
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal paidAmount = BigDecimal.ZERO;
            int paidCount = 0;
            
            for (Bill bill : bills) {
                totalAmount = totalAmount.add(bill.getAmount());
                if (bill.getPaidAmount() != null) {
                    paidAmount = paidAmount.add(bill.getPaidAmount());
                }
                if ("PAID".equals(bill.getStatus())) {
                    paidCount++;
                }
            }
            
            // 设置统计数据
            stats.setBuildingId(buildingId);
            stats.setBuilding(buildingId + "号楼");
            stats.setTotalHouses((int) totalHouses);
            stats.setPaidHouses(paidCount);
            stats.setUnpaidHouses((int) totalHouses - paidCount);
            stats.setTotalAmount(totalAmount);
            stats.setPaidAmount(paidAmount);
            stats.setUnpaidAmount(totalAmount.subtract(paidAmount));
            
            // 计算缴费率
            double rate = totalHouses > 0 ? 
                (paidCount * 100.0 / totalHouses) : 0;
            stats.setPaymentRate(rate);
            
            statsList.add(stats);
        }
        
        return statsList;
    }
}

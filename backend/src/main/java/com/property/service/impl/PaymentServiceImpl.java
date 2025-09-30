package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.PageResult;
import com.property.entity.Payment;
import com.property.mapper.PaymentMapper;
import com.property.service.PaymentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 支付记录服务实现类
 * 
 * @author Property Management Team
 * @since 1.0.0
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Override
    public PageResult<Payment> getPaymentPage(int pageNum, int pageSize, 
                                             Long ownerId, String paymentMethod, String status) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        IPage<Payment> result = baseMapper.selectPageWithDetails(page, ownerId, paymentMethod, status);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), 
                               Long.valueOf(pageNum), Long.valueOf(pageSize));
    }

    @Override
    @Transactional
    public boolean createPayment(Payment payment) {
        // 生成支付编号
        if (payment.getPaymentNo() == null || payment.getPaymentNo().isEmpty()) {
            payment.setPaymentNo(generatePaymentNo());
        }
        
        // 设置默认值
        if (payment.getStatus() == null) {
            payment.setStatus("SUCCESS");
        }
        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        
        return this.save(payment);
    }

    /**
     * 生成支付编号
     * 
     * @return 支付编号
     */
    private String generatePaymentNo() {
        String paymentNo = baseMapper.generatePaymentNo();
        return paymentNo != null ? paymentNo : "PAY" + System.currentTimeMillis();
    }

    @Override
    public void exportPaymentList(Long ownerId, String paymentMethod, String status, HttpServletResponse response) throws IOException {
        // 查询符合条件的所有缴费记录（不分页）
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        
        if (ownerId != null) {
            queryWrapper.eq("owner_id", ownerId);
        }
        if (StringUtils.hasText(paymentMethod)) {
            queryWrapper.eq("payment_method", paymentMethod);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("payment_time");
        List<Payment> payments = baseMapper.selectList(queryWrapper);
        
        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("缴费记录");
        
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
        String[] headers = {"缴费编号", "账单编号", "业主ID", "缴费金额", "支付方式", "支付状态", "缴费时间", "交易流水号", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4500);
        }
        
        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (Payment payment : payments) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(payment.getPaymentNo() != null ? payment.getPaymentNo() : "");
            row.createCell(1).setCellValue(payment.getBillNo() != null ? payment.getBillNo() : "");
            row.createCell(2).setCellValue(payment.getOwnerId() != null ? payment.getOwnerId() : 0);
            row.createCell(3).setCellValue(payment.getAmount() != null ? payment.getAmount().doubleValue() : 0);
            row.createCell(4).setCellValue(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "");
            row.createCell(5).setCellValue(payment.getStatus() != null ? payment.getStatus() : "");
            row.createCell(6).setCellValue(payment.getPaymentTime() != null ? payment.getPaymentTime().format(formatter) : "");
            row.createCell(7).setCellValue(payment.getTransactionId() != null ? payment.getTransactionId() : "");
            row.createCell(8).setCellValue(payment.getRemark() != null ? payment.getRemark() : "");
        }
        
        // 设置响应头
        String fileName = "缴费记录_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }
}







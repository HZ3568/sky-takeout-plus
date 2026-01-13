package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = getDateList(begin, end);

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = orderMapper.turnoverStatistics(beginTime, endTime, Orders.COMPLETED);
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }
//        String dateStr = String.join(",", dateList.toString());
//        String turnoverStr = String.join(",", turnoverList.toString());
        return new TurnoverReportVO(dateList.toString(), turnoverList.toString());
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = getDateList(begin, end);

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> allUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            int newUser = userMapper.getUserStatistics(endTime, beginTime);
            int allUser = userMapper.getUserStatistics(endTime);
            newUserList.add(newUser);
            allUserList.add(allUser);
        }
        return new UserReportVO(dateList.toString(), allUserList.toString(), newUserList.toString());
    }

    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int totalValidOrderCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            int orderCount = orderMapper.ordersStatistics(beginTime, endTime);
            int validOrderCount = orderMapper.ordersStatistics(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
            totalOrderCount += orderCount;
            totalValidOrderCount += validOrderCount;
        }

        return new OrderReportVO(
                dateList.toString(), orderCountList.toString(), validOrderCountList.toString(),
                totalOrderCount, totalValidOrderCount,
                totalOrderCount != 0 ? ((double) totalValidOrderCount / totalOrderCount) : 0.0
        );
    }

    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesList = orderMapper.top10(beginTime, endTime, Orders.COMPLETED);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : salesList) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        return new SalesTop10ReportVO(nameList.toString(), numberList.toString());
    }

    public void export(HttpServletResponse response) {
        // 1.获取营业数据(近30天)
        LocalDateTime begin = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);


        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            // 2.通过POI将数据写入Excel
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).
                    setCellValue(LocalDate.now().minusDays(30).toString() + "至" + LocalDate.now().minusDays(1).toString());
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                begin = LocalDateTime.of(LocalDate.now().minusDays(30 - i), LocalTime.MIN);
                end = LocalDateTime.of(LocalDate.now().minusDays(30 - i), LocalTime.MAX);
                businessDataVO = workspaceService.getBusinessData(begin, end);
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(LocalDate.now().minusDays(30 - i).toString());
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            }

            // 3.通过输出流将Excel写入到浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            in.close();
            out.close();
            excel.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = begin; !date.equals(end); date = date.plusDays(1)) {
            dateList.add(date);
        }
        dateList.add(end);
        return dateList;
    }
}

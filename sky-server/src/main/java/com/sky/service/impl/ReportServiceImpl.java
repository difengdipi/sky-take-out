package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/5
 **/
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //计算从开始日期到结束日期的具体天数
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while(!begin.equals(end)){
            //计算指定日期的后一天的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();

        //查询数据库，获取每天的营业额
        for (LocalDate date : dateList) {
            //根据日期查询营业额--》营业额是状态已完成的订单的合计
            LocalDateTime BeginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //Select sum(amount) from orders where order_time > BeginTime and order_time < endTime and status = 5 ;
            Map map = new HashMap();
            map.put("begin",BeginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double aDouble=orderMapper.SumByMap(map);
            //数据处理
            aDouble = aDouble == null ? 0.0 :aDouble;
            turnoverList.add(aDouble);

        }

        //封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList( StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //先计算日期
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        //新用户列表
        List<Integer> newUserList = new ArrayList<>();
        //截止到每一天的用户的总数量
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate localDate : localDateList) {
            LocalDateTime endTime = LocalDateTime.of(localDate,LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(localDate,LocalTime.MIN);
            //新用户的创建时间在这个区间为新用户
            //select sum(id) from user where ceate_time < ? and create_time > ?
            //统计总用户数量
            //select sum(id) from user where create_time < ?
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.CountByMap(map);

            map.put("begin",beginTime);
            Integer newUser = userMapper.CountByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);

        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //处理日期
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(begin);

        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        //每日订单数
        List<Integer> orderCountList = new ArrayList<>();
        //每日有效订单数-->完成的订单数
        List<Integer> validOrderCountList  = new ArrayList<>();
        //订单总数
        Integer totalOrderCount = 0;
        //有效订单数
        Integer validOrderCount = 0;

        Integer vali = 0;
        Integer orderCount = 0;

        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate,LocalTime.MAX);

            //Select count(id) from order where create_time < ? and create_time > ？ and status  = ?
            Map map = new HashMap();
            //订单总数
            totalOrderCount = orderMapper.CountByMap(map);
            //有效订单数
            map.put("status", Orders.COMPLETED);
            validOrderCount = orderMapper.CountByMap(map);
            map.clear();
            //每日订单数
            map.put("begin",beginTime);
            map.put("end",endTime);
            orderCount = orderMapper.CountByMap(map);
            orderCountList.add(orderCount);
            //每日有效订单数
            map.put("status", Orders.COMPLETED);
            vali = orderMapper.CountByMap(map);
            validOrderCountList.add(vali);

        }
            Double orderCompletionRate  = (double) (validOrderCount/totalOrderCount);
        return OrderReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询销量排名Top10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop = orderMapper.getSalesTop(begin, end);

        //对数据进行处理
        List<String> names = salesTop.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> number = salesTop.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numbers = StringUtils.join(number, ",");



        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numbers)
                .build();
    }

    /**
     * 导出运营数据报表
     * @return response
     */
    public void export(HttpServletResponse response) {
        //查询数据库获取数据-->获取最近30天的营业数据   LocalDate只有天，LocalDateTime 有秒
        LocalDate beginTime = LocalDate.now().minusDays(30);
        LocalDate endTime = LocalDate.now().minusDays(1);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(beginTime,LocalTime.MIN),LocalDateTime.of(endTime,LocalTime.MAX));
        //通过POI将数据写入到excel文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模版文件创建新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取excel的sheet标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充数据--时间--获取行--单元格
            sheet.getRow(1).getCell(1).setCellValue("时间"+beginTime+"至"+endTime);
            //填充数据---获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //循环填充数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = beginTime.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获得某一行
                row = sheet.getRow(i + 7);
                //获取单元格-->填充数据
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            }

            //通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            //关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

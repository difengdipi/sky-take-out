package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/5
 **/
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

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
}

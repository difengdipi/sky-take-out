package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/5
 **/
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

//    @GetMapping("/ordersStatistics")
//    @ApiOperation("订单统计")
//    public Result<OrderReportVO>  ordersStatistics(){
//        log.info("订单统计:{}", LocalDateTime.now());
//        OrderReportVO orderReportVO =  reportService.ordersStatistics();
//        return Result.success(orderReportVO);
//    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO>  turnoverStatistics(
            //日期格式的数据需要根据日期的格式设置特定的格式
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end){
        log.info("营业额统计:{},{}",begin,end);
        TurnoverReportVO turnoverReportVO =  reportService.turnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }


}
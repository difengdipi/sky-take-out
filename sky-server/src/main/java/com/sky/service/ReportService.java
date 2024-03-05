package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/5
 **/
public interface ReportService {
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);
}

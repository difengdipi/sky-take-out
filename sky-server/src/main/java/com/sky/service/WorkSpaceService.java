package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/3
 **/

public interface WorkSpaceService {
    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO SelectTodayDate();

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO ViewOrders();
}

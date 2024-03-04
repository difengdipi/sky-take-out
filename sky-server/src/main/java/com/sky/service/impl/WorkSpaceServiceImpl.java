package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/3
 **/
@Service
@Slf4j
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    public BusinessDataVO SelectTodayDate() {
        //新增的用户数-->现在的时间-24小时这段时间的第一次下单的用户就是新用户
        //TODO:查询今日运营数据
        return null;

    }

    /**
     * 查询订单管理数据
     * @return
     */
    public OrderOverViewVO ViewOrders() {
        //TODO:查询订单管理数据
       List<Orders> ordersList = orderMapper.SelectAll();
       OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
       orderOverViewVO.setAllOrders(ordersList.size());
//        for (Orders orders : ordersList) {
//           if(orders)
//        }
        return orderOverViewVO;
    }
}

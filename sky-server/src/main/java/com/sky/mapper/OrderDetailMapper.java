package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/2/29
 **/
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param orderDetailslist
     */
    void insertBatch(List<OrderDetail> orderDetailslist);
}

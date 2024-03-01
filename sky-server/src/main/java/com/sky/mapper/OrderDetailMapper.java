package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 根据orderId查找菜单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail  where order_id = #{orderId}")
    List<OrderDetail> selectByOrderId(String orderId);
    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}

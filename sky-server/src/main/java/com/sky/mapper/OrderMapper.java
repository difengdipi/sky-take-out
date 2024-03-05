package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/2/29
 **/
@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据id号查找订单信息
     * @param orderId
     * @return
     */
    @Select("select * from orders where id = #{orderId}")
    Orders SelectById(Long orderId);

    /**
     * 根据id号查找订单信息
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);



    /**
     * 根据订单状态查找订单
     * @return
     */
    @Select("select * from orders")
    List<Orders> SelectByStatus();

    /**
     * 根据订单Id修改订单的状态
     * @param ordersConfirmDTO
     */
    @Update("update  orders set status = #{ordersConfirmDTO.status} where id = #{ordersConfirmDTO.id}")
    void updateStatusById(OrdersConfirmDTO ordersConfirmDTO);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}" )
    List<Orders> getByStatusAndOrderTimeLT(Integer status,LocalDateTime orderTime);

    /**
     * 查询全部的订单信息
     * @return
     */
    @Select("select * from orders ")
    List<Orders> SelectAll();

    /**
     *  营业额统计
     * @param map
     * @return
     */
    Double SumByMap(Map map);

    /**
     * 订单统计
     * @param map
     * @return
     */
    Integer CountByMap(Map map);

    /**
     * 指定销量排名前十
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop(LocalDate begin, LocalDate end);
}

package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description : 用户订单
 * @author: 周海
 * @Create on : 2024/2/29
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;
    private Orders orders;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional//事务注解，保证数据可以操作一致性
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常（地址簿为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list.size() == 0 && list == null){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //1.向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        //设置为待付款状态
        orders.setStatus(orders.PENDING_PAYMENT);
        //使用时间戳+userID生成订单号
        orders.setNumber(String.valueOf(System.currentTimeMillis())+userId);

        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        //应该设置address否则页面回显不出地址信息
//        orders.setAddress(addressBook.get);
        orders.setUserId(userId);
        this.orders = orders ;
        List<OrderDetail> orderDetailslist = new ArrayList<>();

        orderMapper.insert(orders);

        //2.向订单明细表插入N条数据
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();//订单明细
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());//当前订单明细所关联的订单
            orderDetailslist.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailslist);
        //3.下单成功并且后，清空用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        //4.封装Vo返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        //TODO:交易有误

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderPaidStatus = Orders.PAID;//支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单
        LocalDateTime check_out_time = LocalDateTime.now();//更新支付时间
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, this.orders.getId());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 用户端订单分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }



    /**
     * 查询订单详情
     * @return
     */
    @Transactional
    public OrderVO OrderDetailByOrderId(Long orderId) {
            //1 根据id查询订单
            Orders orders = orderMapper.SelectById(orderId);

            // 2查询该订单对应的菜品/套餐明细
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

            // 3将该订单及其详情封装到OrderVO并返回

            AddressBook addressBook = addressBookMapper.SelectAddressByOrderID(orders.getAddressBookId());

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
            orderVO.setOrderDetailList(orderDetailList);

            return orderVO;
        }

    /**
     * 用户取消订单
     * @param id
     */
    public void cancel(Long id) {
        //取消订单的前提：1.该订单不在派送中 2.该订单未接单 3.联系管理员，通过管理员进行操作
        Orders orders = orderMapper.SelectById(id);
        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 订单处于待接单状态下取消，需要进行退款
        //支付状态修改为 退款
        orders.setPayStatus(Orders.REFUND);
        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

    }
    /**
     * 用户再来一单
     * @return
     */
    public void repetition(Long OrderId) {
        //根据订单Id查找订单详细
        List<OrderDetail> orderDetail = orderDetailMapper.SelectByOrderId(OrderId);
        //获取菜品和套餐信息，并放入购物车中
        //TODO：这里可以优化，调用Sql过多，效率低
        for(OrderDetail od : orderDetail){
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            BeanUtils.copyProperties(od,shoppingCart);
            System.out.println(shoppingCart);
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    public OrderStatisticsVO statistics() {
        //搜索各个状态的订单
        List<Orders> order = orderMapper.SelectByStatus();
        //将订单进行分类处理
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        for(Orders od:order){
            if(od.getStatus() == orders.TO_BE_CONFIRMED){
                //待接单的数量
                if(orderStatisticsVO.getToBeConfirmed() == null){
                    orderStatisticsVO.setToBeConfirmed(1);
                }else{
                    orderStatisticsVO.setToBeConfirmed(orderStatisticsVO.getToBeConfirmed()+1);
                }
            } else if (od.getStatus() == orders.CONFIRMED) {
                //已接单的数量
                if(orderStatisticsVO.getConfirmed() == null){
                    orderStatisticsVO.setConfirmed(1);
                }else {
                    orderStatisticsVO.setConfirmed(orderStatisticsVO.getConfirmed() + 1);
                }
            } else if (od.getStatus() ==orders.DELIVERY_IN_PROGRESS ) {
                //派送中的数量
               if(orderStatisticsVO.getDeliveryInProgress() == null){
                   orderStatisticsVO.setDeliveryInProgress(1);
               }else{
               orderStatisticsVO.setDeliveryInProgress(orderStatisticsVO.getDeliveryInProgress()+1);
               }
            }
        }
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {

        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param rejectionDTO
     * @return
     */
    public void rejection(OrdersRejectionDTO rejectionDTO) {

        Orders orders = Orders.builder()
                .id(rejectionDTO.getId())
                .rejectionReason(rejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .status(Orders.CANCELLED)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 管理员取消订单
     * @param ordersCancelDTO
     * @return
     */
    public void AdminCancel(OrdersCancelDTO ordersCancelDTO) {
        //修改订单的状态
        Orders orders = Orders.builder()
                .id(ordersCancelDTO.getId())
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .status(Orders.CANCELLED)
                .build();

        orderMapper.update(orders);
    }


    /**
     * 派送订单
     * @param id
     * @return
     */
    public void delivery(Long id) {
        //修改订单的状态即可
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(orders);

    }


    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }
    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }




}

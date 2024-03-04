package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Description :
 * @author: 周海
 * @Create on : 2024/2/29
 **/
@RestController("UserOrderController")
@RequestMapping("/user/order")
@Api(tags = "用户端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单，参数为:{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @ApiOperation("历史订单查询")
    @GetMapping("/historyOrders")
    public Result<PageResult> Page(int page, int pageSize, Integer status){
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);

    }
    /**
     * 查询订单详情
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public  Result<OrderVO>  OrderDetail(@PathVariable Long id){
        log.info("查询订单详情，订单id:{}",id);
        OrderVO orderVO = orderService.OrderDetailByOrderId(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public  Result  cancel(@PathVariable Long id){
        log.info("用户取消订单，订单ID:{}",id);
        orderService.cancel(id);
        return Result.success();
    }

    /**
     * 用户再来一单
     * @return
     */
    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public  Result repetition(@PathVariable Long id){
        log.info("用户再来一单，订单ID:{}",id);
        orderService.repetition(id);
        return Result.success();
    }


    @GetMapping("reminder/{id}")
    @ApiOperation("顾客催单")
    public Result  reminder(@PathVariable("id") Long id){
        log.info("顾客催单");
        orderService.reminder(id);
        return Result.success();
    }

}

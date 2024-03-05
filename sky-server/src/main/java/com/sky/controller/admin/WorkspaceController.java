package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description :
 * @author: 周海
 * @Create : 2024/3/3
 **/
@Slf4j
@RequestMapping("/admin/workspace")
@RestController
@Api(tags = "工作台相关接口")
public class WorkspaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> businessData(){
            log.info("查询今日运营数据");
            BusinessDataVO businessDataVO = workSpaceService.SelectTodayDate();
            return Result.success(businessDataVO);
    }

    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrders(){
        log.info("查询订单管理数据");
        OrderOverViewVO orderOverViewVO =  workSpaceService.ViewOrders();
        return Result.success(orderOverViewVO);

    }
}

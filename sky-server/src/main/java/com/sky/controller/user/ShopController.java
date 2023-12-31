package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @description：店铺相关接口
 * @author： 周海
 * @create： 2023/12/31
 */
@RestController("userShopController")
@Slf4j
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    private static final String kEY = "SHOP_STATUS";
    
    
    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer>  getStatus(){
        Integer status =(Integer) redisTemplate.opsForValue().get(kEY);
        
        log.info("获取店铺的营业状态为: {}",status ==1 ?"营业中":"打烊中");
        
        return Result.success(status);
    }
    
    
}

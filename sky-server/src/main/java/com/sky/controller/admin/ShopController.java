package com.sky.controller.admin;

import com.sky.config.RedisConfiguration;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @description：店铺相关接口
 * @author： 周海
 * @create： 2023/12/31
 */
@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    
    private static final String kEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 设置店铺的营业状态
     * @param status
     * @return
     */
    @ApiOperation("设置店铺的营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态为：{}",status == 1?"营业中":"打烊中");
        redisTemplate.opsForValue().set(kEY,status);
        
        return  Result.success();
    }
    
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

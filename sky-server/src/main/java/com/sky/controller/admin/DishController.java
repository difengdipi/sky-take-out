package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description：菜品管理
 * @author： 周海
 * @create： 2023/12/27
 */
@Slf4j
@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
   
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping("")
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品"+dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询"+dishPageQueryDTO);
        
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        
        return Result.success(pageResult);
    }
    
    @ApiOperation("菜品批量删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品"+ids);
        
        dishService.deleteBatch(ids);
        return  Result.success();
    }
    
    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    //PathVariable接收路径参数
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品"+id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    
    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品"+dishDTO);
        dishService.updatewithFlavor(dishDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售菜品")
    public Result status(@PathVariable Integer status,@RequestParam Long id){
        log.info("起售停售菜品"+status);
        
        dishService.status(id,status);
        return Result.success();
    }
    
}

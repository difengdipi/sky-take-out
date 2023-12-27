package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description：
 * @author： 周海
 * @create： 2023/12/27
 */
public interface DishService {
    
    /**
     * 新增菜品和相应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
    
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    
    /**
     * 菜品的批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}

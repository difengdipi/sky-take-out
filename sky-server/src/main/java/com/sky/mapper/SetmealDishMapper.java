package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description：
 * @author： 周海
 * @create： 2023/12/27
 */
@Mapper
public interface SetmealDishMapper {
   
    /**
     * 根据菜品ID查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealDishIds(List<Long> dishIds);
}

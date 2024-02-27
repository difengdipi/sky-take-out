package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
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
    
    /**
     * 批量插入菜品数据
     * @param setmealDishList
     */
    void insert(List<SetmealDish> setmealDishList);
    
    /**
     * 根据套餐id删除对应套餐菜品关系表中的数据
     * @param setmealid
     */
    void deleteByIds(List<Long> setmealid);
    /**
     * 新增套餐
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);
    /**
     * 根据套餐id删除套餐和菜品的关联关系
     *
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}

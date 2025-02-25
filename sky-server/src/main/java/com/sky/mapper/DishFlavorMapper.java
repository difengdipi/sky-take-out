package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description：口味
 * @author： 周海
 * @create： 2023/12/27
 */
@Mapper
public interface DishFlavorMapper {
   
    /**
     * 批量插入口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
    
    /**
     * 根据菜品id删除口味表中的数据
     * @param dishId
     */
    @Delete("delete  from dish_flavor where dish_id =#{dishId}")
    void deleteByDishId(Long dishId);
    
    /**
     * 根据菜品id批量删除口味表中的数据
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);
    
    @Select("select * from dish_flavor where dish_id = #{dishid}")
    List<DishFlavor> getByDishId(Long dishid);
}

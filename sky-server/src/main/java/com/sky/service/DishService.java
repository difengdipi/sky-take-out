package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
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
    
    /**
     * 根据菜品id查询对应的菜品和口味表
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);
    
    /**
     * 根据id修改菜品数据和口味信息
     * @param dishDTO
     */
    void updatewithFlavor(DishDTO dishDTO);
    
    /**
     * 根据id起售停售菜品
     * @param status
     */
    void status(Long id,Integer status);
    
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}

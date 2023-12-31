package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @description：套餐相应接口
 * @author： 周海
 * @create： 2023/12/27
 */
public interface SetmealService {
   
    /**
     * 套餐分页查询
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    
    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);
    
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);
}

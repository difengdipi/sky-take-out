package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;

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
}

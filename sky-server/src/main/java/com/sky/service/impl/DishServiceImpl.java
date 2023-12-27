package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description：
 * @author： 周海
 * @create： 2023/12/27
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    
    @Autowired
    private DishMapper dishMapper;
    
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    
    /**
     * 新增菜品和相应的口味
     * @param dishDTO
     */
    @Transactional//多表进行操作，保证进行原子行的操作，要么都成功要么都失败
    public void saveWithFlavor(DishDTO dishDTO) {
        //1.新增菜品，向菜品表中添加1条数据
        Dish dish = new Dish();
        
        BeanUtils.copyProperties(dishDTO,dish);
        
        dishMapper.insert(dish);
        
        //这里获取的是Mapper中insert语句生成的id(主键值)
        Long dishId = dish.getId();
        
        //2.向口味表中插入N条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        
        if(flavors != null && flavors.size() > 0){
            //拥有口味数据
           flavors.forEach(dishFlavor -> {
               dishFlavor.setDishId(dishId);
           });
            //1.批量插入口味
            dishFlavorMapper.insertBatch(flavors);
        }
        
    }
}

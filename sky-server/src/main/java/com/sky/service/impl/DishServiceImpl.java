package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    private SetmealDishMapper setmealDishMapper;
    
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
    
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
    
    /**
     * 菜品的批量删除
     * @param ids
     */
    @Transactional//进行多表删除，保证事务的一直性
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除---是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品出于起售中-->不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否被套餐关联
        List<Long> setmealDishIds = setmealDishMapper.getSetmealDishIds(ids);
        
        if(setmealDishIds !=null && setmealDishIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据--->对代码进行优化
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品表关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //根据菜品id集合批量删除菜品数据
        dishMapper.deleteByIds(ids);
        //根据菜品id集合批量删除关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);
        
    }
}

package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
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
public class SetmealServiceImpl implements SetmealService {
    
    @Autowired
    private SetmealMapper setmealMapper;
    
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    
    
   
    /**
     * 新增套餐
     * @param setmealVO
     */
    @Transactional
    public void save(SetmealVO setmealVO) {
        //给setmeal进行拷贝赋值
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO,setmeal);
        
        setmealMapper.insert(setmeal);
        System.out.println("套餐id"+ setmeal.getId());
        //获取套餐id
        Long setmealid = setmeal.getId();
       
        List<SetmealDish> setmealDishList = setmealVO.getSetmealDishes();
       
       
        if(setmealDishList != null && setmealDishList.size() > 0){
           //套餐中包含的菜品
            setmealDishList.forEach(SetmealDish->{
                SetmealDish.setSetmealId(setmealid);
            });
            
            //批量插入菜品数据
        setmealDishMapper.insert(setmealDishList);
        }
  
    }
    
    /**
     * 套餐分页查询
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
       
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
       
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        
        long total = page.getTotal();
        List<Setmeal> records = page.getResult();
        
        return new PageResult(total,records);
    }
    /**
     * 批量删除套餐
     * @param ids
     */
    public void deleteBatch(List<Long> ids) {
        //判断套餐的状态，正在启用的套餐无法删除
        for(Long id : ids){
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                //套餐处于起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //删除套餐
       setmealMapper.deleteByIds(ids);
        //删除对应套餐菜品关系
        setmealDishMapper.deleteByIds(ids);
    }
}

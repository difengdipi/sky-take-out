package com.sky.service;

import com.sky.vo.SetmealVO;

/**
 * @description：套餐相应接口
 * @author： 周海
 * @create： 2023/12/27
 */
public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealVO
     */
    void save(SetmealVO setmealVO);
}

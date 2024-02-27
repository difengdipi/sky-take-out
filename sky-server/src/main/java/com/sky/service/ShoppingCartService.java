package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

/**
 * @author 周海
 * @version v1.0.0
 * @Package : com.sky.service
 * @Description : TODO
 * @Create on : 2024/2/26
 **/
public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}

package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

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
    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();
}

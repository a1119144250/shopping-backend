package com.xiaowang.shopping.shop.cart.param;

import lombok.Data;

/**
 * 添加购物车请求参数
 * 
 * @author xiaowang
 */
@Data
public class AddCartRequest {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer count;
}


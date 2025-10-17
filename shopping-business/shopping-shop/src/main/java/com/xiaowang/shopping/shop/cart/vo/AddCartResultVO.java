package com.xiaowang.shopping.shop.cart.vo;

import lombok.Data;

/**
 * 添加购物车结果视图对象
 * 
 * @author xiaowang
 */
@Data
public class AddCartResultVO {

    /**
     * 购物车项ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer count;
}


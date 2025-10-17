package com.xiaowang.shopping.shop.cart.param;

import lombok.Data;

/**
 * 更新购物车请求参数
 * 
 * @author xiaowang
 */
@Data
public class UpdateCartRequest {

    /**
     * 数量（可选）
     */
    private Integer count;

    /**
     * 是否选中（可选）
     */
    private Boolean selected;
}


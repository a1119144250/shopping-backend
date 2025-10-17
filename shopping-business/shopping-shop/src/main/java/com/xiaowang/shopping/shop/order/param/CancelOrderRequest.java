package com.xiaowang.shopping.shop.order.param;

import lombok.Data;

/**
 * 取消订单请求参数
 * 
 * @author xiaowang
 */
@Data
public class CancelOrderRequest {

    /**
     * 取消原因
     */
    private String reason;
}


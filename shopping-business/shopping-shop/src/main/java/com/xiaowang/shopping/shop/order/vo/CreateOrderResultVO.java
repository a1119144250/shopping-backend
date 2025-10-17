package com.xiaowang.shopping.shop.order.vo;

import lombok.Data;

/**
 * 创建订单结果视图对象
 * 
 * @author xiaowang
 */
@Data
public class CreateOrderResultVO {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单号
     */
    private String orderNo;
}


package com.xiaowang.shopping.shop.order.param;

import lombok.Data;

/**
 * 支付订单请求参数
 * 
 * @author xiaowang
 */
@Data
public class PayOrderRequest {

    /**
     * 支付方式：wechat-微信支付，alipay-支付宝
     */
    private String payType;
}


package com.xiaowang.shopping.shop.coupon.param;

import lombok.Data;

/**
 * 使用优惠券请求参数
 * 
 * @author xiaowang
 */
@Data
public class UseCouponRequest {

    /**
     * 订单ID
     */
    private Long orderId;
}


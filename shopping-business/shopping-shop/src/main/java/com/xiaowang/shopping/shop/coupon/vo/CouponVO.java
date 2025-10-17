package com.xiaowang.shopping.shop.coupon.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券视图对象
 * 
 * @author xiaowang
 */
@Data
public class CouponVO {

    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 类型：discount-满减，percent-折扣
     */
    private String type;

    /**
     * 优惠金额
     */
    private BigDecimal discount;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 描述
     */
    private String description;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 状态：unused-未使用，used-已使用，expired-已过期
     */
    private String status;

    /**
     * 使用时间
     */
    private String useTime;

    /**
     * 领取时间
     */
    private String receiveTime;
}


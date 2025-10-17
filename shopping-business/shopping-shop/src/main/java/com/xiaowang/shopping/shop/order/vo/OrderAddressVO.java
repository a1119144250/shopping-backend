package com.xiaowang.shopping.shop.order.vo;

import lombok.Data;

/**
 * 订单地址视图对象
 * 
 * @author xiaowang
 */
@Data
public class OrderAddressVO {

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 收货人手机号
     */
    private String phone;

    /**
     * 地区（省 市 区）
     */
    private String region;

    /**
     * 详细地址
     */
    private String detail;
}


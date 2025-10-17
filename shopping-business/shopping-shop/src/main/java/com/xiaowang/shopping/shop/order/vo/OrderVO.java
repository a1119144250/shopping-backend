package com.xiaowang.shopping.shop.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单视图对象
 * 
 * @author xiaowang
 */
@Data
public class OrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单状态文本
     */
    private String statusText;

    /**
     * 订单商品列表
     */
    private List<OrderItemVO> items;

    /**
     * 收货地址
     */
    private OrderAddressVO address;

    /**
     * 商品总额
     */
    private BigDecimal goodsAmount;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 优惠金额
     */
    private BigDecimal couponDiscount;

    /**
     * 订单总额
     */
    private BigDecimal totalAmount;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 发货时间
     */
    private String deliveryTime;

    /**
     * 完成时间
     */
    private String completeTime;

    /**
     * 取消时间
     */
    private String cancelTime;

    /**
     * 预计送达时间（仅详情页）
     */
    private String estimatedDeliveryTime;
}


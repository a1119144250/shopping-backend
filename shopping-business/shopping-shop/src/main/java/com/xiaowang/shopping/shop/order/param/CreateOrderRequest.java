package com.xiaowang.shopping.shop.order.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求参数
 * 
 * @author xiaowang
 */
@Data
public class CreateOrderRequest {

    /**
     * 订单商品列表
     */
    private List<OrderItemParam> items;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 商品总额
     */
    private BigDecimal goodsAmount;

    /**
     * 订单总额
     */
    private BigDecimal totalAmount;

    /**
     * 订单商品参数
     */
    @Data
    public static class OrderItemParam {
        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 商品名称
         */
        private String productName;

        /**
         * 商品图片
         */
        private String productImage;

        /**
         * 商品价格
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer count;
    }
}


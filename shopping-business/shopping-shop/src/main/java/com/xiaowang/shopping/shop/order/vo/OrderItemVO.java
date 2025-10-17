package com.xiaowang.shopping.shop.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项视图对象
 * 
 * @author xiaowang
 */
@Data
public class OrderItemVO {

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

    /**
     * 小计
     */
    private BigDecimal subtotal;
}


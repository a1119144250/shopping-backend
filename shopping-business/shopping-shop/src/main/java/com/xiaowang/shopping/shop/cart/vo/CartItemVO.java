package com.xiaowang.shopping.shop.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车项视图对象
 * 
 * @author xiaowang
 */
@Data
public class CartItemVO {

    /**
     * 购物车项ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图
     */
    private String productImage;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 是否选中
     */
    private Boolean selected;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 商品状态：1-正常，0-下架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;
}


package com.xiaowang.shopping.shop.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情视图对象
 * 
 * @author xiaowang
 */
@Data
public class ProductDetailVO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品主图
     */
    private String image;

    /**
     * 商品图片列表
     */
    private List<String> images;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 详细描述
     */
    private String detail;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 配料列表
     */
    private List<String> ingredients;

    /**
     * 营养成分
     */
    private NutritionVO nutrition;

    /**
     * 状态：1-上架，0-下架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;
}


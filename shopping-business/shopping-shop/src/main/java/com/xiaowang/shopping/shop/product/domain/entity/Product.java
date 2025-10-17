package com.xiaowang.shopping.shop.product.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 * 
 * @author xiaowang
 */
@Data
@TableName("product")
public class Product {

    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 商品图片列表（JSON格式）
     */
    private String images;

    /**
     * 详细描述
     */
    private String detail;

    /**
     * 配料列表（JSON格式）
     */
    private String ingredients;

    /**
     * 营养成分（JSON格式）
     */
    private String nutrition;

    /**
     * 商品状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer recommended;

    /**
     * 是否热卖：0-否，1-是
     */
    private Integer hot;

    /**
     * 是否新品：0-否，1-是
     */
    private Integer newProduct;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = org.apache.ibatis.type.JdbcType.TIMESTAMP)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = org.apache.ibatis.type.JdbcType.TIMESTAMP)
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}


package com.xiaowang.shopping.shop.category.vo;

import lombok.Data;

/**
 * 商品分类视图对象
 * 
 * @author xiaowang
 */
@Data
public class CategoryVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}


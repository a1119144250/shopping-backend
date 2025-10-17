package com.xiaowang.shopping.shop.common.vo;

import lombok.Data;

/**
 * 轮播图视图对象
 * 
 * @author xiaowang
 */
@Data
public class BannerVO {

    /**
     * 轮播图ID
     */
    private Long id;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 标题
     */
    private String title;

    /**
     * 链接类型：product-商品详情，category-分类，url-外链
     */
    private String linkType;

    /**
     * 链接值：商品ID、分类ID或URL
     */
    private String linkValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;
}


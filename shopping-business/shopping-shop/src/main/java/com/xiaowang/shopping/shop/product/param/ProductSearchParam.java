package com.xiaowang.shopping.shop.product.param;

import lombok.Data;

/**
 * 商品搜索参数
 * 
 * @author xiaowang
 */
@Data
public class ProductSearchParam {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 页码，默认1
     */
    private Integer page = 1;

    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
}


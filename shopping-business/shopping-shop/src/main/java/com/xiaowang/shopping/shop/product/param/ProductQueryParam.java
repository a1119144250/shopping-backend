package com.xiaowang.shopping.shop.product.param;

import lombok.Data;

/**
 * 商品查询参数
 * 
 * @author xiaowang
 */
@Data
public class ProductQueryParam {

    /**
     * 页码，默认1
     */
    private Integer page = 1;

    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;

    /**
     * 分类ID（可选）
     */
    private Long categoryId;

    /**
     * 排序方式：sales-销量，price-价格，rating-评分
     */
    private String sortBy;

    /**
     * 排序顺序：asc-升序，desc-降序
     */
    private String sortOrder = "desc";
}


package com.xiaowang.shopping.shop.common.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 * 
 * @author xiaowang
 */
@Data
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 数据列表
     */
    private List<T> items;

    public PageResult() {
    }

    public PageResult(Long total, Integer page, Integer pageSize, List<T> items) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.items = items;
    }
}


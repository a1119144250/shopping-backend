package com.xiaowang.shopping.shop.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品状态枚举
 * 
 * @author xiaowang
 */
@Getter
@AllArgsConstructor
public enum ProductStatus {

    /**
     * 上架
     */
    ON_SHELF(1, "上架"),

    /**
     * 下架
     */
    OFF_SHELF(0, "下架");

    private final Integer code;
    private final String desc;

    public static ProductStatus fromCode(Integer code) {
        for (ProductStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}


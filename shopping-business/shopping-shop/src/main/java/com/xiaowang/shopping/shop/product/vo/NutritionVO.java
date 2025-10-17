package com.xiaowang.shopping.shop.product.vo;

import lombok.Data;

/**
 * 营养成分视图对象
 * 
 * @author xiaowang
 */
@Data
public class NutritionVO {

    /**
     * 卡路里
     */
    private Integer calories;

    /**
     * 蛋白质（克）
     */
    private Integer protein;

    /**
     * 脂肪（克）
     */
    private Integer fat;

    /**
     * 碳水化合物（克）
     */
    private Integer carbs;
}


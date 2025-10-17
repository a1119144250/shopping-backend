package com.xiaowang.shopping.shop.order.vo;

import lombok.Data;

import java.util.Map;

/**
 * 支付结果视图对象
 * 
 * @author xiaowang
 */
@Data
public class PayResultVO {

    /**
     * 支付参数信息
     */
    private Map<String, String> payInfo;
}


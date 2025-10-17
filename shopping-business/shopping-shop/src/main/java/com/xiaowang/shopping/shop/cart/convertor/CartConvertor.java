package com.xiaowang.shopping.shop.cart.convertor;

import cn.hutool.core.date.DatePattern;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import com.xiaowang.shopping.shop.cart.vo.AddCartResultVO;
import com.xiaowang.shopping.shop.cart.vo.CartItemVO;
import com.xiaowang.shopping.shop.product.domain.entity.Product;

import java.time.format.DateTimeFormatter;

/**
 * 购物车转换器
 * 
 * @author xiaowang
 */
public class CartConvertor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

    /**
     * 转换为购物车项VO
     */
    public static CartItemVO toCartItemVO(CartItem cartItem, Product product) {
        if (cartItem == null) {
            return null;
        }

        CartItemVO vo = new CartItemVO();
        vo.setId(cartItem.getId());
        vo.setUserId(cartItem.getUserId());
        vo.setProductId(cartItem.getProductId());
        vo.setProductName(cartItem.getProductName());
        vo.setProductImage(cartItem.getProductImage());
        vo.setPrice(cartItem.getProductPrice());
        vo.setCount(cartItem.getCount());
        vo.setSelected(cartItem.getSelected() == 1);
        vo.setCreateTime(cartItem.getCreateTime() != null ? cartItem.getCreateTime().format(formatter) : null);

        // 从商品中获取最新信息
        if (product != null) {
            vo.setOriginalPrice(product.getOriginalPrice());
            vo.setStock(product.getStock());
            vo.setStatus(product.getStatus());
        } else {
            // 商品不存在或已删除
            vo.setOriginalPrice(cartItem.getProductPrice());
            vo.setStock(0);
            vo.setStatus(0);
        }

        return vo;
    }

    /**
     * 构建添加购物车结果VO
     */
    public static AddCartResultVO buildAddCartResultVO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        AddCartResultVO vo = new AddCartResultVO();
        vo.setId(cartItem.getId());
        vo.setProductId(cartItem.getProductId());
        vo.setCount(cartItem.getCount());

        return vo;
    }
}


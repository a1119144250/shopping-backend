package com.xiaowang.shopping.shop.cart.domain.service;

import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 * 
 * @author xiaowang
 */
public interface CartService {

    /**
     * 添加商品到购物车
     */
    void addItem(Long userId, Long productId, Integer quantity);

    /**
     * 添加商品到购物车（返回购物车项）
     */
    CartItem addItemWithResult(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车项
     */
    void updateItem(Long userId, Long cartItemId, Integer quantity, Integer selected);

    /**
     * 更新购物车项数量
     */
    void updateQuantity(Long userId, Long cartItemId, Integer quantity);

    /**
     * 根据ID获取购物车项
     */
    CartItem getById(Long userId, Long cartItemId);

    /**
     * 删除购物车项
     */
    void deleteItem(Long userId, Long cartItemId);

    /**
     * 批量删除购物车项
     */
    void deleteItems(Long userId, List<Long> cartItemIds);

    /**
     * 清空购物车
     */
    void clear(Long userId);

    /**
     * 获取用户购物车列表
     */
    List<CartItem> listByUserId(Long userId);

    /**
     * 选中/取消选中购物车项
     */
    void toggleSelect(Long userId, Long cartItemId, Integer selected);

    /**
     * 全选/取消全选
     */
    void toggleSelectAll(Long userId, Integer selected);

    /**
     * 获取购物车商品数量
     */
    Integer getCartCount(Long userId);

    /**
     * 获取选中的购物车项
     */
    List<CartItem> getSelectedItems(Long userId);
}


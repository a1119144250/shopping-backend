package com.xiaowang.shopping.shop.cart.controller;

import com.xiaowang.shopping.web.vo.Result;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import com.xiaowang.shopping.shop.cart.domain.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 * 
 * @author xiaowang
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<Void> addItem(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addItem(userId, productId, quantity);
        return Result.success(null);
    }

    /**
     * 更新购物车项数量
     */
    @PutMapping("/update-quantity")
    public Result<Void> updateQuantity(
            @RequestParam Long userId,
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {
        cartService.updateQuantity(userId, cartItemId, quantity);
        return Result.success(null);
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/{cartItemId}")
    public Result<Void> deleteItem(
            @RequestParam Long userId,
            @PathVariable Long cartItemId) {
        cartService.deleteItem(userId, cartItemId);
        return Result.success(null);
    }

    /**
     * 批量删除购物车项
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteItems(
            @RequestParam Long userId,
            @RequestBody List<Long> cartItemIds) {
        cartService.deleteItems(userId, cartItemIds);
        return Result.success(null);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<Void> clear(@RequestParam Long userId) {
        cartService.clear(userId);
        return Result.success(null);
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<CartItem>> list(@RequestParam Long userId) {
        return Result.success(cartService.listByUserId(userId));
    }

    /**
     * 选中/取消选中
     */
    @PutMapping("/toggle-select")
    public Result<Void> toggleSelect(
            @RequestParam Long userId,
            @RequestParam Long cartItemId,
            @RequestParam Integer selected) {
        cartService.toggleSelect(userId, cartItemId, selected);
        return Result.success(null);
    }

    /**
     * 全选/取消全选
     */
    @PutMapping("/toggle-select-all")
    public Result<Void> toggleSelectAll(
            @RequestParam Long userId,
            @RequestParam Integer selected) {
        cartService.toggleSelectAll(userId, selected);
        return Result.success(null);
    }

    /**
     * 获取购物车数量
     */
    @GetMapping("/count")
    public Result<Integer> getCount(@RequestParam Long userId) {
        return Result.success(cartService.getCartCount(userId));
    }

    /**
     * 获取选中的购物车项
     */
    @GetMapping("/selected")
    public Result<List<CartItem>> getSelected(@RequestParam Long userId) {
        return Result.success(cartService.getSelectedItems(userId));
    }
}


package com.xiaowang.shopping.shop.cart.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaowang.shopping.shop.cart.convertor.CartConvertor;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import com.xiaowang.shopping.shop.cart.domain.service.CartService;
import com.xiaowang.shopping.shop.cart.param.AddCartRequest;
import com.xiaowang.shopping.shop.cart.param.UpdateCartRequest;
import com.xiaowang.shopping.shop.cart.vo.AddCartResultVO;
import com.xiaowang.shopping.shop.cart.vo.CartItemVO;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import com.xiaowang.shopping.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车接口控制器（新版API）
 * 
 * @author xiaowang
 */
@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;
    private final ProductService productService;

    /**
     * 获取购物车列表
     */
    @GetMapping
    public Result<List<CartItemVO>> getCart() {
        Long userId = getUserId();
        
        // 使用联表查询直接获取购物车列表及商品信息
        List<CartItemVO> voList = cartService.listByUserIdWithProduct(userId);
        
        return Result.success(voList);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public Result<AddCartResultVO> addToCart(@RequestBody AddCartRequest request) {
        // 参数校验
        if (request.getProductId() == null) {
            return Result.error("1001", "商品ID不能为空");
        }
        if (request.getCount() == null || request.getCount() <= 0) {
            return Result.error("1002", "商品数量必须大于0");
        }
        
        Long userId = getUserId();
        
        // 添加到购物车
        CartItem cartItem = cartService.addItemWithResult(userId, request.getProductId(), request.getCount());
        
        // 转换为VO
        AddCartResultVO resultVO = CartConvertor.buildAddCartResultVO(cartItem);
        
        return Result.success(resultVO);
    }

    /**
     * 更新购物车商品
     */
    @PutMapping("/{id}")
    public Result<Void> updateCart(@PathVariable Long id, @RequestBody UpdateCartRequest request) {
        Long userId = getUserId();
        
        // 转换选中状态
        Integer selected = null;
        if (request.getSelected() != null) {
            selected = request.getSelected() ? 1 : 0;
        }
        
        // 更新购物车
        cartService.updateItem(userId, id, request.getCount(), selected);
        
        return Result.success(null);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCart(@PathVariable Long id) {
        Long userId = getUserId();
        
        cartService.deleteItem(userId, id);
        
        return Result.success(null);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    public Result<Void> clearCart() {
        Long userId = getUserId();
        
        cartService.clear(userId);
        
        return Result.success(null);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId() {
        String userIdStr = (String) StpUtil.getLoginId();
        return Long.valueOf(userIdStr);
    }
}


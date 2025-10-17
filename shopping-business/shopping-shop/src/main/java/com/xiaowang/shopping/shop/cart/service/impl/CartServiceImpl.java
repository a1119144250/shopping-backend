package com.xiaowang.shopping.shop.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import com.xiaowang.shopping.shop.cart.domain.service.CartService;
import com.xiaowang.shopping.shop.cart.infrastructure.mapper.CartItemMapper;
import com.xiaowang.shopping.shop.cart.vo.CartItemVO;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopErrorCode;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopException;
import com.xiaowang.shopping.shop.product.domain.entity.Product;
import com.xiaowang.shopping.shop.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现
 * 
 * @author xiaowang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductService productService;

    @Override
    public void addItem(Long userId, Long productId, Integer quantity) {
        addItemWithResult(userId, productId, quantity);
    }

    @Override
    public CartItem addItemWithResult(Long userId, Long productId, Integer count) {
        // 获取商品信息
        Product product = productService.getById(productId);
        
        // 检查商品库存
        if (product.getStock() < count) {
            throw new ShopException(ShopErrorCode.PRODUCT_STOCK_INSUFFICIENT);
        }
        
        // 检查商品是否已在购物车中
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId);
        CartItem existItem = cartItemMapper.selectOne(wrapper);
        
        if (existItem != null) {
            // 更新数量
            existItem.setCount(existItem.getCount() + count);
            cartItemMapper.updateById(existItem);
            return existItem;
        } else {
            // 新增购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setProductName(product.getName());
            cartItem.setProductImage(product.getMainImage());
            cartItem.setProductPrice(product.getPrice());
            cartItem.setCount(count);
            cartItem.setSelected(1);
            cartItemMapper.insert(cartItem);
            return cartItem;
        }
    }

    @Override
    public void updateItem(Long userId, Long cartItemId, Integer quantity, Integer selected) {
        CartItem cartItem = getCartItem(userId, cartItemId);
        
        // 更新数量
        if (quantity != null) {
            // 检查库存
            Product product = productService.getById(cartItem.getProductId());
            if (product.getStock() < quantity) {
                throw new ShopException(ShopErrorCode.PRODUCT_STOCK_INSUFFICIENT);
            }
            cartItem.setCount(quantity);
        }
        
        // 更新选中状态
        if (selected != null) {
            cartItem.setSelected(selected);
        }
        
        cartItemMapper.updateById(cartItem);
    }

    @Override
    public CartItem getById(Long userId, Long cartItemId) {
        return getCartItem(userId, cartItemId);
    }

    @Override
    public void updateQuantity(Long userId, Long cartItemId, Integer count) {
        CartItem cartItem = getCartItem(userId, cartItemId);
        
        // 检查库存
        Product product = productService.getById(cartItem.getProductId());
        if (product.getStock() < count) {
            throw new ShopException(ShopErrorCode.PRODUCT_STOCK_INSUFFICIENT);
        }
        
        cartItem.setCount(count);
        cartItemMapper.updateById(cartItem);
    }

    @Override
    public void deleteItem(Long userId, Long cartItemId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getId, cartItemId);
        cartItemMapper.delete(wrapper);
    }

    @Override
    public void deleteItems(Long userId, List<Long> cartItemIds) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .in(CartItem::getId, cartItemIds);
        cartItemMapper.delete(wrapper);
    }

    @Override
    public void clear(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        cartItemMapper.delete(wrapper);
    }

    @Override
    public List<CartItem> listByUserId(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreateTime);
        return cartItemMapper.selectList(wrapper);
    }

    @Override
    public void toggleSelect(Long userId, Long cartItemId, Integer selected) {
        CartItem cartItem = getCartItem(userId, cartItemId);
        cartItem.setSelected(selected);
        cartItemMapper.updateById(cartItem);
    }

    @Override
    public void toggleSelectAll(Long userId, Integer selected) {
        LambdaUpdateWrapper<CartItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(CartItem::getSelected, selected)
                .eq(CartItem::getUserId, userId);
        cartItemMapper.update(null, wrapper);
    }

    @Override
    public Integer getCartCount(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        return cartItemMapper.selectCount(wrapper).intValue();
    }

    @Override
    public List<CartItem> getSelectedItems(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getSelected, 1)
                .orderByDesc(CartItem::getCreateTime);
        List<CartItem> cartItems = cartItemMapper.selectList(wrapper);
        
        // 填充商品信息（这些字段不存储在数据库，但需要在内存中使用）
        for (CartItem item : cartItems) {
            try {
                Product product = productService.getById(item.getProductId());
                item.setProductName(product.getName());
                item.setProductImage(product.getMainImage());
                item.setProductPrice(product.getPrice());
            } catch (Exception e) {
                log.warn("获取商品信息失败, productId: {}", item.getProductId(), e);
            }
        }
        
        return cartItems;
    }

    @Override
    public List<CartItemVO> listByUserIdWithProduct(Long userId) {
        return cartItemMapper.listByUserIdWithProduct(userId);
    }

    @Override
    public List<CartItemVO> getSelectedItemsWithProduct(Long userId) {
        return cartItemMapper.listSelectedByUserIdWithProduct(userId);
    }

    private CartItem getCartItem(Long userId, Long cartItemId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId)
                .eq(CartItem::getId, cartItemId);
        CartItem cartItem = cartItemMapper.selectOne(wrapper);
        if (cartItem == null) {
            throw new ShopException(ShopErrorCode.CART_ITEM_NOT_FOUND);
        }
        return cartItem;
    }
}


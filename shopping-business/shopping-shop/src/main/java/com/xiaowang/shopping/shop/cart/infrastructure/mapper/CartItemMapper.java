package com.xiaowang.shopping.shop.cart.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import com.xiaowang.shopping.shop.cart.vo.CartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    
    /**
     * 查询用户购物车列表（联表查询商品信息）
     * 
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItemVO> listByUserIdWithProduct(@Param("userId") Long userId);
    
    /**
     * 查询用户选中的购物车项（联表查询商品信息）
     * 
     * @param userId 用户ID
     * @return 选中的购物车项列表
     */
    List<CartItemVO> listSelectedByUserIdWithProduct(@Param("userId") Long userId);
}


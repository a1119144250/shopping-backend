package com.xiaowang.shopping.shop.cart.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.cart.domain.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}


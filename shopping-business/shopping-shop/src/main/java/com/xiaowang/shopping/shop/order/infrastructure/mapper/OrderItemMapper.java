package com.xiaowang.shopping.shop.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.order.domain.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}


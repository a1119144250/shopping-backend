package com.xiaowang.shopping.shop.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.order.domain.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}


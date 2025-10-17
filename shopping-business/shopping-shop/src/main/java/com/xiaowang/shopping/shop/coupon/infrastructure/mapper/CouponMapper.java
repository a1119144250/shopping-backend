package com.xiaowang.shopping.shop.coupon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.coupon.domain.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券 Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}


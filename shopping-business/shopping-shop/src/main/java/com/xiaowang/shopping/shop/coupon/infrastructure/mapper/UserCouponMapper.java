package com.xiaowang.shopping.shop.coupon.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.coupon.domain.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户优惠券 Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
}


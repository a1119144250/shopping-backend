package com.xiaowang.shopping.shop.coupon.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.coupon.domain.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 * 
 * @author xiaowang
 */
public interface CouponService {

    /**
     * 获取用户优惠券列表
     */
    Page<UserCoupon> pageByUserId(Long userId, String status, Integer pageNum, Integer pageSize);

    /**
     * 获取用户可用优惠券列表
     */
    List<UserCoupon> getAvailableCoupons(Long userId, BigDecimal amount);

    /**
     * 领取优惠券
     */
    void receiveCoupon(Long userId, Long couponId);

    /**
     * 使用优惠券
     */
    void useCoupon(Long userId, Long userCouponId, Long orderId);

    /**
     * 根据ID获取用户优惠券
     */
    UserCoupon getUserCouponById(Long userCouponId);

    /**
     * 自动标记过期优惠券
     */
    void autoExpireCoupons();
}


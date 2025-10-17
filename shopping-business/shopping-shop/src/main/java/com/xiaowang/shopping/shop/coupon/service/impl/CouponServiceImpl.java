package com.xiaowang.shopping.shop.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.coupon.domain.entity.Coupon;
import com.xiaowang.shopping.shop.coupon.domain.entity.UserCoupon;
import com.xiaowang.shopping.shop.coupon.domain.service.CouponService;
import com.xiaowang.shopping.shop.coupon.infrastructure.mapper.CouponMapper;
import com.xiaowang.shopping.shop.coupon.infrastructure.mapper.UserCouponMapper;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopErrorCode;
import com.xiaowang.shopping.shop.infrastructure.exception.ShopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务实现
 * 
 * @author xiaowang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    public Page<UserCoupon> pageByUserId(Long userId, String status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);

        // 根据状态筛选
        if (status != null && !"all".equals(status)) {
            Integer statusCode = convertStatusToCode(status);
            if (statusCode != null) {
                wrapper.eq(UserCoupon::getStatus, statusCode);
            }
        }

        wrapper.orderByDesc(UserCoupon::getCreateTime);

        return userCouponMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<UserCoupon> getAvailableCoupons(Long userId, BigDecimal amount) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, 0) // 未使用
                .le(UserCoupon::getMinAmount, amount) // 满足最低消费
                .gt(UserCoupon::getEndTime, LocalDateTime.now()) // 未过期
                .orderByDesc(UserCoupon::getDiscount);

        return userCouponMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Long userId, Long couponId) {
        // 查询优惠券
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ShopException(ShopErrorCode.COUPON_NOT_FOUND);
        }

        // 检查优惠券状态
        if (coupon.getStatus() != 1) {
            throw new ShopException(ShopErrorCode.COUPON_NOT_AVAILABLE);
        }

        // 检查是否已过期
        if (LocalDateTime.now().isAfter(coupon.getEndTime())) {
            throw new ShopException(ShopErrorCode.COUPON_EXPIRED);
        }

        // 检查库存
        if (coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new ShopException(ShopErrorCode.COUPON_OUT_OF_STOCK);
        }

        // 检查用户领取次数
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId);
        Long count = userCouponMapper.selectCount(wrapper);
        if (count >= coupon.getLimitPerUser()) {
            throw new ShopException(ShopErrorCode.COUPON_LIMIT_EXCEEDED);
        }

        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getType());
        userCoupon.setDiscount(coupon.getDiscount());
        userCoupon.setMinAmount(coupon.getMinAmount());
        userCoupon.setDescription(coupon.getDescription());
        userCoupon.setStartTime(coupon.getStartTime());
        userCoupon.setEndTime(coupon.getEndTime());
        userCoupon.setStatus(0); // 未使用
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCouponMapper.insert(userCoupon);

        // 更新优惠券领取数量
        coupon.setReceivedCount(coupon.getReceivedCount() + 1);
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useCoupon(Long userId, Long userCouponId, Long orderId) {
        // 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new ShopException(ShopErrorCode.USER_COUPON_NOT_FOUND);
        }

        // 验证所属
        if (!userCoupon.getUserId().equals(userId)) {
            throw new ShopException(ShopErrorCode.USER_COUPON_NOT_BELONG);
        }

        // 检查状态
        if (userCoupon.getStatus() != 0) {
            throw new ShopException(ShopErrorCode.USER_COUPON_ALREADY_USED);
        }

        // 检查是否过期
        if (LocalDateTime.now().isAfter(userCoupon.getEndTime())) {
            throw new ShopException(ShopErrorCode.COUPON_EXPIRED);
        }

        // 更新状态
        userCoupon.setStatus(1); // 已使用
        userCoupon.setUseTime(LocalDateTime.now());
        userCoupon.setOrderId(orderId);
        userCouponMapper.updateById(userCoupon);
    }

    @Override
    public UserCoupon getUserCouponById(Long userCouponId) {
        return userCouponMapper.selectById(userCouponId);
    }

    @Override
    public void autoExpireCoupons() {
        LambdaUpdateWrapper<UserCoupon> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(UserCoupon::getStatus, 2) // 设置为已过期
                .eq(UserCoupon::getStatus, 0) // 只更新未使用的
                .lt(UserCoupon::getEndTime, LocalDateTime.now()); // 过期时间小于当前时间

        userCouponMapper.update(null, wrapper);
    }

    /**
     * 转换状态字符串为状态码
     */
    private Integer convertStatusToCode(String status) {
        switch (status) {
            case "unused":
                return 0;
            case "used":
                return 1;
            case "expired":
                return 2;
            default:
                return null;
        }
    }
}


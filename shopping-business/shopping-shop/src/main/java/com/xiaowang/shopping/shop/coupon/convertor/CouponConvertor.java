package com.xiaowang.shopping.shop.coupon.convertor;

import cn.hutool.core.date.DatePattern;
import com.xiaowang.shopping.shop.coupon.domain.entity.UserCoupon;
import com.xiaowang.shopping.shop.coupon.vo.CouponVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 优惠券转换器
 * 
 * @author xiaowang
 */
public class CouponConvertor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);

    /**
     * 转换为优惠券VO
     */
    public static CouponVO toCouponVO(UserCoupon userCoupon) {
        if (userCoupon == null) {
            return null;
        }

        CouponVO vo = new CouponVO();
        vo.setId(userCoupon.getId());
        vo.setName(userCoupon.getCouponName());
        vo.setType(getCouponType(userCoupon.getCouponType()));
        vo.setDiscount(userCoupon.getDiscount());
        vo.setMinAmount(userCoupon.getMinAmount());
        vo.setDescription(userCoupon.getDescription());
        vo.setStartTime(userCoupon.getStartTime() != null ? userCoupon.getStartTime().format(formatter) : null);
        vo.setEndTime(userCoupon.getEndTime() != null ? userCoupon.getEndTime().format(formatter) : null);
        vo.setStatus(getCouponStatus(userCoupon.getStatus(), userCoupon.getEndTime()));
        vo.setUseTime(userCoupon.getUseTime() != null ? userCoupon.getUseTime().format(formatter) : null);
        vo.setReceiveTime(userCoupon.getReceiveTime() != null ? userCoupon.getReceiveTime().format(formatter) : null);

        return vo;
    }

    /**
     * 获取优惠券类型
     */
    private static String getCouponType(Integer type) {
        if (type == null) {
            return "discount";
        }
        return type == 1 ? "discount" : "percent";
    }

    /**
     * 获取优惠券状态
     */
    private static String getCouponStatus(Integer status, LocalDateTime endTime) {
        // 先检查是否过期
        if (endTime != null && LocalDateTime.now().isAfter(endTime)) {
            return "expired";
        }

        if (status == null) {
            return "unused";
        }

        switch (status) {
            case 0:
                return "unused";
            case 1:
                return "used";
            case 2:
                return "expired";
            default:
                return "unused";
        }
    }
}


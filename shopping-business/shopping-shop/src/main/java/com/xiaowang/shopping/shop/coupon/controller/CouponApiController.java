package com.xiaowang.shopping.shop.coupon.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowang.shopping.shop.common.vo.PageResult;
import com.xiaowang.shopping.shop.coupon.convertor.CouponConvertor;
import com.xiaowang.shopping.shop.coupon.domain.entity.UserCoupon;
import com.xiaowang.shopping.shop.coupon.domain.service.CouponService;
import com.xiaowang.shopping.shop.coupon.param.UseCouponRequest;
import com.xiaowang.shopping.shop.coupon.vo.CouponVO;
import com.xiaowang.shopping.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券接口控制器（新版API）
 * 
 * @author xiaowang
 */
@Slf4j
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponApiController {

    private final CouponService couponService;

    /**
     * 获取优惠券列表
     */
    @GetMapping
    public Result<PageResult<CouponVO>> getCoupons(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Long userId = getUserId();

        // 查询优惠券列表
        Page<UserCoupon> couponPage = couponService.pageByUserId(userId, status, page, pageSize);

        // 转换为VO
        List<CouponVO> voList = couponPage.getRecords().stream()
                .map(CouponConvertor::toCouponVO)
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<CouponVO> pageResult = new PageResult<>(
                couponPage.getTotal(),
                page,
                pageSize,
                voList
        );

        return Result.success(pageResult);
    }

    /**
     * 获取可用优惠券列表
     */
    @GetMapping("/available")
    public Result<List<CouponVO>> getAvailableCoupons(@RequestParam BigDecimal amount) {
        Long userId = getUserId();

        // 查询可用优惠券
        List<UserCoupon> coupons = couponService.getAvailableCoupons(userId, amount);

        // 转换为VO
        List<CouponVO> voList = coupons.stream()
                .map(CouponConvertor::toCouponVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/{id}/receive")
    public Result<Void> receiveCoupon(@PathVariable Long id) {
        Long userId = getUserId();

        // 领取优惠券
        couponService.receiveCoupon(userId, id);

        return Result.success(null);
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/{id}/use")
    public Result<Void> useCoupon(@PathVariable Long id, @RequestBody UseCouponRequest request) {
        // 参数校验
        if (request.getOrderId() == null) {
            return Result.error("1001", "订单ID不能为空");
        }

        Long userId = getUserId();

        // 使用优惠券
        couponService.useCoupon(userId, id, request.getOrderId());

        return Result.success(null);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId() {
        String userIdStr = (String) StpUtil.getLoginId();
        return Long.valueOf(userIdStr);
    }
}


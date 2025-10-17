package com.xiaowang.shopping.shop.infrastructure.exception;

import com.xiaowang.shopping.base.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商城模块错误码
 * 
 * @author xiaowang
 */
@Getter
@AllArgsConstructor
public enum ShopErrorCode implements ErrorCode {

    // 商品相关错误码 (30001-30100)
    PRODUCT_NOT_FOUND("30001", "商品不存在"),
    PRODUCT_STOCK_INSUFFICIENT("30002", "商品库存不足"),
    PRODUCT_OFF_SHELF("30003", "商品已下架"),
    PRODUCT_DELETED("30004", "商品已删除"),

    // 分类相关错误码 (30101-30200)
    CATEGORY_NOT_FOUND("30101", "分类不存在"),
    CATEGORY_HAS_CHILDREN("30102", "该分类下还有子分类，无法删除"),
    CATEGORY_HAS_PRODUCTS("30103", "该分类下还有商品，无法删除"),

    // 购物车相关错误码 (30201-30300)
    CART_ITEM_NOT_FOUND("30201", "购物车项不存在"),
    CART_ITEMS_EXCEED_LIMIT("30202", "购物车商品数量超过限制"),
    CART_ITEM_ALREADY_EXISTS("30203", "商品已在购物车中"),

    // 订单相关错误码 (30301-30400)
    ORDER_NOT_FOUND("30301", "订单不存在"),
    ORDER_STATUS_ERROR("30302", "订单状态错误"),
    ORDER_CANNOT_CANCEL("30303", "订单不可取消"),
    ORDER_CANNOT_PAY("30304", "订单不可支付"),
    ORDER_ALREADY_PAID("30305", "订单已支付"),
    ORDER_TIMEOUT("30306", "订单已超时"),
    ORDER_AMOUNT_ERROR("30307", "订单金额错误"),

    // 优惠券相关错误码 (30401-30500)
    COUPON_NOT_FOUND("30401", "优惠券不存在"),
    COUPON_NOT_AVAILABLE("30402", "优惠券不可用"),
    COUPON_EXPIRED("30403", "优惠券已过期"),
    COUPON_OUT_OF_STOCK("30404", "优惠券已领完"),
    COUPON_LIMIT_EXCEEDED("30405", "优惠券领取次数已达上限"),
    USER_COUPON_NOT_FOUND("30406", "用户优惠券不存在"),
    USER_COUPON_NOT_BELONG("30407", "该优惠券不属于当前用户"),
    USER_COUPON_ALREADY_USED("30408", "优惠券已使用"),
    COUPON_NOT_MEET_CONDITION("30409", "不满足优惠券使用条件"),

    // 通用错误
    OPERATION_FAILED("30999", "操作失败");

    private final String code;
    private final String message;
}


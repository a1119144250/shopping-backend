package com.xiaowang.shopping.user.domain.resp;

import lombok.Data;

/**
 * 用户信息视图对象
 * @author wangjin
 */
@Data
public class UserProfileVO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 账号
     */
    private String userName;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 优惠券数量
     */
    private Integer coupons;

    /**
     * 余额
     */
    private Double balance;
}

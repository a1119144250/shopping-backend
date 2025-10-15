package com.xiaowang.shopping.user.domain.resp;

import lombok.Data;

import java.util.Date;

/**
 * 登录响应
 * @author wangjin
 */
@Data
public class LoginResponse {
    /**
     * 登录令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    @Data
    public static class UserInfoVO {
        /**
         * 用户ID
         */
        private Long id;

        /**
         * 微信openId
         */
        private String openId;

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

        /**
         * 创建时间
         */
        private Date createTime;
    }
}

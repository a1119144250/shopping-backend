package com.xiaowang.shopping.user.param;

import lombok.Data;

/**
 * 微信用户信息
 * @author wangjin
 */
@Data
public class WxUserInfo {
    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别 (0-未知，1-男，2-女)
     */
    private Integer gender;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份
     */
    private String province;

    /**
     * 国家
     */
    private String country;
}

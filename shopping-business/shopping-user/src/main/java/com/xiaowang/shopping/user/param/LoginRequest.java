package com.xiaowang.shopping.user.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求参数
 * @author wangjin
 */
@Data
public class LoginRequest {
    /**
     * 登录类型: account-账号密码登录, wechat-微信登录
     */
    @NotBlank(message = "登录类型不能为空")
    private String loginType;

    /**
     * 用户名（账号登录时必填）
     */
    private String username;

    /**
     * 密码（账号登录时必填）
     */
    private String password;

    /**
     * 微信登录code（微信登录时必填）
     */
    private String code;

    /**
     * 用户信息（微信登录时可选）
     */
    private WxUserInfo userInfo;
}

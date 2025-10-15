package com.xiaowang.shopping.user.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求参数
 * @author wangjin
 */
@Data
public class WxLoginRequest {
    /**
     * 微信登录code
     */
    @NotBlank(message = "微信登录code不能为空")
    private String code;

    /**
     * 用户信息
     */
    private WxUserInfo userInfo;
}

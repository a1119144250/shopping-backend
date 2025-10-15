package com.xiaowang.shopping.user.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册请求参数
 * @author wangjin
 */
@Data
public class RegisterRequest {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 注册类型
     */
    @NotBlank(message = "注册类型不能为空")
    private String registerType;

    /**
     * 邀请码（可选）
     */
    private String inviteCode;
}

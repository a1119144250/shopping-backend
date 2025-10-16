package com.xiaowang.shopping.user.param;

import com.xiaowang.shopping.user.param.base.BaseRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息更新请求
 * 
 * @author wangjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserProfileUpdateRequest extends BaseRequest {

  private static final long serialVersionUID = 1L;

  /**
   * 邮箱
   */
  @Email(message = "邮箱格式不正确")
  private String email;

  /**
   * 性别（0-未知，1-男，2-女）
   */
  private Integer gender;

  /**
   * 昵称
   */
  private String nickName;

  /**
   * 手机号
   */
  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
  private String phone;

  /**
   * 头像URL
   */
  private String avatarUrl;
}

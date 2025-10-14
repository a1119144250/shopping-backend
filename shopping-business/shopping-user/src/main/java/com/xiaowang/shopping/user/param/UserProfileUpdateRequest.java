package com.xiaowang.shopping.user.param;

import lombok.Data;

/**
 * 用户信息更新请求
 *
 * @author wangjin
 */
@Data
public class UserProfileUpdateRequest {
  /**
   * 昵称
   */
  private String nickName;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 头像URL
   */
  private String avatarUrl;
}

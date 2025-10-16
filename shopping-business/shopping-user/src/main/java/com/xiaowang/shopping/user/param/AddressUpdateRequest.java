package com.xiaowang.shopping.user.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新地址请求参数
 * 
 * @author wangjin
 */
@Data
public class AddressUpdateRequest {

  /**
   * 收货人姓名
   */
  @NotBlank(message = "收货人姓名不能为空")
  private String name;

  /**
   * 手机号
   */
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
  private String phone;

  /**
   * 所在地区（省市区）
   */
  @NotBlank(message = "所在地区不能为空")
  private String region;

  /**
   * 详细地址
   */
  @NotBlank(message = "详细地址不能为空")
  private String detail;

  /**
   * 地址标签：家/公司/学校/其他
   */
  private String tag;

  /**
   * 是否为默认地址
   */
  @NotNull(message = "是否默认地址不能为空")
  private Boolean isDefault;
}

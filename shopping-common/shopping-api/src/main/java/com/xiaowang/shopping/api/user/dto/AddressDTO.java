package com.xiaowang.shopping.api.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址数据传输对象
 * 
 * @author xiaowang
 */
@Data
public class AddressDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 地址ID
   */
  private Long id;

  /**
   * 用户ID
   */
  private Long userId;

  /**
   * 收货人姓名
   */
  private String name;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 所在地区（省市区）
   */
  private String region;

  /**
   * 详细地址
   */
  private String detail;

  /**
   * 地址标签
   */
  private String tag;

  /**
   * 是否为默认地址
   */
  private Integer isDefault;
}

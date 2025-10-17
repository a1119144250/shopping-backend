package com.xiaowang.shopping.user.domain.resp;

import lombok.Data;

/**
 * 地址视图对象
 * 
 * @author wangjin
 */
@Data
public class AddressVO {
  /**
   * 地址ID
   */
  private String id;

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
   * 地址标签：家/公司/学校/其他
   */
  private String tag;

  /**
   * 是否为默认地址
   */
  private Boolean isDefault;

  /**
   * 创建时间（yyyy-MM-dd HH:mm:ss）
   */
  private String createTime;

  /**
   * 更新时间（yyyy-MM-dd HH:mm:ss）
   */
  private String updateTime;
}

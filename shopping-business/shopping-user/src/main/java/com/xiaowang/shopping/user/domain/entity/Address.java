package com.xiaowang.shopping.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 收货地址实体
 * 
 * @author wangjin
 */
@Data
@TableName("address")
public class Address {
  /**
   * 地址ID
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 用户ID，关联用户表
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
   * 地址标签：家/公司/学校/其他
   */
  private String tag;

  /**
   * 是否为默认地址：0-否，1-是
   */
  private Integer isDefault;

  /**
   * 创建时间（时间戳，毫秒）
   */
  private Long createTime;

  /**
   * 更新时间（时间戳，毫秒）
   */
  private Long updateTime;

  /**
   * 逻辑删除：0-未删除，1-已删除
   */
  @TableLogic
  private Integer isDeleted;
}

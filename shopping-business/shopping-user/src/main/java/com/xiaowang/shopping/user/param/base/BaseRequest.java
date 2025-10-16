package com.xiaowang.shopping.user.param.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用请求基类
 * 
 * @author wangjin
 */
@Data
public class BaseRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 请求ID（用于链路追踪）
   */
  private String requestId;

  /**
   * 时间戳
   */
  private Long timestamp;
}

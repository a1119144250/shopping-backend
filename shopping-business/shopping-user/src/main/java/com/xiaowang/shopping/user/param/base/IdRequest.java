package com.xiaowang.shopping.user.param.base;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据ID操作的通用请求参数（用于查询/删除）
 * 
 * @author wangjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdRequest extends BaseRequest {

  private static final long serialVersionUID = 1L;

  /**
   * 业务ID
   */
  @NotNull(message = "ID不能为空")
  private Long id;
}

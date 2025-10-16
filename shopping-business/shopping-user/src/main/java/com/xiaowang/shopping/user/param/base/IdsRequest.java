package com.xiaowang.shopping.user.param.base;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 批量ID操作的通用请求参数（用于批量查询/删除）
 * 
 * @author wangjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdsRequest extends BaseRequest {

  private static final long serialVersionUID = 1L;

  /**
   * 业务ID列表
   */
  @NotEmpty(message = "ID列表不能为空")
  private List<Long> ids;
}

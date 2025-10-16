package com.xiaowang.shopping.user.param.base;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询通用请求参数
 * 
 * @author wangjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRequest extends BaseRequest {

  private static final long serialVersionUID = 1L;

  /**
   * 页码（从1开始）
   */
  @Min(value = 1, message = "页码最小为1")
  private Integer pageNum = 1;

  /**
   * 每页数量
   */
  @Min(value = 1, message = "每页数量最小为1")
  @Max(value = 100, message = "每页数量最大为100")
  private Integer pageSize = 10;

  /**
   * 排序字段
   */
  private String orderBy;

  /**
   * 排序方式（asc/desc）
   */
  private String orderDirection = "desc";
}

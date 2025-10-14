package com.xiaowang.shopping.api.user.response;

import com.xiaowang.shopping.api.user.response.data.UserInfo;
import com.xiaowang.shopping.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户操作响应
 *
 * @author cola
 */
@Getter
@Setter
public class UserOperatorResponse extends BaseResponse {

    private UserInfo user;
}

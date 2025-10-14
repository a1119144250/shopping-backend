package com.xiaowang.shopping.api.user.request;

import com.xiaowang.shopping.base.request.BaseRequest;
import lombok.*;

/**
 * @author cola
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest extends BaseRequest {

    private String telephone;

    private String inviteCode;

    private String password;

}

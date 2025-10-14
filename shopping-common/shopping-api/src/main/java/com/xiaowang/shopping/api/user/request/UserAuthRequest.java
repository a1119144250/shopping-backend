package com.xiaowang.shopping.api.user.request;

import com.xiaowang.shopping.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author cola
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequest extends BaseRequest {

    private Long userId;
    private String realName;
    private String idCard;

}

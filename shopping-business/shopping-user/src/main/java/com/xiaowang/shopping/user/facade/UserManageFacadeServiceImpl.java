package com.xiaowang.shopping.user.facade;

import com.xiaowang.shopping.api.user.request.UserRegisterRequest;
import com.xiaowang.shopping.api.user.response.UserOperatorResponse;
import com.xiaowang.shopping.api.user.service.UserManageFacadeService;
import com.xiaowang.shopping.rpc.facade.Facade;
import com.xiaowang.shopping.user.domain.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cola
 */
@DubboService(version = "1.0.0")
public class UserManageFacadeServiceImpl implements UserManageFacadeService {

    @Autowired
    private UserService userService;

    @Override
    @Facade
    public UserOperatorResponse registerAdmin(UserRegisterRequest userRegisterRequest) {
        return userService.registerAdmin(userRegisterRequest.getTelephone(), userRegisterRequest.getPassword());
    }

    @Override
    public UserOperatorResponse freeze(Long userId) {
        return userService.freeze(userId);
    }

    @Override
    public UserOperatorResponse unfreeze(Long userId) {
        return userService.unfreeze(userId);
    }
}

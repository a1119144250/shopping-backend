package com.xiaowang.shopping.user.domain.service;

/**
 * Mock的认证服务
 *
 * @author cola
 */
public class MockAuthServiceImpl implements AuthService {
    @Override
    public boolean checkAuth(String realName, String idCard) {
        return true;
    }
}

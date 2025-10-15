package com.xiaowang.shopping.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaowang.shopping.user.domain.entity.User;
import com.xiaowang.shopping.user.domain.resp.LoginResponse;
import com.xiaowang.shopping.user.domain.resp.UserProfileVO;
import com.xiaowang.shopping.user.domain.service.UserService;
import com.xiaowang.shopping.user.infrastructure.exception.UserException;
import com.xiaowang.shopping.user.param.LoginRequest;
import com.xiaowang.shopping.user.param.RegisterRequest;
import com.xiaowang.shopping.user.param.UserProfileUpdateRequest;
import com.xiaowang.shopping.web.vo.Result;
import org.apache.commons.lang3.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;

/**
 * 用户管理接口
 * @author wangjin
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 账号注册
            User user = userService.registerByUsername(
                    request.getUsername(),
                    request.getPassword(),
                    request.getInviteCode());

            // 注册成功，自动登录
            StpUtil.login(user.getId());
            String token = StpUtil.getTokenValue();

            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);

            LoginResponse.UserInfoVO userInfoVO = new LoginResponse.UserInfoVO();
            userInfoVO.setId(user.getId());
            userInfoVO.setNickName(user.getNickName());
            userInfoVO.setAvatarUrl(user.getProfilePhotoUrl());
            userInfoVO.setPhone(user.getTelephone());
            userInfoVO.setPoints(user.getPoints());
            userInfoVO.setCoupons(user.getCoupons());
            userInfoVO.setBalance(user.getBalance());
            userInfoVO.setCreateTime(user.getGmtCreate());

            response.setUserInfo(userInfoVO);

            return Result.success(response);
        } catch (UserException e) {
            log.error("用户注册失败", e);
            return Result.error(e.getErrorCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.error("REGISTER_ERROR", "注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录（支持账号密码登录和微信登录）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user;

            // 根据登录类型选择不同的登录方式
            if ("account".equals(request.getLoginType())) {
                // 账号密码登录
                if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
                    return Result.error("PARAM_ERROR", "用户名和密码不能为空");
                }
                user = userService.loginByUsername(request.getUsername(), request.getPassword());
            } else if ("wechat".equals(request.getLoginType())) {
                // 微信登录
                if (StringUtils.isBlank(request.getCode())) {
                    return Result.error("PARAM_ERROR", "微信登录code不能为空");
                }
                user = userService.wxLogin(
                        request.getCode(),
                        request.getUserInfo() != null ? request.getUserInfo().getNickName() : "微信用户",
                        request.getUserInfo() != null ? request.getUserInfo().getAvatarUrl() : "",
                        request.getUserInfo() != null ? request.getUserInfo().getGender() : 0,
                        request.getUserInfo() != null ? request.getUserInfo().getCity() : "",
                        request.getUserInfo() != null ? request.getUserInfo().getProvince() : "",
                        request.getUserInfo() != null ? request.getUserInfo().getCountry() : "");
            } else {
                return Result.error("PARAM_ERROR", "不支持的登录类型");
            }

            // 登录成功，创建token
            StpUtil.login(user.getId());
            String token = StpUtil.getTokenValue();

            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);

            LoginResponse.UserInfoVO userInfoVO = new LoginResponse.UserInfoVO();
            userInfoVO.setId(user.getId());
            userInfoVO.setOpenId(user.getOpenId());
            userInfoVO.setNickName(user.getNickName());
            userInfoVO.setAvatarUrl(user.getProfilePhotoUrl());
            userInfoVO.setPhone(user.getTelephone());
            userInfoVO.setPoints(user.getPoints());
            userInfoVO.setCoupons(user.getCoupons());
            userInfoVO.setBalance(user.getBalance());
            userInfoVO.setCreateTime(user.getGmtCreate());

            response.setUserInfo(userInfoVO);

            return Result.success(response);
        } catch (UserException e) {
            log.error("用户登录失败", e);
            return Result.error(e.getErrorCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error("LOGIN_ERROR", "登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        try {
            String userId = (String)StpUtil.getLoginId();
            User user = userService.findById(Long.valueOf(userId));

            if (user == null) {
                throw new UserException(USER_NOT_EXIST);
            }

            UserProfileVO profileVO = new UserProfileVO();
            profileVO.setId(user.getId());
            profileVO.setNickName(user.getNickName());
            profileVO.setAvatarUrl(user.getProfilePhotoUrl());
            profileVO.setPhone(user.getTelephone());
            profileVO.setPoints(user.getPoints());
            profileVO.setCoupons(user.getCoupons());
            profileVO.setBalance(user.getBalance());

            return Result.success(profileVO);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("GET_PROFILE_ERROR", "获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        try {
            String userId = (String)StpUtil.getLoginId();

            userService.updateProfile(
                    Long.valueOf(userId),
                    request.getNickName(),
                    request.getPhone(),
                    request.getEmail(),
                    request.getGender());

            return Result.success(true);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("UPDATE_PROFILE_ERROR", "更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        try {
            StpUtil.logout();
            return Result.success(true);
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return Result.error("LOGOUT_ERROR", "登出失败：" + e.getMessage());
        }
    }
}

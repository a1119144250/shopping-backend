package com.xiaowang.shopping.user.controller.convertor;

import com.xiaowang.shopping.user.domain.entity.User;
import com.xiaowang.shopping.user.domain.resp.LoginResponse;
import com.xiaowang.shopping.user.domain.resp.UserProfileVO;

/**
 * Controller层用户VO转换工具类
 * 
 * @author wangjin
 */
public class UserVOConvertor {

  /**
   * 将User实体转换为UserInfoVO
   * 
   * @param user 用户实体
   * @return UserInfoVO
   */
  public static LoginResponse.UserInfoVO toUserInfoVO(User user) {
    if (user == null) {
      return null;
    }

    LoginResponse.UserInfoVO userInfoVO = new LoginResponse.UserInfoVO();
    userInfoVO.setId(user.getId());
    userInfoVO.setOpenId(user.getOpenId());
    userInfoVO.setNickName(user.getNickName());
    userInfoVO.setUserName(user.getUserName());
    userInfoVO.setAvatarUrl(user.getProfilePhotoUrl());
    userInfoVO.setPhone(user.getTelephone());
    userInfoVO.setPoints(user.getPoints());
    userInfoVO.setCoupons(user.getCoupons());
    userInfoVO.setBalance(user.getBalance());
    userInfoVO.setCreateTime(user.getGmtCreate());

    return userInfoVO;
  }

  /**
   * 将User实体转换为UserProfileVO
   * 
   * @param user 用户实体
   * @return UserProfileVO
   */
  public static UserProfileVO toUserProfileVO(User user) {
    if (user == null) {
      return null;
    }

    UserProfileVO profileVO = new UserProfileVO();
    profileVO.setId(user.getId());
    profileVO.setNickName(user.getNickName());
    profileVO.setUserName(user.getUserName());
    profileVO.setAvatarUrl(user.getProfilePhotoUrl());
    profileVO.setPhone(user.getTelephone());
    profileVO.setPoints(user.getPoints());
    profileVO.setCoupons(user.getCoupons());
    profileVO.setBalance(user.getBalance());

    return profileVO;
  }

  /**
   * 构建登录响应
   * 
   * @param user  用户实体
   * @param token 令牌
   * @return LoginResponse
   */
  public static LoginResponse buildLoginResponse(User user, String token) {
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setUserInfo(toUserInfoVO(user));
    return response;
  }
}

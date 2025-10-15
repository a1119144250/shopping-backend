package com.xiaowang.shopping.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaowang.shopping.user.domain.entity.User;
import com.xiaowang.shopping.user.domain.resp.AvatarUploadResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;

/**
 * 用户管理接口
 * 
 * @author wangjin
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

  private final UserService userService;

  /**
   * 允许上传的图片格式
   */
  private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif");

  /**
   * 最大文件大小（5MB）
   */
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

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
      userInfoVO.setUserName(user.getUserName());
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
      String userId = (String) StpUtil.getLoginId();
      User user = userService.findById(Long.valueOf(userId));

      if (user == null) {
        throw new UserException(USER_NOT_EXIST);
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
      String userId = (String) StpUtil.getLoginId();

      userService.updateProfile(
          Long.valueOf(userId),
          request.getNickName(),
          request.getPhone(),
          request.getEmail(),
          request.getGender(),
          request.getAvatarUrl());

      return Result.success(true);
    } catch (Exception e) {
      log.error("更新用户信息失败", e);
      return Result.error("UPDATE_PROFILE_ERROR", "更新用户信息失败：" + e.getMessage());
    }
  }

  /**
   * 上传头像
   */
  @PostMapping("/avatar")
  public Result<AvatarUploadResponse> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
    try {
      // 1. 验证用户登录
      String userId = (String) StpUtil.getLoginId();

      // 2. 验证文件是否为空
      if (file == null || file.isEmpty()) {
        return Result.error("FILE_EMPTY", "上传文件不能为空");
      }

      // 3. 验证文件大小
      if (file.getSize() > MAX_FILE_SIZE) {
        return Result.error("FILE_TOO_LARGE", "文件大小不能超过5MB");
      }

      // 4. 验证文件格式
      String originalFilename = file.getOriginalFilename();
      if (originalFilename == null || originalFilename.isEmpty()) {
        return Result.error("FILE_NAME_ERROR", "文件名不能为空");
      }

      String fileExtension = getFileExtension(originalFilename).toLowerCase();
      if (!ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
        return Result.error("FILE_TYPE_ERROR", "仅支持jpg、jpeg、png、gif格式的图片");
      }

      // 5. 获取文件的MIME类型
      String mimeType = getContentType(fileExtension);

      // 6. 读取文件并转换为Base64
      byte[] fileBytes = file.getBytes();
      String base64Data = Base64.getEncoder().encodeToString(fileBytes);

      // 7. 构建完整的Data URL (格式: data:image/png;base64,iVBORw0KG...)
      String avatarUrl = "data:" + mimeType + ";base64," + base64Data;

      // 8. 更新用户头像URL
      userService.updateProfile(
          Long.valueOf(userId),
          null,
          null,
          null,
          null,
          avatarUrl);

      // 9. 返回头像URL
      AvatarUploadResponse response = new AvatarUploadResponse();
      response.setAvatarUrl(avatarUrl);

      log.info("用户{}上传头像成功，图片大小：{}KB", userId, file.getSize() / 1024);
      return Result.success(response);

    } catch (Exception e) {
      log.error("上传头像失败", e);
      return Result.error("UPLOAD_AVATAR_ERROR", "上传头像失败：" + e.getMessage());
    }
  }

  /**
   * 获取文件扩展名
   */
  private String getFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
      return filename.substring(lastDotIndex + 1);
    }
    return "";
  }

  /**
   * 根据文件扩展名获取MIME类型
   */
  private String getContentType(String extension) {
    switch (extension.toLowerCase()) {
      case "jpg":
      case "jpeg":
        return "image/jpeg";
      case "png":
        return "image/png";
      case "gif":
        return "image/gif";
      default:
        return "image/jpeg";
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

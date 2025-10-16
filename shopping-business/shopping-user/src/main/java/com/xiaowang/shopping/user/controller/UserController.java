package com.xiaowang.shopping.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaowang.shopping.user.controller.convertor.UserVOConvertor;
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

import java.io.IOException;
import java.util.Base64;

import static com.xiaowang.shopping.base.constant.Constant.*;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_UPLOAD_PICTURE_FAIL;

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
   * 用户注册
   * 
   * @param request 注册请求参数
   * @return 登录响应（包含token和用户信息）
   */
  @PostMapping("/register")
  public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
    // 账号注册
    User user = userService.registerByUsername(
        request.getUsername(),
        request.getPassword(),
        request.getInviteCode());

    // 注册成功，自动登录
    StpUtil.login(user.getId());
    String token = StpUtil.getTokenValue();

    // 构建响应
    LoginResponse response = UserVOConvertor.buildLoginResponse(user, token);

    log.info("用户注册成功, userId: {}", user.getId());
    return Result.success(response);
  }

  /**
   * 用户登录（支持账号密码登录和微信登录）
   * 
   * @param request 登录请求参数
   * @return 登录响应（包含token和用户信息）
   */
  @PostMapping("/login")
  public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    User user;

    // 根据登录类型选择不同的登录方式
    if (LoginType.ACCOUNT.equals(request.getLoginType())) {
      // 账号密码登录
      if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
        return Result.error(ErrorCode.PARAM_ERROR, "用户名和密码不能为空");
      }
      user = userService.loginByUsername(request.getUsername(), request.getPassword());
    } else if (LoginType.WECHAT.equals(request.getLoginType())) {
      // 微信登录
      if (StringUtils.isBlank(request.getCode())) {
        return Result.error(ErrorCode.PARAM_ERROR, "微信登录code不能为空");
      }
      user = userService.wxLogin(
          request.getCode(),
          request.getUserInfo() != null ? request.getUserInfo().getNickName() : UserDefaults.DEFAULT_WECHAT_NICK_NAME,
          request.getUserInfo() != null ? request.getUserInfo().getAvatarUrl() : "",
          request.getUserInfo() != null ? request.getUserInfo().getGender() : UserDefaults.DEFAULT_GENDER,
          request.getUserInfo() != null ? request.getUserInfo().getCity() : "",
          request.getUserInfo() != null ? request.getUserInfo().getProvince() : "",
          request.getUserInfo() != null ? request.getUserInfo().getCountry() : "");
    } else {
      return Result.error(ErrorCode.PARAM_ERROR, "不支持的登录类型");
    }

    // 登录成功，创建token
    StpUtil.login(user.getId());
    String token = StpUtil.getTokenValue();

    // 构建响应
    LoginResponse response = UserVOConvertor.buildLoginResponse(user, token);

    log.info("用户登录成功, userId: {}, loginType: {}", user.getId(), request.getLoginType());
    return Result.success(response);
  }

  /**
   * 获取用户信息
   * 
   * @return 用户资料信息
   */
  @GetMapping("/profile")
  public Result<UserProfileVO> getProfile() {
    String userId = (String) StpUtil.getLoginId();
    User user = userService.findById(Long.valueOf(userId));

    if (user == null) {
      throw new UserException(USER_NOT_EXIST);
    }

    UserProfileVO profileVO = UserVOConvertor.toUserProfileVO(user);
    return Result.success(profileVO);
  }

  /**
   * 更新用户信息
   * 
   * @param request 用户资料更新请求参数
   * @return 更新结果
   */
  @PutMapping("/profile")
  public Result<Boolean> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
    String userId = (String) StpUtil.getLoginId();

    userService.updateProfile(
        Long.valueOf(userId),
        request.getNickName(),
        request.getPhone(),
        request.getEmail(),
        request.getGender(),
        request.getAvatarUrl());

    log.info("用户信息更新成功, userId: {}", userId);
    return Result.success(true);
  }

  /**
   * 上传头像
   * 
   * @param file 头像文件
   * @return 头像上传响应（包含头像URL）
   */
  @PostMapping("/avatar")
  public Result<AvatarUploadResponse> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
    // 1. 验证用户登录
    String userId = (String) StpUtil.getLoginId();

    // 2. 验证文件是否为空
    if (file == null || file.isEmpty()) {
      return Result.error(ErrorCode.FILE_EMPTY, "上传文件不能为空");
    }

    // 3. 验证文件大小
    if (file.getSize() > FileUpload.MAX_FILE_SIZE) {
      return Result.error(ErrorCode.FILE_TOO_LARGE, "文件大小不能超过5MB");
    }

    // 4. 验证文件格式
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || originalFilename.isEmpty()) {
      return Result.error(ErrorCode.FILE_NAME_ERROR, "文件名不能为空");
    }

    String fileExtension = getFileExtension(originalFilename).toLowerCase();
    if (!FileUpload.ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
      return Result.error(ErrorCode.FILE_TYPE_ERROR, "仅支持jpg、jpeg、png、gif格式的图片");
    }

    try {
      // 5. 获取文件的MIME类型
      String mimeType = getContentType(fileExtension);

      // 6. 读取文件并转换为Base64
      byte[] fileBytes = file.getBytes();
      String base64Data = Base64.getEncoder().encodeToString(fileBytes);

      // 7. 构建完整的Data URL (格式: data:image/png;base64,iVBORw0KG...)
      String avatarUrl = DataUrl.PREFIX + mimeType + DataUrl.BASE64_SEPARATOR + base64Data;

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

      log.info("用户上传头像成功, userId: {}, 图片大小: {}KB", userId, file.getSize() / FileUpload.FILE_SIZE_KB);
      return Result.success(response);

    } catch (IOException e) {
      log.error("读取文件失败, userId: {}", userId, e);
      throw new UserException("读取文件失败", USER_UPLOAD_PICTURE_FAIL);
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
      case ImageExtension.JPG:
      case ImageExtension.JPEG:
        return ImageMimeType.JPEG;
      case ImageExtension.PNG:
        return ImageMimeType.PNG;
      case ImageExtension.GIF:
        return ImageMimeType.GIF;
      default:
        return ImageMimeType.JPEG;
    }
  }

  /**
   * 用户登出
   * 
   * @return 登出结果
   */
  @PostMapping("/logout")
  public Result<Boolean> logout() {
    String userId = (String) StpUtil.getLoginId();
    StpUtil.logout();

    log.info("用户登出成功, userId: {}", userId);
    return Result.success(true);
  }
}

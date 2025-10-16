package com.xiaowang.shopping.base.constant;

import java.util.Arrays;
import java.util.List;

public interface Constant {
  // ==================== Excel文件类型 ====================
  String XLS_EXCEL_FILE_TYPE = ".xls";
  String XLSX_EXCEL_FILE_TYPE = ".xlsx";

  // ==================== 用户相关常量 ====================
  /**
   * 用户登录类型
   */
  interface LoginType {
    /** 账号密码登录 */
    String ACCOUNT = "account";
    /** 微信登录 */
    String WECHAT = "wechat";
  }

  /**
   * 用户相关默认值
   */
  interface UserDefaults {
    /** 默认昵称前缀 */
    String DEFAULT_NICK_NAME_PREFIX = "藏家_";
    /** 微信用户默认昵称 */
    String DEFAULT_WECHAT_NICK_NAME = "微信用户";
    /** 邀请码长度 */
    int INVITE_CODE_LENGTH = 6;
    /** 默认性别（未知） */
    int DEFAULT_GENDER = 0;
  }

  /**
   * 文件上传相关常量
   */
  interface FileUpload {
    /** 允许上传的图片格式 */
    List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif");
    /** 最大文件大小（5MB） */
    long MAX_FILE_SIZE = 5 * 1024 * 1024;
    /** 文件大小单位：KB */
    long FILE_SIZE_KB = 1024;
    /** 文件大小单位：MB */
    long FILE_SIZE_MB = 1024 * 1024;
  }

  /**
   * 图片MIME类型
   */
  interface ImageMimeType {
    String JPEG = "image/jpeg";
    String PNG = "image/png";
    String GIF = "image/gif";
  }

  /**
   * 图片文件扩展名
   */
  interface ImageExtension {
    String JPG = "jpg";
    String JPEG = "jpeg";
    String PNG = "png";
    String GIF = "gif";
  }

  /**
   * 通用错误码
   */
  interface ErrorCode {
    String PARAM_ERROR = "PARAM_ERROR";
    String FILE_EMPTY = "FILE_EMPTY";
    String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    String FILE_NAME_ERROR = "FILE_NAME_ERROR";
    String FILE_TYPE_ERROR = "FILE_TYPE_ERROR";
  }

  /**
   * 分布式锁场景
   */
  interface LockScene {
    /** 用户注册场景 */
    String USER_REGISTER = "USER_REGISTER";
  }

  /**
   * HTTP相关常量
   */
  interface Http {
    String METHOD_POST = "POST";
    String METHOD_GET = "GET";
    String PARAM_STATE = "state";
  }

  /**
   * Data URL相关
   */
  interface DataUrl {
    String PREFIX = "data:";
    String BASE64_SEPARATOR = ";base64,";
  }
}
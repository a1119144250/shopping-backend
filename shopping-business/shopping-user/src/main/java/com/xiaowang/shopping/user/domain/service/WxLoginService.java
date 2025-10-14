package com.xiaowang.shopping.user.domain.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 微信登录服务
 *
 * @author wangjin
 */
@Slf4j
@Service
public class WxLoginService {

  @Value("${wx.appid:}")
  private String appId;

  @Value("${wx.secret:}")
  private String appSecret;

  private final OkHttpClient httpClient = new OkHttpClient();

  /**
   * 通过code获取openId
   *
   * @param code 微信登录code
   * @return openId
   */
  public String getOpenId(String code) {
    String url = String.format(
        "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
        appId, appSecret, code);

    Request request = new Request.Builder()
        .url(url)
        .get()
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful() && response.body() != null) {
        String responseBody = response.body().string();
        log.info("微信登录响应: {}", responseBody);

        JSONObject jsonObject = JSONUtil.parseObj(responseBody);

        // 检查是否有错误
        if (jsonObject.containsKey("errcode")) {
          Integer errcode = jsonObject.getInt("errcode");
          if (errcode != 0) {
            String errmsg = jsonObject.getStr("errmsg");
            log.error("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);
            throw new RuntimeException("微信登录失败: " + errmsg);
          }
        }

        String openId = jsonObject.getStr("openid");
        if (openId == null || openId.isEmpty()) {
          log.error("获取openId失败，响应: {}", responseBody);
          throw new RuntimeException("获取openId失败");
        }

        return openId;
      } else {
        log.error("请求微信接口失败: {}", response.code());
        throw new RuntimeException("请求微信接口失败");
      }
    } catch (IOException e) {
      log.error("请求微信接口异常", e);
      throw new RuntimeException("请求微信接口异常", e);
    }
  }
}

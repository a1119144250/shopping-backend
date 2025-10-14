# 用户管理模块 API 文档

## 概述

本模块提供微信小程序用户管理相关接口，包括用户登录、获取用户信息、更新用户信息和登出功能。

## 配置说明

在 `application.yml` 中配置微信小程序的 AppID 和 Secret：

```yaml
wx:
  appid: your-wx-appid # 替换为你的微信小程序AppID
  secret: your-wx-secret # 替换为你的微信小程序Secret
```

## 数据库字段扩展

User 表新增字段：

- `open_id` VARCHAR(128) - 微信 openId
- `gender` INT - 性别 (0-未知，1-男，2-女)
- `city` VARCHAR(100) - 城市
- `province` VARCHAR(100) - 省份
- `country` VARCHAR(100) - 国家
- `points` INT - 积分，默认 0
- `coupons` INT - 优惠券数量，默认 0
- `balance` DOUBLE - 余额，默认 0.0

## API 接口

### 1. 用户登录

**接口**: `POST /user/login`

**描述**: 微信小程序用户登录，如果用户不存在则自动注册

**请求参数**:

```json
{
  "code": "微信登录code",
  "userInfo": {
    "nickName": "用户昵称",
    "avatarUrl": "头像URL",
    "gender": 1,
    "city": "城市",
    "province": "省份",
    "country": "国家"
  }
}
```

**响应数据**:

```json
{
  "code": "SUCCESS",
  "success": true,
  "message": "SUCCESS",
  "data": {
    "token": "jwt_token_string",
    "userInfo": {
      "id": 1,
      "openId": "微信openId",
      "nickName": "用户昵称",
      "avatarUrl": "头像URL",
      "phone": "手机号",
      "points": 1200,
      "coupons": 3,
      "balance": 58.5,
      "createTime": "2024-01-01T00:00:00Z"
    }
  }
}
```

**错误响应**:

```json
{
  "code": "LOGIN_ERROR",
  "success": false,
  "message": "登录失败：错误详情",
  "data": null
}
```

### 2. 获取用户信息

**接口**: `GET /user/profile`

**描述**: 获取当前登录用户的详细信息

**请求头**: `Authorization: Bearer {token}`

**响应数据**:

```json
{
  "code": "SUCCESS",
  "success": true,
  "message": "SUCCESS",
  "data": {
    "id": 1,
    "nickName": "用户昵称",
    "avatarUrl": "头像URL",
    "phone": "手机号",
    "points": 1200,
    "coupons": 3,
    "balance": 58.5
  }
}
```

**错误响应**:

```json
{
  "code": "GET_PROFILE_ERROR",
  "success": false,
  "message": "获取用户信息失败：错误详情",
  "data": null
}
```

### 3. 更新用户信息

**接口**: `PUT /user/profile`

**描述**: 更新用户的基本信息（昵称、手机号、头像）

**请求头**: `Authorization: Bearer {token}`

**请求参数**:

```json
{
  "nickName": "新昵称",
  "phone": "13800138000",
  "avatarUrl": "新头像URL"
}
```

**说明**: 所有字段均为可选，只更新提供的字段

**响应数据**:

```json
{
  "code": "SUCCESS",
  "success": true,
  "message": "SUCCESS",
  "data": true
}
```

**错误响应**:

```json
{
  "code": "UPDATE_PROFILE_ERROR",
  "success": false,
  "message": "更新用户信息失败：错误详情",
  "data": null
}
```

### 4. 用户登出

**接口**: `POST /user/logout`

**描述**: 用户登出，清除登录状态

**请求头**: `Authorization: Bearer {token}`

**响应数据**:

```json
{
  "code": "SUCCESS",
  "success": true,
  "message": "SUCCESS",
  "data": true
}
```

**错误响应**:

```json
{
  "code": "LOGOUT_ERROR",
  "success": false,
  "message": "登出失败：错误详情",
  "data": null
}
```

## 业务流程说明

### 登录流程

1. 小程序端调用 `wx.login()` 获取 code
2. 将 code 和用户信息发送到后端 `/user/login` 接口
3. 后端通过 code 调用微信接口获取 openId
4. 根据 openId 查询用户：
   - 如果用户不存在，创建新用户（自动注册）
   - 如果用户存在，更新用户信息和最后登录时间
5. 生成 token 并返回用户信息

### 权限验证

除登录接口外，其他接口都需要在请求头中携带 token：

```
Authorization: Bearer {token}
```

## 错误码说明

| 错误码               | 说明             |
| -------------------- | ---------------- |
| SUCCESS              | 成功             |
| LOGIN_ERROR          | 登录失败         |
| GET_PROFILE_ERROR    | 获取用户信息失败 |
| UPDATE_PROFILE_ERROR | 更新用户信息失败 |
| LOGOUT_ERROR         | 登出失败         |
| USER_NOT_EXIST       | 用户不存在       |

## 注意事项

1. 微信小程序的 code 只能使用一次，请勿重复使用
2. token 有效期根据 Sa-Token 配置决定，请注意刷新
3. 用户首次登录时会自动创建用户记录
4. 积分、优惠券、余额初始值均为 0
5. 更新用户信息时，未提供的字段不会被更新

## 技术栈

- Spring Boot
- MyBatis Plus
- Sa-Token (认证授权)
- Redis (缓存)
- OkHttp (调用微信接口)

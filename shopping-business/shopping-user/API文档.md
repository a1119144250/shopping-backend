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

### 1. 用户注册

**接口**: `POST /user/register`

**描述**: 用户账号注册，支持用户名+密码方式注册

**请求参数**:

```json
{
  "username": "testuser",
  "password": "123456",
  "registerType": "account",
  "inviteCode": "ABC123"
}
```

**参数说明**:

- `username`: 用户名（必填）
- `password`: 密码（必填）
- `registerType`: 注册类型（必填），目前支持 "account"
- `inviteCode`: 邀请码（可选）

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
      "nickName": "testuser",
      "avatarUrl": null,
      "phone": null,
      "points": 0,
      "coupons": 0,
      "balance": 0.0,
      "createTime": "2024-01-01T00:00:00Z"
    }
  }
}
```

**错误响应**:

```json
{
  "code": "NICK_NAME_EXIST",
  "success": false,
  "message": "用户名已存在",
  "data": null
}
```

或

```json
{
  "code": "REGISTER_ERROR",
  "success": false,
  "message": "注册失败：错误详情",
  "data": null
}
```

### 2. 用户登录

**接口**: `POST /user/login`

**描述**: 用户登录，支持账号密码登录和微信小程序登录

#### 2.1 账号密码登录

**请求参数**:

```json
{
  "loginType": "account",
  "username": "testuser",
  "password": "123456"
}
```

**参数说明**:

- `loginType`: 登录类型，固定值 "account"
- `username`: 用户名（必填）
- `password`: 密码（必填）

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
      "nickName": "testuser",
      "avatarUrl": null,
      "phone": null,
      "points": 0,
      "coupons": 0,
      "balance": 0.0,
      "createTime": "2024-01-01T00:00:00Z"
    }
  }
}
```

#### 2.2 微信小程序登录

**请求参数**:

```json
{
  "loginType": "wechat",
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

**参数说明**:

- `loginType`: 登录类型，固定值 "wechat"
- `code`: 微信登录 code（必填）
- `userInfo`: 用户信息（可选）

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
  "code": "USER_PASSWD_CHECK_FAIL",
  "success": false,
  "message": "用户密码校验失败",
  "data": null
}
```

或

```json
{
  "code": "PARAM_ERROR",
  "success": false,
  "message": "用户名和密码不能为空",
  "data": null
}
```

或

```json
{
  "code": "LOGIN_ERROR",
  "success": false,
  "message": "登录失败：错误详情",
  "data": null
}
```

### 3. 获取用户信息

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

### 4. 更新用户信息

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

### 5. 用户登出

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

### 注册流程

1. 用户填写用户名和密码，可选填写邀请码
2. 调用 `/user/register` 接口提交注册信息
3. 后端验证用户名是否已存在
4. 创建用户，初始化积分、优惠券、余额为 0
5. 自动生成唯一邀请码
6. 如果提供了邀请码，建立邀请关系并更新邀请排行榜
7. 注册成功后自动登录，返回 token 和用户信息

### 登录流程

#### 账号密码登录

1. 用户输入用户名和密码
2. 调用 `/user/login` 接口，设置 `loginType` 为 `account`
3. 后端验证用户名和密码
4. 验证成功后更新最后登录时间
5. 生成 token 并返回用户信息

#### 微信小程序登录

1. 小程序端调用 `wx.login()` 获取 code
2. 将 code 和用户信息发送到后端 `/user/login` 接口，设置 `loginType` 为 `wechat`
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

| 错误码                 | 说明             |
| ---------------------- | ---------------- |
| SUCCESS                | 成功             |
| REGISTER_ERROR         | 注册失败         |
| NICK_NAME_EXIST        | 用户名已存在     |
| LOGIN_ERROR            | 登录失败         |
| USER_PASSWD_CHECK_FAIL | 用户密码校验失败 |
| PARAM_ERROR            | 参数错误         |
| GET_PROFILE_ERROR      | 获取用户信息失败 |
| UPDATE_PROFILE_ERROR   | 更新用户信息失败 |
| LOGOUT_ERROR           | 登出失败         |
| USER_NOT_EXIST         | 用户不存在       |
| USER_OPERATE_FAILED    | 用户操作失败     |

## 注意事项

1. **注册相关**：

   - 用户注册成功后会自动登录并返回 token
   - 注册时用户名不能重复，系统会自动检查
   - 注册成功后会自动生成邀请码，可用于邀请其他用户
   - 如果提供了邀请码，系统会记录邀请关系
   - 积分、优惠券、余额初始值均为 0

2. **登录相关**：

   - 登录接口支持两种登录方式：账号密码登录和微信小程序登录
   - 账号密码登录：需要提供 `loginType=account`、`username` 和 `password`
   - 微信登录：需要提供 `loginType=wechat`、`code` 和可选的 `userInfo`
   - 微信小程序的 code 只能使用一次，请勿重复使用
   - 微信用户首次登录时会自动创建用户记录
   - 密码使用 MD5 加密存储
   - 登录成功后会更新最后登录时间

3. **Token 和权限**：

   - token 有效期根据 Sa-Token 配置决定，请注意刷新
   - 除注册和登录接口外，其他接口都需要在请求头中携带 token

4. **其他**：
   - 更新用户信息时，未提供的字段不会被更新

## 技术栈

- Spring Boot
- MyBatis Plus
- Sa-Token (认证授权)
- Redis (缓存)
- OkHttp (调用微信接口)

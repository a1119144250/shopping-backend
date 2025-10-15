package com.xiaowang.shopping.user.domain.entity;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import com.xiaowang.shopping.api.user.constant.UserRole;
import com.xiaowang.shopping.api.user.constant.UserStateEnum;
import com.xiaowang.shopping.datasource.domain.entity.BaseEntity;
import com.xiaowang.shopping.user.infrastructure.mapper.AesEncryptTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户
 * @author cola
 */
@Setter
@Getter
@TableName("users")
public class User extends BaseEntity {
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String passwordHash;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private UserStateEnum state;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请人用户ID
     */
    private String inviterId;

    /**
     * 手机号
     */
    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;

    /**
     * 区块链地址
     */
    private String blockChainUrl;

    /**
     * 区块链平台
     */
    private String blockChainPlatform;

    /**
     * 实名认证
     */
    private Boolean certification;

    /**
     * 真实姓名
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String realName;

    /**
     * 身份证hash
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String idCardNo;

    /**
     * 用户角色
     */
    private UserRole userRole;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 性别 (0-未知，1-男，2-女)
     */
    private Integer gender;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 优惠券数量
     */
    private Integer coupons;

    /**
     * 余额
     */
    private Double balance;

    public User register(String telephone, String nickName, String password, String inviteCode, String inviterId) {
        this.setTelephone(telephone);
        this.setNickName(nickName);
        this.setPasswordHash(DigestUtil.md5Hex(password));
        this.setState(UserStateEnum.INIT);
        this.setUserRole(UserRole.CUSTOMER);
        this.setInviteCode(inviteCode);
        this.setInviterId(inviterId);

        // 显式设置时间字段，防止自动填充失效
        Date now = new Date();
        this.setGmtCreate(now);
        this.setGmtModified(now);

        return this;
    }

    public User registerAdmin(String telephone, String nickName, String password) {
        this.setTelephone(telephone);
        this.setNickName(nickName);
        this.setPasswordHash(DigestUtil.md5Hex(password));
        this.setState(UserStateEnum.ACTIVE);
        this.setUserRole(UserRole.ADMIN);

        // 显式设置时间字段，防止自动填充失效
        Date now = new Date();
        this.setGmtCreate(now);
        this.setGmtModified(now);

        return this;
    }

    public User auth(String realName, String idCard) {
        this.setRealName(realName);
        this.setIdCardNo(idCard);
        this.setCertification(true);
        this.setState(UserStateEnum.AUTH);
        return this;
    }

    public User active(String blockChainUrl, String blockChainPlatform) {
        this.setBlockChainUrl(blockChainUrl);
        this.setBlockChainPlatform(blockChainPlatform);
        this.setState(UserStateEnum.ACTIVE);
        return this;
    }

    public boolean canModifyInfo() {
        return state == UserStateEnum.INIT || state == UserStateEnum.AUTH || state == UserStateEnum.ACTIVE;
    }

    /**
     * 微信登录/注册
     */
    public User wxLogin(String openId, String nickName, String avatarUrl, Integer gender, String city, String province,
                        String country) {
        this.setOpenId(openId);
        this.setNickName(nickName);
        this.setProfilePhotoUrl(avatarUrl);
        this.setGender(gender);
        this.setCity(city);
        this.setProvince(province);
        this.setCountry(country);
        this.setState(UserStateEnum.ACTIVE);
        this.setUserRole(UserRole.CUSTOMER);
        this.setPoints(0);
        this.setCoupons(0);
        this.setBalance(0.0);

        // 显式设置时间字段，防止自动填充失效
        Date now = new Date();
        this.setGmtCreate(now);
        this.setGmtModified(now);
        this.setLastLoginTime(now);

        return this;
    }
}

package com.xiaowang.shopping.user.domain.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.template.QuickConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.*;

import com.xiaowang.shopping.api.user.constant.UserStateEnum;
import com.xiaowang.shopping.api.user.request.UserActiveRequest;
import com.xiaowang.shopping.api.user.request.UserAuthRequest;
import com.xiaowang.shopping.api.user.request.UserModifyRequest;
import com.xiaowang.shopping.api.user.response.UserOperatorResponse;
import com.xiaowang.shopping.api.user.response.data.InviteRankInfo;
import com.xiaowang.shopping.base.exception.BizException;
import com.xiaowang.shopping.base.exception.RepoErrorCode;
import com.xiaowang.shopping.base.response.PageResponse;
import com.xiaowang.shopping.lock.DistributeLock;
import com.xiaowang.shopping.user.domain.entity.User;
import com.xiaowang.shopping.user.domain.entity.convertor.UserConvertor;
import com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode;
import com.xiaowang.shopping.user.infrastructure.exception.UserException;
import com.xiaowang.shopping.user.infrastructure.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.xiaowang.shopping.base.constant.Constant.*;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.DUPLICATE_TELEPHONE_NUMBER;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.NICK_NAME_EXIST;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_AUTH_FAIL;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_STATUS_CANT_OPERATE;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_STATUS_IS_NOT_ACTIVE;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_STATUS_IS_NOT_AUTH;
import static com.xiaowang.shopping.user.infrastructure.exception.UserErrorCode.USER_STATUS_IS_NOT_INIT;

/**
 * 用户服务
 * 
 * @author cola
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements InitializingBean {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private AuthService authService;

  @Autowired
  private RedissonClient redissonClient;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private UserCacheDelayDeleteService userCacheDelayDeleteService;

  @Autowired
  private WxLoginService wxLoginService;

  /**
   * 用户名布隆过滤器
   */
  private RBloomFilter<String> nickNameBloomFilter;

  /**
   * 邀请码布隆过滤器
   */
  private RBloomFilter<String> inviteCodeBloomFilter;

  /**
   * 邀请排行榜
   */
  private RScoredSortedSet<String> inviteRank;

  /**
   * 通过用户ID对用户信息做的缓存
   */
  private Cache<String, User> idUserCache;

  @PostConstruct
  public void init() {
    QuickConfig idQc = QuickConfig.newBuilder(":user:cache:id:")
        .cacheType(CacheType.BOTH)
        .expire(Duration.ofHours(2))
        .syncLocal(true)
        .build();
    idUserCache = cacheManager.getOrCreateCache(idQc);
  }

  @DistributeLock(keyExpression = "#telephone", scene = LockScene.USER_REGISTER)
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse register(String telephone, String inviteCode) {
    String defaultNickName;
    String randomString;
    do {
      randomString = RandomUtil.randomString(UserDefaults.INVITE_CODE_LENGTH).toUpperCase();
      // 前缀 + 6位随机数 + 手机号后四位
      defaultNickName = UserDefaults.DEFAULT_NICK_NAME_PREFIX + randomString + telephone.substring(7, 11);
    } while (nickNameExist(defaultNickName) || inviteCodeExist(randomString));

    String inviterId = null;
    if (StringUtils.isNotBlank(inviteCode)) {
      User inviter = userMapper.findByInviteCode(inviteCode);
      if (inviter != null) {
        inviterId = inviter.getId().toString();
      }
    }

    User user = register(telephone, defaultNickName, telephone, randomString, inviterId);
    Assert.notNull(user, UserErrorCode.USER_OPERATE_FAILED.getCode());

    addNickName(defaultNickName);
    addInviteCode(randomString);
    updateInviteRank(inviterId);
    updateUserCache(user.getId().toString(), user);

    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    userOperatorResponse.setSuccess(true);

    return userOperatorResponse;
  }

  /**
   * 管理员注册
   * 
   * @param telephone
   * @param password
   * @return
   */
  @DistributeLock(keyExpression = "#telephone", scene = LockScene.USER_REGISTER)
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse registerAdmin(String telephone, String password) {
    User user = registerAdmin(telephone, telephone, password);
    Assert.notNull(user, UserErrorCode.USER_OPERATE_FAILED.getCode());

    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    userOperatorResponse.setSuccess(true);

    return userOperatorResponse;
  }

  /**
   * 注册
   * 
   * @param telephone
   * @param nickName
   * @param password
   * @return
   */
  private User register(String telephone, String nickName, String password, String inviteCode, String inviterId) {
    if (userMapper.findByTelephone(telephone) != null) {
      throw new UserException(DUPLICATE_TELEPHONE_NUMBER);
    }

    User user = new User();
    user.register(telephone, nickName, password, inviteCode, inviterId);
    return save(user) ? user : null;
  }

  private User registerAdmin(String telephone, String nickName, String password) {
    if (userMapper.findByTelephone(telephone) != null) {
      throw new UserException(DUPLICATE_TELEPHONE_NUMBER);
    }

    User user = new User();
    user.registerAdmin(telephone, nickName, password);
    return save(user) ? user : null;
  }

  /**
   * 实名认证
   * 
   * @param userAuthRequest
   * @return
   */
  @CacheInvalidate(name = ":user:cache:id:", key = "#userAuthRequest.userId")
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse auth(UserAuthRequest userAuthRequest) {
    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    User user = userMapper.findById(userAuthRequest.getUserId());
    Assert.notNull(user, () -> new UserException(USER_NOT_EXIST));

    if (user.getState() == UserStateEnum.AUTH || user.getState() == UserStateEnum.ACTIVE) {
      userOperatorResponse.setSuccess(true);
      userOperatorResponse.setUser(UserConvertor.INSTANCE.mapToVo(user));
      return userOperatorResponse;
    }

    Assert.isTrue(user.getState() == UserStateEnum.INIT, () -> new UserException(USER_STATUS_IS_NOT_INIT));
    Assert.isTrue(authService.checkAuth(userAuthRequest.getRealName(), userAuthRequest.getIdCard()),
        () -> new UserException(USER_AUTH_FAIL));
    user.auth(userAuthRequest.getRealName(), userAuthRequest.getIdCard());
    boolean result = updateById(user);
    if (result) {
      userOperatorResponse.setSuccess(true);
      userOperatorResponse.setUser(UserConvertor.INSTANCE.mapToVo(user));
    } else {
      userOperatorResponse.setSuccess(false);
      userOperatorResponse.setResponseCode(UserErrorCode.USER_OPERATE_FAILED.getCode());
      userOperatorResponse.setResponseMessage(UserErrorCode.USER_OPERATE_FAILED.getMessage());
    }
    return userOperatorResponse;
  }

  /**
   * 用户激活
   * 
   * @param userActiveRequest
   * @return
   */
  @CacheInvalidate(name = ":user:cache:id:", key = "#userActiveRequest.userId")
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse active(UserActiveRequest userActiveRequest) {
    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    User user = userMapper.findById(userActiveRequest.getUserId());
    Assert.notNull(user, () -> new UserException(USER_NOT_EXIST));
    Assert.isTrue(user.getState() == UserStateEnum.AUTH, () -> new UserException(USER_STATUS_IS_NOT_AUTH));
    user.active(userActiveRequest.getBlockChainUrl(), userActiveRequest.getBlockChainPlatform());
    boolean result = updateById(user);
    if (result) {
      userOperatorResponse.setSuccess(true);
    } else {
      userOperatorResponse.setSuccess(false);
      userOperatorResponse.setResponseCode(UserErrorCode.USER_OPERATE_FAILED.getCode());
      userOperatorResponse.setResponseMessage(UserErrorCode.USER_OPERATE_FAILED.getMessage());
    }
    return userOperatorResponse;
  }

  /**
   * 冻结
   * 
   * @param userId
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse freeze(Long userId) {
    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    User user = userMapper.findById(userId);
    Assert.notNull(user, () -> new UserException(USER_NOT_EXIST));
    Assert.isTrue(user.getState() == UserStateEnum.ACTIVE, () -> new UserException(USER_STATUS_IS_NOT_ACTIVE));

    // 第一次删除缓存
    idUserCache.remove(user.getId().toString());

    if (user.getState() == UserStateEnum.FROZEN) {
      userOperatorResponse.setSuccess(true);
      return userOperatorResponse;
    }
    user.setState(UserStateEnum.FROZEN);
    boolean updateResult = updateById(user);
    Assert.isTrue(updateResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

    // 第二次删除缓存
    userCacheDelayDeleteService.delayedCacheDelete(idUserCache, user);

    userOperatorResponse.setSuccess(true);
    return userOperatorResponse;
  }

  /**
   * 解冻
   * 
   * @param userId
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse unfreeze(Long userId) {
    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    User user = userMapper.findById(userId);
    Assert.notNull(user, () -> new UserException(USER_NOT_EXIST));

    // 第一次删除缓存
    idUserCache.remove(user.getId().toString());

    if (user.getState() == UserStateEnum.ACTIVE) {
      userOperatorResponse.setSuccess(true);
      return userOperatorResponse;
    }
    user.setState(UserStateEnum.ACTIVE);
    // 更新数据库
    boolean updateResult = updateById(user);
    Assert.isTrue(updateResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

    // 第二次删除缓存
    userCacheDelayDeleteService.delayedCacheDelete(idUserCache, user);

    userOperatorResponse.setSuccess(true);
    return userOperatorResponse;
  }

  /**
   * 分页查询用户信息
   * 
   * @param keyWord
   * @param state
   * @param currentPage
   * @param pageSize
   * @return
   */
  public PageResponse<User> pageQueryByState(String keyWord, String state, int currentPage, int pageSize) {
    Page<User> page = new Page<>(currentPage, pageSize);
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.eq("state", state);

    if (keyWord != null) {
      wrapper.like("telephone", keyWord);
    }
    wrapper.orderBy(true, true, "gmt_create");

    Page<User> userPage = this.page(page, wrapper);

    return PageResponse.of(userPage.getRecords(), (int) userPage.getTotal(), pageSize, currentPage);
  }

  /**
   * 通过手机号和密码查询用户信息
   * 
   * @param telephone
   * @param password
   * @return
   */
  public User findByTelephoneAndPass(String telephone, String password) {
    return userMapper.findByTelephoneAndPass(telephone, DigestUtil.md5Hex(password));
  }

  /**
   * 通过用户名和密码查询用户信息
   * 
   * @param username
   * @param password
   * @return
   */
  public User findByUsernameAndPass(String username, String password) {
    // 先通过用户名查找用户
    User user = userMapper.findByUserName(username);
    if (user != null && user.getPasswordHash() != null) {
      // 验证密码
      String inputPasswordHash = DigestUtil.md5Hex(password);
      if (inputPasswordHash.equals(user.getPasswordHash())) {
        return user;
      }
    }
    return null;
  }

  /**
   * 账号密码登录
   * 
   * @param username 用户名
   * @param password 密码
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public User loginByUsername(String username, String password) {
    // 查询用户
    User user = findByUsernameAndPass(username, password);
    if (user == null) {
      throw new UserException(UserErrorCode.USER_PASSWD_CHECK_FAIL);
    }

    // 更新最后登录时间
    user.setLastLoginTime(new Date());
    updateById(user);

    // 清除缓存
    idUserCache.remove(user.getId().toString());

    return user;
  }

  /**
   * 通过手机号查询用户信息
   * 
   * @param telephone
   * @return
   */
  public User findByTelephone(String telephone) {
    return userMapper.findByTelephone(telephone);
  }

  /**
   * 通过openId查询用户信息
   * 
   * @param openId
   * @return
   */
  public User findByOpenId(String openId) {
    return userMapper.findByOpenId(openId);
  }

  /**
   * 账号注册（用户名+密码）
   * 
   * @param username   用户名
   * @param password   密码
   * @param inviteCode 邀请码
   * @return
   */
  @DistributeLock(keyExpression = "#username", scene = LockScene.USER_REGISTER)
  @Transactional(rollbackFor = Exception.class)
  public User registerByUsername(String username, String password, String inviteCode) {
    // 检查用户名是否已存在
    if (nickNameExist(username)) {
      throw new UserException(NICK_NAME_EXIST);
    }

    // 生成邀请码
    String randomString;
    do {
      randomString = RandomUtil.randomString(UserDefaults.INVITE_CODE_LENGTH).toUpperCase();
    } while (inviteCodeExist(randomString));

    // 处理邀请人ID
    String inviterId = null;
    if (StringUtils.isNotBlank(inviteCode)) {
      User inviter = userMapper.findByInviteCode(inviteCode);
      if (inviter != null) {
        inviterId = inviter.getId().toString();
      }
    }

    // 创建用户
    User user = new User();
    user.setUserName(username);
    user.setNickName(username);
    user.setPasswordHash(DigestUtil.md5Hex(password));
    user.setState(UserStateEnum.ACTIVE);
    user.setUserRole(com.xiaowang.shopping.api.user.constant.UserRole.CUSTOMER);
    user.setInviteCode(randomString);
    user.setInviterId(inviterId);
    user.setPoints(NumberUtils.INTEGER_ZERO);
    user.setCoupons(NumberUtils.INTEGER_ZERO);
    user.setBalance(NumberUtils.DOUBLE_ZERO);
    user.setDeleted(NumberUtils.INTEGER_ZERO);

    // 显式设置时间字段，防止自动填充失效
    Date now = new Date();
    user.setGmtCreate(now);
    user.setGmtModified(now);
    user.setLastLoginTime(now);

    boolean saveResult = save(user);
    Assert.isTrue(saveResult, () -> new UserException(UserErrorCode.USER_OPERATE_FAILED));

    // 添加到布隆过滤器
    addNickName(username);
    addInviteCode(randomString);

    // 更新邀请排行榜
    updateInviteRank(inviterId);

    // 更新缓存
    updateUserCache(user.getId().toString(), user);

    return user;
  }

  /**
   * 微信登录
   * 
   * @param code      微信code
   * @param nickName  昵称
   * @param avatarUrl 头像
   * @param gender    性别
   * @param city      城市
   * @param province  省份
   * @param country   国家
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public User wxLogin(String code, String nickName, String avatarUrl, Integer gender, String city, String province,
      String country) {
    // 通过code获取openId
    String openId = wxLoginService.getOpenId(code);

    // 查询用户是否存在
    User user = findByOpenId(openId);

    if (user == null) {
      // 用户不存在，创建新用户
      user = new User();
      user.wxLogin(openId, nickName, avatarUrl, gender, city, province, country);
      save(user);
    } else {
      // 用户存在，更新用户信息
      user.setNickName(nickName);
      user.setProfilePhotoUrl(avatarUrl);
      user.setGender(gender);
      user.setCity(city);
      user.setProvince(province);
      user.setCountry(country);
      user.setLastLoginTime(new Date());
      updateById(user);

      // 清除缓存
      idUserCache.remove(user.getId().toString());
    }

    return user;
  }

  /**
   * 更新用户资料
   * 
   * @param userId    用户ID
   * @param nickName  昵称
   * @param phone     手机号
   * @param email     邮箱
   * @param gender    性别
   * @param avatarUrl 头像URL
   * @return
   */
  @CacheInvalidate(name = ":user:cache:id:", key = "#userId")
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse updateProfile(Long userId, String nickName, String phone, String email,
      Integer gender, String avatarUrl) {
    UserOperatorResponse response = new UserOperatorResponse();
    User user = userMapper.findById(userId);
    Assert.notNull(user, () -> new UserException(USER_NOT_EXIST));

    if (StringUtils.isNotBlank(nickName)) {
      user.setNickName(nickName);
    }
    if (StringUtils.isNotBlank(phone)) {
      user.setTelephone(phone);
    }
    if (StringUtils.isNotBlank(email)) {
      user.setEmail(email);
    }

    if (Objects.nonNull(gender)) {
      user.setGender(gender);
    }

    if (StringUtils.isNotBlank(avatarUrl)) {
      user.setProfilePhotoUrl(avatarUrl);
    }

    if (updateById(user)) {
      response.setSuccess(true);
    } else {
      response.setSuccess(false);
      response.setResponseCode(UserErrorCode.USER_OPERATE_FAILED.getCode());
      response.setResponseMessage(UserErrorCode.USER_OPERATE_FAILED.getMessage());
    }

    return response;
  }

  /**
   * 通过用户ID查询用户信息
   * 
   * @param userId
   * @return
   */
  @Cached(name = ":user:cache:id:", cacheType = CacheType.BOTH, key = "#userId", cacheNullValue = true)
  @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
  public User findById(Long userId) {
    return userMapper.findById(userId);
  }

  /**
   * 更新用户信息
   * 
   * @param userModifyRequest
   * @return
   */
  @CacheInvalidate(name = ":user:cache:id:", key = "#userModifyRequest.userId")
  @Transactional(rollbackFor = Exception.class)
  public UserOperatorResponse modify(UserModifyRequest userModifyRequest) {
    UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
    User user = userMapper.findById(userModifyRequest.getUserId());
    Assert.notNull(user, () -> new UserException(USER_NOT_EXIST));
    Assert.isTrue(user.canModifyInfo(), () -> new UserException(USER_STATUS_CANT_OPERATE));

    if (StringUtils.isNotBlank(userModifyRequest.getNickName()) && nickNameExist(userModifyRequest.getNickName())) {
      throw new UserException(NICK_NAME_EXIST);
    }
    BeanUtils.copyProperties(userModifyRequest, user);

    if (StringUtils.isNotBlank(userModifyRequest.getPassword())) {
      user.setPasswordHash(DigestUtil.md5Hex(userModifyRequest.getPassword()));
    }
    if (updateById(user)) {
      addNickName(userModifyRequest.getNickName());
      userOperatorResponse.setSuccess(true);

      return userOperatorResponse;
    }
    userOperatorResponse.setSuccess(false);
    userOperatorResponse.setResponseCode(UserErrorCode.USER_OPERATE_FAILED.getCode());
    userOperatorResponse.setResponseMessage(UserErrorCode.USER_OPERATE_FAILED.getMessage());

    return userOperatorResponse;
  }

  public Integer getInviteRank(String userId) {
    Integer rank = inviteRank.revRank(userId);
    if (rank != null) {
      return rank + 1;
    }
    return null;
  }

  public PageResponse<User> getUsersByInviterId(String inviterId, int currentPage, int pageSize) {
    Page<User> page = new Page<>(currentPage, pageSize);
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.select("nick_name", "gmt_create");
    wrapper.eq("inviter_id", inviterId);

    wrapper.orderBy(true, false, "gmt_create");

    Page<User> userPage = this.page(page, wrapper);
    return PageResponse.of(userPage.getRecords(), (int) userPage.getTotal(), pageSize, currentPage);
  }

  public List<InviteRankInfo> getTopN(Integer topN) {
    Collection<ScoredEntry<String>> rankInfos = inviteRank.entryRangeReversed(0, topN - 1);

    List<InviteRankInfo> inviteRankInfos = new ArrayList<>();

    if (rankInfos != null) {
      for (ScoredEntry<String> rankInfo : rankInfos) {
        InviteRankInfo inviteRankInfo = new InviteRankInfo();
        String userId = rankInfo.getValue();
        if (StringUtils.isNotBlank(userId)) {
          User user = findById(Long.valueOf(userId));
          if (user != null) {
            inviteRankInfo.setNickName(user.getNickName());
            inviteRankInfo.setInviteCode(user.getInviteCode());
            inviteRankInfo.setInviteScore(rankInfo.getScore().intValue());
            inviteRankInfos.add(inviteRankInfo);
          }
        }
      }
    }

    return inviteRankInfos;
  }

  public boolean nickNameExist(String nickName) {
    // 如果布隆过滤器中存在，再进行数据库二次判断
    if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
      return userMapper.findByNickname(nickName) != null;
    }

    return false;
  }

  public boolean inviteCodeExist(String inviteCode) {
    // 如果布隆过滤器中存在，再进行数据库二次判断
    if (this.inviteCodeBloomFilter != null && this.inviteCodeBloomFilter.contains(inviteCode)) {
      return userMapper.findByInviteCode(inviteCode) != null;
    }

    return false;
  }

  private boolean addNickName(String nickName) {
    return this.nickNameBloomFilter != null && this.nickNameBloomFilter.add(nickName);
  }

  private boolean addInviteCode(String inviteCode) {
    return this.inviteCodeBloomFilter != null && this.inviteCodeBloomFilter.add(inviteCode);
  }

  /**
   * 更新排名，排名规则：
   *
   * <pre>
   *     1、优先按照分数排，分数越大的，排名越靠前
   *     2、分数相同，则按照上榜时间排，上榜越早的排名越靠前
   * </pre>
   * 
   * @param inviterId
   */
  private void updateInviteRank(String inviterId) {
    if (inviterId == null) {
      return;
    }
    // 1、这里因为是一个私有方法，无法通过注解方式实现分布式锁。
    // 2、register方法已经加了锁，这里需要二次加锁的原因是register锁的是注册人，这里锁的是邀请人
    RLock rLock = redissonClient.getLock(inviterId);
    rLock.lock();
    try {
      // 获取当前用户的积分
      Double score = inviteRank.getScore(inviterId);
      if (score == null) {
        score = 0.0;
      }

      // 获取最近一次上榜时间
      long currentTimeStamp = System.currentTimeMillis();
      // 把上榜时间转成小数(时间戳13位，所以除以10000000000000能转成小数)，并且倒序排列（用1减），即上榜时间越早，分数越大（时间越晚，时间戳越大，用1减一下，就反过来了）
      double timePartScore = 1 - (double) currentTimeStamp / 10000000000000L;

      // 1、当前积分保留整数，即移除上一次的小数位
      // 2、当前积分加100，表示新邀请了一个用户
      // 3、加上“最近一次上榜时间的倒序小数位“作为score
      inviteRank.add(score.intValue() + 100.0 + timePartScore, inviterId);
    } finally {
      rLock.unlock();
    }
  }

  private void updateUserCache(String userId, User user) {
    idUserCache.put(userId, user);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.nickNameBloomFilter = redissonClient.getBloomFilter("nickName");
    if (nickNameBloomFilter != null && !nickNameBloomFilter.isExists()) {
      this.nickNameBloomFilter.tryInit(100000L, 0.01);
    }

    this.inviteCodeBloomFilter = redissonClient.getBloomFilter("inviteCode");
    if (inviteCodeBloomFilter != null && !inviteCodeBloomFilter.isExists()) {
      this.inviteCodeBloomFilter.tryInit(100000L, 0.01);
    }

    this.inviteRank = redissonClient.getScoredSortedSet("inviteRank");
  }
}

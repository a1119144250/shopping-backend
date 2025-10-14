package com.xiaowang.shopping.user;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;

import com.github.houbb.sensitive.core.api.SensitiveUtil;
import com.xiaowang.shopping.api.user.constant.UserStateEnum;
import com.xiaowang.shopping.api.user.request.UserActiveRequest;
import com.xiaowang.shopping.api.user.request.UserAuthRequest;
import com.xiaowang.shopping.api.user.request.UserModifyRequest;
import com.xiaowang.shopping.api.user.response.UserOperatorResponse;
import com.xiaowang.shopping.user.domain.entity.User;
import com.xiaowang.shopping.user.domain.service.UserService;
import com.xiaowang.shopping.user.infrastructure.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cola
 */
public class UserServiceTest extends UserBaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        List<Integer> nums = Arrays.asList(1, 2, 2, 3, 4, 5);

        List<Integer> result = nums.stream()
                                   .filter(n -> n % 2 == 0)
                                   .map(n -> n * n)
                                   .distinct()
                                   .sorted(Comparator.reverseOrder())
                                   .limit(2)
                                   .peek(n -> System.out.println("Peek: " + n))
                                   .collect(Collectors.toList());

        System.out.println(result); // [16, 4]
    }

    @Test
    public void testUserById() {
        User user = userService.findById(40L);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getTelephone(), "17691020550");
    }

    @Test
    public void testCache() {
        UserOperatorResponse response = userService.registerAdmin("17691020550", "wangjin521");
        System.out.println(JSON.toJSONString(response.getUser()));
    }

    @Test
    public void testCache1() {
        userService.register("13448444447", "6666");
        User user = userService.findByTelephone("13448444447");
        Assert.assertEquals(user.getState(), UserStateEnum.INIT);

        UserAuthRequest request = new UserAuthRequest();
        request.setIdCard("12321321321321");
        request.setRealName("test");
        request.setUserId(user.getId());
        Boolean res = userService.auth(request).getSuccess();
        Assert.assertTrue(res);

        user = userService.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.AUTH);
    }

    @Test
    public void testAuth() {
        userService.register("13444444445", "6666");
        User user = userService.findByTelephone("13444444445");
        User sensitiveUser = SensitiveUtil.desCopy(user);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.INIT);
        Assert.assertEquals(sensitiveUser.getTelephone(), "1344****445");
        UserAuthRequest userAuthRequest = new UserAuthRequest();
        userAuthRequest.setUserId(user.getId());
        userAuthRequest.setRealName("wang");
        userAuthRequest.setIdCard("1234");
        Boolean authResult = userService.auth(userAuthRequest).getSuccess();
        Assert.assertTrue(authResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.AUTH);
        Assert.assertEquals(user.getRealName(), "wang");
        Assert.assertEquals(user.getIdCardNo(), "1234");
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(user.getId());
        userModifyRequest.setNickName("newNick");
        userModifyRequest.setProfilePhotoUrl("xxx");
        Boolean modifyResult = userService.modify(userModifyRequest).getSuccess();
        Assert.assertTrue(modifyResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getRealName(), "wang");
        Assert.assertEquals(user.getIdCardNo(), "1234");
    }

    @Test
    public void testActive() {
        userService.register("13444444446", "6666");
        User user = userService.findByTelephone("13444444446");

        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.INIT);
        UserAuthRequest userAuthRequest = new UserAuthRequest();
        userAuthRequest.setUserId(user.getId());
        userAuthRequest.setRealName("wang");
        userAuthRequest.setIdCard("1234");
        Boolean authResult = userService.auth(userAuthRequest).getSuccess();
        Assert.assertTrue(authResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.AUTH);
        UserActiveRequest userActiveRequest = new UserActiveRequest();
        userActiveRequest.setUserId(user.getId());
        userActiveRequest.setBlockChainUrl("url");
        userActiveRequest.setBlockChainPlatform("XXX");
        Boolean activeResult = userService.active(userActiveRequest).getSuccess();
        Assert.assertTrue(activeResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getBlockChainUrl(), "url");
        Assert.assertEquals(user.getState(), UserStateEnum.ACTIVE);

    }

    @Test
    public void testModify() {
        userService.register("13844444448", "6666");
        User user = userService.findByTelephone("13844444448");

        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.INIT);

        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(user.getId());
        String nickName = UUID.randomUUID().toString().substring(0, 13);
        userModifyRequest.setNickName(nickName);
        userModifyRequest.setProfilePhotoUrl("xxx");
        Boolean modifyResult = userService.modify(userModifyRequest).getSuccess();
        Assert.assertTrue(modifyResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getProfilePhotoUrl(), "xxx");
        Assert.assertEquals(user.getNickName(), nickName);
    }

    @Test
    public void testFreezeANdUnFreeze() {
        userService.register("13447444448", "6666");
        User user = userService.findByTelephone("13447444448");

        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.INIT);
        UserAuthRequest userAuthRequest = new UserAuthRequest();
        userAuthRequest.setUserId(user.getId());
        userAuthRequest.setRealName("wang");
        userAuthRequest.setIdCard("1234");

        Boolean authResult = userService.auth(userAuthRequest).getSuccess();
        Assert.assertTrue(authResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.AUTH);
        UserActiveRequest userActiveRequest = new UserActiveRequest();
        userActiveRequest.setUserId(user.getId());
        userActiveRequest.setBlockChainUrl("url");
        userActiveRequest.setBlockChainPlatform("XXX");
        Boolean activeResult = userService.active(userActiveRequest).getSuccess();
        Assert.assertTrue(activeResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getBlockChainUrl(), "url");
        Assert.assertEquals(user.getState(), UserStateEnum.ACTIVE);
        Boolean freezeResult = userService.freeze(user.getId()).getSuccess();
        Assert.assertTrue(freezeResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.FROZEN);
        Boolean unFreezeResult = userService.unfreeze(user.getId()).getSuccess();
        Assert.assertTrue(unFreezeResult);
        user = userMapper.findById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getState(), UserStateEnum.ACTIVE);

    }
}

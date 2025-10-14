package com.xiaowang.shopping.user.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowang.shopping.api.user.constant.UserOperateTypeEnum;
import com.xiaowang.shopping.user.domain.entity.User;
import com.xiaowang.shopping.user.domain.entity.UserOperateStream;
import com.xiaowang.shopping.user.infrastructure.mapper.UserOperateStreamMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 用户操作流水表 服务类
 * </p>
 *
 * @author cola
 */
@Service
public class UserOperateStreamService extends ServiceImpl<UserOperateStreamMapper, UserOperateStream> {

    public Long insertStream(User user, UserOperateTypeEnum type) {
        UserOperateStream stream = new UserOperateStream();
        stream.setUserId(String.valueOf(user.getId()));
        stream.setOperateTime(new Date());
        stream.setType(type.name());
        stream.setParam(JSON.toJSONString(user));
        boolean result = save(stream);
        if (result) {
            return stream.getId();
        }
        return null;
    }
}

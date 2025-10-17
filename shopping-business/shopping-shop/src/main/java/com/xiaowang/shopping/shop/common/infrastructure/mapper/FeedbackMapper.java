package com.xiaowang.shopping.shop.common.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.common.domain.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 意见反馈 Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}


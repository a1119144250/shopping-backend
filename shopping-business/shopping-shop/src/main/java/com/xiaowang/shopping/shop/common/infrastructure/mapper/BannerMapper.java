package com.xiaowang.shopping.shop.common.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowang.shopping.shop.common.domain.entity.Banner;
import org.apache.ibatis.annotations.Mapper;

/**
 * 轮播图 Mapper
 * 
 * @author xiaowang
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {
}


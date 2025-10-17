package com.xiaowang.shopping.shop.common.domain.service;

import com.xiaowang.shopping.shop.common.domain.entity.Banner;

import java.util.List;

/**
 * 通用服务接口
 * 
 * @author xiaowang
 */
public interface CommonService {

    /**
     * 获取轮播图列表
     */
    List<Banner> listBanners();

    /**
     * 提交意见反馈
     */
    void submitFeedback(Long userId, Integer type, String content, List<String> images, String contact);
}


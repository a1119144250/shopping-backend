package com.xiaowang.shopping.shop.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowang.shopping.shop.common.domain.entity.Banner;
import com.xiaowang.shopping.shop.common.domain.entity.Feedback;
import com.xiaowang.shopping.shop.common.domain.service.CommonService;
import com.xiaowang.shopping.shop.common.infrastructure.mapper.BannerMapper;
import com.xiaowang.shopping.shop.common.infrastructure.mapper.FeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用服务实现
 * 
 * @author xiaowang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final BannerMapper bannerMapper;
    private final FeedbackMapper feedbackMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<Banner> listBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1) // 只查询启用的
                .orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getId);

        return bannerMapper.selectList(wrapper);
    }

    @Override
    public void submitFeedback(Long userId, Integer type, String content, List<String> images, String contact) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setType(type);
        feedback.setContent(content);
        feedback.setContact(contact);
        feedback.setStatus(0); // 待处理

        // 转换图片列表为JSON
        if (images != null && !images.isEmpty()) {
            try {
                String imagesJson = objectMapper.writeValueAsString(images);
                feedback.setImages(imagesJson);
            } catch (JsonProcessingException e) {
                log.error("转换图片列表为JSON失败", e);
            }
        }

        feedbackMapper.insert(feedback);
    }
}


package com.xiaowang.shopping.shop.common.param;

import lombok.Data;

import java.util.List;

/**
 * 意见反馈请求参数
 * 
 * @author xiaowang
 */
@Data
public class FeedbackRequest {

    /**
     * 类型：complaint-投诉，suggestion-建议，other-其他
     */
    private String type;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 图片列表
     */
    private List<String> images;

    /**
     * 联系方式
     */
    private String contact;
}


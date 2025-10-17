package com.xiaowang.shopping.shop.common.vo;

import lombok.Data;

/**
 * 文件上传结果视图对象
 * 
 * @author xiaowang
 */
@Data
public class UploadResultVO {

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}


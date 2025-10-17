package com.xiaowang.shopping.shop.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图实体
 * 
 * @author xiaowang
 */
@Data
@TableName("banner")
public class Banner {

    /**
     * 轮播图ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 标题
     */
    private String title;

    /**
     * 链接类型：1-商品详情，2-分类，3-外链
     */
    private Integer linkType;

    /**
     * 链接值：商品ID、分类ID或URL
     */
    private String linkValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = org.apache.ibatis.type.JdbcType.TIMESTAMP)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = org.apache.ibatis.type.JdbcType.TIMESTAMP)
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}


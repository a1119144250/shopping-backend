package com.xiaowang.shopping.shop.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 意见反馈实体
 * 
 * @author xiaowang
 */
@Data
@TableName("feedback")
public class Feedback {

    /**
     * 反馈ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 反馈类型：1-投诉，2-建议，3-其他
     */
    private Integer type;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 图片列表（JSON格式）
     */
    private String images;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 状态：0-待处理，1-处理中，2-已处理
     */
    private Integer status;

    /**
     * 回复内容
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

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


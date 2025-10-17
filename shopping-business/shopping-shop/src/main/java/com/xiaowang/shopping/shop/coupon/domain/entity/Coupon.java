package com.xiaowang.shopping.shop.coupon.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体
 * 
 * @author xiaowang
 */
@Data
@TableName("coupon")
public class Coupon {

    /**
     * 优惠券ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1-满减，2-折扣
     */
    private Integer type;

    /**
     * 优惠金额（满减）或折扣比例（折扣）
     */
    private BigDecimal discount;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 描述
     */
    private String description;

    /**
     * 发行总量
     */
    private Integer totalCount;

    /**
     * 已领取数量
     */
    private Integer receivedCount;

    /**
     * 每人限领数量
     */
    private Integer limitPerUser;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-未发布，1-已发布，2-已下架
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


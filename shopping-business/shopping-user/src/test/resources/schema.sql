/******************************************/
/*   DatabaseName = cola   */
/*   TableName = users   */
/******************************************/
create TABLE `users`
(
    `id`                   bigint unsigned NOT NULL AUTO_INCREMENT comment '主键ID（自增主键）',
    `gmt_create`           datetime NOT NULL comment '创建时间',
    `gmt_modified`         datetime NOT NULL comment '最后更新时间',
    `nick_name`            varchar(255) DEFAULT NULL comment '用户昵称',
    `password_hash`        varchar(255) DEFAULT NULL comment '密码哈希',
    `state`                varchar(64)  DEFAULT NULL comment '用户状态（ACTIVE，FROZEN）',
    `invite_code`          varchar(255) DEFAULT NULL comment '邀请码',
    `telephone`            varchar(20)  DEFAULT NULL comment '手机号码',
    `inviter_id`           varchar(255) DEFAULT NULL comment '邀请人用户ID',
    `last_login_time`      datetime     DEFAULT NULL comment '最后登录时间',
    `profile_photo_url`    varchar(255) DEFAULT NULL comment '用户头像URL',
    `block_chain_url`      varchar(255) DEFAULT NULL comment '区块链地址',
    `block_chain_platform` varchar(255) DEFAULT NULL comment '区块链平台',
    `certification`        tinyint(1) DEFAULT NULL comment '实名认证状态（TRUE或FALSE）',
    `real_name`            varchar(255) DEFAULT NULL comment '真实姓名',
    `id_card_no`         varchar(255) DEFAULT NULL comment '身份证号',
    `user_role`            varchar(128) DEFAULT NULL comment '用户角色',
    `deleted`              int          DEFAULT NULL comment '是否逻辑删除，0为未删除，非0为已删除',
    `lock_version`         int          DEFAULT NULL comment '乐观锁版本号',
    PRIMARY KEY (`id`)
);


/******************************************/
/*   DatabaseName = cola   */
/*   TableName = user_operate_stream   */
/******************************************/
create TABLE `user_operate_stream`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT comment '主键ID（自增主键）',
    `gmt_create`   datetime    DEFAULT NULL comment '创建时间',
    `gmt_modified` datetime    DEFAULT NULL comment '最后更新时间',
    `user_id`      varchar(64) DEFAULT NULL comment '用户ID',
    `type`         varchar(64) DEFAULT NULL comment '操作类型',
    `operate_time` datetime    DEFAULT NULL comment '操作时间',
    `param`        text comment '操作参数',
    `extend_info`  text comment '扩展字段',
    `deleted`      int         DEFAULT NULL comment '是否逻辑删除，0为未删除，非0为已删除',
    `lock_version` int         DEFAULT NULL comment '乐观锁版本号',
    PRIMARY KEY (`id`)
);


/******************************************/
/*   DatabaseName = cola   */
/*   TableName = address   */
/******************************************/
CREATE TABLE `address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID，关联用户表',
  `name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `region` VARCHAR(200) NOT NULL COMMENT '所在地区（省市区）',
  `detail` VARCHAR(500) NOT NULL COMMENT '详细地址',
  `tag` VARCHAR(20) DEFAULT '家' COMMENT '地址标签：家/公司/学校/其他',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否为默认地址：0-否，1-是',
  `create_time` BIGINT NOT NULL COMMENT '创建时间（时间戳，毫秒）',
  `update_time` BIGINT NOT NULL COMMENT '更新时间（时间戳，毫秒）',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_default` (`is_default`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

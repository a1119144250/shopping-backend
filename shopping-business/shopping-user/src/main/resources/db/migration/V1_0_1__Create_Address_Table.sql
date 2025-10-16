-- 创建收货地址表
CREATE TABLE IF NOT EXISTS `address` (
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


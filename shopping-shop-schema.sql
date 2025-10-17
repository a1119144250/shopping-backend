-- Shopping-Shop 模块数据库表结构初始化脚本
-- 数据库: shopping

USE shopping;

-- ========================================
-- 1. 轮播图表 (banner)
-- ========================================
CREATE TABLE IF NOT EXISTS `banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `image` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `link_type` INT DEFAULT NULL COMMENT '链接类型：1-商品详情，2-分类，3-外链',
  `link_value` VARCHAR(200) DEFAULT NULL COMMENT '链接值：商品ID、分类ID或URL',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ========================================
-- 2. 商品分类表 (category)
-- ========================================
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `icon` VARCHAR(500) DEFAULT NULL COMMENT '分类图标',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '分类图片',
  `level` INT DEFAULT 1 COMMENT '分类层级：1-一级，2-二级，3-三级',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ========================================
-- 3. 商品表 (product)
-- ========================================
CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '商品描述',
  `category_id` BIGINT NOT NULL COMMENT '商品分类ID',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
  `stock` INT DEFAULT 0 COMMENT '库存',
  `main_image` VARCHAR(500) DEFAULT NULL COMMENT '商品主图',
  `images` TEXT DEFAULT NULL COMMENT '商品图片列表（JSON格式）',
  `detail` TEXT DEFAULT NULL COMMENT '详细描述',
  `ingredients` TEXT DEFAULT NULL COMMENT '配料列表（JSON格式）',
  `nutrition` TEXT DEFAULT NULL COMMENT '营养成分（JSON格式）',
  `status` INT DEFAULT 1 COMMENT '商品状态：0-下架，1-上架',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `rating` DECIMAL(3, 2) DEFAULT 5.00 COMMENT '评分',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `recommended` INT DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
  `hot` INT DEFAULT 0 COMMENT '是否热卖：0-否，1-是',
  `new_product` INT DEFAULT 0 COMMENT '是否新品：0-否，1-是',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_recommended` (`recommended`),
  INDEX `idx_hot` (`hot`),
  INDEX `idx_new_product` (`new_product`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ========================================
-- 4. 购物车表 (cart_item)
-- ========================================
CREATE TABLE IF NOT EXISTS `cart_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `selected` INT DEFAULT 1 COMMENT '是否选中：0-否，1-是',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_product` (`user_id`, `product_id`, `deleted`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ========================================
-- 5. 订单表 (orders)
-- ========================================
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
  `pay_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
  `discount_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
  `freight_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '运费',
  `status` INT DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-已关闭',
  `pay_type` INT DEFAULT NULL COMMENT '支付方式：1-微信支付，2-支付宝，3-余额支付',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
  `receiver_province` VARCHAR(50) DEFAULT NULL COMMENT '收货地址-省',
  `receiver_city` VARCHAR(50) DEFAULT NULL COMMENT '收货地址-市',
  `receiver_district` VARCHAR(50) DEFAULT NULL COMMENT '收货地址-区',
  `receiver_address` VARCHAR(500) NOT NULL COMMENT '收货详细地址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_order_no` (`order_no`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ========================================
-- 6. 订单项表 (order_item)
-- ========================================
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `product_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
  `product_price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
  `quantity` INT NOT NULL COMMENT '购买数量',
  `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ========================================
-- 7. 优惠券表 (coupon)
-- ========================================
CREATE TABLE IF NOT EXISTS `coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` INT NOT NULL COMMENT '优惠券类型：1-满减券，2-折扣券，3-无门槛券',
  `discount_amount` DECIMAL(10, 2) DEFAULT NULL COMMENT '减免金额（满减券和无门槛券使用）',
  `discount_rate` DECIMAL(5, 2) DEFAULT NULL COMMENT '折扣率（折扣券使用，如0.8表示8折）',
  `min_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '最低消费金额',
  `max_discount` DECIMAL(10, 2) DEFAULT NULL COMMENT '最大优惠金额（折扣券使用）',
  `total_count` INT DEFAULT 0 COMMENT '总发放数量，0表示不限制',
  `remain_count` INT DEFAULT 0 COMMENT '剩余数量',
  `receive_limit` INT DEFAULT 1 COMMENT '每人限领数量',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `valid_days` INT DEFAULT 0 COMMENT '领取后有效天数，0表示使用start_time和end_time',
  `status` INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '使用说明',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- ========================================
-- 8. 用户优惠券表 (user_coupon)
-- ========================================
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID',
  `coupon_name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` INT NOT NULL COMMENT '优惠券类型',
  `discount_amount` DECIMAL(10, 2) DEFAULT NULL COMMENT '减免金额',
  `discount_rate` DECIMAL(5, 2) DEFAULT NULL COMMENT '折扣率',
  `min_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '最低消费金额',
  `max_discount` DECIMAL(10, 2) DEFAULT NULL COMMENT '最大优惠金额',
  `status` INT DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `receive_time` DATETIME NOT NULL COMMENT '领取时间',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `order_id` BIGINT DEFAULT NULL COMMENT '使用的订单ID',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_coupon_id` (`coupon_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ========================================
-- 9. 用户反馈表 (feedback)
-- ========================================
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` INT NOT NULL COMMENT '反馈类型：1-功能异常，2-产品建议，3-其他',
  `content` TEXT NOT NULL COMMENT '反馈内容',
  `images` TEXT DEFAULT NULL COMMENT '图片列表（JSON格式）',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `status` INT DEFAULT 0 COMMENT '处理状态：0-待处理，1-处理中，2-已处理',
  `reply` TEXT DEFAULT NULL COMMENT '回复内容',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';


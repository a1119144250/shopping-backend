-- 用户表字段扩展 - 支持微信小程序登录
-- 执行此脚本前请先备份数据库

-- 添加微信相关字段
ALTER TABLE users ADD COLUMN open_id VARCHAR(128) COMMENT '微信openId' AFTER user_role;
ALTER TABLE users ADD COLUMN gender INT DEFAULT 0 COMMENT '性别 (0-未知，1-男，2-女)' AFTER open_id;
ALTER TABLE users ADD COLUMN city VARCHAR(100) COMMENT '城市' AFTER gender;
ALTER TABLE users ADD COLUMN province VARCHAR(100) COMMENT '省份' AFTER city;
ALTER TABLE users ADD COLUMN country VARCHAR(100) COMMENT '国家' AFTER province;

-- 添加积分、优惠券、余额字段
ALTER TABLE users ADD COLUMN points INT DEFAULT 0 COMMENT '积分' AFTER country;
ALTER TABLE users ADD COLUMN coupons INT DEFAULT 0 COMMENT '优惠券数量' AFTER points;
ALTER TABLE users ADD COLUMN balance DOUBLE DEFAULT 0.0 COMMENT '余额' AFTER coupons;

-- 为openId创建索引，提高查询性能
CREATE INDEX idx_open_id ON users(open_id);

-- 查看表结构
DESCRIBE users;


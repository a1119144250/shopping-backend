package com.xiaowang.shopping.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商城服务主启动类
 * 
 * @author xiaowang
 */
@SpringBootApplication(scanBasePackages = {
    "com.xiaowang.shopping.shop",
    "com.xiaowang.shopping.datasource",
    "com.xiaowang.shopping.web",
    "com.xiaowang.shopping.cache",
    "com.xiaowang.shopping.rpc",
    "com.xiaowang.shopping.mq"
})
@MapperScan("com.xiaowang.shopping.shop.*.infrastructure.mapper")
public class ShoppingShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingShopApplication.class, args);
    }

}


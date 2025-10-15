package com.xiaowang.shopping.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xiaowang.shopping.gateway")
public class ShoppingGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingGatewayApplication.class, args);
    }

}

package com.xiaowang.shopping.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xiaowang.shopping.user")
public class ShoppingUserApplication {

  public static void main(String[] args) {
    SpringApplication.run(ShoppingUserApplication.class, args);
  }

}

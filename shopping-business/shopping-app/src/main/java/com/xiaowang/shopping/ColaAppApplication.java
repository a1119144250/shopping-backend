package com.xiaowang.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xiaowang.shopping")
public class ColaAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(ColaAppApplication.class, args);
  }

}

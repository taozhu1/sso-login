package com.tao.ssocenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tao")
public class SSOCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SSOCenterApplication.class, args);
    }

}

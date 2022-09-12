package com.tao.ssoclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tao")
public class SsoClientApplication9001 {

    public static void main(String[] args) {
        SpringApplication.run(SsoClientApplication9001.class, args);
    }

}

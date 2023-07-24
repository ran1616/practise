package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.common.feign.product",
        "com.atguigu.gmall.common.feign.item",
        "com.atguigu.gmall.common.feign.seach"
})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class , args) ;
    }

}

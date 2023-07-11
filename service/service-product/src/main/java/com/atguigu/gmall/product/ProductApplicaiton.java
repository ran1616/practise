package com.atguigu.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
public class ProductApplicaiton {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplicaiton.class , args) ;
    }

}

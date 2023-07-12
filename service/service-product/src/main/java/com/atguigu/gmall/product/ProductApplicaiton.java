package com.atguigu.gmall.product;

import com.atguigu.gmall.common.anno.EnableMinioClient;
import com.atguigu.gmall.common.anno.EnableSwagger2Configuration;
import com.atguigu.gmall.common.config.MinioConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
// @ComponentScan(basePackages = "com.atguigu.gmall")
// @EnableGlobalExceptionHandler
@EnableMinioClient
@EnableSwagger2Configuration
public class ProductApplicaiton {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplicaiton.class , args) ;
    }

}

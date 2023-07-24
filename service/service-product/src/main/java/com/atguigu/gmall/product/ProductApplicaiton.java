package com.atguigu.gmall.product;

import com.atguigu.gmall.common.anno.EnableMinioClient;
import com.atguigu.gmall.common.anno.EnableSwagger2Configuration;
import com.atguigu.gmall.common.cache.anno.EnableRedissonClient;
import com.atguigu.gmall.common.config.MinioConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
// @ComponentScan(basePackages = "com.atguigu.gmall")
// @EnableGlobalExceptionHandler
@EnableMinioClient
@EnableSwagger2Configuration
@EnableScheduling           // 开启定时任务的功能
@EnableRedissonClient
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.common.feign.seach"
})
public class ProductApplicaiton {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplicaiton.class , args) ;
    }

}

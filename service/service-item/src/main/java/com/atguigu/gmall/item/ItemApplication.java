package com.atguigu.gmall.item;

import com.atguigu.gmall.common.anno.EnableThreadPool;
import com.atguigu.gmall.common.cache.anno.EnableRedissonClient;
import com.atguigu.gmall.common.properties.ThreadPoolProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.common.feign.product"
})
@EnableRedissonClient
@EnableThreadPool
public class ItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class , args) ;
    }

}

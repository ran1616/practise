package com.atguigu.gmall.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer              // 声明当前这个微服务是一个sba的服务端
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class , args) ;
    }

}

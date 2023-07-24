package com.atguigu.gmall.canal;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableCanalClient          // 声明当前的服务是Canal的一个客户端
public class CanalClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalClientApplication.class , args) ;
    }

}

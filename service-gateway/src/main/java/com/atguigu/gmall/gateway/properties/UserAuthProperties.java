package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "gmall.auth")
public class UserAuthProperties {

    private List<String>     noauthurls ;           // 不需要验证用户登录的路径规则
    private List<String>     authurls ;             // 需要验证用户登录的路径规则

    private String     toLoginPage ;

}

package com.atguigu.gmall.common.feign.interceptor;

import lombok.Data;

@Data
public class AuthUserInfo {

    private String userId ;
    private String userTempId ;

}

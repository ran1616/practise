package com.atguigu.gmall.common.feign.util;

import com.atguigu.gmall.common.feign.interceptor.AuthUserInfo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class AuthUserInfoUtils {

    public static AuthUserInfo getAuthUserInfo() {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null) {

            // 获取用户的id和临时用户的id
            HttpServletRequest httpServletRequest = requestAttributes.getRequest();
            String userId = httpServletRequest.getHeader("userId");
            String userTempId = httpServletRequest.getHeader("userTempId");

            // 封装数据
            AuthUserInfo authUserInfo = new AuthUserInfo() ;
            authUserInfo.setUserId(userId);
            authUserInfo.setUserTempId(userTempId);

            return authUserInfo ;
        }

        return null ;

    }

}

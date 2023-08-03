package com.atguigu.gmall.common.feign.util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestThreadLocal {

    private static final ThreadLocal<HttpServletRequest> threadLocal = new ThreadLocal<>() ;

    public static void set(HttpServletRequest httpServletRequest) {
        threadLocal.set(httpServletRequest) ;
    }

    public static HttpServletRequest get() {
        return threadLocal.get() ;
    }

}

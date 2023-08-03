package com.atguigu.gmall.common.feign.interceptor;

import com.atguigu.gmall.common.feign.util.AuthUserInfoUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * ThreadLocal: 在同一个线程中进行数据的共享
 * RequestContextHolder底层原理：
 * 1、监听器
 * 2、ThreadLocal
 * 通过监听器监听Request对象的变化，当Request对象创建好了以后，那么此时就会把request对象存储到RequestContextHolder对象的ThreadLocal属性上
 */
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        log.info("FeignClientInterceptor...apply方法执行了... , ThreadId: {}" , Thread.currentThread().getId());

        // 从ThreadLocal中获取HttpServletRequest对象
        AuthUserInfo authUserInfo = AuthUserInfoUtils.getAuthUserInfo();
        if(authUserInfo != null) {
            template.header("userId" , authUserInfo.getUserId()) ;
            template.header("userTempId" , authUserInfo.getUserTempId() ) ;
        }

    }

}

package com.atguigu.gmall.common.feign.anno;

import com.atguigu.gmall.common.feign.interceptor.FeignClientInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Import(value = FeignClientInterceptor.class)
public @interface EnableFeignClientInterceptor {

}

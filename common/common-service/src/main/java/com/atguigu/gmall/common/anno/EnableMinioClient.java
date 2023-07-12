package com.atguigu.gmall.common.anno;

import com.atguigu.gmall.common.config.MinioConfiguration;
import com.atguigu.gmall.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Import(value = MinioConfiguration.class)
public @interface EnableMinioClient {

}

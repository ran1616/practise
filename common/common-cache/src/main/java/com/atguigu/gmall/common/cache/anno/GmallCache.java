package com.atguigu.gmall.common.cache.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface GmallCache {

    // 缓存数据key的属性
    public String cacheKey() ;

    // 布隆过滤器的名称
    public String bloomFilterName() default "" ;

    // 布隆过滤器的值
    public String bloomFilterValue() default "" ;

    // 是否启用分布式锁的属性
    public boolean enableLock() default false ;

    // 分布式锁的名称
    public String lockName() default "" ;

    // 缓存时间
    public long time() default 30L ;

    // 缓存时间单位
    public TimeUnit timeUnit() default TimeUnit.MINUTES ;

}

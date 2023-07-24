package com.atguigu.gmall.common.cache.service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public interface RedisCacheService {

    /**
     * 获取数据的方法
     */
    public abstract <T> T getData(String redisKey , Class<T> clazz ) ;

    /**
     * 获取数据的方法
     */
    public abstract <T> T getData(String redisKey , Type type) ;

    /**
     * 设置数据的方法
     */
    public abstract void saveData(String redisKey , Object data , Long time , TimeUnit timeUnit) ;

    public abstract void saveData(String redisKey , Object data ) ;

}

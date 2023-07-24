package com.atguigu.gmall.common.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.service.RedisCacheService;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

//@Service
public class RedisCacheServiceImpl implements RedisCacheService  {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public <T> T getData(String redisKey, Class<T> clazz) {
        String redisValue = redisTemplate.opsForValue().get(redisKey);      // X , null , 对象

        if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(redisValue)) {
            throw new GmallException(ResultCodeEnum.REDIS_DATA_ERROR) ;
        }

        if(null == redisValue || "".equalsIgnoreCase(redisValue)) {
            return null; 
        }

        T result = JSON.parseObject(redisValue, clazz);
        return result;
    }

    @Override
    public Object getData(String redisKey, Type type) {
        String redisValue = redisTemplate.opsForValue().get(redisKey);      // X , null , 对象
        if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(redisValue)) {
            throw new GmallException(ResultCodeEnum.REDIS_DATA_ERROR) ;
        }

        if(null == redisValue || "".equalsIgnoreCase(redisValue)) {
            return null;
        }

        Object object = JSON.parseObject(redisValue, type);

        return object;
    }

    @Override
    public void saveData(String redisKey, Object data , Long time , TimeUnit timeUnit) {
        if(data != null) {
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(data.toString())) {
                redisTemplate.opsForValue().set(redisKey , GmallConstant.REDIS_NULL_VALUE , time , timeUnit);
            }else {
                redisTemplate.opsForValue().set(redisKey , JSON.toJSONString(data) , time , timeUnit);
            }
        }
    }

    @Override
    public void saveData(String redisKey, Object data ) {
        if(data != null) {
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(data.toString())) {
                redisTemplate.opsForValue().set(redisKey , GmallConstant.REDIS_NULL_VALUE);
            }else {
                redisTemplate.opsForValue().set(redisKey , JSON.toJSONString(data));
            }
        }
    }
}

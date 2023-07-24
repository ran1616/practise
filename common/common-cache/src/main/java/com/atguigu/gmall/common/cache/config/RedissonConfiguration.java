package com.atguigu.gmall.common.cache.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {

    @Autowired
    private RedisProperties redisProperties ;

    @Bean
    public RedissonClient redisClient() {
        Config config = new Config() ;
        config.useSingleServer()
                .setPassword(redisProperties.getPassword()).setAddress("redis://" + redisProperties.getHost()  +":" + redisProperties.getPort());
        RedissonClient redisson = Redisson.create(config);
        return redisson ;
    }

}

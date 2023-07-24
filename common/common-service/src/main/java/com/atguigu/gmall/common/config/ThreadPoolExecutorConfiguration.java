package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.properties.ThreadPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(value = ThreadPoolProperties.class)
public class ThreadPoolExecutorConfiguration {

    @Autowired
    private ThreadPoolProperties threadPoolProperties ;

    @Value("${spring.application.name}")
    private String applicationName ;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(), threadPoolProperties.getMaximumPoolSize(), threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(threadPoolProperties.getWorkQueueSize()), new ThreadFactory() {
            int count = 0 ;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r) ;
                thread.setName("[pool-" + applicationName + "-" + count +"]");
                count++ ;
                return thread;
            }
        }, new ThreadPoolExecutor.AbortPolicy()) ;
        return threadPoolExecutor ;
    }

}

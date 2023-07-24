package com.atguigu.gmall.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gmall.threadpool")
public class ThreadPoolProperties {

    private Integer     corePoolSize ;
    private Integer     maximumPoolSize ;
    private Integer   keepAliveTime ;
    private Integer       workQueueSize ;

}

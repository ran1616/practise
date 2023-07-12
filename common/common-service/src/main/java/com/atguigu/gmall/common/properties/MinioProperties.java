package com.atguigu.gmall.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gmall.minio")
public class MinioProperties {

    private String endpoint ;
    private String accessKey ;
    private String secretKey ;
    private String bucket ;

}

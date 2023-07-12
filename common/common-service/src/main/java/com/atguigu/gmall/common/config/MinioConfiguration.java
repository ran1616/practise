package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.properties.MinioProperties;
import com.atguigu.gmall.common.service.FileUploadService;
import com.atguigu.gmall.common.service.impl.FileUploadServiceImpl;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = MinioProperties.class )
public class MinioConfiguration {

    @Autowired
    private MinioProperties minioProperties ;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioProperties.getEndpoint())
                        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                        .build();
        return minioClient ;
    }

    @Bean
    public FileUploadService fileUploadService() {
        return new FileUploadServiceImpl() ;
    }

}

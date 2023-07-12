package com.atguigu.gmall.common.service.impl;

import com.atguigu.gmall.common.properties.MinioProperties;
import com.atguigu.gmall.common.service.FileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

// @Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioProperties minioProperties ;

    @Autowired
    private MinioClient minioClient ;

    @Override
    public String upload(MultipartFile multipartFile) {

        try {

            // 创建桶
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
            if(!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
            }

            // 上传文件
            String uuidFileName = UUID.randomUUID().toString().replace("-" , "") ;
            String originalFilename = multipartFile.getOriginalFilename();      // 获取文件的原始名称  xxxx.jpg
            String extFileName = FilenameUtils.getExtension(originalFilename);
            String fileName = uuidFileName + "." + extFileName ;
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .stream(multipartFile.getInputStream() , multipartFile.getSize() , -1 )
                    .build() ;
            minioClient.putObject(putObjectArgs) ;

            // 访问文件路径
            String imageUrl = minioProperties.getEndpoint() + "/" + minioProperties.getBucket() +"/" + fileName ;

            return imageUrl ;

        }catch ( Exception e) {
            e.printStackTrace();
            return null ;
        }

    }

}

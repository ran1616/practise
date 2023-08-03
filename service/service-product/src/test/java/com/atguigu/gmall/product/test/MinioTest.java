package com.atguigu.gmall.product.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioTest {

    public static void main(String[] args) throws Exception {

        // 创建MinioClient对象
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.100.50:9000")
                        .credentials("admin", "admin123456")
                        .build();

        // 创建桶
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("gmall").build());
        if(!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("gmall").build());
        }

        // 上传文件
        FileInputStream fileInputStream = new FileInputStream("D://images//a.jpg") ;

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("gmall")
                .object("a.jpg")
                .stream(fileInputStream , fileInputStream.available() , -1 )
                .build() ;
        minioClient.putObject(putObjectArgs) ;

        // 访问文件路径
        String imageUrl = "http://192.168.100.50:9000/gmall/a.jpg" ;
        System.out.println(imageUrl);



    }

}

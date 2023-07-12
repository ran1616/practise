package com.atguigu.gmall.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    public abstract String upload(MultipartFile multipartFile);
}

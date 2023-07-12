package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/admin/product")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService ;

    @PostMapping(value = "/fileUpload")
    public Result<String> upload(@RequestParam(value = "file") MultipartFile multipartFile) {
        String url = fileUploadService.upload(multipartFile) ;
        return Result.build(url , ResultCodeEnum.SUCCESS) ;
    }


}

package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/admin/product")
@ApiIgnore
public class HelloController {

    @GetMapping(value = "/hello")
    public Result<String> hello() {
        return Result.build("ok" , ResultCodeEnum.SUCCESS)  ;
    }

}

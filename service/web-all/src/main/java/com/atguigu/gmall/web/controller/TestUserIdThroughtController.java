package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order")
public class TestUserIdThroughtController {

    @GetMapping(value = "/userIdThrought")
    public Result userIdThrought(@RequestHeader(value = "userId" , required = false) String userId) {
        System.out.println(userId);
        return Result.ok() ;
    }

}
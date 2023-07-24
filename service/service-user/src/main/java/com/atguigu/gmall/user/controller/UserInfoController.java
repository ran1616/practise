package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.dto.UserLoginDto;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService ;

    @PostMapping(value = "/passport/login")
    public Result<UserLoginSuccessVo> login(@RequestBody UserLoginDto userLoginDto) {
        UserLoginSuccessVo userLoginSuccessVo = userInfoService.login(userLoginDto) ;
        return Result.build(userLoginSuccessVo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/passport/logout")
    public Result logout(@RequestHeader(value = "token") String token) {        // 获取token的请求头数据
        userInfoService.logout(token) ;
        return Result.ok() ;
    }

}

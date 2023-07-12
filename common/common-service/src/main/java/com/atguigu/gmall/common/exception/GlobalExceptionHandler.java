package com.atguigu.gmall.common.exception;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice               // 声明当前这个类就是一个全局异常处理器
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GmallException.class)
    public Result gmallExceptionHandler(GmallException e) {
        e.printStackTrace();
        return Result.build(null  , e.getResultCodeEnum()) ;
    }

    @ExceptionHandler(value = Exception.class)
    public Result systemExceptionHandler(Exception e) {
        e.printStackTrace();
        return Result.build(null  , ResultCodeEnum.SYS_ERROR) ;
    }

}

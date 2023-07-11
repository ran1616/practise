package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController  // @RestController = @Controller + @ResponseBody(把方法的返回值作为响应体进行输出，如果返回值是集合类型或者实体类型，那么
// 此时会将集合以及实体类转换成json进行返回 )
@RequestMapping(value = "/admin/product")
//@CrossOrigin
public class BaseCategory1Controller {

    @Autowired
    private BaseCategory1Service baseCategory1Service ;

    @GetMapping(value = "/getCategory1")
    public Result<List<BaseCategory1>> findAllBaseCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategory1Service.findAllBaseCategory1() ;
        return Result.build(baseCategory1List , ResultCodeEnum.SUCCESS) ;
    }

}

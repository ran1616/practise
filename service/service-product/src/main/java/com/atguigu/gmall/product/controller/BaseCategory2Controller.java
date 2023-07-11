package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class BaseCategory2Controller {

    @Autowired
    private BaseCategory2Service baseCategory2Service ;

    @GetMapping(value = "/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> findBaseCategoryByC1Id(@PathVariable(value = "category1Id") Long c1Id) {
        List<BaseCategory2> baseCategory2List = baseCategory2Service.findBaseCategoryByC1Id(c1Id) ;
        return Result.build(baseCategory2List , ResultCodeEnum.SUCCESS) ;
    }

}

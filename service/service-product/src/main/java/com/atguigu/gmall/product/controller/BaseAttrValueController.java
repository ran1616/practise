package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class BaseAttrValueController {

    @Autowired
    private BaseAttrValueService baseAttrValueService ;

    @GetMapping(value = "/getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> findByAttrId(@PathVariable(value = "attrId") Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrValueService.findByAttrId(attrId);
        return Result.build(baseAttrValueList , ResultCodeEnum.SUCCESS) ;
    }

}

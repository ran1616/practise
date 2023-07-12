package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product/")
public class BaseAttrInfoController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService ;

    @GetMapping(value = "/attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result<List<BaseAttrInfo>> findBaseAttrInfo(@PathVariable(value = "c1Id") Long c1Id ,@PathVariable(value = "c2Id") Long c2Id , @PathVariable(value = "c3Id") Long c3Id) {
        List<BaseAttrInfo> attrInfoList =  baseAttrInfoService.findBaseAttrInfo(c1Id , c2Id , c3Id);
        return Result.build(attrInfoList , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/saveAttrInfo")
    public Result saveBaseAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.saveBaseAttrInfo(baseAttrInfo) ;
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

}

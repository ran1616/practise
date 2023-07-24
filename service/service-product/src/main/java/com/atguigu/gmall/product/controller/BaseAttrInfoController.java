package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product/")
@Api(tags = "平台属性管理")
public class BaseAttrInfoController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService ;

    @GetMapping(value = "/attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    @ApiOperation(value = "根据分类id查询平台属性")
    public Result<List<BaseAttrInfo>> findBaseAttrInfo(@ApiParam(name = "一级分类id") @PathVariable(value = "c1Id") Long c1Id ,
                                                       @ApiParam(name = "二级分类id") @PathVariable(value = "c2Id") Long c2Id ,
                                                       @ApiParam(name = "三级分类id") @PathVariable(value = "c3Id") Long c3Id) {
        List<BaseAttrInfo> attrInfoList =  baseAttrInfoService.findBaseAttrInfo(c1Id , c2Id , c3Id);
        return Result.build(attrInfoList , ResultCodeEnum.SUCCESS) ;
    }

    @ApiOperation(value = "保存平台属性")
    @PostMapping(value = "/saveAttrInfo")
    public Result saveBaseAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.saveBaseAttrInfo(baseAttrInfo) ;
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

}

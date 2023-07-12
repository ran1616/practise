package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.dto.SkuInfoDto;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/product")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService ;

    @GetMapping(value = "/list/{page}/{limit}")
    public Result<Page> findByPage(@PathVariable(value = "page") Integer pageNo , @PathVariable(value = "limit") Integer pageSize) {
        Page page = skuInfoService.findByPage(pageNo, pageSize) ;
        return Result.build(page , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfoDto skuInfoDto) {
        skuInfoService.saveSkuInfo(skuInfoDto) ;
        return Result.ok() ;
    }

}

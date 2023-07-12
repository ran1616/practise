package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService ;

    @GetMapping(value = "/{page}/{limit}")
    public Result<Page> findByPage(@PathVariable(value = "page") Integer pageNo , @PathVariable(value = "limit")Integer pageSize) {
        Page page = baseTrademarkService.findByPage(pageNo , pageSize) ;
        return Result.build(page , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/save")
    public Result save(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark) ;
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/get/{id}")
    public Result<BaseTrademark> getById(@PathVariable(value = "id") Long id) {
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.build(baseTrademark , ResultCodeEnum.SUCCESS) ;
    }

    @PutMapping(value = "/update")
    public Result updateById(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark) ;
        return Result.ok() ;
    }

    @DeleteMapping(value = "/remove/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id) {
//        try {
//            baseTrademarkService.deleteById(id) ;
//            return Result.ok() ;
//        }catch (GmallException e) {
//            e.printStackTrace();
//            return Result.build(null , e.getResultCodeEnum()) ;
//        }

        baseTrademarkService.deleteById(id) ;
        return Result.ok() ;
    }

    @GetMapping(value = "/getTrademarkList")
    public Result<List<BaseTrademark>> findAll() {
        List<BaseTrademark> baseTrademarkList = baseTrademarkService.list();
        return Result.build(baseTrademarkList , ResultCodeEnum.SUCCESS) ;
    }

}

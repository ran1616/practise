package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.biz.SkuBizService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.vo.AttrValueConcatVo;
import com.atguigu.gmall.product.vo.CategoryView;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/inner/product")
public class SkuController {

    @Autowired
    private SkuBizService skuBizService ;

    @GetMapping(value = "/findCategoryViewBySkuId/{skuId}")
    public Result<CategoryView> findCategoryViewBySkuId(@PathVariable(value = "skuId")Long skuId) {
        CategoryView categoryView = skuBizService.findCategoryViewBySkuId(skuId) ;
        return Result.build(categoryView , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findSkuInfoAndImageBySkuId/{skuId}")
    public Result<SkuInfo> findSkuInfoAndImageBySkuId(@PathVariable(value = "skuId")Long skuId) {
        SkuInfo skuInfo = skuBizService.findSkuInfoAndImageBySkuId(skuId) ;
        return Result.build(skuInfo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findSkuInfoBySkuId/{skuId}")
    public Result<SkuInfo> findSkuInfoBySkuId(@PathVariable(value = "skuId")Long skuId) {
        SkuInfo skuInfo = skuBizService.findSkuInfoBySkuId(skuId) ;
        return Result.build(skuInfo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findSpuSalAttrBySkuId/{skuId}")
    public Result<List<SpuSaleAttr>> findSpuSalAttrBySkuId(@PathVariable(value = "skuId")Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList = skuBizService.findSpuSalAttrBySkuId(skuId) ;
        return Result.build(spuSaleAttrList , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findSkuAttrValueConcatBySkuId/{skuId}")
    public Result<List<AttrValueConcatVo>> findSkuAttrValueConcatBySkuId(@PathVariable(value = "skuId")Long skuId) {
        List<AttrValueConcatVo> attrValueConcatVos = skuBizService.findSkuAttrValueConcatBySkuId(skuId) ;
        return Result.build(attrValueConcatVos , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findSkuDetailVo/{skuId}")
    public Result<SkuDetailVo> findSkuDetailVo(@PathVariable(value = "skuId")Long skuId) {
        SkuDetailVo skuDetailVo = skuBizService.findSkuDetailVo(skuId) ;
        return  Result.build(skuDetailVo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findAllSkuIds")
    public Result<List<Long>> findAllSkuIds(){
        List<Long> ids = skuBizService.findAllSkuIds() ;
        return Result.build(ids , ResultCodeEnum.SUCCESS) ;
    }
}

package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.item.biz.SkuDetailBizService;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/inner/item")
public class SkuDetailController {

    @Autowired
    private SkuDetailBizService skuDetailBizService ;

    @GetMapping(value = "/item/{skuId}")
    public Result<SkuDetailVo> item(@PathVariable(value = "skuId")Long skuId) {
        SkuDetailVo skuDetailVo = skuDetailBizService.item(skuId) ;
        return Result.build(skuDetailVo , ResultCodeEnum.SUCCESS) ;
    }


}

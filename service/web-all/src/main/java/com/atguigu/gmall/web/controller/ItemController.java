package com.atguigu.gmall.web.controller;


import com.atguigu.gmall.common.feign.item.SkuDetailFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient ;

    @GetMapping(value = "/{skuId}.html")
    public String item(@PathVariable(value = "skuId") Long skuId , Model model) {
        Result<SkuDetailVo> result = skuDetailFeignClient.item(skuId);
        SkuDetailVo skuDetailVo = result.getData();

        // 把数据存储到Model数据模型中
        model.addAttribute("categoryView" , skuDetailVo.getCategoryView()) ;
        model.addAttribute("skuInfo" , skuDetailVo.getSkuInfo()) ;
        model.addAttribute("price" , skuDetailVo.getPrice()) ;
        model.addAttribute("spuSaleAttrList" , skuDetailVo.getSpuSaleAttrList()) ;
        model.addAttribute("valuesSkuJson" , skuDetailVo.getValuesSkuJson()) ;

        // 跳转页面
        return "item/index" ;

    }

}

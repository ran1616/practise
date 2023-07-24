package com.atguigu.gmall.common.feign.item;

import com.atguigu.gmall.common.feign.item.fallback.SkuDetailFeignClientFallback;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-item" , fallback = SkuDetailFeignClientFallback.class)
public interface SkuDetailFeignClient {

    @GetMapping(value = "/api/inner/item/item/{skuId}")
    public abstract Result<SkuDetailVo> item(@PathVariable(value = "skuId")Long skuId) ;

}

package com.atguigu.gmall.common.feign.cart;

import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-cart")
public interface CartFeignClient {

    @GetMapping(value = "/api/inner/cart/addCart")
    public Result<AddCartSuccessVo> addCart(@RequestParam(value = "skuId") Long skuId ,
                                            @RequestParam(value = "skuNum")Integer skuNum ) ;

}

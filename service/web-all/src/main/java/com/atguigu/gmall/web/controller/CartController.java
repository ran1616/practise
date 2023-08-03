package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.feign.cart.CartFeignClient;
import com.atguigu.gmall.common.feign.util.HttpServletRequestThreadLocal;
import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CartController {

    @Autowired
    private CartFeignClient cartFeignClient ;

    @GetMapping(value = "/addCart.html")
    public String addCart(@RequestParam(value = "skuId") Long skuId ,
                          @RequestParam(value = "skuNum") Integer skuNum , Model model) {
        Result<AddCartSuccessVo> result = cartFeignClient.addCart(skuId, skuNum);
        AddCartSuccessVo addCartSuccessVo = result.getData();
        model.addAttribute("skuInfo" , addCartSuccessVo.getSkuInfo()) ;
        model.addAttribute("skuNum" , addCartSuccessVo.getSkuNum()) ;
        return "cart/addCart" ;
    }

    @GetMapping(value = "/cart.html")
    public String cart() {
        return "cart/index" ;
    }
}

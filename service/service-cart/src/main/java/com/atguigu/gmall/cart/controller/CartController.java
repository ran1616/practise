package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cart")
public class CartController {

    @Autowired
    private CartService cartService ;

    @GetMapping(value = "/cartList")
    public Result<List<CartItem>> findAllCartItem() {
        List<CartItem> cartItemList = cartService.findAllCartItem() ;
        return Result.build(cartItemList , ResultCodeEnum.SUCCESS) ;
    }

}
